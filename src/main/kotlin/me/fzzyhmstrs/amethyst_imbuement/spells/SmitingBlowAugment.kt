package me.fzzyhmstrs.amethyst_imbuement.spells

import com.ibm.icu.lang.UCharacter.GraphemeClusterBreak.T
import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.SpellActionResult
import me.fzzyhmstrs.amethyst_core.augments.base.SingleTargetAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.AIClient
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.ContextData
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellHelper
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityGroup
import net.minecraft.entity.LivingEntity
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class SmitingBlowAugment: SingleTargetAugment(ScepterTier.TWO) {
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("smiting_blow"),SpellType.FURY,20,8,
            10, 5,1,2, LoreTier.LOW_TIER, RegisterItem.GLOWING_FRAGMENT)

    //ml 5
    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withDamage(4.5F,0.5F)
            .withRange(7.5,0.5)
            .withAmplifier(2)

    override fun <T> canTarget(
        entityHitResult: EntityHitResult,
        context: ProcessContext,
        world: World,
        user: T,
        hand: Hand,
        spells: PairedAugments
    ): Boolean where T : SpellCastingEntity,T : LivingEntity {
        return SpellHelper.hostileTarget(entityHitResult.entity,user,this)
    }

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        TODO("Not yet implemented")
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideAdjective(this))
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        SpellAdvancementChecks.uniqueOrDouble(player, pair)
    }

    override fun <T> modifyDealtDamage(
        amount: Float,
        context: ProcessContext,
        entityHitResult: EntityHitResult,
        user: T,
        world: World,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        othersType: AugmentType,
        spells: PairedAugments
    ): Float where T : SpellCastingEntity, T : LivingEntity {
        val entity = entityHitResult.entity
        if (entity is LivingEntity && entity.group == EntityGroup.UNDEAD && !context.get(ContextData.CRIT))
            return amount * 1.5f
        return amount
    }

    override fun <T> entityEffects(
        entityHitResult: EntityHitResult,
        context: ProcessContext,
        world: World,
        source: Entity?,
        user: T,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        othersType: AugmentType,
        spells: PairedAugments
    ): SpellActionResult where T : SpellCastingEntity, T : LivingEntity {
        if (othersType.empty){
            if (!canTarget(entityHitResult, context, world, user, hand, spells)) return FAIL
            val damage = spells.provideDealtDamage(effects.damage(level),context, entityHitResult, user, world, hand, level, effects)
            val damageSource = spells.provideDamageSource(context, entityHitResult, source, user, world, hand, level, effects)
            val bl = entityHitResult.entity.damage(damageSource,damage)
            if (bl){
                val pos = entityHitResult.entity.eyePos
                splashParticles(entityHitResult,world,pos.x,pos.y + 0.2,pos.z,spells)
                splashParticles(entityHitResult,world,pos.x,pos.y,pos.z,spells)
                splashParticles(entityHitResult,world,pos.x,pos.y - 0.2,pos.z,spells)
                user.applyDamageEffects(user,entityHitResult.entity)
                if (entityHitResult.entity.isAlive) {
                    SpellActionResult.success(AugmentHelper.DAMAGED_MOB)
                } else {
                    spells.processOnKill(entityHitResult, context, world, source, user, hand, level, effects)
                    SpellActionResult.success(AugmentHelper.DAMAGED_MOB, AugmentHelper.KILLED_MOB)
                }
            }
        }
        return SUCCESSFUL_PASS
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
                val bl = if(target.isUndead) {
                    target.damage(user.damageSources.indirectMagic(user,user),effects.damage(level) * effects.amplifier(level))
                } else {
                    target.damage(user.damageSources.indirectMagic(user,user),effects.damage(level))
                }
                if (bl) {
                    effects.accept(target,AugmentConsumer.Type.HARMFUL)
                    effects.accept(user, AugmentConsumer.Type.BENEFICIAL)
                    if (user is ServerPlayerEntity){
                        sendParticles(target.pos, user)
                    }
                    generateParticles(world,target.pos)
                    world.playSound(null, target.blockPos, soundEvent(), SoundCategory.PLAYERS, 0.8F, 1.3F)
                }
                bl
            } else {
                false
            }
        } else {
            false
        }
    }

    override fun castParticleType(): ParticleEffect? {
        return ParticleTypes.SPIT
    }

    override fun hitParticleType(hit: HitResult): ParticleEffect? {
        return ParticleTypes.SPIT
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos,SoundEvents.ENTITY_ELDER_GUARDIAN_HURT,SoundCategory.PLAYERS,1f,1f)
    }
}
