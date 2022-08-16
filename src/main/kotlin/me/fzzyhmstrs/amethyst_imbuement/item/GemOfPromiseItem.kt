package me.fzzyhmstrs.amethyst_imbuement.item

import net.minecraft.entity.Entity
import net.minecraft.entity.damage.DamageRecord
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.world.World

class GemOfPromiseItem(settings: Settings): Item(settings) {

    override fun inventoryTick(stack: ItemStack, world: World, entity: Entity, slot: Int, selected: Boolean) {
        if (entity !is PlayerEntity) return
        if(!PlayerInventory.isValidHotbarIndex(slot)) return
        super.inventoryTick(stack, world, entity, slot, selected)
        val record = entity.damageTracker.mostRecentDamage
    }


    companion object{
        
        private const val KILL_TARGET = 30
        private const val FIRE_TARGET = 120
        private const val HIT_TARGET = 100
        private const val HEAL_TARGET = 80
        private const val STATUS_TARGET = 10
        
        fun healersGemCheck(stack: ItemStack,inventory: PlayerInventory, player: PlayerEntity, healAmount: Int){
            val nbt = stack.orCreateNbt
            var healed: Int = 0
            if (nbt.contains("healed")){
                    healed = Nbt.getIntNbt("healed",nbt)
                }
                val newHeal = healed + healAmount
                if (newHeal >= HEAL_TARGET){
                    stack.decrement(1)
                    todo()
                } else {
                    Nbt.writeIntNbt("healed",newHit,nbt)
                }
        }
        
        fun brutalGemCheck(stack: ItemStack, inventory: PlayerInventory, damageSource: DamageSource){
            val source = damageSource.source
            if (damageSource is DamageSource.MOB || (source != null && source is HostileEntity)){
                val nbt = stack.orCreateNbt
                var hit: Int = 0
                if (nbt.contains("mob_hit")){
                    hit = Nbt.getIntNbt("mob_hit",nbt)
                }
                val newHit = hit + 1
                if (newHit >= HIT_TARGET){
                    stack.decrement(1)
                    todo()
                } else {
                    Nbt.writeIntNbt("mob_hit",newHit,nbt)
                }
                
            }
        }
        
        fun inquisitiveGemCheck(stack: ItemStack,inventory: PlayerInventory, statusEffect: StatusEffect){
            val nbt = stack.orCreateNbt
            var compound = NbtCompound()
            if (nbt.contains("statuses")){
                compound = nbt.get("statuses") as NbtCompound
            }
            val id = statusEffect.id
            if (compound.contains(id)) return
            Nbt.writeIntNbt(id, 1, compound)
            val keys = compound.keys
            if (keys.size >= STATUS_TARGET){
                stack.decrement(1)
                todo()
            }
        }
        
        fun sparkingGemCheck(stack: ItemStack, inventory: PlayerInventory, damageSource: DamageSource){
            if (damageSource is DamageSource.LIGHTNING_BOLT){
                stack.decrement(1)
                todo()
            }
        }

        fun blazingGemCheck(stack: ItemStack, inventory: PlayerInventory,player: LivingEntity, damageSource: DamageSource){
            if ((damageSource is DamageSource.ON_FIRE || damageSource is DamageSource.LAVA) && !player.hasStatus(StatusEffects.FIRE_RESISTANCE)){
                val nbt = stack.orCreateNbt
                var fire: Int = 0
                if (nbt.contains("on_fire")){
                    fire = Nbt.getIntNbt("on_fire",nbt)
                }
                val newFire = fire + 1
                if (newFire >= FIRE_TARGET){
                    stack.decrement(1)
                    todo()
                } else {
                    Nbt.writeIntNbt("on_fire",newFire,nbt)
                }
                
            }
        }
        
        fun lethalGemCheck(stack: ItemStack, inventory: PlayerInventory){
            val nbt = stack.orCreateNbt
            var kills: Int = 0
            if (nbt.contains("kill_count")){
                kills = Nbt.getIntNbt("kill_count",nbt)
            }
            val newCount = kills + 1
            if (newCount >= KILL_TARGET){
                stack.decrement(1)
                TODO()
            } else {
                Nbt.writeIntNbt("kill_count", newCount,nbt)
            }
        }
    }

}
