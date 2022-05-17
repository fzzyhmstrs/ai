package me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments

import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.scepter.ScepterObject
import me.fzzyhmstrs.amethyst_imbuement.util.AcceptableItemStacks
import me.fzzyhmstrs.amethyst_imbuement.util.RaycasterUtil
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.EnchantmentTarget
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.world.World
import kotlin.math.max

abstract class ScepterAugment(_tier: Int, _maxLvl: Int, target: EnchantmentTarget, vararg slot: EquipmentSlot): Enchantment(Rarity.VERY_RARE, target,slot) {
    private val maxLvl = _maxLvl
    private val tier = _tier
    open val baseEffect = ScepterObject.AugmentEffect(4.0F,0.0F,0,0,20,20,8.0,1.0)

    abstract fun applyTasks(world: World, user: LivingEntity, hand: Hand, level: Int, effects: ScepterObject.AugmentEffect): Boolean

    fun applyModifiableTasks(world: World, user: LivingEntity, hand: Hand, level: Int, modifiers: List<AugmentModifier>? = null): Boolean{
        val effectModifiers = ScepterObject.AugmentEffect()
        modifiers?.forEach {
            if (it.hasSecondaryEffect()){
                it.secondaryEffect?.applyModifiableTasks(world, user, hand, level, null)
            }
            effectModifiers.plus(it.getEffectModifier())
        }
        effectModifiers.plus(baseEffect)
        return applyTasks(world,user,hand,level,effectModifiers)
    }

    open fun clientTask(world: World, user: LivingEntity, hand: Hand, level: Int){
    }

    open fun entityTask(world: World, target: Entity, user: LivingEntity, level: Double, hit: HitResult?){
    }

    open fun soundEvent(): SoundEvent {
        return SoundEvents.BLOCK_BEACON_ACTIVATE
    }

    fun rangeOfEffect(modifier: Pair<Double, Double>, level: Int): Double{
        return max(1.0,(modifier.first + baseRangeOfEffect().first) + ((modifier.second + baseRangeOfEffect().second) * level))
    }
    open fun baseRangeOfEffect(): Pair<Double, Double>{
        return Pair(8.0,1.0)
    }

    fun damage(modifier: Float): Float{
        return max(0.1F,baseDamage() + modifier)
    }
    open fun baseDamage(): Float{
        return 4.0F
    }

    fun duration(modifier: Pair<Int,Int>, level: Int): Int{
        return max(0,(modifier.first + baseDuration().first) + ((modifier.second + baseDuration().second) * level))
    }
    open fun baseDuration(): Pair<Int,Int>{
        return Pair(20,20)
    }

    fun amplifier(modifier: Int): Int{
        return max(0,baseAmplifier() + modifier)
    }
    open fun baseAmplifier(): Int{
        return 0
    }

    override fun getMinPower(level: Int): Int {
        return 30
    }

    override fun getMaxPower(level: Int): Int {
        return 50
    }

    override fun getMaxLevel(): Int {
        return 1
    }

    open fun getAugmentMaxLevel(): Int{
        return maxLvl
    }

    override fun isTreasure(): Boolean {
        return true
    }

    override fun isAvailableForEnchantedBookOffer(): Boolean {
        return false
    }

    override fun isAvailableForRandomSelection(): Boolean {
        return false
    }

    fun isAcceptableScepterItem(stack: ItemStack, player: PlayerEntity): Boolean {
        val nbt = stack.orCreateNbt
        return ScepterObject.checkScepterStat(
            nbt,
            Registry.ENCHANTMENT.getId(this)?.toString() ?: ""
        ) || player.abilities.creativeMode
    }

    override fun isAcceptableItem(stack: ItemStack): Boolean {
        return when (tier) {
            1 -> (stack.isOf(RegisterItem.OPALINE_SCEPTER) || stack.isOf(RegisterItem.IRIDESCENT_SCEPTER) || stack.isOf(
                RegisterItem.LUSTROUS_SCEPTER))
            2 -> (stack.isOf(RegisterItem.IRIDESCENT_SCEPTER) || stack.isOf(RegisterItem.LUSTROUS_SCEPTER))
            3 -> stack.isOf(RegisterItem.LUSTROUS_SCEPTER)
            else -> {
                false
            }
        }
    }

    open fun acceptableItemStacks(): MutableList<ItemStack> {
        return AcceptableItemStacks.scepterAcceptableItemStacks(tier)
    }

    fun getTier(): Int{
        return tier
    }

    fun registerAugmentStat(){
        val id = EnchantmentHelper.getEnchantmentId(this)?.toString()?:throw NoSuchElementException("Enchantment ID for ${this.javaClass.canonicalName} not found!")
        val imbueLevel = if (ScepterObject.checkAugmentStat(id)){
            ScepterObject.getAugmentImbueLevel(id)
        } else {
            1
        }
        ScepterObject.registerAugmentStat(id,configAugmentStat(id,imbueLevel),true)
    }

    private fun configAugmentStat(id: String,imbueLevel: Int = 1): ScepterObject.AugmentDatapoint{
        val stat = augmentStat(imbueLevel)
        val augmentConfig = AiConfig.AugmentStats()
        val type = stat.type
        augmentConfig.id = id
        augmentConfig.cooldown = stat.cooldown
        augmentConfig.manaCost = stat.manaCost
        augmentConfig.minLvl = stat.minLvl
        val tier = stat.bookOfLoreTier
        val item = stat.keyItem
        val augmentAfterConfig = AiConfig.configAugment(this.javaClass.simpleName + AiConfig.augmentVersion +".json",augmentConfig)
        return ScepterObject.AugmentDatapoint(type,augmentAfterConfig.cooldown,augmentAfterConfig.manaCost,augmentAfterConfig.minLvl,imbueLevel,tier,item)
    }

    abstract fun augmentStat(imbueLevel: Int = 1): ScepterObject.AugmentDatapoint
}