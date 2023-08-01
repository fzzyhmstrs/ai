package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.event.AfterSpellEvent
import me.fzzyhmstrs.amethyst_core.event.ModifyModifiersEvent
import me.fzzyhmstrs.amethyst_core.modifier.AugmentModifier
import me.fzzyhmstrs.amethyst_core.modifier.ModifierHelper
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.item.promise.MysticalGemItem
import me.fzzyhmstrs.amethyst_imbuement.recipe.AltarRecipe
import me.fzzyhmstrs.amethyst_imbuement.screen.SpellcastersFocusScreenHandlerFactory
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.coding_util.PlayerParticlesV2
import me.fzzyhmstrs.fzzy_core.interfaces.Modifiable
import me.fzzyhmstrs.fzzy_core.item_util.CustomFlavorItem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtString
import net.minecraft.particle.DustParticleEffect
import net.minecraft.recipe.RecipeType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.*
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class SpellcastersFocusItem(settings: Settings): CustomFlavorItem(settings), Modifiable, Reactant {

    private val FOCUS_TIER = "focus_tier"
    private val FOCUS_RECORDS = "focus_records"
    private val FOCUS_XP = "focus_xp"
    private val FOCUS_SPECIAL = "focus_special"
    internal val LEVEL_UP_READY = "lvl_up_ready"
    internal val LEVEL_UP = "lvl_up_data"
    internal val OPTION_1 = "option_1"
    internal val OPTION_2 = "option_2"
    internal val OPTION_3 = "option_3"
    internal val CHOSEN_OPTION = "last_chosen_option"
    private val tiers: Array<TierData> = arrayOf(
        TierData("", Rarity.UNCOMMON,0, AiConfig.items.focus.tierXp.get()[0],1,-1),
        TierData(".novice", Rarity.UNCOMMON,AiConfig.items.focus.tierXp.get()[0] + 1, AiConfig.items.focus.tierXp.get()[1],2,0),
        TierData(".adept", Rarity.RARE,AiConfig.items.focus.tierXp.get()[1] + 1, AiConfig.items.focus.tierXp.get()[2],3,1),
        TierData(".master", Rarity.RARE,AiConfig.items.focus.tierXp.get()[2] + 1, AiConfig.items.focus.tierXp.get()[3],4,2),
        TierData(".savant", Rarity.EPIC,AiConfig.items.focus.tierXp.get()[3] + 1, -1,-1,3)
    )

    init{
        ModifyModifiersEvent.EVENT.register{ _,user,_,modifiers ->
            for (stack in user.handItems) {
                if (stack.item is SpellcastersFocusItem) {
                    val focusMods = ModifierHelper.getActiveModifiers(stack)
                    val mods = modifiers.combineWith(focusMods, AugmentModifier())
                    return@register mods
                }
            }
            modifiers
        }

        AfterSpellEvent.EVENT.register{ world,user,_,spell ->
            val offhand = user.offHandStack
            val item = offhand.item
            if (item is SpellcastersFocusItem){
                addXpAndLevelUp(offhand, spell, user, world)
            }
        }
    }

    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        super.appendTooltip(stack, world, tooltip, context)
        val nbt = stack.nbt?:return
        tooltip.add(AcText.empty())
        if (nbt.getBoolean(LEVEL_UP_READY)){
            tooltip.add(AcText.translatable("item.amethyst_imbuement.spellcasters_focus.ready").formatted(Formatting.GOLD,Formatting.BOLD))
        }
        val tier = getTier(nbt)
        if (tier.nextTier != -1){
            tooltip.add(AcText.translatable("item.amethyst_imbuement.spellcasters_focus.xp",nbt.getInt(FOCUS_XP),tier.xpToNextTier))
        }
    }

    override fun inventoryTick(stack: ItemStack, world: World, entity: Entity, slot: Int, selected: Boolean) {
        super.inventoryTick(stack, world, entity, slot, selected)
        if (entity !is PlayerEntity) return
        val offhand = entity.offHandStack == stack
        if (stack.nbt?.getBoolean(LEVEL_UP_READY) != true) return
        if (world.isClient && (selected || offhand)){
            val rnd = world.random.nextInt(5)
            if (rnd < 1){
                val particlePos = PlayerParticlesV2.scepterParticlePos(MinecraftClient.getInstance(), entity,offhand)
                val rnd1 = world.random.nextDouble() * 0.1 - 0.05
                val rnd2 = world.random.nextDouble() * 0.2 - 0.1
                val rnd3 = world.random.nextInt(DyeColor.values().size)
                val colorInt = DyeColor.values()[rnd3].signColor
                val color = Vec3d.unpackRgb(colorInt).toVector3f()
                world.addParticle(DustParticleEffect(color,0.8f),particlePos.x + rnd1, particlePos.y + rnd2, particlePos.z + rnd2, 0.0, 0.0, 0.0)
            }
        }
    }
    
    override fun canReact(stack: ItemStack, reagents: List<ItemStack>, player: PlayerEntity?, type: RecipeType<*>?): Boolean {
        for (reagent in reagents) {
            if (reagent.item is MysticalGemItem && type == AltarRecipe.Type) {
                if (getTier(stack.nbt).previousTier == -1) return false
            }
        }
        return true
    }
    
    override fun react(stack: ItemStack, reagents: List<ItemStack>, player: PlayerEntity?, type: RecipeType<*>?) {
        for (reagent in reagents){
            val item = reagent.item
            if (item is SpellcastersReagent){
                if (stack.nbt?.contains("AttributeModifiers") == true) return
                val attribute = item.getAttributeModifier()
                val list = NbtList()
                val nbt = SpellcastersReagent.toNbt(attribute)
                nbt.putString("Slot","offhand")
                list.add(nbt)
                stack.orCreateNbt.put("AttributeModifiers",list)
                break
            }else if (item is MysticalGemItem && type == AltarRecipe.Type){
                resetXpAndLevelDown(stack)
            }
        }
    }
    
    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = user.getStackInHand(hand)
        if (hand != Hand.MAIN_HAND) return TypedActionResult.fail(stack)
        val nbt = stack.nbt?:return TypedActionResult.fail(stack)
        if (!nbt.getBoolean(LEVEL_UP_READY)) return TypedActionResult.fail(stack)
        if (!nbt.contains(LEVEL_UP)) return TypedActionResult.fail(stack)
        if (world.isClient) return TypedActionResult.fail(stack)
        user.openHandledScreen(SpellcastersFocusScreenHandlerFactory(stack))
        return TypedActionResult.success(stack)
    }

    private fun addXpAndLevelUp(stack: ItemStack, spell: ScepterAugment, user: LivingEntity, world: World){
        val nbt = stack.orCreateNbt
        val tier = getTier(nbt)
        if (tier.nextTier == -1) return
        val id = spell.id?.toString()?:return
        val newXp = AugmentHelper.getAugmentCastXp(id)
        val currentXp = nbt.getInt(FOCUS_XP)
        val newCurrentXp = currentXp + newXp
        nbt.putInt(FOCUS_XP,newCurrentXp)
        val records = nbt.getCompound(FOCUS_RECORDS)
        val currentRecordedXp = if(records.contains(id)){
            records.getInt(id)
        }else{
            0
        }
        val newCurrentRecordedXp = currentRecordedXp + newXp
        records.putInt(id,newCurrentRecordedXp)
        nbt.put(FOCUS_RECORDS,records)
        if (newCurrentXp > tier.xpToNextTier && !nbt.getBoolean(LEVEL_UP_READY)){
            if (user is ServerPlayerEntity && world is ServerWorld) {
                nbt.putInt(FOCUS_TIER,tier.nextTier)
                val lvlUpNbt = NbtCompound()
                val modifiers1: List<Identifier>
                val option1 = NbtList()
                val modifiers2: List<Identifier>
                val option2 = NbtList()
                val modifiers3: List<Identifier>
                val option3 = NbtList()
                if (newCurrentRecordedXp/tier.xpToNextTier > 0.9 && !nbt.contains(FOCUS_SPECIAL)){
                    nbt.putBoolean(FOCUS_SPECIAL,true)
                    modifiers1 = listOf(spell.augmentSpecificModifier.modifierId)
                    modifiers2 = ModifierHelper.rollScepterModifiers(stack,user,world)
                    modifiers3 = ModifierHelper.rollScepterModifiers(stack,user,world)
                } else {
                    modifiers1 = ModifierHelper.rollScepterModifiers(stack,user,world)
                    modifiers2 = ModifierHelper.rollScepterModifiers(stack,user,world)
                    modifiers3 = ModifierHelper.rollScepterModifiers(stack,user,world)
                }
                for (mod in modifiers1){
                    option1.add(NbtString.of(mod.toString()))
                }
                lvlUpNbt.put(OPTION_1,option1)
                for (mod in modifiers2){
                    option2.add(NbtString.of(mod.toString()))
                }
                lvlUpNbt.put(OPTION_2,option2)
                for (mod in modifiers3){
                    option3.add(NbtString.of(mod.toString()))
                }
                lvlUpNbt.put(OPTION_3,option3)
                nbt.putBoolean(LEVEL_UP_READY,true)
                nbt.remove(CHOSEN_OPTION)
                nbt.put(LEVEL_UP,lvlUpNbt)
            }
        }
    }

    private fun resetXpAndLevelDown(stack: ItemStack){
        val nbt = stack.orCreateNbt
        val tier = getTier(nbt)
        val prevTier = tiers[tier.previousTier]
        nbt.putInt(FOCUS_TIER,tier.previousTier)
        nbt.putInt(FOCUS_XP,prevTier.startingXp)
        if (!nbt.getBoolean(LEVEL_UP_READY)) {
            val list = nbt.getList(CHOSEN_OPTION, 8)
            val ids = list.stream().map { Identifier(it.asString()) }.toList()
            ModifierHelper.removeRolledModifiers(stack, ids)
            nbt.remove(CHOSEN_OPTION)
        }
        nbt.putBoolean(LEVEL_UP_READY, false)
        nbt.remove(LEVEL_UP)
        nbt.remove(FOCUS_RECORDS)
    }

    private fun getTier(nbt: NbtCompound?): TierData{
        if (nbt == null) return tiers[0]
        if(!nbt.contains(FOCUS_TIER)) return tiers[0]
        val tier = MathHelper.clamp(nbt.getByte(FOCUS_TIER).toInt(),0,4)
        return tiers[tier.toInt()]
    }

    private data class TierData(val key: String, val rarity: Rarity, val startingXp: Int, val xpToNextTier: Int, val nextTier: Int, val previousTier: Int)

    override fun getRarity(stack: ItemStack): Rarity {
        val tier = getTier(stack.nbt)
        return tier.rarity
    }

    override fun isFireproof(): Boolean {
        return true
    }

    override fun getTranslationKey(stack: ItemStack): String {
        val tier = getTier(stack.nbt)
        return super.getTranslationKey(stack) + tier.key
    }
}
