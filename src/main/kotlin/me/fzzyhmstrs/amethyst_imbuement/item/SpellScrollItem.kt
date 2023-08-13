package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.item.SpellCasting
import me.fzzyhmstrs.amethyst_core.nbt.NbtKeys
import me.fzzyhmstrs.amethyst_core.scepter.ScepterHelper
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterScepter
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.recipe.RecipeType
import net.minecraft.registry.Registries
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World
import kotlin.math.max
import kotlin.math.min

@Suppress("PrivatePropertyName", "PropertyName")
class SpellScrollItem(settings: Settings): Item(settings), SpellCasting, Reactant, Reagent {

    private val SCROLL_LEVEL = "scroll_level"
    private val TOTAL_USES = "total_uses"
    private val SPENT_USES = "spent_uses"
    internal val SPELL = "written_spell"
    internal val SPELL_PAIR = "wirtten_spell_pair"
    internal val SPELL_TYPE = "written_spell_type"
    internal val MODEL_KEY = "model_predicate_key"
    internal val DISENCHANTED = "disenchanted"

    companion object{
        internal fun createSpellScroll(enchant: ScepterAugment, pair: PairedAugments? = null, disenchanted: Boolean = false): ItemStack{
            val stack = ItemStack(RegisterScepter.SPELL_SCROLL)
            val nbt = stack.orCreateNbt
            val spellString = Registries.ENCHANTMENT.getId(enchant)?.toString()?: throw IllegalStateException("Enchantment couldn't be found!")
            nbt.putString(RegisterScepter.SPELL_SCROLL.SPELL,spellString)
            nbt.put(RegisterScepter.SPELL_SCROLL.SPELL_PAIR,AugmentHelper.writePairedAugmentsToNbt(pair ?: PairedAugments(enchant)))
            val type = AugmentHelper.getAugmentType(spellString)
            nbt.putString(RegisterScepter.SPELL_SCROLL.SPELL_TYPE,type.str())
            nbt.putFloat(RegisterScepter.SPELL_SCROLL.MODEL_KEY,33f)
            nbt.putInt(RegisterScepter.SPELL_SCROLL.SPENT_USES,0)
            nbt.putInt(RegisterScepter.SPELL_SCROLL.TOTAL_USES,AiConfig.items.scroll.uses.get()[0])
            nbt.putInt(RegisterScepter.SPELL_SCROLL.SCROLL_LEVEL, AiConfig.items.scroll.levels.get()[0])
            if (disenchanted) nbt.putBoolean(RegisterScepter.SPELL_SCROLL.DISENCHANTED, true)
            return stack
        }
    }

    override fun appendTooltip(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<Text>,
        context: TooltipContext
    ) {
        super.appendTooltip(stack, world, tooltip, context)
        val nbt = stack.nbt?:return
        val pair = AugmentHelper.getOrCreatePairedAugmentsFromNbt(nbt.getCompound(SPELL_PAIR))
        val level = max(1,nbt.getInt(SCROLL_LEVEL))
        tooltip.add(AcText.translatable("item.amethyst_imbuement.spell_scroll.spell",pair.provideName(level).formatted(Formatting.GOLD)))
        val pairedSpell = pair.paired()
        if (pairedSpell != null){
            tooltip.add(AcText.translatable("item.amethyst_imbuement.spell_scroll.paired_spell",pairedSpell.getName(level)))
        }
        val boost = pair.boost()
        if (boost != null){
            tooltip.add(AcText.translatable("item.amethyst_imbuement.spell_scroll.paired_boost",boost.name()))
        }
        val cooldown = pair.provideCooldown(level)
        tooltip.add(AcText.translatable("lore_book.cooldown.basic",cooldown).formatted(Formatting.WHITE))
        val type = nbt.getString(SPELL_TYPE)

        val castXp = pair.provideCastXp(SpellType.fromString(type))
        tooltip.add(AcText.translatable("lore_book.cast_xp",castXp.toString()).formatted(Formatting.WHITE))
        tooltip.add(AcText.empty())
        if (nbt.contains(DISENCHANTED)){
            tooltip.add(AcText.translatable("item.amethyst_imbuement.spell_scroll.disenchanted").formatted(Formatting.ITALIC, Formatting.AQUA))
        }
        val uses = max(1,nbt.getInt(TOTAL_USES))
        val spentUses = nbt.getInt(SPENT_USES)
        val usesLeft = uses - spentUses
        tooltip.add(AcText.translatable("item.amethyst_imbuement.spell_scroll.uses",usesLeft,uses))

    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = user.getStackInHand(hand)
        val nbt = stack.orCreateNbt
        val spellString = nbt.getString(SPELL)
        val spell = Registries.ENCHANTMENT.get(Identifier(spellString))?:return resetCooldown(stack,world,user,spellString)
        if (spell !is ScepterAugment) return resetCooldown(stack,world,user,spellString)
        val pair: PairedAugments =if (!nbt.contains(SPELL_PAIR)){
            val pairedAugments = PairedAugments(spell)
            nbt.put(SPELL_PAIR,AugmentHelper.writePairedAugmentsToNbt(pairedAugments))
            pairedAugments
        } else {
            AugmentHelper.getOrCreatePairedAugmentsFromNbt(nbt.getCompound(SPELL_PAIR))
        }

        val level = min(spell.maxLevel,max(1,nbt.getInt(SCROLL_LEVEL)))
        if (world.isClient) {
            //spell.clientTask(world,user,hand,level)
            return TypedActionResult.pass(stack)
        }
        return ScepterHelper.castSpell(world, ProcessContext.EMPTY_CONTEXT,user,hand,stack,spellString,spell,pair,level,this,
            incrementStats = false,
            checkEnchant = false
        )
    }

    override fun onCraft(stack: ItemStack, world: World, player: PlayerEntity) {
        val nbt = stack.orCreateNbt
        val rnd = world.random.nextFloat()
        var modelKey = 0f
        modelKey += if (rnd < 0.025){
            nbt.putInt(TOTAL_USES,AiConfig.items.scroll.uses.get()[2])
            128f
        } else if (rnd < 0.15){
            nbt.putInt(TOTAL_USES,AiConfig.items.scroll.uses.get()[1])
            64f
        } else {
            nbt.putInt(TOTAL_USES,AiConfig.items.scroll.uses.get()[0])
            32f
        }
        val rnd2 = world.random.nextFloat()
        modelKey +=if (rnd2 < 0.01){
            nbt.putInt(SCROLL_LEVEL, AiConfig.items.scroll.levels.get()[4])
            5f
        } else if (rnd2 < 0.033333){
            nbt.putInt(SCROLL_LEVEL, AiConfig.items.scroll.levels.get()[3])
            4f
        }else if (rnd2 < 0.1){
            nbt.putInt(SCROLL_LEVEL, AiConfig.items.scroll.levels.get()[2])
            3f
        }else if (rnd2 < 0.25){
            nbt.putInt(SCROLL_LEVEL, AiConfig.items.scroll.levels.get()[1])
            2f
        } else {
            nbt.putInt(SCROLL_LEVEL, AiConfig.items.scroll.levels.get()[0])
            1f
        }
        nbt.putInt(SPENT_USES,0)
        nbt.putFloat(MODEL_KEY, modelKey)
    }
    
    override fun canReact(stack: ItemStack, reagents: List<ItemStack>, player: PlayerEntity?, type: RecipeType<*>?): Boolean {
        if (reagents.isEmpty()) return true
        if (reagents.size == 1 && reagents[0].item is SpellScrollItem) return true
        val nbt = stack.nbt
        if (nbt?.contains(SPELL) == true) return false
        var fails = 0
        for (reagent in reagents){
            if (reagent.item is BookOfKnowledge){
                val spell = reagent.nbt?.contains(NbtKeys.LORE_KEY)?:false
                val loreType = reagent.nbt?.contains(NbtKeys.LORE_TYPE)?:false
                if (!spell || !loreType){
                    fails++
                }
            }
        }
        return fails == 0
    }
    
    override fun react(stack: ItemStack, reagents: List<ItemStack>, player: PlayerEntity?, type: RecipeType<*>?) {
        val nbt = stack.orCreateNbt
        for (reagent in reagents){
            if (nbt.contains(SPELL)) break
            if (reagent.item is BookOfKnowledge){
                val spellString = reagent.nbt?.getString(NbtKeys.LORE_KEY)?:return
                val spellType = reagent.nbt?.getString(NbtKeys.LORE_TYPE)?:""
                nbt.putString(SPELL,spellString)
                nbt.putString(SPELL_TYPE,spellType)
            }
        }
    }

    override fun applyManaCost(cost: Int, stack: ItemStack, world: World, user: LivingEntity) {
        val nbt = stack.orCreateNbt
        val spent = nbt.getInt(SPENT_USES)
        val total = nbt.getInt(TOTAL_USES)
        if (spent + 1 >= total){
            stack.count = 0
            world.playSound(null,user.blockPos,SoundEvents.BLOCK_BEACON_DEACTIVATE,SoundCategory.PLAYERS,1.0f,1.0f)
            world.playSound(null,user.blockPos,SoundEvents.BLOCK_FIRE_EXTINGUISH,SoundCategory.PLAYERS,1.0f,1.0f)
        } else {
            nbt.putInt(SPENT_USES, spent + 1)
        }
    }

    override fun checkManaCost(cost: Int, stack: ItemStack, world: World, user: LivingEntity): Boolean {
        return true
    }

    override fun resetCooldown(
        stack: ItemStack,
        world: World,
        user: LivingEntity,
        activeEnchant: String
    ): TypedActionResult<ItemStack> {
        world.playSound(null,user.blockPos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS,0.6F,0.8F)
        val level = stack.nbt?.getInt(SCROLL_LEVEL)?:1
        ScepterHelper.resetCooldown(world, stack, user, activeEnchant,level)
        Exception().printStackTrace()
        return TypedActionResult.fail(stack)
    }
}
