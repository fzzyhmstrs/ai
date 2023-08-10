package me.fzzyhmstrs.amethyst_imbuement.spells

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
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.ApplyTaskAugmentData
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellHelper
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import me.fzzyhmstrs.fzzy_core.coding_util.PersistentEffectHelper
import me.fzzyhmstrs.fzzy_core.raycaster_util.RaycasterUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.world.World

class HealingWindAugment: EntityAoeAugment(ScepterTier.THREE,true), PersistentEffectHelper.PersistentEffect{
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("healing_wind"),SpellType.GRACE,1200,240,
            24,11,1,65, LoreTier.HIGH_TIER, RegisterItem.DAZZLING_MELON_SLICE)

    //ml 11
    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withAmplifier(1)
            .withDuration(360,40)
            .withDamage(1.9f,0.1f)
            .withRange(11.5,0.5)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        description.addLang("amethyst_imbuement.todo")
    }

    override fun filter(list: List<Entity>, user: LivingEntity): MutableList<EntityHitResult> {
        return SpellHelper.friendlyFilter(list, user, this)
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideAdjective(this))
    }

    override fun specialName(otherSpell: ScepterAugment): MutableText {
        return super.specialName(otherSpell)
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        SpellAdvancementChecks.uniqueOrDouble(player, pair)
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.DURATION_TRIGGER)
    }

    override fun <T> applyTasks(
        world: World,
        context: ProcessContext,
        user: T,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        spells: PairedAugments
    ): SpellActionResult where T : SpellCastingEntity, T : LivingEntity {
        val onCastResults = spells.processOnCast(context,world,null,user, hand, level, effects)
        if (!onCastResults.success()) return  FAIL
        if (onCastResults.overwrite()) return onCastResults
        val results = applyWindEffects(world, context, user, hand, level, effects, spells)
        if (!results.success() && !onCastResults.acted()) return FAIL
        val data = ApplyTaskAugmentData(world, context, user, hand, level, effects, spells)
        PersistentEffectHelper.setPersistentTickerNeed(this,delay.value(level), effects.duration(level),data)
        return results.withResults(onCastResults.results())
    }

    private fun<T> applyWindEffects(
        world: World,
        context: ProcessContext,
        user: T,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        spells: PairedAugments
    ): SpellActionResult where T : SpellCastingEntity, T : LivingEntity{
        val entityList = RaycasterUtil.raycastEntityArea(effects.range(level), user)
        val filteredList = filter(entityList,user)
        if (filteredList.isEmpty()) return FAIL
        val list = spells.processMultipleEntityHits(filteredList,context,world,null,user, hand, level, effects)
        return if (list.isEmpty()) {
            FAIL
        } else {
            if (world is ServerWorld){
                world.spawnParticles(ParticleTypes.HAPPY_VILLAGER,user.x,user.getBodyY(0.5),user.z,250,effects.range(level),0.8,effects.range(level),0.0)
            }
            spells.castSoundEvents(world,user.blockPos,context)
            SpellActionResult.success(list)
        }
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
            val entity = entityHitResult.entity
            if (entity !is LivingEntity) return FAIL
            if (entity.health == entity.maxHealth) return FAIL
            entity.heal(effects.damage(level))
        }
        return SUCCESSFUL_PASS
    }

    override val delay: PerLvlI
        get() = PerLvlI(36,-1)

    override fun persistentEffect(data: PersistentEffectHelper.PersistentEffectData) {
        if (data !is ApplyTaskAugmentData<*>) return
        if (data.user !is LivingEntity) return
        applyWindEffects(data.world,data.context,data.user,data.hand,data.level,data.effects,data.spells)
        val world = data.world
        if (world is ServerWorld){
            world.spawnParticles(ParticleTypes.HAPPY_VILLAGER,data.user.x,data.user.getBodyY(0.5),data.user.z,100,data.effects.range(data.level),0.8,data.effects.range(data.level),0.0)
        }
    }

}
