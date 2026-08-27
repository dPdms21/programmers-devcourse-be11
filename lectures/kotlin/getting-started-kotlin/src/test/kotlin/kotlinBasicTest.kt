import org.junit.jupiter.api.Test

class kotlinBasicTest {

    @Test
    fun valTest() {
        val name = "Java"  // val: 재할당 불가능 변수

        println(name)
    }

    @Test
    fun varTest() {
        var name = "java" // var: 재할당 가능 변수
        name = "Kotlin"

        println(name)
    }

    // collection
    @Test
    fun immutableList() {
        val fruits: List<String> = listOf("Apple", "Banana", "Pineapple")

    }

    @Test
    fun mutableList() {
        val fruits: MutableList<String> = mutableListOf("Apple", "Banana", "Pineapple")

        fruits.add("Mango") // MutableList<String>
        fruits.remove("Apple") // MutableList<String>
    }

    private fun convertNumberToString(number: Int) =
        when {
            number < 0 -> "Negative"
            number == 0 -> "Zero"
            number > 0 -> "Positive"
            else -> "Wrong Number"
        }

    @Test
    fun classTest() {
        val person = Person(
            name = "James",
            address = "Seoul",
            age = 15
        )
        println(person.name)
        println(person.age)
    }

    class Person(
        val name: String, // property
        val address: String,
        val age: Int
    )
}