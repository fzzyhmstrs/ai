package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.util.*
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEvent
import me.fzzyhmstrs.amethyst_imbuement.scepter.ScepterObject
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.*
import me.fzzyhmstrs.amethyst_imbuement.tool.ScepterMaterialAddon
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.client.MinecraftClient
import net.minecraft.client.item.TooltipContext
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.*
import net.minecraft.nbt.NbtCompound
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.*
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.MathHelper
import net.minecraft.util.registry.Registry
import net.minecraft.world.World
import kotlin.math.max

@Suppress("SameParameterValue", "unused", "USELESS_IS_CHECK")
class ScepterItem(material: ToolMaterial, settings: Settings, vararg defaultModifier: AugmentModifier): ToolItem(material, settings), ManaItem {

    private val tickerManaRepair: RegisterEvent.Ticker
    private val defaultManaRepairTime = 150L
    private val augmentModifiers: MutableMap<Identifier,AugmentModifier> = mutableMapOf()
    private val tempAugmentModifiers: MutableMap<Identifier,RegisterEvent.Ticker> = mutableMapOf()

    init {
        val manaRepairTime = if (material !is ScepterMaterialAddon){
            defaultManaRepairTime
        } else {
            material.healCooldown()
        }
        tickerManaRepair = RegisterEvent.Ticker(manaRepairTime.toInt())
        defaultModifier.forEach {
            addModifier(it)

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
            TranslatableText("enchantment.amethyst_imbuement.${Identifier(activeEnchantId).path}")
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
        if (augmentModifiers.isNotEmpty()){
            val modifierText = TranslatableText("scepter.modifiers").formatted(Formatting.GOLD)
            val commaText = LiteralText(", ").formatted(Formatting.GOLD)
            val itr = augmentModifiers.asIterable().iterator()
            while(itr.hasNext()){
                val entry = itr.next()
                modifierText.append(TranslatableText("scepter.modifiers.${entry.key}").formatted(Formatting.GOLD))
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
                activeEnchantId = ScepterObject.activeEnchantHelper(world,stack,readAugNbt(NbtKeys.ACTIVE_ENCHANT.str(), nbt))
                testEnchant = Registry.ENCHANTMENT.get(Identifier(activeEnchantId))?: return resetCooldown(stack,world,user,activeEnchantId)
            } else {
                ScepterObject.initializeScepter(stack,world)
                activeEnchantId = ScepterObject.activeEnchantHelper(world,stack,readAugNbt(NbtKeys.ACTIVE_ENCHANT.str(), nbt))
                testEnchant = Registry.ENCHANTMENT.get(Identifier(activeEnchantId))?: return resetCooldown(stack,world,user,activeEnchantId)
            }
        } else {
            ScepterObject.initializeScepter(stack,world)
            nbt = stack.nbt
            if (nbt != null) {
                activeEnchantId =
                    ScepterObject.activeEnchantHelper(world,stack, readAugNbt(NbtKeys.ACTIVE_ENCHANT.str(), nbt))
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
        val mods = checkForModifiers(Identifier(activeEnchantId))
        var cdMod = 0
        var levelMod = 0
        var manaMod = 0
        mods.forEach{
            cdMod += it.cooldownModifier
            levelMod += it.levelModifier
            manaMod += it.manaCostModifier

        }
        val cd : Int? = ScepterObject.useScepter(activeEnchantId, stack, user, world, cdMod)
        return if (cd != null) {
            val manaReduction = EnchantmentHelper.getLevel(RegisterEnchantment.ATTUNED, stack) + manaMod
            val manaCost = ScepterObject.getAugmentManaCost(activeEnchantId,manaReduction)
            if (!ScepterObject.checkManaCost(manaCost,stack,world,user)) return resetCooldown(stack,world,user,activeEnchantId)
            val level = max(1,testLevel + levelMod)
            if (testEnchant.applyTasks(world, user, hand, level, mods)) {
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

        tickModifiers()
        tickerManaRepair.tickUp()
        val id = ScepterObject.scepterTickNbtCheck(stack)
        if (id > 0){
            if (ScepterObject.getPersistentTickerNeed(id)){
                ScepterObject.persistentEffectTicker(id)
            }
            val chk = ScepterObject.shouldRemoveCooldownChecker(id)
            if (chk > 0){
                entity.itemCooldownManager.set(stack.item,chk)
                ScepterObject.activeEnchantHelper(world,stack,ScepterObject.fallbackAugment)
            } else if (chk == 0){
                entity.itemCooldownManager.remove(stack.item)
            }
        }
        //slowly heal damage over time
        if (tickerManaRepair.isReady()){
            healDamage(1,stack)
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

    fun addModifier(modifier: AugmentModifier){
        val id = modifier.modifierId
        augmentModifiers[id] = modifier
        if (modifier.hasLifespan()){
            tempAugmentModifiers[id] = RegisterEvent.Ticker(modifier.modifierLifespan)
        }
    }
    fun removeModifier(identifier: Identifier){
        augmentModifiers.remove(identifier)
        tempAugmentModifiers.remove(identifier)
    }
    fun tickModifiers(){
        tempAugmentModifiers.forEach {
            it.value.tickUp()
            if (it.value.isReady()){
                removeModifier(it.key)
            }
        }
    }
    fun checkForModifiers(id: Identifier): List<AugmentModifier>{
        if (augmentModifiers.isEmpty()) return listOf()
        val list = mutableListOf<AugmentModifier>()
        augmentModifiers.forEach{
            if (it.value.hasSpellToAffect()){
                if (it.value.spellsToAffect?.contains(id) == true){
                    list.add(it.value)
                }
            } else {
                list.add(it.value)
            }
        }
        return list
    }

    //companion object for the scepter item, handles private functions and other housekeeping

    companion object SI {

        val SCEPTER_SMOKE_PACKET = Identifier(AI.MOD_ID,"scepter_smoke_packet")

        fun registerClient(){
            ClientPlayNetworking.registerGlobalReceiver(SCEPTER_SMOKE_PACKET) { minecraftClient: MinecraftClient, _, _, _ ->
                val world = minecraftClient.world
                val entity = minecraftClient.player
                if (world != null && entity != null){
                    doSmoke(world,entity)
                }
            }
        }

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
            if(!nbt.contains(NbtKeys.ACTIVE_ENCHANT.str())){
                val identifier = Registry.ENCHANTMENT.getId(RegisterEnchantment.MAGIC_MISSILE)
                if (identifier != null) {
                    nbt.putString(NbtKeys.ACTIVE_ENCHANT.str(), identifier.toString())
                }
            }
            ScepterObject.getScepterStats(stack)
        }
    }

    data class EntityTaskInstance(val enchant: Enchantment,val user: LivingEntity, val level: Double, val hit: HitResult?)

}