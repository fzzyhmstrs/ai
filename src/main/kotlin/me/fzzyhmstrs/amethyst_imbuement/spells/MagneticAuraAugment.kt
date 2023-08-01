package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.AugmentPersistentEffectData
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.base.EntityAoeAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.modifier.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import me.fzzyhmstrs.fzzy_core.coding_util.PersistentEffectHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Box
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
        TODO("Not yet implemented")
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
    }

    override fun effect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        hit: HitResult?,
        effect: AugmentEffect
    ): Boolean {
        if (user !is PlayerEntity) return false
        val range = effect.range(level)
        val list = world.getOtherEntities(
            user, Box(user.x - range, user.y - range, user.z - range, user.x + range, user.y + range, user.z + range)
        ) { obj: Entity -> obj is ItemEntity }
        if (list.isEmpty()) return false
        val bl = effect(world, user, list, level, effect)
        if (bl) {
            val data = AugmentPersistentEffectData(world, user, user.blockPos, list, level, effect)
            PersistentEffectHelper.setPersistentTickerNeed(
                RegisterEnchantment.MAGNETIC_AURA,
                delay.value(level),
                effect.duration(level),
                data
            )
            if (world is ServerWorld){
                world.spawnParticles(ParticleTypes.ELECTRIC_SPARK,user.x,user.getBodyY(0.5),user.z,120,effect.range(level),effect.range(level),effect.range(level),0.0)
            }
            world.playSound(null, user.blockPos, soundEvent(), SoundCategory.PLAYERS, 1.0F, 1.0F)
            effect.accept(user, AugmentConsumer.Type.BENEFICIAL)
        }
        return bl
    }

    override fun effect(
        world: World,
        user: LivingEntity,
        entityList: MutableList<Entity>,
        level: Int,
        effect: AugmentEffect
    ): Boolean {
        for (target in entityList) {
            if (target !is ItemEntity) continue
            if (user !is PlayerEntity) return false
            val stack = target.stack
            user.inventory.offerOrDrop(stack)
            target.discard()
        }
        return true
    }

    override val delay: PerLvlI
        get() = PerLvlI(10)

    @Suppress("SpellCheckingInspection")
    override fun persistentEffect(data: PersistentEffectHelper.PersistentEffectData) {
        if (data !is AugmentPersistentEffectData) return
        val range = data.effect.range(data.level)
        val list = data.world.getOtherEntities(
            data.user, Box(data.user.x - range, data.user.y - range, data.user.z - range, data.user.x + range, data.user.y + range, data.user.z + range)
        ) { obj: Entity -> obj is ItemEntity }
        effect(data.world,data.user,list,data.level,data.effect)
        data.world.playSound(null,data.user.blockPos,soundEvent(),SoundCategory.PLAYERS,1.0f,0.8f + data.world.random.nextFloat()*0.4f)
        val world = data.world
        if (world is ServerWorld){
            world.spawnParticles(ParticleTypes.ELECTRIC_SPARK,data.user.x,data.user.getBodyY(0.5),data.user.z,100,data.effect.range(data.level),0.8,data.effect.range(data.level),0.0)
        }
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_WANDERING_TRADER_REAPPEARED
    }

}
