package me.fzzyhmstrs.amethyst_imbuement.mixins;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fzzyhmstrs.amethyst_imbuement.item.SpellcastersFocusItem;
import me.fzzyhmstrs.amethyst_imbuement.item.TotemItem;
import me.fzzyhmstrs.amethyst_imbuement.item.WitchesOrbItem;
import me.fzzyhmstrs.gear_core.interfaces.*;
import net.minecraft.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;

@Restriction(
        require = {
                @Condition("gear_core")
        }
)
@Mixin({SpellcastersFocusItem.class, WitchesOrbItem.class, TotemItem.class})
public class ModifiableItemMixin implements HitTracking, MineTracking, AttributeTracking, DamageTracking, KillTracking, ModifierTracking {

    @Override
    public boolean correctSlot(EquipmentSlot slot){
        return slot == EquipmentSlot.OFFHAND;
    }

    @Override
    public EquipmentSlot getCorrectSlot(){
        return EquipmentSlot.OFFHAND;
    }

}
