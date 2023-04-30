package me.fzzyhmstrs.amethyst_imbuement.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.fzzyhmstrs.amethyst_imbuement.item.promise.GemOfPromiseItem;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterTag;
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
import net.minecraft.item.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;



@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    @Shadow @Final private PlayerInventory inventory;

    private DamageSource damageSource;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method="isUsingSpyglass", at = @At(value = "HEAD"), cancellable = true)
    private void amethyst_imbuement_isUsingSpyglass(CallbackInfoReturnable<Boolean> cir){
        if(super.getActiveItem().isOf(RegisterItem.INSTANCE.getSNIPER_BOW())){
            if (CrossbowItem.isCharged(super.getActiveItem())) {
                if (EnchantmentHelper.getLevel(RegisterEnchantment.INSTANCE.getILLUMINATING(),super.getActiveItem()) > 0){
                    this.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION,260));
                }
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "dropInventory", at = @At(value = "INVOKE", target = "net/minecraft/entity/player/PlayerInventory.dropAll ()V"), cancellable = true)
        private void amethyst_imbuement_checkForSoulbinding(CallbackInfo ci){
            if (this.hasStatusEffect(RegisterStatus.INSTANCE.getSOULBINDING())){
                ci.cancel();
            }
        }

    @Inject(method = "damage", at = @At(value = "HEAD"))
    private void amethyst_imbuement_damageMixin(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir){
        damageSource = source;
        if (!(this.timeUntilRegen > 10)) {
            ItemStack stack2 = inventory.getStack(PlayerInventory.OFF_HAND_SLOT);
            if (stack2.isOf(RegisterItem.INSTANCE.getGEM_OF_PROMISE())) {
                RegisterItem.INSTANCE.getSPARKING_GEM().sparkingGemCheck(stack2, inventory, source);
                RegisterItem.INSTANCE.getBLAZING_GEM().blazingGemCheck(stack2, inventory, this, source);
                RegisterItem.INSTANCE.getBRUTAL_GEM().brutalGemCheck(stack2, inventory, source);
            }
        }
    }

    @Inject(method = "damageShield", at = @At(value = "HEAD"))
    private void amethyst_imbuement_checkShieldEnchants(float amount, CallbackInfo ci){
        ItemStack activeStack = this.activeItemStack;
        if (activeStack.getItem() instanceof ShieldItem){
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


    @WrapOperation(method = "damageShield", at = @At(value = "INVOKE", target = "net/minecraft/item/ItemStack.isOf (Lnet/minecraft/item/Item;)Z"))
    private boolean amethyst_imbuement_damageWards(ItemStack instance, Item item, Operation<Boolean> operation){
        return operation.call(instance,item) || instance.isIn(RegisterTag.INSTANCE.getBASIC_WARDS_TAG()) || instance.isOf(RegisterItem.INSTANCE.getIMBUED_WARD());
    }

    @Inject(method = "onKilledOther", at = @At(value = "HEAD"))
    private void amethyst_imbuement_checkForPromiseGemKill(ServerWorld world, LivingEntity other, CallbackInfoReturnable<Boolean> cir){
        ItemStack stack = inventory.getStack(PlayerInventory.OFF_HAND_SLOT);
        if (stack.getItem() instanceof GemOfPromiseItem){
            RegisterItem.INSTANCE.getLETHAL_GEM().lethalGemCheck(stack,inventory);
        }
    }
}
