package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentPersistentEffectData
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.MiscAugment
import me.fzzyhmstrs.amethyst_imbuement.entity.PlayerLightningEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import me.fzzyhmstrs.fzzy_core.coding_util.PersistentEffectHelper
import me.fzzyhmstrs.fzzy_core.raycaster_util.RaycasterUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.mob.Monster
import net.minecraft.item.Items
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.Heightmap
import net.minecraft.world.World

@Suppress("SpellCheckingInspection")
class LightningStormAugment: MiscAugment(ScepterTier.THREE,3), PersistentEffectHelper.PersistentEffect{

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withRange(8.0,1.0,0.0)
            .withDuration(0,120,0)
            .withDamage(5.0f,1.0f)

    override val delay = PerLvlI(21,-3,0)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.FURY,400,80,
            20,imbueLevel,10,LoreTier.HIGH_TIER, Items.COPPER_BLOCK)
    }

    override fun applyTasks(
        world: World,
        user: LivingEntity,
        hand: Hand,
        level: Int,
        effects: AugmentEffect
    ): Boolean {
        var target: Entity? = null
        val hit = RaycasterUtil.raycastHit(distance = effects.range(level) * 2,user, includeFluids = true)
        if (hit != null) {
            if (hit.type == HitResult.Type.ENTITY) {
                target = (hit as EntityHitResult).entity
            }
        }
        return effect(world, target, user, level, hit, effects)
    }

    override fun effect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        hit: HitResult?,
        effect: AugmentEffect
    ): Boolean {
        val (_,entityList) = RaycasterUtil.raycastEntityArea(user,hit,effect.range(level))
        if (entityList.isEmpty()) {
            return false
        }
        val s = entityList.size
        var x = 0
        var y= 0
        var z = 0
        for(e in entityList){
            x += e.blockPos.x
            y += e.blockPos.y
            z += e.blockPos.z
        }
        val avgBlockPos = BlockPos(x/s,y/s,z/s)

        (world as ServerWorld).setWeather(0, 1200, true, true)
        if (!effect(world, user, entityList, level, effect)) return false
        val data = AugmentPersistentEffectData(world, user, avgBlockPos, entityList, level, effect)
        PersistentEffectHelper.setPersistentTickerNeed(RegisterEnchantment.LIGHTNING_STORM, delay.value(level),effect.duration(level), data)
        effect.accept(user, AugmentConsumer.Type.BENEFICIAL)
        world.playSound(null, user.blockPos, soundEvent(), SoundCategory.PLAYERS, 1.0F, 1.0F)
        return true
    }


    override fun effect(
        world: World,
        user: LivingEntity,
        entityList: MutableList<Entity>,
        level: Int,
        effect: AugmentEffect
    ): Boolean {
        user.isInvulnerable = true
        var successes = 0
        for (entity3 in entityList) {
            if(entity3 is Monster && world.isSkyVisible(entity3.blockPos)){
                //repalce with a player version that can pass consumers?
                val le = PlayerLightningEntity.createLightning(world, Vec3d.ofBottomCenter(entity3.blockPos), user, effect, level)
                if (world.spawnEntity(le)){
                    successes++
                }
            }
        }
        user.isInvulnerable = false
        return successes > 0
    }

    override fun persistentEffect(data: PersistentEffectHelper.PersistentEffectData) {
        if (data !is AugmentPersistentEffectData) return
        var tries = 2
        while (tries >= 0) {
            val rnd1 = data.entityList.size
            val rnd2 = data.world.random.nextInt(rnd1)
            val entity = data.entityList[rnd2]
            val rnd3 = if(!entity.isAlive) {
                data.world.random.nextDouble()
            } else {
                0.0
            }

            val bP: BlockPos
            val bpXrnd: Int
            val bpZrnd: Int
            if (rnd3 > 0.3) {
                bP = entity.blockPos
                bpXrnd = data.world.random.nextInt(5) - 2
                bpZrnd = data.world.random.nextInt(5) - 2
            } else {
                bP = data.blockPos
                val range = data.effect.range(data.level)
                bpXrnd = data.world.random.nextInt((range*2 + 1).toInt()) - range.toInt()
                bpZrnd = data.world.random.nextInt((range*2 + 1).toInt()) - range.toInt()
            }
            val rndBlockPos = BlockPos(bP.x + bpXrnd,data.world.getTopY(Heightmap.Type.MOTION_BLOCKING,bP.x + bpXrnd,bP.z + bpZrnd), bP.z + bpZrnd)
            if (entity !is Monster || !data.world.isSkyVisible(rndBlockPos)){
                tries--
                continue
            }
            val le = PlayerLightningEntity.createLightning(data.world, Vec3d.ofBottomCenter(data.blockPos),data.user, data.effect, data.level)
            data.world.spawnEntity(le)
            break
        }
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ITEM_TRIDENT_THUNDER
    }

}