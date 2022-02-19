package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_imbuement.entity.UnhallowedEntity
import me.fzzyhmstrs.amethyst_imbuement.item.ImbuedJewelryItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEntity
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.SummonEntityAugment
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.EntityType
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.passive.StriderEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World
import kotlin.math.max
import kotlin.math.min

class SummonZombieAugment(weight: Rarity, _tier: Int, _maxLvl: Int, vararg slot: EquipmentSlot): SummonEntityAugment(weight, _tier, _maxLvl, *slot) {
    override fun placeEntity(world: World, user: PlayerEntity, hit: HitResult, level: Int): Boolean {
        val bonus = max(0,level-3)
        for(i in 1..min(3,level)) {
            val xrnd: Double = (hit as BlockHitResult).blockPos.x + (world.random.nextDouble() * 4.0 - 2.0)
            val zrnd: Double = (hit).blockPos.z + (world.random.nextDouble() * 4.0 - 2.0)
            val yrnd = hit.blockPos.y + 1.0
            val zom = UnhallowedEntity(RegisterEntity.UNHALLOWED_ENTITY, world,2400, bonus)
            zom.setPos(xrnd, yrnd, zrnd)
            world.spawnEntity(zom)
        }
        world.playSound(null,user.blockPos,soundEvent(), SoundCategory.PLAYERS,1.0F,1.0F)
        return true
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_ZOMBIE_AMBIENT
    }
}