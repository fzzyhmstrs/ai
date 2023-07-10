package me.fzzyhmstrs.amethyst_imbuement.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext;
import me.fzzyhmstrs.amethyst_core.entity.ModifiableEffect;
import me.fzzyhmstrs.amethyst_core.entity.ModifiableEffectContainer;
import me.fzzyhmstrs.amethyst_core.entity.ModifiableEffectEntity;
import me.fzzyhmstrs.amethyst_imbuement.augment.ShieldingAugment;
import me.fzzyhmstrs.amethyst_imbuement.interfaces.ModifiableEffectMobOrPlayer;
import me.fzzyhmstrs.amethyst_imbuement.item.promise.GemOfPromiseItem;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus;
import me.fzzyhmstrs.fzzy_core.registry.EventRegistry;
import me.fzzyhmstrs.fzzy_core.trinket_util.base_augments.AbstractEquipmentAugment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Map;

@SuppressWarnings("ConstantConditions")
@Mixin(value = LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements ModifiableEffectMobOrPlayer {


    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Unique
    private long lastTime = 0;
    @Unique
    private DamageSource damageSource;

    @Shadow public abstract int getArmor();
    @Shadow public abstract Iterable<ItemStack> getArmorItems();
    @Shadow public abstract ItemStack getStackInHand(Hand hand);
    @Shadow public abstract boolean addStatusEffect(StatusEffectInstance effect);
    @Shadow public abstract void setHealth(float v);

    @Shadow @Final
    private static TrackedData<Float> HEALTH;

    @Shadow public abstract boolean hasStatusEffect(StatusEffect effect);

    @Shadow protected ItemStack activeItemStack;

    @Shadow public abstract float getHealth();

    @Shadow public abstract float getMaxHealth();

    @Shadow public abstract boolean removeStatusEffect(StatusEffect type);

    @Unique
    final private ProcessContext processContext = ProcessContext.Companion.getEMPTY_CONTEXT();
    @Unique
    ModifiableEffectContainer modifiableEffectContainer = new ModifiableEffectContainer();

    @Override
    public void amethyst_imbuement_addTemporaryEffect(Identifier type, ModifiableEffect effect, int lifespan) {
        modifiableEffectContainer.addTemporary(type, effect, lifespan);
    }

    @Override
    public void amethyst_imbuement_run(Identifier type, Entity entity, @Nullable Entity owner, ProcessContext processContext){
        modifiableEffectContainer.run(type, entity, owner, processContext);
    }

    @Inject(method = "onAttacking", at = @At("HEAD"))
    public void amethyst_imbuement_onPlayerAttackWhilstStunnedTarget(Entity target, CallbackInfo ci) {
        modifiableEffectContainer.run(ModifiableEffectEntity.Companion.getDAMAGE(), this, null, processContext);
    }

    //credit for this mixin (C) Timefall Development, Chronos Sacaria, Kluzzio
    @Inject(method = "swingHand(Lnet/minecraft/util/Hand;)V", at = @At("HEAD"), cancellable = true)
    public void onAttackWhilstStunnedNoTarget(Hand hand, CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;

        if (livingEntity.hasStatusEffect(RegisterStatus.INSTANCE.getSTUNNED())) {
            ci.cancel();
        }
    }

    @WrapOperation(method = "getArmorVisibility", at = @At(value = "INVOKE", target = "net/minecraft/item/ItemStack.isEmpty ()Z"))
    private boolean amethyst_imbuement_checkArmorInvisibility(ItemStack instance, Operation<Boolean> operation){
        return (operation.call(instance) || EnchantmentHelper.getLevel(RegisterEnchantment.INSTANCE.getINVISIBILITY(), instance) > 0);
    }

    @Inject(method = "canHaveStatusEffect", at = @At(value = "HEAD"), cancellable = true)
    private void amethyst_imbuement_canHaveWithImmunity(StatusEffectInstance effect, CallbackInfoReturnable<Boolean> cir){
        if (effect.getEffectType() != RegisterStatus.INSTANCE.getIMMUNITY()) {
            if (this.hasStatusEffect(RegisterStatus.INSTANCE.getIMMUNITY()) || RegisterEnchantment.INSTANCE.getIMMUNITY().checkAndDamageTrinket((LivingEntity) (Object) this, effect)) {
                if (!effect.getEffectType().isBeneficial()) {
                    cir.setReturnValue(false);
                }
            }
        }
    }

    @Inject(method = "heal", at = @At(value = "HEAD"))
    private void amethyst_imbuement_healMixin(float amount, CallbackInfo ci){
        if ((LivingEntity)(Object)this instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)(Object)this;
            PlayerInventory inventory = player.getInventory();
            float newHealth = this.getHealth() + amount;
            float clampedDelta = newHealth - MathHelper.clamp(newHealth,0.0f,this.getMaxHealth());
            float healed = amount - clampedDelta;
            ItemStack stack2 = inventory.getStack(PlayerInventory.OFF_HAND_SLOT);
            if (stack2.getItem() instanceof GemOfPromiseItem) {
                RegisterItem.INSTANCE.getHEALERS_GEM().healersGemCheck(stack2, inventory, healed);
            }
        }
    }

    @Inject(method = "addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;Lnet/minecraft/entity/Entity;)Z", at = @At(value = "HEAD"))
    private void amethyst_imbuement_addStatusEffectMixin(StatusEffectInstance effect, Entity source, CallbackInfoReturnable<Boolean> cir){
        if ((LivingEntity)(Object)this instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)(Object)this;
            PlayerInventory inventory = player.getInventory();
            ItemStack stack2 = inventory.getStack(PlayerInventory.OFF_HAND_SLOT);
            if (stack2.getItem() instanceof GemOfPromiseItem) {
                RegisterItem.INSTANCE.getINQUISITIVE_GEM().inquisitiveGemCheck(stack2,inventory,effect.getEffectType());
            }
        }
    }

    @Inject(method = "damage", at = @At(value = "HEAD"), cancellable = true)
    private void amethyst_imbuement_getDamageSourceForShield(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir){
        if (ShieldingAugment.ShieldingObject.damageIsBlocked(this.getWorld().random, (LivingEntity) (Object) this, source)){
            this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 0.4f, 0.9f + this.getWorld().random.nextFloat() * 0.4f);
            cir.setReturnValue(false);
        }
        if (((LivingEntity)(Object)this) instanceof MobEntity){
            modifiableEffectContainer.run(ModifiableEffectEntity.Companion.getON_DAMAGED(), this, null, processContext);
        }
        damageSource = source;
    }

    @Inject(method = "setHealth", at = @At(value = "HEAD"), cancellable = true)
    private void amethyst_imbuement_setHealthMixin(float health, CallbackInfo ci){
        if (this.hasStatusEffect(RegisterStatus.INSTANCE.getINSPIRED())){
            if (health <= 0.0f){
                this.removeStatusEffect(RegisterStatus.INSTANCE.getINSPIRED());
                this.dataTracker.set(HEALTH, 0.5f);
                ci.cancel();
            }
        }
    }

    @Inject(method = "damageShield", at = @At(value = "HEAD"))
    private void amethyst_imbuement_checkShieldEnchants(float amount, CallbackInfo ci){
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
                RegisterEnchantment.INSTANCE.getBULWARK().specialEffect((LivingEntity)(Object)this, level, activeStack);
            }
        }
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void amethyst_imbuement_augmentStatusCheck(CallbackInfo ci){
        if (!this.getWorld().isClient) {

            //armor effects a little less often because more intensive
            if (EventRegistry.INSTANCE.getTicker_20().isReady()) {
                Iterable<ItemStack> iterable = this.getArmorItems();
                for (ItemStack stack : iterable) {
                    if (stack.isEmpty()) continue;
                    if (!stack.hasEnchantments()) continue;
                    Map<Enchantment, Integer> map = EnchantmentHelper.get(stack);
                    for (Enchantment enchant : map.keySet()){
                        if (enchant instanceof AbstractEquipmentAugment) {
                            ((AbstractEquipmentAugment) enchant).equipmentEffect((LivingEntity) (Object) this, map.get(enchant), stack);
                        }
                    }
                }
            }
        }
        if (((LivingEntity)(Object)this) instanceof MobEntity){
            modifiableEffectContainer.run(ModifiableEffectEntity.Companion.getTICK(), this, null, processContext);
        }
    }

    @WrapOperation(method = "tickMovement", at = @At(value = "INVOKE", target = "net/minecraft/entity/LivingEntity.isOnGround ()Z"))
    private boolean amethyst_imbuement_checkForMultiJump(LivingEntity instance, Operation<Boolean> operation){
        long time = instance.getWorld().getTime();
        if (operation.call(instance)) {
            instance.removeStatusEffect(RegisterStatus.INSTANCE.getLEAPT());
            lastTime = time;
            return true; //don't need to multi-jump if the player is already on the ground
        }
        if (!(instance instanceof PlayerEntity)) return operation.call(instance);
        if (!instance.getWorld().isClient) return operation.call(instance);
        ItemStack footStack = instance.getEquippedStack(EquipmentSlot.FEET);
        if (footStack.isEmpty()) return operation.call(instance);
        if (EnchantmentHelper.getLevel(RegisterEnchantment.INSTANCE.getMULTI_JUMP(),footStack) > 0){
            if(RegisterEnchantment.INSTANCE.getMULTI_JUMP().isEnabled()) {
                if (!(instance.hasStatusEffect(RegisterStatus.INSTANCE.getLEAPT())) && ((time - lastTime) > 5)) {
                    instance.addStatusEffect(new StatusEffectInstance(RegisterStatus.INSTANCE.getLEAPT(), 1200));
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @WrapOperation(method = "tryUseTotem", at = @At(value = "INVOKE", target = "net/minecraft/item/ItemStack.isOf (Lnet/minecraft/item/Item;)Z"))
    private boolean amethyst_imbuement_tryUseTotemCheck(ItemStack instance, Item item, Operation<Boolean> operation){
        if (!instance.isOf(RegisterItem.INSTANCE.getTOTEM_OF_AMETHYST())) return operation.call(instance, item);
        if (EnchantmentHelper.getLevel(RegisterEnchantment.INSTANCE.getUNDYING(),instance) == 0) return operation.call(instance, item);
        if (!RegisterEnchantment.INSTANCE.getUNDYING().specialEffect((LivingEntity)(Object)this,1,instance)) return operation.call(instance, item);
        return true;
    }

    @WrapOperation(method = "tryUseTotem", at = @At(value = "INVOKE", target = "net/minecraft/item/ItemStack.decrement (I)V"))
    private void amethyst_imbuement_tryUseTotemNoDecrement(ItemStack instance, int i, Operation<Void> operation){
        if (!instance.isOf(RegisterItem.INSTANCE.getTOTEM_OF_AMETHYST())) operation.call(instance, i);
    }

    @Inject(method = "onKilledBy", at = @At("HEAD"))
    private void amethyst_imbuement_fireKillEffectsOnKiller(LivingEntity adversary, CallbackInfo ci){
        if (adversary instanceof ModifiableEffectMobOrPlayer && !(adversary instanceof PlayerEntity)){
            ((ModifiableEffectMobOrPlayer) adversary).amethyst_imbuement_run(ModifiableEffectEntity.Companion.getKILL(), adversary,null, processContext);
        }
    }
}
