package dagger.magic.model

data class Replacements(val values: List<Replacement>) {
    operator fun get(original: String?) = values.find { it.original == original }
}

data class Replacement(val original: String, val replacement: String)
