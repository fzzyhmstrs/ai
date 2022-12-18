package me.fzzyhmstrs.amethyst_imbuement.item

import me.fzzyhmstrs.amethyst_core.coding_util.PlayerParticlesV2.scepterParticlePos
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterToolMaterial
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.LivingEntity
import net.minecraft.particle.DustParticleEffect
import net.minecraft.util.DyeColor
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class EquinoxScepterItem(material: ScepterToolMaterial, settings: Settings): CustomScepterItem(material, settings) {

    @Environment(EnvType.CLIENT)
    override fun emitParticles(world: World, client: MinecraftClient, user: LivingEntity) {
        val particlePos = scepterParticlePos(client, user)
        val rnd1 = world.random.nextDouble() * 0.1 - 0.05
        val rnd2 = world.random.nextDouble() * 0.2 - 0.1
        val rnd3 = world.random.nextInt(DyeColor.values().size)
        val colorInt = DyeColor.values()[rnd3].signColor
        val color = Vec3d.unpackRgb(colorInt).toVector3f()
        world.addParticle(DustParticleEffect(color,0.8f),particlePos.x + rnd1, particlePos.y + rnd2, particlePos.z + rnd2, 0.0, 0.0, 0.0)
        super.emitParticles(world,client, user)
    }

    override fun particleChance(): Int {
        return 8
    }

}