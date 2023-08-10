package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.SpellActionResult
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
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.MovementType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class DashAugment: ScepterAugment(ScepterTier.TWO, AugmentType.SINGLE_TARGET_OR_SELF){
    
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("dash"),SpellType.WIT,32,12,
            8,1,1,1, LoreTier.LOW_TIER, Items.SUGAR)

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withAmplifier(1,1).withDuration(20)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        description.addLang("amethyst_imbuement.todo")
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideNoun(this))
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        SpellAdvancementChecks.uniqueOrDouble(player, pair)
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
        if (user !is PlayerEntity) return false
        val y: Float = user.getYaw()
        val p: Float = user.getPitch()
        var g =
            -MathHelper.sin(y * (Math.PI.toFloat() / 180)) * MathHelper.cos(p * (Math.PI.toFloat() / 180))
        var h = -MathHelper.sin(p * (Math.PI.toFloat() / 180))
        var k =
            MathHelper.cos(y * (Math.PI.toFloat() / 180)) * MathHelper.cos(p * (Math.PI.toFloat() / 180))
        val l = MathHelper.sqrt(g * g + h * h + k * k)
        val m: Float = 3.0f * (effect.amplifier(level).toFloat() / 4.0f)

        g *= m / l
        h *= m / l
        k *= m / l
        user.addVelocity(g.toDouble(),h.toDouble(),k.toDouble())
        user.useRiptide(effect.duration(level))

        if (user.isOnGround()) {
            user.move(MovementType.SELF, Vec3d(0.0, 1.1999999284744263, 0.0))
        }
        world.playSoundFromEntity(null,user,soundEvent(),SoundCategory.PLAYERS,1.0F,1.0F)
        return true
    }

    override fun clientTask(world: World, user: LivingEntity, hand: Hand, level: Int) {
        effect(world, null, user, level, null, baseEffect)
    }

    override fun soundEvent(): SoundEvent {
        return when(AI.aiRandom().nextInt(3)){
            0-> SoundEvents.ITEM_TRIDENT_RIPTIDE_1
            1-> SoundEvents.ITEM_TRIDENT_RIPTIDE_2
            2-> SoundEvents.ITEM_TRIDENT_RIPTIDE_3
            else-> SoundEvents.ITEM_TRIDENT_RIPTIDE_1
        }

    }
}
