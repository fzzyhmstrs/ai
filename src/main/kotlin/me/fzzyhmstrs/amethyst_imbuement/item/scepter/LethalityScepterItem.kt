package me.fzzyhmstrs.amethyst_imbuement.item.scepter

import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterToolMaterial
import me.fzzyhmstrs.fzzy_core.coding_util.PlayerParticlesV2.scepterParticlePos
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.LivingEntity
import net.minecraft.particle.DustParticleEffect
import net.minecraft.world.World

class LethalityScepterItem(material: ScepterToolMaterial, settings: Settings): CustomSpellbladeItem(material,3,-2.7f, settings) {

    companion object{
        private val SMALL_DUST = DustParticleEffect(DustParticleEffect.RED,0.8f)
    }

    @Environment(EnvType.CLIENT)
    override fun emitParticles(world: World, client: MinecraftClient, user: LivingEntity) {
        val particlePos = scepterParticlePos(client, user)
        val rnd1 = world.random.nextDouble() * 0.1 - 0.05
        val rnd2 = world.random.nextDouble() * 0.2 - 0.1
        world.addParticle(SMALL_DUST,particlePos.x + rnd1, particlePos.y + rnd2, particlePos.z + rnd2, 0.0, 0.0, 0.0)
        super.emitParticles(world,client, user)
    }

    override fun particleChance(): Int {
        return 6
    }

}