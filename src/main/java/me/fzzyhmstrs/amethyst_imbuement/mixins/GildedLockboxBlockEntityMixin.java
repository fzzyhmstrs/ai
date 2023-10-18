package me.fzzyhmstrs.amethyst_imbuement.mixins;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fzzyhmstrs.amethyst_imbuement.entity.block.GildedLockboxBlockEntity;
import me.jellysquid.mods.lithium.api.inventory.LithiumInventory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;

@Restriction(
        require = {
                @Condition("lithium")
        }
)
@Mixin(GildedLockboxBlockEntity.class)
public abstract class GildedLockboxBlockEntityMixin extends ChestBlockEntity implements LithiumInventory {

    public GildedLockboxBlockEntityMixin(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getInventoryLithium() {
        return this.getInvStackList();
    }

    @Override
    public void setInventoryLithium(DefaultedList<ItemStack> defaultedList) {
        this.setInvStackList(defaultedList);
    }
}
