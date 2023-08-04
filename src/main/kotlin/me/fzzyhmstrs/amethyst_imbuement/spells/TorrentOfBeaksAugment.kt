package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.base.ProjectileAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.world.World

class TorrentOfBeaksAugment: ProjectileAugment(ScepterTier.TWO, AugmentType.SUMMON_BOOM.plus(AugmentType.PROJECTILE)){
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("torrent_of_beaks"),SpellType.FURY, PerLvlI(15,-1),2,
            19, 11,1,1, LoreTier.NO_TIER, Items.CHICKEN)

    //11
    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withDamage(2.8F,0.2f)
            .withAmplifier(9,1)
            .withRange(12.0,0.5)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        TODO("Not yet implemented")
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideNoun(this))
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        SpellAdvancementChecks.uniqueOrDouble(player, pair)
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.EXPLODES_TRIGGER)
    }

    override fun entityClass(world: World, user: LivingEntity, level: Int, effects: AugmentEffect): ProjectileEntity {
        val speed = effects.amplifier(level+5)/10f
        val div = 1.0F
        val eggEntity = PlayerEggEntity(world, user)
        eggEntity.setVelocity(user, user.pitch, user.yaw, 0.0f, speed, div)
        eggEntity.passEffects(effects, level)
        return eggEntity
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_EGG_THROW
    }
}
