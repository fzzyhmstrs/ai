package me.fzzyhmstrs.amethyst_imbuement.mixins;

import me.fzzyhmstrs.amethyst_imbuement.util.TryBucketReplacer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(FishEntity.class)
public class FishEntityMixin {

    @Inject(method = "interactMob", at = @At(value = "HEAD"), cancellable = true)
    private void amethyst_imbuement_interactInfinityMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack itemStack = player.getStackInHand(hand);
        int level = EnchantmentHelper.getLevel(Enchantments.INFINITY, itemStack);
        if (level > 0){
            Optional<ActionResult> optional = TryBucketReplacer.INSTANCE.tryBucket(player,hand,(LivingEntity) (Object) this);
            optional.ifPresent(cir::setReturnValue);
        }
    }

}
