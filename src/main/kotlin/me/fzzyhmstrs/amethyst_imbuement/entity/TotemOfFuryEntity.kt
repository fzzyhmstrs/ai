package me.fzzyhmstrs.amethyst_imbuement.entity

import me.fzzyhmstrs.amethyst_core.entity_util.ModifiableEffectEntity
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.entity.EntityType
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.TargetPredicate
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Arm
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
        super.tick()
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
    }

    override fun lookAt(): LivingEntity? {
        return target
    }

    override fun totemEffect() {
        TODO("Not yet implemented")
    }



}