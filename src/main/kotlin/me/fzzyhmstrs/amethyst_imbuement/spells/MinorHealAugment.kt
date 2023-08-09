package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.SpellActionResult
import me.fzzyhmstrs.amethyst_core.augments.base.SingleTargetOrSelfAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.modifier.addLang
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellHelper
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.item.Items
import net.minecraft.particle.DustParticleEffect
import net.minecraft.particle.ParticleEffect
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class MinorHealAugment: SingleTargetOrSelfAugment(ScepterTier.ONE){
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(
            AI.identity("minor_heal"),SpellType.GRACE, PerLvlI(130,-10),12,
            1,6,1,5,LoreTier.LOW_TIER, Items.GLISTERING_MELON_SLICE)

    //ml 6
    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDamage(2.5F,0.5F,0.0F)

    override fun <T> canTarget(entityHitResult: EntityHitResult, context: ProcessContext, world: World, user: T, hand: Hand, spells: PairedAugments)
            : Boolean where T : SpellCastingEntity, T : LivingEntity {
        return SpellHelper.friendlyTarget(entityHitResult.entity,user,this)
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
        if ((othersType.empty || spells.spellsAreEqual()) && entity is LivingEntity) {
            if (entity.health < entity.maxHealth){
                entity.heal(effects.damage(level))
                return SpellActionResult.success(AugmentHelper.APPLIED_POSITIVE_EFFECTS)
            } else {
                return FAIL
            }
        }
        return SUCCESSFUL_PASS
    }

    override fun castParticleType(): ParticleEffect? {
        return DustParticleEffect.DEFAULT
    }
    
    override fun hitParticleType(hit: HitResult): ParticleEffect? {
        return DustParticleEffect.DEFAULT
    }
    
    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos, SoundEvents.BLOCK_BEACON_ACTIVATE,SoundCategory.PLAYERS,1f,1f)
    }
}
