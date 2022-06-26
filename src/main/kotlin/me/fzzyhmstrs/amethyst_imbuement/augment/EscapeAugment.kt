package me.fzzyhmstrs.amethyst_imbuement.augment

import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.UsedActiveAugment
import me.fzzyhmstrs.amethyst_imbuement.item.TotemItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.TranslatableText
import net.minecraft.world.Heightmap

class EscapeAugment(weight: Rarity, mxLvl: Int = 1, vararg slot: EquipmentSlot): UsedActiveAugment(weight,mxLvl, *slot) {

    override fun useEffect(user: LivingEntity, level: Int, stack: ItemStack) {
        val world = user.world
        val rndX = user.blockPos.x + world.random.nextInt(17) - 8
        val rndZ = user.blockPos.z + world.random.nextInt(17) - 8
        val rndY = world.getTopY(Heightmap.Type.MOTION_BLOCKING,rndX,rndZ)
        if(TotemItem.checkCanUseHandler(stack, world, user as PlayerEntity, 120, TranslatableText("augment_damage.escape.check_can_use"))) {
            if (TotemItem.damageHandler(stack, world, user, 120)) {
                TotemItem.burnOutHandler(stack, RegisterEnchantment.ESCAPE,user, TranslatableText("augment_damage.escape.burnout").toString())
            }
            user.teleport(rndX.toDouble(), (rndY + 1).toDouble(), rndZ.toDouble())
            world.playSound(
                null,
                user.blockPos,
                SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT,
                SoundCategory.PLAYERS,
                1.0F,
                1.0F
            )
            user.itemCooldownManager.set(stack.item, 1200)
        }
    }
}