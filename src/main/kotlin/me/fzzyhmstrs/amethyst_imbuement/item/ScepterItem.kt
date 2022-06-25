package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.util.*
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.ScepterObject
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.*
import me.fzzyhmstrs.amethyst_imbuement.tool.ScepterMaterialAddon
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.client.MinecraftClient
import net.minecraft.client.item.TooltipContext
import net.minecraft.client.option.Perspective
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.*
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.*
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.util.registry.Registry
import net.minecraft.world.World
import kotlin.math.max

@Suppress("SameParameterValue", "unused", "USELESS_IS_CHECK")
open class ScepterItem(material: ToolMaterial, settings: Settings, vararg defaultModifier: Identifier): ToolItem(material, settings), ManaItem {

    private val tickerManaRepair: Long
    private val defaultModifiers: MutableList<Identifier> = mutableListOf()

    init {
        tickerManaRepair = if (material !is ScepterMaterialAddon){
            AiConfig.scepters.baseRegenRateTicks
        } else {
            material.healCooldown()
        }
        defaultModifier.forEach {
            defaultModifiers.add(it)
        }
    }

    fun getRepairTime(): Int{
        return tickerManaRepair.toInt()
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
            val activeEnchantId = Nbt.readStringNbt(NbtKeys.ACTIVE_ENCHANT.str(), nbt)
            Text.translatable("enchantment.amethyst_imbuement.${Identifier(activeEnchantId).path}")
        } else {
            Text.translatable("enchantment.amethyst_imbuement.none")
        }
        tooltip.add(Text.translatable("scepter.active_spell").formatted(Formatting.GOLD).append(activeSpell.formatted(Formatting.GOLD)))
        val stats = ScepterObject.getScepterStats(stack)
        val furyText = Text.translatable("scepter.fury.lvl").string + stats[0].toString() + Text.translatable("scepter.xp").string + ScepterObject.xpToNextLevel(stats[3],stats[0]).toString()
        tooltip.add(Text.literal(furyText).formatted(SpellType.FURY.fmt()))
        val graceText = Text.translatable("scepter.grace.lvl").string + stats[1].toString() + Text.translatable("scepter.xp").string + ScepterObject.xpToNextLevel(stats[4],stats[1]).toString()
        tooltip.add(Text.literal(graceText).formatted(SpellType.GRACE.fmt()))
        val witText = Text.translatable("scepter.wit.lvl").string + stats[2].toString() + Text.translatable("scepter.xp").string + ScepterObject.xpToNextLevel(stats[5],stats[2]).toString()
        tooltip.add(Text.literal(witText).formatted(SpellType.WIT.fmt()))
        val modifierList = ScepterObject.getModifiers(stack)
        if (modifierList.isNotEmpty()){
            val modifierText = Text.translatable("scepter.modifiers").formatted(Formatting.GOLD)

            val itr = modifierList.asIterable().iterator()
            while(itr.hasNext()){
                val mod = itr.next()
                modifierText.append(Text.translatable("scepter.modifiers.${mod}").formatted(Formatting.GOLD))
                if (itr.hasNext()){
                    modifierText.append(commaText)
                }
            }
            tooltip.add(modifierText)
        }
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
                activeEnchantId = ScepterObject.activeEnchantHelper(world,stack,Nbt.readStringNbt(NbtKeys.ACTIVE_ENCHANT.str(), nbt))
                testEnchant = Registry.ENCHANTMENT.get(Identifier(activeEnchantId))?: return resetCooldown(stack,world,user,activeEnchantId)
            } else {
                ScepterObject.initializeScepter(stack,world)
                activeEnchantId = ScepterObject.activeEnchantHelper(world,stack,Nbt.readStringNbt(NbtKeys.ACTIVE_ENCHANT.str(), nbt))
                testEnchant = Registry.ENCHANTMENT.get(Identifier(activeEnchantId))?: return resetCooldown(stack,world,user,activeEnchantId)
            }
        } else {
            ScepterObject.initializeScepter(stack,world)
            nbt = stack.nbt
            if (nbt != null) {
                activeEnchantId =
                    ScepterObject.activeEnchantHelper(world,stack, Nbt.readStringNbt(NbtKeys.ACTIVE_ENCHANT.str(), nbt))
                testEnchant =
                    Registry.ENCHANTMENT.get(Identifier(activeEnchantId)) ?: return resetCooldown(
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

        val stack2 = if (hand == Hand.MAIN_HAND) {
            user.offHandStack
        } else {
            user.mainHandStack
        }
        if(world.isClient()) {
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
            return clientUse(world, user, hand, stack, activeEnchantId, testEnchant,testLevel)
        } else {
            if (!stack2.isEmpty) {
                if (stack2.item is BlockItem) {
                    val reachDistance = if (user.abilities.creativeMode){
                        5.0
                    } else {
                        4.5
                    }
                    val cht = RaycasterUtil.raycastBlock(distance = reachDistance,entity = user)
                    if (cht != null) {
                        return TypedActionResult.pass(stack)
                    }
                }
            }
            return serverUse(world, user, hand, stack, activeEnchantId, testEnchant, testLevel)
        }
    }

    private fun serverUse(world: World, user: PlayerEntity, hand: Hand, stack: ItemStack,
                          activeEnchantId: String, testEnchant: ScepterAugment, testLevel: Int): TypedActionResult<ItemStack>{

        val modifiers = ScepterObject.getActiveModifiers(stack)

        val cd : Int? = ScepterObject.useScepter(activeEnchantId, stack, user, world, modifiers.compiledData.cooldownModifier)
        return if (cd != null) {
            val manaCost = ScepterObject.getAugmentManaCost(activeEnchantId,modifiers.compiledData.manaCostModifier)
            if (!ScepterObject.checkManaCost(manaCost,stack,world,user)) return resetCooldown(stack,world,user,activeEnchantId)
            val level = max(1,testLevel + modifiers.compiledData.levelModifier)
            if (testEnchant.applyModifiableTasks(world, user, hand, level, modifiers.modifiers, modifiers.compiledData)) {
                ScepterObject.applyManaCost(manaCost,stack, world, user)
                ScepterObject.incrementScepterStats(stack.orCreateNbt, activeEnchantId, modifiers.compiledData.getXpModifiers())
                user.itemCooldownManager.set(stack.item, cd)
                TypedActionResult.success(stack)
            } else {
                resetCooldown(stack,world,user,activeEnchantId)
            }
        } else {
            resetCooldown(stack,world,user,activeEnchantId)
        }
    }
    @Suppress("UNUSED_PARAMETER")
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
        if (world.isClient) return
        if (entity !is PlayerEntity) return

        val id = ScepterObject.scepterTickNbtCheck(stack)
        if (id > 0){
            if (ScepterObject.getPersistentTickerNeed(id)){
                ScepterObject.persistentEffectTicker(id)
            }
            val chk = ScepterObject.shouldRemoveCooldownChecker(id)
            if (chk > 0){
                entity.itemCooldownManager.set(stack.item,chk)
                ScepterObject.activeEnchantHelper(world,stack, ScepterObject.fallbackAugment)
            } else if (chk == 0){
                entity.itemCooldownManager.remove(stack.item)
            }
            //slowly heal damage over time
            if (ScepterObject.tickTicker(id)){
                healDamage(1,stack)
            }
        }

    }

    override fun getUseAction(stack: ItemStack): UseAction {
        return UseAction.BLOCK
    }

    private fun resetCooldown(stack: ItemStack, world: World, user: PlayerEntity, activeEnchant: String): TypedActionResult<ItemStack>{
        world.playSound(null,user.blockPos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS,0.6F,0.8F)
        ScepterObject.resetCooldown(stack,activeEnchant)
        if (user is ServerPlayerEntity) {
            val buf = PacketByteBufs.create()
            ServerPlayNetworking.send(user, SCEPTER_SMOKE_PACKET,buf)
        } else {
            doSmoke(world,user)
        }
        return TypedActionResult.fail(stack)
    }

    //companion object for the scepter item, handles private functions and other housekeeping

    companion object SI {

        val SCEPTER_SMOKE_PACKET = Identifier(AI.MOD_ID,"scepter_smoke_packet")
        val commaText: MutableText = Text.literal(", ").formatted(Formatting.GOLD)

        fun registerClient(){
            ClientPlayNetworking.registerGlobalReceiver(SCEPTER_SMOKE_PACKET) { minecraftClient: MinecraftClient, _, _, _ ->
                val world = minecraftClient.world
                val entity = minecraftClient.player
                if (world != null && entity != null){
                    doSmoke(world,minecraftClient,entity)
                }
            }
        }

        private fun doSmoke(world: World, client: MinecraftClient, user: LivingEntity){
            val particlePos = scepterParticlePos(client, user)
            world.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,particlePos.x,particlePos.y,particlePos.z,user.velocity.x,user.velocity.y + 0.5,user.velocity.z)
        }
        private fun doSmoke(world: World, user: LivingEntity){
            val pos = user.eyePos
            val width = user.width
            val yaw = user.yaw
            val posX = pos.x - (width + 0.8f) * 0.5 * MathHelper.sin(yaw * (Math.PI.toFloat() / 180)) - 0.6 * MathHelper.cos(yaw * (Math.PI.toFloat() / 180))
            val posY = pos.y - 0.1
            val posZ = pos.z + (width + 0.8f) * 0.5 * MathHelper.cos(yaw * (Math.PI.toFloat() / 180)) - 0.6 * MathHelper.sin(yaw * (Math.PI.toFloat() / 180))
            world.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,posX,posY,posZ,user.velocity.x,user.velocity.y + 0.5,user.velocity.z)
        }

        @Deprecated("moving to amethyst_core")
        fun scepterParticlePos(client: MinecraftClient, user: LivingEntity): Vec3d{
            val pos = user.getCameraPosVec(client.tickDelta)
            val width = user.width
            val perspective = client.options.perspective
            val yaw = if(perspective == Perspective.THIRD_PERSON_FRONT){
                user.bodyYaw
            } else {
                user.getYaw(client.tickDelta)
            }
            val fov = MathHelper.clamp(client.options.fov.value,30,110)
            return scepterParticlePos(pos, width, yaw, perspective, fov.toDouble())
        }

        @Deprecated("moving to amethyst_core")
        private fun scepterParticlePos(pos: Vec3d, width: Float, yaw: Float, perspective: Perspective, fov: Double): Vec3d{
            val offset: Vec3d = scepterOffset(perspective, fov)
            return playerParticlePos(pos, width, yaw, offset)
        }

        @Deprecated("moving to amethyst_core")
        fun playerParticlePos(pos: Vec3d, width: Float, yaw: Float, offset: Vec3d): Vec3d{
            val posX = pos.x - (width + offset.x) * 0.5 * MathHelper.sin(yaw * (Math.PI.toFloat() / 180)) - offset.z * MathHelper.cos(yaw * (Math.PI.toFloat() / 180))
            val posY = pos.y + offset.y
            val posZ = pos.z + (width + offset.x) * 0.5 * MathHelper.cos(yaw * (Math.PI.toFloat() / 180)) - offset.z * MathHelper.sin(yaw * (Math.PI.toFloat() / 180))
            return Vec3d(posX, posY, posZ)
        }

        @Deprecated("moving to amethyst_core")
        fun scepterOffset(perspective: Perspective, fov: Double): Vec3d{
            return when(perspective){
                Perspective.FIRST_PERSON -> {
                    val fpx = MathHelper.lerpFromProgress(fov,30.0,110.0,1.4,-0.1)
                    Vec3d(fpx,-0.13,0.6)
                }
                Perspective.THIRD_PERSON_FRONT -> {
                    Vec3d(0.8,-0.5,0.5)
                }
                Perspective.THIRD_PERSON_BACK -> {
                    Vec3d(0.8, -0.5, 0.5)
                }
            }
        }

        fun writeDefaultNbt(stack: ItemStack){
            val nbt = stack.orCreateNbt
            if(!nbt.contains(NbtKeys.ACTIVE_ENCHANT.str())){
                val identifier = Registry.ENCHANTMENT.getId(RegisterEnchantment.MAGIC_MISSILE)
                if (identifier != null) {
                    Nbt.writeStringNbt(NbtKeys.ACTIVE_ENCHANT.str(), identifier.toString(), nbt)
                }
            }
            val item = stack.item
            if (item is ScepterItem) {
                val nbtList = NbtList()
                item.defaultModifiers.forEach {
                    val nbtEl = NbtCompound()
                    Nbt.writeStringNbt(NbtKeys.MODIFIER_ID.str(),it.toString(), nbtEl)
                    nbtList.add(nbtEl)
                }
                nbt.put(NbtKeys.MODIFIERS.str(),nbtList)
            }
            ScepterObject.getScepterStats(stack)

        }
    }

    data class EntityTaskInstance(val enchant: Enchantment,val user: LivingEntity, val level: Double, val hit: HitResult?)

}
