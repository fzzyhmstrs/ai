package me.fzzyhmstrs.amethyst_imbuement.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.fzzyhmstrs.amethyst_imbuement.item.promise.GemOfPromiseItem;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterTool;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.util.Hand;
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

import java.util.Map;

@SuppressWarnings("ConstantConditions")
@Mixin(value = LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {


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

    @WrapOperation(method = "applyDamage", at = @At(value = "INVOKE", target = "net/minecraft/entity/LivingEntity.applyArmorToDamage (Lnet/minecraft/entity/damage/DamageSource;F)F"))
    private float amethyst_core_applyMultiplicationAttributeToDamage(LivingEntity instance, DamageSource source, float amount, Operation<Float> operation){
        float newAmount = instance.hasStatusEffect(RegisterStatus.INSTANCE.getRESONATING())
                ?
                amount + 1f + instance.getStatusEffect(RegisterStatus.INSTANCE.getRESONATING()).getAmplifier()
                :
                amount;
        float newAmount2 = instance.hasStatusEffect(RegisterStatus.INSTANCE.getBLAST_RESISTANT()) && source.isIn(DamageTypeTags.IS_EXPLOSION)
                ?
                newAmount * Math.max(0f,(9f - (float)( instance.getStatusEffect(RegisterStatus.INSTANCE.getRESONATING()).getAmplifier())) / 10f)
                :
                newAmount;
        return operation.call(instance,source,newAmount2);
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

    @Inject(method = "damage", at = @At(value = "HEAD"))
    private void amethyst_imbuement_getDamageSourceForShield(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir){
        /*if (ShieldingAugment.ShieldingObject.damageIsBlocked(this.getWorld().random, (LivingEntity) (Object) this, source)){
            this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 0.4f, 0.9f + this.getWorld().random.nextFloat() * 0.4f);
            cir.setReturnValue(false);
        }*/
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
        if (!instance.isOf(RegisterTool.INSTANCE.getTOTEM_OF_AMETHYST())) return operation.call(instance, item);
        if (EnchantmentHelper.getLevel(RegisterEnchantment.INSTANCE.getUNDYING(),instance) == 0) return operation.call(instance, item);
        if (!RegisterEnchantment.INSTANCE.getUNDYING().specialEffect((LivingEntity)(Object)this,1,instance)) return operation.call(instance, item);
        return true;
    }

    @WrapOperation(method = "tryUseTotem", at = @At(value = "INVOKE", target = "net/minecraft/item/ItemStack.decrement (I)V"))
    private void amethyst_imbuement_tryUseTotemNoDecrement(ItemStack instance, int i, Operation<Void> operation){
        if (!instance.isOf(RegisterTool.INSTANCE.getTOTEM_OF_AMETHYST())) operation.call(instance, i);
    }

    /*@WrapOperation(method = "getEquipmentChanges", at = @At(value = "INVOKE", target = "net/minecraft/item/ItemStack.getAttributeModifiers (Lnet/minecraft/entity/EquipmentSlot;)Lcom/google/common/collect/Multimap;"))
    private Multimap<EntityAttribute, EntityAttributeModifier> amethyst_imbuement_getEquipmentChangesMixin(ItemStack instance, EquipmentSlot slot, Operation<Multimap<EntityAttribute, EntityAttributeModifier>> operation){
        int stackValAdd = RegisterEnchantment.INSTANCE.getRESILIENCE().isEnabled() ? EnchantmentHelper.getLevel(RegisterEnchantment.INSTANCE.getRESILIENCE(), instance) : 0;
        int stackValAdd2 = RegisterEnchantment.INSTANCE.getSTEADFAST().isEnabled() ? EnchantmentHelper.getLevel(RegisterEnchantment.INSTANCE.getSTEADFAST(), instance) : 0;
        if (stackValAdd == 0 && stackValAdd2 == 0){
            return operation.call(instance, slot);
        } else {
            return provideResilientMultimap(instance, slot, stackValAdd, stackValAdd2);
        }
    }


    private Multimap<EntityAttribute, EntityAttributeModifier> provideResilientMultimap(ItemStack instance, EquipmentSlot slot, int stackValAdd, int stackValAdd2){
        Multimap<EntityAttribute, EntityAttributeModifier> map = instance.getAttributeModifiers(slot);
        Multimap<EntityAttribute, EntityAttributeModifier> map2 = HashMultimap.create();
        for (EntityAttribute key : map.keys()){
            if (key == EntityAttributes.GENERIC_ARMOR){
                if (stackValAdd == 0){
                    map2.putAll(key,map.get(key));
                    continue;
                }
                Optional<EntityAttributeModifier> emo = map.get(EntityAttributes.GENERIC_ARMOR).stream().findFirst();
                if (emo.isPresent()) {
                    EntityAttributeModifier em = emo.get();
                    double armor = em.getValue();
                    UUID uuid = em.getId();
                    String name = em.getName();
                    EntityAttributeModifier.Operation operation = em.getOperation();
                    EntityAttributeModifier em2 = new EntityAttributeModifier(uuid,name,armor + stackValAdd,operation);
                    map2.put(EntityAttributes.GENERIC_ARMOR,em2);
                }
            } else if (key == EntityAttributes.GENERIC_ARMOR_TOUGHNESS){
                if (stackValAdd == 0){
                    map2.putAll(key,map.get(key));
                    continue;
                }
                Optional<EntityAttributeModifier> emo = map.get(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).stream().findFirst();
                if (emo.isPresent()) {
                    EntityAttributeModifier em = emo.get();
                    double tough = em.getValue();
                    UUID uuid = em.getId();
                    String name = em.getName();
                    EntityAttributeModifier.Operation operation = em.getOperation();
                    EntityAttributeModifier em2 = new EntityAttributeModifier(uuid,name,tough + stackValAdd/2.0,operation);
                    map2.put(EntityAttributes.GENERIC_ARMOR_TOUGHNESS,em2);
                }
            }else if (key == EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE){
                if (stackValAdd2 == 0){
                    map2.putAll(key,map.get(key));
                    continue;
                }
                Optional<EntityAttributeModifier> emo = map.get(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE).stream().findFirst();
                if (emo.isPresent()) {
                    EntityAttributeModifier em = emo.get();
                    double tough = em.getValue();
                    UUID uuid = em.getId();
                    String name = em.getName();
                    EntityAttributeModifier.Operation operation = em.getOperation();
                    EntityAttributeModifier em2 = new EntityAttributeModifier(uuid,name,tough + stackValAdd2 * 0.05,operation);
                    map2.put(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE,em2);
                }
            } else {
                map2.putAll(key,map.get(key));
            }
        }
        return map2;
    }*/


}
