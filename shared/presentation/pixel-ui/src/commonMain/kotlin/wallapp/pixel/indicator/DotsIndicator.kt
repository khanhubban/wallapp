package wallapp.pixel.indicator

//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


//@Preview(showBackground = false)
@Composable
fun DotsIndicator(
    totalDots: Int = 5,
    selectedIndex: Int = 2,
    selectedColor: Color = Color.DarkGray,
    unSelectedColor: Color = Color.LightGray,
    dotSize: Dp = 8.dp,
    dotSpacing: Dp = 4.dp,
){
    LazyRow(
        modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight()

    ) {
        items(totalDots) { index ->
            if (index == selectedIndex) {
                Box(
                    modifier = Modifier
                        .size(dotSize)
                        .clip(CircleShape)
                        .background(selectedColor),
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(dotSize)
                        .clip(CircleShape)
                        .background(unSelectedColor),
                )
            }

            if (index != totalDots - 1) {
                Spacer(
                    modifier = Modifier.padding(horizontal = dotSpacing),
                )
            }
        }
    }
}
