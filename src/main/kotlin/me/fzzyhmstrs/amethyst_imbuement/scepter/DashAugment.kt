package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.MiscAugment
import me.fzzyhmstrs.amethyst_imbuement.util.ScepterObject
import me.fzzyhmstrs.amethyst_imbuement.util.SpellType
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.MovementType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class DashAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): MiscAugment(tier,maxLvl, *slot) {

    override fun effect(world: World, target: Entity?, user: LivingEntity, level: Int, hit: HitResult?): Boolean {
        if (user !is PlayerEntity) return false
        val y: Float = user.getYaw()
        val p: Float = user.getPitch()
        var g =
            -MathHelper.sin(y * (Math.PI.toFloat() / 180)) * MathHelper.cos(p * (Math.PI.toFloat() / 180))
        var h = -MathHelper.sin(p * (Math.PI.toFloat() / 180))
        var k =
            MathHelper.cos(y * (Math.PI.toFloat() / 180)) * MathHelper.cos(p * (Math.PI.toFloat() / 180))
        val l = MathHelper.sqrt(g * g + h * h + k * k)
        val m: Float = 3.0f * ((1.0f + level.toFloat()) / 4.0f)

        g *= m / l
        h *= m / l
        k *= m / l
        user.addVelocity(g.toDouble(),h.toDouble(),k.toDouble())
        user.setRiptideTicks(20)

        if (user.isOnGround()) {
            user.move(MovementType.SELF, Vec3d(0.0, 1.1999999284744263, 0.0))
        }
        world.playSoundFromEntity(null,user,soundEvent(),SoundCategory.PLAYERS,1.0F,1.0F)
        return true
    }

    override fun clientTask(world: World, user: LivingEntity, hand: Hand, level: Int) {
        effect(world, null, user, level, null)
    }

    override fun augmentStat(imbueLevel: Int): ScepterObject.AugmentDatapoint {
        return ScepterObject.AugmentDatapoint(SpellType.WIT,36,10,5,imbueLevel,1, Items.SUGAR)
    }

    override fun rangeOfEffect(): Double {
        return 0.0
    }

    override fun soundEvent(): SoundEvent {
        return when(MinecraftClient.getInstance().world?.random?.nextInt(3)?:0){
            0-> SoundEvents.ITEM_TRIDENT_RIPTIDE_1
            1-> SoundEvents.ITEM_TRIDENT_RIPTIDE_2
            2-> SoundEvents.ITEM_TRIDENT_RIPTIDE_3
            else-> SoundEvents.ITEM_TRIDENT_RIPTIDE_1
        }

    }
}