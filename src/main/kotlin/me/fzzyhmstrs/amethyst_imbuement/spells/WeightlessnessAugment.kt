package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.base.SingleTargetOrSelfAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.modifier.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.mob.HostileEntity
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.world.World

class WeightlessnessAugment: SingleTargetOrSelfAugment(ScepterTier.TWO){
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("weightlessness"),SpellType.WIT, PerLvlI(725,-25),125,
            9,5,1,20, LoreTier.LOW_TIER, Items.PHANTOM_MEMBRANE)

    //ml 5
    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDuration(120,40)

    override fun <T> canTarget(entityHitResult: EntityHitResult, context: ProcessContext, world: World, user: T, hand: Hand, spells: PairedAugments)
            : Boolean where T : SpellCastingEntity, T : LivingEntity {
        return true
    }
        
    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        description.addLang("amethyst_imbuement.todo")
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideNoun(this))
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        SpellAdvancementChecks.uniqueOrDouble(player, pair)
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
        val entity = entityHitResult.entity
        if ((othersType.empty || spells.spellsAreEqual())) {
            val bl = entityHitResult.addStatus(StatusEffects.LEVITATION, effects.duration(level), 0)
            if (bl && entityHitResult.addStatus(StatusEffects.SLOW_FALLING, effects.duration(level+2), 0)){
                entity.heal(effects.damage(level))
                return SpellActionResult.success(AugmentHelper.APPLIED_NEGATIVE_EFFECTS)
            } else {
                return FAIL
            }
        }
        return SUCCESSFUL_PASS
    }

    override fun castParticleType(): ParticleEffect? {
        return ParticleTypes.END_ROD
    }
    
    override fun hitParticleType(hit: HitResult): ParticleEffect? {
        return ParticleTypes.END_ROD
    }
    
    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos,SoundEvents.ENTITY_SHULKER_BULLET_HIT,SoundCategory.PLAYERS,0.6F, 1.0F)
    }
}
