package me.fzzyhmstrs.amethyst_imbuement.scepter

import it.unimi.dsi.fastutil.objects.Object2FloatMap
import me.fzzyhmstrs.amethyst_core.coding_util.AcText
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.SummonEntityAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.entity.ImbuedFamiliarEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.passive.StriderEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World
import java.util.*

@Suppress("SpellCheckingInspection")
class SummonFamiliarAugment(tier: Int, maxLvl: Int, vararg slot: EquipmentSlot): SummonEntityAugment(tier, maxLvl, *slot) {

    override val baseEffect: AugmentEffect
        get() = super.baseEffect.withAmplifier(2,1,0)
            .withDamage(-0.1f,0.2f)
            .withRange(-0.5,0.5)

    override fun placeEntity(
        world: World,
        user: PlayerEntity,
        hit: HitResult,
        level: Int,
        effects: AugmentEffect
    ): Boolean {
        val foundFamiliar = findFamiliar(user, world)
        var successes = 0
        val xrnd: Double = (hit as BlockHitResult).blockPos.x.toDouble()
        val zrnd: Double = (hit).blockPos.z.toDouble()
        val yrnd = hit.blockPos.y + 1.1
        println(foundFamiliar)
        if (foundFamiliar != null && foundFamiliar is ImbuedFamiliarEntity){
            if (foundFamiliar.distanceTo(user) > maxDismissDistance){
                //teleport your far away familiar to you
                foundFamiliar.refreshPositionAndAngles(xrnd, yrnd, zrnd,0.0f,0.0f)
                successes++
            } else {
                //if close to you, dismisses the entity instead
                foundFamiliar.remove(Entity.RemovalReason.DISCARDED)
                user.sendMessage(AcText.translatable(""))
                world.playSound(null, user.blockPos, SoundEvents.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, SoundCategory.PLAYERS, 0.7F, 1.3F)
                return false
            }
        } else {
            //no active entity, build a new one from storage or from scratch
            val familiar = ImbuedFamiliarEntity(
                RegisterEntity.IMBUED_FAMILIAR_ENTITY,
                world,
                user,
                effects.damage(level).toDouble(),
                effects.range(level),
                effects.amplifier(level),
                level
            )
            //if stored, grab the nbt and load up onto the entity.
            if (storedFamiliars.containsKey(user.uuid)){
                val nbt = storedFamiliars[user.uuid]
                if (nbt != null){
                    familiar.readCustomDataFromNbt(nbt)
                }
            familiar.setLevel(level)
            }
            familiar.setPos(xrnd, yrnd, zrnd)
            println(familiar)
            if (world.spawnEntity(familiar)){
                successes++
            }
        }

        if (successes > 0) {
            return super.placeEntity(world, user, hit, level, effects)
        }
        return false
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_CAT_PURREOW
    }

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.WIT,1200,250,17,imbueLevel,LoreTier.NO_TIER, Items.IRON_HORSE_ARMOR)
    }

    private fun findFamiliar(user: PlayerEntity, world: World): Entity?{
        val uuid = user.uuid
        if (activeFamiliars.containsKey(uuid) && world is ServerWorld){
            return world.getEntity(activeFamiliars[uuid])
        }
        return null
    }

    companion object{

        private const val maxDismissDistance = 10f

        fun removeActiveFamiliar(familiar: ImbuedFamiliarEntity){
            val uuid = familiar.uuid
            var toRemove: UUID? = null
            for (entry in activeFamiliars){
                if (entry.value == uuid){
                    toRemove = entry.key
                    break
                }
            }
            if (toRemove != null){
                val nbt = NbtCompound()
                familiar.writeCustomDataToNbt(nbt)
                storedFamiliars[toRemove] = nbt
                activeFamiliars.remove(toRemove)
            }
        }

        private val activeFamiliars: MutableMap<UUID,UUID> = mutableMapOf()
        private val storedFamiliars: MutableMap<UUID,NbtCompound> = mutableMapOf()
    }
}