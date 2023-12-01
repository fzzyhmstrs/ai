package me.fzzyhmstrs.amethyst_imbuement.config

import fzzyhmstrs.should_i_hit_that.api.ShouldHitResult
import fzzyhmstrs.should_i_hit_that.checkers.MobChecker
import io.github.ladysnake.pal.Pal
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.entity.Entity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

object TogglePvpMobChecker: MobChecker{

    private val pvpId = Identifier("togglepvp", "pvp_state")
    
    override fun shouldItHit(attacker: Entity?, victim: Entity, vararg args: Any?): ShouldHitResult{
        if (attacker == victim) return ShouldHitResult.PASS
        if (!FabricLoader.getInstance().isModLoaded("togglepvp")) return ShouldHitResult.PASS
        if (attacker !is ServerPlayerEntity || victim !is ServerPlayerEntity) return ShouldHitResult.PASS
        if (!Pal.isAbilityRegistered(pvpId)) return ShouldHitResult.PASS
        return try {
            val pvpAbility = Pal.provideRegisteredAbility(pvpId).get()
            if (pvpAbility.isEnabledFor(attacker) && pvpAbility.isEnabledFor(victim)){
                ShouldHitResult.HIT
            } else {
                ShouldHitResult.FAIL
            }
        } catch (e: Exception){
            ShouldHitResult.PASS
        }
    }

    fun togglePvpInstalledButNotPvp(attacker: Entity?, victim: Entity): Boolean{
        val bl = FabricLoader.getInstance().isModLoaded("togglepvp") && attacker is ServerPlayerEntity && victim is ServerPlayerEntity && Pal.isAbilityRegistered(pvpId)
        if (!bl) return false
        val pvpAbility = Pal.provideRegisteredAbility(pvpId).get()
        return !pvpAbility.isEnabledFor(attacker as ServerPlayerEntity) || !pvpAbility.isEnabledFor(victim as ServerPlayerEntity)
    }

    fun areBothPvp(attacker: Entity?, victim: Entity): Boolean{
        if (!FabricLoader.getInstance().isModLoaded("togglepvp")) return false
        if (attacker !is ServerPlayerEntity || victim !is ServerPlayerEntity) return false
        if (!Pal.isAbilityRegistered(pvpId)) return false
        return try{
            val pvpAbility = Pal.provideRegisteredAbility(pvpId).get()
            pvpAbility.isEnabledFor(attacker) && pvpAbility.isEnabledFor(victim)
        } catch (e: Exception){
            false
        }

    }
}