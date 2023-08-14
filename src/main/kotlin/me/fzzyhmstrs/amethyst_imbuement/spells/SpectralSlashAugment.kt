package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.base.SlashAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.DamageSourceBuilder
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.modifier.addLang
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageTypes
import net.minecraft.item.Items
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

@Suppress("SameParameterValue")
open class SpectralSlashAugment: SlashAugment(ScepterTier.ONE){
    //ml 9
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("spectral_slash"),SpellType.FURY,18,4,
            9,9,1,1,LoreTier.LOW_TIER, Items.IRON_SWORD)

    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withDamage(4.5F,0.5F,0.0F)
            .withRange(2.625,0.125,0.0)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        description.addLang("amethyst_imbuement.todo")
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideAdjective(this))
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        SpellAdvancementChecks.uniqueOrDouble(player, pair)
    }

    override fun damageSourceBuilder(world: World, source: Entity?, attacker: LivingEntity?): DamageSourceBuilder {
        return DamageSourceBuilder(world, attacker, source).set(DamageTypes.MAGIC)
    }
    
    override fun filter(list: List<Entity>, user: LivingEntity): MutableList<EntityHitResult>{
        return AugmentHelper.hostileFilter(list, user,this)
    }

    override fun castParticleType(): ParticleEffect? {
        return ParticleTypes.ELECTRIC_SPARK
    }
    
    override fun hitParticleType(hit: HitResult): ParticleEffect?{
        return ParticleTypes.ELECTRIC_SPARK
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext){
        world.playSound(null, blockPos, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 0.7F, 1.1F)
    }
}
