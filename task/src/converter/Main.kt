package converter

fun main() {
    println("Enter a number and a measure of length: ")
    val (numberStr, measure) = readln().lowercase().split(" ")
    val number = numberStr.toDouble()
    var result = 0.0
    var measureStr = ""

    when (measure) {
        "mm", "millimeter", "millimeters" -> {
            result = number * 0.001
            measureStr = if (number == 1.0) "millimeter" else "millimeters"
        }
        "cm", "centimeter", "centimeters" -> {
            result = number * 0.01
            measureStr = if (number == 1.0) "centimeter" else "centimeters"
        }
        "m", "meter", "meters" -> {
            result = number
            measureStr = if (number == 1.0) "meter" else "meters"
        }
        "km", "kilometer", "kilometers" -> {
            result = number * 1000
            measureStr = if (number == 1.0) "kilometer" else "kilometers"
        }
        "in", "inch", "inches" -> {
            result = number * 0.0254
            measureStr = if (number == 1.0) "inch" else "inches"
        }
        "ft", "foot", "feet" -> {
            result = number * 0.3048
            measureStr = if (number == 1.0) "foot" else "feet"
        }
        "yd", "yard", "yards" -> {
            result = number * 0.9144
            measureStr = if (number == 1.0) "yard" else "yards"
        }
        "mi", "mile", "miles" -> {
            result = number * 1609.35
            measureStr = if (number == 1.0) "mile" else "miles"
        }
        else -> {
            println("Wrong input. Unknown unit $measure")
            return
        }
    }

    println("$number $measureStr is $result ${if (result == 1.0) "meter" else "meters"}")
}
