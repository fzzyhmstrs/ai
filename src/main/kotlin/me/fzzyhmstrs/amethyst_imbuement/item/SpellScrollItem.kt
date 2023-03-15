package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.amethyst_core.item_util.SpellCasting
import me.fzzyhmstrs.amethyst_core.modifier_util.ModifierHelper
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterHelper
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.ScepterAugment
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

class SpellScrollItem(settings: Settings): Item(settings), SpellCasting, Modifiable {

    private val SCROLL_MODIFIER_TOLL = BinomialLootNumberProvider.create(20,0.24f)
    private val SCROLL_LEVEL = "scroll_level"
    private val TOTAL_USES = "total_uses"
    private val SPENT_USES = "spent_uses"
    internal val SPELL = "written_spell"
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
            nbt.putInt(TOTAL_USES,128)
            128f
        } else if (rnd < 0.15){
            nbt.putInt(TOTAL_USES,64)
            64f
        } else {
            nbt.putInt(TOTAL_USES,32)
            32f
        }
        val rnd2 = world.random.nextFloat()
        modelKey +=if (rnd2 < 0.01){
            nbt.putInt(SCROLL_LEVEL, 5)
            5f
        } else if (rnd2 < 0.025){
            nbt.putInt(SCROLL_LEVEL, 4)
            4f
        }else if (rnd2 < 0.05){
            nbt.putInt(SCROLL_LEVEL, 3)
            3f
        }else if (rnd2 < 0.15){
            nbt.putInt(SCROLL_LEVEL, 2)
            2f
        } else {
            nbt.putInt(SCROLL_LEVEL, 1)
            1f
        }
        nbt.putInt(SPENT_USES,0)
        nbt.putFloat(MODEL_KEY, modelKey)
        if (player is ServerPlayerEntity && world is ServerWorld){
            val rolls = ModifierHelper.rollScepterModifiers(stack, player, world, SCROLL_MODIFIER_TOLL)
            ModifierHelper.addRolledModifiers(stack,rolls)
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