package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.augments.MiscAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World

class AnimalHusbandryAugment: MiscAugment(ScepterTier.TWO,4) {

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withRange(6.0,0.5)
            .withAmplifier(2,1)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.GRACE, 80,16,
            7,imbueLevel,4, LoreTier.NO_TIER, Items.HAY_BLOCK)
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
        val list = world.getOtherEntities(user,user.boundingBox.expand(effect.range(level))).stream().filter { it is LivingEntity }.toList()
        val (affectedList,_) = RegisterEnchantment.BEDAZZLE.getRndEntityList(world,toLivingEntityList(list).toMutableList(),effect.amplifier(level))
        for (entity in affectedList){
            if (entity is AnimalEntity) {
                if (entity.isBaby) {
                    val i = entity.breedingAge
                    entity.growUp(AnimalEntity.toGrowUpAge(-i), true)
                    successes++
                    if (entity.health < entity.maxHealth){
                        entity.heal(0.5f)
                    }
                } else {
                    if (entity.canEat() && entity.breedingAge == 0) {
                        entity.lovePlayer(user as? PlayerEntity)
                        successes++
                        if (entity.health < entity.maxHealth){
                            entity.heal(0.5f)
                        }
                    }
                }


            }
        }
        if (successes > 0) world.playSound(null,user.blockPos,soundEvent(),SoundCategory.PLAYERS,1.0f,1.0f)
        return successes > 0
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.BLOCK_GRASS_BREAK
    }
}
