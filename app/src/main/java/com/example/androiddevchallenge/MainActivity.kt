/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.ui.theme.MyTheme
import com.example.androiddevchallenge.ui.theme.bleu
import com.example.androiddevchallenge.ui.theme.bleuDark
import com.example.androiddevchallenge.ui.theme.bleuLight
import com.example.androiddevchallenge.ui.theme.teal200
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }
}

@Composable
fun GaugeView(isRunning: Boolean, modifier: Modifier = Modifier) {

    val infiniteTransition = rememberInfiniteTransition()
    val extra by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val increaseAngle = remember { mutableStateOf(true) }

    val angle = remember { mutableStateOf(6f) }
    if (angle.value == 360f) {
        increaseAngle.value = false
    }
    if (angle.value == 0f) {
        increaseAngle.value = true
    }

    if (isRunning) {
        LaunchedEffect("timer-gauge") {
            while (isActive) {
                delay(50)
                if (increaseAngle.value) {
                    angle.value = angle.value + 6
                } else {
                    angle.value = angle.value - 6
                }
            }
        }
    }

    Box(contentAlignment = Alignment.Center, modifier = Modifier.background(color = bleu)) {
        Canvas(
            modifier = modifier
                .fillMaxSize(1f)
                .padding(48.dp),
            onDraw = {
                val canvasHeight = size.height
                val canvasWidth = size.width

                for (degree in 1..360 step 6) {
                    rotate(degrees = degree.toFloat()) {
                        drawRect(
                            color = bleuDark,
                            topLeft = Offset(x = canvasWidth / 2F, y = canvasHeight / 2F),
                            size = Size(width = size.maxDimension / 4, height = 10f + extra)
                        )
                    }
                }

                for (degree in 1..angle.value.toInt() step 6) {
                    rotate(degrees = degree.toFloat() + angle.value) {
                        drawRect(
                            color = teal200,
                            topLeft = Offset(x = canvasWidth / 2F, y = canvasHeight / 2F),
                            size = Size(width = size.maxDimension / 4, height = 10f)
                        )
                    }
                }

                drawCircle(
                    color = bleu,
                    radius = canvasHeight / 4.5f,
                    center = Offset(x = canvasWidth / 2, canvasHeight / 2)
                )
            }
        )
    }
}

@Composable
@Preview
fun GaugeViewPreview() {
    GaugeView(isRunning = true)
}

@Composable
fun MyApp() {
    val isRunning = remember { mutableStateOf(true) }
    val remainingTime = remember { mutableStateOf(122) }

    if (remainingTime.value == 0) {
        isRunning.value = false
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        content = {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,
                content = {
                    Box(contentAlignment = Alignment.Center) {
                        GaugeView(isRunning = isRunning.value)
                        NumberCounter(
                            remainingTime = remainingTime.value,
                            isRunning = isRunning.value,
                            onCountDown = {
                                remainingTime.value = remainingTime.value - 1
                            }
                        )

                        ActionButton(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 100.dp),
                            isRunning = isRunning.value,
                            onRestart = {
                                isRunning.value = true
                                remainingTime.value = 100
                            },
                            onFinish = {
                                isRunning.value = false
                                remainingTime.value = 0
                            },
                            onPlay = {
                                if (remainingTime.value == 0) {
                                    remainingTime.value = 100
                                }
                                isRunning.value = !isRunning.value
                            }
                        )
                    }
                }
            )
        }
    )
}

@Composable
fun ActionButton(
    modifier: Modifier = Modifier,
    isRunning: Boolean,
    onRestart: () -> Unit,
    onFinish: () -> Unit,
    onPlay: (Boolean) -> Unit
) {

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        content = {
            IconButton(onClick = onRestart) {
                Icon(
                    imageVector = Icons.Rounded.Replay,
                    contentDescription = null,
                    tint = bleuLight,
                    modifier = Modifier.size(48.dp),
                )
            }

            FloatingActionButton(
                modifier = Modifier.padding(
                    start = 32.dp,
                    end = 32.dp
                ),
                onClick = { onPlay(!isRunning) },
                content = {
                    Crossfade(targetState = isRunning) { state ->
                        if (state) {
                            Icon(
                                modifier = Modifier.size(30.dp),
                                imageVector = Icons.Rounded.Pause,
                                contentDescription = null,
                                tint = MaterialTheme.colors.background
                            )
                        } else {
                            Icon(
                                modifier = Modifier.size(30.dp),
                                imageVector = Icons.Rounded.PlayArrow,
                                contentDescription = null,
                                tint = MaterialTheme.colors.background
                            )
                        }
                    }
                }
            )

            IconButton(onClick = onFinish) {
                Icon(
                    modifier = Modifier.size(48.dp),
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null,
                    tint = bleuLight
                )
            }
        }
    )
}

@Composable
fun NumberCounter(
    remainingTime: Int,
    isRunning: Boolean,
    onCountDown: () -> Unit
) {
    val seconds = remainingTime % 60
    val minutes = remainingTime / 60

    val formatted = "%02d:%02d".format(minutes, seconds)

    if (isRunning) {
        LaunchedEffect("timer") {
            while (isActive) {
                onCountDown()
                delay(1000)
            }
        }
    }

    Box(contentAlignment = Alignment.Center) {
        Text(
            text = formatted,
            color = MaterialTheme.colors.secondary,
            style = MaterialTheme.typography.h1
        )
    }
}

@Composable
@Preview("Action Button")
fun ActionButtonPreview() {
    ActionButton(
        isRunning = true,
        onRestart = { /*TODO*/ },
        onFinish = { /*TODO*/ },
        onPlay = { /*TODO*/ }
    )
}
