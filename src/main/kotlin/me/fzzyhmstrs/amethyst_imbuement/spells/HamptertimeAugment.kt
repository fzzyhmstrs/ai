@file:Suppress("SpellCheckingInspection")

package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper.findSpawnPos
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.base.SummonAugment
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
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.entity.hamster.BaseHamsterEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterArmor
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterSound
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import kotlin.math.max

class HamptertimeAugment: SummonAugment<BaseHamsterEntity>(ScepterTier.THREE) {
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(
            AI.identity("hamptertime"),SpellType.WIT, 6000,750,
            1,25,1,85,LoreTier.NO_TIER, Items.LEAD)

    //ml 25
    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withDuration(AiConfig.entities.hamster.baseLifespan.get())
            .withAmplifier(AiConfig.entities.hamster.baseHealth.get().toInt())
            .withDamage(AiConfig.entities.hamster.baseHamptertimeDamage.get(),AiConfig.entities.hamster.perLvlDamage.get())
            .withRange(AiConfig.entities.hamster.hamptertimeBaseSpawnCount.get(),AiConfig.entities.hamster.hamptertimePerLvlSpawnCount.get())

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        description.addLang("amethyst_imbuement.todo")
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideNoun(this))
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        SpellAdvancementChecks.uniqueOrDouble(player, pair)
    }

    override fun <T> startCount(
        user: T,
        effects: AugmentEffect,
        level: Int,
        othersType: AugmentType,
        spells: PairedAugments
    ): Int where T : SpellCastingEntity, T : LivingEntity {
        return max(effects.range(level/2).toInt(),1)
    }

    override fun entitiesToSpawn(
        world: World,
        user: LivingEntity,
        hand: Hand,
        hit: HitResult,
        level: Int,
        effects: AugmentEffect,
        spells: PairedAugments,
        count: Int
    ): List<BaseHamsterEntity> {
        val startPos = BlockPos.ofFloored(hit.pos)
        val list: MutableList<BaseHamsterEntity> = mutableListOf()
        for (i in 1..count){
            val hamsterEntity = BaseHamsterEntity(RegisterEntity.BASIC_HAMSTER_ENTITY, world, effects.duration(level), user)
            val success = findSpawnPos(world, startPos, hamsterEntity, 1, 20, user.pitch, user.yaw)
            if (success) {
                val rnd1 = world.random.nextFloat()
                if (rnd1 < (0.2f + 0.005f * level)){
                    if (rnd1 < 0.1){
                        hamsterEntity.setArmor(ItemStack(RegisterArmor.AMETRINE_HELMET))
                    } else {
                        hamsterEntity.setArmor(ItemStack(Items.IRON_HELMET))
                    }
                }
                val rnd2 = world.random.nextFloat()
                if (rnd2 < (0.15f + 0.003f * level)){
                    if (rnd2 < 0.05){
                        hamsterEntity.setMainHand(ItemStack(RegisterItem.GARNET_SWORD))
                    } else {
                        hamsterEntity.setMainHand(ItemStack(Items.STONE_SWORD))
                    }
                }
                hamsterEntity.passEffects(spells, effects, level)
                list.add(hamsterEntity)
            }
        }

        return list
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos,RegisterSound.HAMSTER_AMBIENT, SoundCategory.PLAYERS,1f,1f)
    }
}
