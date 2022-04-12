package me.fzzyhmstrs.amethyst_imbuement.mixins;

import kotlin.Triple;
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterVillager;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(PoolStructurePiece.class)
public class PoolStructurePieceMixin {

    @Shadow @Final protected StructurePoolElement poolElement;

    @Inject(method = "writeNbt", at = @At(value = "TAIL"))
    private void fixPoolElement(StructureContext context, NbtCompound nbt, CallbackInfo ci){
        if (!nbt.contains("pool_element")) {
            RegistryOps<NbtElement> dynamicOps = RegistryOps.of(NbtOps.INSTANCE, context.registryManager());
            System.out.println(dynamicOps);
            System.out.println(poolElement);
            NbtCompound nbtEl = new NbtCompound();
            String poolId = poolElement.toString();
            String poolId2 = poolId.replace("\\]","");
            String split = "\\[";
            String[] poolIdArray = poolId2.split(split);
            String poolLocation = poolIdArray[poolIdArray.length - 1];
            System.out.println(poolLocation);
            System.out.println(RegisterVillager.INSTANCE.getLocationMap());
            if (RegisterVillager.INSTANCE.getLocationMap().containsKey(poolLocation)){
                Triple<String, String, String> data = RegisterVillager.INSTANCE.getLocationMap().get(poolLocation);
                nbtEl.putString("element_type",data.getFirst());
                nbtEl.putString("location",poolLocation);
                nbtEl.putString("processors", data.getSecond());
                nbtEl.putString("projection", data.getThird());
                nbt.put("pool_element",nbtEl);
            }
            System.out.println(nbt);
        }
    }

}
