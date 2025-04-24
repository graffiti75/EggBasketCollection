package com.cericatto.eggbasketcollection.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun isLandscapeOrientation(): Boolean {
	val context = LocalContext.current
	val orientation = context.resources.configuration.orientation
	return when (orientation) {
		Configuration.ORIENTATION_LANDSCAPE -> true
		else -> false
	}
}

@Composable
fun getCanvasWidth(): Dp {
	val configuration = LocalConfiguration.current
	return if (isLandscapeOrientation()) {
		configuration.screenHeightDp.dp
	} else {
		configuration.screenWidthDp.dp
	}
}

@Composable
fun getCanvasHeight(): Dp {
	val configuration = LocalConfiguration.current
	return if (isLandscapeOrientation()) {
		configuration.screenWidthDp.dp
	} else {
		configuration.screenHeightDp.dp
	}
}

@Composable
fun Dp.Dp2Float(): Float {
	val density = LocalDensity.current
	return with(density) { this@Dp2Float.toPx() }
}

fun Modifier.canvasModifier(
	lineColor: Color = Color.LightGray,
	radius: Dp = 30.dp,
	borderPadding: Dp = 10.dp
) = this
	.background(
		color = lineColor,
		shape = RoundedCornerShape(radius)
	)
	.padding(0.5.dp)
	.background(
		color = Color.White,
		shape = RoundedCornerShape(radius)
	)
	.padding(borderPadding)
	.background(
		color = lineColor,
		shape = RoundedCornerShape(radius)
	)
	.padding(0.5.dp)
	.background(
		color = Color.White,
		shape = RoundedCornerShape(radius)
	)
	.clipToBounds()