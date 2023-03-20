package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.MiscAugment
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.FireworkRocketEntity
import net.minecraft.item.FireworkRocketItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.DyeColor
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World

class FlareAugment: MiscAugment(ScepterTier.ONE,15) {

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withAmplifier(1)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.GRACE,15,3,
            1,imbueLevel,1, LoreTier.NO_TIER, Items.FIREWORK_STAR)
    }

    override fun effect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        hit: HitResult?,
        effect: AugmentEffect
    ): Boolean {
        val stack = ItemStack(Items.FIREWORK_ROCKET)
        val nbt = stack.getOrCreateSubNbt("Explosion")
        val colors: MutableList<Int> = mutableListOf()
        val type = if (level <= 5){
            colors.add(DyeColor.values()[world.random.nextInt(DyeColor.values().size)].fireworkColor)
            world.random.nextInt(2)
        } else if (level <= 10){
            if (world.random.nextFloat() <0.5){
                nbt.putBoolean("Flicker",true)
            }
            colors.add(DyeColor.values()[world.random.nextInt(DyeColor.values().size)].fireworkColor)
            colors.add(DyeColor.values()[world.random.nextInt(DyeColor.values().size)].fireworkColor)
            world.random.nextInt(3)
        } else {
            if (world.random.nextFloat() <0.5){
                nbt.putBoolean("Flicker",true)
            }
            if (world.random.nextFloat() <0.5){
                nbt.putBoolean("Trail",true)
            }
            colors.add(DyeColor.values()[world.random.nextInt(DyeColor.values().size)].fireworkColor)
            colors.add(DyeColor.values()[world.random.nextInt(DyeColor.values().size)].fireworkColor)
            colors.add(DyeColor.values()[world.random.nextInt(DyeColor.values().size)].fireworkColor)
            world.random.nextInt(5)
        }
        nbt.putIntArray("Colors",colors)
        nbt.putByte("Type",type.toByte())
        FireworkRocketItem.setFlight(stack,world.random.nextInt(3).toByte())
        val fireworkRocketEntity = FireworkRocketEntity(world, stack, user)
        val bl = world.spawnEntity(fireworkRocketEntity)
        stack.decrement(1)
        return bl
    }
}