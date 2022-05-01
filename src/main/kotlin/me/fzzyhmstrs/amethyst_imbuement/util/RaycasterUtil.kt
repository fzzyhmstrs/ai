package me.fzzyhmstrs.amethyst_imbuement.util

import net.minecraft.client.MinecraftClient
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.RaycastContext
import java.util.*
import kotlin.math.acos
import kotlin.math.min


object RaycasterUtil {

    fun raycastHit(distance: Double = 4.5,entity: Entity, includeFluids: Boolean = false): HitResult? {
        return raycasterServer(distance, entity, includeFluids)
    }

    @Suppress("unused")
    fun raycastBlock(distance: Double = 4.5,entity: Entity, includeFluids: Boolean = false): BlockPos? {
        val hit = raycast(entity, distance, 1.0F, includeFluids, entity.getRotationVec(1.0F)) ?: return null
        return when(hit.type){
            HitResult.Type.MISS -> null
            HitResult.Type.BLOCK -> {
                val blockHit = hit as BlockHitResult
                blockHit.blockPos
            }
            else -> null
        }
    }

    fun raycastEntity(distance: Double = 4.5,entity: Entity, includeFluids: Boolean = false): Entity? {
        val hit = raycasterServer(distance, entity, includeFluids) ?: return null
        return when(hit.type){
            HitResult.Type.MISS -> null
            HitResult.Type.ENTITY -> {val entityHit = hit as EntityHitResult; entityHit.entity
            }
            else -> null
        }
    }

    fun raycastEntityArea(distance: Double = 4.5,entity: Entity,pos: Vec3d? = null, rotation: Vec3d = Vec3d(1.0,0.0,0.0)): MutableList<Entity>{
        val pos2: Vec3d = pos ?: entity.pos
        if (entity.world.isClient) return mutableListOf()
        val world = entity.world as ServerWorld
        val entityList: MutableList<Entity> = raycastEntityRotatedArea(world.iterateEntities(),entity,pos2,rotation,distance)

        return entityList.ifEmpty {
            mutableListOf()
        }
    }

    private fun raycastEntityRotatedArea(iterable : Iterable<Entity>,
                                         entityToExclude: Entity?,
                                         center: Vec3d,
                                         rotation: Vec3d,
                                         size: Double): MutableList<Entity>{
        val flatRotation = rotation.multiply(1.0,0.0,1.0)
        val flatPerpendicular = perpendicularVector(flatRotation,InPlane.XZ)
        return raycastEntityRotatedArea(iterable,entityToExclude, center, flatRotation,flatPerpendicular, size,size,size)
    }

    fun raycastEntityRotatedArea(iterable : Iterable<Entity>,
                                 entityToExclude: Entity?,
                                 center: Vec3d,
                                 rotation: Vec3d,
                                 perpendicularRotation: Vec3d,
                                 lengthAlongRotation: Double,
                                 lengthAlongPerpendicular: Double,
                                 lengthPerpendicularToBoth: Double): MutableList<Entity>{
        val entityList: MutableList<Entity> = mutableListOf()
        val box = RaycasterBox(center, rotation, perpendicularRotation, lengthAlongRotation,
                                lengthAlongPerpendicular, lengthPerpendicularToBoth)
        for (entity in iterable) {
            if (entity !is LivingEntity) continue
            if (entity.isSpectator) continue
            if (entity === entityToExclude) continue
            if (box.testPoint(entity.pos.add(0.0, entity.height / 2.0, 0.0))) {
                entityList.add(entity)
            }
        }
        return entityList
    }

    private fun raycasterServer(distance: Double = 10.0, entity: Entity, includeFluids: Boolean,tickDelta: Float = 1.0F): HitResult? {
        val rotation = entity.getRotationVec(tickDelta)
        val world = entity.world
        if (world.isClient) return null
        var reachDistance: Double = distance //Change this to extend the reach
        val target: HitResult? = raycast(entity, reachDistance, tickDelta, includeFluids, rotation)
        val cameraPos: Vec3d = entity.getCameraPosVec(tickDelta)
        if (target != null) {
            reachDistance = target.pos.distanceTo(cameraPos)
        }

        val perpendicularVector = perpendicularVector(rotation,InPlane.XZ)
        val center = cameraPos.add(rotation.multiply(reachDistance/2.0))

        val entityList: MutableList<Entity> = raycastEntityRotatedArea((world as ServerWorld).iterateEntities(),entity,center,rotation,perpendicularVector,reachDistance,1.0,1.8)
        if (entityList.isEmpty()){
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
                target
            }
        }
    }

    private fun raycaster(distance: Double = 10.0, client: MinecraftClient, includeFluids: Boolean): HitResult? {
        val tickDelta = client.tickDelta
        val entity: Entity? = client.getCameraEntity()
        if (entity == null || client.world == null) {
            return null
        }
        val rotation = entity.getRotationVec(tickDelta)
        var reachDistance: Double = distance //Change this to extend the reach
        val target: HitResult? = raycast(entity, reachDistance, tickDelta, includeFluids, rotation)
        val cameraPos: Vec3d = entity.getCameraPosVec(tickDelta)
        if (target != null) {
            reachDistance = target.pos.distanceTo(cameraPos)
        }

        val perpendicularVector = perpendicularVector(rotation,InPlane.XZ)
        val center = cameraPos.add(entity.getRotationVec(tickDelta).multiply(reachDistance/2.0))
        val entityList: MutableList<Entity> = raycastEntityRotatedArea((entity.world as ClientWorld).entities,entity,center,rotation,perpendicularVector,reachDistance,1.0,1.8)
        if (entityList.isEmpty()){
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

    fun perpendicularVector(rotation: Vec3d, inPlane: InPlane): Vec3d{
        return when (inPlane) {
            InPlane.XY -> {
                if (rotation.y == 0.0){
                    Vec3d(0.0,1.0,0.0)
                } else {
                    val perpendicularToPosX = 1.0
                    val perpendicularToPosY = (rotation.x / rotation.y) * -1
                    Vec3d(perpendicularToPosX, perpendicularToPosY, 0.0).normalize()
                }
            }
            InPlane.XZ -> {
                if (rotation.z == 0.0){
                    Vec3d(0.0,0.0,1.0)
                } else {
                    val perpendicularToPosX = 1.0
                    val perpendicularToPosZ = (rotation.x / rotation.z) * -1
                    Vec3d(perpendicularToPosX, 0.0, perpendicularToPosZ).normalize()
                }
            }
            InPlane.YZ -> {
                if (rotation.z == 0.0){
                    Vec3d(0.0,0.0,1.0)
                } else {
                    val perpendicularToPosY = 1.0
                    val perpendicularToPosZ = (rotation.y / rotation.z) * -1
                    Vec3d(0.0, perpendicularToPosY, perpendicularToPosZ).normalize()
                }
            }
        }
    }

    enum class InPlane{
        XY,
        XZ,
        YZ
    }

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
            val drp = doublePerpendicularRotation.normalize().multiply(lengthPerpendicularToBoth/2.0)

            distanceToOrigin = origin.relativize(center.add(rp).add(prp).add(drp)).length()

            //initialize the min/max values
            minX = center.x
            maxX = center.x
            minY = center.y
            maxY = center.y
            minZ = center.z
            maxZ = center.z

            //generate the corner points
            for (x in flipA){
                for (b in flipB){
                    val y = b.first
                    val z = b.second
                    val pointVec = center.add(rp.multiply(x)).add(prp.multiply(y)).add(drp.multiply(z))
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
                val directionVec = center.add(rp.multiply(x)).add(prp.multiply(y)).add(drp.multiply(z))
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
                //calculating angle between vectors with arc cos(dot product[a*b] / (magnitude_a * magnitude_b))
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