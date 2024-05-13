#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")
package ${PACKAGE_NAME}
#end

import com.github.trueddd.EventGateTest
import kotlinx.coroutines.test.runTest
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

class ${NAME}ItemTest : EventGateTest() {
    
    @Test
    fun `basic test`() = runTest {
        val user = requireRandomParticipant()
        assertTrue(false)
    }
}
