package me.fzzyhmstrs.amethyst_imbuement.item.promise

import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterCriteria
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterModifier
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.mob.HostileEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier

class BrutalGemItem(settings: Settings): IgnitedGemItem(settings) {

    private val HIT_TARGET by lazy {
        AiConfig.items.gems.hitTarget.get()
    }
  
    override fun giveTooltipHint(nbt: NbtCompound, stack: ItemStack, tooltip: MutableList<Text>){
        if (nbt.contains("mob_hit")){
            val hit = nbt.getInt("mob_hit").toFloat()
            val progress = hit/ HIT_TARGET.toFloat()*100.0F
            tooltip.add(AcText.translatable("item.amethyst_imbuement.gem_of_promise.brutal", progress).formatted(Formatting.GRAY))
        }
    }

    override fun getModifier(): Identifier {
        return RegisterModifier.BLESSED.modifierId
    }

    fun brutalGemCheck(stack: ItemStack, inventory: PlayerInventory, damageSource: DamageSource){
            val source = damageSource.source
            if (damageSource.name == "mob" || (source != null && source is HostileEntity)){
                val nbt = stack.orCreateNbt
                var hit = 0
                if (nbt.contains("mob_hit")){
                    hit = nbt.getInt("mob_hit")
                }
                val newHit = hit + 1
                if (newHit >= HIT_TARGET){
                    stack.decrement(1)
                    val newStack = ItemStack(RegisterItem.BRUTAL_GEM)
                    inventory.offerOrDrop(newStack)
                    val player = inventory.player
                    if (player is ServerPlayerEntity) {
                        RegisterCriteria.IGNITE.trigger(player)
                    }
                } else {
                    nbt.putInt("mob_hit",newHit)
                }

            }
        }

}
