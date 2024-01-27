package me.fzzyhmstrs.amethyst_imbuement.spells.tales.rpg_series

import me.fzzyhmstrs.amethyst_core.compat.spell_power.SpChecker
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.registry.RegisterTag
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.MiscAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.item.book.BookOfTalesItem
import me.fzzyhmstrs.fzzy_core.coding_util.compat.FzzyDamage
import me.fzzyhmstrs.fzzy_core.raycaster_util.RaycasterUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.Items
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class SoulfreezeAugment: MiscAugment(ScepterTier.THREE,13){

    override val baseEffect: AugmentEffect = super.baseEffect
                                                .withDamage(4.5f,0.5f)
                                                .withAmplifier(1)
                                                .withDuration(1150,50)
                                                .withRange(11.25,0.25)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.FURY,600,120,
            23,imbueLevel,25, BookOfTalesItem.TALES_TIER, Items.SOUL_SOIL)
    }

    override fun effect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        hit: HitResult?,
        effect: AugmentEffect
    ): Boolean {
        val hitResult = EntityHitResult(user, Vec3d(user.x,user.getBodyY(0.5),user.z))
        val (_, entityList) = RaycasterUtil.raycastEntityArea(user,hitResult,effect.range(level))
        val filteredList = entityList.filter { AiConfig.entities.shouldItHitBase(user, it,this) }.toMutableList()
        if (filteredList.isEmpty()) return false
        val bl = effect(world, user, filteredList, level, effect)
        if (bl) {
            if (world is ServerWorld){
                world.spawnParticles(ParticleTypes.SNOWFLAKE,user.x,user.getBodyY(0.5),user.z,1000,effect.range(level),0.8,effect.range(level),0.0)
                world.spawnParticles(ParticleTypes.SMOKE,user.x,user.getBodyY(0.5),user.z,1000,effect.range(level),0.8,effect.range(level),0.0)

            }
            world.playSound(null, user.blockPos, soundEvent(), SoundCategory.PLAYERS, 1.0F, 1.0F)
            effect.accept(user, AugmentConsumer.Type.BENEFICIAL)
        }
        return bl
    }

    override fun effect(
        world: World,
        user: LivingEntity,
        entityList: MutableList<Entity>,
        level: Int,
        effect: AugmentEffect
    ): Boolean {

        var successes = 0
        for (target in entityList) {
            //if (!AiConfig.entities.shouldItHit(user, target,this)) continue
            val bl = target.damage(FzzyDamage.freeze(user),effect.damage(level))
            if (bl && target is LivingEntity) {
                val mod = SpChecker.getModFromTags(user, RegisterTag.SOUL_AUGMENTS, RegisterTag.ICE_AUGMENTS)
                val duration = effect.duration(level) * (1 + mod.toInt() / 100)
                val amplifier = effect.amplifier(level) * (1 + mod.toInt() / 60)
                //RegisterStatus.stun(target,duration/4)
                target.addStatusEffect(StatusEffectInstance(StatusEffects.WITHER,effect.duration(level), amplifier))
                target.frozenTicks = duration * 2
                effect.accept(target, AugmentConsumer.Type.HARMFUL)
            }
            if (bl) successes++
        }
        return successes > 0
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_PLAYER_HURT_FREEZE
    }

}
