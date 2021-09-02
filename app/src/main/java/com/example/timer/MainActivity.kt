package com.example.timer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF101010))
            ) {
                Timer(
                    totalTime = 100L * 1000L,
                    inActiveStrokeColor = Color.DarkGray,
                    activeStrokeColor = Color(0xFF37B900),
                    modifier = Modifier.size(200.dp)
                )
            }
        }
    }
}


@Composable
fun Timer(
    initialValue: Float = 1f,
    totalTime: Long,
    roundHandleColor: Color = Color.Green,
    inActiveStrokeColor: Color,
    activeStrokeColor: Color,
    strokeWidth: Dp = 5.dp,
    modifier: Modifier = Modifier
) {

    // State for holding value for Timer Text.
    var currentTime by remember {
        mutableStateOf(totalTime)
    }

    // State for holding position of green handle
    var currentValue by remember {
        mutableStateOf(initialValue)
    }

    var isTimerRunning by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = currentTime, key2 = isTimerRunning) {
        // In Running state
        if (currentTime > 0L && isTimerRunning) {
            delay(100L)
            currentTime -= 100L // Minus one second from 100 sec
            currentValue = currentTime / totalTime.toFloat() // Updating the current time
        }
    }

    // Box containing Timer Arc.
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Canvas(
            modifier = modifier,
        ) {
            // Gray Arc
            drawArc(
                color = inActiveStrokeColor,
                startAngle = -215f, // Starting angle to draw an arc.
                sweepAngle = 250f, // Size of an arc that is drawn clockwise relative to startAngle
                useCenter = false,
                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round),
                size = Size(size.width.toFloat(), size.height.toFloat())
            )

            // Green Arc
            drawArc(
                color = activeStrokeColor,
                startAngle = -215f, // Starting angle to draw an arc.
                sweepAngle = 250f * currentValue, // Size of an arc that is drawn clockwise relative to startAngle
                useCenter = false,
                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round),
                size = Size(size.width.toFloat(), size.height.toFloat())
            )


            val radius = size.width / 2f  // Radius.
            val center = Offset(size.width / 2f, size.height / 2f)  // Center of the arc.
            val angle =
                (250f * currentValue + 145f) * (PI / 180f).toFloat() // Calculating angle and converting into radian
            val sideA = cos(angle) * radius // Give the X value of angle.
            val sideB = sin(angle) * radius // Give the Y value of angle.

            // Draw roundHandle using Points.
            drawPoints(
                listOf(Offset(center.x + sideA, center.y + sideB)), // Offset for drawing points.
                pointMode = PointMode.Points,
                color = roundHandleColor,
                strokeWidth = (strokeWidth * 3f).toPx(), // 3 times greater than stroke width of an arc
                cap = StrokeCap.Round
            )

        }

        Text(
            text = (currentTime / 1000L).toString(),
            fontSize = 44.sp,
            color = Color.White,
        )

        Button(
            onClick = {
                if (currentTime > 0L) {
                    isTimerRunning = !isTimerRunning
                } else {
                    currentTime = totalTime
                    isTimerRunning = true
                }
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor =
                // Running
                if (isTimerRunning && currentTime >= 0L) {
                    Color.Red  // Red Button with "Stop" text
                } else {
                    Color.Green // Green Button with "Start" text
                }
            ),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text(
                text =
                // Running State
                if (isTimerRunning && currentTime > 0L) "Stop"
                // Initial state or Pause state
                else if (!isTimerRunning && currentTime >= 0L) "Start"
                // Last state - currentTime reached to 0L
                else "Restart"
            )
        }
    }
}