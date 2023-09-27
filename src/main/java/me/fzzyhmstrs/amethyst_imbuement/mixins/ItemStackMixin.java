package me.fzzyhmstrs.amethyst_imbuement.mixins;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.AttributeProviding;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemStack.class, priority = 3000)
public abstract class ItemStackMixin {

    @Shadow public abstract Item getItem();

    @Shadow public abstract NbtList getEnchantments();

    //credit for this mixin (C) Timefall Development, Chronos Sacaria, Kluzzio
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void amethyst_imbuement_onUsingItemWhilstStunned(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir){
        if (user.hasStatusEffect(RegisterStatus.INSTANCE.getSTUNNED())){
            cir.setReturnValue(TypedActionResult.fail((ItemStack) (Object) this));
        }
    }

    @ModifyReturnValue(method = "getAttributeModifiers", at = @At("RETURN"))
    private Multimap<EntityAttribute, EntityAttributeModifier> amethyst_imbuement_addResilientAttributesToModifiers(Multimap<EntityAttribute, EntityAttributeModifier> original, EquipmentSlot slot){
        if (getItem() instanceof EnchantedBookItem) return original;
        if (getItem() instanceof Equipment equipment && equipment.getSlotType() != slot) return original;
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
