package me.fzzyhmstrs.amethyst_imbuement.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.structure.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(StructureTemplate.class)
public class StructureMixin {

    /*@Redirect(method = "loadPalettedBlockInfo", at = @At(value = "INVOKE", target = "net/minecraft/nbt/NbtList.getCompound (I)Lnet/minecraft/nbt/NbtCompound;"))
    private NbtCompound amethyst_imbuement_checkForEnchantingTable(NbtList instance, int index){
        NbtCompound nbtCompound = instance.getCompound(index);
        BlockState state = NbtHelper.toBlockState(nbtCompound);
        if (state.isOf(Blocks.ENCHANTING_TABLE) && AiConfig.INSTANCE.getAltars().getImbuingTableReplaceEnchantingTable()){
            Item table = RegisterBlock.INSTANCE.getIMBUING_TABLE().asItem();
            nbtCompound.putString("Name", Registry.ITEM.getId(table).toString());
        }
        return nbtCompound;
    }*/

    @WrapOperation(method = "loadPalettedBlockInfo", at = @At(value = "INVOKE", target = "net/minecraft/nbt/NbtList.getCompound (I)Lnet/minecraft/nbt/NbtCompound;"))
    private NbtCompound amethyst_imbuement_checkForEnchantingTable(NbtList instance, int index, Operation<NbtCompound> operation, RegistryEntryLookup<Block> blockLookup, NbtList palette, NbtList blocks){
        NbtCompound nbtCompound = operation.call(instance,index);
        BlockState state = NbtHelper.toBlockState(blockLookup,nbtCompound);
        if (state.isOf(Blocks.ENCHANTING_TABLE) && AiConfig.INSTANCE.getBlocks().getImbuing().getReplaceEnchantingTable().get()){
            Item table = RegisterBlock.INSTANCE.getIMBUING_TABLE().asItem();
            nbtCompound.putString("Name", Registries.ITEM.getId(table).toString());
        }
        return nbtCompound;
    }

}
