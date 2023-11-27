package me.fzzyhmstrs.amethyst_imbuement.entity.totem

import me.fzzyhmstrs.amethyst_core.scepter_util.SpellDamageSource
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.fzzy_core.registry.EventRegistry
import net.minecraft.entity.*
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Arm
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.MathHelper
import net.minecraft.world.EntityView
import net.minecraft.world.World
import java.util.*
import kotlin.math.atan2
import kotlin.math.roundToInt

abstract class AbstractEffectTotemEntity(
    entityType: EntityType<out AbstractEffectTotemEntity>,
    world: World,
    protected var summoner: LivingEntity? = null,
    private val maxAge: Int = 600,
    item: Item = Items.AIR)
    : LivingEntity(entityType, world), Tameable {

    var ticks = 0
    var lookingRotR = 0f
    private var turningSpeedR = 2f
    protected val ticker: EventRegistry.Ticker
    internal val stack = ItemStack(item)
    internal val seed = this.blockPos.asLong().toInt()
    private var ownerUuid: UUID? = summoner?.uuid
    private val init: Int by lazy{
        firstTick()
    }

    init{
        ticker = initTickerInternal()
    }

    override fun getOwnerUuid(): UUID?{
        return ownerUuid
    }

    override fun method_48926(): EntityView{
        return this.world
    }

    override fun getOwner(): LivingEntity? {
        return if (summoner != null) {
            summoner
        } else if (world is ServerWorld && ownerUuid != null) {
            val o = (world as ServerWorld).getEntity(ownerUuid)
            if (o != null && o is LivingEntity) {
                this.summoner = o
                o
            } else {
                null
            }

        }else {
            null
        }
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        super.writeCustomDataToNbt(nbt)
        if (ownerUuid != null) {
            nbt.putUuid("owner",ownerUuid)
        }
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        super.readCustomDataFromNbt(nbt)
        if (nbt.containsUuid("owner")){
            ownerUuid = nbt.getUuid("owner")
        }
    }

    override fun damage(source: DamageSource, amount: Float): Boolean {
        if (isInvulnerableTo(source)) {
            return false
        }
        val attacker = source.attacker
        if (attacker is LivingEntity){
            val spell = if (source is SpellDamageSource) source.getSpell() else null
            if(!AiConfig.entities.shouldItHitBase(attacker, this, AiConfig.Entities.Options.NONE, spell)) return false
        }
        return super.damage(source, amount)
    }

    private fun initTickerInternal(): EventRegistry.Ticker {
        return initTicker()
    }
    open fun initTicker(): EventRegistry.Ticker{
        return EventRegistry.ticker_30
    }

    override fun shouldRenderName(): Boolean {
        return false
    }

    override fun isCollidable(): Boolean {
        return false
    }

    override fun isPushable(): Boolean {
        return false
    }

    override fun collidesWith(other: Entity?): Boolean {
        return false
    }

    override fun isInsideWall(): Boolean {
        return false
    }

    override fun getArmorItems(): MutableIterable<ItemStack> {
        return DefaultedList.ofSize(4, ItemStack.EMPTY)
    }

    override fun equipStack(slot: EquipmentSlot, stack: ItemStack) {
        return
    }

    override fun getEquippedStack(slot: EquipmentSlot?): ItemStack {
        return ItemStack.EMPTY
    }

    override fun getMainArm(): Arm {
        return Arm.RIGHT
    }

    override fun onRemoved() {
        EventRegistry.removeTickUppable(ticker)
    }

    private fun firstTick(): Int{
        totemEffect()
        return 0
    }

    override fun getHurtSound(source: DamageSource): SoundEvent {
        return SoundEvents.BLOCK_DEEPSLATE_BRICKS_FALL
    }

    override fun tick() {
        super.tick()
        if (!world.isClient()) {
            val i = init
        }
        if (world.isClient) {
            val lookAtTarget = lookAt()
            if (lookAtTarget != null) {
                val x: Double = lookAtTarget.x - pos.x
                val z: Double = lookAtTarget.z - pos.z
                val rotY = (atan2(z, x).toFloat() / Math.PI * 180 + 180).toFloat()
                moveOnTickR(rotY)
            } else {
                lookingRotR += 2
            }
            ticks++
            if (ticks >= 360){
                ticks = 0
            }
            lookingRotR = rotClamp(360, lookingRotR)
            return
        }
        if (!world.isClient() && ticker.isReady()) {
            totemEffect()
        }
        if (age >= maxAge){
            this.kill()
        }
    }

    abstract fun totemEffect()

    open fun lookAt(): LivingEntity? {
        return world.getClosestPlayer(
            pos.x,
            pos.y + 0.5,
            pos.z,
            3.0,
            false
        )
    }

    private fun rotClamp(clampTo: Int, value: Float): Float {
        return if (value >= clampTo) {
            value - clampTo
        } else if (value < 0) {
            value + clampTo
        } else {
            value
        }
    }

    private fun checkBound(amount: Int, rotBase: Float): Boolean {
        val rot = rotBase.roundToInt().toFloat()
        val rot2 = rotClamp(360, rot + 180)
        return rot - amount <= lookingRotR && lookingRotR <= rot + amount || rot2 - amount <= lookingRotR && lookingRotR <= rot2 + amount
    }

    private fun moveOnTickR(rot: Float) {
        if (!checkBound(2, rot)) {
            val check = ((rotClamp(180, rot) - rotClamp(180, lookingRotR) + 180) % 180).toDouble()
            if (check < 90) {
                lookingRotR += turningSpeedR
            } else {
                lookingRotR -= turningSpeedR
            }
            lookingRotR = rotClamp(360, lookingRotR)
            if (checkBound(10, rot)) {
                turningSpeedR = 2f
            } else {
                turningSpeedR += 1f
                turningSpeedR = MathHelper.clamp(turningSpeedR, 2f, 20f)
            }
        }
    }

    interface TotemAbstractAttributes{
        fun createTotemAttributes(): DefaultAttributeContainer.Builder{
            return createBaseTotemAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH,18.0)

        }
        fun createBaseTotemAttributes(): DefaultAttributeContainer.Builder{
            return createLivingAttributes().add(EntityAttributes.GENERIC_FOLLOW_RANGE, 1.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED,0.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE,1.0)
        }
    }
}