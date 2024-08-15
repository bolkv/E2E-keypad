package bob.e2e.domain.service


import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.time.Instant
import java.util.*
import javax.imageio.ImageIO

@Service
class KeypadService( ) {


    private lateinit var hash : Map<String,String>
    private val index : MutableList<Int> = (0..11).toMutableList()
    private lateinit var index_hash : MutableList<String>
    private lateinit var imagePaths : MutableList<String>
    private var cachedImage: BufferedImage? = null

    fun makeHash() {
        hash = (0..10).associate {
            val uuid = UUID.randomUUID().toString()
            val timestamp = Instant.now().toEpochMilli().toString()
            it.toString() to "$uuid-$timestamp"
        }
    }

    fun shuffle(){
        index_hash = mutableListOf()
        imagePaths = mutableListOf()

        for(i in  index.shuffled()){
            if( i == 10 || i==11) {
                index_hash.add(hash["10"].toString())
                imagePaths.add("/keypad/_blank.png")
            }
            else {
                hash[i.toString()]?.let { index_hash.add(it) }
                imagePaths.add("/keypad/_${i}.png")
            }

        }
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

    fun getKeypadData(): Pair<String, List<String>> {
        // base64 이미지와 해시 값을 반환
        val image = createImage(3, 4)
        val base64Image = convertToBase64(image)
        return base64Image to index_hash
    }


}

/*
val keypadMap  = keys.associateWith{
key-> if( key
 */