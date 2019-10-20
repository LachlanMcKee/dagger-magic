package dagger.magic.mapper

import dagger.magic.model.Replacement
import dagger.magic.model.Replacements
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ReplacementsMapperTest {
    @Test
    fun test() {
        val input = listOf(
                "myapp.ProvidesSingleton",
                "javax.inject.Singleton",
                "myapp.ProvidesFeatureScope",
                "myapp.FeatureScope"
        )

        val output = ReplacementsMapper.map(input)

        Assertions.assertEquals(Replacements(listOf(
                Replacement("Lmyapp/ProvidesSingleton;", "Ljavax/inject/Singleton;"),
                Replacement("Lmyapp/ProvidesFeatureScope;", "Lmyapp/FeatureScope;")
        )), output)
    }
}