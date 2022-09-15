package me.fzzyhmstrs.amethyst_imbuement.augment

import me.fzzyhmstrs.amethyst_core.trinket_util.EffectQueue
import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.TotemPassiveAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.entity.CrystallineGolemEntity
import me.fzzyhmstrs.amethyst_imbuement.item.TotemItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.text.Text

class GuardianAugment(weight: Rarity, mxLvl: Int = 1, vararg slot: EquipmentSlot): TotemPassiveAugment(weight,mxLvl, *slot) {

    override fun specialEffect(user: LivingEntity, level: Int, stack: ItemStack): Boolean {
        val world = user.world
        if (RegisterItem.TOTEM_OF_AMETHYST.checkCanUse(stack, world, user as PlayerEntity, 240, Text.translatable("augment_damage.guardian.check_can_use"))) {
            if (RegisterItem.TOTEM_OF_AMETHYST.manaDamage(stack, world, user, 240)) {
                RegisterItem.TOTEM_OF_AMETHYST.burnOutHandler(stack, RegisterEnchantment.GUARDIAN,user, Text.translatable("augment_damage.guardian.burnout"))
            }
            EffectQueue.addStatusToQueue(user, StatusEffects.REGENERATION, 400, 0)
            EffectQueue.addStatusToQueue(user, StatusEffects.RESISTANCE, 200, 0)
            RegisterEnchantment.GUSTING.effect(world,null,user,1,null,RegisterEnchantment.GUSTING.baseEffect)
            //spawn the golem right at the player
            val cge = CrystallineGolemEntity(RegisterEntity.CRYSTAL_GOLEM_ENTITY, world, AiConfig.entities.crystalGolemGuardianLifespan, user)
            cge.setPosition(user.pos)
            world.spawnEntity(cge)
        }
        return true
    }

}