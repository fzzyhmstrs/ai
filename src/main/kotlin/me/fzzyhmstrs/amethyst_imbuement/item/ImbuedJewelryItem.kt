package me.fzzyhmstrs.amethyst_imbuement.item

import com.google.common.collect.Multimap
import dev.emi.trinkets.api.SlotReference
import me.fzzyhmstrs.amethyst_imbuement.augment.ShieldingAugment
import me.fzzyhmstrs.amethyst_imbuement.util.AugmentTasks
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import net.minecraft.client.item.TooltipContext
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import net.minecraft.world.World
import java.util.*

class ImbuedJewelryItem(settings: Settings,_ttn: String):CopperJewelryItem(settings,"copper_ring"), AugmentTasks {
    private val ttn: String = _ttn
    private var shieldLevel: Int = 0
    private var ticks = 0
    private var tickCounter = 0
    private val duration = 18000 //1200 per minute
    val durDiscount = 1200

    override fun appendTooltip(stack: ItemStack?, world: World?, tooltip: MutableList<Text>?, context: TooltipContext?) {
        super.appendTooltip(stack, world, tooltip, context)
        tooltip?.removeAt(tooltip.size-1)
        tooltip?.add(TranslatableText("item.amethyst_imbuement.$ttn.tooltip1").formatted(Formatting.WHITE, Formatting.ITALIC))
    }

    override fun getModifiers(
        stack: ItemStack,
        slot: SlotReference,
        entity: LivingEntity,
        uuid: UUID
    ): Multimap<EntityAttribute, EntityAttributeModifier> {
        val modifiers = super.getModifiers(stack, slot, entity, uuid)
        val modifiersNew = modifierEnchantmentTasks(stack,entity.world,entity)
        if (modifiersNew != null){
            for (mod in modifiersNew.keys){
                modifiers.put(mod,modifiersNew[mod])
            }
        }
        return modifiers
    }

    override fun onEquip(stack: ItemStack, slot: SlotReference, entity: LivingEntity) {
        super.onEquip(stack, slot, entity)
        if (entity.world.isClient()) return
        /*val trinkets = TrinketsApi.getTrinketComponent(entity)
        println(slot.inventory.slotType)
        if(trinkets.isPresent) {
            trinkets.get().initializeTrinkets(2,
                entity.world?.time as Long,
                duration,
                durDiscount,
                entity)
        }*/
        shieldLevel = EnchantmentHelper.getLevel(RegisterEnchantment.SHIELDING, stack)
        /*val nbt = stack.orCreateNbt
        var id = 0
        if (nbt!!.contains(NbtKeys.IMBUE_ID.str())) id = readNbt(NbtKeys.IMBUE_ID.str(), nbt)
        val id2 = ShieldingObject.registerTrinket(
            id,
            2 + shieldLevel,
            entity.world?.time as Long,
            (duration - durDiscount * shieldLevel),
            entity
        )
        if (id2 != id) writeNbt(NbtKeys.IMBUE_ID.str(), id2, nbt)*/

        ShieldingAugment.addTrinketToQueue(entity,slot,2 + shieldLevel)
        passiveEnchantmentTasks(stack,entity.world,entity)
    }

    override fun onUnequip(stack: ItemStack, slot: SlotReference, entity: LivingEntity) {
        super.onUnequip(stack, slot, entity)
        //val nbt = stack.nbt ?: return
        if(entity.world.isClient()) return
        //ShieldingObject.removeTrinket(readNbt(NbtKeys.IMBUE_ID.str(),nbt))
        unequipEnchantmentTasks(stack,entity.world,entity)
        ShieldingAugment.removeTrinketFromQueue(entity,slot)
    }

    override fun tick(stack: ItemStack, slot: SlotReference, entity: LivingEntity) {
        if(entity.world.isClient()) return

        if (entity.world.time%30 == 0L){
            ShieldingAugment.applyShielding(entity)
            passiveEnchantmentTasks(stack,entity.world,entity)
            tickCounter = 0
        }

    }
    override fun passiveEnchantmentTasks(stack: ItemStack,world: World,entity: Entity){
        if (entity !is PlayerEntity) return
        super.passiveEnchantmentTasks(stack, world, entity)
        //could try updating to a check the map of enchantments rather than calling getLevel over and over
        /*val enchants = EnchantmentHelper.get(stack)
        if (enchants.isEmpty()) return
        if (enchants.containsKey(RegisterEnchantment.LUCKY)) {
            BaseAugment.addStatusToQueue(entity.uuid,StatusEffects.LUCK, 240, 0)
        } else if (enchants.containsKey(RegisterEnchantment.IMMUNITY)) {
            RegisterEnchantment.CLEANSE.supportEffect(world,null,entity,1)
        } else if (enchants.containsKey(RegisterEnchantment.MOONLIT)) {
            if (world.isNight){
                val lvl = EnchantmentHelper.getLevel(RegisterEnchantment.MOONLIT,stack)
                val tod = world.timeOfDay%24000
                val comp1 = abs(tod - 13000L)
                val comp2 = abs(tod - 23000L)
                val comp3 = abs(tod - 18000L)
                if((comp3 < comp1) && (comp3 < comp2)){
                    BaseAugment.addStatusToQueue(entity.uuid,StatusEffects.STRENGTH, 400, lvl)
                    BaseAugment.addStatusToQueue(entity.uuid,StatusEffects.RESISTANCE, 400, lvl-1)
                } else {
                    BaseAugment.addStatusToQueue(entity.uuid,StatusEffects.STRENGTH, 400, lvl-1)
                }
            }
        } else if (enchants.containsKey(RegisterEnchantment.SUNTOUCHED)) {
            if (world.isDay){
                val lvl = EnchantmentHelper.getLevel(RegisterEnchantment.SUNTOUCHED,stack)
                val tod = world.timeOfDay
                val comp1 = abs(tod - 1000L)
                val comp2 = abs(tod - 11000L)
                val comp3 = abs(tod - 6000L)
                if((comp3 < comp1) && (comp3 < comp2)){
                    BaseAugment.addStatusToQueue(entity.uuid,StatusEffects.STRENGTH, 400, lvl)
                    BaseAugment.addStatusToQueue(entity.uuid,StatusEffects.SPEED, 400, lvl-1)
                } else {
                    BaseAugment.addStatusToQueue(entity.uuid,StatusEffects.STRENGTH, 400, lvl-1)
                }
            }
        } else if (enchants.containsKey(RegisterEnchantment.SPECTRAL_VISION)) {
            val entityList: MutableList<Entity> = RaycasterUtil.raycastEntityArea(25.0)
            if (entityList.isEmpty()) return
            for (target in entityList){
                if (target is Monster || target is PassiveEntity){
                    (target as LivingEntity).addStatusEffect(StatusEffectInstance(StatusEffects.GLOWING,60))
                }
            }
        } else if (enchants.containsKey(RegisterEnchantment.HEALTHY)) {
            BaseAugment.addStatusToQueue(entity.uuid,StatusEffects.HEALTH_BOOST, 400, 0)
        }
        else if (enchants.containsKey(RegisterEnchantment.DRACONIC_VISION)) {
            if (entity.hasStatusEffect(AI.DRACONIC_VISION)) return
            val pos = entity.blockPos
            val posI = pos.x
            val posJ = pos.y
            val posK = pos.z
            val range = RegisterEnchantment.DRACONIC_VISION.rangeOfEffect()
            for (i in -range..range){
                for (j in -range..range){
                    for (k in -range..range){
                        val ii = posI + i
                        val jj = posJ + j
                        val kk = posK + k
                        if (world.isAir(pos.add(i,j,k))) continue
                        println("trying!: ${world.getBlockState(pos.add(i,j,k)).block.name}")
                        println("at position: ${pos.add(i,j,k)}")
                        if (world.getBlockState(pos.add(i,j,k)).block is OreBlock){
                            println("found ore!")
                            val dbe = DraconicBoxEntity(RegisterEntity.DRACONIC_BOX_ENTITY,world)
                            dbe.setPosition(ii + 0.5,jj.toDouble(),kk + 0.5)
                            world.spawnEntity(dbe)
                        }
                    }
                }
            }
            BaseAugment.addStatusToQueue(entity.uuid,AI.DRACONIC_VISION,300,0)
            world.playSound(null,pos,SoundEvents.BLOCK_CONDUIT_AMBIENT_SHORT,SoundCategory.NEUTRAL,1.0f,0.8f)
        }*/
    }
    private fun writeNbt(key: String, id: Int, nbt: NbtCompound){
        nbt.putInt(key,id)
    }
    private fun readNbt(key: String, nbt: NbtCompound): Int {
        return nbt.getInt(key)
    }
}