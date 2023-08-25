package me.fzzyhmstrs.amethyst_imbuement.mixins;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.AttributeProviding;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ItemStack.class, priority = 3000)
public abstract class ItemStackMixin {

    @Shadow public abstract Item getItem();

    @Shadow public abstract NbtList getEnchantments();

    @ModifyReturnValue(method = "getAttributeModifiers", at = @At("RETURN"))
    private Multimap<EntityAttribute, EntityAttributeModifier> amethyst_imbuement_addResilientAttributesToModifiers(Multimap<EntityAttribute, EntityAttributeModifier> original, EquipmentSlot slot){
        if (getItem() instanceof ArmorItem armorItem && armorItem.getSlotType() != slot) return original;
        Multimap<EntityAttribute, EntityAttributeModifier> newMap = null;
        boolean modified = false;
        for (var entry: EnchantmentHelper.get((ItemStack) (Object) this).entrySet()){
            if (entry.getKey() instanceof AttributeProviding attributeProvidingEnchant){
                if (newMap == null) newMap = ArrayListMultimap.create(original);
                attributeProvidingEnchant.modifyAttributeMap(newMap,slot, entry.getValue());
                modified = true;
            }
        }
        return modified ? newMap : original;
    }

}
