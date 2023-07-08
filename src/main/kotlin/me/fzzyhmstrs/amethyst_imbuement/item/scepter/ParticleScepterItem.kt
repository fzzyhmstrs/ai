package me.fzzyhmstrs.amethyst_imbuement.item.scepter

import me.fzzyhmstrs.amethyst_core.scepter.ScepterToolMaterial
import me.fzzyhmstrs.fzzy_core.coding_util.PlayerParticlesV2
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.LivingEntity
import net.minecraft.particle.ParticleEffect
import net.minecraft.world.World

class ParticleScepterItem(private val particle: ParticleEffect, private val chance: Int,material: ScepterToolMaterial, settings: Settings): CustomScepterItem(material, settings) {

    @Environment(EnvType.CLIENT)
    override fun emitParticles(world: World, client: MinecraftClient, user: LivingEntity) {
        val particlePos = PlayerParticlesV2.scepterParticlePos(client, user)
        world.addParticle(particle,particlePos.x, particlePos.y, particlePos.z, 0.0, 0.0, 0.0)
        super.emitParticles(world, client, user)
    }

    override fun particleChance(): Int {
        return chance
    }

}