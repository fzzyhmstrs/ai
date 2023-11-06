package me.fzzyhmstrs.amethyst_imbuement.mixins;

import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LootTable.Builder.class)
public interface BuilderAccessor {
    @Accessor
    LootContextType getType();
}
