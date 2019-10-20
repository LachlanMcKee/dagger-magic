package myapp

import java.math.BigDecimal
import javax.inject.Inject

class Application {
    @Inject
    lateinit var boundsClass: BoundClass
    @Inject
    lateinit var bigDecimal: BigDecimal
}