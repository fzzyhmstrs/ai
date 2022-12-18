package me.fzzyhmstrs.amethyst_imbuement.model

import me.fzzyhmstrs.amethyst_imbuement.entity.UnhallowedEntity
import net.minecraft.client.model.ModelPart
import net.minecraft.client.render.entity.model.BipedEntityModel
import net.minecraft.client.render.entity.model.CrossbowPosing
import net.minecraft.entity.mob.MobEntity

class UnhallowedEntityModel(_root: ModelPart): BipedEntityModel<UnhallowedEntity>(_root) {


    override fun setAngles(unhallowedEntity: UnhallowedEntity, f: Float, g: Float, h: Float, i: Float, j: Float) {
        super.setAngles(unhallowedEntity, f, g, h, i, j)
        CrossbowPosing.meleeAttack(leftArm, rightArm, this.isAttacking(unhallowedEntity), handSwingProgress, h)
    }

    private fun isAttacking(unhallowedEntity: UnhallowedEntity): Boolean {
        return (unhallowedEntity as MobEntity).isAttacking
    }
}