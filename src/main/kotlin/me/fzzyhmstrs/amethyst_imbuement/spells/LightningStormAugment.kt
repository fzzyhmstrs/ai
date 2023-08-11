package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.AugmentHelper
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.SpellActionResult
import me.fzzyhmstrs.amethyst_core.augments.base.EntityAoeAugment
import me.fzzyhmstrs.amethyst_core.augments.base.ProjectileAugment
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
import me.fzzyhmstrs.amethyst_imbuement.entity.PlayerLightningEntity
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.ApplyTaskAugmentData
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.ContextData
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellHelper
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import me.fzzyhmstrs.fzzy_core.coding_util.PersistentEffectHelper
import me.fzzyhmstrs.fzzy_core.raycaster_util.RaycasterUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

@Suppress("SpellCheckingInspection")
class LightningStormAugment: EntityAoeAugment(ScepterTier.THREE, AugmentType.AREA_DAMAGE), PersistentEffectHelper.PersistentEffect{
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(
            AI.identity("lightning_storm"),SpellType.FURY,400,80,
            23,3,1,10,LoreTier.HIGH_TIER, Items.COPPER_BLOCK)

    //ml 3
    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withRange(8.0,1.0,0.0)
            .withDuration(0,120,0)
            .withDamage(5.0f,1.0f)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        description.addLang("amethyst_imbuement.todo")
    }

    override fun filter(list: List<Entity>, user: LivingEntity): MutableList<EntityHitResult> {
        return SpellHelper.hostileFilter(list,user,this)
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideNoun(this))
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        SpellAdvancementChecks.uniqueOrDouble(player, pair)
    }

    override val delay = PerLvlI(21,-3,0)

    override fun <T> applyTasks(world: World, context: ProcessContext, user: T, hand: Hand, level: Int, effects: AugmentEffect, spells: PairedAugments)
            :
            SpellActionResult
            where
            T: LivingEntity,
            T: SpellCastingEntity
    {
        val onCastResults = spells.processOnCast(context,world,null,user, hand, level, effects)
        if (!onCastResults.success()) return  FAIL
        if (onCastResults.overwrite()) return onCastResults
        val hit = RaycasterUtil.raycastBlock(effects.range(level),user)
        val entityList = RaycasterUtil.raycastEntityArea(effects.range(level), user, if (hit != null) Vec3d.of(hit) else null)
        val filteredList = filter(entityList,user)
        if (filteredList.isEmpty()) return FAIL
        val list = spells.processMultipleEntityHits(filteredList,context,world,null,user, hand, level, effects)
        list.addAll(onCastResults.results())
        return if (list.isEmpty()) {
            FAIL
        } else {
            if (!context.get(ContextData.PERSISTENT)) {
                context.set(ContextData.PERSISTENT,true)
                (world as ServerWorld).setWeather(0, 1200, true, true)
                val data = ApplyTaskAugmentData(world, context, user, hand, level, effects, spells)
                PersistentEffectHelper.setPersistentTickerNeed(this,delay.value(level),effects.duration(level),data)
                castSoundEvent(world,user.blockPos,context)
            }
            SpellActionResult.success(list)
        }
    }

    override fun <T> entityEffects(
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
        if (context.get(ProcessContext.FROM_ENTITY)){
            val result = super.onEntityHit(entityHitResult, context, world, source, user, hand, level, effects, othersType, spells)
            if (!result.success() || result.acted())
                return result
        } else if (othersType.empty){
            val ple = PlayerLightningEntity(world,user)
            ple.passEffects(spells,effects,level)
            ple.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(entityHitResult.entity.blockPos))
            ple.passContext(ProjectileAugment.projectileContext(context.copy()))
            return if (world.spawnEntity(ple)) SpellActionResult.success(AugmentHelper.PROJECTILE_FIRED) else FAIL
        }
        return SUCCESSFUL_PASS
    }

    @Suppress("KotlinConstantConditions")
    override fun persistentEffect(data: PersistentEffectHelper.PersistentEffectData) {
        if (data !is ApplyTaskAugmentData<*>) return
        if (data.user !is LivingEntity) return
        this.applyTasks(data.world,data.context,data.user,data.hand,data.level,data.effects,data.spells)
    }

    override fun castSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null, blockPos, SoundEvents.ITEM_TRIDENT_THUNDER, SoundCategory.PLAYERS,1f,1f)
    }
}
