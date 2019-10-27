package myapp

import dagger.Module
import java.math.BigDecimal

@ProvidesSingleton
@Module(includes = [DaggerExampleModule.Inner::class])
object DaggerExampleModule {
    @JvmStatic
    fun provideString(): String = ""

    @JvmStatic
    fun provideRegex(stringDep: String): Regex = Regex(stringDep)

    @JvmStatic
    fun provideBigDecimal(regex: Regex): BigDecimal = BigDecimal(5)

    @BindsSingleton
    @Module
    interface Inner {
        fun bindBoundClass(impl: BoundClassImpl): BoundClass
    }
}
