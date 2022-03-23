package me.fzzyhmstrs.amethyst_imbuement.util

import net.minecraft.block.Block
import net.minecraft.client.MinecraftClient
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.world.GameRules
import net.minecraft.world.RaycastContext
import net.minecraft.world.World
import java.util.*
import kotlin.math.acos
import kotlin.math.min


object RaycasterUtil {

    fun raycastEntity(distance: Double = 4.0, includeFluids: Boolean = false): Entity? {
        val client = MinecraftClient.getInstance()
        val hit = raycaster(distance, client,includeFluids) ?: return null
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
                val blockHit = hit as BlockHitResult
                val blockPos = blockHit.blockPos
                val blockState = client.world?.getBlockState(blockPos)
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
        val box: Box = if (pos != null){
            Box(pos.x+distance, pos.y+distance/2,pos.z+distance,pos.x-distance, pos.y-distance/2,pos.z-distance)
        } else {
            Box(entity.x+distance, entity.y+distance/2,entity.z+distance,entity.x-distance, entity.y-distance/2,entity.z-distance)
        }
        val entityList: MutableList<Entity> = entity.world.getOtherEntities(entity, box) { entity2: Entity -> !entity2.isSpectator && entity2.collides() }
        return entityList.ifEmpty {
            mutableListOf()
        }
    }

    fun raycastEntityRotatedArea(world: World,
                                 user: PlayerEntity?,
                                 center: Vec3d,
                                 rotation: Vec3d,
                                 perpendicularRotation: Vec3d,
                                 lengthAlongRotation: Double,
                                 lengthAlongPerpendicular: Double,
                                 lengthPerpendicularToBoth: Double): MutableList<Entity>{
        val entityList: MutableList<Entity> = mutableListOf()
        val box = RaycasterBox(center, rotation, perpendicularRotation, lengthAlongRotation,
                                lengthAlongPerpendicular, lengthPerpendicularToBoth)

        if (world is ServerWorld) {
            for (entity in world.iterateEntities()) {
                if (entity !is LivingEntity) continue
                if (box.testPoint(entity.pos.add(0.0, entity.height / 2.0, 0.0))) {
                    entityList.add(entity)
                }
            }
        } else if (world is ClientWorld){
            for (entity in world.entities) {
                if (entity !is LivingEntity) continue
                if (box.testPoint(entity.pos.add(0.0, entity.height / 2.0, 0.0))) {
                    entityList.add(entity)
                }
            }
        }
        return entityList
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

        val rotation = entity.getRotationVec(tickDelta)
        val perpendicularToPosX = 1.0
        val perpendicularToPosZ = (rotation.x/rotation.z) * -1
        val perpendicularVector = Vec3d(perpendicularToPosX,0.0,perpendicularToPosZ).normalize()
        val center = cameraPos.add(entity.getRotationVec(tickDelta).multiply(reachDistance/2.0))
        val entityList: MutableList<Entity> = raycastEntityRotatedArea(entity.world,null,center,rotation,perpendicularVector,reachDistance,1.0,1.8)
        if (entityList.isEmpty()){
            return target
        } else {
            var dist = 1000000.0
            var tempEntity: Entity? = null
            for (entity3 in entityList) {
                val distTemp = dist
                dist = min(entity3.squaredDistanceTo(entity),dist)
                if (distTemp > dist && entity3 != entity){
                    tempEntity = entity3
                }
            }
            return if (tempEntity != null) {
                EntityHitResult(tempEntity, tempEntity.pos)
            } else {
                target
            }
        }
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

    @Suppress("SpellCheckingInspection", "UnnecessaryVariable")
    private class RaycasterBox(center: Vec3d,
                               rotation: Vec3d,
                               perpendicularRotation: Vec3d,
                               lengthAlongRotation: Double,
                               lengthAlongPerpendicular: Double,
                               lengthPerpendicularToBoth: Double){


        private val pointMap: MutableMap<Int,RaycasterBoxPoint> = mutableMapOf()
        private val directionMap: MutableMap<Int,RaycasterBoxPoint> = mutableMapOf()
        private val origin = center
        private val distanceToOrigin: Double
        private var minX: Double = 0.0
        private var minY: Double = 0.0
        private var minZ: Double = 0.0
        private var maxX: Double = 0.0
        private var maxY: Double = 0.0
        private var maxZ: Double = 0.0


        init{
            val doublePerpendicularRotation = rotation.crossProduct(perpendicularRotation)
            val rp = rotation.normalize().multiply(lengthAlongRotation/2.0)
            val prp = perpendicularRotation.normalize().multiply(lengthAlongPerpendicular/2.0)
            val dprp = doublePerpendicularRotation.normalize().multiply(lengthPerpendicularToBoth/2.0)

            distanceToOrigin = origin.relativize(center.add(rp).add(prp).add(dprp)).length()

            //initialize the min/max values
            minX = center.x
            maxX = center.x
            minY = center.y
            maxY = center.y
            minZ = center.z
            maxZ = center.z

            //generate the corner points
            for (a in flipA){
                for (b in flipB){
                    val x = a
                    val y = b.first
                    val z = b.second
                    val pointVec = center.add(rp.multiply(x)).add(prp.multiply(y)).add(dprp.multiply(z))
                    if (pointVec.x > maxX) {
                        maxX = pointVec.x
                    } else if (pointVec.x < minX){
                        minX = pointVec.x
                    }
                    if (pointVec.y > maxY) {
                        maxY = pointVec.y
                    } else if (pointVec.y < minY){
                        minY = pointVec.y
                    }
                    if (pointVec.z > maxZ) {
                        maxZ = pointVec.z
                    } else if (pointVec.z < minZ){
                        minZ = pointVec.z
                    }
                    val pointIndex = pointNumbering[Triple(x,y,z)]?:throw IndexOutOfBoundsException("Map pointNumbering not correctly configured!")
                    pointMap[pointIndex] = RaycasterBoxPoint(pointVec,center)
                }
            }

            //generate the cardinal direction vectors for angle checking
            for (keys in directionNumbering){
                val x = keys.value.first
                val y = keys.value.second
                val z = keys.value.third
                val directionVec = center.add(rp.multiply(x)).add(prp.multiply(y)).add(dprp.multiply(z))
                directionMap[keys.key] = RaycasterBoxPoint(directionVec,center)
            }
        }

        fun testPoint(point: Vec3d): Boolean{
            if (testOutsideDistance(point)) return false
            if (testOutsideMinMax(point)) return false
            if (testExactMatches(point)) return true
            val pointVectorFromOrigin = origin.relativize(point)
            val anglesToDirections: SortedMap<Double,Int> = mutableMapOf<Double, Int>().toSortedMap()

            for (directionPoint in directionMap){
                var angle = directionPoint.value.angleToPoint(point)
                while (anglesToDirections.containsKey(angle)){
                    angle += 0.00000001
                }
                anglesToDirections[angle] = directionPoint.key
            }

            val threeClosestPlanes: MutableList<Int> = mutableListOf()
            for (angles in anglesToDirections){
                val plane = angles.value
                threeClosestPlanes.add(plane)
                if (threeClosestPlanes.size == 3) break
            }


            if (threeClosestPlanes.size != 3) return false
            for (planeIndex in threeClosestPlanes) {
                val pointSet = directionToPointReference[planeIndex]?:throw IndexOutOfBoundsException("Map directionToPointReference not correctly configured!")
                val point1 = pointMap[pointSet.a]?:throw IndexOutOfBoundsException("Map pointMap not correctly configured at A!")
                val point2 = pointMap[pointSet.b]?:throw IndexOutOfBoundsException("Map pointMap not correctly configured at B!")
                val point3 = pointMap[pointSet.c]?:throw IndexOutOfBoundsException("Map pointMap not correctly configured at C!")
                val planeVec1 = point1.vecToPoint(point3.asVec3d())
                val planeVec2 = point2.vecToPoint(point3.asVec3d())
                val planeCrossVec = planeVec1.crossProduct(planeVec2)
                val planeConstant = planeCrossVec.x * point1.asVec3d().x
                +planeCrossVec.y * point1.asVec3d().y
                +planeCrossVec.z * point1.asVec3d().z
                val pointConstant = planeCrossVec.x * pointVectorFromOrigin.x
                +planeCrossVec.y * pointVectorFromOrigin.y
                +planeCrossVec.z * pointVectorFromOrigin.z
                val t = planeConstant / pointConstant
                val planeIntersectionPoint = pointVectorFromOrigin.multiply(t)
                if (planeIntersectionPoint.length() < pointVectorFromOrigin.length()) return false
            }
            return true
        }

        private fun testOutsideMinMax(point: Vec3d): Boolean{
            @Suppress("RedundantIf")
            return if (point.x < minX){
                true
            } else if (point.x > maxX){
                true
            } else if (point.y < minY){
                true
            } else if (point.y > maxY){
                true
            } else if (point.z < minZ){
                true
            } else if (point.z > maxZ){
                true
            } else {
                false
            }
        }

        private fun testExactMatches(point: Vec3d): Boolean{
            for (boxPoint in pointMap){
                if (boxPoint.value.asVec3d() == point) return true
            }
            if (point == origin) return true
            return false
        }

        private fun testOutsideDistance(point: Vec3d): Boolean{
            return origin.relativize(point).length() > distanceToOrigin
        }

        private class RaycasterBoxPoint(x: Double, y:Double, z:Double, origin: Vec3d){

            constructor(point: Vec3d, origin: Vec3d) :this(point.x,point.y,point.z,origin)

            @Suppress("JoinDeclarationAndAssignment")
            private val vectorFromOrigin: Vec3d
            private val vectorToOrigin: Vec3d
            private val position: Vec3d = Vec3d(x,y,z)

            init{
                vectorFromOrigin = origin.relativize(position)
                vectorToOrigin = vectorFromOrigin.negate()
            }

            fun angleToPoint(point: Vec3d): Double{
                //needs to be a zero-origin point
                //calcualting angle between vectors with arccos(dotproduct[a*b] / (magnitude_a * magnitude_b))
                return acos(vectorFromOrigin.dotProduct(point)/(vectorFromOrigin.length() * point.length()))
            }

            fun vecToPoint(point:Vec3d): Vec3d{
                return position.relativize(point)
            }

            fun asVec3d(): Vec3d{
                return position
            }

        }

        companion object{
            private val directionNumbering: Map<Int,Triple<Double,Double,Double>> =
                mapOf(1 to Triple(1.0,0.0,0.0),
                2 to Triple(-1.0,0.0,0.0),
                3 to Triple(0.0,1.0,0.0),
                4 to Triple(0.0,-1.0,0.0),
                5 to Triple(0.0,0.0,1.0),
                6 to Triple(0.0,0.0,-1.0))
            private val pointNumbering: Map<Triple<Double,Double,Double>,Int> =
                mapOf(
                Triple(1.0,1.0,1.0) to 1,
                Triple(1.0,-1.0,1.0) to 2,
                Triple(1.0,1.0,-1.0) to 3,
                Triple(1.0,-1.0,-1.0) to 4,
                Triple(-1.0,1.0,1.0) to 5,
                Triple(-1.0,-1.0,1.0) to 6,
                Triple(-1.0,1.0,-1.0) to 7,
                Triple(-1.0,-1.0,-1.0) to 8)
            private val directionToPointReference: Map<Int,Quartet<Int>> =
                mapOf(1 to Quartet(1,2,3,4),
                    2 to Quartet(5,6,7,8),
                    3 to Quartet(1,5,3,7),
                    4 to Quartet(2,6,4,8),
                    5 to Quartet(1,2,5,6),
                    6 to Quartet(3,4,7,8))
            private val flipA : Array<Double> = arrayOf(1.0, -1.0)
            private val flipB : Array<Pair<Double,Double>> = arrayOf(Pair(1.0,1.0),Pair(-1.0,1.0),Pair(1.0,-1.0),Pair(-1.0,-1.0))

            private data class Quartet<out A>(val a: A,val b: A, val c: A, val d: A)
        }

    }

}