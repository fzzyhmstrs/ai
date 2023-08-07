package me.fzzyhmstrs.amethyst_imbuement.mixins;

import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(CreeperEntity.class)
public class CreeperEntityMixin extends MobEntity {

    @SuppressWarnings("unused")
    protected CreeperEntityMixin(EntityType<? extends MobEntity> entityType, World world, GoalSelector goalSelector) {
        super(entityType, world);
    }

    @Inject(method = "initGoals", at = @At(value = "HEAD"))
    private void amethyst_imbuement_addFelineGoalSelector(CallbackInfo ci){
        Predicate<LivingEntity> predicate = this::checkForFeline;
        this.goalSelector.add(3, new FleeEntityGoal<>((CreeperEntity) (Object) this, PlayerEntity.class, 6.0f, 1.0, 1.2, predicate));
    }

    private boolean checkForFeline(LivingEntity entity){
        if (!(entity instanceof PlayerEntity)) return false;
        return RegisterEnchantment.INSTANCE.getFELINE().specialEffect(entity,1,ItemStack.EMPTY);
    }

}
