import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.MavenBuildStep
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertTrue
import org.junit.Test
import settings.TheNextFaceBook

class StringTests {

    val project = TheNextFaceBook

    @Test
    fun projectHasTestStep() {
        var testStep = project.buildTypes.flatMap { bt -> bt.steps.items }.find { step ->
            step is MavenBuildStep && step.goals!!.contains("test")
        }
        assertNotNull(testStep);
    }

    @Test
    fun buildConfigsRequireJava11() {
        val i = project.buildTypes.flatMap { bt -> bt.requirements.items }
        val all = i.all { r -> (r.param.equals("teamcity.agent.jvm.version") && r.value.equals("11")) }
        assertTrue(all)

    }
}