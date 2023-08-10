package me.fzzyhmstrs.amethyst_imbuement.spells

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
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class RecallAugment: ScepterAugment(ScepterTier.TWO, AugmentType.SINGLE_TARGET_OR_SELF){

    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("recall"),SpellType.WIT,12000,400,
            15,1,1,40, LoreTier.NO_TIER, Items.SHIELD)

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
        if (user !is ServerPlayerEntity) return FAIL
        val spawn = user.spawnPointPosition ?: return FAIL
        val serverWorld: ServerWorld = user.server.getWorld(user.spawnPointDimension) ?: return FAIL
        val exactSpawn = PlayerEntity.findRespawnPosition(serverWorld, spawn, user.spawnAngle, false, user.isAlive)
        val bl = exactSpawn.isPresent
        if (bl) {
            val spawnPos = exactSpawn.get()
            user.teleport(serverWorld,spawnPos.x,spawnPos.y, spawnPos.z,user.yaw,user.pitch)
            spells.castSoundEvents(world, user.blockPos,context)
            return SpellActionResult.success()
        }
        return FAIL
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos,SoundEvents.BLOCK_PORTAL_TRAVEL,SoundCategory.PLAYERS,1f,1f)
    }
}
