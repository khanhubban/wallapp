/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package wallapp.unit


interface Alignment {

    interface Horizontal

    interface Vertical

    /**
     * A collection of common [Alignment]s aware of layout direction.
     */
    companion object {
        // 2D Alignments.
            val TopStart: Alignment = BiasAlignment(-1f, -1f)
            val TopCenter: Alignment = BiasAlignment(0f, -1f)
            val TopEnd: Alignment = BiasAlignment(1f, -1f)
            val CenterStart: Alignment = BiasAlignment(-1f, 0f)
            val Center: Alignment = BiasAlignment(0f, 0f)
            val CenterEnd: Alignment = BiasAlignment(1f, 0f)
            val BottomStart: Alignment = BiasAlignment(-1f, 1f)
            val BottomCenter: Alignment = BiasAlignment(0f, 1f)
            val BottomEnd: Alignment = BiasAlignment(1f, 1f)

        // 1D Alignment.Verticals.
            val Top: Vertical = BiasAlignment.Vertical(-1f)
            val CenterVertically: Vertical = BiasAlignment.Vertical(0f)
            val Bottom: Vertical = BiasAlignment.Vertical(1f)

        // 1D Alignment.Horizontals.
            val Start: Horizontal = BiasAlignment.Horizontal(-1f)
            val CenterHorizontally: Horizontal = BiasAlignment.Horizontal(0f)
            val End: Horizontal = BiasAlignment.Horizontal(1f)
    }
}

/**
 * A collection of common [Alignment]s unaware of the layout direction.
 */
object AbsoluteAlignment {
    // 2D AbsoluteAlignments.
    val TopLeft: Alignment = BiasAbsoluteAlignment(-1f, -1f)
    val TopRight: Alignment = BiasAbsoluteAlignment(1f, -1f)
    val CenterLeft: Alignment = BiasAbsoluteAlignment(-1f, 0f)
    val CenterRight: Alignment = BiasAbsoluteAlignment(1f, 0f)
    val BottomLeft: Alignment = BiasAbsoluteAlignment(-1f, 1f)
    val BottomRight: Alignment = BiasAbsoluteAlignment(1f, 1f)

    // 1D BiasAbsoluteAlignment.Horizontals.
    val Left: Alignment.Horizontal = BiasAbsoluteAlignment.Horizontal(-1f)
    val Right: Alignment.Horizontal = BiasAbsoluteAlignment.Horizontal(1f)
}

data class BiasAlignment(
    val horizontalBias: Float,
    val verticalBias: Float
) : Alignment {

    data class Horizontal(private val bias: Float) : Alignment.Horizontal

    data class Vertical(private val bias: Float) : Alignment.Vertical
}

data class BiasAbsoluteAlignment(
    val horizontalBias: Float,
    val verticalBias: Float
) : Alignment {

    data class Horizontal(private val bias: Float) : Alignment.Horizontal
}
