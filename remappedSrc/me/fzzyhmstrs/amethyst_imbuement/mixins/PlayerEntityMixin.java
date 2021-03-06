package me.fzzyhmstrs.amethyst_imbuement.mixins;

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;



@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    private DamageSource damageSource;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method="isUsingSpyglass", at = @At(value = "HEAD"), cancellable = true)
    private void isUsingSpyglass(CallbackInfoReturnable<Boolean> cir){
        if(super.getActiveItem().isOf(RegisterItem.INSTANCE.getSNIPER_BOW())){
            if (CrossbowItem.isCharged(super.getActiveItem())) {
                if (EnchantmentHelper.getLevel(RegisterEnchantment.INSTANCE.getILLUMINATING(),super.getActiveItem()) > 0){
                    this.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION,260));
                }
                cir.setReturnValue(true);

            }
        }
    }

    /*@Redirect(method = "dropInventory", at = @At(value = "INVOKE", target = "net/minecraft/entity/player/PlayerInventory.dropAll ()V"))
    private void checkForSoulbinding(PlayerInventory instance){
        if (!this.hasStatusEffect(RegisterStatus.INSTANCE.getSOULBINDING())){
            instance.dropAll();
        }
    }*/

    @Inject(method = "dropInventory", at = @At(value = "INVOKE", target = "net/minecraft/entity/player/PlayerInventory.dropAll ()V"), cancellable = true)
    private void checkForSoulbinding(CallbackInfo ci){
        if (this.hasStatusEffect(RegisterStatus.INSTANCE.getSOULBINDING())){
            ci.cancel();
        }
    }

    @Inject(method = "damage", at = @At(value = "HEAD"))
    private void getDamageSourceForShield(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir){
        damageSource = source;
    }

    @Inject(method = "damageShield", at = @At(value = "HEAD"))
    private void checkShieldEnchants(float amount, CallbackInfo ci){
        ItemStack activeStack = this.activeItemStack;
        if (activeStack.isOf(Items.SHIELD)){
            int level = EnchantmentHelper.getLevel(RegisterEnchantment.INSTANCE.getSPIKED(),activeStack);
            if (level > 0){
                Entity source = damageSource.getSource();
                if (source != null) {
                    if (source instanceof LivingEntity) {
                        RegisterEnchantment.INSTANCE.getSPIKED().specialEffect((LivingEntity) source, level, activeStack);
                    } else if (source instanceof ProjectileEntity){
                        Entity owner = ((ProjectileEntity) source).getOwner();
                        if (owner != null){
                            RegisterEnchantment.INSTANCE.getSPIKED().specialEffect((LivingEntity) owner, level, activeStack);
                        }
                    }
                }
            }
            level = EnchantmentHelper.getLevel(RegisterEnchantment.INSTANCE.getBULWARK(),activeStack);
            if (level > 0){
                Entity source = damageSource.getSource();
                if (source != null) {
                    if (source instanceof LivingEntity) {
                        RegisterEnchantment.INSTANCE.getBULWARK().specialEffect(this, level, activeStack);
                    }
                }
            }
        }
    }
}
