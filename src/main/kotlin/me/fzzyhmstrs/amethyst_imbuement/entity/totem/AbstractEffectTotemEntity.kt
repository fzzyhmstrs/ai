package me.fzzyhmstrs.amethyst_imbuement.entity.totem

import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.augments.paired.ProcessContext
import me.fzzyhmstrs.amethyst_core.entity.ModifiableEffectContainer
import me.fzzyhmstrs.amethyst_core.entity.ModifiableEffectEntity
import me.fzzyhmstrs.amethyst_core.entity.Scalable
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_imbuement.entity.living.BoomChickenEntity
import me.fzzyhmstrs.amethyst_imbuement.entity.living.PlayerCreatedConstructEntity
import me.fzzyhmstrs.fzzy_core.entity_util.PlayerCreatable
import me.fzzyhmstrs.fzzy_core.registry.EventRegistry
import net.minecraft.entity.*
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedDataHandlerRegistry
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

@Suppress("LeakingThis")
abstract class AbstractEffectTotemEntity(
    entityType: EntityType<out AbstractEffectTotemEntity>,
    world: World,
    summoner: LivingEntity? = null,
    override var maxAge: Int = 600,
    item: Item = Items.AIR)
    :
    LivingEntity(entityType, world), ModifiableEffectEntity, PlayerCreatable, Tameable, Scalable
{

    companion object{
        private val SCALE = DataTracker.registerData(AbstractEffectTotemEntity::class.java, TrackedDataHandlerRegistry.FLOAT)
        internal val TOTEM_BEAM = object : ProcessContext.Data<Boolean>("totem_beam_sound",ProcessContext.BooleanDataType){}
    }

    override var entityEffects: AugmentEffect = AugmentEffect().withDamage(6F).withAmplifier(0)
    override var level: Int = 1
    override var spells: PairedAugments = PairedAugments()
    override var modifiableEffects = ModifiableEffectContainer()
    override var processContext = ProcessContext.EMPTY_CONTEXT

    var ticks = 0
    var lookingRotR = 0f
    private var turningSpeedR = 2f
    protected val ticker: EventRegistry.Ticker
    override var createdBy: UUID? = null
    override var entityOwner: LivingEntity? = null
    internal val stack = ItemStack(item)
    internal val seed = this.blockPos.asLong().toInt()
    private val init: Int by lazy{
        firstTick()
    }

    override fun passContext(context: ProcessContext) {
        super.passContext(context.set(TOTEM_BEAM, true))
    }

    init{
        ticker = initTickerInternal()
        entityOwner = summoner
    }

    override fun getOwnerUuid(): UUID? {
        return createdBy
    }

    override fun method_48926(): EntityView {
        return this.world
    }

    override fun getOwner(): LivingEntity? {
        return if (entityOwner != null) {
            entityOwner
        } else if (world is ServerWorld && createdBy != null) {
            val o = (world as ServerWorld).getEntity(createdBy)
            if (o != null && o is LivingEntity) {
                this.entityOwner = o
                o
            } else {
                null
            }

        }else {
            null
        }
    }

    override fun getScale(): Float {
        return dataTracker.get(SCALE)
    }

    override fun setScale(scale: Float) {
        dataTracker.set(SCALE,MathHelper.clamp(scale,0.0f,20.0f))
        this.calculateDimensions()
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        super.writeCustomDataToNbt(nbt)
        writePlayerCreatedNbt(nbt)
        writeModifiableNbt(nbt)
        nbt.putFloat("scale_factor",getScale())
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        super.readCustomDataFromNbt(nbt)
        readPlayerCreatedNbt(world, nbt)
        readModifiableNbt(nbt)
        setScale(nbt.getFloat("scale_factor").takeIf { it > 0f } ?: 1f)
    }

    private fun initTickerInternal(): EventRegistry.Ticker {
        return initTicker()
    }
    open fun initTicker(): EventRegistry.Ticker{
        return EventRegistry.ticker_30
    }

    override fun initDataTracker() {
        super.initDataTracker()
        dataTracker.startTracking(SCALE, 1f)
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

    private fun firstTick(): Int{
        totemEffect()
        return 0
    }

    override fun remove(reason: RemovalReason?) {
        processContext.beforeRemoval()
        EventRegistry.removeTickUppable(ticker)
        runEffect(ModifiableEffectEntity.ON_REMOVED,this,owner,processContext)
        super.remove(reason)
    }

    override fun damage(source: DamageSource?, amount: Float): Boolean {
        runEffect(ModifiableEffectEntity.ON_DAMAGED,this,owner,processContext)
        return super.damage(source, amount)
    }

    override fun getHurtSound(source: DamageSource): SoundEvent {
        return SoundEvents.BLOCK_DEEPSLATE_BRICKS_FALL
    }

    override fun tick() {
        super.tick()
        tickTickEffects(this, owner, processContext)
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
            runEffect(ModifiableEffectEntity.ON_REMOVED,this,owner,processContext)
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
                .add(EntityAttributes.GENERIC_MAX_HEALTH,20.0)

        }
        fun createBaseTotemAttributes(): DefaultAttributeContainer.Builder{
            return createLivingAttributes().add(EntityAttributes.GENERIC_FOLLOW_RANGE, 1.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED,0.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE,1.0)
        }
    }
}