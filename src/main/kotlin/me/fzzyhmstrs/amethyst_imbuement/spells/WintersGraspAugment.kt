package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.CustomDamageSources
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentPersistentEffectData
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.MiscAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlF
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import me.fzzyhmstrs.fzzy_core.coding_util.PersistentEffectHelper
import me.fzzyhmstrs.fzzy_core.raycaster_util.RaycasterUtil
import net.minecraft.block.Blocks
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class WintersGraspAugment: MiscAugment(ScepterTier.THREE,17), PersistentEffectHelper.PersistentEffect{

    override val baseEffect: AugmentEffect = super.baseEffect
            .withDamage(1.8f,0.1f)
            .withAmplifier(1)
            .withDuration(115,5)
            .withRange(11.75,0.25)

    private val hailDelay = PerLvlF(20f,-0.5f,0f)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.FURY,600,120,
            23,imbueLevel,25, LoreTier.HIGH_TIER, RegisterBlock.GLISTENING_ICE.asItem())
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
        val (blockPos, entityList) = RaycasterUtil.raycastEntityArea(user,hitResult,effect.range(level))
        if (entityList.isEmpty()) return false
        val bl = effect(world, user, entityList, level, effect)
        if (bl) {
            val data = AugmentPersistentEffectData(world, user, blockPos, entityList, level, effect)
            PersistentEffectHelper.setPersistentTickerNeed(
                RegisterEnchantment.WINTERS_GRASP,
                hailDelay.value(level).toInt(),
                effect.duration(level),
                data
            )
            if (world is ServerWorld){
                world.spawnParticles(ParticleTypes.SNOWFLAKE,user.x,user.getBodyY(0.5),user.z,100,effect.range(level),0.8,effect.range(level),0.0)
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
            if (target is SpellCastingEntity && AiConfig.entities.isEntityPvpTeammate(user, target,this)) continue
            val bl = target.damage(CustomDamageSources.freeze(world,null,user),effect.damage(level))
            if (bl && target is LivingEntity) {
                target.addStatusEffect(StatusEffectInstance(StatusEffects.SLOWNESS,effect.duration(level), effect.amplifier(level)))
                target.addStatusEffect(StatusEffectInstance(StatusEffects.WEAKNESS,effect.duration(level), effect.amplifier(level)))
                if(world.random.nextFloat() < 0.2f){
                    target.frozenTicks = 360
                    val pos = target.blockPos
                    if (world.getBlockState(pos).isAir){
                        world.setBlockState(pos, Blocks.SNOW.defaultState)
                    }
                }
                effect.accept(target, AugmentConsumer.Type.HARMFUL)
            }
            if (bl) successes++
        }
        return successes > 0
    }

    override val delay: PerLvlI
        get() = PerLvlI()

    @Suppress("SpellCheckingInspection")
    override fun persistentEffect(data: PersistentEffectHelper.PersistentEffectData) {
        if (data !is AugmentPersistentEffectData) return
        val hitResult = EntityHitResult(data.user, Vec3d(data.user.x,data.user.getBodyY(0.5),data.user.z))
        val (_, entityList) = RaycasterUtil.raycastEntityArea(data.user,hitResult,data.effect.range(data.level))
        effect(data.world,data.user,entityList,data.level,data.effect)
        data.world.playSound(null,data.user.blockPos,soundEvent(),SoundCategory.PLAYERS,1.0f,0.8f + data.world.random.nextFloat()*0.4f)
        val world = data.world
        if (world is ServerWorld){
            world.spawnParticles(ParticleTypes.SNOWFLAKE,data.user.x,data.user.getBodyY(0.5),data.user.z,100,data.effect.range(data.level),0.8,data.effect.range(data.level),0.0)
        }
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_PLAYER_HURT_FREEZE
    }

}
