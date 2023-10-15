package me.fzzyhmstrs.amethyst_imbuement.mixins;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fzzyhmstrs.amethyst_imbuement.block.GildedLockboxBlock;
import me.jellysquid.mods.lithium.api.inventory.LithiumInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Restriction(
        require = {
                @Condition("lithium")
        }
)
@Mixin(GildedLockboxBlock.class)
public abstract class GildedLockboxBlockMixin implements LithiumInventory {
    @Shadow
    private int size;

    @Shadow
    private DefaultedList<ItemStack> inventory;

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public DefaultedList<ItemStack> getInventoryLithium() {
        return this.inventory;
    }

    @Override
    public void setInventoryLithium(DefaultedList<ItemStack> defaultedList) {
        this.inventory = defaultedList;
    }
}
