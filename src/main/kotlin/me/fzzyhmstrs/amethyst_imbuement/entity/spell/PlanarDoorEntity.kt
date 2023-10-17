package me.fzzyhmstrs.amethyst_imbuement.entity.spell

import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.fzzy_core.nbt_util.Nbt
import me.fzzyhmstrs.fzzy_core.registry.EventRegistry
import net.minecraft.entity.*
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.vehicle.MinecartEntity
import net.minecraft.item.CompassItem
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtOps
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket
import net.minecraft.particle.DustParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.registry.RegistryKey
import net.minecraft.server.world.ChunkTicketType
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent
import java.awt.Color
import java.util.*

class PlanarDoorEntity(type: EntityType<*>?, world: World?, private var colorInt: Int = 0x6400C8) : Entity(type, world), Ownable {

    constructor(owner: LivingEntity, world: World?, colorInt: Int = 0x6400C8): this(RegisterEntity.PLANAR_DOOR,world, colorInt){
        setOwner(owner)
    }

    private var ownerUuid: UUID? = null
    private var owner: Entity? = null
    private var partnerUuid: UUID? = null
    private var partnerWorldKey: RegistryKey<World>? = World.OVERWORLD
    private var partnerBlockPos: BlockPos = this.blockPos
    private var partner: PlanarDoorEntity? = null
    protected val expectedEntities: MutableList<UUID> = mutableListOf()
    private var color = Vec3d.unpackRgb(colorInt).toVector3f()
    private var damageTracking = Pair(0f,0L)

    override fun createSpawnPacket(): Packet<ClientPlayPacketListener> {
        return EntitySpawnS2CPacket(this, colorInt)
    }

    override fun onSpawnPacket(packet: EntitySpawnS2CPacket) {
        super.onSpawnPacket(packet)
        val ci = packet.entityData
        color = Vec3d.unpackRgb(ci).toVector3f()
        //ProjectileUtil.setRotationFromVelocity(this,1f)
    }

    fun setOwner(entity: Entity?) {
        if (entity != null) {
            ownerUuid = entity.uuid
            owner = entity
        }
    }
    fun setPartner(entity: PlanarDoorEntity?) {
        if (entity != null) {
            partnerUuid = entity.uuid
            partnerWorldKey = entity.world.registryKey
            partnerBlockPos = entity.blockPos
            partner = entity
        } else {
            partnerUuid = null
            partnerWorldKey = null
            partnerBlockPos = this.blockPos
            partner = null
        }
    }
    override fun initDataTracker() {
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        if (nbt.containsUuid("Owner")) {
            ownerUuid = nbt.getUuid("Owner")
            owner = null
        }
        if (nbt.containsUuid("Partner")){
            partnerUuid = nbt.getUuid("Partner")
        }
        if (nbt.contains("PartnerPos")){
            partnerBlockPos = Nbt.readBlockPos("PartnerPos",nbt)
        }
        val worldOptional = World.CODEC.parse(NbtOps.INSTANCE, nbt[CompassItem.LODESTONE_DIMENSION_KEY]).result()
        if (worldOptional.isPresent)
            partnerWorldKey = worldOptional.get()
        color = Vec3d.unpackRgb(nbt.getInt("planar_door_color")).toVector3f()
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        if (ownerUuid != null) {
            nbt.putUuid("Owner", ownerUuid)
        }
        if (partnerUuid != null) {
            nbt.putUuid("Partner", partnerUuid)
        }
        if (partnerBlockPos != this.blockPos){
            Nbt.writeBlockPos("PartnerPos",partnerBlockPos,nbt)
        }
        if (partnerWorldKey != null) {
            World.CODEC.encodeStart(NbtOps.INSTANCE, partnerWorldKey).resultOrPartial { s: String? ->
                println(s)
            }.ifPresent { nbtElement: NbtElement? ->
                nbt.put(CompassItem.LODESTONE_DIMENSION_KEY, nbtElement)
            }
        }
        nbt.putInt("planar_door_color",Color(color.x,color.y,color.z).rgb)
    }

    override fun damage(source: DamageSource, amount: Float): Boolean {
        if (isInvulnerableTo(source)) {
            return false
        }
        if (world.isClient || this.isRemoved) {
            return true
        }
        this.emitGameEvent(GameEvent.ENTITY_DAMAGE, source.attacker)
        val bl = source.attacker is PlayerEntity
        if (bl) {
            val totalAmount = if (world.time - damageTracking.second > 100L) {
                damageTracking = Pair(amount, world.time)
                amount
            } else {
                val tempAmount = damageTracking.first + amount
                damageTracking = Pair(tempAmount, world.time)
                tempAmount
            }
            if (totalAmount >= 20f) {
                getPartner()?.setPartner(null)
                discard()
            }
        }
        return true
    }

    override fun canHit(): Boolean {
        return !this.isRemoved
    }

    override fun tick() {
        super.tick()
        val w = world
        if (w is ServerWorld){
            val particle = if(partnerUuid == null){
                ParticleTypes.SMOKE
            } else {
                DustParticleEffect(color,0.8f)
            }
            w.spawnParticles(particle,this.x,this.y+0.25,this.z,20,0.5,0.4,0.5,0.0)
            w.spawnParticles(particle,this.x,this.y+0.65,this.z,20,0.45,0.4,0.45,0.0)
            w.spawnParticles(particle,this.x,this.y+0.95,this.z,20,0.4,0.4,0.4,0.0)
            w.spawnParticles(particle,this.x,this.y+1.35,this.z,20,0.25,0.4,0.25,0.0)

            if (ticker.isReady()){
                    val entities = world.getOtherEntities(this, this.boundingBox)
                    { e ->
                        (e is PlayerEntity && AiConfig.entities.isEntityPvpTeammate(
                            getOwner() as? LivingEntity,
                            e,
                            RegisterEnchantment.PLANAR_DOOR
                        )) || e is AnimalEntity || e is MinecartEntity || e is ItemEntity
                    }
                    if (entities.isNotEmpty()) {
                        val p = getPartner()
                        if (p != null) {
                            for (entity in entities) {
                                if (expectedEntities.contains(entity.uuid)) continue
                                if (entity is Tameable) {
                                    if (!p.expectedEntities.contains(entity.ownerUuid)) continue
                                }
                                if (entity != null) {
                                    val pw = p.world
                                    println(pw)
                                    if (pw is ServerWorld) {
                                        p.expectedEntities.add(entity.uuid)
                                        entity.teleport(pw, p.x, p.y, p.z, setOf(), entity.yaw, entity.pitch)
                                    }
                                    pw.playSound(
                                        null,
                                        p.blockPos,
                                        SoundEvents.BLOCK_PORTAL_TRAVEL,
                                        SoundCategory.BLOCKS,
                                        0.2f,
                                        1.0f
                                    )
                                }
                            }
                        }
                    }
                    val entityUUIDs = entities.map { it.uuid }
                    val iterator = expectedEntities.iterator()
                    while (iterator.hasNext()) {
                        if (!entityUUIDs.contains(iterator.next()))
                            iterator.remove()
                    }
            }
        }
        if (world.random.nextFloat() < 0.02f){
            if (partnerUuid != null){
                world.playSound(null,this.blockPos,SoundEvents.BLOCK_PORTAL_AMBIENT,SoundCategory.BLOCKS,0.25f,1.0f)
            }
        }

    }

    override fun getEyeHeight(pose: EntityPose, dimensions: EntityDimensions): Float {
        return dimensions.height
    }

    override fun getOwner(): Entity? {
        if (owner != null && owner?.isRemoved != true) {
            return owner
        }
        if (ownerUuid != null && world is ServerWorld) {
            owner = (world as ServerWorld).getEntity(ownerUuid)
            return owner
        }
        return null
    }
    fun getPartner(): PlanarDoorEntity? {
        if (partner != null && partner?.isRemoved != true) {
            return partner
        }
        if (partnerUuid != null && world is ServerWorld) {
            if (partnerWorldKey != null){
                val pWorld = (world as ServerWorld).server.getWorld(partnerWorldKey)
                println(partnerUuid)
                println(partnerWorldKey)
                //pWorld?.getChunk(partnerBlockPos)
                println(pWorld?.isChunkLoaded(partnerBlockPos))
                pWorld?.chunkManager?.addTicket(ChunkTicketType.PORTAL, ChunkPos(partnerBlockPos), 3, partnerBlockPos)
                /*while(pWorld?.isChunkLoaded(partnerBlockPos) != true){
                    val i = MathHelper.atan2(System.nanoTime().toDouble(), System.currentTimeMillis().toDouble())
                }*/
                partner = pWorld?.getEntity(partnerUuid) as? PlanarDoorEntity
                partnerBlockPos = partner?.blockPos ?: partnerBlockPos
                println(partner)
                return partner
            }
        }
        return null
    }

    companion object{
        //private val color = Vector3f(0.5f,0.0f,0.75f)
        private val ticker = EventRegistry.Ticker(4).also { EventRegistry.registerTickUppable(it) }
    }

}