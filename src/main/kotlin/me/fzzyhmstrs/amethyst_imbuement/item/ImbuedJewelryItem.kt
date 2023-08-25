package me.fzzyhmstrs.amethyst_imbuement.item

import com.google.common.collect.Multimap
import dev.emi.trinkets.api.SlotReference
import dev.emi.trinkets.api.TrinketEnums
import me.fzzyhmstrs.amethyst_core.item_util.AbstractAugmentJewelryItem
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import me.fzzyhmstrs.fzzy_core.mana_util.ManaItem
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtList
import net.minecraft.recipe.RecipeType
import java.util.*

open class ImbuedJewelryItem(settings: Settings): AbstractAugmentJewelryItem(settings), Reactant, ManaItem {

    override fun getModifiers(
        stack: ItemStack,
        slot: SlotReference,
        entity: LivingEntity,
        uuid: UUID
    ): Multimap<EntityAttribute, EntityAttributeModifier> {
        val modifiers = super.getModifiers(stack, slot, entity, uuid)
        modifiers.put(
            EntityAttributes.GENERIC_MOVEMENT_SPEED,
            EntityAttributeModifier(uuid, "amethyst_imbuement:movement_speed", 0.03, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        )
        return modifiers
    }

    override fun getDropRule(stack: ItemStack, slot: SlotReference, entity: LivingEntity): TrinketEnums.DropRule {
        return if (entity.hasStatusEffect(RegisterStatus.SOULBINDING)){
            TrinketEnums.DropRule.KEEP
        } else {
            TrinketEnums.DropRule.DEFAULT
        }
    }

    override fun canReact(stack: ItemStack, reagents: List<ItemStack>, player: PlayerEntity?, type: RecipeType<*>?): Boolean {
        return true
    }

    override fun react(stack: ItemStack, reagents: List<ItemStack>, player: PlayerEntity?, type: RecipeType<*>?) {
        for (reagent in reagents){
            val item = reagent.item
            if (item is SpellcastersReagent){
                if (stack.nbt?.contains("TrinketAttributeModifiers") == true) return
                val attribute = item.getAttributeModifier()
                val list = NbtList()
                val nbt = SpellcastersReagent.toNbt(attribute)
                list.add(nbt)
                stack.orCreateNbt.put("TrinketAttributeModifiers",list)
                break
            }
        }
    }

    override fun getItemBarColor(stack: ItemStack): Int {
        return AiConfig.items.manaItems.getItemBarColor(stack)
    }

    override fun canRepair(stack: ItemStack, ingredient: ItemStack): Boolean {
        return ingredient.isOf(Items.AMETHYST_SHARD) && stack.item is ImbuedJewelryItem
    }

    override fun getRepairTime(): Int {
        return 0
    }
}