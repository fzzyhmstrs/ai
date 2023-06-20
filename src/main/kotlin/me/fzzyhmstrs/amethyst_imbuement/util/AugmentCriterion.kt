package me.fzzyhmstrs.amethyst_imbuement.util

import com.google.common.base.Predicate
import com.google.gson.JsonObject
import me.fzzyhmstrs.fzzy_core.trinket_util.base_augments.AbstractEquipmentAugment
import me.fzzyhmstrs.fzzy_core.trinket_util.base_augments.AbstractPassiveAugment
import net.minecraft.advancement.criterion.AbstractCriterion
import net.minecraft.advancement.criterion.AbstractCriterionConditions
import net.minecraft.enchantment.Enchantment
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer
import net.minecraft.predicate.entity.EntityPredicate
import net.minecraft.predicate.entity.LootContextPredicate
import net.minecraft.registry.Registries
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

class AugmentCriterion(private val id: Identifier): AbstractCriterion<AugmentCriterion.AugmentConditions>() {

    override fun getId(): Identifier {
        return id
    }

    override fun conditionsFromJson(
        obj: JsonObject,
        playerPredicate: LootContextPredicate,
        predicateDeserializer: AdvancementEntityPredicateDeserializer
    ): AugmentConditions {
        if (obj.has("augment")){
            val el = obj.get("augment")
            if (el.isJsonPrimitive){
                val str = el.asString
                if (str == "equipment"){
                    return AugmentConditions.equipmentTypeCondition(id,playerPredicate)
                } else if (str == "jewelry"){
                    return AugmentConditions.jewelryTypeCondition(id,playerPredicate)
                } else {
                    val augment = Identifier.tryParse(str)?:throw IllegalStateException("Augment $str not found in enchantment registry.")
                    return AugmentConditions(id,{aug -> aug == Registries.ENCHANTMENT.get(augment)},playerPredicate)
                }
            }else {
                throw IllegalStateException("Augment Criterion not properly formatted in json object: ${obj.asString}")
            }
        }else {
            throw IllegalStateException("Augment Criterion not properly formatted in json object: ${obj.asString}")
        }
    }

    fun trigger(player: ServerPlayerEntity, augment: Enchantment){
        this.trigger(player) { condition -> condition.test(augment) }
    }


    class AugmentConditions(id: Identifier, private val augmentPredicate: Predicate<Enchantment>,entityPredicate: LootContextPredicate): AbstractCriterionConditions(id,entityPredicate){

        fun test(augment: Enchantment): Boolean{
            return augmentPredicate.test(augment)
        }

        companion object{
            fun equipmentTypeCondition(id: Identifier,entityPredicate: LootContextPredicate): AugmentConditions{
                return AugmentConditions(id,{aug -> aug is AbstractEquipmentAugment},entityPredicate)
            }

            fun jewelryTypeCondition(id: Identifier,entityPredicate: LootContextPredicate): AugmentConditions{
                return AugmentConditions(id,{aug -> aug is AbstractPassiveAugment},entityPredicate)
            }
        }

    }
}