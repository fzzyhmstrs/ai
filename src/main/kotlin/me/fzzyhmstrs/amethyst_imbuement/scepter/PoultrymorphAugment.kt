package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.augments.MinorSupportAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import me.fzzyhmstrs.fzzy_core.coding_util.PersistentEffectHelper
import me.fzzyhmstrs.fzzy_core.raycaster_util.RaycasterUtil
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.passive.ChickenEntity
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class PoultrymorphAugment: MinorSupportAugment(ScepterTier.TWO,5), PersistentEffectHelper.PersistentEffect {

    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withDuration(250,50)
            .withRange(4.0,.5)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.WIT,1200,200,
            13, imbueLevel,35, LoreTier.NO_TIER, Items.FEATHER)
    }

    override fun applyTasks(world: World, user: LivingEntity, hand: Hand, level: Int, effects: AugmentEffect): Boolean {
        val stack = user.getStackInHand(hand)
        if (stack.item != RegisterItem.A_SCEPTER_SO_FOWL) return false
        val target = RaycasterUtil.raycastEntity(distance = effects.range(level),user)
        return if(target != null && target !is SpellCastingEntity && target !is ChickenEntity) {
            val nbt = NbtCompound()
            if (!target.saveSelfNbt(nbt)) return false
            val chickenEntity = EntityType.CHICKEN.create(world)?:return false
            val pos = target.pos
            chickenEntity.refreshPositionAndAngles(pos.x,pos.y,pos.z, target.yaw, target.pitch)
            chickenEntity.isInvulnerable = true
            if (!world.spawnEntity(chickenEntity)) return false
            val data = PoultrymorphPersistentData(nbt,chickenEntity.id,Vec3d(target.pos.toVector3f()),world)
            target.discard()
            PersistentEffectHelper.setPersistentTickerNeed(this,effects.duration(level),effects.duration(level),data)
            if (world is ServerWorld){
                world.spawnParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,chickenEntity.x,chickenEntity.y + chickenEntity.height/2.0,chickenEntity.z,15,0.4,0.6,0.4,0.0)
            }
            world.playSound(null,user.blockPos,SoundEvents.ENTITY_FIREWORK_ROCKET_TWINKLE_FAR, SoundCategory.NEUTRAL,0.7f,1.0f)
            world.playSound(null,user.blockPos,soundEvent(), SoundCategory.NEUTRAL,1.0f,1.0f)
            true
        } else {
            false
        }
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_CHICKEN_HURT
    }

    override val delay: PerLvlI
        get() = PerLvlI()

    override fun persistentEffect(data: PersistentEffectHelper.PersistentEffectData) {
        if (data !is PoultrymorphPersistentData) return
        EntityType.loadEntityWithPassengers(data.nbt,data.world){ storedEntity ->
            val pos = data.world.getEntityById(data.id)?.also { it.discard() }?.pos ?:data.fallbackPos
            storedEntity.refreshPositionAndAngles(pos.x,pos.y,pos.z,0f,0f)
            if (!data.world.spawnEntity(storedEntity)){
                null
            } else {
                val world = data.world
                if (world is ServerWorld) {
                    world.spawnParticles(
                        ParticleTypes.ANGRY_VILLAGER,
                        storedEntity.x,
                        storedEntity.y + storedEntity.height / 2.0,
                        storedEntity.z,
                        30,
                        storedEntity.width.toDouble(),
                        storedEntity.height *0.9,
                        storedEntity.width.toDouble(),
                        0.0
                    )
                }
                world.playSound(null,storedEntity.blockPos,SoundEvents.ENTITY_FIREWORK_ROCKET_TWINKLE_FAR, SoundCategory.NEUTRAL,0.7f,1.0f)
                world.playSound(null,storedEntity.blockPos,SoundEvents.ENTITY_CHICKEN_AMBIENT, SoundCategory.NEUTRAL,1.0f,1.0f)
                storedEntity

            }
        }
    }

    class PoultrymorphPersistentData(val nbt: NbtCompound,val id: Int, val fallbackPos: Vec3d, val world: World): PersistentEffectHelper.PersistentEffectData
}
