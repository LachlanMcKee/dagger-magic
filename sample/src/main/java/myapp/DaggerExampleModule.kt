package myapp

import dagger.Module
import java.math.BigDecimal

@ProvidesSingleton
@Module(includes = [DaggerExampleModule.Inner::class])
object DaggerExampleModule {
    @JvmStatic
    fun provideString(): String = ""

    @JvmStatic
    fun provideRegex(stringDep: String): Regex = regexBuilder(stringDep)

    @JvmStatic
    fun provideBigDecimal(regex: Regex): BigDecimal = BigDecimal(5)

    private fun regexBuilder(stringDep: String): Regex {
        return Regex(stringDep)
    }

    @BindsSingleton
    @Module
    interface Inner {
        fun bindBoundClass(impl: BoundClassImpl): BoundClass
    }
}
