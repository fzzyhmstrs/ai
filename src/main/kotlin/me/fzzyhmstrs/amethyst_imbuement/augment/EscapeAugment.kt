package me.fzzyhmstrs.amethyst_imbuement.augment

import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.UsedActiveAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.world.Heightmap

class EscapeAugment(weight: Rarity, mxLvl: Int = 1, vararg slot: EquipmentSlot): UsedActiveAugment(weight,mxLvl, *slot) {

    override fun useEffect(user: LivingEntity, level: Int, stack: ItemStack) {
        val world = user.world
        val rndX = user.blockPos.x + world.random.nextInt(17) - 8
        val rndZ = user.blockPos.z + world.random.nextInt(17) - 8
        val rndY = world.getTopY(Heightmap.Type.MOTION_BLOCKING,rndX,rndZ)
        if(RegisterItem.TOTEM_OF_AMETHYST.checkCanUse(stack, world, user as PlayerEntity, 120, AcText.translatable("augment_damage.escape.check_can_use"))) {
            if (RegisterItem.TOTEM_OF_AMETHYST.manaDamage(stack, world, user, 120)) {
                if (AiConfig.trinkets.enableBurnout.get()) {
                    RegisterItem.TOTEM_OF_AMETHYST.burnOutHandler(
                        stack,
                        RegisterEnchantment.ESCAPE,
                        user,
                        AcText.translatable("augment_damage.escape.burnout")
                    )
                }
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