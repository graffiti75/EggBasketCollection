package com.cericatto.eggbasketcollection.ui.basket

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cericatto.eggbasketcollection.R
import com.cericatto.eggbasketcollection.ui.components.Dp2Float
import com.cericatto.eggbasketcollection.ui.components.getCanvasHeight
import com.cericatto.eggbasketcollection.ui.components.getCanvasWidth
import com.cericatto.eggbasketcollection.ui.theme.backgroundFirstColor
import com.cericatto.eggbasketcollection.ui.theme.backgroundLastColor
import com.cericatto.eggbasketcollection.ui.theme.brown
import com.cericatto.eggbasketcollection.ui.theme.nunitoBoldFont
import com.cericatto.eggbasketcollection.ui.theme.titleColor

@Composable
fun BasketScreenRoot(
	modifier: Modifier = Modifier,
	viewModel: BasketScreenViewModel = hiltViewModel()
) {
	val state by viewModel.state.collectAsStateWithLifecycle()
	BasketScreen(
		modifier = modifier,
		onAction = viewModel::onAction,
		state = state
	)
}

@Composable
private fun BasketScreen(
	modifier: Modifier = Modifier,
	onAction: (BasketScreenAction) -> Unit,
	state: BasketScreenState
) {
	if (state.loading) {
		Box(
			modifier = Modifier
				.padding(vertical = 20.dp)
				.fillMaxSize(),
			contentAlignment = Alignment.Center
		) {
			CircularProgressIndicator(
				color = MaterialTheme.colorScheme.primary,
				strokeWidth = 4.dp,
				modifier = Modifier.size(64.dp)
			)
		}
	} else {
		BasketScreenContent(
			onAction = onAction,
			state = state
		)
	}
}

@Composable
fun BasketScreenContent(
	onAction: (BasketScreenAction) -> Unit,
	state: BasketScreenState
) {
	Column(
		verticalArrangement = Arrangement.Top,
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = Modifier
			.fillMaxSize()
			.background(
				brush = Brush.verticalGradient(
					colors = listOf(
						backgroundFirstColor,
						backgroundLastColor
					)
				)
			)
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween,
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 10.dp)
		) {
			EggPoints()
			RestartButton()
		}
		Text(
			text = "Easter Basket",
			style = TextStyle(
				color = titleColor,
				fontSize = 24.sp,
				fontFamily = nunitoBoldFont
			),
			modifier = Modifier.padding(top = 30.dp)
		)
		DrawCanvas(
			onAction = onAction,
			state = state
		)
	}
}

@Composable
private fun EggPoints(
	modifier: Modifier = Modifier
) {
	Box(
		contentAlignment = Alignment.Center
	) {
		Row(
			horizontalArrangement = Arrangement.Center,
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier
				.background(
					color = brown,
					shape = RoundedCornerShape(20.dp)
				)
				.minimumInteractiveComponentSize()
		) {
			Image(
				painter = painterResource(R.drawable.egg_normal),
				contentDescription = "Egg",
				modifier = Modifier
					.padding(start = 50.dp)
					.size(32.dp)
			)
			Text(
				text = "0",
				style = TextStyle(
					fontFamily = nunitoBoldFont,
					color = Color.White,
					fontSize = 18.sp
				),
				modifier = Modifier.padding(end = 10.dp)
			)
		}
		Image(
			painter = painterResource(R.drawable.basket_three_normal),
			contentDescription = "Egg",
			modifier = Modifier
				.size(56.dp)
				.rotate(-10f)
				.align(Alignment.CenterStart)
		)
	}
}

@Composable
private fun RestartButton() {
	Row(
		horizontalArrangement = Arrangement.Center,
		verticalAlignment = Alignment.CenterVertically,
		modifier = Modifier
			.background(
				color = Color(0xFFC7C7C7),
				shape = RoundedCornerShape(10.dp)
			)
			.padding(3.dp)
			.background(
				color = Color(0xFF9C9C9C),
				shape = RoundedCornerShape(10.dp)
			)
			.padding(5.dp)
	) {
		Image(
			painter = painterResource(R.drawable.restart),
			contentDescription = "Restart",
			modifier = Modifier
				.size(32.dp)
		)
		Text(
			text = "Restart",
			style = TextStyle(
				fontFamily = nunitoBoldFont,
				color = Color.White,
				fontSize = 18.sp
			),
		)
	}
}

@Composable
private fun DrawCanvas(
	onAction: (BasketScreenAction) -> Unit,
	state: BasketScreenState,
	modifier: Modifier = Modifier
) {
	val context = LocalContext.current
	val canvasWidth = getCanvasWidth().Dp2Float()
	// 80% of the height of the screen will be the Canvas.
	val canvasHeight = (getCanvasHeight().Dp2Float() / 5) * 4
	val egg: Drawable? = ContextCompat.getDrawable(context, R.drawable.egg_normal)
	val basket: Drawable? = ContextCompat.getDrawable(context, R.drawable.basket_zero_normal)
	val padding = 40.dp.Dp2Float()
	val unit = 300f
	val positions = listOf<CanvasPoint>(
		CanvasPoint(point = Offset(canvasWidth / 3, padding), rotation = 10f),
		CanvasPoint(point = Offset(10f, canvasHeight / 4), rotation = -25f),
		CanvasPoint(point = Offset(canvasWidth / 3, canvasHeight / 3), rotation = 0f),
		CanvasPoint(point = Offset(canvasWidth - unit, canvasHeight / 4), rotation = 20f, scale = 0.8f),
		CanvasPoint(point = Offset(10f, canvasHeight / 2), rotation = -20f),
		CanvasPoint(point = Offset(canvasWidth - unit, canvasHeight / 2), rotation = 20f, scale = 0.9f),
	)
	Box(
		contentAlignment = Alignment.Center,
		modifier = modifier
			.fillMaxSize()
	) {
		Canvas(
			modifier = modifier
				.fillMaxSize()
				.clipToBounds()
		) {
			/*
			drawRect(
				color = Color.Yellow,
				topLeft = Offset(0f, 0f),
				size = Size(
					width = canvasWidth,
					height = canvasHeight
				)
			)
			 */
			positions.forEachIndexed { index, point ->
				egg?.let { draw ->
					val bitmap = createBitmap(size.width.toInt(), size.height.toInt())
					val canvas = android.graphics.Canvas(bitmap)

					val centerX = point.point.x.toInt() + draw.intrinsicWidth / 2f
					val centerY = point.point.y.toInt() + draw.intrinsicHeight / 2f
					val rotationAngle = point.rotation
					canvas.rotate(rotationAngle, centerX, centerY)
					draw.setBounds(
						point.point.x.toInt(),
						point.point.y.toInt(),
						(point.point.x.toInt() + draw.intrinsicWidth * point.scale).toInt(),
						(point.point.y.toInt() + draw.intrinsicHeight * point.scale).toInt()
					)
					draw.draw(canvas)
					drawImage(
						image = bitmap.asImageBitmap()
					)
				}
			}
			basket?.let { draw ->
				val bitmap = createBitmap(size.width.toInt(), size.height.toInt())
				val canvas = android.graphics.Canvas(bitmap)
				val width = draw.intrinsicWidth
				val halfWidth = (width / 2f).toInt()
				val height = draw.intrinsicHeight
				draw.setBounds(
					(canvasWidth / 2 - halfWidth).toInt(),
					canvasHeight.toInt() - height,
					(canvasWidth / 2 - halfWidth).toInt() + draw.intrinsicWidth,
					canvasHeight.toInt() - height + draw.intrinsicHeight,
				)
				draw.draw(canvas)
				drawImage(
					image = bitmap.asImageBitmap()
				)
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
private fun BasketScreenPreview() {
	BasketScreen(
		onAction = {},
		state = BasketScreenState().copy(
			loading = false
		)
	)
}

@Preview(showBackground = true)
@Composable
private fun EggPointsPreview() {
	EggPoints()
}