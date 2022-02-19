package me.fzzyhmstrs.amethyst_imbuement.util

import net.minecraft.block.Block
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.world.RaycastContext
import kotlin.math.min


object RaycasterUtil {

    fun raycastEntity(distance: Double = 4.0, includeFluids: Boolean = false): Entity? {
        val client = MinecraftClient.getInstance()
        val hit = raycaster(distance, client,includeFluids) ?: return null
        //println(hit.toString())
        return when(hit.type){
            HitResult.Type.MISS -> null
            HitResult.Type.ENTITY -> {val entityHit = hit as EntityHitResult; entityHit.entity
            }
            else -> null
        }
    }

    fun raycastBlock(distance: Double = 4.0, includeFluids: Boolean = false): Block? {
        val client = MinecraftClient.getInstance()
        val hit = raycaster(distance, client, includeFluids) ?: return null
        return when(hit.type){
            HitResult.Type.MISS -> null
            HitResult.Type.BLOCK -> {
                val blockHit = hit as BlockHitResult;
                val blockPos = blockHit.blockPos;
                val blockState = client.world?.getBlockState(blockPos);
                blockState?.block
            }
            else -> null
        }
    }

    fun raycastHit(distance: Double = 4.0, includeFluids: Boolean = false): HitResult? {
        val client = MinecraftClient.getInstance()
        return raycaster(distance, client, includeFluids)
    }

    fun raycastEntityArea(distance: Double = 4.0,pos: BlockPos? = null): MutableList<Entity>{
        val client = MinecraftClient.getInstance()
        val entity: Entity? = client.getCameraEntity()
        if (entity == null || client.world == null) {
            return mutableListOf()
        }
        val box: Box
        box = if (pos != null){
            Box(pos.x+distance, pos.y+distance/2,pos.z+distance,pos.x-distance, pos.y-distance/2,pos.z-distance)
        } else {
            Box(entity.x+distance, entity.y+distance/2,entity.z+distance,entity.x-distance, entity.y-distance/2,entity.z-distance)
        }
        val entityList: MutableList<Entity> = entity.world.getOtherEntities(entity, box) { entity2: Entity -> !entity2.isSpectator && entity2.collides() }
        //println(entityList)
        //println(box.center)
        //println(box.averageSideLength)
        return entityList.ifEmpty {
            mutableListOf()
        }
    }

    private fun raycaster(distance: Double = 10.0, client: MinecraftClient, includeFluids: Boolean): HitResult? {
        val tickDelta = client.tickDelta
        val cameraDirection = client.cameraEntity!!.getRotationVec(tickDelta)
        return raycastInDirection(client, tickDelta, cameraDirection, distance, includeFluids)
    }


    private fun raycastInDirection(client: MinecraftClient, tickDelta: Float, direction: Vec3d, distance: Double, includeFluids: Boolean): HitResult? {
        val entity: Entity? = client.getCameraEntity()
        if (entity == null || client.world == null) {
            return null
        }
        var reachDistance: Double = distance //Change this to extend the reach
        val target: HitResult? = raycast(entity, reachDistance, tickDelta, includeFluids, direction)
        val cameraPos: Vec3d = entity.getCameraPosVec(tickDelta)
        reachDistance *= reachDistance
        if (target != null) {
            reachDistance = target.pos.squaredDistanceTo(cameraPos)
        }
        val box: Box = entity
            .boundingBox
            .stretch(entity.getRotationVec(tickDelta).multiply(reachDistance))
            .expand(1.0, 1.0, 1.0)
        //println(entity.world.getOtherEntities(entity, box, { entity2: Entity -> !entity2.isSpectator && entity2.collides() }).toString())
        val entityList: MutableList<Entity> = entity.world.getOtherEntities(entity, box) { entity2: Entity -> !entity2.isSpectator && entity2.collides() }
        if (entityList.isEmpty()){
            //println("bleep")
            return target
        } else {
            var dist = 1000000.0
            var tempEntity: Entity? = null
            for (entity3 in entityList) {
                val distTemp = dist
                dist = min(entity3.squaredDistanceTo(entity),dist)
                if (distTemp > dist){
                    tempEntity = entity3
                }
            }
            return if (tempEntity != null) {
                EntityHitResult(tempEntity, tempEntity.pos)
            } else {
                println("bloop")
                target
            }
        }
        /*val entityHitResult = ProjectileUtil.raycast(
            entity,
            cameraPos,
            vec3d3,
            box,
            { entity2: Entity -> !entity2.isSpectator && entity2.collides() },
            reachDistance
        ) ?: return target
        val entity3: Entity = entityHitResult.entity
        println(entity3.toString())
        val vec3d4 = entityHitResult.pos
        val g = cameraPos.squaredDistanceTo(vec3d4)
        if (g > distance*distance) {
            return null
        } else if (g < reachDistance || target == null) {
            target = entityHitResult
            if (entity3 is LivingEntity || entity3 is ItemFrameEntity) {
                client.targetedEntity = entity3
            }
        }*/

    }

    private fun raycast(
        entity: Entity,
        maxDistance: Double,
        tickDelta: Float,
        includeFluids: Boolean,
        direction: Vec3d
    ): HitResult? {
        val end: Vec3d = entity.getCameraPosVec(tickDelta).add(direction.multiply(maxDistance))
        return entity.world.raycast(
            RaycastContext(
                entity.getCameraPosVec(tickDelta),
                end,
                RaycastContext.ShapeType.OUTLINE,
                if (includeFluids) RaycastContext.FluidHandling.SOURCE_ONLY else RaycastContext.FluidHandling.NONE,
                entity
            )
        )
    }

}