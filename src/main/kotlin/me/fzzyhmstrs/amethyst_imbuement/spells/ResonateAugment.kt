package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.base.SlashAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.modifier.addLang
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterHelper
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterStatus
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.MultiTargetAugment
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.MultiTargetAugment.Companion.writeBuf
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlF
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.network.PacketByteBuf
import net.minecraft.particle.DefaultParticleType
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import java.util.*

class ResonateAugment: MultiTargetAugment(ScepterTier.THREE) {
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity( "resonate"),SpellType.FURY,18,18,
            18,5,1,1, LoreTier.NO_TIER, Items.NOTE_BLOCK)

    //ml 5
    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDamage(4.25F,0.75F,0.0F)
            .withRange(10.25,0.75,0.0)
            .withDuration(72,8)
            .withAmplifier(0,1,0)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        if (othersType.has(AugmentType.DAMAGE)) {
            description.addLang("enchantment.amethyst_imbuement.resonate.desc.damage", SpellAdvancementChecks.DAMAGE)
            description.addLang("enchantment.amethyst_imbuement.resonate.desc.damage2", SpellAdvancementChecks.DAMAGE)
        }
        if (othersType.has(AugmentType.BENEFICIAL))
            description.addLang("enchantment.amethyst_imbuement.resonate.desc.amplifier", SpellAdvancementChecks.STAT)
        if (othersType.has(AugmentType.SUMMONS))
            description.addLang("enchantment.amethyst_imbuement.resonate.desc.summons", SpellAdvancementChecks.STAT)
        when(other){
            RegisterEnchantment.FORTIFY -> {
                description.addLang("enchantment.amethyst_imbuement.resonate.fortify.desc", SpellAdvancementChecks.UNIQUE)
                description.addLang("enchantment.amethyst_imbuement.resonate.fortify.desc2", SpellAdvancementChecks.UNIQUE)
            }
            RegisterEnchantment.INSPIRING_SONG ->
                description.addLang("enchantment.amethyst_imbuement.resonate.inspiring_song.desc", SpellAdvancementChecks.UNIQUE)
        }
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideNoun(this))
    }

    override fun specialName(otherSpell: ScepterAugment): MutableText {
        return when(otherSpell) {
            RegisterEnchantment.FORTIFY ->
                AcText.translatable("enchantment.amethyst_imbuement.resonate.fortify")
            RegisterEnchantment.INSPIRING_SONG ->
                AcText.translatable("enchantment.amethyst_imbuement.resonate.inspiring_song")
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
        SpellAdvancementChecks.grant(player,SpellAdvancementChecks.DAMAGE_TRIGGER)
    }

    override fun modifyAmplifier(
        amplifier: PerLvlI,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlI {
        if (othersType.has(AugmentType.BENEFICIAL)){
            return amplifier.plus(2)
        }
        return amplifier
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
        val perLvlDamage = if(othersType.empty) 1f else 0.5f
        val entity = entityHitResult.entity
        if (entity !is LivingEntity) return amount
        val resonance = entity.getStatusEffect(RegisterStatus.RESONATING)
        if (resonance == null){
            entity.addStatusEffect(StatusEffectInstance(RegisterStatus.RESONATING,80,0))
            return amount
        }
        val resonanceLevel = resonance.amplifier + 1
        entity.removeStatusEffect(RegisterStatus.RESONATING)
        entity.addStatusEffect(StatusEffectInstance(RegisterStatus.RESONATING,80,resonanceLevel))
        return amount + (resonanceLevel * perLvlDamage)
    }

    override fun effect(world: World, user: LivingEntity, entityList: MutableList<Entity>, level: Int, effect: AugmentEffect): Boolean {
        val entityDistance: SortedMap<Double, Entity> = mutableMapOf<Double, Entity>().toSortedMap()
        for (entity in entityList){
            if (entity is MobEntity){
                val dist = entity.squaredDistanceTo(user)
                entityDistance[dist] = entity
            }
        }
        var bl = false
        if (entityDistance.isNotEmpty()) {
            val entityDistance2 = entityDistance.toList()
            val entity1 = entityDistance2[0].second
            bl = resonateTarget(world,user,entity1,level, effect)
            var nextTarget = 1
            while (entityDistance.size > nextTarget && effect.amplifier(level) > nextTarget){
                val entity2 = entityDistance2[nextTarget].second
                bl = bl || resonateTarget(world, user, entity2, level, effect, true)
                nextTarget++
            }
            if (bl){
                effect.accept(user, AugmentConsumer.Type.BENEFICIAL)
            }
            effect.accept(toLivingEntityList(entityList), AugmentConsumer.Type.HARMFUL)
        }
        return bl
    }

    private fun resonateTarget(world: World,user: LivingEntity,target: Entity,level: Int,effect: AugmentEffect, splash: Boolean = false): Boolean{
        val amp = if (target is LivingEntity){
            val status = target.getStatusEffect(RegisterStatus.RESONATING)
            status?.amplifier?:-1
        } else {
            -1
        }
        val damage = if(!splash) {
            effect.damage(level + amp + 1)
        } else {
            effect.damage(level + amp - 1)
        }
        val bl = if(user is PlayerEntity) target.damage(user.damageSources.playerAttack(user),damage) else target.damage(user.damageSources.mobAttack(user),damage)
        if (bl) {
            if (user is ServerPlayerEntity) {
                ServerPlayNetworking.send(user, NOTE_BLAST, writeBuf(user, target))
            }
            secondaryEffect(world, user, target, level, effect)
        }
        return bl
    }

    override fun clientTask(world: World, user: LivingEntity, hand: Hand, level: Int) {
    }

    override fun secondaryEffect(world: World, user: LivingEntity, target: Entity, level: Int, effect: AugmentEffect) {
        if (target is LivingEntity){
            val status = target.getStatusEffect(RegisterStatus.RESONATING)
            val amp = status?.amplifier?:-1
            target.addStatusEffect(addStatusInstance(effect,amp + 1))
        }
    }


    override fun addStatusInstance(effect: AugmentEffect, level: Int): StatusEffectInstance {
        return StatusEffectInstance(RegisterStatus.RESONATING,effect.duration(level), level)
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.BLOCK_BELL_RESONATE
    }

    override fun particleType(): DefaultParticleType {
        return ParticleTypes.ELECTRIC_SPARK
    }

    override fun particleSpeed(): Double {
        return 3.0
    }
}
