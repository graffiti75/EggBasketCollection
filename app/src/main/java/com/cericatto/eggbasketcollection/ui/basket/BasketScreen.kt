package com.cericatto.eggbasketcollection.ui.basket

import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cericatto.eggbasketcollection.R
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
			modifier = modifier
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
			state = state,
			modifier = modifier
		)
	}
}

@Composable
fun BasketScreenContent(
	onAction: (BasketScreenAction) -> Unit,
	state: BasketScreenState,
	modifier: Modifier = Modifier,
) {
	Column(
		verticalArrangement = Arrangement.Top,
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = modifier
			.fillMaxSize()
			.background(
				brush = Brush.verticalGradient(
					colors = listOf(
						backgroundFirstColor,
						backgroundLastColor
					)
				)
			)
			.padding(top = 10.dp)
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween,
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 10.dp)
		) {
			EggPoints()
			RestartButton(
				onAction = onAction,
				state = state,
				modifier = Modifier
			)
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
private fun RestartButton(
	onAction: (BasketScreenAction) -> Unit,
	state: BasketScreenState,
	modifier: Modifier = Modifier
) {
	Row(
		horizontalArrangement = Arrangement.Center,
		verticalAlignment = Alignment.CenterVertically,
		modifier = modifier
			.alpha(
				if (!state.changedEggPositions) 0f else 1f
			)
			.clickable {
				onAction(
					BasketScreenAction.OnResetButtonClicked
				)
			}
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
fun DrawCanvas(
	onAction: (BasketScreenAction) -> Unit,
	state: BasketScreenState,
	modifier: Modifier = Modifier
) {
	val context = LocalContext.current
	val density = LocalDensity.current
	val eggNormal = ContextCompat.getDrawable(context, R.drawable.egg_normal)
	val eggShine = ContextCompat.getDrawable(context, R.drawable.egg_shine)
	val basket = ContextCompat.getDrawable(context, R.drawable.basket_zero_normal)
	val padding = with(density) { 40.dp.toPx() }

	// State for canvas size
	var canvasSize by remember { mutableStateOf(IntSize.Zero) }
	val canvasWidth = canvasSize.width.toFloat()
	val canvasHeight = canvasSize.height.toFloat() * 0.8f // 80% of screen height

	// Egg positions with absolute coordinates.
	// Used the spread operator (*) to convert List<CanvasPoint> to varargs for mutableStateListOf.
	var eggPositions = remember(canvasWidth, canvasHeight) {
		mutableStateListOf(
			*initialEggPositions(canvasWidth, canvasHeight, padding).toTypedArray()
		)
	}

	if (state.reset) {
		eggPositions = remember(canvasWidth, canvasHeight) {
			mutableStateListOf(
				*initialEggPositions(canvasWidth, canvasHeight, padding).toTypedArray()
			)
		}
		onAction(
			BasketScreenAction.OnAfterResetButtonClicked
		)
	}

	// Create bitmap with remembered size
	val bitmap = remember(canvasSize) {
		if (canvasSize != IntSize.Zero) {
			createBitmap(canvasSize.width, canvasSize.height)
		} else {
			createBitmap(1, 1) // Fallback
		}
	}

	Canvas(
		modifier = modifier
			.fillMaxSize()
			.onGloballyPositioned { coordinates ->
				canvasSize = coordinates.size
			}
			.pointerInput(Unit) {
				// Handle drag gestures
				detectDragGestures(
					onDragStart = { offset ->
						eggPositions.forEachIndexed { index, point ->
							val bounds = RectF(
								point.point.x,
								point.point.y,
								point.point.x + (eggNormal?.intrinsicWidth?.toFloat() ?: 0f) * point.scale,
								point.point.y + (eggNormal?.intrinsicHeight?.toFloat() ?: 0f) * point.scale
							)
							if (bounds.contains(offset.x, offset.y)) {
								point.isDragging = true
							}
						}
					},
					onDragEnd = {
						eggPositions.forEach { it.isDragging = false }
					}
				) { change, dragAmount ->
					eggPositions.forEachIndexed { index, point ->
						if (point.isDragging) {
							val newX = (point.point.x + dragAmount.x)
								.coerceIn(0f, size.width - (eggNormal?.intrinsicWidth?.toFloat() ?: 0f) * point.scale)
							val newY = (point.point.y + dragAmount.y)
								.coerceIn(0f, size.height - (eggNormal?.intrinsicHeight?.toFloat() ?: 0f) * point.scale)
							// FIXME
//							onAction(
//								BasketScreenAction.UpdateEggPosition(index, Offset(newX, newY))
//							)
							eggPositions[index] = point.copy(
								point = Offset(newX, newY)
							)
							onAction(
								BasketScreenAction.CheckEggPositionsChanged(eggPositions)
							)
						}
					}
					change.consume()
				}
				// Handle tap gestures to unselect eggs
				detectTapGestures { offset ->
					var isEggTapped = false
					eggPositions.forEachIndexed { index, point ->
						val bounds = RectF(
							point.point.x,
							point.point.y,
							point.point.x + (eggNormal?.intrinsicWidth?.toFloat() ?: 0f) * point.scale,
							point.point.y + (eggNormal?.intrinsicHeight?.toFloat() ?: 0f) * point.scale
						)
						if (bounds.contains(offset.x, offset.y)) {
							isEggTapped = true
							point.isDragging = true
						}
					}
					if (!isEggTapped) {
						eggPositions.forEach { it.isDragging = false }
					}
				}
			}
	) {
		val canvas = android.graphics.Canvas(bitmap)
		canvas.drawColor(android.graphics.Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR)

		// Draw eggs
		eggPositions.forEach { point ->
			// Choose drawable based on isDragging
			val eggDrawable = if (point.isDragging) eggShine else eggNormal
			eggDrawable?.let { draw ->
				val centerX = point.point.x + draw.intrinsicWidth / 2f
				val centerY = point.point.y + draw.intrinsicHeight / 2f
				canvas.rotate(point.rotation, centerX, centerY)
				draw.setBounds(
					point.point.x.toInt(),
					point.point.y.toInt(),
					(point.point.x + draw.intrinsicWidth * point.scale).toInt(),
					(point.point.y + draw.intrinsicHeight * point.scale).toInt()
				)
				draw.draw(canvas)
				canvas.rotate(-point.rotation, centerX, centerY)
			}
		}

		// Draw fixed basket
		basket?.let { draw ->
			val width = draw.intrinsicWidth
			val height = draw.intrinsicHeight
			val basketX = (size.width - width) / 2
			val basketY = size.height - height
			draw.setBounds(
				basketX.toInt(),
				basketY.toInt(),
				(basketX + width).toInt(),
				(basketY + height).toInt()
			)
			draw.draw(canvas)
		}

		drawImage(image = bitmap.asImageBitmap())
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