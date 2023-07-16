package me.fzzyhmstrs.amethyst_imbuement.spells

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
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageTypes
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import kotlin.math.min

class FangsAugment: ScepterAugment(ScepterTier.TWO, AugmentType.DIRECTED_ENERGY) {
    //ml 6
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("fangs"),SpellType.FURY,34,12,
            9,6,1,1, LoreTier.LOW_TIER, Items.EMERALD)

    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withRange(11.0,1.0,0.0)
            .withDamage(5.8F,0.2F)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        if (othersType.has(AugmentType.PROJECTILE))
            description.addLang("enchantment.amethyst_imbuement.fangs.desc.projectile", SpellAdvancementChecks.DAMAGE)
        if (othersType.has(AugmentType.DAMAGE))
            description.addLang("enchantment.amethyst_imbuement.fangs.desc.damage", SpellAdvancementChecks.DAMAGE_SOURCE)
        when(other) {
            RegisterEnchantment.SUMMON_FURY_TOTEM -> {
                description.addLang("enchantment.amethyst_imbuement.fangs.summon_fury_totem.desc1", SpellAdvancementChecks.UNIQUE.or(SpellAdvancementChecks.SUMMONS))
                description.addLang("enchantment.amethyst_imbuement.fangs.summon_fury_totem.desc2", SpellAdvancementChecks.UNIQUE.or(SpellAdvancementChecks.SUMMONS))

            }
            RegisterEnchantment.FORTIFY ->
                description.addLang("enchantment.amethyst_imbuement.fangs.fortify.desc", SpellAdvancementChecks.UNIQUE.or(SpellAdvancementChecks.DAMAGE))
        }
        TODO("Not yet implemented")
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
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.DAMAGE_TRIGGER)
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.DAMAGE_SOURCE_TRIGGER)
    }

    override fun specialName(otherSpell: ScepterAugment): MutableText {
        return when (otherSpell) {
            RegisterEnchantment.SUMMON_FURY_TOTEM ->
                AcText.translatable("enchantment.amethyst_imbuement.fangs.summon_fury_totem")
            RegisterEnchantment.FORTIFY ->
                AcText.translatable("enchantment.amethyst_imbuement.fangs.fortify")
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
        TODO("Not yet implemented")
    }

    override fun <T> modifyDamageSource(
        builder: DamageSourceBuilder,
        context: ProcessContext,
        entityHitResult: EntityHitResult,
        source: Entity?,
        user: T,
        world: World,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        othersType: AugmentType,
        spells: PairedAugments
    ): DamageSourceBuilder where T: SpellCastingEntity,T: LivingEntity {
        return builder.add(DamageTypes.MAGIC)
    }

    override fun effect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        hit: HitResult?,
        effect: AugmentEffect
    ): Boolean {
        var successes = 0
        var d: Double
        var e: Double
        if (target != null){
            d = min(target.y, user.y)
            e = d + 2.0
        } else {
            d = user.y
            e = d + 2.0
        }
        val f = (user.yaw + 90) * MathHelper.PI / 180
        for (i in 0..effect.range(level).toInt()) {
            val g = 1.25 * (i + 1).toDouble()
            val success = PlayerFangsEntity.conjureFang(
                world,
                user,
                user.x + MathHelper.cos(f).toDouble() * g,
                user.z + MathHelper.sin(f).toDouble() * g,
                d,
                e,
                f,
                i,
                effect,
                level,
                this
            )
            if (success != Double.NEGATIVE_INFINITY) {
                successes++
                d = success
                e = d + 2.0
            }
        }
        val bl = successes > 0
        if (bl){
            effect.accept(user, AugmentConsumer.Type.BENEFICIAL)
        }
        return successes > 0
    }


    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos,SoundEvents.BLOCK_COMPOSTER_FILL, SoundCategory.PLAYERS,1.0f,world.random.nextFloat()*0.8f + 0.4f)
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_EVOKER_FANGS_ATTACK
    }
}
