package me.fzzyhmstrs.amethyst_imbuement.mixins;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import me.fzzyhmstrs.amethyst_core.registry.EventRegistry;
import me.fzzyhmstrs.amethyst_core.trinket_util.base_augments.AbstractEquipmentAugment;
import me.fzzyhmstrs.amethyst_imbuement.AI;
import me.fzzyhmstrs.amethyst_imbuement.augment.ShieldingAugment;
import me.fzzyhmstrs.amethyst_imbuement.item.GemOfPromiseItem;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;


@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {


    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    private long lastTime = 0;
    private DamageSource damageSource;

    @Shadow protected abstract ItemStack getSyncedArmorStack(EquipmentSlot slot);
    @Shadow protected abstract ItemStack getSyncedHandStack(EquipmentSlot slot);
    @Shadow public abstract ItemStack getEquippedStack(EquipmentSlot var1);
    @Shadow public abstract int getArmor();
    @Shadow public abstract AttributeContainer getAttributes();
    @Shadow public abstract Iterable<ItemStack> getArmorItems();
    @Shadow public abstract ItemStack getStackInHand(Hand hand);
    @Shadow public abstract boolean addStatusEffect(StatusEffectInstance effect);
    @Shadow public abstract boolean clearStatusEffects();
    @Shadow public abstract void setHealth(float v);

    @Shadow @Final
    private static TrackedData<Float> HEALTH;

    @Shadow public abstract boolean hasStatusEffect(StatusEffect effect);

    @Shadow protected ItemStack activeItemStack;

    @Shadow public abstract float getHealth();

    @Shadow public abstract float getMaxHealth();

    @Shadow public abstract boolean removeStatusEffect(StatusEffect type);

    @Redirect(method = "getArmorVisibility", at = @At(value = "INVOKE", target = "net/minecraft/item/ItemStack.isEmpty ()Z"))
    private boolean checkArmorInvisibility(ItemStack instance){
        return (instance.isEmpty() || EnchantmentHelper.getLevel(RegisterEnchantment.INSTANCE.getINVISIBILITY(), instance) == 0);
    }

    @Inject(method = "canHaveStatusEffect", at = @At(value = "HEAD"), cancellable = true)
    private void canHaveWithImmunity(StatusEffectInstance effect, CallbackInfoReturnable<Boolean> cir){
        if (this.hasStatusEffect(RegisterStatus.INSTANCE.getIMMUNITY())){
            if (!effect.getEffectType().isBeneficial()){
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "heal", at = @At(value = "HEAD"))
    private void healMixin(float amount, CallbackInfo ci){
        if ((LivingEntity)(Object)this instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)(Object)this;
            PlayerInventory inventory = player.getInventory();
            float newHealth = this.getHealth() + amount;
            float clampedDelta = newHealth - MathHelper.clamp(newHealth,0.0f,this.getMaxHealth());
            float healed = amount - clampedDelta;
            ItemStack stack2 = inventory.getStack(PlayerInventory.OFF_HAND_SLOT);
            if (stack2.getItem() instanceof GemOfPromiseItem) {
                GemOfPromiseItem.Companion.healersGemCheck(stack2, inventory, healed);
            }
        }
    }

    @Inject(method = "addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;Lnet/minecraft/entity/Entity;)Z", at = @At(value = "HEAD"))
    private void addStatusEffectMixin(StatusEffectInstance effect, Entity source, CallbackInfoReturnable<Boolean> cir){
        if ((LivingEntity)(Object)this instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)(Object)this;
            PlayerInventory inventory = player.getInventory();
            ItemStack stack2 = inventory.getStack(PlayerInventory.OFF_HAND_SLOT);
            if (stack2.getItem() instanceof GemOfPromiseItem) {
                GemOfPromiseItem.Companion.inquisitiveGemCheck(stack2,inventory,effect.getEffectType());
            }
        }
    }

    @Inject(method = "damage", at = @At(value = "HEAD"), cancellable = true)
    private void getDamageSourceForShield(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir){
        if (ShieldingAugment.ShieldingObject.damageIsBlocked(this.world.random, (LivingEntity) (Object) this)){
            this.world.playSound(null, this.getBlockPos(), SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 0.4f, 0.9f + this.world.random.nextFloat() * 0.4f);
            cir.setReturnValue(false);
        }
        damageSource = source;
    }

    @Inject(method = "setHealth", at = @At(value = "HEAD"), cancellable = true)
    private void setHealthMixin(float health, CallbackInfo ci){
        if (this.hasStatusEffect(RegisterStatus.INSTANCE.getINSPIRED())){
            if (health <= 0.0f){
                this.removeStatusEffect(RegisterStatus.INSTANCE.getINSPIRED());
                this.dataTracker.set(HEALTH, 0.5f);
                ci.cancel();
            }
        }
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
                RegisterEnchantment.INSTANCE.getBULWARK().specialEffect((LivingEntity)(Object)this, level, activeStack);
            }
        }
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void augmentStatusCheck(CallbackInfo ci){
        if (!world.isClient) {

            //armor effects a little less often because more intensive
            if (EventRegistry.INSTANCE.getTicker_30().isReady()) {
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


    @Redirect(method = "tickMovement", at = @At(value = "FIELD", target = "net/minecraft/entity/LivingEntity.onGround : Z"))
    private boolean checkForMultiJump(LivingEntity instance){
        long time = instance.world.getTime();
        if (instance.isOnGround()) {
            instance.removeStatusEffect(RegisterStatus.INSTANCE.getLEAPT());
            lastTime = time;
            return true; //don't need to multi-jump if the player is already on the ground
        }
        if (!(instance instanceof PlayerEntity)) return instance.isOnGround();
        if (!instance.world.isClient) return instance.isOnGround();
        ItemStack footStack = instance.getEquippedStack(EquipmentSlot.FEET);
        if (footStack.isEmpty()) return instance.isOnGround();
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

    @Inject(method = "tryUseTotem", at = @At(value = "HEAD"), cancellable = true)
    private void tryUseTotem(DamageSource source, CallbackInfoReturnable<Boolean> cir){
        if (source.isOutOfWorld()) {
            cir.setReturnValue(false);
        }
        ItemStack itemStack = null;
        for (Hand hand : Hand.values()) {
            ItemStack itemStack2 = this.getStackInHand(hand);
            if (!itemStack2.isOf(RegisterItem.INSTANCE.getTOTEM_OF_AMETHYST())) continue;
            if (EnchantmentHelper.getLevel(RegisterEnchantment.INSTANCE.getUNDYING(),itemStack2) == 0) continue;
            itemStack = itemStack2.copy();
            RegisterEnchantment.INSTANCE.getUNDYING().specialEffect((LivingEntity)(Object)this,1,itemStack2);
            break;
        }
        if (itemStack != null) {
            this.setHealth(1.0f);
            this.clearStatusEffects();
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1));
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1));
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 800, 0));
            this.world.sendEntityStatus(this, (byte)35);
            cir.setReturnValue(true);
        }

    }

    @Inject(method = "getEquipmentChanges", at = @At(value = "RETURN"), cancellable = true)
    private void getEquipmentChangesMixin(CallbackInfoReturnable<@Nullable Map<EquipmentSlot, ItemStack>> cir){
        int stackValAdd = 0;
        for (EquipmentSlot equipmentSlot : AI.INSTANCE.getSlots()) {
            ItemStack stack1 = this.getSyncedArmorStack(equipmentSlot);
            ItemStack stack2 = this.getEquippedStack(equipmentSlot);
            if (ItemStack.areEqual(stack2, stack1)) continue;
            if (!stack1.isEmpty()) {
                if (RegisterEnchantment.INSTANCE.getRESILIENCE().isEnabled()) {
                    stackValAdd += EnchantmentHelper.getLevel(RegisterEnchantment.INSTANCE.getRESILIENCE(), stack1);
                }
                if (RegisterEnchantment.INSTANCE.getSTEADFAST().isEnabled()) {
                    stackValAdd += EnchantmentHelper.getLevel(RegisterEnchantment.INSTANCE.getSTEADFAST(), stack1);
                }
            }
            if (!stack2.isEmpty()) {
                if (RegisterEnchantment.INSTANCE.getRESILIENCE().isEnabled()) {
                    stackValAdd += EnchantmentHelper.getLevel(RegisterEnchantment.INSTANCE.getRESILIENCE(), stack2);
                }
                if (RegisterEnchantment.INSTANCE.getSTEADFAST().isEnabled()) {
                    stackValAdd += EnchantmentHelper.getLevel(RegisterEnchantment.INSTANCE.getSTEADFAST(), stack2);
                }
            }
        }
        if (stackValAdd == 0) return;
        EnumMap<EquipmentSlot, ItemStack> map = null;
        block4: for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            ItemStack itemStack;
            switch (equipmentSlot.getType()) {
                case HAND -> itemStack = this.getSyncedHandStack(equipmentSlot);
                case ARMOR -> itemStack = this.getSyncedArmorStack(equipmentSlot);
                default -> {
                    continue block4;
                }
            }
            ItemStack itemStack2 = this.getEquippedStack(equipmentSlot);
            if (ItemStack.areEqual(itemStack2, itemStack)) continue;
            if (map == null) {
                map = Maps.newEnumMap(EquipmentSlot.class);
            }
            map.put(equipmentSlot, itemStack2);
            if (!itemStack.isEmpty()) {
                this.getAttributes().removeModifiers(provideResilientMultimap(itemStack,equipmentSlot));
            }
            if (itemStack2.isEmpty()) continue;
            this.getAttributes().addTemporaryModifiers(provideResilientMultimap(itemStack2,equipmentSlot));
        }
        cir.setReturnValue(map);
    }


    private Multimap<EntityAttribute, EntityAttributeModifier> provideResilientMultimap(ItemStack instance, EquipmentSlot slot){
        int stackValAdd = EnchantmentHelper.getLevel(RegisterEnchantment.INSTANCE.getRESILIENCE(), instance);
        int stackValAdd2 = EnchantmentHelper.getLevel(RegisterEnchantment.INSTANCE.getSTEADFAST(), instance);
        if (stackValAdd == 0 && stackValAdd2 == 0) {
            return instance.getAttributeModifiers(slot);
        } else {
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
        }
    }


}
