package me.fzzyhmstrs.amethyst_imbuement.spells.special

import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.MinorSupportAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterSound
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.item.Items
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.world.World

class ExecuteAugment: MinorSupportAugment(ScepterTier.THREE,6) {

    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withDamage(9.4F,0.6F)
            .withRange(7.5,0.5)
            .withAmplifier(9,1)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.FURY,300,250,
            30, imbueLevel,25, LoreTier.NO_TIER, Items.NETHERITE_AXE)
    }

    override fun supportEffect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        effects: AugmentEffect
    ): Boolean {
        return if(target != null) {
            if (target is LivingEntity) {
                if (target is SpellCastingEntity && AiConfig.entities.isEntityPvpTeammate(user, target,this)) return false
                val bl = if (target.health <= effects.amplifier(level)){
                    target.kill()
                    true
                } else {
                    target.damage(user.damageSources.indirectMagic(user,user),effects.damage(level))
                }
                if (bl) {
                    effects.accept(target,AugmentConsumer.Type.HARMFUL)
                    effects.accept(user, AugmentConsumer.Type.BENEFICIAL)
                    if (user is ServerPlayerEntity){
                        (world as ServerWorld).spawnParticles(ParticleTypes.LARGE_SMOKE,target.x,target.eyeY,target.z,50,1.0,1.0,1.0,0.1)
                    }
                    world.playSound(null, target.blockPos, soundEvent(), SoundCategory.PLAYERS, 1.0F, 1.0F)
                }
                bl
            } else {
                false
            }
        } else {
            false
        }
    }

    override fun soundEvent(): SoundEvent {
        return RegisterSound.EXECUTE
    }
}
