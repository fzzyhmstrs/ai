package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.amethyst_core.nbt.NbtKeys
import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.ActiveAugment
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterTag
import me.fzzyhmstrs.fzzy_core.interfaces.Modifiable
import me.fzzyhmstrs.fzzy_core.mana_util.ManaItem
import me.fzzyhmstrs.fzzy_core.registry.EventRegistry
import me.fzzyhmstrs.fzzy_core.trinket_util.AugmentTasks
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

@Suppress("SameParameterValue")
class TotemItem(settings: Settings): Item(settings), AugmentTasks, Modifiable, ManaItem {

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
            return TypedActionResult.pass(stack)
        }
        usageEnchantmentTasks(stack, world, user)
        val nbt = stack.orCreateNbt
        return if (!nbt.contains(NbtKeys.TOTEM.str())){
            if (canActivateTasks(stack,world,user)) {
                nbt.putBoolean(NbtKeys.TOTEM.str(), true)
                activeEnchantmentTasks(stack, world, user)
                TypedActionResult.success(stack)
            } else {
                TypedActionResult.fail(stack)
            }
        } else {
            val bl = !nbt.getBoolean(NbtKeys.TOTEM.str())
            if (bl){
                if (!canActivateTasks(stack,world,user)){
                    return TypedActionResult.fail(stack)
                }
            }
            nbt.putBoolean(NbtKeys.TOTEM.str(), bl)
            if (bl) {
                activeEnchantmentTasks(stack, world, user)
            } else {
                inactiveEnchantmentTasks(stack, world, user)
            }
            TypedActionResult.success(stack)
        }
    }

    override fun getItemBarColor(stack: ItemStack): Int {
        return AiConfig.items.manaItems.getItemBarColor(stack)
    }

    override fun canRepair(stack: ItemStack, ingredient: ItemStack): Boolean {
        return ingredient.isOf(Items.AMETHYST_SHARD) && stack.isOf(RegisterItem.TOTEM_OF_AMETHYST)
    }

    override fun isFireproof(): Boolean {
        return true
    }

    override fun inventoryTick(stack: ItemStack, world: World, entity: Entity, slot: Int, selected: Boolean) {
        if(world.isClient || entity !is PlayerEntity) return
        if (EventRegistry.ticker_20.isReady()){
            val nbt = stack.orCreateNbt
            passiveEnchantmentTasks(stack,world,entity)
            if (!nbt.getBoolean(NbtKeys.TOTEM.str())) return
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

    fun canActivateTasks(stack: ItemStack, world: World, entity: LivingEntity): Boolean{
        val enchants = EnchantmentHelper.get(stack)
        for (enchant in enchants.keys){
            if (enchant is ActiveAugment){
                val lvl = enchants[enchant] ?: 1
                if (!enchant.canActivate(entity,lvl, stack)) return false
            }
        }
        return true
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

    companion object {

        fun getActive(stack: ItemStack): Boolean{
            val nbt = stack.nbt ?: return false
            return if (!nbt.contains(NbtKeys.TOTEM.str())){
                false
            } else {
                nbt.getBoolean(NbtKeys.TOTEM.str())
            }
        }

        private fun checkDamageSource(ds: DamageSource): Boolean{
            return !ds.isIn(RegisterTag.GUARDIAN_IGNORES_DAMAGE_TAG)
        }
    }

    override fun getRepairTime(): Int {
        return 0
    }
}