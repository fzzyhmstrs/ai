package me.fzzyhmstrs.amethyst_imbuement.mixins;

import me.fzzyhmstrs.amethyst_imbuement.compat.patchouli.PatchouliCompat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.patchouli.common.book.Book;

@Pseudo
@Mixin(Book.class)
public abstract class BookMixin {

    @Shadow public abstract boolean advancementsEnabled();

    @Shadow @Final public boolean showToasts;

    @Inject(method = "reloadLocks", at = @At("TAIL"), require = 0, remap = false)
    private void amethyst_imbuement_patchouliUpdateLockStatusOnPages(boolean suppressToasts, CallbackInfo ci){
        if (!suppressToasts && advancementsEnabled() && showToasts)
            PatchouliCompat.INSTANCE.addPageToast((Book) (Object)this);
    }

}