package me.fzzyhmstrs.amethyst_imbuement.entity.goal

import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.TargetPredicate
import net.minecraft.entity.ai.goal.TrackTargetGoal
import net.minecraft.entity.mob.PathAwareEntity

open class TrackSummonerAttackerGoal(summoned: PathAwareEntity, private var summoner: LivingEntity?): TrackTargetGoal(summoned,false) {

    private var attacker: LivingEntity? = null
    private var lastAttackedTime = 0

    override fun canStart(): Boolean {
        val summonerChk = summoner ?: return false
        attacker = summonerChk.attacker
        val i = summonerChk.lastAttackedTime
        val bl = i != lastAttackedTime
        val bl2 = this.canTrack(this.attacker, TargetPredicate.DEFAULT) && mob.canTarget(attacker)
        return bl && bl2
    }

    override fun start() {
        mob.target = attacker
        this.lastAttackedTime = summoner?.lastAttackedTime?:0
        super.start()
    }

    fun setSummoner(summoner: LivingEntity){
        this.summoner = summoner
    }
}