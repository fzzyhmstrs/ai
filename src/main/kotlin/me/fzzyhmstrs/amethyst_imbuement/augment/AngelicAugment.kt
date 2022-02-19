package me.fzzyhmstrs.amethyst_imbuement.augment

import io.github.ladysnake.pal.AbilitySource
import io.github.ladysnake.pal.Pal
import io.github.ladysnake.pal.PlayerAbility
import io.github.ladysnake.pal.VanillaAbilities
import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.ActiveAugment
import me.fzzyhmstrs.amethyst_imbuement.item.TotemItem
import me.fzzyhmstrs.amethyst_imbuement.util.NbtKeys
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterEnchantment
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier

class AngelicAugment(weight: Rarity, mxLvl: Int = 1, vararg slot: EquipmentSlot): ActiveAugment(weight,mxLvl,*slot) {

    private val ability: PlayerAbility = VanillaAbilities.ALLOW_FLYING
    private val abilitySource: AbilitySource = Pal.getAbilitySource(Identifier("amethyst_imbuement","angelic"))

    override fun activateEffect(user: LivingEntity, level: Int, stack: ItemStack) {
        user as PlayerEntity
        if (!user.abilities.creativeMode) {
            if (!abilitySource.grants(user,ability)){
                abilitySource.grantTo(user,ability)
            }
            val count = readCountFromQueue(user.uuid,NbtKeys.ANGELIC.str())
            if (count > 0){
                val rnd = user.world.random.nextFloat()
                val dmg: Int = if(count > 13) {
                    1
                } else if (count > 6) {
                    if (rnd > 0.33) {1} else {0}
                } else {
                    if (rnd > 0.66) {1} else {0}
                }
                if (TotemItem.damageHandler(stack, user.world, user, dmg)){
                    TotemItem.burnOutHandler(stack, RegisterEnchantment.ANGELIC, "Angelic augment burnt out!")
                    if (abilitySource.grants(user,ability)){
                        abilitySource.revokeFrom(user,ability)
                    }
                }
                addCountToQueue(user.uuid,NbtKeys.ANGELIC.str(),0)
            }
        }
    }

    override fun deactivateEffect(user: LivingEntity, level: Int, stack: ItemStack) {
        user as PlayerEntity
        if (abilitySource.grants(user,ability)){
            abilitySource.revokeFrom(user,ability)
        }
    }

}