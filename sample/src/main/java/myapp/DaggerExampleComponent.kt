package myapp

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DaggerExampleModule::class])
interface DaggerExampleComponent {
    fun inject(application: Application)
}