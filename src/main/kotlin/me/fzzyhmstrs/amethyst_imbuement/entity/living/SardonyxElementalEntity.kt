package me.fzzyhmstrs.amethyst_imbuement.entity.living

import net.minecraft.entity.EntityType
import net.minecraft.entity.mob.Monster
import net.minecraft.world.World

class SardonyxElementalEntity(entityType: EntityType<out AttackTicksGolemEntity>, world: World): AttackTicksGolemEntity(entityType, world), Monster {

    /*
    Attacks and Spells
    Melee - Slow, powerful
    AOE Spikes - expanding rings of spikes like the illager AOE attack. Announced with a special sound. 25 Mana
    Chest Laser Beam - extremely damaging beam, telegraphs it's direction for a bit before. 50 mana
    Crystal Shard Fan - relatively fast ranged attack, 3 sardonyx shards. 5 Mana
    Crystal Shell - Temporary Resist 4. 25 Mana
    Summon Fragments - Summons Fragments around it based on Config number. 25 Mana
    Reflect - Used when taking massive DPS, relects taken damage to dealer, reduces damage taken by half while active. 25 Mana

    May want to build in a "memory" of magic users, increasing damage against them.

    Drops: Sardonyx Figurine, used to slowly spawn Sardonyx Fragments
    Sardonyx Fragments are the mobs that drop actual Sardonyx (Elemental may also drop a few, based on looting)

    Regens 10 (X) mana on killing a mob.
    Regens 2 (X) mana and X health when one of it's fragments dies.
    */
    
    class SardonyxElemental: ConfigSection(Header.Builder().space().add("readme.entities.sardonyx_elemental_1").build()){
        var baseAttributes = ValidatedEntityAttributes(mapOf(
            EntityAttributes.GENERIC_MAX_HEALTH to 400.0,
            EntityAttributes.GENERIC_MOVEMENT_SPEED to 0.3,
            EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE to 1.0,
            EntityAttributes.GENERIC_ATTACK_DAMAGE to 26.0,
            EntityAttributes.GENERIC_ARMOR to 10.0,
            RegisterAttribute.MAGIC_RESISTANCE to 0.5))
        @ReadMeText("readme.entities.sardonyx_elemental.baseMana")
        var baseMana = ValidatedInt(100,1500,-1)
        @ReadMeText("readme.entities.sardonyx_elemental.baseManaRegen")
        var baseManaRegen = ValidatedInt(10,100,1)
        @ReadMeText("readme.entities.sardonyx_elemental.thornsDamage")
        var thornsDamage = ValidatedFloat(4.0f,20.0f,0.0f)
        @ReadMeText("readme.entities.sardonyx_elemental.spikesDamage")
        var spikesDamage = ValidatedFloat(14.0f,40.0f,0.0f)
        @ReadMeText("readme.entities.sardonyx_elemental.beamDamage")
        var beamDamage = ValidatedFloat(80.0f,240.0f,0.0f)
        @ReadMeText("readme.entities.sardonyx_elemental.shardDamage")
        var shardDamage = ValidatedFloat(10.0f,100.0f,0.0f)
        @ReadMeText("readme.entities.sardonyx_elemental.fragmentsSummoned")
        var fragmentsSummoned = ValidatedInt(4,10,1)
    }

}
