package me.fzzyhmstrs.amethyst_imbuement.mixins;

import me.fzzyhmstrs.amethyst_imbuement.block.CrystallineCoreBlock;
import me.fzzyhmstrs.amethyst_imbuement.entity.CrystallineGolemEntity;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CarvedPumpkinBlock.class)
public class CarvedPumpkinBlockMixin {

    @Inject(method = "onBlockAdded", at = @At(value = "HEAD"), cancellable = true)
    private void crystalGolemTest(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify, CallbackInfo ci){
        BlockPattern.Result result = CrystallineCoreBlock.Companion.getCrystallineGolemPattern().searchAround(world,pos);
        if (result != null){
            CachedBlockPosition cachedBlockPosition;
            for (int i = 0; i < CrystallineCoreBlock.Companion.getCrystallineGolemPattern().getWidth(); ++i) {
                for (int j = 0; j < CrystallineCoreBlock.Companion.getCrystallineGolemPattern().getHeight(); ++j) {
                    cachedBlockPosition = result.translate(i, j, 0);
                    world.setBlockState(cachedBlockPosition.getBlockPos(), Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
                    world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, cachedBlockPosition.getBlockPos(), Block.getRawIdFromState(cachedBlockPosition.getBlockState()));
                }
            }
            CrystallineGolemEntity cge = new CrystallineGolemEntity(RegisterEntity.INSTANCE.getCRYSTAL_GOLEM_ENTITY(), world);
            BlockPos blockPos = result.translate(1, 2, 0).getBlockPos();
            cge.setPlayerCreated(true);
            cge.refreshPositionAndAngles((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.05, (double)blockPos.getZ() + 0.5, 0.0f, 0.0f);
            world.spawnEntity(cge);
            for (ServerPlayerEntity serverPlayerEntity : world.getNonSpectatingEntities(ServerPlayerEntity.class, cge.getBoundingBox().expand(5.0))) {
                Criteria.SUMMONED_ENTITY.trigger(serverPlayerEntity, cge);
            }
            for (int i = 0; i < CrystallineCoreBlock.Companion.getCrystallineGolemPattern().getWidth(); ++i) {
                for (int j = 0; j < CrystallineCoreBlock.Companion.getCrystallineGolemPattern().getHeight(); ++j) {
                    CachedBlockPosition cachedBlockPosition2 = result.translate(i, j, 0);
                    world.updateNeighbors(cachedBlockPosition2.getBlockPos(), Blocks.AIR);
                }
            }
            ci.cancel();
        }
    }

}
