package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.SpellActionResult
import me.fzzyhmstrs.amethyst_core.augments.base.EntityAoeAugment
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
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellHelper.addStatus
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.mob.Monster
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class InspiringSongAugment: EntityAoeAugment(ScepterTier.TWO,true){
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("inspiring_song"),SpellType.GRACE,750,125,
            10,13,1,20,LoreTier.NO_TIER, Items.NOTE_BLOCK)

    //ml 13
    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDuration(175,125,0)
            .withAmplifier(0,0,0)
            .withRange(3.5,0.5,0.0)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        description.addLang("amethyst_imbuement.todo")
    }

    override fun filter(list: List<Entity>, user: LivingEntity): MutableList<EntityHitResult> {
        return friendlyFilter(list,user,this)
    }
    
    private fun friendlyFilter(list: List<Entity>, user: LivingEntity, spell: ScepterAugment): MutableList<EntityHitResult> {
        val friendlyEntityList: MutableList<EntityHitResult> = mutableListOf()
        if (list.isNotEmpty()) {
            for (entity in list) {
                if (entity !== user) {
                    if (entity is Monster || entity is PassiveEntity) continue
                    if (entity is SpellCastingEntity && !AiConfig.entities.isEntityPvpTeammate(user,entity,spell)) continue
                    friendlyEntityList.add(EntityHitResult(entity))
                }
            }
        }
        return friendlyEntityList
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
        if (othersType.empty){
            if (inspire(entityHitResult,level,effects)) {
                return SpellActionResult.success(AugmentHelper.APPLIED_POSITIVE_EFFECTS)
            } else{
                FAIL
            }
        }
        return SUCCESSFUL_PASS
    }
    
    private fun inspire(entityHitResult: EntityHitResult,level: Int, effect: AugmentEffect): Boolean{
        return if(level < 6){
            val bl = entityHitResult.addStatus(StatusEffects.SPEED, effect.duration(level), effect.amplifier(level))
            bl && entityHitResult.addStatus(StatusEffects.REGENERATION, effect.duration(3), effect.amplifier(level))
        } else if(level < 11){
            var bl = entityHitResult.addStatus(StatusEffects.SPEED, effect.duration(level), effect.amplifier(level))
            bl = bl && entityHitResult.addStatus(StatusEffects.JUMP_BOOST, effect.duration(level), effect.amplifier(level))
            bl && entityHitResult.addStatus(StatusEffects.REGENERATION, effect.duration(8), effect.amplifier(level))
        } else {
            var bl = entityHitResult.addStatus(StatusEffects.SPEED, effect.duration(level), effect.amplifier(level))
            bl = bl && entityHitResult.addStatus(StatusEffects.JUMP_BOOST, effect.duration(level), effect.amplifier(level))
            bl = bl && entityHitResult.addStatus(StatusEffects.REGENERATION, effect.duration(12), effect.amplifier(level))
            bl && entityHitResult.addStatus( RegisterStatus.INSPIRED, effect.duration(level), 0)
        }
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos,SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.PLAYERS,1f,1f)
    }
}
