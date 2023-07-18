package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.SpellActionResult
import me.fzzyhmstrs.amethyst_core.augments.base.EntityAoeAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.entity.ModifiableEffectEntity
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.item.ScepterLike
import me.fzzyhmstrs.amethyst_core.item.SpellCasting
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.modifier.addLang
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.living.DraftHorseEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterBoost
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks.or
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellHelper
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlD
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*

/*
    Checklist
    - implement all special combinations
     */

class AnimalHusbandryAugment: EntityAoeAugment(ScepterTier.TWO,true) {

    private val uuid = UUID.fromString("be9acea0-2112-11ee-be56-0242ac120002")

    override val augmentData: AugmentDatapoint= 
        AugmentDatapoint(AI.identity("animal_husbandry"),SpellType.GRACE, 80,16,
            7,4,1,4, LoreTier.NO_TIER, Items.HAY_BLOCK)

    //ml 4
    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withRange(6.0,0.5)
            .withAmplifier(2,1)

    override fun <T> canTarget(entityHitResult: EntityHitResult, context: ProcessContext, world: World, user: T, hand: Hand, spells: PairedAugments)
            : Boolean where T : SpellCastingEntity, T : LivingEntity {
        return SpellHelper.friendlyTarget(entityHitResult.entity,user,this)
    }

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        description.addLang("enchantment.amethyst_imbuement.animal_husbandry.desc.manaCost", SpellAdvancementChecks.STAT)
        if (othersType.has(AugmentType.BENEFICIAL))
            description.addLang("enchantment.amethyst_imbuement.animal_husbandry.desc.range", SpellAdvancementChecks.RANGE)
        if (othersType.has(AugmentType.DAMAGE))
            description.addLang("enchantment.amethyst_imbuement.animal_husbandry.desc.damage", SpellAdvancementChecks.DAMAGE)
        if (othersType.has(AugmentType.SUMMONS))
            description.addLang("enchantment.amethyst_imbuement.animal_husbandry.desc.summons", SpellAdvancementChecks.SUMMONS.or(SpellAdvancementChecks.HEALTH))
        if (othersType == AugmentType.AOE_POSITIVE)
            description.addLang("enchantment.amethyst_imbuement.animal_husbandry.desc.aoe_positive", SpellAdvancementChecks.SUMMONS.or(SpellAdvancementChecks.HEALTH))
        if (othersType.has(SpellHelper.CHICKEN))
            description.addLang("enchantment.amethyst_imbuement.animal_husbandry.desc.chicken", SpellAdvancementChecks.SUMMONS.or(SpellAdvancementChecks.CHICKEN).or(SpellAdvancementChecks.SHOOT_ITEM))
        when(other) {
            RegisterEnchantment.SUMMON_SEAHORSE ->
                description.addLang("enchantment.amethyst_imbuement.animal_husbandry.summon_seahorse.desc", SpellAdvancementChecks.UNIQUE.or(SpellAdvancementChecks.SUMMONS))
            RegisterEnchantment.PERSUADE ->
                description.addLang("enchantment.amethyst_imbuement.animal_husbandry.persuade.desc", SpellAdvancementChecks.UNIQUE.or(SpellAdvancementChecks.STUNS))
            RegisterEnchantment.SUMMON_CHICKEN ->
                description.addLang("enchantment.amethyst_imbuement.animal_husbandry.summon_chicken.desc", SpellAdvancementChecks.AMPLIFIER.or(SpellAdvancementChecks.CHICKEN))
        }
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideNoun(this))
    }

    override fun specialName(otherSpell: ScepterAugment): MutableText {
        when(otherSpell) {
            RegisterEnchantment.SUMMON_SEAHORSE ->
                AcText.translatable("enchantment.amethyst_imbuement.animal_husbandry.summon_seahorse")
            RegisterEnchantment.PERSUADE ->
                AcText.translatable("enchantment.amethyst_imbuement.animal_husbandry.persuade")
        }
        return super.specialName(otherSpell)
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        if (pair.spellsAreEqual()){
            SpellAdvancementChecks.grant(player, SpellAdvancementChecks.DOUBLE_TRIGGER)
        }
        if (pair.spellsAreUnique()){
            SpellAdvancementChecks.grant(player, SpellAdvancementChecks.UNIQUE_TRIGGER)
        }
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.STAT_TRIGGER)
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.RANGE_TRIGGER)
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.HEALTH_TRIGGER)
        SpellAdvancementChecks.grant(player,SpellAdvancementChecks.ENTITY_EFFECT_TRIGGER)
    }

    override fun modifyManaCost(
        manaCost: PerLvlI,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlI {
        return manaCost.plus(0,0,5)
    }

    override fun modifyCooldown(
        cooldown: PerLvlI,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlI {
        if (othersType.has(SpellHelper.CHICKEN)){
            return cooldown.plus(0,0,-33)
        }
        return cooldown
    }

    override fun modifyAmplifier(
        amplifier: PerLvlI,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlI {
        if (spells.spellsAreEqual())
            return amplifier.plus(0,1)
        return amplifier
    }

    override fun modifyRange(
        range: PerLvlD,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlD {
        if (spells.spellsAreEqual())
            return range.plus(0.0,0.0,100.0)
        if (othersType.has(AugmentType.BENEFICIAL)){
            return range.plus(0.0,0.0,25.0)
        }

        return range
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
    )
    :
    Float
    where
    T : SpellCastingEntity,
    T : LivingEntity
    {
        if (entityHitResult.entity is AnimalEntity){
            return amount * 4f
        }
        return amount
    }

    override fun <T> modifyCount(
        start: Int,
        context: ProcessContext,
        user: T,
        world: World,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        othersType: AugmentType,
        spells: PairedAugments
    ): Int where T : SpellCastingEntity,T : LivingEntity  {
        if (spells.primary() == RegisterEnchantment.SUMMON_CHICKEN)
            return start * 3
        return start
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
    ): SpellActionResult where T : SpellCastingEntity,T : LivingEntity {
        if (spells.primary() == RegisterEnchantment.SUMMON_SEAHORSE){
            val scepter = user.getStackInHand(hand)
            if (scepter.item is ScepterLike && scepter.item is SpellCasting){
                val nbt = scepter.nbt
                if (nbt != null){
                    if (nbt.contains("current_draft_horse") && world is ServerWorld){
                        val draftHorse = world.getEntity(nbt.getUuid("current_draft_horse"))
                        val chorseNbt = NbtCompound()
                        draftHorse?.saveSelfNbt(chorseNbt) ?: return SUCCESSFUL_PASS
                        nbt.put("stored_draft_horse",chorseNbt)
                        draftHorse.discard()
                        nbt.remove("current_draft_horse")
                        context.set(ProcessContext.COOLDOWN,200)
                        return SpellActionResult.overwrite(AugmentHelper.DRY_FIRED)
                    }
                }
            }
        }
        return SUCCESSFUL_PASS
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
        val result = super.onEntityHit(entityHitResult, context, world, source, user, hand, level, effects, othersType, spells)
        if (result.acted() || !result.success())
            return result
        if (othersType == AugmentType.AOE_POSITIVE){
            val entity = entityHitResult.entity
            if (entity is LivingEntity){
                val hp = entity.maxHealth
                val boost = spells.boost()
                val hpBoost = if (boost == RegisterBoost.ENCHANTED_GOLDEN_APPLE_BOOST) hp/8.0 else hp/12.0
                val modifier = EntityAttributeModifier(
                    uuid,
                    "Animal Husbandry Bonus",
                    hpBoost,
                    EntityAttributeModifier.Operation.ADDITION)
                if (entity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)?.hasModifier(modifier) == true) return SUCCESSFUL_PASS
                entity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)?.addTemporaryModifier(modifier)
                return SpellActionResult.success(AugmentHelper.APPLIED_POSITIVE_EFFECTS)
            }

        }

        return SUCCESSFUL_PASS
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
    )
    :
    SpellActionResult
    where
    T : SpellCastingEntity,
    T : LivingEntity
    {
        if (othersType.empty) {
            val entity = entityHitResult.entity
            if (entity is AnimalEntity) {
                if (entity.isBaby) {
                    val i = entity.breedingAge
                    entity.growUp(AnimalEntity.toGrowUpAge(-i), true)
                    if (entity.health < entity.maxHealth) {
                        entity.heal(0.5f)
                    }
                    return SpellActionResult.success(AugmentHelper.APPLIED_POSITIVE_EFFECTS)
                } else {
                    if (entity.canEat() && entity.breedingAge == 0) {
                        entity.lovePlayer(user as? PlayerEntity)
                        if (entity.health < entity.maxHealth) {
                            entity.heal(0.5f)
                        }
                        return SpellActionResult.success(AugmentHelper.APPLIED_POSITIVE_EFFECTS)
                    }
                }
            }
        }
        return SUCCESSFUL_PASS
    }

    override fun <T, U> modifySummons(
        summons: List<T>,
        hit: HitResult,
        context: ProcessContext,
        user: U,
        world: World,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        othersType: AugmentType,
        spells: PairedAugments
    )
    :
    List<Entity>
    where
    T : ModifiableEffectEntity,
    T : Entity,
    U : SpellCastingEntity,
    U : LivingEntity
    {

        if (spells.primary() == RegisterEnchantment.SUMMON_SEAHORSE){
            val scepter = user.getStackInHand(hand)
            var draftHorseLoaded: Entity? = null
            if (scepter.item is ScepterLike && scepter.item is SpellCasting){
                val nbt = scepter.nbt
                if (nbt != null){
                    if (nbt.contains("stored_draft_horse")){
                        val storedChorse = nbt.getCompound("stored_draft_horse")
                        draftHorseLoaded = EntityType.loadEntityWithPassengers(storedChorse,world) { entity -> entity}
                        nbt.remove("stored_draft_horse")
                    }
                }
            }
            val chorse = if(draftHorseLoaded is DraftHorseEntity) {
                draftHorseLoaded
            } else {
                RegisterEntity.DRAFT_HORSE_ENTITY.create(world) ?: return summons
            }
            val found = AugmentHelper.findSpawnPos(world, BlockPos.ofFloored(hit.pos), chorse, 3, tries = 16)
            if (!found){
                return listOf()
            }
            chorse.setPlayerHorseOwner(user)
            chorse.passEffects(spells,effects,level)
            chorse.passContext(context)
            if (scepter.item is ScepterLike && scepter.item is SpellCasting){
                scepter.orCreateNbt.putUuid("current_draft_horse",chorse.uuid)
            }
            return listOf(chorse)



        }

        for (summon in summons){
            if (summon is LivingEntity){
                val hp = summon.maxHealth
                val boost = spells.boost()
                val hpBoost = if (boost == RegisterBoost.ENCHANTED_GOLDEN_APPLE_BOOST) hp/5.0 else hp/8.0
                val modifier = EntityAttributeModifier(
                    uuid,
                    "Animal Husbandry Bonus",
                    hpBoost,
                    EntityAttributeModifier.Operation.ADDITION)
                if (summon.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)?.hasModifier(modifier) == true) continue
                summon.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)?.addPersistentModifier(modifier)
            }
        }
        return summons
    }

    override fun filter(list: List<Entity>, user: LivingEntity): MutableList<EntityHitResult> {
        return list.stream().filter { it is LivingEntity && it is AnimalEntity }.map { EntityHitResult(it) }.toList()
    }

    override fun castParticleType(): ParticleEffect? {
        return ParticleTypes.HAPPY_VILLAGER
    }

    override fun hitParticleType(hit: HitResult): ParticleEffect? {
        return if (hit is EntityHitResult && hit.entity is AnimalEntity) ParticleTypes.HAPPY_VILLAGER else null
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos,SoundEvents.BLOCK_GRASS_BREAK,SoundCategory.PLAYERS,1.0f,1.0f)
    }
}
