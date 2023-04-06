package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentPersistentEffectData
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.MiscAugment
import me.fzzyhmstrs.amethyst_imbuement.entity.IceShardEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlF
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import me.fzzyhmstrs.fzzy_core.coding_util.PersistentEffectHelper
import me.fzzyhmstrs.fzzy_core.raycaster_util.RaycasterUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.mob.Monster
import net.minecraft.item.Items
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World

class HailStormAugment: MiscAugment(ScepterTier.THREE,12), PersistentEffectHelper.PersistentEffect{

    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withAmplifier(1)
            .withDuration(60,5)
            .withDamage(4.66666f,0.333333f)
            .withRange(8.25,0.25)

    private val hailDelay = PerLvlF(8.75f,-0.25f,0f)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.FURY,400,100,
            23,imbueLevel,10, LoreTier.HIGH_TIER, Items.BLUE_ICE)
    }

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
                hailDelay.value(level).toInt(),
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
                val rot = entity3.pos.add(0.0,entity3.height/2.0,0.0).subtract(user.pos.add(0.0,user.standingEyeHeight.toDouble(),0.0)).normalize()
                val ise = IceShardEntity(world,user,4.5f,0.4f,user.eyePos.subtract(0.0,0.2,0.0),rot)
                ise.passEffects(effect, level)
                ise.setAugment(this)
                if (world.spawnEntity(ise)){
                    successes++
                }
            }
        }
        effect.accept(toLivingEntityList(entityList),AugmentConsumer.Type.HARMFUL)
        return successes > 0
    }

    override val delay: PerLvlI
        get() = PerLvlI()

    @Suppress("SpellCheckingInspection")
    override fun persistentEffect(data: PersistentEffectHelper.PersistentEffectData) {
        if (data !is AugmentPersistentEffectData) return
        val rnd1 = data.entityList.size
        var entity: LivingEntity? = null
        var tries = 3
        do{
            val rnd2 = data.world.random.nextInt(rnd1)
            val entityTmp = data.entityList[rnd2]
            if (entityTmp.isAlive && entityTmp is LivingEntity){
                entity = entityTmp
                break
            }
            tries--
        } while(tries > 0)
        if (entity == null){
            for (entityTmp2 in data.entityList){
                if (entityTmp2.isAlive && entityTmp2 is LivingEntity){
                    entity = entityTmp2
                    break
                }
            }
        }
        if (entity == null) return

        val rot = entity.pos.add(0.0,entity.height/2.0,0.0).subtract(data.user.pos.add(0.0,data.user.standingEyeHeight.toDouble(),0.0)).normalize()
        val ise = IceShardEntity(data.world,data.user,4.5f,0.4f,data.user.eyePos.subtract(0.0,0.2,0.0),rot)
        ise.passEffects(data.effect, data.level)
        ise.setAugment(this)
        data.world.spawnEntity(ise)
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_PLAYER_HURT_FREEZE
    }
}
