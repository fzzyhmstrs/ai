package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.amethyst_core.nbt_util.Nbt
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.mob.HostileEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.registry.Registry

class GemOfPromiseItem(settings: Settings): Item(settings) {


    companion object{

        private const val KILL_TARGET = 30
        private const val FIRE_TARGET = 120
        private const val HIT_TARGET = 100
        private const val HEAL_TARGET = 140.0F
        private const val STATUS_TARGET = 10

        fun healersGemCheck(stack: ItemStack,inventory: PlayerInventory, healAmount: Float){
            val nbt = stack.orCreateNbt
            var healed = 0.0F
            if (nbt.contains("healed")){
                healed = nbt.getFloat("healed")
            }
            val newHeal = healed + healAmount
            if (newHeal >= HEAL_TARGET){
                stack.decrement(1)
                val newStack = ItemStack(RegisterItem.HEALERS_GEM)
                if (inventory.insertStack(newStack)) {
                    inventory.player.dropItem(newStack, false)
                }
            } else {
                nbt.putFloat("healed",newHeal)
            }
        }

        fun brutalGemCheck(stack: ItemStack, inventory: PlayerInventory, damageSource: DamageSource){
            val source = damageSource.source
            if (damageSource.name == "mob" || (source != null && source is HostileEntity)){
                val nbt = stack.orCreateNbt
                var hit = 0
                if (nbt.contains("mob_hit")){
                    hit = Nbt.readIntNbt("mob_hit",nbt)
                }
                val newHit = hit + 1
                if (newHit >= HIT_TARGET){
                    stack.decrement(1)
                    val newStack = ItemStack(RegisterItem.BRUTAL_GEM)
                    if (inventory.insertStack(newStack)) {
                        inventory.player.dropItem(newStack, false)
                    }
                } else {
                    Nbt.writeIntNbt("mob_hit",newHit,nbt)
                }

            }
        }

        fun inquisitiveGemCheck(stack: ItemStack, inventory: PlayerInventory, statusEffect: StatusEffect){
            val nbt = stack.orCreateNbt
            var compound = NbtCompound()
            if (nbt.contains("statuses")){
                compound = nbt.get("statuses") as NbtCompound
            }
            val statusIdentifier = Registry.STATUS_EFFECT.getId(statusEffect)?:return
            val id = statusIdentifier.toString()
            if (compound.contains(id)) return
            Nbt.writeIntNbt(id, 1, compound)
            val keys = compound.keys
            if (keys.size >= STATUS_TARGET){
                stack.decrement(1)
                val newStack = ItemStack(RegisterItem.INQUISITIVE_GEM)
                if (inventory.insertStack(newStack)) {
                    inventory.player.dropItem(newStack, false)
                }
            }
        }

        fun sparkingGemCheck(stack: ItemStack, inventory: PlayerInventory, damageSource: DamageSource){
            if (damageSource == DamageSource.LIGHTNING_BOLT){
                stack.decrement(1)
                val newStack = ItemStack(RegisterItem.SPARKING_GEM)
                if (inventory.insertStack(newStack)) {
                    inventory.player.dropItem(newStack, false)
                }
            }
        }

        fun blazingGemCheck(stack: ItemStack, inventory: PlayerInventory, player: LivingEntity, damageSource: DamageSource){
            if ((damageSource == DamageSource.ON_FIRE || damageSource == DamageSource.LAVA) && !player.hasStatusEffect(
                    StatusEffects.FIRE_RESISTANCE)){
                val nbt = stack.orCreateNbt
                var fire: Int = 0
                if (nbt.contains("on_fire")){
                    fire = Nbt.readIntNbt("on_fire",nbt)
                }
                val newFire = fire + 1
                if (newFire >= FIRE_TARGET){
                    stack.decrement(1)
                    val newStack = ItemStack(RegisterItem.BLAZING_GEM)
                    if (inventory.insertStack(newStack)) {
                        inventory.player.dropItem(newStack, false)
                    }
                } else {
                    Nbt.writeIntNbt("on_fire",newFire,nbt)
                }

            }
        }

        fun lethalGemCheck(stack: ItemStack, inventory: PlayerInventory){
            val nbt = stack.orCreateNbt
            var kills: Int = 0
            if (nbt.contains("kill_count")){
                kills = Nbt.readIntNbt("kill_count",nbt)
            }
            val newCount = kills + 1
            if (newCount >= KILL_TARGET){
                stack.decrement(1)
                val newStack = ItemStack(RegisterItem.LETHAL_GEM)
                if (inventory.insertStack(newStack)) {
                    inventory.player.dropItem(newStack, false)
                }
            } else {
                Nbt.writeIntNbt("kill_count", newCount,nbt)
            }
        }
    }

}
