package me.fzzyhmstrs.amethyst_imbuement.mixins;


import me.fzzyhmstrs.amethyst_imbuement.augment.HeadhunterAugment;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
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

@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileEntityMixin extends Entity {

    private static int i;
    private Entity headhunterEntity = null;

    public PersistentProjectileEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    public boolean isNoClip(){
        return false;
    }

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

    @Inject(method = "onEntityHit", at = @At(value = "HEAD"))
    private void getEntityForHeadhunter(EntityHitResult entityHitResult, CallbackInfo ci){
        headhunterEntity = entityHitResult.getEntity();
    }

    @Redirect(method = "onEntityHit", at = @At(value = "INVOKE", target = "net/minecraft/util/math/MathHelper.ceil (D)I"))
    private int checkHeadhunter(double value){
        int amount = MathHelper.ceil(value);
        //System.out.println(amount);
        PersistentProjectileEntity chk = (PersistentProjectileEntity) (Object) this;
        Entity owner = chk.getOwner();
        if (owner != null) {
            if (owner instanceof LivingEntity) {
                ItemStack chk2 = ((LivingEntity) owner).getStackInHand(Hand.MAIN_HAND);
                int lvl = EnchantmentHelper.getLevel(RegisterEnchantment.INSTANCE.getHEADHUNTER(), chk2);
                if (lvl > 0 && headhunterEntity != null){
                    amount = round(HeadhunterAugment.Companion.checkHeadhunterHit(headhunterEntity,(PersistentProjectileEntity)(Object) this,amount));
                    headhunterEntity = null;
                    //System.out.println(amount);
                }
            }
        }
        return amount;
    }

    private int round(float value){
        int ceil = MathHelper.ceil(value);
        int floor = MathHelper.floor(value);
        double ceilDiff = Math.abs(ceil-value);
        double floorDiff = Math.abs(floor-value);
        if (ceilDiff >= floorDiff){
            return floor;
        } else {
            return ceil;
        }
    }


    /*@Redirect(method = "onEntityHit", at = @At(value = "INVOKE", target = "net/minecraft/entity/Entity.damage (Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private boolean checkHeadHunter(Entity instance, DamageSource source, float amount){
        PersistentProjectileEntity chk = (PersistentProjectileEntity) (Object) this;
        Entity owner = chk.getOwner();
        if (owner != null) {
            if (owner instanceof LivingEntity){
                ItemStack chk2 = ((LivingEntity) owner).getStackInHand(Hand.MAIN_HAND);
                int lvl = EnchantmentHelper.getLevel(RegisterEnchantment.INSTANCE.getHEADHUNTER(), chk2);
                if (lvl > 0){
                    amount = HeadhunterAugment.Companion.checkHeadhunterHit(instance,(PersistentProjectileEntity)(Object) this,amount);
                }
            }
        }
        return instance.damage(source,amount);
    }*/
}
