package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.BaseAugment
import me.fzzyhmstrs.amethyst_imbuement.util.*
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.*
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World

@Suppress("SameParameterValue")
class TotemItem(settings: Settings): Item(settings), AugmentTasks, ManaItem{

    private var tickCounter = 0
    var angelicCounter = 0
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
            writeNbt(NbtKeys.TOTEM.str(),true,nbt)
            activeEnchantmentTasks(stack, world, user)
            TypedActionResult.success(stack)
        } else {
            val bl = !readNbt(NbtKeys.TOTEM.str(), nbt)
            writeNbt(NbtKeys.TOTEM.str(), bl, nbt)
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
        if(world !is ServerWorld || entity !is PlayerEntity) return
        if (tickCounter < 19){
            tickCounter++
            if (entity.abilities.flying) {
                val count = BaseAugment.readCountFromQueue(entity.uuid,NbtKeys.ANGELIC.str())
                BaseAugment.addCountToQueue(entity.uuid,NbtKeys.ANGELIC.str(),count + 1)
            }
        } else {
            tickCounter = 0
            val nbt = stack.orCreateNbt
            passiveEnchantmentTasks(stack,world,entity)
            if (!readNbt(NbtKeys.TOTEM.str(),nbt)) return
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
                        val bl = (ds != DamageSource.FALL &&
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
                                ds != DamageSource.SWEET_BERRY_BUSH)
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
        if (entity !is PlayerEntity) return
        super.activeEnchantmentTasks(stack, world, entity)
        /*if (EnchantmentHelper.getLevel(RegisterEnchantment.SOUL_OF_THE_CONDUIT,stack) > 0){
            if(entity.isSubmergedInWater){
                entity.addStatusEffect(StatusEffectInstance(StatusEffects.CONDUIT_POWER,100))
                if (damageHandler(stack,world,entity,1)){
                    burnOutHandler(stack,RegisterEnchantment.SOUL_OF_THE_CONDUIT,"Soul of the Conduit augment burnt out!")
                }
            }
        } else if (EnchantmentHelper.getLevel(RegisterEnchantment.ANGELIC,stack) > 0) {
            if (!entity.abilities.creativeMode) {
                entity.abilities.allowFlying = true
                if (angelicCounter > 0){
                    val dmg: Int = if(angelicCounter > 13) {
                        3
                    } else if (angelicCounter > 6) {
                        2
                    } else {
                        1
                    }
                    if (damageHandler(stack,world,entity,dmg)){
                        burnOutHandler(stack,RegisterEnchantment.ANGELIC,"Angelic augment burnt out!")
                        entity.abilities.allowFlying = false
                        entity.abilities.flying = false
                    }
                }
            }
        } else if (EnchantmentHelper.getLevel(RegisterEnchantment.HASTING,stack) > 0) {
            val lvl = EnchantmentHelper.getLevel(RegisterEnchantment.HASTING,stack)
            BaseAugment.addStatusToQueue(entity.uuid,StatusEffects.HASTE,100,lvl-1)
            //entity.addStatusEffect(StatusEffectInstance(StatusEffects.HASTE,100,lvl - 1))
            if (damageHandler(stack,world,entity,1)){
                burnOutHandler(stack,RegisterEnchantment.HASTING,"Hasting augment burnt out!")
            }
        } else if (EnchantmentHelper.getLevel(RegisterEnchantment.STRENGTH,stack) > 0) {
            val lvl = EnchantmentHelper.getLevel(RegisterEnchantment.STRENGTH,stack)
            entity.addStatusEffect(StatusEffectInstance(StatusEffects.STRENGTH,100,lvl-1))
            if(damageHandler(stack,world,entity,2)){
                burnOutHandler(stack,RegisterEnchantment.STRENGTH,"Strength augment burnt out!")
            }
        }*/
    }
    override fun inactiveEnchantmentTasks(stack: ItemStack,world: World, entity: Entity){
        if (entity !is PlayerEntity) return
        super.inactiveEnchantmentTasks(stack, world, entity)
        /*if (entity !is PlayerEntity) return
        if (!entity.abilities.creativeMode) {
            entity.abilities.allowFlying = false
            entity.abilities.flying = false
        }*/
    }
    override fun usageEnchantmentTasks(stack: ItemStack,world: World,entity: Entity){
        if (entity !is PlayerEntity) return
        super.usageEnchantmentTasks(stack, world, entity)
        /*if (EnchantmentHelper.getLevel(RegisterEnchantment.ESCAPE,stack) > 0){
            val rndX = entity.blockPos.x + world.random.nextInt(17) - 8
            val rndZ = entity.blockPos.z + world.random.nextInt(17) - 8
            val rndY = world.getTopY(Heightmap.Type.MOTION_BLOCKING,rndX,rndZ)
            if(checkCanUseHandler(stack,world,entity,120,"Not enough durability for escaping!")) {
                if (damageHandler(stack, world, entity, 120)) {
                    burnOutHandler(stack, RegisterEnchantment.ESCAPE, "Escape augment burnt out!")
                }
                entity.teleport(rndX.toDouble(), (rndY + 1).toDouble(), rndZ.toDouble())
                world.playSound(
                    null,
                    entity.blockPos,
                    SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT,
                    SoundCategory.PLAYERS,
                    1.0F,
                    1.0F
                )
                entity.itemCooldownManager.set(stack.item, 1200)
            }
        }*/
    }
    override fun passiveEnchantmentTasks(stack: ItemStack,world: World,entity: Entity){
        if (entity !is PlayerEntity) return
        super.passiveEnchantmentTasks(stack, world, entity)
        /*if (EnchantmentHelper.getLevel(RegisterEnchantment.LUCKY,stack) > 0) {
            BaseAugment.addStatusToQueue(entity.uuid,StatusEffects.LUCK, 240, 0)
            //entity.addStatusEffect(StatusEffectInstance(StatusEffects.LUCK,100))
        } else if (EnchantmentHelper.getLevel(RegisterEnchantment.IMMUNITY,stack) > 0) {
            RegisterEnchantment.CLEANSE.supportEffect(world,null,entity,1)
        } else if (EnchantmentHelper.getLevel(RegisterEnchantment.MOONLIT,stack) > 0) {
            if (world.isNight){
                val lvl = EnchantmentHelper.getLevel(RegisterEnchantment.MOONLIT,stack)
                val tod = world.timeOfDay
                val comp1 = abs(tod - 13000L)
                val comp2 = abs(tod - 23000L)
                val comp3 = abs(tod - 18000L)
                if((comp3 < comp1) && (comp3 < comp2)){
                    BaseAugment.addStatusToQueue(entity.uuid,StatusEffects.STRENGTH, 400, lvl)
                    BaseAugment.addStatusToQueue(entity.uuid,StatusEffects.RESISTANCE, 400, lvl-1)
                    //entity.addStatusEffect(StatusEffectInstance(StatusEffects.STRENGTH,400,lvl))
                    //entity.addStatusEffect(StatusEffectInstance(StatusEffects.RESISTANCE,400,0))
                } else {
                    BaseAugment.addStatusToQueue(entity.uuid,StatusEffects.STRENGTH, 400, lvl-1)
                    //entity.addStatusEffect(StatusEffectInstance(StatusEffects.STRENGTH,400,lvl-1))
                }
            }
        } else if (EnchantmentHelper.getLevel(RegisterEnchantment.SUNTOUCHED,stack) > 0) {
            if (world.isDay){
                val lvl = EnchantmentHelper.getLevel(RegisterEnchantment.SUNTOUCHED,stack)
                val tod = world.timeOfDay%24000
                val comp1 = abs(tod - 1000L)
                val comp2 = abs(tod - 11000L)
                val comp3 = abs(tod - 6000L)
                if((comp3 < comp1) && (comp3 < comp2)){
                    BaseAugment.addStatusToQueue(entity.uuid,StatusEffects.STRENGTH, 400, lvl)
                    BaseAugment.addStatusToQueue(entity.uuid,StatusEffects.SPEED, 400, lvl-1)
                    //entity.addStatusEffect(StatusEffectInstance(StatusEffects.STRENGTH,400,lvl))
                    //entity.addStatusEffect(StatusEffectInstance(StatusEffects.SPEED,400,lvl-1))
                } else {
                    BaseAugment.addStatusToQueue(entity.uuid,StatusEffects.STRENGTH, 400, lvl-1)
                    //entity.addStatusEffect(StatusEffectInstance(StatusEffects.STRENGTH,400,lvl-1))
                }
            }
        } else if (EnchantmentHelper.getLevel(RegisterEnchantment.HEALTHY,stack) > 0) {
            BaseAugment.addStatusToQueue(entity.uuid,StatusEffects.HEALTH_BOOST, 400, 0)
            //entity.addStatusEffect(StatusEffectInstance(StatusEffects.HEALTH_BOOST,60))
        } else if (EnchantmentHelper.getLevel(RegisterEnchantment.SOULBINDING,stack) > 0) {
            BaseAugment.addStatusToQueue(entity.uuid,AI.SOULBINDING, 240, 0)
            //entity.addStatusEffect(StatusEffectInstance(AI.SOULBINDING,80))
        }*/
    }
    /*fun undyingEnchantmentTasks(stack: ItemStack,world: World,entity: Entity){
        if (entity !is PlayerEntity) return
        if (damageHandler(stack, world, entity, 360)) {
            burnOutHandler(stack, RegisterEnchantment.UNDYING, "Undying augment burnt out!")
        }
    }*/

    companion object: AugmentDamage {
        private fun writeNbt(key: String, state: Boolean, nbt: NbtCompound) {
            nbt.putBoolean(key, state)
        }

        private fun readNbt(key: String, nbt: NbtCompound): Boolean {
            return nbt.getBoolean(key)
        }
    }
}