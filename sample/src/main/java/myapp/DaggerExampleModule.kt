package myapp

@ProvidesSingleton
@ModuleWithStaticMethods
object DaggerExampleModule {
    fun blah(): BoundClass = BoundClassImpl()
    fun blah2(): BoundClass2 = BoundClass2Impl()
    fun blah3(): BoundClass2 = BoundClass2Impl()
    fun blah4(): BoundClass2 = BoundClass2Impl()
}

@ModuleWithStaticMethods
abstract class ZAbstractDaggerExampleModule {
    @ProvidesSingleton
    fun blah(): BoundClass {
        return BoundClassImpl()
    }

    @ProvidesSingleton
    fun blah2(): BoundClass2 {
        return BoundClass2Impl()
    }

    @BindsSingleton
    abstract fun blah3(impl: BoundClass2Impl): BoundClass2
}