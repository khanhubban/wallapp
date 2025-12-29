package wallapp.pixel.shape

object ShapeStyleSpecs {
    val Rectangle = ShapeStyleSpec(
        ShapeStyle.Rectangle,
        label = "Sharp",
    )

    val RoundedCorners = ShapeStyleSpec(
        ShapeStyle.RoundedCorners,
        label = "Rounded",
    )

    val CutCorners = ShapeStyleSpec(
        ShapeStyle.CutCorners,
        label = "Cut Corners",
    )

    val All = listOf(
        Rectangle,
        RoundedCorners,
        CutCorners,
    )

    val Map = mapOf<ShapeStyle, ShapeStyleSpec>(
        ShapeStyle.Rectangle to Rectangle,
        ShapeStyle.RoundedCorners to RoundedCorners,
        ShapeStyle.CutCorners to CutCorners,
    )
}
