package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.SpellActionResult
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.item.ScepterLike
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import net.minecraft.entity.LivingEntity
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class DebugAugment: ScepterAugment(ScepterTier.ONE, AugmentType.EMPTY) {

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos, SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.PLAYERS,1.0f,1.0f)
    }

    private fun writeNbt(key: String, input: Int, nbt: NbtCompound){
        nbt.putInt(key,input)
    }

    override val augmentData: AugmentDatapoint
        get() = AugmentDatapoint(Identifier(AI.MOD_ID,"debug"),SpellType.GRACE,1,1,
            1,1,1,500, LoreTier.NO_TIER, Items.DEBUG_STICK)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        description.add(AcText.translatable("enchantment.amethyst_imbuement.debug.desc.all"))
    }

    override fun <T> applyTasks(
        world: World,
        context: ProcessContext,
        user: T,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        spells: PairedAugments
    ): SpellActionResult where T : SpellCastingEntity,T : LivingEntity {
        val stack1 = user.getStackInHand(Hand.MAIN_HAND)
        val stack2 = user.getStackInHand(Hand.OFF_HAND)
        if (stack1.item is ScepterLike) {
            val nbt = stack1.orCreateNbt
            writeNbt(SpellType.FURY.name+"_lvl", 25, nbt)
            writeNbt(SpellType.WIT.name+"_lvl", 25, nbt)
            writeNbt(SpellType.GRACE.name+"_lvl", 25, nbt)
        } else if (stack2.item is ScepterLike) {
            val nbt = stack2.orCreateNbt
            writeNbt(SpellType.FURY.name+"_lvl", 25, nbt)
            writeNbt(SpellType.WIT.name+"_lvl", 25, nbt)
            writeNbt(SpellType.GRACE.name+"_lvl", 25, nbt)
        }
        return SpellActionResult.success(AugmentHelper.DRY_FIRED)
    }
}
