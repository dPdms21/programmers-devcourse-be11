import org.junit.jupiter.api.Test

class keyFeatureTest {

    /*
    * null safety
     */
    @Test
    fun notNullType() {
        val name: String = "test"
    }

    @Test
    fun nullableType() {
        val name: String? = null
    }

    @Test
    fun safeCall() {
        val nullableName: String? = "kotlin"

        println(nullableName?.length)

        val name: String = "java"

        println(name.length)
    }

    @Test
    fun elvisOperator() {
        val language: String? = null

        val length = language?.length ?: 10

        println(length)
    }

    /*
    * concise
     */
    @Test
    fun functionTest() {
        val res = sumV1(1,3)
        println(res)
    }

    // statement
    private fun sumV1(num1: Int, num2: Int): Int {
        return num1 + num2
    }

    // expression
    private fun sumV2(num1: Int, num2: Int): Int = num1 + num2

    // void
    fun process() {
        // 저장
        // 외부 api 호출
    }
}