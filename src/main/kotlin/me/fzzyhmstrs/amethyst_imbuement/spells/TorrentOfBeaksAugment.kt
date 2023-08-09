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
        description.addLang("amethyst_imbuement.todo")
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideNoun(this))
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        SpellAdvancementChecks.uniqueOrDouble(player, pair)
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.EXPLODES_TRIGGER)
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
            val me = PlayerItemEntity(RegisterEntity.PLAYER_EGG,world,user,Items.EGG)
            val direction = user.rotationVec3d
            val speed = effects.amplifier(level+5)/10f
            val div = 1.0F
            me.setVelocity(direction.x,direction.y,direction.z, speed, div)
            me.passEffects(spells,effects,level)
            me.passContext(context)
            list.add(me)
        }
        return list
    }

    override fun <T> onEntityHit(
        entityHitResult: EntityHitResult,
        context: ProcessContext,
        world: World,
        source: Entity?,
        user: T,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        othersType: AugmentType,
        spells: PairedAugments
    ): SpellActionResult where T : SpellCastingEntity, T : LivingEntity {
        val result = super.onEntityHit(entityHitResult, context, world, source, user, hand, level, effects, othersType, spells)
        if (result.acted() || !result.success()) {
            if (result.acted()){
                spawnChicken(world,world.random,source,effects,level,spells)
            }
            return result
        }
        
    }

    override fun <T> onBlockHit(
        blockHitResult: BlockHitResult,
        context: ProcessContext,
        world: World,
        source: Entity?,
        user: T,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        othersType: AugmentType,
        spells: PairedAugments
    )
            :
            SpellActionResult
            where
            T : SpellCastingEntity,
            T : LivingEntity
    {
        val result = super.onBlockHit(blockHitResult, context, world, source, user, hand, level, effects, othersType, spells)
        if (result.acted() || !result.success()) {
            if (result.acted()){
                spawnChicken(world,world.random,source,effects,level,spells)
            }
            return result
        }
    }

    private fun spawnChicken(world: World, random: Random, source: Entity?, entityEffects: AugmentEffects, level: Int, spells: PairedAugments){
        if (source == null) return
        if (!world.isClient) {
            if (random.nextFloat() < (1f-10f/entityEffects.range(level))) {
                var i = 1
                if (random.nextFloat() < (1f-10f/entityEffects.range(level))/5f) {
                    i = 4
                }
                for (j in 0 until i) {
                    val chickenEntity = RegisterEntity.BOOM_CHICKEN_ENTITY.create(world) ?: return
                    chickenEntity.refreshPositionAndAngles(source.x, source.y, source.z, yaw, 0.0f)
                    chickenEntity.isBaby = false
                    if (source.owner is LivingEntity) {
                        chickenEntity.setChickenOwner(source.owner as LivingEntity)
                    }
                    chickenEntity.passEffects(spells,entityEffects,levels)
                    world.spawnEntity(chickenEntity)
                }
            }
            world.sendEntityStatus(source, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES)
        }
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos, SoundEvents.ENTITY_EGG_THROW, SoundCategory.PLAYERS, 1f, 1f)
    }
}
