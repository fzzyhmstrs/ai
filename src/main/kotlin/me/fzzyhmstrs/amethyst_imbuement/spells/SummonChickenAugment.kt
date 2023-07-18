package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper.findSpawnPos
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.SpellActionResult
import me.fzzyhmstrs.amethyst_core.augments.base.SummonAugment.Companion.summonContext
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
import me.fzzyhmstrs.amethyst_imbuement.entity.living.ChorseEntity
import me.fzzyhmstrs.amethyst_imbuement.interfaces.ModifiableEffectMobOrPlayer
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks.or
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.effects.ModifiableEffects
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.raycaster_util.RaycasterUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.ItemScatterer
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

/*
    Checklist
     */

@Suppress("SpellCheckingInspection")
class SummonChickenAugment: ScepterAugment(ScepterTier.ONE, AugmentType.SUMMON_GOOD) {

    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("summon_chicken"),SpellType.GRACE,900,75,
            5,3,1,20,LoreTier.LOW_TIER, Items.EGG)

    override val baseEffect: AugmentEffect
        get() = super.baseEffect
    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        when(other) {
            RegisterEnchantment.SUMMON_SEAHORSE -> {
                description.addLang("enchantment.amethyst_imbuement.summon_chicken.summon_seahorse.desc1", SpellAdvancementChecks.UNIQUE.or(SpellAdvancementChecks.SUMMONS))
                description.addLang("enchantment.amethyst_imbuement.summon_chicken.summon_seahorse.desc2", SpellAdvancementChecks.UNIQUE.or(SpellAdvancementChecks.SUMMONS))

            }
            RegisterEnchantment.GUSTING ->
                description.addLang("enchantment.amethyst_imbuement.summon_chicken.gusting.desc", SpellAdvancementChecks.UNIQUE)
            RegisterEnchantment.SUMMON_HAMSTER ->
                description.addLang("enchantment.amethyst_imbuement.summon_chicken.summon_hamster.desc", SpellAdvancementChecks.UNIQUE.or(SpellAdvancementChecks.SUMMONS))
            RegisterEnchantment.HAMPTERTIME ->
                description.addLang("enchantment.amethyst_imbuement.summon_chicken.summon_hamster.desc", SpellAdvancementChecks.UNIQUE.or(SpellAdvancementChecks.SUMMONS))
        }
        if (othersType.positiveEffect)
            description.addLang("enchantment.amethyst_imbuement.summon_chicken.desc.falling", SpellAdvancementChecks.ENTITY_EFFECT)
        if (othersType.has(AugmentType.SUMMONS))
            description.addLang("enchantment.amethyst_imbuement.summon_chicken.desc.summons", SpellAdvancementChecks.SUMMONS)
        if (othersType.has(AugmentType.DAMAGE))
            description.addLang("enchantment.amethyst_imbuement.summon_chicken.desc.damage", SpellAdvancementChecks.ON_KILL)
        if (othersType.has(AugmentType.PROJECTILE))
            description.addLang("enchantment.amethyst_imbuement.summon_chicken.desc.projectile", SpellAdvancementChecks.DAMAGE)
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideAdjective(this))
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        if (pair.spellsAreEqual()){
            SpellAdvancementChecks.grant(player, SpellAdvancementChecks.DOUBLE_TRIGGER)
        }
        if (pair.spellsAreUnique()){
            SpellAdvancementChecks.grant(player, SpellAdvancementChecks.UNIQUE_TRIGGER)
        }
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.ON_KILL_TRIGGER)
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.SUMMONS_TRIGGER)
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.ENTITY_EFFECT_TRIGGER)
    }

    override fun specialName(otherSpell: ScepterAugment): MutableText {
        return when (otherSpell) {
            RegisterEnchantment.SUMMON_SEAHORSE ->
                AcText.translatable("enchantment.amethyst_imbuement.summon_chicken.summon_seahorse")
            RegisterEnchantment.GUSTING ->
                AcText.translatable("enchantment.amethyst_imbuement.summon_chicken.gusting")
            else ->
                super.specialName(otherSpell)
        }
    }

    override fun <T> applyTasks(
        world: World,
        context: ProcessContext,
        user: T,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        spells: PairedAugments
    ): SpellActionResult where T : SpellCastingEntity,T : LivingEntity {
        val onCastResults = spells.processOnCast(context,world,null,user, hand, level, effects)
        if (!onCastResults.success()) return  FAIL
        if (onCastResults.overwrite()) return onCastResults
        val hit = RaycasterUtil.raycastHit(
            distance = effects.range(level),
            user,
            includeFluids = true
        ) ?: BlockHitResult(user.pos, Direction.UP,user.blockPos,false)
        val list = if (hit is BlockHitResult) {
            spells.processSingleBlockHit(hit, context, world, null, user, hand, level, effects)
        } else {
            val entityHitResult = EntityHitResult(user)
            spells.processSingleEntityHit(entityHitResult,context,world,null,user, hand, level, effects)
        }
        return if (list.isEmpty()) {
            FAIL
        } else {
            spells.castSoundEvents(world,user.blockPos,context)
            list.addAll(onCastResults.results())
            SpellActionResult.success(list)
        }
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
    ): Int where T : SpellCastingEntity,T : LivingEntity {
        if (spells.primary() == RegisterEnchantment.SUMMON_HAMSTER || spells.primary() == RegisterEnchantment.HAMPTERTIME)
            return start + 1
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
        if (spells.primary() == RegisterEnchantment.GUSTING){
            if (user is ModifiableEffectMobOrPlayer){
                user.amethyst_imbuement_addTemporaryEffect(ModifiableEffectEntity.TICK, ModifiableEffects.GUST_EFFECT, 600)
                return SpellActionResult.success(AugmentHelper.APPLIED_POSITIVE_EFFECTS)
            } else if (user is ModifiableEffectEntity){
                user.addTemporaryEffect(ModifiableEffectEntity.TICK,ModifiableEffects.GUST_EFFECT, 600)
                return SpellActionResult.success(AugmentHelper.APPLIED_POSITIVE_EFFECTS)
            }
        }
        if (spells.primary() == RegisterEnchantment.SUMMON_SEAHORSE){
            val scepter = user.getStackInHand(hand)
            if (scepter.item is ScepterLike && scepter.item is SpellCasting){
                val nbt = scepter.nbt
                if (nbt != null){
                    if (nbt.contains("current_chorse") && world is ServerWorld){
                        val chorse = world.getEntity(nbt.getUuid("current_chorse"))
                        val chorseNbt = NbtCompound()
                        chorse?.saveSelfNbt(chorseNbt) ?: return SUCCESSFUL_PASS
                        nbt.put("stored_chorse",chorseNbt)
                        chorse.discard()
                        nbt.remove("current_chorse")
                        context.set(ProcessContext.COOLDOWN,200)
                        return SpellActionResult.overwrite(AugmentHelper.DRY_FIRED)
                    }
                }
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
    ): SpellActionResult where T : SpellCastingEntity, T : LivingEntity {
        return spawnChimkin(blockHitResult,context, world, user, hand, level, effects, othersType, spells)

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
    ): SpellActionResult where T : SpellCastingEntity,T : LivingEntity {
        if (othersType.positiveEffect){
            val entity = entityHitResult.entity
            if (entity is LivingEntity){
                entity.addStatusEffect(StatusEffectInstance(StatusEffectInstance(StatusEffects.SLOW_FALLING,effects.duration(level))))
                return SpellActionResult.success(AugmentHelper.APPLIED_POSITIVE_EFFECTS)
            }
        }
        return spawnChimkin(entityHitResult,context, world, user, hand, level, effects, othersType, spells)
    }

    override fun <T> onEntityKill(
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
        if (othersType.has(AugmentType.DAMAGE)){
            if (world.random.nextFloat() < 0.25){
                ItemScatterer.spawn(world,entityHitResult.pos.x,entityHitResult.pos.y,entityHitResult.pos.z, ItemStack(Items.EGG))
                return SpellActionResult.success(AugmentHelper.APPLIED_POSITIVE_EFFECTS)
            }
        }
        return SUCCESSFUL_PASS
    }

    private fun <T> spawnChimkin(
        hit: HitResult,
        context: ProcessContext,
        world: World,
        user: T,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        othersType: AugmentType,
        spells: PairedAugments
    ): SpellActionResult where T : SpellCastingEntity,T : LivingEntity {
        if (othersType.empty || othersType.has(AugmentType.PROJECTILE)){
            summonContext(context)
            var count = if (othersType.has(AugmentType.PROJECTILE)) 1 else spells.provideCount(level,context,user,world, hand, level, effects, othersType, spells)
            count *= if (spells.spellsAreEqual()) 3 else 1
            var successes = 0
            for(i in 1..count) {
                val startPos = BlockPos.ofFloored(hit.pos)
                val chimkin = EntityType.CHICKEN.create(world)?:continue
                val posResult = findSpawnPos(world,startPos,chimkin,2)
                if (!posResult) continue
                if (world.spawnEntity(chimkin)){
                    successes++
                }
            }
            return if (successes > 0){
                SpellActionResult.success(AugmentHelper.SUMMONED_MOB)
            } else {
                FAIL
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
    ): List<Entity> where T : ModifiableEffectEntity,T : Entity, U : SpellCastingEntity,U : LivingEntity {
        if (spells.primary() == RegisterEnchantment.SUMMON_SEAHORSE) {
            val scepter = user.getStackInHand(hand)
            var chorseLoaded: Entity? = null
            if (scepter.item is ScepterLike && scepter.item is SpellCasting){
                val nbt = scepter.nbt
                if (nbt != null){
                    if (nbt.contains("stored_chorse")){
                        val storedChorse = nbt.getCompound("stored_chorse")
                        chorseLoaded = EntityType.loadEntityWithPassengers(storedChorse,world) {entity -> entity}
                        nbt.remove("stored_chorse")
                    }
                }
            }
            val chorse = if(chorseLoaded is ChorseEntity) {
                chorseLoaded
            } else {
                RegisterEntity.CHORSE_ENTITY.create(world) ?: return summons
            }
            val found = findSpawnPos(world, BlockPos.ofFloored(hit.pos),chorse,3, tries = 16)
            if (!found){
                return listOf()
            }
            chorse.setPlayerHorseOwner(user)
            chorse.passEffects(spells,effects,level)
            chorse.passContext(context)
            if (scepter.item is ScepterLike && scepter.item is SpellCasting){
                scepter.orCreateNbt.putUuid("current_chorse",chorse.uuid)
            }
            return listOf(chorse)
        }
        for (summon in summons){
            if (summon is LivingEntity){
                summon.addStatusEffect(StatusEffectInstance(StatusEffects.SLOW_FALLING, effects.duration(level)))
            }
        }
        return summons
    }


    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos,SoundEvents.ENTITY_CHICKEN_AMBIENT,SoundCategory.PLAYERS,1.0f,1.0f)
    }
}
