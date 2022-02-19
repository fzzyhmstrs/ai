package me.fzzyhmstrs.amethyst_imbuement.mixins;

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FarmlandBlock.class)
public abstract class FarmlandBlockMixin extends Block {

    public FarmlandBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "onLandedUpon", at = @At(value = "HEAD"), cancellable = true)
    private void onLightfootedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance, CallbackInfo ci){
        if (entity instanceof LivingEntity){
            ItemStack stack = ((LivingEntity) entity).getEquippedStack(EquipmentSlot.FEET);
            if (EnchantmentHelper.getLevel(RegisterEnchantment.INSTANCE.getLIGHTFOOTED(), stack) > 0){
                super.onLandedUpon(world,state,pos,entity,fallDistance);
                ci.cancel();
            }
        }
    }

}
