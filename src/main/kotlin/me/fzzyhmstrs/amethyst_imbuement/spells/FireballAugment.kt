package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.base.ProjectileAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.ExplosionBuilder
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.modifier.addLang
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.PlayerFireballEntity
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class FireballAugment: ProjectileAugment(ScepterTier.TWO, AugmentType.BALL){
    //ml 5
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(
            AI.identity("fireball"),SpellType.FURY,32,10,
            10,5,1,2, LoreTier.LOW_TIER, Items.TNT)

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withDamage(7.75F,0.25f)

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

    override fun explosionBuilder(world: World, source: Entity?, attacker: LivingEntity?): ExplosionBuilder {
        return super.explosionBuilder(world, source, attacker).withType(World.ExplosionSourceType.MOB).withCreateFire(true)
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
            val direction = user.rotationVec3d
            val vec3d: Vec3d = Vec3d(direction.x, direction.y, direction.z).normalize().add(
                world.random.nextTriangular(0.0, 0.0172275 * 0.2),
                world.random.nextTriangular(0.0, 0.0172275 * 0.2),
                world.random.nextTriangular(0.0, 0.0172275 * 0.2)
            ).multiply(2.0)

            val pfe = PlayerFireballEntity(world, user, vec3d.x,vec3d.y,vec3d.z)
            val pos = user.pos.add(0.0,user.eyeY -0.4,0.0).add(direction.multiply(0.5))
            pfe.refreshPositionAfterTeleport(pos)
            pfe.passEffects(spells,effects,level)
            list.add(pfe)
        }
        return list
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos,SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.PLAYERS,1.0f,world.random.nextFloat()*0.8f + 0.4f)
    }
}
