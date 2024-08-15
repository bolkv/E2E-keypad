package bob.e2e.domain.keypad

import java.util.*

class Hash {
    private var hash : Map<String,String> = mapOf()

    init{
        hash = (0..10).associate{it.toString() to UUID.randomUUID().toString() }
    }

    // 초기화된 hash 맵을 반환하는 메서드
    fun getHash(): Map<String, String> {
        return hash
    }

}