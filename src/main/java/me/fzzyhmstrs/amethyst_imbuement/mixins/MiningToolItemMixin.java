package me.fzzyhmstrs.amethyst_imbuement.mixins;

import me.fzzyhmstrs.amethyst_imbuement.enchantment.VeinMinerEnchantment;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.OperatorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
                VeinMinerEnchantment.Companion.veinMine(world,pos,block, (ServerPlayerEntity) miner,lvl);
            }
        }
    }
}
