package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.base.ProjectileAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.entity.MissileEntity
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.modifier.addLang
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlF
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.Items
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class MagicMissileAugment: ProjectileAugment(ScepterTier.ONE) {
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(
            AI.identity("magic_missile"),SpellType.NULL,15,1,
            1,1,0,0,LoreTier.NO_TIER,Items.GOLD_INGOT)

    //ml 1
    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDamage(3.0F)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        description.addLang("enchantment.amethyst_imbuement.magic_missile.desc.manaCost")
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
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.MANA_COST_TRIGGER)
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.STAT_TRIGGER)
    }

    override fun modifyManaCost(
        manaCost: PerLvlI,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlI {
        return manaCost.plus(0,0,-10)
    }
    
    override fun modifyDamage(
        damage: PerLvlF,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlF {
        if (spells.spellsAreEqual())
            return damage.plus(1f)
        return damage
    }

    override fun <T> createProjectileEntities(world: World, context: ProcessContext, user: T, level: Int, effects: AugmentEffect, spells: PairedAugments, count: Int)
    : 
    List<ProjectileEntity>
    where 
    T: LivingEntity,
    T: SpellCastingEntity
    {
        val list: MutableList<ProjectileEntity> = mutableListOf()
        for (i in 1..count){
            val me = MissileEntity(world, user)
            val direction = user.rotationVec3d
            me.setVelocity(direction.x,direction.y,direction.z, 2.0f, 0.1f)
            me.passEffects(spells,effects,level)
            me.passContext(context)
            list.add(me)
        }
        return list
    }

    override fun hitParticleType(hit: HitResult): ParticleEffect? {
        return ParticleTypes.CRIT
    }

    override fun castParticleType(): ParticleEffect? {
        return ParticleTypes.CRIT
    }

    override fun hitSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos, SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.PLAYERS, 0.5f, 1f)
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos, SoundEvents.ENTITY_ENDER_DRAGON_SHOOT, SoundCategory.PLAYERS, 1f, 1f)
    }
}
