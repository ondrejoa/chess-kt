package model

data class SquareLocation(val x: Int, val y: Int) {
    fun isValid(): Boolean {
        return x >= 0 && y >= 0 && x < 8 && y < 8
    }
}
