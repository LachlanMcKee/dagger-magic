package dagger.magic.mapper

object ByteCodeMapper {
    fun convertClassPathToByteCode(original: String): String {
        return "L${original.replace(".", "/")};"
    }
}