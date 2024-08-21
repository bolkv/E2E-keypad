package bob.e2e.domain.service


import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.security.MessageDigest
import java.time.Instant
import java.util.*
import javax.imageio.ImageIO

@Service
class KeypadService( ) {


    private lateinit var keypadhash : String
    private lateinit var keyhash: Map<String,String>
    private val index : MutableList<Int> = (0..11).toMutableList()
    private lateinit var index_hash : MutableList<String>
    private lateinit var imagePaths : MutableList<String>
    private var cachedImage: BufferedImage? = null
    val uuid = UUID.randomUUID().toString()
    val timestamp = Instant.now().toEpochMilli().toString()
    val secretkey = "bob"

    fun getKeyHash():Map<String,String>{
        return keyhash
    }

    fun doHash(text: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(text.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    fun makeHash(uuid: String, timestamp: String): String {
        val hashInput = "$uuid-$timestamp-$secretkey"
        keypadhash = doHash(hashInput)

            keyhash = (0..9).associate{ number ->
            val hash = doHash(number.toString())
            number.toString() to hash
        }
        return keypadhash
    }

    fun createImage(rows: Int, cols: Int): BufferedImage {
        if (cachedImage != null) {
            return cachedImage!!
        }
        val images = imagePaths.map { getImageFromClassPath(it)}
        val width = images[0].width
        val height = images[0].height
        val combinedImage = BufferedImage(width * cols, height * rows, BufferedImage.TYPE_INT_ARGB)
        val g: Graphics2D = combinedImage.createGraphics()

        for (i in images.indices) {
            val x = (i % cols) * width
            val y = (i / cols) * height
            g.drawImage(images[i], x, y, null)
        }

        g.dispose()
        cachedImage = combinedImage // 이미지를 캐시에 저장
        return combinedImage
    }

    fun convertToBase64(image: BufferedImage): String {
        val outputStream = ByteArrayOutputStream()
        ImageIO.write(image, "png", outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.getEncoder().encodeToString(byteArray)
    }

    private fun getImageFromClassPath(fileName: String): BufferedImage {
        return try {
            val resource = ClassPathResource(fileName)
            ImageIO.read(resource.inputStream)
        } catch (e: IOException) {
            throw RuntimeException("Failed to load image: $fileName", e)
        }
    }

    fun getPublicKey(): String {
        return try {
            val resource = ClassPathResource("/publicKey/public_key.pem")
            resource.inputStream.bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            throw RuntimeException("Failed to load public key", e)
        }
    }

    fun getKeypadData():  Map<String, Any> {
        keypadhash = makeHash(this.uuid, this.timestamp)
        shuffle()
        val publicKey = getPublicKey()

        val image = createImage(3, 4)
        val base64Image = convertToBase64(image)


        return mapOf(
        "uuid" to uuid,
        "timestamp" to timestamp,
        "keypadhash" to keypadhash,
        "image" to base64Image,
        "index_hash" to index_hash,
        "publicKey" to  publicKey
        )
    }

    fun shuffle(){
        index_hash = mutableListOf()
        imagePaths = mutableListOf()

        for(i in  index.shuffled()){
            if( i == 10 || i==11) {
                index_hash.add("")
                imagePaths.add("/keypad/_blank.png")
            }
            else {
                keyhash[i.toString()]?.let { index_hash.add(it) }
                imagePaths.add("/keypad/_${i}.png")
            }

        }
    }


    fun checkIntegrity(uuid:String, timestamp:String, keypadhash:String): Boolean{
        val checkHash : String = makeHash(uuid, timestamp)
        if(checkHash == keypadhash){
            return true
        }

        else return false
    }


}

/*
val keypadMap  = keys.associateWith{
key-> if( key
 */