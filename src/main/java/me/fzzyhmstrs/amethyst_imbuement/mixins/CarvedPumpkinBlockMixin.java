package me.fzzyhmstrs.amethyst_imbuement.mixins;

import me.fzzyhmstrs.amethyst_imbuement.block.CrystallineCoreBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CarvedPumpkinBlock.class)
public class CarvedPumpkinBlockMixin {

    @Inject(method = "onBlockAdded", at = @At(value = "HEAD"), cancellable = true)
    private void amethyst_imbuement_crystalGolemTest(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify, CallbackInfo ci){
        BlockPattern.Result result = CrystallineCoreBlock.Companion.getCrystallineGolemPattern().searchAround(world,pos);
        if (result != null){
            CrystallineCoreBlock.Companion.spawnCrystallineGolem(result,world);
            ci.cancel();
        }
    }

}
