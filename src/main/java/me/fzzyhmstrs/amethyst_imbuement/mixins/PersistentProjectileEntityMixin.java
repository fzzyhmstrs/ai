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
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
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

    @Redirect(method = "onEntityHit", at = @At(value = "INVOKE", target = "net/minecraft/entity/Entity.damage (Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private boolean checkHeadHunter(Entity instance, DamageSource source, float amount){
        PersistentProjectileEntity chk = (PersistentProjectileEntity) (Object) this;
        Entity owner = chk.getOwner();
        if (owner != null) {
            if (owner instanceof LivingEntity){
                ItemStack chk2 = ((LivingEntity) owner).getStackInHand(Hand.MAIN_HAND);
                int lvl = EnchantmentHelper.getLevel(RegisterEnchantment.INSTANCE.getHEADHUNTER(), chk2);
                if (lvl > 0){
                    double y1 = this.getY();
                    double y2 = instance.getEyeY();
                    double xdiff = this.getX() - instance.getX();
                    double zdiff = this.getZ() - instance.getZ();
                    double sqrd = xdiff * xdiff + zdiff * zdiff;
                    if (sqrd < 0.0) sqrd = 0.0;
                    double d = Math.sqrt(sqrd);
                    double y1Fix;
                    if (Math.abs(this.getPitch()) != 90.0) {
                        y1Fix = y1 + d * Math.tan(this.getPitch() * 0.01745329);;
                    } else if (this.getPitch() == 90.0) {
                        return instance.damage(source,amount);
                    } else if (this.getPitch() == -90.0){
                        return instance.damage(source,amount*2.0f);
                    } else {
                        y1Fix = y1;
                    }


                    if (Math.abs(y1-y2) < 0.03){
                        amount = amount *4.2F;
                    } else if (Math.abs(y1Fix-y2) < 0.14){
                        float rndMultiplier = (instance.world.random.nextFloat() * 0.7f + 0.6f);
                        amount = amount * (1.0F + rndMultiplier);
                    }
                }
            }
        }
        return instance.damage(source,amount);
    }
}
