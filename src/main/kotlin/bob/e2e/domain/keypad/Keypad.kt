package bob.e2e.domain.keypad

data class Keypad(
    val id: Long,
    var excpirationTime: Long,
    var message: String,
)