package converter

enum class MeasureUnit(val sort: String, val long: String, val plural: String, val rate: Double) {
    MM("mm", "millimeter", "millimeters", 0.001),
    CM("cm", "centimeter", "centimeters", 0.01),
    M("m", "meter", "meters", 1.0),
    KM("km", "kilometer", "kilometers", 1000.0),
    IN("in", "inch", "inches", 0.0254),
    FT("ft", "foot", "feet", 0.3048),
    YD("yd", "yard", "yards", 0.9144),
    MI("mi", "mile", "miles", 1609.35),
    MG("mg", "milligram", "milligrams", 0.001),
    G("g", "gram", "grams", 1.0),
    KG("kg", "kilogram", "kilograms", 1000.0),
    LB("lb", "pound", "pounds", 453.592),
    OZ("oz", "ounce", "ounces", 28.3495),
    NULL("", "", "", 0.0)
}

class Converter {
    fun getUnitFromInput(input: String): MeasureUnit {
        return when(input) {
            "mm", "millimeter", "millimeters" -> MeasureUnit.MM
            "cm", "centimeter", "centimeters" -> MeasureUnit.CM
            "m", "meter", "meters" -> MeasureUnit.M
            "km", "kilometer", "kilometers" -> MeasureUnit.KM
            "in", "inch", "inches" -> MeasureUnit.IN
            "ft", "foot", "feet" -> MeasureUnit.FT
            "yd", "yard", "yards" -> MeasureUnit.YD
            "mi", "mile", "miles" -> MeasureUnit.MI
            "mg", "milligram", "milligrams" -> MeasureUnit.MG
            "g", "gram", "grams" -> MeasureUnit.G
            "kg", "kilogram", "kilograms" -> MeasureUnit.KG
            "lb", "pound", "pounds" -> MeasureUnit.LB
            "oz", "ounce", "ounces" -> MeasureUnit.OZ
            else -> MeasureUnit.NULL
        }
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

    fun areOriginAndTargetValid(origin: MeasureUnit, target: MeasureUnit): Boolean {
        return (this.isLengthUnit(origin) && this.isLengthUnit(target)) ||
                (this.isWeightUnit(origin) && this.isWeightUnit(target))
    }

    private fun convertAroundBase(number: Double, measure: MeasureUnit, toBase: Boolean = true): Double {
        return if (toBase) number * measure.rate else number / measure.rate
    }

    fun performConversion(number: Double, origin: MeasureUnit, target: MeasureUnit): Double {
        return this.convertAroundBase(this.convertAroundBase(number, origin), target, false)
    }
}

fun main() {
    val converter = Converter()
    var option = ""

    do {
        println("Enter what you want to convert (or exit): ")
        option = readln().lowercase()

        if (option == "exit") break

        val (amountStr, originStr, transition, targetStr) = option.split(" ")
        val amount = amountStr.toDouble()
        val origin = converter.getUnitFromInput(originStr)
        val target = converter.getUnitFromInput(targetStr)

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

        val newUnitValue = converter.performConversion(amount, origin, target)

        println("$amount ${
            if (amount == 1.0) origin.long else origin.plural
        } is $newUnitValue ${
            if (newUnitValue == 1.0) target.long else target.plural
        }\n")
    } while (option != "exit")
}
