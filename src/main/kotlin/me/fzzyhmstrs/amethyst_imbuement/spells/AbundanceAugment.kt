package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.SpellActionResult
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
import me.fzzyhmstrs.amethyst_imbuement.entity.PlayerFangsEntity
import me.fzzyhmstrs.amethyst_imbuement.entity.living.FloralConstructEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks.or
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.effects.ModifiableEffects
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.explosion_behaviors.AbundanceExplosionBehavior
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlD
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import net.minecraft.block.CropBlock
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.item.BoneMealItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World


/*
    Checklist
     */

class AbundanceAugment: ScepterAugment(ScepterTier.ONE, AugmentType.BLOCK_AREA) {
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("abundance"),SpellType.GRACE, PerLvlI(15,-1),3,
            1,6,1,1, LoreTier.NO_TIER, Items.HAY_BLOCK)

    //ml 6
    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withRange(1.5,0.5).withAmplifier(18,2)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        description.addLang("enchantment.amethyst_imbuement.abundance.desc.amplifier", SpellAdvancementChecks.STAT)
        if (othersType.positiveEffect)
            description.addLang("enchantment.amethyst_imbuement.abundance.desc.positive", SpellAdvancementChecks.STAT)
        if (othersType.has(AugmentType.EXPLODES))
            description.addLang("enchantment.amethyst_imbuement.abundance.desc.explodes", SpellAdvancementChecks.EXPLODES)
        if (othersType.has(AugmentType.DAMAGE) || othersType.has(AugmentType.SUMMONS))
            description.addLang("enchantment.amethyst_imbuement.abundance.desc.kill", SpellAdvancementChecks.ON_KILL.or(SpellAdvancementChecks.SUMMONS))
        when(other) {
            RegisterEnchantment.SUMMON_BONESTORM -> {
                description.addLang("enchantment.amethyst_imbuement.abundance.summon_bonestorm.desc1", SpellAdvancementChecks.UNIQUE.or(SpellAdvancementChecks.CHICKEN))
                description.addLang("enchantment.amethyst_imbuement.abundance.summon_bonestorm.desc2", SpellAdvancementChecks.UNIQUE.or(SpellAdvancementChecks.CHICKEN))
            }
            RegisterEnchantment.FANGS ->
                description.addLang("enchantment.amethyst_imbuement.abundance.fangs.desc", SpellAdvancementChecks.UNIQUE.or(SpellAdvancementChecks.DAMAGE))

        }
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideNoun(this))
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        if (pair.spellsAreEqual()){
            SpellAdvancementChecks.grant(player, SpellAdvancementChecks.DOUBLE_TRIGGER)
        }
        if (pair.spellsAreUnique()){
            SpellAdvancementChecks.grant(player, SpellAdvancementChecks.UNIQUE_TRIGGER)
        }
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.BLOCK_TRIGGER)
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.AMPLIFIER_TRIGGER)
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.ON_KILL_TRIGGER)
    }

    override fun specialName(otherSpell: ScepterAugment): MutableText {
        return when (otherSpell) {
            RegisterEnchantment.SUMMON_BONESTORM ->
                AcText.translatable("enchantment.amethyst_imbuement.abundance.summon_bonestorm")
            RegisterEnchantment.FANGS ->
                AcText.translatable("enchantment.amethyst_imbuement.abundance.fangs")
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

        val range = (effects.range(level)).toInt()
        val userPos = user.blockPos
        val list: MutableList<Identifier> = mutableListOf()
        for (i in -range..range) {
            for (j in -range..range) {
                for (k in -2..2) {
                    val bp = userPos.add(i, k, j)
                    val bs = world.getBlockState(bp)
                    val bsb = bs.block
                    if (bsb is CropBlock) {
                        val hitList = spells.processSingleBlockHit(BlockHitResult(bp.toCenterPos(),Direction.UP,bp,false),context,world,null,user,hand, level, effects)
                        list.addAll(hitList)
                    }
                }
            }
        }
        list.addAll(onCastResults.results())
        return if (list.isEmpty()) {
            FAIL
        } else {
            spells.castSoundEvents(world,userPos,context)
            SpellActionResult.success(list)
        }
    }

    override fun modifyCooldown(
        cooldown: PerLvlI,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlI {
        if (other == RegisterEnchantment.SUMMON_CHICKEN)
            return cooldown.plus(5100)
        if (other == RegisterEnchantment.FANGS)
            return cooldown.plus(6)
        return cooldown
    }

    override fun modifyManaCost(
        manaCost: PerLvlI,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlI {
        if (other == RegisterEnchantment.SUMMON_CHICKEN)
            return manaCost.plus(75)
        if (other == RegisterEnchantment.FANGS)
            return manaCost.plus(13)
        if (spells.spellsAreEqual())
            return manaCost.plus(1)
        return manaCost
    }

    override fun modifyAmplifier(
        amplifier: PerLvlI,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlI {
        return amplifier.plus(1)
    }

    override fun modifyRange(
        range: PerLvlD,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlD {
        if (spells.spellsAreEqual())
            return range.plus(0.0,0.0,50.0)
        if (othersType.positiveEffect)
            return range.plus(0.0,0.0,25.0)
        return range
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
        if (spells.primary() == RegisterEnchantment.FANGS){
            var successes = 0
            var d = user.y
            var e = d + 4.0
            for (i in 0..7){
                val angle = (user.yaw + (45 * i)) * MathHelper.PI / 180
                val x = user.x + MathHelper.cos(angle).toDouble() * 1.25
                val z = user.z + MathHelper.sin(angle).toDouble() * 1.25
                val success = PlayerFangsEntity.conjureFang(world,user,x,z,d,e,angle,4,effects,level,spells)
                if (success != Double.NEGATIVE_INFINITY){
                    successes++
                    d = success
                    e = d + 4.0
                }
            }
            d = user.y
            e = d + 4.0
            for (i in 0..11){
                val angle = (user.yaw + 15 + (30 * i)) * MathHelper.PI / 180
                val x = user.x + MathHelper.cos(angle).toDouble() * 2.75
                val z = user.z + MathHelper.sin(angle).toDouble() * 2.75
                val success = PlayerFangsEntity.conjureFang(world,user,x,z,d,e,angle,8,effects,level,spells)
                if (success != Double.NEGATIVE_INFINITY){
                    successes++
                    d = success
                    e = d + 4.0
                }
            }
            d = user.y
            e = d + 4.0
            for (i in 0..15){
                val angle = (user.yaw +  (22.5f * i)) * MathHelper.PI / 180
                val x = user.x + MathHelper.cos(angle).toDouble() * 4.5
                val z = user.z + MathHelper.sin(angle).toDouble() * 4.5
                val success = PlayerFangsEntity.conjureFang(world,user,x,z,d,e,angle,12,effects,level,spells)
                if (success != Double.NEGATIVE_INFINITY){
                    successes++
                    d = success
                    e = d + 4.0
                }
            }
            return if (successes > 0){
                spells.castSoundEvents(world,user.blockPos,context)
                SpellActionResult.overwrite(AugmentHelper.PROJECTILE_FIRED)
            } else {
                FAIL
            }
        }

        return super.onCast(context, world, source, user, hand, level, effects, othersType, spells)
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
    ): SpellActionResult where T : SpellCastingEntity,T : LivingEntity {


        val blockPos = blockHitResult.blockPos
        val bs = world.getBlockState(blockPos)
        val bsb = bs.block

        val rnd1 = world.random.nextInt(100)
        if (rnd1 < effects.amplifier(level) && bsb is CropBlock) {
            val rounds = if (spells.spellsAreEqual()) 2 else 1
            for (i in 1..rounds) {
                if (bsb.isMature(bs)) {
                    world.breakBlock(blockPos, true)
                    world.setBlockState(blockPos, bsb.defaultState)
                    val pos = blockHitResult.pos
                    splashParticles(blockHitResult, world, pos.x, pos.y, pos.z, spells)
                }
            }
            bsb.grow(world as ServerWorld, world.random, blockPos, bs)
            return SpellActionResult.success(AugmentHelper.BLOCK_BROKE)
        }

        return SUCCESSFUL_PASS
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
    ): SpellActionResult where T : SpellCastingEntity,T : LivingEntity {
        if (othersType.has(AugmentType.DAMAGE)){
            val stack = ItemStack(Items.BONE_MEAL)
            BoneMealItem.useOnGround(stack,world,entityHitResult.entity.blockPos.down(),Direction.UP)
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
        if (spells.primary() == RegisterEnchantment.SUMMON_BONESTORM) {
            val fce = FloralConstructEntity(RegisterEntity.FLORAL_CONSTRUCT_ENTITY, world, -1, user)
            val found = AugmentHelper.findSpawnPos(world, BlockPos.ofFloored(hit.pos),fce, tries = 15)
            if (!found){
                return listOf()
            }
            fce.passEffects(spells,effects,level)
            fce.passContext(context)
            fce.addEffect(ModifiableEffectEntity.ON_REMOVED,ModifiableEffects.GROW_EFFECT)
            return listOf(fce)
        }
        for (summon in summons){
            summon.addEffect(ModifiableEffectEntity.ON_REMOVED,ModifiableEffects.GROW_EFFECT)
        }
        return summons
    }

    override fun <T> modifyExplosion(
        builder: ExplosionBuilder,
        context: ProcessContext,
        user: T,
        world: World,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        othersType: AugmentType,
        spells: PairedAugments
    ): ExplosionBuilder where T : SpellCastingEntity, T : LivingEntity {
        return builder.withCustomBehavior(AbundanceExplosionBehavior())
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
        if (othersType.has(AugmentType.SUMMONS))
            return start + 1
        return start
    }

    override fun hitParticleType(hit: HitResult): ParticleEffect {
        return ParticleTypes.HAPPY_VILLAGER
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos,SoundEvents.BLOCK_COMPOSTER_FILL,SoundCategory.PLAYERS,1.0f,world.random.nextFloat()*0.8f + 0.4f)
    }

}
