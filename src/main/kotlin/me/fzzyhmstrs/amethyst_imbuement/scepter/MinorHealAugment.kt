package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.AugmentConsumer
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.AugmentEffect
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.MinorSupportAugment
import me.fzzyhmstrs.amethyst_imbuement.util.LoreTier
import me.fzzyhmstrs.amethyst_imbuement.util.SpellType
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.passive.GolemEntity
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundCategory
import net.minecraft.world.World

class MinorHealAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): MinorSupportAugment(tier, maxLvl, *slot) {

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDamage(2.0F,1.0F,0.0F)

    override fun supportEffect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        effects: AugmentEffect
    ): Boolean {
        if(target != null) {
            if (target is PassiveEntity || target is GolemEntity || target is PlayerEntity) {
                (target as LivingEntity).heal(effects.damage(level))
                world.playSound(null, target.blockPos, soundEvent(), SoundCategory.PLAYERS, 1.0F, 1.0F)
                effects.accept(target,AugmentConsumer.Type.BENEFICIAL)
                return true
            }
        }
        return if (user is PlayerEntity) {
                user.heal(effects.damage(level))
                world.playSound(null, user.blockPos, soundEvent(), SoundCategory.PLAYERS, 1.0F, 1.0F)
                effects.accept(user,AugmentConsumer.Type.BENEFICIAL)
                true
            } else {
                false
            }

    }

    override fun augmentStat(imbueLevel: Int): ScepterObject.AugmentDatapoint {
        return ScepterObject.AugmentDatapoint(SpellType.WIT,100,8,1,imbueLevel,LoreTier.LOW_TIER, Items.GLISTERING_MELON_SLICE)
    }
}