package me.fzzyhmstrs.amethyst_imbuement.config

import fzzyhmstrs.should_i_hit_that.checkers.MobChecker
import fzzyhmstrs.should_i_hit_that.api.ShouldHitResult
import net.minecraft.entity.Entity

object TogglePvpMobChecker: MobChecker{

    private val pvpId = Identifier("togglepvp", "pvp_state")
    
    override fun shouldItHit(attacker: Entity?, victim: Entity, vararg args: Any?){
        if (!FabricLoader.getInstance().isModLoaded("togglepvp")) return ShouldHitResult.PASS
        if (attacker !is ServerPlayerEntity || victim !is ServerPlayerEntity) return ShouldHitResult.PASS
        if (!Pal.isAbilityRegistered(pvpId)) return ShouldHitResult.PASS
        return try {
            val pvpAbility = Pal.provideRegisteredAbility(pvpId).get()
            if (pvpAbility.isEnabledFor(attacker) && pvpAbility.isEnabledFor(victim){
                ShouldHitResult.HIT
            } else {
                ShouldHitResult.FAIL
            }
        } catch (e: Exception){
            ShouldHitResult.PASS
        }
    }
}
