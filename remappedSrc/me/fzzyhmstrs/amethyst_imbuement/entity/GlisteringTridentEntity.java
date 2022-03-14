/*
 * Decompiled with CFR 0.0.9 (FabricMC cc05e23f).
 */
package me.fzzyhmstrs.amethyst_imbuement.entity;

import Z;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GlisteringTridentEntity
        extends PersistentProjectileEntity {
    private static final TrackedData<Byte> LOYALTY = DataTracker.registerData(net.minecraft.entity.projectile.TridentEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Boolean> ENCHANTED = DataTracker.registerData(net.minecraft.entity.projectile.TridentEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private ItemStack tridentStack = new ItemStack(RegisterItem.INSTANCE.getGLISTERING_TRIDENT());
    private boolean dealtDamage;
    public int returnTimer;

    public GlisteringTridentEntity(EntityType<? extends GlisteringTridentEntity> entityType, World world) {
        super((EntityType<? extends GlisteringTridentEntity>)entityType, world);
    }

    public GlisteringTridentEntity(World world, LivingEntity owner, ItemStack stack) {
        super(RegisterEntity.INSTANCE.getGLISTERING_TRIDENT_ENTITY(), owner, world);
        this.tridentStack = stack.copy();
        this.dataTracker.set(LOYALTY, (byte)EnchantmentHelper.getLoyalty(stack));
        this.dataTracker.set(ENCHANTED, stack.hasGlint());
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(LOYALTY, (byte)0);
        this.dataTracker.startTracking(ENCHANTED, false);
    }

    @Override
    public void tick() {
        if (this.inGroundTime > 4) {
            this.dealtDamage = true;
        }
        Entity entity = this.getOwner();
        byte i = this.dataTracker.get(LOYALTY);
        if (i > 0 && (this.dealtDamage || this.isNoClip()) && entity != null) {
            if (!this.isOwnerAlive()) {
                if (!this.world.isClient && this.pickupType == PersistentProjectileEntity.PickupPermission.ALLOWED) {
                    this.dropStack(this.asItemStack(), 0.1f);
                }
                this.discard();
            } else {
                this.setNoClip(true);
                Vec3d vec3d = entity.getEyePos().subtract(this.getPos());
                this.setPos(this.getX(), this.getY() + vec3d.y * 0.015 * (double)i, this.getZ());
                if (this.world.isClient) {
                    this.lastRenderY = this.getY();
                }
                double d = 0.05 * (double)i;
                this.setVelocity(this.getVelocity().multiply(0.95).add(vec3d.normalize().multiply(d)));
                if (this.returnTimer == 0) {
                    this.playSound(SoundEvents.ITEM_TRIDENT_RETURN, 10.0f, 1.0f);
                }
                ++this.returnTimer;
            }
        }
        super.tick();
    }

    private boolean isOwnerAlive() {
        Entity entity = this.getOwner();
        if (entity == null || !entity.isAlive()) {
            return false;
        }
        return !(entity instanceof ServerPlayerEntity) || !entity.isSpectator();
    }

    @Override
    protected ItemStack asItemStack() {
        return this.tridentStack.copy();
    }

    public boolean isEnchanted() {
        return this.dataTracker.get(ENCHANTED);
    }

    @Override
    @Nullable
    protected EntityHitResult getEntityCollision(Vec3d currentPosition, Vec3d nextPosition) {
        if (this.dealtDamage) {
            return null;
        }
        return super.getEntityCollision(currentPosition, nextPosition);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        BlockPos blockPos;
        Entity livingEntity;
        Entity entity = entityHitResult.getEntity();
        float f = 11.0f; //base thrown trident damage, setting to 11 vs. vanilla 8
        if (entity instanceof LivingEntity) {
            livingEntity = (LivingEntity)entity;
            f += EnchantmentHelper.getAttackDamage(this.tridentStack, ((LivingEntity)livingEntity).getGroup());
        }
        DamageSource damageSource = DamageSource.trident(this, (livingEntity = this.getOwner()) == null ? this : livingEntity);
        this.dealtDamage = true;
        SoundEvent soundEvent = SoundEvents.ITEM_TRIDENT_HIT;
        if (entity.damage(damageSource, f)) {
            if (entity.getType() == EntityType.ENDERMAN) {
                return;
            }
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity2 = (LivingEntity)entity;
                if (livingEntity instanceof LivingEntity) {
                    EnchantmentHelper.onUserDamaged(livingEntity2, livingEntity);
                    EnchantmentHelper.onTargetDamaged((LivingEntity)livingEntity, livingEntity2);
                    if(EnchantmentHelper.getLevel(RegisterEnchantment.INSTANCE.getDECAYED(), this.asItemStack()) > 0){
                        var b = livingEntity2.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 100, 1));
                    } else if(EnchantmentHelper.getLevel(RegisterEnchantment.INSTANCE.getCONTAMINATED(), this.asItemStack()) > 0){
                        var b = livingEntity2.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 100, 1));
                    }
                }
                this.onHit(livingEntity2);
            }
        }
        this.setVelocity(this.getVelocity().multiply(-0.01, -0.1, -0.01));
        float livingEntity2 = 1.0f;
        if (this.world instanceof ServerWorld && this.world.isThundering() && this.hasChanneling() && this.world.isSkyVisible(blockPos = entity.getBlockPos())) {
            LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(this.world);
            lightningEntity.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(blockPos));
            lightningEntity.setChanneler(livingEntity instanceof ServerPlayerEntity ? (ServerPlayerEntity)livingEntity : null);
            this.world.spawnEntity(lightningEntity);
            soundEvent = SoundEvents.ITEM_TRIDENT_THUNDER;
            livingEntity2 = 5.0f;
        }
        this.playSound(soundEvent, livingEntity2, 1.0f);
    }

    public boolean hasChanneling() {
        return EnchantmentHelper.hasChanneling(this.tridentStack);
    }

    @Override
    protected boolean tryPickup(PlayerEntity player) {
        return super.tryPickup(player) || this.isNoClip() && this.isOwner(player) && player.getInventory().insertStack(this.asItemStack());
    }

    @Override
    protected SoundEvent getHitSound() {
        return SoundEvents.ITEM_TRIDENT_HIT_GROUND;
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        if (this.isOwner(player) || this.getOwner() == null) {
            super.onPlayerCollision(player);
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("Trident", 10)) {
            this.tridentStack = ItemStack.fromNbt(nbt.getCompound("Trident"));
        }
        this.dealtDamage = nbt.getBoolean("DealtDamage");
        this.dataTracker.set(LOYALTY, (byte)EnchantmentHelper.getLoyalty(this.tridentStack));
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.put("Trident", this.tridentStack.writeNbt(new NbtCompound()));
        nbt.putBoolean("DealtDamage", this.dealtDamage);
    }

    @Override
    public void age() {
        byte i = this.dataTracker.get(LOYALTY);
        if (this.pickupType != PersistentProjectileEntity.PickupPermission.ALLOWED || i <= 0) {
            super.age();
        }
    }

    @Override
    protected float getDragInWater() {
        return 0.99f;
    }

    @Override
    public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
        return true;
    }
}

