package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.SpellActionResult
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.PlayerLightningEntity
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import me.fzzyhmstrs.fzzy_core.raycaster_util.RaycasterUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class LightningBoltAugment: ScepterAugment(ScepterTier.TWO, AugmentType.TARGET_DAMAGE){
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("lightning_bolt"),SpellType.FURY, PerLvlI(51,-1),20,
            11,11,1,3,LoreTier.LOW_TIER, Items.LIGHTNING_ROD)

    //ml 11
    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withRange(13.8,0.2,0.0)
            .withDamage(4.8F,0.2f)

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

    override fun <T> applyTasks(
        world: World,
        context: ProcessContext,
        user: T,
        hand: Hand,
        level: Int,
        effects: AugmentEffect,
        spells: PairedAugments
    )
            :
            SpellActionResult
            where
            T : SpellCastingEntity,
            T : LivingEntity
    {
        TODO("Not yet implemented")
    }

    override fun effect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        hit: HitResult?,
        effect: AugmentEffect
    ): Boolean {
        val entity = RaycasterUtil.raycastEntity(effect.range(level),user)

        val blockPos: BlockPos = if (entity == null){
            if (hit == null) {
                return false
            } else if (hit.type == HitResult.Type.BLOCK){
                val bp = (hit as BlockHitResult).blockPos
                if (world.isSkyVisible(bp)){
                    bp
                } else {
                    hit.blockPos.add(0,1,0)
                }

            } else if (hit.type == HitResult.Type.ENTITY){
                (hit as EntityHitResult).entity.blockPos
            } else if (hit.type == HitResult.Type.MISS){
                return false
            } else {
                return false
            }
        } else {
            entity.blockPos
        }

        if (world.isSkyVisible(blockPos)) {
            //replace with a player version that can pass consumers?
            val le = PlayerLightningEntity.createLightning(world, Vec3d.ofBottomCenter(blockPos),user,effect, level,this)
            val bl = world.spawnEntity(le)
            if (bl) {
                effect.accept(user, AugmentConsumer.Type.BENEFICIAL)
                world.playSound(null, user.blockPos, soundEvent(), SoundCategory.PLAYERS, 1.0F, 0.65F)
            }
            return bl
        }
        return false
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ITEM_TRIDENT_THUNDER
    }

}
