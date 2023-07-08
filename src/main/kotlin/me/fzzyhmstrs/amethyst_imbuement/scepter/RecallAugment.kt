package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.modifier.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.MiscAugment
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World

class RecallAugment: MiscAugment(ScepterTier.TWO,1){

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.WIT,12000,400,
            15,imbueLevel,40, LoreTier.NO_TIER, Items.SHIELD)
    }

    override fun effect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        hit: HitResult?,
        effect: AugmentEffect
    ): Boolean {
        if (user !is ServerPlayerEntity) return false
        val spawn = user.spawnPointPosition ?: return false
        val serverWorld: ServerWorld = user.server.getWorld(user.spawnPointDimension) ?: return false
        val exactSpawn = PlayerEntity.findRespawnPosition(serverWorld, spawn, user.spawnAngle, false, user.isAlive)
        val bl = exactSpawn.isPresent
        if (bl) {
            val spawnPos = exactSpawn.get()
            user.teleport(serverWorld,spawnPos.x,spawnPos.y, spawnPos.z,user.yaw,user.pitch)
            world.playSound(null,user.blockPos,soundEvent(),SoundCategory.NEUTRAL,0.25f,1.0f)
            effect.accept(user, AugmentConsumer.Type.BENEFICIAL)
        }
        return bl
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.BLOCK_PORTAL_TRAVEL
    }
}