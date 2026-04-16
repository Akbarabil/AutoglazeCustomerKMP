package com.example.autoglazecustomer.data.local

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ShowcaseItem(
    val title: String,
    val description: String
)

val LocalShowcaseState = staticCompositionLocalOf<ShowcaseState> {
    error("ShowcaseState belum di-provide!")
}

class ShowcaseState {
    var isVisible by mutableStateOf(false)
    var currentStep by mutableStateOf(0)
    val targets = mutableStateMapOf<Int, Rect>()

    private var onDismissAction: (() -> Unit)? = null

    fun start(onDismiss: () -> Unit = {}) {
        onDismissAction = onDismiss
        currentStep = 0
        isVisible = true
    }

    fun next(totalSteps: Int) {
        if (currentStep < totalSteps - 1) {
            currentStep++
        } else {
            isVisible = false
            onDismissAction?.invoke()
        }
    }

    fun skip() {
        isVisible = false
        onDismissAction?.invoke()
    }

    fun registerTarget(index: Int, bounds: Rect) {
        targets[index] = bounds
    }
}

@Composable
fun rememberShowcaseState(): ShowcaseState {
    return remember { ShowcaseState() }
}

fun Modifier.showcaseTarget(index: Int, state: ShowcaseState): Modifier =
    this.onGloballyPositioned { coordinates ->
        state.registerTarget(index, coordinates.boundsInRoot())
    }

@Composable
fun ShowcaseOverlay(
    state: ShowcaseState,
    items: List<ShowcaseItem>
) {
    if (!state.isVisible || items.isEmpty()) return

    val currentItem = items.getOrNull(state.currentStep) ?: return
    val targetBounds = state.targets[state.currentStep]

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { alpha = 0.99f }
    ) {
        if (targetBounds != null) {
            val animatedLeft by animateFloatAsState(targetValue = targetBounds.left, animationSpec = tween(400, easing = FastOutSlowInEasing), label = "left")
            val animatedTop by animateFloatAsState(targetValue = targetBounds.top, animationSpec = tween(400, easing = FastOutSlowInEasing), label = "top")
            val animatedRight by animateFloatAsState(targetValue = targetBounds.right, animationSpec = tween(400, easing = FastOutSlowInEasing), label = "right")
            val animatedBottom by animateFloatAsState(targetValue = targetBounds.bottom, animationSpec = tween(400, easing = FastOutSlowInEasing), label = "bottom")

            val isBottomNavTarget = state.currentStep >= 3

            Canvas(modifier = Modifier.fillMaxSize()) {
                val paddingX = if (isBottomNavTarget) 24.dp.toPx() else 16.dp.toPx()
                val paddingTop = 16.dp.toPx()
                val paddingBottom = if (isBottomNavTarget) 42.dp.toPx() else 16.dp.toPx()

                drawRect(color = Color.Black.copy(alpha = 0.75f))

                val path = Path().apply {
                    addRoundRect(
                        RoundRect(
                            left = animatedLeft - paddingX,
                            top = animatedTop - paddingTop,
                            right = animatedRight + paddingX,
                            bottom = animatedBottom + paddingBottom,
                            cornerRadius = CornerRadius(24.dp.toPx(), 24.dp.toPx())
                        )
                    )
                }
                drawPath(path = path, color = Color.Transparent, blendMode = BlendMode.Clear)
            }

            val verticalBias by animateFloatAsState(
                targetValue = if (isBottomNavTarget) 0f else 1f,
                animationSpec = tween(400, easing = FastOutSlowInEasing),
                label = "bias"
            )

            val dynamicBottomPadding by animateDpAsState(
                targetValue = if (isBottomNavTarget) 24.dp else 96.dp,
                animationSpec = tween(400, easing = FastOutSlowInEasing),
                label = "paddingBottom"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 24.dp, end = 24.dp, top = 72.dp, bottom = dynamicBottomPadding),
                contentAlignment = BiasAlignment(horizontalBias = 0f, verticalBias = verticalBias)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = currentItem.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD53B1E)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currentItem.description,
                            fontSize = 14.sp,
                            color = Color.DarkGray,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = { state.skip() }) {
                                Text("Lewati", color = Color.Gray)
                            }

                            Button(
                                onClick = { state.next(items.size) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD53B1E)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(if (state.currentStep == items.size - 1) "Selesai" else "Lanjut")
                            }
                        }
                    }
                }
            }
        }
    }
}