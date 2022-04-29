package me.fzzyhmstrs.amethyst_imbuement.augment

import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.TotemPassiveAugment
import me.fzzyhmstrs.amethyst_imbuement.entity.CrystallineGolemEntity
import me.fzzyhmstrs.amethyst_imbuement.item.TotemItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.text.TranslatableText

class GuardianAugment(weight: Rarity, mxLvl: Int = 1, vararg slot: EquipmentSlot): TotemPassiveAugment(weight,mxLvl, *slot) {

    override fun specialEffect(user: LivingEntity, level: Int, stack: ItemStack): Boolean {
        val world = user.world
        if (TotemItem.checkCanUseHandler(stack, world, user as PlayerEntity, 240, TranslatableText("augment_damage.guardian.check_can_use").toString())) {
            if (TotemItem.damageHandler(stack, world, user, 240)) {
                TotemItem.burnOutHandler(stack, RegisterEnchantment.GUARDIAN,user, TranslatableText("augment_damage.guardian.burnout").toString())
            }
            addStatusToQueue(user.uuid, StatusEffects.REGENERATION, 400, 0)
            addStatusToQueue(user.uuid, StatusEffects.RESISTANCE, 400, 0)
            //spawn the golem right at the player
            val cge = CrystallineGolemEntity(RegisterEntity.CRYSTAL_GOLEM_ENTITY, world, 900)
            cge.setPosition(user.pos)
            world.spawnEntity(cge)
        }
        return true
    }

}