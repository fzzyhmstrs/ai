package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.amethyst_imbuement.util.*
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.*
import net.minecraft.client.MinecraftClient
import net.minecraft.client.item.TooltipContext
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.mob.ZombieEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.*
import net.minecraft.nbt.NbtCompound
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.*
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.MathHelper
import net.minecraft.util.registry.Registry
import net.minecraft.world.World
import java.util.*

@Suppress("SameParameterValue", "unused", "USELESS_IS_CHECK")
class ScepterItem(material: ToolMaterial, settings: Settings): ToolItem(material,settings), ManaItem {

    override fun appendTooltip(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<Text>,
        context: TooltipContext?
    ) {
        super.appendTooltip(stack, world, tooltip, context)
        val nbt = stack.orCreateNbt
        val activeSpell = if (nbt.contains(NbtKeys.ACTIVE_ENCHANT.str())) {
            val activeEnchantId = readAugNbt(NbtKeys.ACTIVE_ENCHANT.str(), nbt)
            TranslatableText("enchantment.amethyst_imbuement.$activeEnchantId")
        } else {
            TranslatableText("enchantment.amethyst_imbuement.none")
        }
        tooltip.add(TranslatableText("scepter.active_spell").formatted(Formatting.GOLD).append(activeSpell.formatted(Formatting.GOLD)))
        val stats = getStats(stack)
        //tooltip.add(LiteralText("Fury: ${stats[0]}, XP to next: ${ScepterObject.xpToNextLevel(stats[3],stats[0])}").formatted(Formatting.RED))
        val furyText = TranslatableText("scepter.fury.lvl").string + stats[0].toString() + TranslatableText("scepter.xp").string + ScepterObject.xpToNextLevel(stats[3],stats[0]).toString()
        tooltip.add(LiteralText(furyText).formatted(SpellType.FURY.fmt()))
        //tooltip.add(LiteralText("Grace: ${stats[1]}, XP to next: ${ScepterObject.xpToNextLevel(stats[4],stats[1])}").formatted(Formatting.GREEN))
        val graceText = TranslatableText("scepter.grace.lvl").string + stats[1].toString() + TranslatableText("scepter.xp").string + ScepterObject.xpToNextLevel(stats[4],stats[1]).toString()
        tooltip.add(LiteralText(graceText).formatted(SpellType.GRACE.fmt()))
        //tooltip.add(LiteralText("Wit: ${stats[2]}, XP to next: ${ScepterObject.xpToNextLevel(stats[5],stats[2])}").formatted(Formatting.BLUE))
        val witText = TranslatableText("scepter.wit.lvl").string + stats[2].toString() + TranslatableText("scepter.xp").string + ScepterObject.xpToNextLevel(stats[5],stats[2]).toString()
        tooltip.add(LiteralText(witText).formatted(SpellType.WIT.fmt()))
    }

    override fun isFireproof(): Boolean {
        return true
    }

    /*override fun isDamageable(): Boolean {
        return false
    }*/

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = user.getStackInHand(hand)

        if(world.isClient()) return TypedActionResult.fail(stack)
        val stack2 = if (hand == Hand.MAIN_HAND){
            user.offHandStack
        } else {
            user.mainHandStack
        }
        if (!stack2.isEmpty){
            if (stack2.item is BlockItem){
                val cht = MinecraftClient.getInstance().crosshairTarget
                if(cht != null){
                    if (cht.type == HitResult.Type.BLOCK) {
                        return TypedActionResult.pass(stack)
                    }
                }
            }
        }
        val activeEnchantId: String
        val testEnchant: Any
        val nbt = stack.nbt
        if (nbt != null) {
            if  (nbt.contains(NbtKeys.ACTIVE_ENCHANT.str())) {
                activeEnchantId = ScepterObject.activeEnchantHelper(stack,readAugNbt(NbtKeys.ACTIVE_ENCHANT.str(), nbt))
                testEnchant = Registry.ENCHANTMENT.get(Identifier("amethyst_imbuement",activeEnchantId))?: return resetCooldown(stack,world,user,activeEnchantId)
            } else {
                return TypedActionResult.fail(stack)
            }
        } else {
            return TypedActionResult.fail(stack)
        }

        //activeEnchantId = ScepterObject.activeEnchantHelper(stack,readAugNbt(NbtKeys.ACTIVE_ENCHANT.str(), nbt))
        val cd : Int? = ScepterObject.useScepter(activeEnchantId, stack, user, world)
        return if (cd != null) {
            val manaReduction = EnchantmentHelper.getLevel(RegisterEnchantment.ATTUNED, stack)
            val level = ScepterObject.getScepterStat(nbt,activeEnchantId).first
            //val testEnchant = Registry.ENCHANTMENT.get(Identifier("amethyst_imbuement",activeEnchantId))?: return resetCooldown(stack,world,user,activeEnchantId)
            val manaCost = ScepterObject.getAugmentManaCost(activeEnchantId,manaReduction)
            println("mana cost: $manaCost")
            if (!ScepterObject.checkManaCost(manaCost,stack,world,user)) return resetCooldown(stack,world,user,activeEnchantId)
            testEnchant as ScepterAugment
            //determine the level at which to apply the active augment, from 1 to the maximum level the augment can operate
            val minLvl = ScepterObject.getAugmentMinLvl(activeEnchantId)
            val maxLevel = (testEnchant.getAugmentMaxLevel()) + minLvl - 1
            var testLevel = 1
            if (level >= minLvl){
                testLevel = level
                if (testLevel > maxLevel) testLevel = maxLevel
                testLevel -= (ScepterObject.getAugmentMinLvl(activeEnchantId) - 1)
            }

            if (testEnchant is SummonProjectileAugment) {
                val activeEnchant = testEnchant as? SummonProjectileAugment
                val pierce = activeEnchantId == "soul_missile"
                val projectile = activeEnchant?.entityClass(world, user as LivingEntity, pierce)
                val soundEvent = activeEnchant?.soundEvent()
                if (projectile != null && soundEvent != null) {
                    ScepterObject.applyManaCost(manaCost,stack, world, user)
                    ScepterObject.spawnProjectileEntity(world, user as LivingEntity, projectile, soundEvent)
                    if (testEnchant.needsClient()) clientTasks[testEnchant] = ClientTaskInstance(null,level,null)
                    user.itemCooldownManager.set(stack.item, cd)
                    TypedActionResult.success(stack)
                } else {
                    resetCooldown(stack,world,user,activeEnchantId)
                }

            } else if (testEnchant is MinorSupportAugment) {
                val activeEnchant = testEnchant as? MinorSupportAugment
                var target = RaycasterUtil.raycastEntity(distance = activeEnchant?.rangeOfEffect()?: 5.0)
                if (target == null){
                    target = ZombieEntity(world)
                }
                if (activeEnchant?.supportEffect(world,target,user,testLevel) == true){
                    ScepterObject.applyManaCost(manaCost,stack, world, user)
                    if (testEnchant.needsClient()) clientTasks[testEnchant] = ClientTaskInstance(target,testLevel,null)
                    user.itemCooldownManager.set(stack.item, cd)
                    TypedActionResult.success(stack)
                } else {
                    resetCooldown(stack,world,user,activeEnchantId)
                }

            } else if (testEnchant is PlaceItemAugment) {
                val activeEnchant = testEnchant as? PlaceItemAugment
                val hit = MinecraftClient.getInstance().crosshairTarget ?: return resetCooldown(stack,world,user,activeEnchantId)
                if (hit.type != HitResult.Type.BLOCK) return resetCooldown(stack,world,user,activeEnchantId)
                val testStack = activeEnchant?.itemToPlace(world,user)?: return resetCooldown(stack,world,user,activeEnchantId)
                val testItem = testStack.item
                if(testItem is BlockItem) {
                    ScepterObject.applyManaCost(manaCost,stack, world, user)
                    testItem.place(ItemPlacementContext(user,hand, ItemStack(testItem),hit as BlockHitResult))
                    world.playSound(null,user.blockPos,testEnchant.soundEvent(),SoundCategory.NEUTRAL,1.0f,1.0f)
                    if (testEnchant.needsClient()) clientTasks[testEnchant] = ClientTaskInstance(null,testLevel,hit)
                    user.itemCooldownManager.set(stack.item, cd)
                    return TypedActionResult.success(stack)
                } else if(testItem is BucketItem){
                    if (testItem.placeFluid(user,world,(hit as BlockHitResult).blockPos,hit)) {
                        world.playSound(null,user.blockPos,testEnchant.soundEvent(),SoundCategory.NEUTRAL,1.0f,1.0f)
                        ScepterObject.applyManaCost(manaCost,stack, world, user)
                        if (testEnchant.needsClient()) clientTasks[testEnchant] = ClientTaskInstance(null,testLevel,hit)
                        user.itemCooldownManager.set(stack.item, cd)
                        return TypedActionResult.success(stack)
                    } else {
                        resetCooldown(stack,world,user,activeEnchantId)
                    }
                }
                resetCooldown(stack,world,user,activeEnchantId)

            } else if (testEnchant is SummonEntityAugment) {
                val activeEnchant = testEnchant as? SummonEntityAugment
                val hit = RaycasterUtil.raycastHit(distance = MinecraftClient.getInstance().interactionManager?.reachDistance?.toDouble()?:3.0, includeFluids = true) ?: return resetCooldown(stack,world,user,activeEnchantId)
                if (hit.type != HitResult.Type.BLOCK) return resetCooldown(stack,world,user,activeEnchantId)
                if (activeEnchant?.placeEntity(world,user,hit,testLevel) == true){
                    ScepterObject.applyManaCost(manaCost,stack, world, user)
                    if (testEnchant.needsClient()) clientTasks[testEnchant] = ClientTaskInstance(null,testLevel,hit)
                    user.itemCooldownManager.set(stack.item, cd)
                    return TypedActionResult.success(stack)
                }
                resetCooldown(stack,world,user,activeEnchantId)

            }else if (testEnchant is MiscAugment) {
                val activeEnchant = testEnchant as? MiscAugment
                var target: Entity? = null
                val hit = RaycasterUtil.raycastHit(distance = activeEnchant?.rangeOfEffect()?:4.0, includeFluids = true) ?: return resetCooldown(stack,world,user,activeEnchantId)
                if (hit.type == HitResult.Type.ENTITY){
                    target = (hit as EntityHitResult).entity
                }
                if (activeEnchant?.effect(world,target,user,testLevel,hit) == true){
                    ScepterObject.applyManaCost(manaCost,stack, world, user)
                    if (testEnchant.needsClient()) clientTasks[testEnchant] = ClientTaskInstance(target,testLevel,hit)
                    user.itemCooldownManager.set(stack.item, cd)
                    return TypedActionResult.success(stack)
                }
                resetCooldown(stack,world,user,activeEnchantId)

            } else{
                resetCooldown(stack,world,user,activeEnchantId)
            }
        } else {
            resetCooldown(stack,world,user,activeEnchantId)
        }
    }

    override fun getItemBarColor(stack: ItemStack): Int {
        return MathHelper.hsvToRgb(0.66f,1.0f,1.0f)
    }

    override fun onCraft(stack: ItemStack, world: World, player: PlayerEntity) {
        if(EnchantmentHelper.getLevel(RegisterEnchantment.MAGIC_MISSILE,stack) == 0){
            stack.addEnchantment(RegisterEnchantment.MAGIC_MISSILE,1)
            val nbt = stack.orCreateNbt
            writeDefaultNbt(NbtKeys.ACTIVE_ENCHANT.str(),nbt)
            ScepterObject.initializeScepter(stack,world)
        }
    }

    //removes cooldown on the item if you switch item
    override fun inventoryTick(stack: ItemStack, world: World, entity: Entity, slot: Int, selected: Boolean) {
        if(world !is ServerWorld) {
            if (smoke){
                doSmoke(world,entity as LivingEntity)
                smoke = false
            }
            if (entity !is PlayerEntity) return
            if (clientTasks.isNotEmpty()) {
                for (task in clientTasks.keys) {
                    if (task is ScepterAugment) {
                        val target = clientTasks[task]?.target
                        val level = clientTasks[task]?.level ?: 1
                        val hit = clientTasks[task]?.hit
                        task.clientTask(world, target, entity, level, hit)
                    }
                }
                clientTasks.clear()
            }

            return
        }
        if (entity !is PlayerEntity) return

        val id = ScepterObject.scepterTickNbtCheck(stack)
        if (id > 0){
            if (ScepterObject.getPersistentTickerNeed(id)){
                ScepterObject.persistentEffectTicker(id)
            }
            val chk = ScepterObject.shouldRemoveCooldownChecker(id)
            if (chk > 0){
                entity.itemCooldownManager.set(stack.item,chk)
                ScepterObject.activeEnchantHelper(stack,"magic_missile")
            } else if (chk == 0){
                entity.itemCooldownManager.remove(stack.item)
            }
        }
        //slowly heal damage over time
        if (world.time%manaRepairTime == 0L){
            healDamage(1,stack)
        }
    }

    override fun getUseAction(stack: ItemStack): UseAction {
        return UseAction.BLOCK
    }

    //companion object for the scepter item, handles private functions and other housekeeping

    companion object SI {

        private val clientTasks: MutableMap<Enchantment, ClientTaskInstance> = mutableMapOf()
        private val entityTasks: MutableMap<UUID, MutableList<EntityTaskInstance>> = mutableMapOf()
        private var smoke = false
        private const val manaRepairTime = 150L
        private fun resetCooldown(stack: ItemStack,world: World, user: PlayerEntity, activeEnchant: String): TypedActionResult<ItemStack>{
            world.playSound(null,user.blockPos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS,0.6F,0.8F)
            ScepterObject.resetCooldown(stack,activeEnchant)
            smoke = true
            return TypedActionResult.fail(stack)
        }
        private fun doSmoke(world: World, user: LivingEntity){
            val smokeX = user.x - (user.width + 0.8f) * 0.5 * MathHelper.sin(user.bodyYaw * (Math.PI.toFloat() / 180)) - 0.1 * MathHelper.cos(user.bodyYaw * (Math.PI.toFloat() / 180))
            val smokeY = user.eyeY - 0.1
            val smokeZ = user.z + (user.width + 0.8f) * 0.5 * MathHelper.cos(user.bodyYaw * (Math.PI.toFloat() / 180)) - 0.1 * MathHelper.sin(user.bodyYaw * (Math.PI.toFloat() / 180))
            world.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,smokeX,smokeY,smokeZ,user.velocity.x,user.velocity.y + 0.5,user.velocity.z)
        }

        private fun writeAugNbt(key: String, enchant: String, nbt: NbtCompound){
            nbt.putString(key,enchant)
        }
        private fun readAugNbt(key: String, nbt: NbtCompound): String {
            return nbt.getString(key)
        }
        private fun readStatNbt(key: String, nbt: NbtCompound): Int {
            return nbt.getInt(key)
        }

        fun writeDefaultNbt(key: String, nbt: NbtCompound){
            if(!nbt.contains(key)){
                val identifier = Registry.ENCHANTMENT.getId(RegisterEnchantment.MAGIC_MISSILE)
                if (identifier != null) {
                    nbt.putString(key, identifier.path)
                    ScepterObject.setLastActiveEnchant(identifier.path)
                }
            }
            getStatsHelper(nbt)
        }

        private fun getStats(stack: ItemStack): IntArray {
            val nbt = stack.orCreateNbt
            return getStatsHelper(nbt)
        }

        private fun getStatsHelper(nbt: NbtCompound): IntArray{
            val stats = intArrayOf(0,0,0,0,0,0)
            if(!nbt.contains("FURY_lvl")){
                nbt.putInt("FURY_lvl",1)
            }
            stats[0] = nbt.getInt("FURY_lvl")
            if(!nbt.contains("GRACE_lvl")){
                nbt.putInt("GRACE_lvl",1)
            }
            stats[1] = nbt.getInt("GRACE_lvl")
            if(!nbt.contains("WIT_lvl")){
                nbt.putInt("WIT_lvl",1)
            }
            stats[2] = nbt.getInt("WIT_lvl")
            if(!nbt.contains("FURY_xp")){
                nbt.putInt("FURY_xp",0)
            }
            stats[3] = nbt.getInt("FURY_xp")
            if(!nbt.contains("GRACE_xp")){
                nbt.putInt("GRACE_xp",0)
            }
            stats[4] = nbt.getInt("GRACE_xp")
            if(!nbt.contains("WIT_xp")){
                nbt.putInt("WIT_xp",0)
            }
            stats[5] = nbt.getInt("WIT_xp")
            return stats
        }

        fun checkEntityTaskQueue(): Boolean{
            return entityTasks.isNotEmpty()
        }

        fun applyEntityTasks(entity: LivingEntity){
            val uuid = entity.uuid
            if (entityTasks.containsKey(uuid)){
                val list = entityTasks[uuid]
                if (list?.isNotEmpty() == true) {
                    for (data in list){
                        val enchant = data.enchant
                        if (enchant is ScepterAugment){
                            enchant.entityTask(entity.world,entity,data.user,data.level,data.hit)
                        }
                    }
                }
                removeEntitiesFromQueue(uuid)
            }
        }

        fun addEntityToQueue(uuid: UUID, dataInstance: EntityTaskInstance){
            if (entityTasks.containsKey(uuid)){
                entityTasks[uuid]?.add(dataInstance)
            } else {
                entityTasks[uuid] = mutableListOf()
                entityTasks[uuid]?.add(dataInstance)
            }
        }

        private fun removeEntitiesFromQueue(uuid: UUID){
            if (entityTasks.containsKey(uuid)){
                entityTasks.remove(uuid)
            }
        }
    }

    private data class ClientTaskInstance(val target: Entity?, val level: Int, val hit: HitResult?)

    data class EntityTaskInstance(val enchant: Enchantment,val user: LivingEntity, val level: Double, val hit: HitResult?)

}