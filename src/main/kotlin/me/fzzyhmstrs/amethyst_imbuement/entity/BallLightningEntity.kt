package me.fzzyhmstrs.amethyst_imbuement.entity

import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.entity.ModifiableEffectEntity
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.fzzy_core.registry.EventRegistry
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class BallLightningEntity(entityType: EntityType<BallLightningEntity>, world: World): BasicMissileEntity(entityType, world) {

    constructor(world: World,owner: LivingEntity?, direction: Vec3d, speed: Float, divergence: Float, pos: Vec3d) : this(RegisterEntity.BALL_LIGHTNING_ENTITY,world){
        this.owner = owner
        this.setVelocity(direction.x,direction.y,direction.z,speed, divergence)
        this.setPosition(pos)
        this.setRotation(owner?.yaw?:0f, owner?.pitch?:0f)
    }

    override var entityEffects: AugmentEffect = AugmentEffect()
        .withDamage(5.4F,0.2F,0.0F)
        .withDuration(19,-1)
        .withRange(3.0,.25)
    override val maxAge: Int = 600
    var ticker = EventRegistry.ticker_20
    var initialBeam = false
    override var colorData: ColorData = ColorData(1f,1f,0f,-2f,1.25f)

    override fun passEffects(spells: PairedAugments, ae: AugmentEffect, level: Int) {
        super.passEffects(spells,ae, level)
        ticker = EventRegistry.Ticker(ae.duration(level))
        EventRegistry.registerTickUppable(ticker)
    }

    override fun tick() {
        super.tick()
        if (world !is ServerWorld) return
        if (!ticker.isReady() && initialBeam) return
        val augment = spells.primary() ?: return
        val livingEntity = owner
        if (livingEntity !is LivingEntity || livingEntity !is SpellCastingEntity) return
        val box = Box(this.pos.add(entityEffects.range(0),entityEffects.range(0),entityEffects.range(0)),this.pos.subtract(entityEffects.range(0),entityEffects.range(0),entityEffects.range(0)))
        val entities = world.getOtherEntities(owner, box)
        for (entity in entities){
            if (entity is SpellCastingEntity && AiConfig.entities.isEntityPvpTeammate(livingEntity, entity, augment)) continue
            if (entity !is LivingEntity) continue
            runEffect(ModifiableEffectEntity.DAMAGE,this,owner,processContext)
            spells.processSingleEntityHit(EntityHitResult(entity),world,this,livingEntity,Hand.MAIN_HAND,level,entityEffects)
            if (!entity.isAlive){
                runEffect(ModifiableEffectEntity.KILL,this,owner,processContext)
                spells.processOnKill(EntityHitResult(entity),world,this,livingEntity,Hand.MAIN_HAND,level,entityEffects)
            }
            beam(world as ServerWorld,entity)
            spells.hitSoundEvents(world, blockPos, processContext)
            //world.playSound(null,this.blockPos, SoundEvents.ITEM_TRIDENT_THUNDER, SoundCategory.NEUTRAL,0.3f,2.0f + world.random.nextFloat() * 0.4f - 0.2f)
        }
        initialBeam = true
    }

    private fun beam(serverWorld: ServerWorld, entity: LivingEntity){
        val startPos = this.pos.add(0.0,0.25,0.0)
        val endPos = entity.pos.add(0.0,entity.height/2.0,0.0)
        val vec = endPos.subtract(startPos).multiply(0.1)
        var pos = startPos
        val particle = spells.getHitParticleType(EntityHitResult(entity))
        for (i in 1..10){
            serverWorld.spawnParticles(particle,pos.x,pos.y,pos.z,2,vec.x,vec.y,vec.z,0.0)
            pos = pos.add(vec)
        }

    }

    override fun onEntityHit(entityHitResult: EntityHitResult) {
    }

    override fun onMissileBlockHit(blockHitResult: BlockHitResult) {
        if (world !is ServerWorld) return
        val augment = spells.primary() ?: return
        val livingEntity = owner
        if (livingEntity !is LivingEntity || livingEntity !is SpellCastingEntity) return
        val box = Box(this.pos.add(entityEffects.range(0),entityEffects.range(0),entityEffects.range(0)),this.pos.subtract(entityEffects.range(0),entityEffects.range(0),entityEffects.range(0)))
        val entities = world.getOtherEntities(owner, box)
        for (entity in entities){
            if (entity is SpellCastingEntity && AiConfig.entities.isEntityPvpTeammate(owner as LivingEntity, entity,augment)) continue
            if (entity !is LivingEntity) continue
            runEffect(ModifiableEffectEntity.DAMAGE,this,owner,processContext)
            spells.processSingleEntityHit(EntityHitResult(entity),world,this,livingEntity,Hand.MAIN_HAND,level,entityEffects)
            if (!entity.isAlive){
                runEffect(ModifiableEffectEntity.KILL,this,owner,processContext)
                spells.processOnKill(EntityHitResult(entity),world,this,livingEntity,Hand.MAIN_HAND,level,entityEffects)
            }
            beam(world as ServerWorld,entity)
            spells.hitSoundEvents(world, blockPos, processContext)
        }
        super.onMissileBlockHit(blockHitResult)
    }
    override fun remove(reason: RemovalReason?) {
        EventRegistry.removeTickUppable(ticker)
        super.remove(reason)
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        super.writeCustomDataToNbt(nbt)
        writeModifiableNbt(nbt)
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        super.readCustomDataFromNbt(nbt)
        readModifiableNbt(nbt)
    }
}
