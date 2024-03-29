package me.fzzyhmstrs.amethyst_imbuement.mixins;

import me.fzzyhmstrs.amethyst_imbuement.item.TotemItem;
import me.fzzyhmstrs.amethyst_imbuement.util.RecipeUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Mixin(ServerPlayerEntity.class)
abstract public class ServerPlayerEntityMixin {

    @Inject(method = "copyFrom", at= @At(value = "HEAD"))
    private void amethyst_imbuement_copyInventoryIfSoulBound(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci){

            boolean canClone = false;
            for (ItemStack stack : oldPlayer.getInventory().main){
                if (stack.getItem() instanceof TotemItem){
                    NbtCompound nbt = stack.getNbt();
                    if (nbt != null){
                        if (nbt.contains("cloneInventory")){
                            canClone = true;
                            nbt.remove("cloneInventory");
                            break;
                        }
                    }
                }
            }
            if (!canClone)
                for (ItemStack stack : oldPlayer.getInventory().offHand){
                    if (stack.getItem() instanceof TotemItem){
                        NbtCompound nbt = stack.getNbt();
                        if (nbt != null){
                            if (nbt.contains("cloneInventory")){
                                canClone = true;
                                nbt.remove("cloneInventory");
                                break;
                            }
                        }
                    }
                }
            if (!canClone)
                for (ItemStack stack : oldPlayer.getInventory().armor){
                    if (stack.getItem() instanceof TotemItem){
                        NbtCompound nbt = stack.getNbt();
                        if (nbt != null){
                            if (nbt.contains("cloneInventory")){
                                canClone = true;
                                nbt.remove("cloneInventory");
                                break;
                            }
                        }
                    }
                }
            if (canClone) {
                ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
                player.getInventory().clone(oldPlayer.getInventory());
            }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void amethyst_imbuement_writeFavoritesToNbt(NbtCompound nbt, CallbackInfo ci){
        UUID uuid = ((ServerPlayerEntity)(Object)this).getUuid();
        if (RecipeUtil.INSTANCE.hasFavorites(uuid)){
            List<ItemStack> list = RecipeUtil.INSTANCE.getFavorites(uuid);
            if (!list.isEmpty()) {
                NbtList nbtList = new NbtList();
                for (ItemStack stack : list) {
                    NbtCompound stackCompound = new NbtCompound();
                    stack.writeNbt(stackCompound);
                    nbtList.add(stackCompound);
                }
                nbt.put("imbuing_favorites",nbtList);
            }
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void amethyst_imbuement_readFavoritesFromNbt(NbtCompound nbt, CallbackInfo ci){
        if (nbt.contains("imbuing_favorites")){
            NbtList list = nbt.getList("imbuing_favorites",10);
            List<ItemStack> stacks = new LinkedList<>();
            for (int i = 0; i < list.size(); ++i) {
                NbtCompound stackCompound = list.getCompound(i);
                ItemStack stack = ItemStack.fromNbt(stackCompound);
                stacks.add(stack);
            }
            UUID uuid = ((ServerPlayerEntity)(Object)this).getUuid();
            RecipeUtil.INSTANCE.setFavorites(uuid,stacks);
        }
    }

}
