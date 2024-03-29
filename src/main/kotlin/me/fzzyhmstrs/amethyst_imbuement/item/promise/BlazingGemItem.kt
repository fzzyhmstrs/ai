package me.fzzyhmstrs.amethyst_imbuement.item.promise

import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterCriteria
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterModifier
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.tag.DamageTypeTags
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier

class BlazingGemItem(settings: Settings): IgnitedGemItem(settings) {
    
    private val FIRE_TARGET by lazy{
        AiConfig.items.gems.fireTarget.get()
    }

    override fun giveTooltipHint(nbt: NbtCompound, stack: ItemStack, tooltip: MutableList<Text>){
        if (nbt.contains("on_fire")){
            val fire = nbt.getInt("on_fire").toFloat()
            val progress = fire/ FIRE_TARGET.toFloat()*100.0F
            tooltip.add(AcText.translatable("item.amethyst_imbuement.gem_of_promise.blazing", progress).formatted(Formatting.RED))
        }
    }

    override fun getModifier(): Identifier {
        return RegisterModifier.INNER_FIRE.modifierId
    }

    fun blazingGemCheck(stack: ItemStack, inventory: PlayerInventory, player: LivingEntity, damageSource: DamageSource){
            if ((damageSource.isIn(DamageTypeTags.IS_FIRE))
                && !player.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)
                && !player.isFireImmune)
            {
                val nbt = stack.orCreateNbt
                var fire = 0
                if (nbt.contains("on_fire")){
                    fire = nbt.getInt("on_fire")
                }
                val newFire = fire + 1
                if (newFire >= FIRE_TARGET){
                    stack.decrement(1)
                    val newStack = ItemStack(RegisterItem.BLAZING_GEM)
                    inventory.offerOrDrop(newStack)
                    if (player is ServerPlayerEntity) {
                        RegisterCriteria.IGNITE.trigger(player)
                    }
                } else {
                    nbt.putInt("on_fire",newFire)
                }

            }
        }

}
