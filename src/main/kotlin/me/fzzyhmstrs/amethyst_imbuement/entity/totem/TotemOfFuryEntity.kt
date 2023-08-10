package me.fzzyhmstrs.amethyst_imbuement.entity.totem

import me.fzzyhmstrs.amethyst_core.entity.ModifiableEffectEntity
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.fzzy_core.registry.EventRegistry
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.TargetPredicate
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Hand
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.Box
import net.minecraft.world.World

class TotemOfFuryEntity(entityType: EntityType<out TotemOfFuryEntity>, world: World, summoner: LivingEntity? = null, maxAge: Int = 600, private val attackModifier: Int = 4):
    AbstractEffectTotemEntity(entityType, world, summoner, maxAge, RegisterItem.LETHAL_GEM) {

    override var entityEffects: AugmentEffect = AugmentEffect().withDamage(3.0F).withRange(5.0)
    private var target: LivingEntity? = null

    override fun initTicker(): EventRegistry.Ticker {
        val ticker = EventRegistry.Ticker(30 - attackModifier)
        EventRegistry.registerTickUppable(ticker)
        return ticker
    }

    override fun tick() {
        val range = entityEffects.range(level)
        val box = Box(this.pos.add(range,range,range),this.pos.subtract(range,range,range))
        val entities = world.getOtherEntities(owner, box)
        val list: MutableList<LivingEntity> = mutableListOf()
        for (entity in entities) {
            if (entity is LivingEntity && entity != this){
                list.add(entity)
            }
        }
        target = world.getClosestEntity(list, TargetPredicate.DEFAULT,owner,pos.x,pos.y,pos.z)
        super.tick()
    }

    override fun lookAt(): LivingEntity? {
        return if (target != null){
            target
        } else {
            super.lookAt()
        }
    }

    override fun totemEffect() {
        val entity = target ?: return
        if (entity == this){
            target = null
            return
        }

        val summoner = owner
        if (summoner !is SpellCastingEntity) return
        val hit = EntityHitResult(entity)
        spells.processSingleEntityHit(hit,processContext,world,this,summoner,Hand.MAIN_HAND,level,entityEffects)
        runEffect(ModifiableEffectEntity.DAMAGE,this,owner,processContext)
        if (!entity.isAlive){
            spells.processOnKill(hit,processContext,world,this,summoner,Hand.MAIN_HAND,level,entityEffects)
            runEffect(ModifiableEffectEntity.KILL,this,owner,processContext)
        }
        spells.hitSoundEvents(world, summoner.blockPos, processContext)
        val serverWorld: ServerWorld = this.world as ServerWorld
        beam(serverWorld)
    }

    private fun beam(serverWorld: ServerWorld){
        val startPos = this.pos.add(0.0,1.2,0.0)
        val endPos = target?.pos?.add(0.0,1.2,0.0)?:startPos.subtract(0.0,2.0,0.0)
        val vec = endPos.subtract(startPos).multiply(0.1)
        var pos = startPos
        for (i in 1..10){
            val particle = spells.getCastParticleType()
            serverWorld.spawnParticles(particle,pos.x,pos.y,pos.z,5,vec.x,vec.y,vec.z,0.0)
            pos = pos.add(vec)
        }

    }

    companion object Stats: TotemAbstractAttributes{
        override fun createTotemAttributes(): DefaultAttributeContainer.Builder {
            return super.createTotemAttributes().add(EntityAttributes.GENERIC_ARMOR,10.0)
        }
    }

}