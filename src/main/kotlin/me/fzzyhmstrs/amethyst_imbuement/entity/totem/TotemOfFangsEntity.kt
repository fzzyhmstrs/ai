package me.fzzyhmstrs.amethyst_imbuement.entity.totem

import me.fzzyhmstrs.amethyst_core.entity.ModifiableEffectEntity
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_imbuement.entity.PlayerFangsEntity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.TargetPredicate
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.item.Items
import net.minecraft.util.Hand
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.Box
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import kotlin.math.atan2

class TotemOfFangsEntity(entityType: EntityType<out TotemOfFangsEntity>, world: World, summoner: LivingEntity? = null, maxAge: Int = 600):
    AbstractEffectTotemEntity(entityType, world, summoner, maxAge, Items.EMERALD) {

    override var entityEffects: AugmentEffect = AugmentEffect().withDamage(3.0F).withRange(5.0)
    private var target: LivingEntity? = null

    override fun tick() {
        val range = entityEffects.range(0)
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

        val user = owner?:return

        val x: Double = entity.x - pos.x
        val z: Double = entity.z - pos.z
        val rotY = (atan2(z, x).toFloat() / Math.PI * 180 + 180).toFloat()

        var d: Double
        var e: Double

        d = pos.y
        e = d + 2.0

        for (i in 0..entityEffects.range(level).toInt()) {
            val g = 1.25 * (i + 1).toDouble()
            val success = PlayerFangsEntity.conjureFang(
                world,
                user,
                user.x + MathHelper.cos(rotY).toDouble() * g,
                user.z + MathHelper.sin(rotY).toDouble() * g,
                d,
                e,
                rotY,
                i,
                entityEffects,
                level,
                spells
            )
            if (success != Double.NEGATIVE_INFINITY) {
                d = success
                e = d + 2.0
            }
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
    }

    companion object Stats: TotemAbstractAttributes{
        override fun createTotemAttributes(): DefaultAttributeContainer.Builder {
            return super.createBaseTotemAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH,16.0).add(EntityAttributes.GENERIC_ARMOR,16.0)
        }
    }

}