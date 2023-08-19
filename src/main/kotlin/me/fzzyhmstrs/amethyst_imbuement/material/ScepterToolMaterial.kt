package me.fzzyhmstrs.amethyst_imbuement.material

import me.fzzyhmstrs.fzzy_config.validated_field.*
import me.fzzyhmstrs.fzzy_core.mana_util.ManaItemMaterial

/**
 * A tool material for defining a scepter. The Scepter tier is used to determine which augments can be applied to which scepters. See [LoreTier] for more info.
 */
open class ScepterToolMaterial protected constructor(
    private val tier: Int,
    attackSpeedDefault: ValidatedDouble,
    healCooldownDefault: ValidatedLong,
    durabilityDefault: ValidatedInt,
    miningSpeedDefault: ValidatedFloat,
    attackDamageDefault: ValidatedFloat,
    miningLevelDefault: ValidatedInt,
    enchantabilityDefault: ValidatedInt,
    repairIngredientDefault: ValidatedIngredient
)
: 
ValidatedToolMaterial(
    durabilityDefault,
    miningSpeedDefault,
    attackDamageDefault,
    miningLevelDefault,
    enchantabilityDefault,
    repairIngredientDefault)
,
ManaItemMaterial 
{
    var attackSpeed = attackSpeedDefault
    var healCooldown = healCooldownDefault

    fun compatMaterial(): CompatScepterToolMaterial{
        return CompatScepterToolMaterial(
            getAttackDamage(),
            getAttackSpeed(),
            getDurability(),
            getEnchantability(),
            getMiningLevel(),
            getMiningSpeedMultiplier(),
            getRepairIngredient(),
            healCooldown(),
            scepterTier())
    }

    fun scepterTier(): Int{
        return tier
    }

    fun getAttackSpeed(): Double{
        return attackSpeed.get()
    }

    override fun healCooldown(): Long {
        return healCooldown.get()
    }


    open class Builder(private val tier: Int): AbstractBuilder<ScepterToolMaterial, Builder>(){
        protected var aS = ValidatedDouble(-3.0,0.0,-4.0)
        protected var hC = ValidatedLong(70L,1200L,10L)

        fun attackSpeed(default: Double): Builder{
            aS = ValidatedDouble(default,0.0,-4.0)
            return builderClass()
        }

        fun healCooldown(default: Long): Builder{
            hC = ValidatedLong(default,1200L,10L)
            return builderClass()
        }

        override fun builderClass(): Builder{
            return this
        }
        
        override fun build(): ScepterToolMaterial{
            return ScepterToolMaterial(tier, aS,hC, d, mSM, aD, mL, e, rI)
        }
        
    }
  
}
