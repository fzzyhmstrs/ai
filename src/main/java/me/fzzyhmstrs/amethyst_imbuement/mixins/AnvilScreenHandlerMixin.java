package me.fzzyhmstrs.amethyst_imbuement.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.fzzyhmstrs.amethyst_core.nbt_util.NbtKeys;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {


    public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }


    @WrapOperation(method = "updateResult", at = @At(value = "INVOKE", target = "net/minecraft/enchantment/EnchantmentHelper.set (Ljava/util/Map;Lnet/minecraft/item/ItemStack;)V"))
    private void amethyst_imbuement_transferDisenchantNbt(Map<Enchantment, Integer> enchantments, ItemStack stack, Operation<Void> operation){
        ItemStack stack2 = this.input.getStack(1);
        NbtCompound nbt2 = stack2.getNbt();
        if (nbt2 != null && nbt2.contains(NbtKeys.DISENCHANT_COUNT.str())){
            NbtCompound nbt = stack.getOrCreateNbt();
            int lvl = nbt2.getInt(NbtKeys.DISENCHANT_COUNT.str());
            nbt.putInt(NbtKeys.DISENCHANT_COUNT.str(), lvl);
        }
        operation.call(enchantments, stack);
    }

}
