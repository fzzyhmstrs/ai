package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.amethyst_core.item_util.ManaItem
import me.fzzyhmstrs.amethyst_core.nbt_util.Nbt
import me.fzzyhmstrs.amethyst_core.nbt_util.NbtKeys
import me.fzzyhmstrs.amethyst_core.registry.EventRegistry
import me.fzzyhmstrs.amethyst_core.trinket_util.AugmentDamage
import me.fzzyhmstrs.amethyst_core.trinket_util.BaseAugment
import me.fzzyhmstrs.amethyst_imbuement.util.*
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.*
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World

@Suppress("SameParameterValue")
class TotemItem(settings: Settings): Item(settings), AugmentTasks, ManaItem {

    private var lastGuardian = 0L

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = user.getStackInHand(hand)
        if(world !is ServerWorld) return TypedActionResult.fail(stack)
        val stack2: ItemStack = if (hand == Hand.MAIN_HAND) {
            user.offHandStack
        } else {
            user.mainHandStack
        }
        if (!stack2.isEmpty){ //always defer usage if there is an item in the player's other hand (only activate when other hand empty)
            return TypedActionResult.fail(stack)
        }
        usageEnchantmentTasks(stack, world, user)
        val nbt = stack.orCreateNbt
        return if (!nbt.contains(NbtKeys.TOTEM.str())){
            Nbt.writeBoolNbt(NbtKeys.TOTEM.str(),true,nbt)
            activeEnchantmentTasks(stack, world, user)
            TypedActionResult.success(stack)
        } else {
            val bl = !Nbt.readBoolNbt(NbtKeys.TOTEM.str(), nbt)
            Nbt.writeBoolNbt(NbtKeys.TOTEM.str(), bl, nbt)
            if (bl) {
                activeEnchantmentTasks(stack, world, user)
            } else {
                inactiveEnchantmentTasks(stack, world, user)
            }
            TypedActionResult.success(stack)
        }
    }

    override fun getItemBarColor(stack: ItemStack): Int {
        return MathHelper.hsvToRgb(0.66f,1.0f,1.0f)
    }

    override fun canRepair(stack: ItemStack, ingredient: ItemStack): Boolean {
        return ingredient.isOf(Items.AMETHYST_SHARD) && stack.isOf(RegisterItem.TOTEM_OF_AMETHYST)
    }

    override fun inventoryTick(stack: ItemStack, world: World, entity: Entity, slot: Int, selected: Boolean) {
        if(world.isClient || entity !is PlayerEntity) return
        if (EventRegistry.ticker_30.isNotReady()){
            if (entity.abilities.flying) {
                val count = BaseAugment.readCountFromQueue(entity.uuid,NbtKeys.ANGELIC.str())
                BaseAugment.addCountToQueue(entity.uuid,NbtKeys.ANGELIC.str(),count + 1)
            }
        } else {
            val nbt = stack.orCreateNbt
            passiveEnchantmentTasks(stack,world,entity)
            if (!Nbt.readBoolNbt(NbtKeys.TOTEM.str(),nbt)) return
            activeEnchantmentTasks(stack,world,entity)
        }
        if (EnchantmentHelper.getLevel(RegisterEnchantment.GUARDIAN,stack) > 0){ //check for guardian every tick for max responsiveness
            val health = entity.health
            val maxHealth = entity.maxHealth
            val healthPercent = (health/maxHealth*100.0).toInt()
            if (healthPercent <= 20) {
                val ds = entity.recentDamageSource
                if (ds != null) {
                    if (world.time - lastGuardian >= 40) {
                        val bl = (checkDamageSource(ds))
                        if (bl) {
                            RegisterEnchantment.GUARDIAN.specialEffect(entity, 1, stack)
                            lastGuardian = world.time
                        }
                    }
                }

            }
        }
    }

    override fun activeEnchantmentTasks(stack: ItemStack, world: World, entity: Entity){
        if (!entity.isPlayer) return
        super.activeEnchantmentTasks(stack, world, entity)

    }

    override fun inactiveEnchantmentTasks(stack: ItemStack,world: World, entity: Entity){
        if (!entity.isPlayer) return
        super.inactiveEnchantmentTasks(stack, world, entity)

    }

    override fun usageEnchantmentTasks(stack: ItemStack,world: World,entity: Entity){
        if (!entity.isPlayer) return
        super.usageEnchantmentTasks(stack, world, entity)

    }

    override fun passiveEnchantmentTasks(stack: ItemStack,world: World,entity: Entity){
        if (!entity.isPlayer) return
        super.passiveEnchantmentTasks(stack, world, entity)

    }

    companion object: AugmentDamage {

        private fun checkDamageSource(ds: DamageSource): Boolean{
            return ds != DamageSource.FALL &&
                    ds != DamageSource.CACTUS &&
                    ds != DamageSource.FLY_INTO_WALL &&
                    ds != DamageSource.FALLING_BLOCK &&
                    ds != DamageSource.HOT_FLOOR &&
                    ds != DamageSource.IN_FIRE &&
                    ds != DamageSource.DROWN &&
                    ds != DamageSource.OUT_OF_WORLD &&
                    ds != DamageSource.STALAGMITE &&
                    ds != DamageSource.IN_WALL &&
                    ds != DamageSource.FREEZE &&
                    ds != DamageSource.DRYOUT &&
                    ds != DamageSource.STARVE &&
                    ds != DamageSource.SWEET_BERRY_BUSH
        }
    }
}