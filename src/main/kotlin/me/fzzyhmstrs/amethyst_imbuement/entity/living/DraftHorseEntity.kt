package me.fzzyhmstrs.amethyst_imbuement.entity.living

import me.fzzyhmstrs.amethyst_core.entity.ModifiableEffectEntity
import me.fzzyhmstrs.amethyst_core.entity.Scalable
import me.fzzyhmstrs.fzzy_core.entity_util.PlayerCreatable
import net.minecraft.entity.EntityData
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnReason
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.passive.AbstractHorseEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.world.LocalDifficulty
import net.minecraft.world.ServerWorldAccess
import net.minecraft.world.World

class DraftHorseEntity(entityType: EntityType<out AbstractHorseEntity>?, world: World?) : PlayerCreatedHorseEntity(entityType, world),
    ModifiableEffectEntity, PlayerCreatable, Scalable {
    companion object{
        fun createDraftHorseBaseAttributes(): DefaultAttributeContainer.Builder{
            return createMobAttributes()
                .add(EntityAttributes.HORSE_JUMP_STRENGTH,0.5)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 50.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2)
        }
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        super.writeCustomDataToNbt(nbt)
        val nbtList = NbtList()
        for (i in 2 until items.size()) {
            val itemStack = items.getStack(i)
            if (itemStack.isEmpty) continue
            val nbtCompound = NbtCompound()
            nbtCompound.putByte("Slot", i.toByte())
            itemStack.writeNbt(nbtCompound)
            nbtList.add(nbtCompound)
        }
        nbt.put("Items", nbtList)
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        super.readCustomDataFromNbt(nbt)
        val nbtList = nbt.getList("Items", NbtElement.COMPOUND_TYPE.toInt())
        for (i in nbtList.indices) {
            val nbtCompound = nbtList.getCompound(i)
            val j = nbtCompound.getByte("Slot").toInt() and 0xFF
            if (j < 2 || j >= items.size()) continue
            items.setStack(j, ItemStack.fromNbt(nbtCompound))
        }
    }

    override fun getInventorySize(): Int {
        return 17
    }

    override fun initialize(
        world: ServerWorldAccess?,
        difficulty: LocalDifficulty?,
        spawnReason: SpawnReason?,
        entityData: EntityData?,
        entityNbt: NbtCompound?
    ): EntityData? {
        val result = super.initialize(world, difficulty, spawnReason, entityData, entityNbt)
        onChestedStatusChanged()
        return result
    }
}