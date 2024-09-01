package converter

fun main() {
    println("Enter a number and a measure: ")
    val (numberStr, measure) = readln().lowercase().split(" ")
    val numberInt = numberStr.toInt()

    if (
        measure != "km" &&
        measure != "kilometers" &&
        measure != "kilometer"
    ) {
        println("Wrong input")
        return
    }

    println("$numberInt kilometer${if (numberInt > 1) 's' else ""} is ${numberInt.toInt() * 1000} meters")
}
