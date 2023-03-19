package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.amethyst_core.item_util.SpellCasting
import me.fzzyhmstrs.amethyst_core.modifier_util.ModifierHelper
import me.fzzyhmstrs.amethyst_core.nbt_util.NbtKeys
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterHelper
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.item.promise.MysticalGemItem
import me.fzzyhmstrs.fzzy_core.interfaces.Modifiable
import me.fzzyhmstrs.fzzy_core.modifier_util.ModifierHelperType
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.loot.provider.number.BinomialLootNumberProvider
import net.minecraft.registry.Registries
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World
import kotlin.math.max
import kotlin.math.min

class SpellScrollItem(settings: Settings): Item(settings), SpellCasting, Modifiable, Reactant {

    private val SCROLL_LEVEL = "scroll_level"
    private val TOTAL_USES = "total_uses"
    private val SPENT_USES = "spent_uses"
    internal val SPELL = "written_spell"
    internal val SPELL_TYPE = "written_spell_type"
    internal val MODEL_KEY = "model_predicate_key"

    override fun appendTooltip(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<Text>,
        context: TooltipContext
    ) {
        super.appendTooltip(stack, world, tooltip, context)
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = user.getStackInHand(hand)
        val nbt = stack.orCreateNbt
        val spellString = nbt.getString(SPELL)
        val spell = Registries.ENCHANTMENT.get(Identifier(spellString))?:return resetCooldown(stack,world,user,spellString)
        if (spell !is ScepterAugment) return resetCooldown(stack,world,user,spellString)
        val level = min(spell.maxLevel,max(1,nbt.getInt(SCROLL_LEVEL)))
        return ScepterHelper.castSpell(world,user,hand,stack,spell,spellString,level,this,false)
    }

    override fun onCraft(stack: ItemStack, world: World, player: PlayerEntity) {
        val nbt = stack.orCreateNbt
        val rnd = world.random.nextFloat()
        var modelKey = 0f
        modelKey += if (rnd < 0.05){
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
        } else if (rnd2 < 0.025){
            nbt.putInt(SCROLL_LEVEL, AiConfig.items.scroll.levels.get()[3])
            4f
        }else if (rnd2 < 0.05){
            nbt.putInt(SCROLL_LEVEL, AiConfig.items.scroll.levels.get()[2])
            3f
        }else if (rnd2 < 0.15){
            nbt.putInt(SCROLL_LEVEL, AiConfig.items.scroll.levels.get()[1])
            2f
        } else {
            nbt.putInt(SCROLL_LEVEL, AiConfig.items.scroll.levels.get()[0])
            1f
        }
        nbt.putInt(SPENT_USES,0)
        nbt.putFloat(MODEL_KEY, modelKey)
    }
    
    override fun canReact(stack: ItemStack,reagents: List<ItemStack>): Boolean{
        val nbt = stack.nbt
        if (nbt?.contains(SPELL) == true) return false
        var bl = false
        for (reagent in reagents){
            if (reagent.item is BookOfKnowledge){
                val spell = reagent.nbt?.contains(NbtKeys.LORE_KEY.str())?:false
                val type = reagent.nbt?.contains(NbtKeys.LORE_TYPE.str())?:false
                if (spell && type){
                    bl = true
                    break
                }
            }
        }
        return bl
    }
    
    override fun react(stack: ItemStack,reagents: List<ItemStack>){
        val nbt = stack.orCreateNbt
        for (reagent in reagents){
            if (nbt.contains(SPELL)) break
            if (reagent.item is BookOfKnowledge){
                val spellString = reagent.nbt?.getString(NbtKeys.LORE_KEY.str())?:return
                val spellType = reagent.nbt?.getString(NbtKeys.LORE_TYPE.str())?:""
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
            stack.decrement(stack.count)
            world.playSound(null,user.blockPos,SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE.value(),SoundCategory.PLAYERS,1.0f,1.0f)
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
        ScepterHelper.resetCooldown(world, stack, user, activeEnchant)
        return TypedActionResult.fail(stack)
    }

    override fun canBeModifiedBy(type: ModifierHelperType?): Boolean {
        return type == ModifierHelper.getType()
    }
}
