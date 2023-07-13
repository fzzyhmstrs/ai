package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.SpellActionResult
import me.fzzyhmstrs.amethyst_core.augments.base.PlaceItemAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.ExplosionBuilder
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.entity.ModifiableEffectEntity
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.modifier.addLang
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.augment.DraconicVisionAugment
import me.fzzyhmstrs.amethyst_imbuement.block.ShineLightBlock
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBlock
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.ContextData
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.PlaceBlockAugment
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks.or
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.boosts.DyeBoost
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.explosion_behaviors.GlowingExplosionBehavior
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import me.fzzyhmstrs.fzzy_core.trinket_util.EffectQueue
import net.minecraft.block.BlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.*
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.registry.tag.FluidTags
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*
import kotlin.math.min

/*
Checklist
*/

class ShineAugment: PlaceBlockAugment(ScepterTier.ONE) {
    override val augmentData: AugmentDatapoint
        get() = AugmentDatapoint(
            AI.identity("shine"),SpellType.WIT,10,2,
            1,1,1,1,LoreTier.NO_TIER,RegisterBlock.SHINE_LIGHT.asItem())

    //ml 1
    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withRange(4.5)

    override fun getBlockStateToPlace(context: ProcessContext, world: World, pos: BlockPos, spells: PairedAugments): BlockState {
        val boost = spells.boost()
        val block = if (boost is DyeBoost){
            blocks[boost.getDyeColor()]?:RegisterBlock.SHINE_LIGHT
        } else {
            RegisterBlock.SHINE_LIGHT
        }
        val fluid = world.getFluidState(pos)
        return block.getShineState(fluid.isIn(FluidTags.WATER))
    }

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        if (!othersType.has(AugmentType.ENTITY) || other == RegisterEnchantment.EXCAVATE || other == RegisterEnchantment.SURVEY) {
            description.addLang("enchantment.amethyst_imbuement.shine.desc.cooldown", SpellAdvancementChecks.COOLDOWN)
        } else {
            description.addLang("enchantment.amethyst_imbuement.shine.desc.entity", SpellAdvancementChecks.ENTITY_EFFECT)
        }
        if (other is PlaceItemAugment) {
            description.addLang("enchantment.amethyst_imbuement.shine.desc.block", arrayOf(other.item(),itemAfterShineTransform(other.item())), SpellAdvancementChecks.BLOCK)
        }
        when(other) {
            RegisterEnchantment.EXCAVATE -> {
                description.addLang("enchantment.amethyst_imbuement.shine.excavate.desc", SpellAdvancementChecks.UNIQUE.or(SpellAdvancementChecks.BLOCK))
                description.addLang("enchantment.amethyst_imbuement.shine.desc.manaCost", SpellAdvancementChecks.MANA_COST)
            }
            RegisterEnchantment.SURVEY -> {
                description.addLang("enchantment.amethyst_imbuement.shine.survey.desc", SpellAdvancementChecks.UNIQUE.or(SpellAdvancementChecks.BLOCK))
                description.addLang("enchantment.amethyst_imbuement.shine.desc.manaCost", SpellAdvancementChecks.MANA_COST)
            }
        }
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideNoun(this))
    }

    override fun specialName(otherSpell: ScepterAugment): MutableText {
        return when(otherSpell) {
            RegisterEnchantment.EXCAVATE ->
                AcText.translatable("enchantment.amethyst_imbuement.shine.excavate")
            RegisterEnchantment.SURVEY ->
                AcText.translatable("enchantment.amethyst_imbuement.shine.survey")
            else ->
                return super.specialName(otherSpell)
        }
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        if (pair.spellsAreEqual()){
            SpellAdvancementChecks.grant(player, SpellAdvancementChecks.DOUBLE_TRIGGER)
        }
        if (pair.spellsAreUnique()){
            SpellAdvancementChecks.grant(player, SpellAdvancementChecks.UNIQUE_TRIGGER)
            SpellAdvancementChecks.grant(player, SpellAdvancementChecks.MANA_COST_TRIGGER)
        }
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.ENTITY_EFFECT_TRIGGER)
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.BLOCK_TRIGGER)
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.COOLDOWN_TRIGGER)
    }

    override fun modifyCooldown(
        cooldown: PerLvlI,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlI {
        if (!othersType.has(AugmentType.ENTITY) || other == RegisterEnchantment.EXCAVATE || other == RegisterEnchantment.SURVEY){
            return cooldown.plus(0,0,25)
        }
        return cooldown
    }

    override fun modifyManaCost(
        manaCost: PerLvlI,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlI {
        if (other == RegisterEnchantment.EXCAVATE){
            return manaCost.plus(24)
        }
        if (other == RegisterEnchantment.SURVEY){
            return manaCost.plus(480)
        }
        return super.modifyManaCost(manaCost, other, othersType, spells)
    }

    override fun <T> onCast(
        context: ProcessContext,
        world: World,
        source: Entity?,
        user: T,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        othersType: AugmentType,
        spells: PairedAugments
    )
    :
    SpellActionResult
    where
    T : SpellCastingEntity,
    T : LivingEntity
    {
        if (spells.primary() == RegisterEnchantment.SURVEY){
            val pos = user.blockPos
            var hits = 0
            DraconicVisionAugment.findAndCreateBoxes(world,pos,16) { hits++ }
            EffectQueue.addStatusToQueue(user, RegisterStatus.DRACONIC_VISION,260,0)
            context.set(ContextData.DRACONIC_BOXES,hits)
            spells.castSoundEvents(world,pos,context)
            return SpellActionResult.overwrite(AugmentHelper.BLOCK_HIT, AugmentHelper.APPLIED_POSITIVE_EFFECTS)
        }
        return super.onCast(context, world, source, user, hand, level, effects, othersType, spells)
    }

    override fun <T> onEntityHit(
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
    )
    :
    SpellActionResult
    where
    T : SpellCastingEntity,
    T : LivingEntity
    {
        if (othersType.has(AugmentType.ENTITY)){
            val entity = entityHitResult.entity
            if (entity is LivingEntity){
                entity.addStatusEffect(StatusEffectInstance(StatusEffects.GLOWING,600))
                return SpellActionResult.success(AugmentHelper.APPLIED_POSITIVE_EFFECTS)
            }
        }
        return SUCCESSFUL_PASS
    }

    override fun <T> onBlockHit(
        blockHitResult: BlockHitResult,
        context: ProcessContext,
        world: World,
        source: Entity?,
        user: T,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        othersType: AugmentType,
        spells: PairedAugments
    )
            :
            SpellActionResult
            where
            T : SpellCastingEntity,
            T : LivingEntity
    {
        val result = super.onBlockHit(blockHitResult, context, world, source, user, hand, level, effects, othersType, spells)
        if (result.acted() || !result.success())
            return result
        val other = spells.primary()
        if (!othersType.empty && other is PlaceItemAugment) {
            val item = itemAfterShineTransform(other.item())
            if (user !is ServerPlayerEntity) return FAIL
            when (item) {
                is BlockItem -> {
                    val stack = ItemStack(item)
                    if (!item.place(ItemPlacementContext(user, hand, stack, blockHitResult)).isAccepted) return FAIL
                    spells.hitSoundEvents(world, blockHitResult.blockPos,context)
                    return SpellActionResult.overwrite(AugmentHelper.BLOCK_PLACED)
                }
                is BucketItem -> {
                    if (!item.placeFluid(user,world,blockHitResult.blockPos,blockHitResult)) return FAIL
                    spells.hitSoundEvents(world, blockHitResult.blockPos, context)
                    return SpellActionResult.overwrite(AugmentHelper.BLOCK_PLACED)
                }
                else -> {
                    return SUCCESSFUL_PASS
                }
            }
        }
        if (other == RegisterEnchantment.EXCAVATE){
            val startPos = blockHitResult.blockPos
            var hits = 0
            DraconicVisionAugment.findAndCreateBoxes(world, startPos,4) { hits++ }
            context.set(ContextData.DRACONIC_BOXES,hits)
            return SpellActionResult.success(AugmentHelper.BLOCK_HIT)
        }
        return SUCCESSFUL_PASS
    }

    override fun <T, U> modifySummons(summons: List<T>, context: ProcessContext, user: U, world: World, hand: Hand, level: Int, effects: AugmentEffect, othersType: AugmentType, spells: PairedAugments)
    :
    List<T>
    where
    T : ModifiableEffectEntity,
    T : Entity,
    U : SpellCastingEntity,
    U : LivingEntity
    {
        for (summon in summons){
            if (summon is LivingEntity){
                summon.addStatusEffect(StatusEffectInstance(StatusEffects.GLOWING,6000))
            }
        }
        return summons
    }

    override fun <T> modifyExplosion(builder: ExplosionBuilder, context: ProcessContext, user: T, world: World, hand: Hand, level: Int, effects: AugmentEffect, othersType: AugmentType, spells: PairedAugments)
    :
    ExplosionBuilder
    where
    T : SpellCastingEntity,
    T : LivingEntity
    {
        return builder.withCustomBehavior(GlowingExplosionBehavior())
    }

    private fun itemAfterShineTransform(item: Item): Item{
        return items[item]?:item
    }

    override fun hitParticleType(hit: HitResult): ParticleEffect? {
        return ParticleTypes.END_ROD
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        val hits = context.get(ContextData.DRACONIC_BOXES)
        if (hits > 0) {
            val volume = min(hits, 10) / 30f
            world.playSound(null, blockPos, SoundEvents.BLOCK_CONDUIT_AMBIENT_SHORT, SoundCategory.NEUTRAL, 0.3f, 0.8f)
        }
    }

    override fun hitSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        val hits = context.get(ContextData.DRACONIC_BOXES)
        if (hits > 0){
            val volume = min(hits,10)/30f
            world.playSound(null,blockPos, SoundEvents.BLOCK_CONDUIT_AMBIENT_SHORT, SoundCategory.BLOCKS,volume,0.8f)
            return
        }
        super.hitSoundEvent(world, blockPos, context)
    }

    companion object{
        val blocks: EnumMap<DyeColor, ShineLightBlock> = EnumMap(mapOf(
            DyeColor.WHITE to RegisterBlock.SHINE_LIGHT,
            DyeColor.LIGHT_GRAY to RegisterBlock.SHINE_LIGHT_LIGHT_GRAY,
            DyeColor.GRAY to RegisterBlock.SHINE_LIGHT_GRAY,
            DyeColor.BLACK to RegisterBlock.SHINE_LIGHT_BLACK,
            DyeColor.BROWN to RegisterBlock.SHINE_LIGHT_BROWN,
            DyeColor.RED to RegisterBlock.SHINE_LIGHT_RED,
            DyeColor.ORANGE to RegisterBlock.SHINE_LIGHT_ORANGE,
            DyeColor.YELLOW to RegisterBlock.SHINE_LIGHT_YELLOW,
            DyeColor.LIME to RegisterBlock.SHINE_LIGHT_LIME,
            DyeColor.GREEN to RegisterBlock.SHINE_LIGHT_GREEN,
            DyeColor.CYAN to RegisterBlock.SHINE_LIGHT_CYAN,
            DyeColor.LIGHT_BLUE to RegisterBlock.SHINE_LIGHT_LIGHT_BLUE,
            DyeColor.BLUE to RegisterBlock.SHINE_LIGHT_BLUE,
            DyeColor.PURPLE to RegisterBlock.SHINE_LIGHT_PURPLE,
            DyeColor.MAGENTA to RegisterBlock.SHINE_LIGHT_MAGENTA,
            DyeColor.PINK to RegisterBlock.SHINE_LIGHT_PINK
        ))

        val items: Map<Item,Item> = mapOf(
            Items.WATER_BUCKET to Items.SEA_LANTERN,
            Items.LAVA_BUCKET to Items.GLOWSTONE,
            Items.SPONGE to Items.JACK_O_LANTERN
        )

    }
}