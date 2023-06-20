package me.fzzyhmstrs.amethyst_imbuement.mixins;

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockMixin {


    @Inject(method = "onLandedUpon", at = @At(value = "HEAD"), cancellable = true)
    private void amethyst_imbuement_onSlimedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance, CallbackInfo ci){
        if (!state.isOf(Blocks.SLIME_BLOCK)) {
            if ((entity instanceof PlayerEntity playerEntity)) {
                if (!playerEntity.getEquippedStack(EquipmentSlot.FEET).isEmpty()) {
                    if (EnchantmentHelper.getLevel(RegisterEnchantment.INSTANCE.getSLIMY(), playerEntity.getEquippedStack(EquipmentSlot.FEET)) > 0 && RegisterEnchantment.INSTANCE.getSLIMY().isEnabled()) {
                        Block slimeBlock = Blocks.SLIME_BLOCK;
                        slimeBlock.onLandedUpon(world, slimeBlock.getDefaultState(), pos, entity, fallDistance);
                        ci.cancel();
                    }
                }
            }
        }
    }

    @Inject(method = "onEntityLand", at = @At(value = "HEAD"), cancellable = true)
    private void amethyst_imbuement_slimyBounce(BlockView world, Entity entity, CallbackInfo ci){
        if ((entity instanceof PlayerEntity playerEntity)) {
            if (!playerEntity.getEquippedStack(EquipmentSlot.FEET).isEmpty()) {

                if (EnchantmentHelper.getLevel(RegisterEnchantment.INSTANCE.getMULTI_JUMP(), playerEntity.getEquippedStack(EquipmentSlot.FEET)) > 0) {
                    ((PlayerEntity) entity).removeStatusEffect(RegisterStatus.INSTANCE.getLEAPT());
                }

                if (EnchantmentHelper.getLevel(RegisterEnchantment.INSTANCE.getSLIMY(), playerEntity.getEquippedStack(EquipmentSlot.FEET)) > 0 && RegisterEnchantment.INSTANCE.getSLIMY().isEnabled()) {
                    if (!entity.bypassesLandingEffects()) {
                        bounce(entity);
                        ci.cancel();
                    }

                }
            }
        }
    }

    private void bounce(Entity entity) {
        Vec3d vec3d = entity.getVelocity();
        if (vec3d.y < -0.45) {
            double d = entity instanceof LivingEntity ? 1.0 : 0.8;
            entity.setVelocity(vec3d.x, -vec3d.y * d, vec3d.z);
            entity.getWorld().playSound(null, entity.getBlockPos(), SoundEvents.BLOCK_SLIME_BLOCK_BREAK, SoundCategory.BLOCKS, 0.5f, 1.0f);
        } else if(vec3d.y <-.25){
            entity.setVelocity(vec3d.x, 0.0, vec3d.z);
            entity.getWorld().playSound(null, entity.getBlockPos(), SoundEvents.BLOCK_SLIME_BLOCK_BREAK, SoundCategory.BLOCKS, 0.25f, 1.0f);
        }else {
            entity.setVelocity(vec3d.x, 0.0, vec3d.z);
        }
    }

}
