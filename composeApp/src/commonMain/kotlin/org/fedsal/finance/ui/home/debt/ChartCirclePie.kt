import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.fedsal.finance.domain.models.Debt
import org.fedsal.finance.ui.common.composables.CategoryIcon
import org.fedsal.finance.ui.common.formatDecimal
import org.fedsal.finance.ui.common.getIcon
import org.fedsal.finance.ui.common.hexToColor

@Composable
fun PieChart(
    data: List<Debt>,
    radiusOuter: Dp = 140.dp,
    chartBarWidth: Dp = 20.dp,
    animDuration: Int = 1000,
) {

    val totalSum = data.sumOf { it.amount }
    val floatValue = mutableListOf<Float>()

    // To set the value of each Arc according to
    // the value given in the data, we have used a simple formula.
    // For a detailed explanation check out the Medium Article.
    // The link is in the about section and readme file of this GitHub Repository
    data.forEachIndexed { index, values ->
        floatValue.add(index, 360 * values.amount.toFloat() / totalSum.toFloat())
    }

    var animationPlayed by remember { mutableStateOf(false) }

    var lastValue = 0f

    // it is the diameter value of the Pie
    val animateSize by animateFloatAsState(
        targetValue = if (animationPlayed) radiusOuter.value * 2f else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = 0,
            easing = LinearOutSlowInEasing
        )
    )

    // if you want to stabilize the Pie Chart you can use value -90f
    // 90f is used to complete 1/4 of the rotation
    val animateRotation by animateFloatAsState(
        targetValue = if (animationPlayed) 90f * 11f else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = 0,
            easing = LinearOutSlowInEasing
        )
    )

    // to play the animation only once when the function is Created or Recomposed
    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Pie Chart using Canvas Arc
        Box(
            modifier = Modifier.size(animateSize.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .size(radiusOuter * 2f)
                    .rotate(animateRotation)
            ) {
                // draw each Arc for each data entry in Pie Chart
                floatValue.forEachIndexed { index, value ->
                    drawArc(
                        color = hexToColor(data[index].paymentMethod.color),
                        lastValue,
                        value,
                        useCenter = false,
                        style = Stroke(chartBarWidth.toPx(), cap = StrokeCap.Butt)
                    )
                    lastValue += value
                }
            }
        }

        // To see the data in more structured way
        // Compose Function in which Items are showing data
        DetailsPieChart(data = data)
    }

}

@Composable
fun DetailsPieChart(
    data: List<Debt>
) {
    Column(
        modifier = Modifier
            .padding(top = 48.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // create the data items
        data.forEach {
            DetailsPieChartItem(
                data = it
            )
        }

    }
}

@Composable
fun DetailsPieChartItem(data: Debt) {
    Surface(
        modifier = Modifier.height(84.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CategoryIcon(
                modifier = Modifier.size(50.dp),
                icon = getIcon(data.paymentMethod.iconId),
                iconTint = hexToColor(data.paymentMethod.color)
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.padding(start = 15.dp),
                    text = data.title,
                    fontWeight = FontWeight.Medium,
                    fontSize = 22.sp,
                )
                Text(
                    modifier = Modifier.padding(start = 15.dp),
                    text = "$ ${data.amount.formatDecimal()}",
                    fontWeight = FontWeight.Medium,
                    fontSize = 22.sp,
                )
            }
        }
    }
}
