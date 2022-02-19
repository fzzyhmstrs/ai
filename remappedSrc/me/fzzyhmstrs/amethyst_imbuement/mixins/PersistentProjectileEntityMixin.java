package me.fzzyhmstrs.amethyst_imbuement.mixins;


import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.TridentItem;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.annotation.Target;
import java.util.Arrays;

@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileEntityMixin extends Entity {

    private static int i;

    public PersistentProjectileEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    public boolean isNoClip(){
        return false;
    };

    @Shadow protected abstract float getDragInWater();

    @Inject(method= "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;)V",at = @At(value = "TAIL"))
    private void constructor(EntityType<? extends PersistentProjectileEntity> type, LivingEntity owner, World world, CallbackInfo ci) {
        i = EnchantmentHelper.getEquipmentLevel(RegisterEnchantment.INSTANCE.getDEADLY_SHOT(), owner);
    }

    @Inject(method="tick",at = @At(value = "TAIL"))
    private void tick(CallbackInfo ci){
        if(i > 0) {
            Vec3d vec3d = this.getVelocity();
            if (this.isTouchingWater()) {
                float j1 = (this.getDragInWater() + (0.05f * i)) / this.getDragInWater(); //figure out the factor to fix the velocity by
                this.setVelocity(vec3d.multiply(j1)); //reset the velocities with the correct values
            } else{
                float j2 = (0.99f + (0.003f * i)) / 0.99f;
                this.setVelocity(vec3d.multiply(j2));
            }
            if (!this.hasNoGravity() && !this.isNoClip()) {
                Vec3d l = this.getVelocity();
                this.setVelocity(l.x, l.y - (double) (0.05f-(0.01f*i)), l.z);
            }
        }
    }

}
