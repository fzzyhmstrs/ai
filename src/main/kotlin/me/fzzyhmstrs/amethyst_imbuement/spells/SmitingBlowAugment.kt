package me.fzzyhmstrs.amethyst_imbuement.spells

import me.fzzyhmstrs.amethyst_core.augments.ScepterAugment
import me.fzzyhmstrs.amethyst_core.augments.base.SingleTargetAugment
import me.fzzyhmstrs.amethyst_core.augments.data.AugmentDatapoint
import me.fzzyhmstrs.amethyst_core.augments.paired.AugmentType
import me.fzzyhmstrs.amethyst_core.augments.paired.PairedAugments
import me.fzzyhmstrs.amethyst_core.interfaces.SpellCastingEntity
import me.fzzyhmstrs.amethyst_core.modifier.AugmentConsumer
import me.fzzyhmstrs.amethyst_core.modifier.AugmentEffect
import me.fzzyhmstrs.amethyst_core.scepter.LoreTier
import me.fzzyhmstrs.amethyst_core.scepter.ScepterTier
import me.fzzyhmstrs.amethyst_core.scepter.SpellType
import me.fzzyhmstrs.amethyst_imbuement.AI
import me.fzzyhmstrs.amethyst_imbuement.AIClient
import me.fzzyhmstrs.amethyst_imbuement.config.AiConfig
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.spells.pieces.SpellAdvancementChecks
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class SmitingBlowAugment: SingleTargetAugment(ScepterTier.TWO) {
    override val augmentData: AugmentDatapoint
        get() = TODO("Not yet implemented")

    //ml 5
    override val baseEffect: AugmentEffect
        get() = super.baseEffect
            .withDamage(4.5F,0.5F)
            .withRange(7.5,0.5)
            .withAmplifier(2)

    override fun appendDescription(description: MutableList<Text>, other: ScepterAugment, othersType: AugmentType) {
        TODO("Not yet implemented")
    }

    override fun provideArgs(pairedSpell: ScepterAugment): Array<Text> {
        TODO()
    }

    override fun onPaired(player: ServerPlayerEntity, pair: PairedAugments) {
        if (pair.spellsAreEqual()){
            SpellAdvancementChecks.grant(player, SpellAdvancementChecks.DOUBLE_TRIGGER)
        }
        if (pair.spellsAreUnique()){
            SpellAdvancementChecks.grant(player, SpellAdvancementChecks.UNIQUE_TRIGGER)
        }
    }

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
                if (target is SpellCastingEntity && AiConfig.entities.isEntityPvpTeammate(user, target,this)) return false
                val bl = if(target.isUndead) {
                    target.damage(user.damageSources.indirectMagic(user,user),effects.damage(level) * effects.amplifier(level))
                } else {
                    target.damage(user.damageSources.indirectMagic(user,user),effects.damage(level))
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
        private val SMITE_PARTICLES = AI.identity("smite_particles")
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
