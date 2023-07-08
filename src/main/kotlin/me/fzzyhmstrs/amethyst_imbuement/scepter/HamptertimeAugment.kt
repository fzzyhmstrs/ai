@file:Suppress("SpellCheckingInspection")

package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.SummonEntityAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.entity.living.BaseHamsterEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterArmor
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterSound
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import kotlin.math.max

class HamptertimeAugment: SummonEntityAugment(ScepterTier.THREE,25) {

    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withDuration(AiConfig.entities.hamster.baseLifespan.get())
            .withAmplifier(AiConfig.entities.hamster.baseHealth.get().toInt())
            .withDamage(AiConfig.entities.hamster.baseHamptertimeDamage.get(),AiConfig.entities.hamster.perLvlDamage.get())
            .withRange(AiConfig.entities.hamster.hamptertimeBaseSpawnCount.get(),AiConfig.entities.hamster.hamptertimePerLvlSpawnCount.get())

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.WIT, 6000,750,
            1,imbueLevel,85,LoreTier.NO_TIER, Items.LEAD)
    }

    override fun placeEntity(
        world: World,
        user: PlayerEntity,
        hit: HitResult,
        level: Int,
        effects: AugmentEffect
    ): Boolean {
        var successes = 0
        for(i in 1..max(effects.range(level/2).toInt(),1)) {
            val startPos = (hit as BlockHitResult).blockPos
            val spawnPos = findSpawnPos(world,startPos,5,1)
            if (spawnPos == BlockPos.ORIGIN) continue

            val hampter = BaseHamsterEntity(RegisterEntity.BASIC_HAMSTER_ENTITY, world, effects.duration(level), user, effects, level)

            val rnd1 = world.random.nextFloat()
            if (rnd1 < (0.2f + 0.005f * level)){
                if (rnd1 < 0.1){
                    hampter.setArmor(ItemStack(RegisterArmor.AMETRINE_HELMET))
                } else {
                    hampter.setArmor(ItemStack(Items.IRON_HELMET))
                }
            }
            val rnd2 = world.random.nextFloat()
            if (rnd2 < (0.15f + 0.003f * level)){
                if (rnd2 < 0.05){
                    hampter.setMainHand(ItemStack(RegisterItem.GARNET_SWORD))
                } else {
                    hampter.setMainHand(ItemStack(Items.STONE_SWORD))
                }
            }

            hampter.refreshPositionAndAngles(spawnPos.x +0.5, spawnPos.y + 0.005, spawnPos.z + 0.5, (world.random.nextFloat() * 360f) - 180f,user.pitch)
            if (world.spawnEntity(hampter)){
                successes++
            }
        }
        if (successes > 0) {
            return super.placeEntity(world, user, hit, level, effects)
        }
        return false
    }

    override fun soundEvent(): SoundEvent {
        return RegisterSound.HAMSTER_AMBIENT
    }
}