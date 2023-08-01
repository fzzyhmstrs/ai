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
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.modifier.addLang
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.ContextData
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.effects.ModifiableEffects
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.effects.ModifiableEffects.getRndEntityList
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import me.fzzyhmstrs.fzzy_core.raycaster_util.RaycasterUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.boss.WitherEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.mob.Monster
import net.minecraft.entity.passive.VillagerEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/*
    Checklist
    - Build description for
        - Unique combinations
        - add Lang
    - special names for uniques
    - onPaired to grant relevant adv.
    - implement all special combinations
    - fill up interaction methods
        - onEntityHit?
        - onEntityKill?
        - onBlockHit?
        - Remember to call and check results of the super for the "default" behavior
    - modify stats. don't forget mana cost and cooldown!
        - modifyDealtDamage for unique interactions
    - modify other things
        - summon?
        - projectile?
        - explosion?
        - drops?
        - count? (affects some things like summon count and projectile count)
     */

class BedazzleAugment: EntityAoeAugment(ScepterTier.TWO, false) {
    
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("bedazzle"), SpellType.WIT,1500,85,
            7,1, 1,40, LoreTier.LOW_TIER, Items.DIAMOND)

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withRange(13.0,1.0)
            .withDuration(1200,0,0)

    override fun <T> applyTasks(
        world: World,
        context: ProcessContext,
        user: T,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        spells: PairedAugments
    )
    :
    SpellActionResult
    where
    T : SpellCastingEntity,
    T : LivingEntity
    {
        val onCastResults = spells.processOnCast(context,world,null,user, hand, level, effects)
        if (!onCastResults.success()) return  FAIL
        if (onCastResults.overwrite()) return onCastResults
        val entityList = RaycasterUtil.raycastEntityArea(effects.range(level), user)
        var hostileEntities: MutableList<LivingEntity> = mutableListOf()
        for (entity2 in entityList){
            if (entity2 is Monster && entity2 !is WitherEntity && entity2 is LivingEntity){
                hostileEntities.add(entity2)
            }
        }
        hostileEntities = getRndEntityList(world,hostileEntities.toMutableList(),level)
        val merchantEntities: MutableList<LivingEntity> = mutableListOf()
        for (entity2 in entityList){
            if (entity2 is VillagerEntity){
                merchantEntities.add(entity2)
            }
        }
        if (entityList.isEmpty()) return FAIL
        val list: MutableList<Identifier> = mutableListOf()
        if (hostileEntities.isNotEmpty())
            list.addAll(spells.processMultipleEntityHits(hostileEntities.stream().map { EntityHitResult(it) }.toList(),context,world,null,user, hand, level, effects))
        if (merchantEntities.isNotEmpty()){
            context.set(ContextData.HERO,merchantEntities.size)
            list.addAll(spells.processSingleEntityHit(EntityHitResult(user),context,world,null,user, hand, level, effects))
        }
        list.addAll(onCastResults.results())
        return if (list.isEmpty()) {
            FAIL
        } else {
            castSoundEvent(world,user.blockPos,context)
            SpellActionResult.success(list)
        }
    }

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        if (othersType.has(AugmentType.DAMAGE)) {
            val tier = other.getTier()
            description.addLang("enchantment.amethyst_imbuement.bedazzle.desc.damage", arrayOf(tier), SpellAdvancementChecks.STUNS)
        }
        if (othersType.positiveEffect) {
            description.addLang("enchantment.amethyst_imbuement.bedazzle.desc.beneficial", SpellAdvancementChecks.STUNS)
        } else if (othersType.has(AugmentType.SUMMONS)) {
            description.addLang("enchantment.amethyst_imbuement.bedazzle.desc.summons", SpellAdvancementChecks.SUMMONS)
        }
        when(other) {
            RegisterEnchantment.PERSUADE ->
                description.addLang("enchantment.amethyst_imbuement.bedazzle.persuade.desc", SpellAdvancementChecks.UNIQUE)
        }
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideVerb(this))
    }

    override fun specialName(otherSpell: ScepterAugment): MutableText {
        return when(otherSpell) {
            RegisterEnchantment.PERSUADE ->
                AcText.translatable("enchantment.amethyst_imbuement.bedazzle.persuade")
            else ->
                return super.specialName(otherSpell)
        }
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        if (pair.spellsAreEqual()){
            SpellAdvancementChecks.grant(player,SpellAdvancementChecks.DOUBLE_TRIGGER)
        }
        if (pair.spellsAreUnique()){
            SpellAdvancementChecks.grant(player,SpellAdvancementChecks.UNIQUE_TRIGGER)
        }
    }

    override fun modifyManaCost(
        manaCost: PerLvlI,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlI {
        if (spells.spellsAreEqual())
            return manaCost.plus(0,0,25)
        return manaCost
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
        val heroism = context.get(ContextData.HERO)
        if (heroism > 0){
            user.addStatusEffect(StatusEffectInstance(StatusEffects.HERO_OF_THE_VILLAGE,effects.duration(level) * heroism))
            return SpellActionResult.success(AugmentHelper.APPLIED_POSITIVE_EFFECTS)
        } else if (othersType.positiveEffect){
            val entity = entityHitResult.entity
            if (entity is PlayerEntity){
                entity.addStatusEffect(StatusEffectInstance(StatusEffects.HERO_OF_THE_VILLAGE,effects.duration(level)))
                return SpellActionResult.success(AugmentHelper.APPLIED_POSITIVE_EFFECTS)
            }
        } else if (othersType.has(AugmentType.DAMAGE)){
            val tier = (spells.paired()?.getTier() ?: 1).toFloat()
            if (world.random.nextFloat() < (0.2f * tier)){
                val entity = entityHitResult.entity
                if (entity is Monster && entity !is WitherEntity && entity is LivingEntity) {
                    entity.addStatusEffect(StatusEffectInstance(RegisterStatus.CHARMED, 400))
                    return SpellActionResult.success(AugmentHelper.APPLIED_NEGATIVE_EFFECTS)
                }
            }
        }
        if (othersType.empty || spells.spellsAreEqual()) {
            val entity = entityHitResult.entity
            if (entity is Monster && entity !is WitherEntity && entity is MobEntity) {
                if (spells.spellsAreEqual()) {
                    PersuadeAugment.persuadeMob(entity, world, effects, level)
                } else {
                    entity.addStatusEffect(StatusEffectInstance(RegisterStatus.CHARMED, effects.duration(level) / 2))
                }
                return SpellActionResult.success(AugmentHelper.APPLIED_NEGATIVE_EFFECTS)
            }
        }
        return SUCCESSFUL_PASS
    }

    override fun filter(list: List<Entity>, user: LivingEntity): MutableList<EntityHitResult> {
        val list1 = list.stream().filter { it is VillagerEntity }.map { EntityHitResult(it) } .toList()
        val list2 = hostileFilter(list, user)
        list2.addAll(list1)
        return list2
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
        for (summon in summons){
            summon.addEffect(ModifiableEffectEntity.TICK, ModifiableEffects.SHOCKING_EFFECT)
            summon.addEffect(ModifiableEffectEntity.ON_REMOVED, ModifiableEffects.SHOCKING_EFFECT)
        }
        return summons
    }

    override fun castParticleType(): ParticleEffect? {
        return ParticleTypes.HAPPY_VILLAGER
    }

    override fun hitParticleType(hit: HitResult): ParticleEffect? {
        return ParticleTypes.HAPPY_VILLAGER
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos, SoundEvents.EVENT_RAID_HORN.value(), SoundCategory.PLAYERS,1.0f,1.0f)
    }

    override fun hitSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS,0.4f,0.8f + world.random.nextFloat() * 0.4f)
    }


}
