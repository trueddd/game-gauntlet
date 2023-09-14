#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")
package ${PACKAGE_NAME}
#end

import com.github.trueddd.EventGateTest
import org.junit.jupiter.api.Test
import kotlinx.coroutines.test.runTest
import kotlin.test.assertTrue

class ${NAME}ItemTest : EventGateTest() {
    
    @Test
    fun `basic test`() = runTest {
        val user = requireRandomParticipant()
        assertTrue(false)
    }
}
