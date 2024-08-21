package bob.e2e.presentation.dto

data class KeypadRequestDto(
    val uuid: String,
    val timestamp: String,
    val keypadhash: String,
    val input: String,
)
