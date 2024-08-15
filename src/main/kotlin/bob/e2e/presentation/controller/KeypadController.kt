package bob.e2e.presentation.controller



import bob.e2e.domain.service.KeypadService
import bob.e2e.presentation.dto.KeypadRequestDto
import bob.e2e.presentation.dto.KeypadResponseDto
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import javax.imageio.ImageIO

@CrossOrigin(origins = ["http://localhost:3000"])
@RestController
@RequestMapping("/api")
class KeypadController(private val keypadService: KeypadService) {


    @GetMapping("/keypad")
    fun getKeypad(response: HttpServletResponse): KeypadResponseDto {
        keypadService.makeHash()
        keypadService.shuffle()
        val (base64Image, hashes) = keypadService.getKeypadData()
        return KeypadResponseDto(image = base64Image, hashes = hashes)

        //response.contentType = "image/png"
        //ImageIO.write(images, "png", response.outputStream)
    }

    @PostMapping("/input")
    fun postInput(@RequestBody request: KeypadRequestDto) {
        val input = request.input
        println("Received input: $input")
        // 입력 값을 처리하거나 데이터베이스에 저장하는 등의 작업을 수행합니다.
    }
}