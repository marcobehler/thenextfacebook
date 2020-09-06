import junit.framework.Assert.assertFalse
import org.junit.Test
import settings.MarcoBehler

class StringTests {

    val project = MarcoBehler

    @Test
    fun projectHasTestConfiguration() {
        project.buildTypes.forEach {
            bt -> println(bt)
        }
    }
}