package me.fzzyhmstrs.amethyst_imbuement.mixins;

import me.fzzyhmstrs.amethyst_imbuement.enchantment.VeinMinerEnchantment;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterKeybindServer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MiningToolItem.class)
public class MiningToolItemMixin {

    @Final
    @Shadow private TagKey<Block> effectiveBlocks;


    @Inject(method = "postMine", at = @At(value = "TAIL"))
    private void postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner, CallbackInfoReturnable<Boolean> cir){
        System.out.println("blup");
        if (miner instanceof ServerPlayerEntity && state.isIn(effectiveBlocks)) {
            int lvl = EnchantmentHelper.getLevel(RegisterEnchantment.INSTANCE.getVEIN_MINER(), stack);
            if (lvl > 0) {
                System.out.println("bloop");
                if (RegisterKeybindServer.INSTANCE.checkForVeinMine(miner.getUuid())) {
                    System.out.println("bleep");
                    Block block = state.getBlock();
                    VeinMinerEnchantment.Companion.veinMine(world, pos, block, (ServerPlayerEntity) miner, lvl);
                }
            }
        }
    }
}
