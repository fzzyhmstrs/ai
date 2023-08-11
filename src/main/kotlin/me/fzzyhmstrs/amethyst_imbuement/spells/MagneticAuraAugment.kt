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
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.ApplyTaskAugmentData
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.ContextData
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import me.fzzyhmstrs.fzzy_core.coding_util.PersistentEffectHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class MagneticAuraAugment: EntityAoeAugment(ScepterTier.TWO,true), PersistentEffectHelper.PersistentEffect{
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(
            AI.identity("magnetic_aura"),SpellType.GRACE,400,60,
            7,7,1,20, LoreTier.LOW_TIER, RegisterItem.PYRITE)

    //ml 7
    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withDuration(120,40)
            .withRange(4.5,0.5)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        description.addLang("amethyst_imbuement.todo")
    }

    override fun filter(list: List<Entity>, user: LivingEntity): MutableList<EntityHitResult> {
        return list.stream().filter { it is ItemEntity } .map { EntityHitResult(it) }.toList()
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideNoun(this))
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        SpellAdvancementChecks.uniqueOrDouble(player, pair)
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
        val result = super.applyTasks(world, context, user, hand, level, effects, spells)
        if (!result.success()) return FAIL
        if (!context.get(ContextData.PERSISTENT)) {
            context.set(ContextData.PERSISTENT,true)
            val data = ApplyTaskAugmentData(world, context, user, hand, level, effects, spells)
            PersistentEffectHelper.setPersistentTickerNeed(this,delay.value(level),effects.duration(level),data)
        }
        return result
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
            if (entity !is ItemEntity) return FAIL
            if (user !is PlayerEntity) return FAIL
            val stack = entity.stack
            user.inventory.offerOrDrop(stack)
            entity.discard()
            return SpellActionResult.success(AugmentHelper.DRY_FIRED)
        }
        return SUCCESSFUL_PASS
    }

    override val delay: PerLvlI
        get() = PerLvlI(10)

    @Suppress("KotlinConstantConditions")
    override fun persistentEffect(data: PersistentEffectHelper.PersistentEffectData) {
        if (data !is ApplyTaskAugmentData<*>) return
        if (data.user !is LivingEntity) return
        this.applyTasks(data.world,data.context,data.user,data.hand,data.level,data.effects,data.spells)
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos,SoundEvents.ENTITY_WANDERING_TRADER_REAPPEARED,SoundCategory.PLAYERS,1f,1f)
    }
}
