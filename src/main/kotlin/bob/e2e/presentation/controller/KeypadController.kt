package bob.e2e.presentation.controller



import bob.e2e.domain.service.KeypadService
import bob.e2e.presentation.dto.KeypadRequestDto
import bob.e2e.presentation.dto.KeypadResponseDto
import bob.e2e.presentation.dto.requestDto

import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import javax.imageio.ImageIO

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestTemplate
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.IOException
import java.util.*

@CrossOrigin(origins = ["http://localhost:3000"])
@RestController
@RequestMapping("/api")
class KeypadController(private val keypadService: KeypadService) {
    private lateinit var keyhash: Map<String, String>

    @GetMapping("/keypad")
    fun getKeypad(response: HttpServletResponse): KeypadResponseDto {

        val m: Map<String, Any> = keypadService.getKeypadData()
        keyhash = keypadService.getKeyHash()
        println(keyhash)

        val dto = KeypadResponseDto(
            image = m["image"] as String,
            uuid = m["uuid"] as String,
            timestamp = m["timestamp"] as String,
            hashes = m["index_hash"] as List<String>,
            keypadhash = m["keypadhash"] as String,
            shuffledIndex = m["shuffledIndex"] as MutableList<Int>
        )

        return dto
    }

    @PostMapping("/input")
    fun postInput(@RequestBody request: KeypadRequestDto): String? {
        val hash = keypadService.getKeyHash()
        val input = request.input
        val uuid = request.uuid
        val timestamp = request.timestamp
        val keypadhash = request.keypadhash
        val ch = keypadService.checkIntegrity(uuid, timestamp, keypadhash)
        if (ch) {
            println(input)
            // RestTemplate 인스턴스 생성
            println(keyhash)


            val dto = requestDto(input, keyhash)



            val restClient = RestClient.create()

            return restClient.post()
                .uri("http://146.56.119.112:8081/auth")
                .accept(MediaType.APPLICATION_JSON)
                .body(dto)
                .retrieve()
                .body(String::class.java)
        }

        return null
    }
}
