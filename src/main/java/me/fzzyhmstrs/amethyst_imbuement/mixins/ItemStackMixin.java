package me.fzzyhmstrs.amethyst_imbuement.mixins;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.UUID;

@Mixin(value = ItemStack.class, priority = 3000)
public abstract class ItemStackMixin {

    @Shadow public abstract Item getItem();

    @ModifyReturnValue(method = "getAttributeModifiers", at = @At("RETURN"))
    private Multimap<EntityAttribute, EntityAttributeModifier> amethyst_imbuement_addResilientAttributesToModifiers(Multimap<EntityAttribute, EntityAttributeModifier> original, EquipmentSlot slot){
        if (getItem() instanceof ArmorItem armorItem){
            EquipmentSlot itemSlot = armorItem.getSlotType();
            if (slot != itemSlot) return original;
            int lvlResilience = EnchantmentHelper.getLevel(RegisterEnchantment.INSTANCE.getRESILIENCE(),(ItemStack) (Object)this);
            Multimap<EntityAttribute, EntityAttributeModifier> newMap = ArrayListMultimap.create(original);
            if (lvlResilience > 0){
                UUID uuid = RegisterEnchantment.INSTANCE.getRESILIENCE().getUuids$ai_main().get(slot);
                String name = "resilience_modifier_"+slot.getName();
                newMap.put(EntityAttributes.GENERIC_ARMOR, new EntityAttributeModifier(uuid,name,0.0+ lvlResilience, EntityAttributeModifier.Operation.ADDITION));
                newMap.put(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, new EntityAttributeModifier(uuid,name,(0.0 + lvlResilience)/2.0, EntityAttributeModifier.Operation.ADDITION));
            }
            int lvlSteadfast = EnchantmentHelper.getLevel(RegisterEnchantment.INSTANCE.getSTEADFAST(),(ItemStack) (Object)this);
            if (lvlSteadfast > 0){
                UUID uuid = RegisterEnchantment.INSTANCE.getSTEADFAST().getUuids$ai_main().get(slot);
                String name = "steadfast_modifier_"+slot.getName();
                newMap.put(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, new EntityAttributeModifier(uuid,name,lvlSteadfast * 0.05, EntityAttributeModifier.Operation.ADDITION));
            }
            return newMap;
        }
        return original;
    }

}
