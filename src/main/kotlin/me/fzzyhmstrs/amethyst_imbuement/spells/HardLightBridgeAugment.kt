package me.fzzyhmstrs.amethyst_imbuement.spells

import eu.pb4.common.protection.api.CommonProtection
import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.SpellActionResult
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.modifier.addLang
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import net.minecraft.block.ShapeContext
import net.minecraft.entity.LivingEntity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class HardLightBridgeAugment: ScepterAugment(ScepterTier.TWO, AugmentType.BLOCK_AREA){
    //ml 11
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("hard_light_bridge"),SpellType.WIT,8,2,
            9,11,1,1,LoreTier.LOW_TIER, RegisterBlock.HARD_LIGHT_BLOCK.asItem())

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withRange(7.8,0.2,0.0)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        description.addLang("amethyst_imbuement.todo")
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideNoun(this))
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        SpellAdvancementChecks.uniqueOrDouble(player, pair)
    }

    override fun <T> applyTasks(
        world: World,
        context: ProcessContext,
        user: T,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        spells: PairedAugments
    )
    :
    SpellActionResult
    where
    T : SpellCastingEntity,
    T : LivingEntity
    {
        val onCastResults = spells.processOnCast(context,world,null,user, hand, level, effects)
        if (!onCastResults.success()) return  FAIL
        if (onCastResults.overwrite()) return onCastResults
        if (user !is ServerPlayerEntity) return FAIL
        var successes = 0
        var range = effects.range(level)
        do {
            val pos = user.pos.subtract(0.0, 0.5, 0.0).add(user.rotationVector.multiply(range))
            val blockPos = BlockPos.ofFloored(pos)
            if (CommonProtection.canPlaceBlock(world,blockPos,user.gameProfile,user)){
                val state = RegisterBlock.HARD_LIGHT_BLOCK.getHardLightState()
                if (world.canPlayerModifyAt(user,blockPos) && world.getBlockState(blockPos).isReplaceable && world.canPlace(state,blockPos, ShapeContext.of(user)) && state.canPlaceAt(world,blockPos)){
                    world.setBlockState(blockPos,state)
                    if (AiConfig.blocks.isBridgeBlockTemporary()){
                        world.scheduleBlockTick(blockPos, RegisterBlock.HARD_LIGHT_BLOCK, AiConfig.blocks.hardLight.temporaryDuration.get())
                    }
                    successes++
                }
            }
            range -= 1.0
        }while (range > 0.0)
        if (successes > 0) {
            spells.castSoundEvents(world,user.blockPos,context)
            return SpellActionResult.success(AugmentHelper.BLOCK_PLACED).withResults(onCastResults.results())
        }
        return FAIL
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos, SoundEvents.BLOCK_GLASS_PLACE,SoundCategory.PLAYERS,1f,1f)
    }
}
