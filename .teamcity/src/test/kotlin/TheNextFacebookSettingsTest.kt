import jetbrains.buildServer.configs.kotlin.v2019_2.Requirement
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.MavenBuildStep
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Condition
import org.junit.jupiter.api.Test

import settings.TheNextFaceBook
import java.util.function.Predicate

class TheNextFacebookSettingsTest {

    val project = TheNextFaceBook

    @Test
    fun projectHasTestConfiguration() {
        val mavenTestConfiguration = project.buildTypes.flatMap { bt -> bt.steps.items }.find { step ->
            step is MavenBuildStep && step.goals!!.contains("test")
        }
        assertThat(mavenTestConfiguration).isNotNull();
    }

    @Test
    fun buildConfigsRequireJava11() {
        val java11Requirement = Condition<Requirement>(Predicate { requirement ->
            (requirement.param.equals("teamcity.agent.jvm.version") &&
            requirement.value.equals("11")) }, "TeamCity Java Agent Version")

        project.buildTypes.forEach { buildType ->
            assertThat(buildType.requirements.items).`as`("'${buildType.id}' should contain a Java 11 requirement")
                    .areAtLeast(1, java11Requirement)
        }
    }
}