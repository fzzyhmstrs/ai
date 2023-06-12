package me.fzzyhmstrs.amethyst_imbuement.scepter

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
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlF
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import me.fzzyhmstrs.fzzy_core.coding_util.PersistentEffectHelper
import me.fzzyhmstrs.fzzy_core.raycaster_util.RaycasterUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class MagneticAuraAugment: MiscAugment(ScepterTier.TWO,7), PersistentEffectHelper.PersistentEffect{

    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withDuration(120,40)
            .withRange(4.5,0.5)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.GRACE,400,60,
            7,imbueLevel,20, LoreTier.LOW_TIER, RegisterItem.PYRITE)
    }

    override fun effect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        hit: HitResult?,
        effect: AugmentEffect
    ): Boolean {
        if (user !is PlayerEntity) return false
        val range = effect.range(level)
        val list = world.getOtherEntities(
            user, Box(user.x - range, user.y - range, user.z - range, user.x + range, user.y + range, user.z + range)
        ) { obj: Entity -> obj is ItemEntity }
        if (list.isEmpty()) return false
        val bl = effect(world, user, list, level, effect)
        if (bl) {
            val data = AugmentPersistentEffectData(world, user, user.blockPos, list, level, effect)
            PersistentEffectHelper.setPersistentTickerNeed(
                RegisterEnchantment.MAGNETIC_AURA,
                delay.value(level),
                effect.duration(level),
                data
            )
            if (world is ServerWorld){
                world.spawnParticles(ParticleTypes.ELECTRIC_SPARK,user.x,user.getBodyY(0.5),user.z,120,effect.range(level),effect.range(level),effect.range(level),0.0)
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
        for (target in entityList) {
            if (target !is ItemEntity) continue
            if (user !is PlayerEntity) return false
            val stack = target.stack
            user.inventory.offerOrDrop(stack)
            target.discard()
        }
        return true
    }

    override val delay: PerLvlI
        get() = PerLvlI(10)

    @Suppress("SpellCheckingInspection")
    override fun persistentEffect(data: PersistentEffectHelper.PersistentEffectData) {
        if (data !is AugmentPersistentEffectData) return
        val range = data.effect.range(data.level)
        val list = data.world.getOtherEntities(
            data.user, Box(data.user.x - range, data.user.y - range, data.user.z - range, data.user.x + range, data.user.y + range, data.user.z + range)
        ) { obj: Entity -> obj is ItemEntity }
        effect(data.world,data.user,list,data.level,data.effect)
        data.world.playSound(null,data.user.blockPos,soundEvent(),SoundCategory.PLAYERS,1.0f,0.8f + data.world.random.nextFloat()*0.4f)
        val world = data.world
        if (world is ServerWorld){
            world.spawnParticles(ParticleTypes.ELECTRIC_SPARK,data.user.x,data.user.getBodyY(0.5),data.user.z,100,data.effect.range(data.level),0.8,data.effect.range(data.level),0.0)
        }
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_WANDERING_TRADER_REAPPEARED
    }

}
