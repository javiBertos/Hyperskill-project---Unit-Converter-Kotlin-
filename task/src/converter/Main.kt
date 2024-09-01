package converter

enum class MeasureUnit(val names: List<String>, val singular: String, val plural: String, val rate: Double) {
    MM(listOf("mm", "millimeter", "millimeters"), "millimeter", "millimeters", 0.001),
    CM(listOf("cm", "centimeter", "centimeters"), "centimeter", "centimeters", 0.01),
    M(listOf("m", "meter", "meters"), "meter", "meters", 1.0),
    KM(listOf("km", "kilometer", "kilometers"), "kilometer", "kilometers", 1000.0),
    IN(listOf("in", "inch", "inches"), "inch", "inches", 0.0254),
    FT(listOf("ft", "foot", "feet"), "foot", "feet", 0.3048),
    YD(listOf("yd", "yard", "yards"), "yard", "yards", 0.9144),
    MI(listOf("mi", "mile", "miles"), "mile", "miles", 1609.35),
    MG(listOf("mg", "milligram", "milligrams"), "milligram", "milligrams", 0.001),
    G(listOf("g", "gram", "grams"), "gram", "grams", 1.0),
    KG(listOf("kg", "kilogram", "kilograms"), "kilogram", "kilograms", 1000.0),
    LB(listOf("lb", "pound", "pounds"), "pound", "pounds", 453.592),
    OZ(listOf("oz", "ounce", "ounces"), "ounce", "ounces", 28.3495),
    C(listOf("c", "dc", "celsius", "degree celsius", "degrees celsius",), "degree Celsius", "degrees Celsius", 0.0),
    F(listOf("f", "df", "fahrenheit", "degree fahrenheit", "degrees fahrenheit"), "degree Fahrenheit", "degrees Fahrenheit", 0.0),
    K(listOf("k", "kelvin", "kelvins"), "kelvin", "kelvins", 0.0),
    NULL(emptyList(), "", "", 0.0)
}

class Converter {
    private fun getUnitFromString(input: String): MeasureUnit {
        for (u in MeasureUnit.values()) {
            if (input in u.names) return u
        }

        return MeasureUnit.NULL
    }

    private fun isLengthUnit(unit: MeasureUnit): Boolean {
        return unit in listOf(
            MeasureUnit.MM,
            MeasureUnit.CM,
            MeasureUnit.M,
            MeasureUnit.KM,
            MeasureUnit.IN,
            MeasureUnit.FT,
            MeasureUnit.YD,
            MeasureUnit.MI
        )
    }

    private fun isWeightUnit(unit: MeasureUnit): Boolean {
        return unit in listOf(
            MeasureUnit.MG,
            MeasureUnit.G,
            MeasureUnit.KG,
            MeasureUnit.LB,
            MeasureUnit.OZ
        )
    }

    private fun isTemperatureUnit(unit: MeasureUnit): Boolean {
        return unit in listOf(
            MeasureUnit.C,
            MeasureUnit.F,
            MeasureUnit.K
        )
    }

    fun areOriginAndTargetValid(origin: MeasureUnit, target: MeasureUnit): Boolean {
        return (this.isLengthUnit(origin) && this.isLengthUnit(target)) ||
                (this.isWeightUnit(origin) && this.isWeightUnit(target)) ||
                (this.isTemperatureUnit(origin) && this.isTemperatureUnit(target))
    }

    fun getUnitType(measure: MeasureUnit): String {
        return when {
            this.isLengthUnit(measure) -> "Length"
            this.isWeightUnit(measure) -> "Weight"
            else -> "Temperature"
        }
    }

    fun canBeNegative(origin: MeasureUnit): Boolean {
        return this.isTemperatureUnit(origin)
    }

    private fun convertAroundBase(number: Double, measure: MeasureUnit, toBase: Boolean = true): Double {
        return if (toBase) number * measure.rate else number / measure.rate
    }

    private fun convertTemperature(number: Double, origin: MeasureUnit, target: MeasureUnit): Double {
        return when(origin) {
            MeasureUnit.C -> when(target) {
                MeasureUnit.F -> number * 9/5 + 32
                MeasureUnit.K -> number + 273.15
                else -> number
            }
            MeasureUnit.F -> when(target) {
                MeasureUnit.C -> (number - 32) * 5/9
                MeasureUnit.K -> (number + 459.67) * 5/9
                else -> number
            }
            MeasureUnit.K -> when(target) {
                MeasureUnit.C -> number - 273.15
                MeasureUnit.F -> number * 9/5 - 459.67
                else -> number
            }
            else -> number
        }
    }

    fun performConversion(number: Double, origin: MeasureUnit, target: MeasureUnit): Double {
        if (this.isTemperatureUnit(origin)) {
            return this.convertTemperature(number, origin, target)
        }

        return this.convertAroundBase(this.convertAroundBase(number, origin), target, false)
    }

    fun getNumberFromPrompt(prompt: String): Double {
        val firstArg = prompt.split(" ")[0]

        if (firstArg.toDoubleOrNull() == null) {
            return 0.0
        }

        return firstArg.toDouble()
    }

    fun getUnitFromPrompt(prompt: String, which: String = "origin"): MeasureUnit {
        val args = prompt.split(" ")

        return when(which) {
            "target" -> {
                val targetFirstIndex = if (args[1] == "degree" || args[1] == "degrees") 4 else 3
                this.getUnitFromString(
                    if (args[targetFirstIndex] == "degree" || args[targetFirstIndex] == "degrees") "${args[targetFirstIndex]} ${args[targetFirstIndex + 1]}" else args[targetFirstIndex]
                )
            }
            else -> this.getUnitFromString(
                if (args[1] == "degree" || args[1] == "degrees") "${args[1]} ${args[2]}" else args[1]
            )
        }
    }
}

fun main() {
    val converter = Converter()
    var option = ""

    do {
        println("Enter what you want to convert (or exit): ")
        option = readln().lowercase()

        if (option == "exit") break

        val quantity = converter.getNumberFromPrompt(option)
        if (quantity == 0.0) {
            println("Parse error")

            continue
        }

        val origin = converter.getUnitFromPrompt(option, "origin")
        val target = converter.getUnitFromPrompt(option, "target")

        if (origin == MeasureUnit.NULL || target == MeasureUnit.NULL) {
            println("Conversion from ${
                if (origin == MeasureUnit.NULL) "???" else origin.plural
            } to ${
                if (target == MeasureUnit.NULL) "???" else target.plural
            } is impossible")

            continue
        }

        if (!converter.areOriginAndTargetValid(origin, target)) {
            println("Conversion from ${origin.plural} to ${target.plural} is impossible")

            continue
        }

        if (quantity < 0.0 && !converter.canBeNegative(origin)) {
            println("${converter.getUnitType(origin)} shouldn't be negative")

            continue
        }

        val newUnitValue = converter.performConversion(quantity, origin, target)

        println("$quantity ${
            if (quantity == 1.0) origin.singular else origin.plural
        } is $newUnitValue ${
            if (newUnitValue == 1.0) target.singular else target.plural
        }\n")
    } while (option != "exit")
}
