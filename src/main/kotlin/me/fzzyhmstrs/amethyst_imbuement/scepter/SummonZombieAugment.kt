@file:Suppress("SpellCheckingInspection")

package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_imbuement.entity.UnhallowedEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.AugmentEffect
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.SummonEntityAugment
import me.fzzyhmstrs.amethyst_imbuement.util.LoreTier
import me.fzzyhmstrs.amethyst_imbuement.util.SpellType
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World
import kotlin.math.max
import kotlin.math.min

class SummonZombieAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): SummonEntityAugment(tier, maxLvl, *slot) {

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withAmplifier(3,0,0).withDuration(2400,0,0)

    override fun placeEntity(
        world: World,
        user: PlayerEntity,
        hit: HitResult,
        level: Int,
        effects: AugmentEffect
    ): Boolean {
        val bonus = max(0,level-3)
        for(i in 1..min(effects.amplifier(level),level)) {
            val xrnd: Double = (hit as BlockHitResult).blockPos.x + (world.random.nextDouble() * 4.0 - 2.0)
            val zrnd: Double = (hit).blockPos.z + (world.random.nextDouble() * 4.0 - 2.0)
            val yrnd = hit.blockPos.y + 1.0
            val zom = UnhallowedEntity(RegisterEntity.UNHALLOWED_ENTITY, world,effects.duration(level), bonus)
            zom.setPos(xrnd, yrnd, zrnd)
            world.spawnEntity(zom)
        }
        world.playSound(null,user.blockPos,soundEvent(), SoundCategory.PLAYERS,1.0F,1.0F)
        return true
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_ZOMBIE_AMBIENT
    }

    override fun augmentStat(imbueLevel: Int): ScepterObject.AugmentDatapoint {
        return ScepterObject.AugmentDatapoint(SpellType.WIT,1200,30,5,imbueLevel,LoreTier.LOW_TIER, Items.ROTTEN_FLESH)
    }
}