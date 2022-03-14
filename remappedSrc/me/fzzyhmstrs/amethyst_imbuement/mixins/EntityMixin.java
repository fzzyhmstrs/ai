package me.fzzyhmstrs.amethyst_imbuement.mixins;

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    /*private boolean hasStriding;
    @Shadow public float stepHeight;*/

    @Shadow public abstract boolean equals(Object o);

    @Redirect(method = "fall", at = @At(value = "INVOKE", target = "net/minecraft/block/Block.onLandedUpon (Lnet/minecraft/world/World;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;F)V"))
    private void onSlimedUpon(Block instance, World world, BlockState state, BlockPos pos, Entity entity, float fallDistance){
        if (!(entity instanceof PlayerEntity playerEntity)){
            instance.onLandedUpon(world, state, pos, entity, fallDistance);
            return;
        }
        if (playerEntity.getEquippedStack(EquipmentSlot.FEET).isEmpty()){
            instance.onLandedUpon(world, state, pos, entity, fallDistance);
            return;
        }
        if (EnchantmentHelper.getLevel(RegisterEnchantment.INSTANCE.getSLIMY(),playerEntity.getEquippedStack(EquipmentSlot.FEET)) > 0){
            Block slimeBlock = Blocks.SLIME_BLOCK;
            slimeBlock.onLandedUpon(world, state, pos, entity, fallDistance);
            entity.world.playSound(null,entity.getBlockPos(), SoundEvents.BLOCK_SLIME_BLOCK_BREAK, SoundCategory.BLOCKS,0.6f,1.0f);
        } else {
            instance.onLandedUpon(world, state, pos, entity, fallDistance);
        }
    }

    @Redirect(method = "move", at = @At(value = "INVOKE", target = "net/minecraft/block/Block.onEntityLand (Lnet/minecraft/world/BlockView;Lnet/minecraft/entity/Entity;)V"))
    private void slimyBounce(Block instance, BlockView world, Entity entity){
        if (!(entity instanceof PlayerEntity playerEntity)){
            instance.onEntityLand(world,entity);
            return;
        }
        if (playerEntity.getEquippedStack(EquipmentSlot.FEET).isEmpty()){
            instance.onEntityLand(world,entity);
            return;
        }
        if (EnchantmentHelper.getLevel(RegisterEnchantment.INSTANCE.getMULTI_JUMP(),playerEntity.getEquippedStack(EquipmentSlot.FEET)) > 0){
            ((PlayerEntity) entity).removeStatusEffect(RegisterStatus.INSTANCE.getLEAPT());
        }

        if (EnchantmentHelper.getLevel(RegisterEnchantment.INSTANCE.getSLIMY(),playerEntity.getEquippedStack(EquipmentSlot.FEET)) > 0){
            /*Block slimeBlock = Blocks.SLIME_BLOCK;
            slimeBlock.onEntityLand(world,entity);*/
            if (entity.bypassesLandingEffects()){
                instance.onEntityLand(world,entity);
            } else {
                bounce(entity);
            }

        } else {
            instance.onEntityLand(world,entity);
        }

    }

    @Shadow public float stepHeight;

    @Unique
    private float things$prevStepHeight = -1;

    @Inject(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", at = @At("HEAD"))
    private void boostStepHeight(Vec3d movement, CallbackInfoReturnable<Vec3d> cir) {
        if (!((Object) this instanceof PlayerEntity player)) return;
        if (checkPlayerStriding(player) > 0){
            if (this.stepHeight < 1.0F) {
                this.things$prevStepHeight = this.stepHeight;
                this.stepHeight = this.stepHeight + 0.5f;
            }
        }
    }

    @Inject(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", at = @At("RETURN"))
    private void resetStepHeight(Vec3d movement, CallbackInfoReturnable<Vec3d> cir) {
        if (this.things$prevStepHeight == -1) return;
        this.stepHeight = this.things$prevStepHeight;
        this.things$prevStepHeight = -1;
    }

    private int checkPlayerStriding(PlayerEntity player){
        return EnchantmentHelper.getLevel(RegisterEnchantment.INSTANCE.getSTRIDING(),player.getEquippedStack(EquipmentSlot.FEET));
    }

    private void bounce(Entity entity) {
        Vec3d vec3d = entity.getVelocity();
        if (vec3d.y < -0.4) {
            double d = entity instanceof LivingEntity ? 1.0 : 0.8;
            entity.setVelocity(vec3d.x, -vec3d.y * d, vec3d.z);
        } else {
            entity.setVelocity(vec3d.x, 0.0, vec3d.z);
        }
    }

    /*@Inject(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", at = @At(value = "HEAD"))
    private void checkStepHeight(Vec3d movement, CallbackInfoReturnable<Vec3d> cir){
        hasStriding =
        System.out.println("step height is: " + this.stepHeight);
    }

    @Redirect(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", at = @At(value = "FIELD", target = "net/minecraft/entity/Entity.stepHeight : F"))
    private float modifyStepHeight*/
}
