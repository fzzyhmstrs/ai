package me.fzzyhmstrs.amethyst_imbuement.mixins;


import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TridentEntity.class)
public abstract class TridentEntityMixin {

    @Shadow protected abstract ItemStack asItemStack();

    @Inject(method = "onEntityHit", at = @At(value = "TAIL"))
    protected void amethyst_imbuement_onEntityHit(EntityHitResult entityHitResult, CallbackInfo ci){
        Entity chk = entityHitResult.getEntity();
        if (chk instanceof LivingEntity livingEntity2) {
            if (EnchantmentHelper.getLevel(RegisterEnchantment.INSTANCE.getDECAYED(), this.asItemStack()) > 0) {
                livingEntity2.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 100, 1));
            } else if (EnchantmentHelper.getLevel(RegisterEnchantment.INSTANCE.getCONTAMINATED(), this.asItemStack()) > 0) {
                livingEntity2.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 100, 1));
            }
        }
    }

}
