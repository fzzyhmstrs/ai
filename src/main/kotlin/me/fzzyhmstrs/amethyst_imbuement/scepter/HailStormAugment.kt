package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentPersistentEffectData
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.MiscAugment
import me.fzzyhmstrs.amethyst_imbuement.entity.IceShardEntity.Companion.createIceShard
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import me.fzzyhmstrs.fzzy_core.coding_util.PersistentEffectHelper
import me.fzzyhmstrs.fzzy_core.raycaster_util.RaycasterUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.mob.Monster
import net.minecraft.item.Items
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class HailStormAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): MiscAugment(tier, maxLvl, *slot),
    PersistentEffectHelper.PersistentEffect{

    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withAmplifier(1)
            .withDuration(60,5)
            .withDamage(4.66666f,0.333333f)
            .withRange(8.25,0.25)

    override val delay = PerLvlf(8.75f,-0.25f,0)

    override fun effect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        hit: HitResult?,
        effect: AugmentEffect
    ): Boolean {
        val (blockPos, entityList) = RaycasterUtil.raycastEntityArea(user,hit,effect.range(level))
        if (entityList.isEmpty() || blockPos == user.blockPos) return false
        val bl = effect(world, user, entityList, level, effect)
        if (bl) {
            val data = AugmentPersistentEffectData(world, user, blockPos, entityList, level, effect)
            PersistentEffectHelper.setPersistentTickerNeed(
                RegisterEnchantment.HAIL_STORM,
                delay.value(level).toInt(),
                effect.duration(level),
                data
            )
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
        for (entity3 in entityList) {
            if(entity3 is Monster){
                val vel = entity3.pos.subtract(user.pos.add(0.0,user.standingEyeHeight.toDouble(),0.0)).normalize().multiply(4.5)
                val ise = createIceShard(world, user, vel, user.eyePos.subtract(0.0,0.2,0.0), effect, level)
                if (world.spawnEntity(ise)){
                    successes++
                }
            }
        }
        effect.accept(toLivingEntityList(entityList),AugmentConsumer.Type.HARMFUL)
        return successes > 0
    }

    @Suppress("SpellCheckingInspection")
    override fun persistentEffect(data: PersistentEffectHelper.PersistentEffectData) {
        if (data !is AugmentPersistentEffectData) return
        val rnd1 = data.entityList.size
        val entity: LivingEntity? = null
        var tries = 3
        do{
            val rnd2 = data.world.random.nextInt(rnd1)
            val entityTmp = data.entityList[rnd2]
            if (entityTmp.isAlive){
                entity = entityTmp
                break
            }
            tries--
        } while(tries > 0)
        if (entity == null){
            for (entityTmp2 in entityList){
                if (entityTmp2.isAlive){
                    entity = entityTmp2
                    break
                }
            }
        }
        if (entity == null) return
        
        val vel = entity.pos.subtract(user.pos.add(0.0,user.standingEyeHeight.toDouble(),0.0)).normalize().multiply(4.5)
        val ce = createIceShard(data.world, data.user, vel, data.user.eyePos.subtract(0.0,0.2,0.0), data.effect, data.level)
        data.world.spawnEntity(ce)
    }

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.FURY,400,100,21,imbueLevel, LoreTier.HIGH_TIER, Items.BLUE_ICE)
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_PLAYER_HURT_FREEZE
    }
}
