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
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow public abstract boolean equals(Object o);

    @Shadow public float stepHeight;

    @Unique
    private float things$prevStepHeight = -1;

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", at = @At("HEAD"))
    private void amethyst_imbuement_boostStepHeight(Vec3d movement, CallbackInfoReturnable<Vec3d> cir) {
        if (!((Object) this instanceof PlayerEntity player)) return;
        if (checkPlayerStriding(player) > 0){
            if (this.stepHeight < 1.0F) {
                this.things$prevStepHeight = this.stepHeight;
                this.stepHeight = this.stepHeight + 0.5f;
            }
        }
    }

    @Inject(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", at = @At("RETURN"))
    private void amethyst_imbuement_resetStepHeight(Vec3d movement, CallbackInfoReturnable<Vec3d> cir) {
        if (this.things$prevStepHeight == -1) return;
        this.stepHeight = this.things$prevStepHeight;
        this.things$prevStepHeight = -1;
    }

    private int checkPlayerStriding(PlayerEntity player){
        return EnchantmentHelper.getLevel(RegisterEnchantment.INSTANCE.getSTRIDING(),player.getEquippedStack(EquipmentSlot.FEET));
    }
}
