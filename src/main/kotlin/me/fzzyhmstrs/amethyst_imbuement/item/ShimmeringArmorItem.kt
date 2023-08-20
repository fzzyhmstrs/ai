package me.fzzyhmstrs.amethyst_imbuement.item

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import me.fzzyhmstrs.amethyst_core.registry.RegisterAttribute
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.item.ArmorItem
import net.minecraft.item.ArmorMaterial
import net.minecraft.util.Util
import java.util.*

class ShimmeringArmorItem(material: ArmorMaterial, type: Type, settings: Settings) : ArmorItem(material, type, settings) {

    private val attributeModifiers: Multimap<EntityAttribute, EntityAttributeModifier> by lazy {
        val map: ArrayListMultimap<EntityAttribute, EntityAttributeModifier> = ArrayListMultimap.create()
        map.putAll(super.getAttributeModifiers(type.equipmentSlot))
        val uUID = MODIFIERS[type] ?: UUID.randomUUID()
        when (type){
            Type.HELMET -> map.put(RegisterAttribute.SPELL_MANA_COST, EntityAttributeModifier(uUID,"shimmering_mana_cost",0.05,EntityAttributeModifier.Operation.MULTIPLY_TOTAL))
            Type.CHESTPLATE -> map.put(RegisterAttribute.SPELL_EXPERIENCE, EntityAttributeModifier(uUID,"shimmering_xp",0.1,EntityAttributeModifier.Operation.MULTIPLY_TOTAL))
            Type.LEGGINGS -> map.put(RegisterAttribute.SPELL_DURATION, EntityAttributeModifier(uUID,"shimmering_duration",0.05,EntityAttributeModifier.Operation.MULTIPLY_TOTAL))
            Type.BOOTS -> map.put(RegisterAttribute.SPELL_RANGE, EntityAttributeModifier(uUID,"shimmering_range",0.05,EntityAttributeModifier.Operation.MULTIPLY_TOTAL))
        }
        map
    }

    private val MODIFIERS: EnumMap<Type, UUID> = Util.make(EnumMap<Type, UUID>(Type::class.java)) { uuidMap: EnumMap<Type, UUID> ->
        uuidMap[Type.BOOTS] = UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B")
        uuidMap[Type.LEGGINGS] = UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D")
        uuidMap[Type.CHESTPLATE] = UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E")
        uuidMap[Type.HELMET] = UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")
    }

    override fun getAttributeModifiers(slot: EquipmentSlot): Multimap<EntityAttribute, EntityAttributeModifier>{
        if (slot == type.equipmentSlot) {
                return this.attributeModifiers
        }
        return super.getAttributeModifiers(slot)
    }

}