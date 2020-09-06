import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.MavenBuildStep
import org.junit.Test
import settings.MarcoBehler

class StringTests {

    val project = MarcoBehler

    @Test
    fun projectHasTestConfiguration() {

        project.buildTypes.forEach{bt -> bt.steps.items.forEach { s -> if (s is MavenBuildStep) println(s.goals) }}


        project.buildTypes.forEach {
            bt -> println(bt)
        }
    }
}