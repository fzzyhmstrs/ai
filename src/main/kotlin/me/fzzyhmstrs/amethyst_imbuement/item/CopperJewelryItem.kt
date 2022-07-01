package me.fzzyhmstrs.amethyst_imbuement.item

import com.google.common.collect.Multimap
import dev.emi.trinkets.api.SlotReference
import dev.emi.trinkets.api.TrinketItem
import me.fzzyhmstrs.amethyst_core.item_util.interfaces.Flavorful
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.world.World
import java.util.*

open class CopperJewelryItem(settings: Settings) : TrinketItem(settings), Flavorful<CopperJewelryItem> {

    override var flavor: String = ""
    override var glint: Boolean = false
    override var flavorDesc: String = ""

    override fun appendTooltip(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<Text>,
        context: TooltipContext
    ) {
        super.appendTooltip(stack, world, tooltip, context)
        addFlavorText(tooltip, context)
    }

    override fun getModifiers(
        stack: ItemStack,
        slot: SlotReference,
        entity: LivingEntity,
        uuid: UUID
    ): Multimap<EntityAttribute, EntityAttributeModifier>? {
        val modifiers = super.getModifiers(stack, slot, entity, uuid)
        modifiers.put(
            EntityAttributes.GENERIC_MOVEMENT_SPEED,
            EntityAttributeModifier(uuid, "amethyst_imbuement:movement_speed", 0.03, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        )
        return modifiers
    }

    override fun getFlavorItem(): CopperJewelryItem {
        return this
    }
}