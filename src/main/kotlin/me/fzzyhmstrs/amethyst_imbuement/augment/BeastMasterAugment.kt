package me.fzzyhmstrs.amethyst_imbuement.augment
import me.fzzyhmstrs.amethyst_core.nbt_util.NbtKeys
import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.ActiveAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.item.TotemItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.entity_util.PlayerCreatable
import me.fzzyhmstrs.fzzy_core.trinket_util.EffectQueue
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.Tameable
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import kotlin.math.min

class BeastMasterAugment(weight: Rarity, mxLvl: Int = 1, vararg slot: EquipmentSlot): ActiveAugment(weight,mxLvl,*slot) {

    override fun canActivate(user: LivingEntity, level: Int, stack: ItemStack): Boolean {
        return RegisterItem.TOTEM_OF_AMETHYST.checkCanUse(stack,user.world,user,10)
    }

    override fun activateEffect(user: LivingEntity, level: Int, stack: ItemStack) {
        //EffectQueue.addStatusToQueue(user,StatusEffects.FIRE_RESISTANCE,240,0)
        val box = user.boundingBox.expand(16.0)
        val list = user.world.getOtherEntities(user,box).stream().filter { it is PlayerCreatable && it.owner == user || it is Tameable && it.owner == user }.toList()
        if (list.isEmpty()) return
        val amplifier = if(list.size < 6) 1 else 0
        var helped = 0
        for (entity in list){
            if (entity !is LivingEntity) continue
            if (level >= 1)
                entity.addStatusEffect(StatusEffectInstance(StatusEffects.STRENGTH,60,amplifier + 1))
            if (level >= 2)
                entity.addStatusEffect(StatusEffectInstance(RegisterStatus.BLESSED,60,amplifier))
            if (level >= 3)
                entity.addStatusEffect(StatusEffectInstance(StatusEffects.SPEED,60,amplifier))
            helped++
        }
        if (helped == 0) return
        val rnd = user.world.random.nextFloat()
        if (rnd <= min(1f,0.2f * helped)) {
            if (RegisterItem.TOTEM_OF_AMETHYST.manaDamage(stack, user.world, user as PlayerEntity, 1)) {
                if (AiConfig.trinkets.enableBurnout.get()) {
                    RegisterItem.TOTEM_OF_AMETHYST.burnOutHandler(
                        stack,
                        RegisterEnchantment.BEAST_MASTER,
                        user,
                        AcText.translatable("augment_damage.beast_master.burnout")
                    )
                } else {
                    val item = stack.item
                    if (item is TotemItem){
                        val nbt = stack.orCreateNbt
                        item.inactiveEnchantmentTasks(stack,user.world,user)
                        nbt.putBoolean(NbtKeys.TOTEM.str(), false)
                    }
                }
            }
        }
    }

}