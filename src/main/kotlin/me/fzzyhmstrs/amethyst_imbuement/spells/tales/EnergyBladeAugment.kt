package me.fzzyhmstrs.amethyst_imbuement.spells.tales

import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.SlashAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.item.Items
import net.minecraft.particle.DefaultParticleType
import net.minecraft.particle.ParticleTypes

@Suppress("SameParameterValue")
open class EnergyBladeAugment: SlashAugment(ScepterTier.THREE,16){

    override val baseEffect: AugmentEffect = super.baseEffect
                                                .withDamage(7.8F,0.2F,0.0F)
                                                .withAmplifier(100)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.FURY,80,20,
            25,imbueLevel,3,BookOfLoreItem.LORE_TIER, Items.GOLD_SWORD)
    }

    override fun applyTasks(
        world: World,
        user: LivingEntity,
        hand: Hand,
        level: Int,
        effects: AugmentEffect
    ): Boolean {
        return spawnProjectileEntity(world, user, entityClass(world, user, level, effects), soundEvent())
    }

    private fun entityClass(world: World, user: LivingEntity, level: Int = 1, effects: AugmentEffect): ProjectileEntity {
        val ebe = EnergyBladeEntity(world,user)
        ebe.passEffects(effects,level)
        ebe.setVelocity(user, user.pitch, user.yaw, 0.0f, 1.2f, 0.1f)
        return MissileEntity(world, user, false)
    }

    private fun spawnProjectileEntity(world: World, entity: LivingEntity, projectile: ProjectileEntity, soundEvent: SoundEvent): Boolean{
        val bl = world.spawnEntity(projectile)
        if(bl) {
            world.playSound(
                null,
                entity.blockPos,
                soundEvent,
                SoundCategory.PLAYERS,
                1.0f,
                world.getRandom().nextFloat() * 0.4f + 0.8f
            )
        }
        return bl
    }

    override fun effect(world: World,target: Entity?,user: LivingEntity,level: Int,hit: HitResult?,effect: AugmentEffect): Boolean{return true}

    override fun effect(world: World, user: LivingEntity, entityList: MutableList<Entity>, level: Int, effect: AugmentEffect): Boolean {return true}

     override fun clientTask(world: World, user: LivingEntity, hand: Hand, level: Int) {}
    
    override fun filter(list: List<Entity>, user: LivingEntity): MutableList<Entity>{
        return list
    }

    override fun particleType(): DefaultParticleType{
        return ParticleTypes.ENCHANTED_HIT
    }
}
