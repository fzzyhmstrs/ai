package me.fzzyhmstrs.amethyst_imbuement.entity.block



import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.fzzy_core.nbt_util.Nbt
import me.fzzyhmstrs.fzzy_core.registry.EventRegistry
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.Entity
import net.minecraft.item.CompassItem
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtOps
import net.minecraft.particle.DustParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.registry.RegistryKey
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import org.joml.Vector3f
import java.awt.Color
import java.util.*

class PlanarDoorBlockEntity(blockEntityType: BlockEntityType<*>, blockPos: BlockPos, blockState: BlockState) :
    BlockEntity(blockEntityType, blockPos, blockState) {

    constructor(blockPos: BlockPos, blockState: BlockState): this(RegisterEntity.PLANAR_DOOR_BLOCK_ENTITY, blockPos, blockState)

    private var ownerUuid: UUID? = null
    private var owner: Entity? = null
    private var color: Vector3f = Vec3d.unpackRgb(0x6400C8).toVector3f()
    private var partnerWorldKey: RegistryKey<World>? = World.OVERWORLD
    private var partnerBlockPos: BlockPos = blockPos

    fun setPartnerPos(pos: BlockPos){
        partnerBlockPos = pos
    }

    fun getPartnerPos(): BlockPos{
        return partnerBlockPos
    }

    fun setColor(colorInt: Int){
        color = Vec3d.unpackRgb(colorInt).toVector3f()
    }

    fun setOwner(entity: Entity?) {
        if (entity != null) {
            ownerUuid = entity.uuid
            owner = entity
        }
    }

    fun getOwner(): Entity? {
        if (owner != null && owner?.isRemoved != true) {
            return owner
        }
        if (ownerUuid != null && world is ServerWorld) {
            owner = (world as ServerWorld).getEntity(ownerUuid)
            return owner
        }
        return null
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        if (nbt.containsUuid("Owner")) {
            ownerUuid = nbt.getUuid("Owner")
            owner = null
        }
        color = Vec3d.unpackRgb(nbt.getInt("planar_door_color")).toVector3f()
        if (nbt.contains("PartnerPos")){
            partnerBlockPos = Nbt.readBlockPos("PartnerPos",nbt)
        }
        val worldOptional = World.CODEC.parse(NbtOps.INSTANCE, nbt[CompassItem.LODESTONE_DIMENSION_KEY]).result()
        if (worldOptional.isPresent)
            partnerWorldKey = worldOptional.get()
    }

    override fun writeNbt(nbt: NbtCompound) {
        super.writeNbt(nbt)
        if (ownerUuid != null) {
            nbt.putUuid("Owner", ownerUuid)
        }
        nbt.putInt("planar_door_color", Color(color.x,color.y,color.z).rgb)
        if (partnerBlockPos != this.pos){
            Nbt.writeBlockPos("PartnerPos",partnerBlockPos,nbt)
        }
        if (partnerWorldKey != null) {
            World.CODEC.encodeStart(NbtOps.INSTANCE, partnerWorldKey).resultOrPartial { s: String? ->
            }.ifPresent { nbtElement: NbtElement? ->
                nbt.put(CompassItem.LODESTONE_DIMENSION_KEY, nbtElement)
            }
        }
    }

    fun setPartnerWorld(key: RegistryKey<World>){
        partnerWorldKey = key
    }

    fun getPartnerWorld(world: World): World?{
        return world.server?.getWorld(partnerWorldKey)
    }

    private fun clear(){
        partnerBlockPos = this.pos
        partnerWorldKey = null
    }

    fun clearPartner(world: World){
        val partnerWorld = world.server?.getWorld(partnerWorldKey) ?: return
        partnerWorld.getBlockState(pos)
        (partnerWorld.getBlockEntity(partnerBlockPos) as? PlanarDoorBlockEntity)?.clear()
    }

    fun breakPartner(world: World){
        val partnerWorld = world.server?.getWorld(partnerWorldKey) ?: return
        if (hasPartner() && partnerWorld.getBlockState(partnerBlockPos).isOf(RegisterBlock.PLANAR_DOOR))
            partnerWorld.breakBlock(partnerBlockPos,false)
    }

    private fun hasPartner(): Boolean{
      return partnerBlockPos != this.pos && partnerWorldKey != null
    }

    companion object{
        private val ticker = EventRegistry.Ticker(3).also { EventRegistry.registerTickUppable(it) }
        fun tick(world: World, pos: BlockPos, state: BlockState, blockEntity: PlanarDoorBlockEntity){
            if(ticker.isNotReady()) return
            val w = world as? ServerWorld ?: return
            val particle = if(!blockEntity.hasPartner()){
                ParticleTypes.LARGE_SMOKE
            } else {
                DustParticleEffect(blockEntity.color,0.8f)
            }
            val p = pos.toCenterPos().add(0.0,-0.5,0.0)
            w.spawnParticles(particle,p.x,p.y+0.25,p.z,30,0.5,0.4,0.5,0.0)
            w.spawnParticles(particle,p.x,p.y+0.65,p.z,30,0.45,0.4,0.45,0.0)
            w.spawnParticles(particle,p.x,p.y+0.95,p.z,30,0.4,0.4,0.4,0.0)
            w.spawnParticles(particle,p.x,p.y+1.35,p.z,30,0.25,0.4,0.25,0.0)
        }
    }


}
