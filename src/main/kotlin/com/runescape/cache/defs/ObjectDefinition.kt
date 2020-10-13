package com.runescape.cache.defs

import com.runescape.Client
import com.runescape.cache.FileArchive
import com.runescape.collection.ReferenceCache
import com.runescape.io.Buffer

@ConfigurationLoaders
class ObjectDefinition : Definitions() {

    override var count: Int = 0
    override val typename: String = "Objects"

    override fun init(archive: FileArchive) {
        init(archive) {
            stream = Buffer(archive.readFile("loc.dat"))
            val stream = Buffer(archive.readFile("loc.idx"))
            count = stream.readUShort()
            streamIndices = IntArray(count)
            var offset = 2
            for (index in 0 until count) {
                streamIndices[index] = offset
                offset += stream.readUShort()
            }
            cache = arrayOfNulls(20)
            for (index in 0..19) {
                cache[index] = ObjectDefinition()
            }
        }
    }


    fun lookup(id: Int): ObjectDefinition {
        var id = id
        if (id > streamIndices.size) {
            id = streamIndices.size - 1
        }
        for (index in 0..19) {
            if (cache[index]!!.type === id){
                return cache[index]!!
            }
        }
        cacheIndex = (cacheIndex + 1) % 20
        val objectDef = cache[cacheIndex]!!
        stream.currentPosition = streamIndices.get(id)
        objectDef.type = id
        objectDef.reset()
        objectDef.decode(stream)

        return objectDef
    }

    fun reset() {
        objectSizeX = 1
        objectSizeY = 1
        solid = true
        impenetrable = true
        isInteractive = false
        contouredGround = false
        delayShading = false
        occludes = false
        animation = -1
        decorDisplacement = 16
        ambientLighting = 0
        lightDiffusion = 0
        minimapFunction = -1
        mapscene = -1
        inverted = false
        castsShadow = true
        scaleX = 128
        scaleY = 128
        scaleZ = 128
        surroundings = 0
        translateX = 0
        translateY = 0
        translateZ = 0
        obstructsGround = false
        removeClipping = false
        supportItems = -1
        varbit = -1
        varp = -1
    }


    fun decode(buffer: Buffer) {
        while (true) {
            val opcode = buffer.readUnsignedByte()
            if (opcode == 0) {
                break
            } else if (opcode == 1) {
                val len = buffer.readUnsignedByte()
                if (len > 0) {
                    if (modelIds == null) {
                        modelTypes = IntArray(len)
                        modelIds = IntArray(len)
                        for (i in 0 until len) {
                            modelIds[i] = buffer.readUShort()
                            modelTypes[i] = buffer.readUnsignedByte()
                        }
                    } else {
                        buffer.currentPosition += len * 3
                    }
                }
            } else if (opcode == 2) {
                name = buffer.readString()
            } else if (opcode == 5) {
                val len = buffer.readUnsignedByte()
                if (len > 0) {
                    if (modelIds == null) {
                        modelIds = IntArray(len)
                        for (i in 0 until len) {
                            modelIds[i] = buffer.readUShort()
                        }
                    } else {
                        buffer.currentPosition += len * 2
                    }
                }
            } else if (opcode == 14) {
                objectSizeX = buffer.readUnsignedByte()
            } else if (opcode == 15) {
                objectSizeY = buffer.readUnsignedByte()
            } else if (opcode == 17) {
                solid = false
            } else if (opcode == 18) {
                impenetrable = false
            } else if (opcode == 19) {
                isInteractive = buffer.readUnsignedByte() === 1
            } else if (opcode == 21) {
                contouredGround = true
            } else if (opcode == 22) {
                delayShading = true
            } else if (opcode == 23) {
                occludes = true
            } else if (opcode == 24) {
                animation = buffer.readUShort()
                if (animation == 0xFFFF) {
                    animation = -1
                }
            } else if (opcode == 27) { // clipType = 1;
            } else if (opcode == 28) {
                decorDisplacement = buffer.readUnsignedByte()
            } else if (opcode == 29) {
                ambientLighting = buffer.readSignedByte()
            } else if (opcode == 39) {
                lightDiffusion = buffer.readSignedByte() * 25
            } else if (opcode in 30..34) {
                if (interactions == null) {
                    interactions = arrayOfNulls(5)
                }
                interactions[opcode - 30] = buffer.readString()
                if (interactions[opcode - 30].equals("Hidden",ignoreCase = true)) {
                    interactions[opcode - 30] = null
                }
            } else if (opcode == 40) {
                val len = buffer.readUnsignedByte()
                modifiedModelColors = IntArray(len)
                originalModelColors = IntArray(len)
                for (i in 0 until len) {
                    modifiedModelColors[i] = buffer.readUShort()
                    originalModelColors[i] = buffer.readUShort()
                }
            } else if (opcode == 41) {
                val len = buffer.readUnsignedByte()
                modifiedModelTexture = ShortArray(len)
                originalModelTexture = ShortArray(len)
                for (i in 0 until len) {
                    modifiedModelTexture[i] = buffer.readUShort().toShort()
                    originalModelTexture[i] = buffer.readUShort().toShort()
                }
            } else if (opcode == 62) {
                inverted = true
            } else if (opcode == 64) {
                castsShadow = false
            } else if (opcode == 65) {
                scaleX = buffer.readUShort()
            } else if (opcode == 66) {
                scaleY = buffer.readUShort()
            } else if (opcode == 67) {
                scaleZ = buffer.readUShort()
            } else if (opcode == 68) {
                mapscene = buffer.readUShort()
            } else if (opcode == 69) {
                surroundings = buffer.readUnsignedByte()
            } else if (opcode == 70) {
                translateX = buffer.readUShort()
            } else if (opcode == 71) {
                translateY = buffer.readUShort()
            } else if (opcode == 72) {
                translateZ = buffer.readUShort()
            } else if (opcode == 73) {
                obstructsGround = true
            } else if (opcode == 74) {
                removeClipping = true
            } else if (opcode == 75) {
                supportItems = buffer.readUnsignedByte()
            } else if (opcode == 78) {
                buffer.readUShort() // ambient sound id
                buffer.readUnsignedByte()
            } else if (opcode == 79) {
                buffer.readUShort()
                buffer.readUShort()
                buffer.readUnsignedByte()
                val len = buffer.readUnsignedByte()
                for (i in 0 until len) {
                    buffer.readUShort()
                }
            } else if (opcode == 81) {
                buffer.readUnsignedByte()
            } else if (opcode == 82) {
                minimapFunction = buffer.readUShort()
                if (minimapFunction == 0xFFFF) {
                    minimapFunction = -1
                }
            } else if (opcode == 77 || opcode == 92) {
                varp = buffer.readUShort()
                if (varp == 0xFFFF) {
                    varp = -1
                }
                varbit = buffer.readUShort()
                if (varbit == 0xFFFF) {
                    varbit = -1
                }
                var value = -1
                if (opcode == 92) {
                    value = buffer.readUShort()
                    if (value == 0xFFFF) {
                        value = -1
                    }
                }
                val len = buffer.readUnsignedByte()
                childrenIDs = IntArray(len + 2)
                for (i in 0..len) {
                    childrenIDs[i] = buffer.readUShort()
                    if (childrenIDs[i] === 0xFFFF) {
                        childrenIDs[i] = -1
                    }
                }
                childrenIDs[len + 1] = value
            } else {
                println("invalid opcode: $opcode")
            }
        }

        if (name != null && name != "null") {
            isInteractive = modelIds != null && (modelTypes == null || modelTypes.get(0) === 10)
            if (interactions != null) isInteractive = true
        }

        if (removeClipping) {
            solid = false
            impenetrable = false
        }

        if (supportItems == -1) {
            supportItems = if (solid) 1 else 0
        }
    }


    var lowMemory = false
    lateinit var stream: Buffer
    lateinit var streamIndices: IntArray
    lateinit var clientInstance: Client
    var cacheIndex = 0
    var models: ReferenceCache = ReferenceCache(30)
    lateinit var cache: Array<ObjectDefinition?>
    var baseModels: ReferenceCache = ReferenceCache(500)
    var obstructsGround = false
    var ambientLighting: Byte = 0
    var translateX = 0
    var name: String = ""
    var scaleZ = 0
    var lightDiffusion = 0
    var objectSizeX = 0
    var translateY = 0
    var minimapFunction = 0
    lateinit var originalModelColors: IntArray
    var scaleX = 0
    var varp = 0
    var inverted = false
    var type = 0
    var impenetrable = false
    var mapscene = 0
    lateinit var childrenIDs: IntArray
    var supportItems = 0
    var objectSizeY = 0
    var contouredGround = false
    var occludes = false
    var removeClipping = false
    var solid = false
    var surroundings = 0
    var delayShading = false
    var scaleY = 0
    lateinit var modelIds: IntArray
    var varbit = 0
    var decorDisplacement = 0
    lateinit var modelTypes: IntArray
    var description: String? = null
    var isInteractive = false
    var castsShadow = false
    var animation = 0
    var translateZ = 0
    lateinit var modifiedModelColors: IntArray
    lateinit var interactions: Array<String?>
    lateinit var originalModelTexture: ShortArray
    lateinit var modifiedModelTexture: ShortArray

}