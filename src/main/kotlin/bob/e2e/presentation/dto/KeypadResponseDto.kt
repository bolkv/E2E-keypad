package bob.e2e.presentation.dto

data class KeypadResponseDto(
    val image: String,
    val timestamp:String,
    val uuid: String,
    val hashes: List<String>,
    val keypadhash: String,
    val shuffledIndex:  MutableList<Int>,
)
