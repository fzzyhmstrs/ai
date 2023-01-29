package me.fzzyhmstrs.amethyst_imbuement.entity

import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.TargetPredicate
import net.minecraft.entity.ai.goal.TrackTargetGoal
import net.minecraft.entity.mob.PathAwareEntity

open class TrackSummonerAttackerGoal(summoned: PathAwareEntity, private val summoner: LivingEntity): TrackTargetGoal(summoned,false) {

    private var attacker: LivingEntity? = null
    private var lastAttackedTime = 0

    override fun canStart(): Boolean {
        attacker = summoner.attacker
        val i = summoner.lastAttackedTime
        val bl = i != lastAttackedTime
        val bl2 = this.canTrack(this.attacker, TargetPredicate.DEFAULT) && mob.canTarget(attacker)
        return bl && bl2
    }

    override fun start() {
        mob.target = attacker
        this.lastAttackedTime = summoner.lastAttackedTime
        super.start()
    }
}