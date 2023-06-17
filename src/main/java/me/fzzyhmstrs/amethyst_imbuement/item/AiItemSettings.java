package me.fzzyhmstrs.amethyst_imbuement.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import org.jetbrains.annotations.Nullable;

public class AiItemSettings extends FabricItemSettings {

    public AiItemSettings(){
    }

    @Nullable
    private AiItemGroup aiGroup;

    public AiItemSettings aiGroup(AiItemGroup group){
        this.aiGroup = group;
        return this;
    }

    public @Nullable AiItemGroup getAiGroup() {
        return aiGroup;
    }

    public enum AiItemGroup{
        ALL,
        GEM,
        EQUIPMENT,
        SCEPTER,
        BOOK,
        FAVES
    }

    @Nullable
    public static AiItemGroup groupFromItem(Item item){
        return ((ItemBookCategory)item).amethyst_imbuement_getAiItemGroup();
    }

}
