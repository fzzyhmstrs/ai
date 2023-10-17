package me.fzzyhmstrs.amethyst_imbuement.mixins;

import net.minecraft.entity.projectile.ProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ProjectileEntity.class)
public interface ProjectileEntityAccessor {
    @Accessor
    boolean isLeftOwner();

    @Accessor
    void setLeftOwner(boolean leftOwner);

    @Accessor
    boolean isShot();

    @Accessor
    void setShot(boolean shot);
}
