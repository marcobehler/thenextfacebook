import jetbrains.buildServer.configs.kotlin.v2019_2.Requirement
import jetbrains.buildServer.configs.kotlin.v2019_2.RequirementType
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.MavenBuildStep
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SettingsTest {

    val project = TheNextFaceBook

    @Test
    internal fun haveBuildConfigurationsJava11Requirement() {

        val java11Requirement = Requirement(RequirementType.NO_LESS_THAN_VER,
                "teamcity.agent.jvm.version", "11", null)

        project.buildTypes.forEach { bt ->
            assertThat(bt.requirements.items)
                    .`as`("'${bt.id}' should have a Java 11 requirement")
                    .contains(java11Requirement)
        }

        // project.buildTypes.first().steps
    }


    @Test
    internal fun projectHasTestConfiguration() {
        val mavenTestConfiguration = project.buildTypes.flatMap { bt -> bt.steps.items }.find { step ->
            step is MavenBuildStep && step.goals!!.equals("test") // mvn test
        }
        assertThat(mavenTestConfiguration).isNotNull();
    }

}