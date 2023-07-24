package me.fzzyhmstrs.amethyst_imbuement.spells

import eu.pb4.common.protection.api.CommonProtection
import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.SpellActionResult
import me.fzzyhmstrs.amethyst_core.augments.base.PlaceItemAugment
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
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks.or
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import me.fzzyhmstrs.fzzy_core.entity_util.PlayerCreatable
import net.minecraft.block.Blocks
import net.minecraft.block.ShapeContext
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.Tameable
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class CreateLavaAugment: PlaceItemAugment(ScepterTier.TWO, Items.LAVA_BUCKET){
    //ml 1
    override val augmentData: AugmentDatapoint =
        AugmentDatapoint(AI.identity("create_lava"),SpellType.WIT,200,50,
            11,1,1,10, LoreTier.LOW_TIER, Items.LAVA_BUCKET)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        when (other){
            RegisterEnchantment.SUMMON_HAMSTER -> {
                description.addLang("enchantment.amethyst_imbuement.create_lava.summon_hamster.desc", SpellAdvancementChecks.UNIQUE.or(SpellAdvancementChecks.FLAME))
            }
            RegisterEnchantment.SUMMON_GOLEM -> {
                description.addLang("enchantment.amethyst_imbuement.create_lava.summon_golem.desc", SpellAdvancementChecks.UNIQUE.or(SpellAdvancementChecks.FLAME))
            }
            is PlaceItemAugment -> {
                description.addLang("enchantment.amethyst_imbuement.create_lava.desc.block", arrayOf(other.item(),itemAfterLavaTransform(other.item())), SpellAdvancementChecks.BLOCK)
            }
        }
        if (othersType.has(AugmentType.DAMAGE)) {
            description.addLang("enchantment.amethyst_imbuement.create_lava.desc.damage", SpellAdvancementChecks.DAMAGE)
            if (othersType.has(AugmentType.PROJECTILE)) {
                description.addLang("enchantment.amethyst_imbuement.create_lava.desc.projectile1", SpellAdvancementChecks.DAMAGE)
                description.addLang("enchantment.amethyst_imbuement.create_lava.desc.projectile2", SpellAdvancementChecks.DAMAGE)
            }
        }
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        return arrayOf(pairedSpell.provideAdjective(this))
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        if (pair.spellsAreEqual()){
            SpellAdvancementChecks.grant(player, SpellAdvancementChecks.DOUBLE_TRIGGER)
        }
        if (pair.spellsAreUnique()){
            SpellAdvancementChecks.grant(player, SpellAdvancementChecks.UNIQUE_TRIGGER)
        }
        SpellAdvancementChecks.grant(player, SpellAdvancementChecks.FLAME_TRIGGER)
    }

    override fun modifyManaCost(
        manaCost: PerLvlI,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlI {
        if (othersType.has(AugmentType.DAMAGE) && othersType.has(AugmentType.PROJECTILE))
            return manaCost.plus(50)
        return manaCost
    }

    override fun modifyCooldown(
        cooldown: PerLvlI,
        other: ScepterAugment,
        othersType: AugmentType,
        spells: PairedAugments
    ): PerLvlI {
        if (othersType.has(AugmentType.DAMAGE) && othersType.has(AugmentType.PROJECTILE))
            return cooldown.plus(0,0,25)
        return cooldown
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
        if (othersType.has(AugmentType.DAMAGE)){
            entityHitResult.entity.setOnFireFor(8)
            val owner = user as? PlayerEntity
                ?: (user as? Tameable)?.owner as? PlayerEntity
                ?: (user as? PlayerCreatable)?.entityOwner as? PlayerEntity
            if (othersType.has(AugmentType.PROJECTILE) && owner != null){
                val blockPos = BlockPos.ofFloored(entityHitResult.pos)
                if (!CommonProtection.canPlaceBlock(world,blockPos,owner.gameProfile,owner)) return SUCCESSFUL_PASS
                val state = Blocks.LAVA.defaultState
                if(!(world.canPlayerModifyAt(owner,blockPos) && world.getBlockState(blockPos).isReplaceable && world.canPlace(state,blockPos, ShapeContext.of(user)) && state.canPlaceAt(world,blockPos))) return SUCCESSFUL_PASS
                world.setBlockState(blockPos,state)
            }
        }
        return SUCCESSFUL_PASS
    }

    override fun <T> customItemPlaceOnBlockHit(
        startItem: Item,
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
    ): Item where T : SpellCastingEntity, T : LivingEntity {
        return items[startItem]?:startItem
    }

    override fun hitSoundEvent(world: World, blockPos: BlockPos, context: ProcessContext) {
        world.playSound(null,blockPos,SoundEvents.ITEM_BUCKET_EMPTY_LAVA,SoundCategory.PLAYERS,1.0f,1.0f)
    }

    private fun itemAfterLavaTransform(item: Item): Item {
        return items[item]?:item
    }

    companion object{
        val items: Map<Item, Item> = mapOf(
            Items.WATER_BUCKET to Items.COBBLESTONE,
            Items.SPONGE to Items.MAGMA_BLOCK
        )
    }
}
