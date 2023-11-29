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
}
