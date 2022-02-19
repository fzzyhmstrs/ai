package me.fzzyhmstrs.amethyst_imbuement.mixins;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Mixin(CreeperEntity.class)
public class CreeperEntityMixin extends MobEntity {


    protected CreeperEntityMixin(EntityType<? extends MobEntity> entityType, World world, GoalSelector goalSelector) {
        super(entityType, world);
    }

    @Inject(method = "initGoals", at = @At(value = "HEAD"))
    private void addFelineGoalSelector(CallbackInfo ci){
        Predicate<LivingEntity> pred = this::checkForFeline;
        this.goalSelector.add(3, new FleeEntityGoal<PlayerEntity>((CreeperEntity)(Object) this, PlayerEntity.class, 6.0f, 1.0, 1.2, pred));
    }

    private boolean checkForFeline(LivingEntity entity){
        if (!(entity instanceof PlayerEntity)) return false;
        Optional<TrinketComponent> comp = TrinketsApi.getTrinketComponent(entity);
        if (comp.isPresent()){
            List<Pair<SlotReference, ItemStack>> items = comp.get().getAllEquipped();
            for (Pair<SlotReference, ItemStack> slot : items){
                if (EnchantmentHelper.getLevel(RegisterEnchantment.INSTANCE.getFELINE(), slot.getRight()) > 0){
                    return true;
                }
            }
        }
        return false;
    }

}
