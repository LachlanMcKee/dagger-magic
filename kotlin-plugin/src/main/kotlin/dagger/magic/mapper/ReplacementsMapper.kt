package dagger.magic.mapper

import dagger.magic.model.Replacement
import dagger.magic.model.Replacements

object ReplacementsMapper {
    fun map(original: List<String>): Replacements {
        return original
                .map(ByteCodeMapper::convertClassPathToByteCode)
                .windowed(WINDOW_SIZE, WINDOW_STEP)
                .map { Replacement(it[0], it[1]) }
                .let(::Replacements)
    }

    private const val WINDOW_SIZE = 2
    private const val WINDOW_STEP = 2
}
