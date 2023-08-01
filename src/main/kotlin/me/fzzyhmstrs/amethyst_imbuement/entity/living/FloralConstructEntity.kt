package me.fzzyhmstrs.amethyst_imbuement.entity.living

import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.entity.goal.FloralConstructWanderGoal
import me.fzzyhmstrs.fzzy_config.config_util.ConfigSection
import me.fzzyhmstrs.fzzy_config.validated_field.ValidatedEntityAttributes
import net.minecraft.block.Block
import net.minecraft.block.CropBlock
import net.minecraft.block.entity.BlockEntity
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.goal.*
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.particle.ParticleTypes
import net.minecraft.recipe.Ingredient
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.ItemScatterer
import net.minecraft.world.World

class FloralConstructEntity: PlayerCreatedConstructEntity {

    constructor(entityType: EntityType<out FloralConstructEntity>, world: World): super(entityType, world, Settings(false, true, false))
    constructor(entityType: EntityType<out PlayerCreatedConstructEntity>, world: World, ageLimit: Int = -1, createdBy: LivingEntity? = null): super(entityType, world, ageLimit, createdBy, Settings(false, true, false))

    class FloralConstruct: ConfigSection(Header.Builder().space().add("readme.entities.floral_construct_1").build()){
        var baseAttributes = ValidatedEntityAttributes(mapOf(
            EntityAttributes.GENERIC_MAX_HEALTH to 8.0,
            EntityAttributes.GENERIC_MOVEMENT_SPEED to 0.15))
    }

    private val inventory = SimpleInventory(9)
    private var full = false
    internal var texture: Identifier = when(AI.aiRandom().nextInt(5)){
            0 -> AI.identity("textures/entity/floral_construct/floral_construct.png")
            1 -> AI.identity("textures/entity/floral_construct/floral_construct_daisy.png")
            2 -> AI.identity("textures/entity/floral_construct/floral_construct_rose.png")
            3 -> AI.identity("textures/entity/floral_construct/floral_construct_spiky.png")
            else -> AI.identity("textures/entity/floral_construct/floral_construct_sunflower.png")
        }

    override fun initGoals() {
        goalSelector.add(0, SwimGoal(this))
        goalSelector.add(1, EscapeDangerGoal(this, 2.0))
        goalSelector.add(3, TemptGoal(this, 1.25, Ingredient.ofItems(Items.EXPERIENCE_BOTTLE), false))
        goalSelector.add(4, FloralConstructWanderGoal(this, 0.6))
        goalSelector.add(5, IronGolemWanderAroundGoal(this, 0.6))
        goalSelector.add(7, LookAtEntityGoal(this, PlayerEntity::class.java, 6.0f))
        goalSelector.add(8, LookAroundGoal(this))
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        super.writeCustomDataToNbt(nbt)
        nbt.putString("floral_variant", texture.toString())
        nbt.put("floral_inventory", inventory.toNbtList())
        nbt.putBoolean("floral_full",full)
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        super.readCustomDataFromNbt(nbt)
        texture = Identifier(nbt.getString("floral_variant"))
        inventory.readNbtList(nbt.getList("floral_inventory",NbtElement.COMPOUND_TYPE.toInt()))
        full = nbt.getBoolean("floral_full")
    }

    override fun tick() {
        super.tick()
        val world = this.world
        if (world.random.nextFloat() < 0.98) return
        if (world !is ServerWorld) return
        if (full){
            world.spawnParticles(ParticleTypes.ANGRY_VILLAGER,this.x,this.eyeY,this.z,5,0.0,0.0,0.0,0.0)
            world.playSound(null,this.blockPos,SoundEvents.BLOCK_LAVA_EXTINGUISH,SoundCategory.PLAYERS,2.0f,1.0f)
            world.playSound(null,this.blockPos,SoundEvents.ENTITY_FIREWORK_ROCKET_TWINKLE,SoundCategory.PLAYERS,1.0f,1.0f)
            return
        }
        val startPos = this.blockPos
        for (i in -4..4) {
            for (j in -4..4) {
                for (k in -3..3) {
                    val bp = startPos.add(i, k, j)
                    val bs = world.getBlockState(bp)
                    val b = bs.block
                    if (b is CropBlock) {
                        if (!b.isMature(bs)) continue
                        val blockEntity: BlockEntity? = if (bs.hasBlockEntity()) world.getBlockEntity(bp) else null
                        val stacks = Block.getDroppedStacks(bs, world, bp, blockEntity,this.owner, ItemStack.EMPTY)
                        for (stack in stacks){
                            val leftover = inventory.addStack(stack)
                            if (!leftover.isEmpty){
                                full = true
                                ItemScatterer.spawn(world,this.x,this.y,this.z,leftover)
                            }
                        }
                        world.setBlockState(bp,b.defaultState)
                    }
                }
            }
        }
    }

    override fun interactMob(player: PlayerEntity, hand: Hand): ActionResult {
        if (!full) return super.interactMob(player, hand)
        for (stack in inventory.stacks){
            player.inventory.offerOrDrop(stack.copy())
        }
        val world = world
        if (world is ServerWorld){
            world.spawnParticles(ParticleTypes.HAPPY_VILLAGER,this.x,this.eyeY,this.z,8,0.0,0.0,0.0,0.0)
            world.playSound(null,this.blockPos,SoundEvents.ENTITY_PLAYER_LEVELUP,SoundCategory.PLAYERS,1.0f,1.0f)
        }
        inventory.clear()
        full = false
        this.discard()
        return ActionResult.SUCCESS
    }

    override fun dropInventory() {
        for (i in 0 until inventory.size()) {
            val itemStack: ItemStack = inventory.getStack(i)
            if (itemStack.isEmpty || EnchantmentHelper.hasVanishingCurse(itemStack)) continue
            this.dropStack(itemStack)
        }
    }

    override fun getAmbientSound(): SoundEvent? {
        return SoundEvents.ENTITY_ZOMBIE_AMBIENT
    }

    override fun getHurtSound(source: DamageSource): SoundEvent {
        return SoundEvents.ENTITY_ZOMBIE_HURT
    }

    override fun getDeathSound(): SoundEvent {
        return SoundEvents.ENTITY_ZOMBIE_DEATH
    }

    override fun getStepSound(): SoundEvent {
        return SoundEvents.ENTITY_ZOMBIE_STEP
    }

    override fun tryAttack(target: Entity): Boolean {
        return false
    }


}