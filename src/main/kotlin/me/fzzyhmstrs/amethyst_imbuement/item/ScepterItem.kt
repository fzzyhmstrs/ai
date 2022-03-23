package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.amethyst_imbuement.util.*
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.*
import me.fzzyhmstrs.amethyst_imbuement.tool.ScepterMaterialAddon
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

    private val manaRepairTime: Long
    private val defaultManaRepairTime = 150L
    private var smoke = false

    init {
        manaRepairTime = if (material !is ScepterMaterialAddon){
            defaultManaRepairTime
        } else {
            material.healCooldown()
        }
    }

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
        val stats = ScepterObject.getScepterStats(stack)
        val furyText = TranslatableText("scepter.fury.lvl").string + stats[0].toString() + TranslatableText("scepter.xp").string + ScepterObject.xpToNextLevel(stats[3],stats[0]).toString()
        tooltip.add(LiteralText(furyText).formatted(SpellType.FURY.fmt()))
        val graceText = TranslatableText("scepter.grace.lvl").string + stats[1].toString() + TranslatableText("scepter.xp").string + ScepterObject.xpToNextLevel(stats[4],stats[1]).toString()
        tooltip.add(LiteralText(graceText).formatted(SpellType.GRACE.fmt()))
        val witText = TranslatableText("scepter.wit.lvl").string + stats[2].toString() + TranslatableText("scepter.xp").string + ScepterObject.xpToNextLevel(stats[5],stats[2]).toString()
        tooltip.add(LiteralText(witText).formatted(SpellType.WIT.fmt()))
    }

    override fun isFireproof(): Boolean {
        return true
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = user.getStackInHand(hand)
        val activeEnchantId: String
        val testEnchant: Any
        var nbt = stack.nbt
        if (nbt != null) {
            if  (nbt.contains(NbtKeys.ACTIVE_ENCHANT.str())) {
                activeEnchantId = ScepterObject.activeEnchantHelper(world,stack,readAugNbt(NbtKeys.ACTIVE_ENCHANT.str(), nbt))
                testEnchant = Registry.ENCHANTMENT.get(Identifier("amethyst_imbuement",activeEnchantId))?: return resetCooldown(stack,world,user,activeEnchantId)
            } else {
                ScepterObject.initializeScepter(stack,world)
                activeEnchantId = ScepterObject.activeEnchantHelper(world,stack,readAugNbt(NbtKeys.ACTIVE_ENCHANT.str(), nbt))
                testEnchant = Registry.ENCHANTMENT.get(Identifier("amethyst_imbuement",activeEnchantId))?: return resetCooldown(stack,world,user,activeEnchantId)
            }
        } else {
            ScepterObject.initializeScepter(stack,world)
            nbt = stack.nbt
            if (nbt != null) {
                activeEnchantId =
                    ScepterObject.activeEnchantHelper(world,stack, readAugNbt(NbtKeys.ACTIVE_ENCHANT.str(), nbt))
                testEnchant =
                    Registry.ENCHANTMENT.get(Identifier("amethyst_imbuement", activeEnchantId)) ?: return resetCooldown(
                        stack,
                        world,
                        user,
                        activeEnchantId
                    )
            } else {
                return TypedActionResult.fail(stack)
            }
        }
        if (testEnchant !is ScepterAugment) return resetCooldown(stack,world,user,activeEnchantId)

        //determine the level at which to apply the active augment, from 1 to the maximum level the augment can operate
        val level = ScepterObject.getScepterStat(nbt,activeEnchantId).first
        val minLvl = ScepterObject.getAugmentMinLvl(activeEnchantId)
        val maxLevel = (testEnchant.getAugmentMaxLevel()) + minLvl - 1
        var testLevel = 1
        if (level >= minLvl){
            testLevel = level
            if (testLevel > maxLevel) testLevel = maxLevel
            testLevel -= (minLvl - 1)
        }

        if(world.isClient()) {
            return clientUse(world, user, hand, stack, activeEnchantId, testEnchant,testLevel)
        } else {
            val stack2 = if (hand == Hand.MAIN_HAND) {
                user.offHandStack
            } else {
                user.mainHandStack
            }
            if (!stack2.isEmpty) {
                if (stack2.item is BlockItem) {
                    val cht = MinecraftClient.getInstance().crosshairTarget
                    if (cht != null) {
                        if (cht.type == HitResult.Type.BLOCK) {
                            return TypedActionResult.pass(stack)
                        }
                    }
                }
            }
            return serverUse(world, user, hand, stack, activeEnchantId, testEnchant, testLevel)
        }



    }

    private fun serverUse(world: World, user: PlayerEntity, hand: Hand, stack: ItemStack,
                          activeEnchantId: String, testEnchant: ScepterAugment, testLevel: Int): TypedActionResult<ItemStack>{
        val cd : Int? = ScepterObject.useScepter(activeEnchantId, stack, user, world)
        return if (cd != null) {
            val manaReduction = EnchantmentHelper.getLevel(RegisterEnchantment.ATTUNED, stack)
            val manaCost = ScepterObject.getAugmentManaCost(activeEnchantId,manaReduction)
            if (!ScepterObject.checkManaCost(manaCost,stack,world,user)) return resetCooldown(stack,world,user,activeEnchantId)

            if (testEnchant.applyTasks(world, user, hand, testLevel)) {
                ScepterObject.applyManaCost(manaCost,stack, world, user)
                user.itemCooldownManager.set(stack.item, cd)
                TypedActionResult.success(stack)
            } else {
                resetCooldown(stack,world,user,activeEnchantId)
            }
        } else {
            resetCooldown(stack,world,user,activeEnchantId)
        }
    }
    private fun clientUse(world: World, user: PlayerEntity, hand: Hand, stack: ItemStack,
                          activeEnchantId: String, testEnchant: ScepterAugment, testLevel: Int): TypedActionResult<ItemStack>{
        testEnchant.clientTask(world,user,hand,testLevel)
        return TypedActionResult.pass(stack)
    }

    override fun getItemBarColor(stack: ItemStack): Int {
        return MathHelper.hsvToRgb(0.66f,1.0f,1.0f)
    }

    override fun onCraft(stack: ItemStack, world: World, player: PlayerEntity) {
        if(EnchantmentHelper.getLevel(RegisterEnchantment.MAGIC_MISSILE,stack) == 0){
            stack.addEnchantment(RegisterEnchantment.MAGIC_MISSILE,1)
            writeDefaultNbt(stack)
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
                ScepterObject.activeEnchantHelper(world,stack,"magic_missile")
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

    private fun resetCooldown(stack: ItemStack,world: World, user: PlayerEntity, activeEnchant: String): TypedActionResult<ItemStack>{
        world.playSound(null,user.blockPos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS,0.6F,0.8F)
        ScepterObject.resetCooldown(stack,activeEnchant)
        smoke = true
        return TypedActionResult.fail(stack)
    }

    //companion object for the scepter item, handles private functions and other housekeeping

    companion object SI {

        private fun doSmoke(world: World, user: LivingEntity){
            val pos = user.pos
            val smokeX = pos.x - (user.width + 0.8f) * 0.5 * MathHelper.sin(user.bodyYaw * (Math.PI.toFloat() / 180)) - 0.1 * MathHelper.cos(user.bodyYaw * (Math.PI.toFloat() / 180))
            val smokeY = user.eyeY - 0.1
            val smokeZ = pos.z + (user.width + 0.8f) * 0.5 * MathHelper.cos(user.bodyYaw * (Math.PI.toFloat() / 180)) - 0.1 * MathHelper.sin(user.bodyYaw * (Math.PI.toFloat() / 180))
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

        fun writeDefaultNbt(stack: ItemStack){
            val nbt = stack.orCreateNbt
            val key = NbtKeys.ACTIVE_ENCHANT.str()
            if(!nbt.contains(key)){
                val identifier = Registry.ENCHANTMENT.getId(RegisterEnchantment.MAGIC_MISSILE)
                if (identifier != null) {
                    nbt.putString(key, identifier.path)
                    ScepterObject.setLastActiveEnchant(identifier.path)
                }
            }
            ScepterObject.getScepterStats(stack)
        }
    }


    //data class ClientTaskInstance(val target: Entity?, val level: Int, val hit: HitResult?)

    data class EntityTaskInstance(val enchant: Enchantment,val user: LivingEntity, val level: Double, val hit: HitResult?)

}