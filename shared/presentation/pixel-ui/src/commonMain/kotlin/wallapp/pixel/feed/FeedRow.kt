package wallapp.pixel.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import wallapp.pixel.render.Render
import wallapp.pixel.render.RenderViewId
import wallapp.pixel.render.RenderViewIdFactory
import wallapp.pixel.spacer.SpacerViewState
import wallapp.pixel.util.DpOptional
import wallapp.pixel.util.ParallaxAlignmentVertical
import wallapp.pixel.view.View
import wallapp.pixel.view.ViewAlignment


/**
 * Short-lived helper class to break feed items into rows. See [formatToRows].
 */
@Immutable
data class FeedRow(
    val renderViewId: RenderViewId?,
    val views: List<View>,
    val paddingHorizontal: Dp,
) {
    constructor(renderViewId: RenderViewId?, view: View, horizontalPadding: Dp)
            : this(renderViewId, listOf(view), horizontalPadding)

    init {
//        if (views.size > 1) {
//            requireNotNull(viewId) { "FeedRow with multiple items must have viewId ${views.joinToString { it::class.simpleName.toString() }}" }
//        }
    }
}

val FeedRow.gridSpan: (LazyGridItemSpanScope.() -> GridItemSpan)
    get() = { GridItemSpan(maxCurrentLineSpan) }
fun FeedRow.gridItemSpan(maxCurrentLineSpan: Int) = GridItemSpan(maxCurrentLineSpan)

fun List<View>.formatToRows(
    renderViewIdFactory: RenderViewIdFactory,
    feedViewSpec: FeedViewSpec,
): List<FeedRow> {
    val result = mutableListOf<FeedRow>()
    val currentRow = mutableListOf<View>()

    val stretchDanglingNonMaxSpanItems = feedViewSpec.stretchDanglingNonMaxSpanItems
    val defaultPaddingHorizontal = feedViewSpec.paddingHorizontal

    val horizontalSpacer = View(SpacerViewState(height = DpOptional(feedViewSpec.itemSpacingVertical)))

    fun FeedRow(views: List<View>, paddingHorizontal: Dp): FeedRow =
        FeedRow(renderViewIdFactory.getRenderViewId(views, index = result.size), views, paddingHorizontal)
    fun FeedRow(view: View, paddingHorizontal: Dp): FeedRow =
        FeedRow(renderViewIdFactory.getRenderViewId(view, index = result.size), view, paddingHorizontal)

    fun finalizeCurrentRow() {
        if (currentRow.isNotEmpty()) {
            val currentRowList = currentRow.toList()
            val feedRow = if (!stretchDanglingNonMaxSpanItems
                && currentRowList.size == 1) {
                FeedRow(currentRowList + horizontalSpacer, defaultPaddingHorizontal)
            } else {
                FeedRow(currentRowList, defaultPaddingHorizontal)
            }
            result.add(feedRow)
        }
    }

    forEach { view ->
        if (view.isMaxItemSpan) {
            finalizeCurrentRow()

            result.add(
                FeedRow(
                    view,
                    if (view.useZeroFeedPadding) { 0.dp } else { defaultPaddingHorizontal },
                )
            )
            currentRow.clear()
        } else {
            currentRow.add(view)
            if (currentRow.size == 2) {
                result.add(FeedRow(currentRow.toList(), defaultPaddingHorizontal))
                currentRow.clear()
            }
        }
    }

    finalizeCurrentRow()

    return result
}

@Composable
private inline fun FeedRowAlignment(
    render: Render,
    view: View,
    rowRenderViewId: RenderViewId?,
    lazyGridState: LazyGridState,
): Alignment {
    if (rowRenderViewId == null) return Alignment.Center

    return when (val viewAlignment: ViewAlignment = render.viewAlignmentMapper.map(view)) {
        is ViewAlignment.ParallaxVertical -> {
            remember {
                ParallaxAlignmentVertical(
                    lazyGridState = lazyGridState,
                    key = rowRenderViewId,
                    maxParallaxScale = viewAlignment.maxScale,
                )
            }
        }
        else -> Alignment.Center
    }
}

@Composable
fun FeedRow(
    render: Render,
    view: View,
    rowViewId: RenderViewId?,
    lazyGridState: LazyGridState,
    modifier: Modifier = Modifier,
) {
    View(
        render,
        view,
        modifier,
        alignment = FeedRowAlignment(render, view, rowViewId, lazyGridState),
    )
}

@Composable
fun FeedRow(
    render: Render,
    feedViewSpec: FeedViewSpec,
    views: List<View>,
    rowRenderViewId: RenderViewId?,
    lazyGridState: LazyGridState,
    modifier: Modifier = Modifier,
) {
    val spacingHorizontal = feedViewSpec.itemSpacingHorizontal

    if (views.isEmpty()) return

    Row(
        horizontalArrangement = Arrangement.spacedBy(spacingHorizontal),
        modifier = modifier,
    ) {
        views.forEach { view ->
            View(
                render,
                view,
                modifier = Modifier.weight(1f),
                alignment = FeedRowAlignment(render, view, rowRenderViewId, lazyGridState),
            )
        }
    }
}

@Composable
fun FeedRow(
    render: Render,
    feedViewSpec: FeedViewSpec,
    feedRow: FeedRow,
    lazyGridState: LazyGridState,
) {
    val modifier = Modifier.padding(horizontal = feedRow.paddingHorizontal)

    if (feedRow.views.size == 1) {
        FeedRow(
            render,
            view = feedRow.views[0],
            rowViewId = feedRow.renderViewId,
            lazyGridState,
            modifier,
        )
    } else {
        FeedRow(
            render,
            feedViewSpec,
            views = feedRow.views,
            rowRenderViewId = feedRow.renderViewId,
            lazyGridState,
            modifier,
        )
    }
}
