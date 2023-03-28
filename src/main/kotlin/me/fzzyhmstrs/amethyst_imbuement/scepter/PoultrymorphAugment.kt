package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.MinorSupportAugment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import me.fzzyhmstrs.fzzy_core.coding_util.PersistentEffectHelper
import me.fzzyhmstrs.fzzy_core.raycaster_util.RaycasterUtil
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.nbt.NbtCompound
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
        return AugmentDatapoint(SpellType.FURY,20,8,
            10, imbueLevel,2, LoreTier.LOW_TIER, RegisterItem.GLOWING_FRAGMENT)
    }

    override fun applyTasks(world: World, user: LivingEntity, hand: Hand, level: Int, effects: AugmentEffect): Boolean {
        val stack = user.getStackInHand(hand)
        if (stack.item != RegisterItem.A_SCEPTER_SO_FOWL) return false
        val target = RaycasterUtil.raycastEntity(distance = effects.range(level),user)
        return if(target != null) {
            val nbt = NbtCompound()
            if (!target.saveSelfNbt(nbt)) return false
            val chickenEntity = EntityType.CHICKEN.create(world)?:return false
            val pos = target.pos
            chickenEntity.refreshPositionAndAngles(pos.x,pos.y,pos.z, target.yaw, target.pitch)
            if (!world.spawnEntity(chickenEntity)) return false
            val data = PoultrymorphPersistentData(nbt,chickenEntity.id,Vec3d(target.pos.toVector3f()),world)
            target.discard()
            PersistentEffectHelper.setPersistentTickerNeed(this,effects.duration(level),effects.duration(level),data)
            true
        } else {
            false
        }
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_ELDER_GUARDIAN_HURT
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
                storedEntity
            }
        }
    }

    class PoultrymorphPersistentData(val nbt: NbtCompound,val id: Int, val fallbackPos: Vec3d, val world: World): PersistentEffectHelper.PersistentEffectData
}
