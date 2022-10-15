package me.fzzyhmstrs.amethyst_imbuement.entity.totem

import me.fzzyhmstrs.amethyst_core.entity_util.ModifiableEffectEntity
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.CustomDamageSources
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.TargetPredicate
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.Box
import net.minecraft.world.World

class TotemOfFuryEntity(entityType: EntityType<out TotemOfFuryEntity>, world: World, summoner: PlayerEntity? = null, maxAge: Int = 600):
    AbstractEffectTotemEntity(entityType, world, summoner, maxAge, RegisterItem.LETHAL_GEM), ModifiableEffectEntity {

    override var entityEffects: AugmentEffect = AugmentEffect().withDamage(3.0F).withRange(5.0)
    private var target: LivingEntity? = null

    override fun passEffects(ae: AugmentEffect, level: Int) {
        super.passEffects(ae, level)
        entityEffects.setDamage(ae.damage(level))
    }

    override fun tick() {
        val range = entityEffects.range(0)
        val box = Box(this.pos.add(range,range,range),this.pos.subtract(range,range,range))
        val entities = world.getOtherEntities(summoner, box)
        val list: MutableList<LivingEntity> = mutableListOf()
        for (entity in entities) {
            if (entity is LivingEntity){
                list.add(entity)
            }
        }
        target = world.getClosestEntity(list, TargetPredicate.DEFAULT,summoner,pos.x,pos.y,pos.z)
        super.tick()
    }

    override fun lookAt(): LivingEntity? {
        return target
    }

    override fun totemEffect() {
        target?.damage(CustomDamageSources.LightningDamageSource(this),entityEffects.damage(0))
        val serverWorld: ServerWorld = this.world as ServerWorld
        beam(serverWorld)
        world.playSound(null,this.blockPos,SoundEvents.ITEM_TRIDENT_THUNDER,SoundCategory.NEUTRAL,1.0f,1.0f)
    }

    private fun beam(serverWorld: ServerWorld){
        val startPos = this.pos.add(0.0,1.2,0.0)
        val endPos = target?.pos?.add(0.0,1.2,0.0)?:startPos.subtract(0.0,2.0,0.0)
        val vec = endPos.subtract(startPos).multiply(0.1)
        var pos = startPos
        for (i in 1..10){
            serverWorld.spawnParticles(ParticleTypes.ELECTRIC_SPARK,pos.x,pos.y,pos.z,5,vec.x,vec.y,vec.z,0.0)
            pos = pos.add(vec)
        }

    }



}