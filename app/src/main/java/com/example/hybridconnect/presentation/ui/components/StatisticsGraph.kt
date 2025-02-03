package com.example.hybridconnect.presentation.ui.components

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hybridconnect.domain.model.AgentCommission
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.PopupProperties
import ir.ehsannarmani.compose_charts.models.StrokeStyle
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun StatisticsGraph(
    modifier: Modifier = Modifier,
    agentCommissions: List<AgentCommission>,
) {
    val totalCommission = remember(agentCommissions) {
        agentCommissions.sumOf { it.amount }
    }

    val values = agentCommissions.map { it.amount }

    val dayNames = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val currentWeekDates = getCurrentWeekDates()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("This week's commission (Ksh. %.2f)".format(totalCommission))
                }
            },
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(bottom = 20.dp)
        )

        LineChart(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 16.dp, bottom = 6.dp)
                .offset(y = (-14).dp),
            minValue = 0.0,
            maxValue = values.maxOrNull() ?: 0.0,
            indicatorProperties = HorizontalIndicatorProperties(
                enabled = true,
                textStyle = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 8.sp
                ),
                padding = 4.dp,
                contentBuilder = { value ->
                    "%.2f".format(value)
                }
            ),
            popupProperties = PopupProperties(
                enabled = true,
                mode = PopupProperties.Mode.PointMode(),
                animationSpec = tween(300),
                duration = 5000L,
                textStyle = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                cornerRadius = 8.dp,
                contentHorizontalPadding = 4.dp,
                contentVerticalPadding = 2.dp,
                contentBuilder = { value ->
                    "Ksh. %.2f".format(value)
                }
            ),
            data = remember(values) {
                listOf(
                    Line(
                        label = "",
                        curvedEdges = true,
                        dotProperties = DotProperties(
                            enabled = true,
                            radius = 2.dp,
                            color = SolidColor(Color.Red),
                            strokeWidth = 3.dp,
                            strokeStyle = StrokeStyle.Normal,
                            animationEnabled = false,
                        ),
                        values = values,
                        color = SolidColor(Color(0xFF009544)),
                        firstGradientFillColor = Color(0xFF009544).copy(alpha = .5f),
                        secondGradientFillColor = Color.Transparent,
                        strokeAnimationSpec = tween(0, easing = EaseInOutCubic),
                        gradientAnimationDelay = 0,
                        drawStyle = DrawStyle.Stroke(width = 2.dp),
                    )
                )
            },
            gridProperties = GridProperties(
                enabled = true,
                yAxisProperties = GridProperties.AxisProperties(
                    enabled = false
                ),
                xAxisProperties = GridProperties.AxisProperties(
                    thickness = .2.dp,
                    style = StrokeStyle.Dashed()
                )
            ),
            animationDelay = 0,
            animationMode = AnimationMode.Together(delayBuilder = {
                it * 1500L
            }),
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .padding(start = 12.dp, end = 8.dp, bottom = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            items(dayNames.zip(currentWeekDates)) { (dayName, date) ->
                Text(
                    text = "$dayName - $date",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Start,
                        fontSize = 8.sp
                    ),
                )
            }
        }
    }
}

private fun getCurrentWeekDates(): List<String> {
    val today = LocalDate.now()
    val startOfWeek = today.minusDays(today.dayOfWeek.value.toLong() % 7)
    val formatter = DateTimeFormatter.ofPattern("dd")

    return (0..6).map { startOfWeek.plusDays(it.toLong()).format(formatter) }
}

@Preview(showBackground = true)
@Composable
private fun StatisticsGraphPreview() {
    val agentCommissions = listOf(
        AgentCommission("2024-12-20", 100.50),
        AgentCommission("2024-12-21", 130.50),
        AgentCommission("2024-12-22", 300.50),
        AgentCommission("2024-12-23", 230.50),
        AgentCommission("2024-12-24", 470.50),
    )

    StatisticsGraph(
        modifier = Modifier.height(250.dp),
        agentCommissions = agentCommissions
    )
}