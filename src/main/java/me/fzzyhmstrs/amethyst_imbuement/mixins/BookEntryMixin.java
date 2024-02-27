package me.fzzyhmstrs.amethyst_imbuement.mixins;

import me.fzzyhmstrs.amethyst_imbuement.compat.patchouli.AdvancementUpdatable;
import me.fzzyhmstrs.amethyst_imbuement.compat.patchouli.PatchouliCompat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookIcon;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.common.book.Book;

@Pseudo
@Mixin(BookEntry.class)
public abstract class BookEntryMixin {

    @Shadow @Final private BookPage[] pages;

    @Shadow @Final private Book book;

    @Shadow @Final private BookIcon icon;

    @Shadow public abstract boolean isLocked();

    @Inject(method = "updateLockStatus", at = @At("TAIL"), require = 0, remap = false)
    private void amethyst_imbuement_patchouliUpdateLockStatusOnPages(CallbackInfo ci){
        if (isLocked()) return;
        for (var page : this.pages){
            if (page instanceof AdvancementUpdatable){
                if (((AdvancementUpdatable)page).updateLockStatus((BookEntry)(Object)this)){
                    PatchouliCompat.INSTANCE.markUpdated(this.book, this.icon);
                    break;
                }
            }
        }
    }

}