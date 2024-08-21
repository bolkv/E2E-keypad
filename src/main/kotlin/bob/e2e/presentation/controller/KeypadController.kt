package bob.e2e.presentation.controller



import bob.e2e.domain.service.KeypadService
import bob.e2e.presentation.dto.KeypadRequestDto
import bob.e2e.presentation.dto.KeypadResponseDto

import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import javax.imageio.ImageIO

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate

@CrossOrigin(origins = ["http://localhost:3000"])
@RestController
@RequestMapping("/api")
class KeypadController(private val keypadService: KeypadService) {


    @GetMapping("/keypad")
    fun getKeypad(response: HttpServletResponse): KeypadResponseDto {
        val m: Map<String, Any> = keypadService.getKeypadData()

        val dto = KeypadResponseDto(
            image = m["image"] as String,
            uuid = m["uuid"] as String,
            timestamp = m["timestamp"] as String,
            hashes = m["index_hash"] as List<String>,
            keypadhash = m["keypadhash"] as String,
            publicKey = m["publicKey"] as String
        )

        return dto
        //response.contentType = "image/png"
        //ImageIO.write(images, "png", response.outputStream)
    }

    @PostMapping("/input")
    fun postInput(@RequestBody request: KeypadRequestDto) {
        val hash = keypadService.getKeyHash()
        val input = request.input
        val uuid = request.uuid
        val timestamp = request.timestamp
        val keypadhash = request.keypadhash
        val ch = keypadService.checkIntegrity(uuid, timestamp, keypadhash)
        if (ch) {
            println(input)
            // RestTemplate 인스턴스 생성
            val restTemplate = RestTemplate()

            // 요청 헤더 설정
            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_JSON

            // 요청 바디 설정
            val requestBody = mapOf(
                "userInput" to input,
                "keyHashMap" to hash,
            )

            // HTTP 요청 생성
            val entity = HttpEntity(requestBody, headers)

            // POST 요청 보내기
            val response = restTemplate.exchange(
                "http://146.56.119.112:8081/auth",
                HttpMethod.POST,
                entity,
                String::class.java
            )

            // 응답 출력
            println("Response from auth server: ${response.body}")
        } else {
            println("Integrity check failed.")
        }


        }
    }
}