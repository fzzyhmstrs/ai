package me.fzzyhmstrs.amethyst_imbuement.mixins;

import me.fzzyhmstrs.amethyst_imbuement.enchantment.VeinMinerEnchantment;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterKeybind;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(value= EnvType.CLIENT)
@Mixin(MiningToolItem.class)
public class MiningToolItemMixin {

    @Final
    @Shadow private Tag<Block> effectiveBlocks;


    @Inject(method = "postMine", at = @At(value = "TAIL"), cancellable = true)
    private void postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner, CallbackInfoReturnable<Boolean> cir){
        Block block = state.getBlock();
        if (miner instanceof ServerPlayerEntity && effectiveBlocks.contains(block)) {
            int lvl = EnchantmentHelper.getLevel(RegisterEnchantment.INSTANCE.getVEIN_MINER(), stack);
            if (lvl > 0) {
                if (RegisterKeybind.INSTANCE.getVEIN_MINER().isPressed()) {
                    VeinMinerEnchantment.Companion.veinMine(world, pos, block, (ServerPlayerEntity) miner, lvl);
                }
            }
        }
    }
}
