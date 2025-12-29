package wallapp.pixel.text

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

data class FontSizeRange(
    val min: TextUnit,
    val max: TextUnit,
    val step: TextUnit = DEFAULT_TEXT_STEP,
) {
    constructor(
        min: TextStyle,
        max: TextStyle,
        step: TextUnit = DEFAULT_TEXT_STEP,
    ) : this(
        min = min.fontSize,
        max = max.fontSize,
        step = step,
    )

    init {
        require(min < max) { "Min ($min) must be less than max ($max), $this" }
        require(step.value > 0) { "Step ($step) must be greater than 0, $this" }
    }

    companion object {
        private val DEFAULT_TEXT_STEP = 1.sp
    }
}