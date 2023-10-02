package me.fzzyhmstrs.amethyst_imbuement.item

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import me.fzzyhmstrs.amethyst_core.event.ModifyModifiersEvent
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentModifier
import me.fzzyhmstrs.amethyst_core.modifier_util.ModifierHelper
import me.fzzyhmstrs.amethyst_core.registry.RegisterAttribute
import me.fzzyhmstrs.amethyst_imbuement.item.promise.IgnitedGemItem
import me.fzzyhmstrs.fzzy_core.interfaces.Modifiable
import me.fzzyhmstrs.fzzy_core.item_util.CustomFlavorItem
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.recipe.RecipeType
import java.util.*

class WitchesOrbItem(settings: Settings)
    : 
    CustomFlavorItem(settings), Reactant, Modifiable
{

    private val attributeUuid: UUID = UUID.fromString("5699133a-4371-11ee-be56-0242ac120002")
    private val attributes: Multimap<EntityAttribute, EntityAttributeModifier>

    init{
        ModifyModifiersEvent.EVENT.register{ _,user,_,modifiers ->
            for (stack in user.handItems) {
                if (stack.item is WitchesOrbItem) {
                    val focusMods = ModifierHelper.getActiveModifiers(stack)
                    return@register modifiers.combineWith(focusMods, AugmentModifier())
                }
            }
            modifiers
        }
        val map: Multimap<EntityAttribute, EntityAttributeModifier> = ArrayListMultimap.create()
        map.put(
            RegisterAttribute.SPELL_DAMAGE,
            EntityAttributeModifier(attributeUuid,"witches_spell_damage",0.075, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        )
        attributes = map
    }

    override fun getAttributeModifiers(slot: EquipmentSlot): Multimap<EntityAttribute, EntityAttributeModifier> {
        if (slot == EquipmentSlot.OFFHAND) {
            return attributes
        }
        return super.getAttributeModifiers(slot)
    }

    override fun canReact(stack: ItemStack, reagents: List<ItemStack>, player: PlayerEntity?, type: RecipeType<*>?): Boolean {
        return true
    }

    override fun react(stack: ItemStack, reagents: List<ItemStack>, player: PlayerEntity?, type: RecipeType<*>?) {
        for (reagent in reagents){
            val item = reagent.item
            if (item is IgnitedGemItem){
                val id = item.getModifier()
                if (ModifierHelper.getModifierByType(id) != null){
                    ModifierHelper.addModifier(id,stack)
                }
                break
            }
        }
    }
}
