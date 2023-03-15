package me.fzzyhmstrs.amethyst_imbuement.scepter

import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier_util.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter_util.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter_util.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter_util.SpellType
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.scepter_util.augments.MinorSupportAugment
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.AIClient
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class SmitingBlowAugment: MinorSupportAugment(ScepterTier.TWO,5) {

    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withDamage(4.5F,0.5F)
            .withRange(7.5,0.5)
            .withAmplifier(2)

    override fun augmentStat(imbueLevel: Int): AugmentDatapoint {
        return AugmentDatapoint(SpellType.FURY,20,8,
            10, imbueLevel,2, LoreTier.LOW_TIER, RegisterItem.GLOWING_FRAGMENT)
    }

    override fun supportEffect(
        world: World,
        target: Entity?,
        user: LivingEntity,
        level: Int,
        effects: AugmentEffect
    ): Boolean {
        return if(target != null) {
            if (target is LivingEntity) {
                if (target is SpellCastingEntity && isEntityPvpTeammate(user, target,this)) continue
                val bl = if(target.isUndead) {
                    target.damage(DamageSource.magic(user,user),effects.damage(level) * effects.amplifier(level))
                } else {
                    target.damage(DamageSource.magic(user,user),effects.damage(level))
                }
                if (bl) {
                    effects.accept(target,AugmentConsumer.Type.HARMFUL)
                    effects.accept(user, AugmentConsumer.Type.BENEFICIAL)
                    if (user is ServerPlayerEntity){
                        sendParticles(target.pos, user)
                    }
                    generateParticles(world,target.pos)
                    world.playSound(null, target.blockPos, soundEvent(), SoundCategory.PLAYERS, 0.8F, 1.3F)
                }
                bl
            } else {
                false
            }
        } else {
            false
        }
    }

    private fun sendParticles(pos: Vec3d, user: ServerPlayerEntity){
        val buf = PacketByteBufs.create()
        buf.writeDouble(pos.x)
        buf.writeDouble(pos.y)
        buf.writeDouble(pos.z)
        ServerPlayNetworking.send(user, SMITE_PARTICLES, buf)
    }

    override fun soundEvent(): SoundEvent {
        return SoundEvents.ENTITY_ELDER_GUARDIAN_HURT
    }

    companion object{
        private val SMITE_PARTICLES = Identifier(AI.MOD_ID,"smite_particles")
        fun registerClient(){
            ClientPlayNetworking.registerGlobalReceiver(SMITE_PARTICLES){client,_,buf,_ ->
                val world = client.world ?: return@registerGlobalReceiver
                val posX = buf.readDouble()
                val posY = buf.readDouble()
                val posZ = buf.readDouble()
                client.execute {
                    generateParticles(world, Vec3d(posX, posY, posZ))
                }
            }
        }
        private fun generateParticles(world: World, pos: Vec3d){
            val random = AIClient.aiRandom()
            for (i in 1..16){
                val rnd1 =  random.nextDouble() - 0.5
                val rnd2 = random.nextDouble() - 0.5
                val rnd3 = -1.5 + (random.nextDouble() - 0.5) * 0.1
                world.addParticle(ParticleTypes.SPIT,true,pos.x + rnd1,pos.y + 2.3,pos.z + rnd2,0.0,rnd3,0.0)
            }
            for (i in 1..16){
                val rnd1 = random.nextDouble() - 0.5
                val rnd2 = random.nextDouble() - 0.5
                val rnd3 = -1.0 + (random.nextDouble() - 0.5) * 0.1
                world.addParticle(ParticleTypes.SPIT,true,pos.x + rnd1,pos.y + 2.0,pos.z + rnd2,0.0,rnd3,0.0)
            }
            for (i in 1..16){
                val rnd1 = random.nextDouble() - 0.5
                val rnd2 = random.nextDouble() - 0.5
                val rnd3 = -1.0 + (random.nextDouble() - 0.5) * 0.1
                world.addParticle(ParticleTypes.SPIT,true,pos.x +rnd1,pos.y + 2.6,pos.z + rnd2,0.0,rnd3,0.0)
            }
        }
    }
}
