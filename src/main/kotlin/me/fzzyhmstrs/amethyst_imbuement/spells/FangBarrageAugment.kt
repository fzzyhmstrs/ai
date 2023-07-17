package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.AugmentPersistentEffectData
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.SpellActionResult
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.DamageSourceBuilder
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.modifier.addLang
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.PlayerFangsEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks.or
import me.fzzyhmstrs.fzzy_core.coding_util.AcText
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import me.fzzyhmstrs.fzzy_core.coding_util.PersistentEffectHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageTypes
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import kotlin.math.min

class FangBarrageAugment: ScepterAugment(ScepterTier.THREE, AugmentType.DIRECTED_ENERGY), PersistentEffectHelper.PersistentEffect {
    //ml 6
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(
            AI.identity("fang_barrage"),SpellType.FURY,100,50,
            26,6,1,2, LoreTier.HIGH_TIER, Items.EMERALD_BLOCK)

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDuration(28,0,0)
            .withAmplifier(9,1,0)
            .withDamage(5.8F,0.2F)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        if (othersType.has(AugmentType.PROJECTILE) && othersType.has(AugmentType.DAMAGE))
            description.addLang("enchantment.amethyst_imbuement.fang_barrage.desc.projectile", SpellAdvancementChecks.DAMAGE)
        if (othersType.has(AugmentType.AOE) && othersType.has(AugmentType.ENTITY))
            description.addLang("enchantment.amethyst_imbuement.fang_barrage.desc.range", SpellAdvancementChecks.RANGE)
        if (othersType.has(AugmentType.DAMAGE))
            description.addLang("enchantment.amethyst_imbuement.fang_barrage.desc.damage", SpellAdvancementChecks.DAMAGE_SOURCE)
        if (othersType.positiveEffect)
            description.addLang("enchantment.amethyst_imbuement.fang_barrage.desc.retaliation", SpellAdvancementChecks.DAMAGE.or(SpellAdvancementChecks.PROTECTED_EFFECT))
        when(other) {
            RegisterEnchantment.SOUL_MISSILE -> {
                description.addLang("enchantment.amethyst_imbuement.fang_barrage.soul_missile.desc1", SpellAdvancementChecks.UNIQUE.or(SpellAdvancementChecks.SUMMONS))
                description.addLang("enchantment.amethyst_imbuement.fang_barrage.soul_missile.desc2", SpellAdvancementChecks.UNIQUE.or(SpellAdvancementChecks.SUMMONS))

            }
            RegisterEnchantment.MASS_FORTIFY ->
                description.addLang("enchantment.amethyst_imbuement.fang_barrage.mass_fortify.desc", SpellAdvancementChecks.UNIQUE.or(SpellAdvancementChecks.BOOSTED_EFFECT))
            RegisterEnchantment.FLAMEWAVE ->
                description.addLang("enchantment.amethyst_imbuement.fang_barrage.flamewave.desc", SpellAdvancementChecks.UNIQUE.or(SpellAdvancementChecks.DAMAGE))
        }
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideAdjective(this))
    }

    override fun specialName(otherSpell: ScepterAugment): MutableText {
        return when (otherSpell) {
            RegisterEnchantment.SOUL_MISSILE ->
                AcText.translatable("enchantment.amethyst_imbuement.fang_barrage.soul_missile")
            RegisterEnchantment.MASS_FORTIFY ->
                AcText.translatable("enchantment.amethyst_imbuement.fang_barrage.mass_fortify")
            RegisterEnchantment.FLAMEWAVE ->
                AcText.translatable("enchantment.amethyst_imbuement.fang_barrage.flamewave")
            else ->
                super.specialName(otherSpell)
        }
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        if (pair.spellsAreEqual()){
            SpellAdvancementChecks.grant(player, SpellAdvancementChecks.DOUBLE_TRIGGER)
        }
        if (pair.spellsAreUnique()){
            SpellAdvancementChecks.grant(player, SpellAdvancementChecks.UNIQUE_TRIGGER)
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
        TODO("Not yet implemented")
    }

    override val delay = PerLvlI(15,-1,0)

    override fun effect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        hit: HitResult?,
        effect: AugmentEffect
    ): Boolean {
        val entityList: MutableList<Entity> = mutableListOf()
        val d: Double
        val e: Double
        if (target != null){
            d = min(target.y, user.y)
            e = d + 2.0
            entityList.add(target)
        } else {
            d = user.y
            e = d + 2.0
        }
        val f = (user.yaw + 90) * MathHelper.PI / 180
        val successes = conjureBarrage(user,world,d,e,f, effect, level)
        val bl = successes > 0
        if (bl){
            effect.accept(user,AugmentConsumer.Type.BENEFICIAL)
        }
        val data = AugmentPersistentEffectData(world, user, BlockPos.ORIGIN, entityList, level, effect)
        PersistentEffectHelper.setPersistentTickerNeed(RegisterEnchantment.FANG_BARRAGE, delay.value(level),effect.duration(level), data)
        return bl
    }

    override fun persistentEffect(data: PersistentEffectHelper.PersistentEffectData) {
        if (data !is AugmentPersistentEffectData) return
        val d: Double
        val e: Double
        if (data.entityList.isNotEmpty()){
            val target = data.entityList[0]
            d = min(target.y, data.user.y)
            e = d + 2.0
            data.entityList.add(target)
        } else {
            d = data.user.y
            e = d + 2.0
        }
        val f = (data.user.yaw + 90) * MathHelper.PI / 180
        conjureBarrage(data.user,data.world,d,e,f, data.effect, data.level)
    }

    override fun damageSourceBuilder(world: World, source: Entity?, attacker: LivingEntity): DamageSourceBuilder {
        return DamageSourceBuilder(world, attacker, source).set(DamageTypes.MAGIC)
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos,SoundEvents.ENTITY_EVOKER_FANGS_ATTACK, SoundCategory.PLAYERS,1.0f,world.random.nextFloat()*0.8f + 0.4f)
    }

    private fun conjureBarrage(user: LivingEntity, world: World, d: Double, e: Double, f: Float, effect: AugmentEffect, level: Int): Int{
        var dd = d
        var ee = e
        var successes = 0
        val fangs = effect.amplifier(level)
        for (i in 0..fangs) {
            val g = 1.25 * (i + 1).toDouble()
            for (k in -1..1){
                val success = PlayerFangsEntity.conjureFang(
                    world,
                    user,
                    user.x + MathHelper.cos(f + (11.0F * MathHelper.PI / 180 * k)).toDouble() * g,
                    user.z + MathHelper.sin(f + (11.0F * MathHelper.PI / 180 * k)).toDouble() * g,
                    dd,
                    ee,
                    f,
                    i,
                    effect,
                    level,
                    this
                )
                if (success != Double.NEGATIVE_INFINITY) {
                    successes++
                    dd = success
                    ee = dd + 2.0
                }
            }
        }
        return successes
    }
}
