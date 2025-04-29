package com.cericatto.eggbasketcollection.ui.basket

import android.annotation.SuppressLint
import android.graphics.RectF
import androidx.compose.animation.core.Animatable
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.cericatto.eggbasketcollection.ui.ObserveAsEvents
import com.cericatto.eggbasketcollection.ui.UiEvent
import com.cericatto.eggbasketcollection.ui.theme.backgroundFirstColor
import com.cericatto.eggbasketcollection.ui.theme.backgroundLastColor
import com.cericatto.eggbasketcollection.ui.theme.brown
import com.cericatto.eggbasketcollection.ui.theme.nunitoBoldFont
import com.cericatto.eggbasketcollection.ui.theme.titleColor
import kotlinx.coroutines.launch

@Composable
fun BasketScreenRoot(
	modifier: Modifier = Modifier,
	viewModel: BasketScreenViewModel = hiltViewModel()
) {
	val state by viewModel.state.collectAsStateWithLifecycle()
	val scope = rememberCoroutineScope()
	val snackbarHostState = remember { SnackbarHostState() }
	val context = LocalContext.current
	ObserveAsEvents(viewModel.events) { event ->
		when (event) {
			is UiEvent.ShowSnackbar -> {
				scope.launch {
					snackbarHostState.showSnackbar(
						message = event.message.asString(context)
					)
				}
			}

			else -> Unit
		}
	}

	BasketScreen(
		onAction = viewModel::onAction,
		state = state,
		snackbarHostState = snackbarHostState,
		modifier = modifier
	)
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun BasketScreen(
	onAction: (BasketScreenAction) -> Unit,
	state: BasketScreenState,
	snackbarHostState: SnackbarHostState,
	modifier: Modifier = Modifier
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
		Scaffold(
			snackbarHost = {
				SnackbarHost(hostState = snackbarHostState)
			},
		) { _ ->
			BasketScreenContent(
				onAction = onAction,
				state = state,
				modifier = modifier
			)
		}
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
			EggPoints(
				onAction = onAction,
				state = state
			)
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
	onAction: (BasketScreenAction) -> Unit,
	state: BasketScreenState,
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
				text = "${state.eggsInBasket}",
				style = TextStyle(
					fontFamily = nunitoBoldFont,
					color = Color.White,
					fontSize = 18.sp
				),
				modifier = Modifier.padding(end = 10.dp)
			)

			if (state.eggsInBasket == EGG_NUMBER) {
				onAction(BasketScreenAction.CollectedAllEggs)
			}
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
	val basketNormal = ContextCompat.getDrawable(context, R.drawable.basket_zero_normal)
	val basketShine = ContextCompat.getDrawable(context, R.drawable.basket_zero_shine)
	val padding = with(density) { 40.dp.toPx() }

	// State for canvas size.
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

	// Then handle the reset by updating the existing list.
	if (state.reset) {
		val initialPositions = initialEggPositions(canvasWidth, canvasHeight, padding)
		// Update existing list in-place instead of creating a new one.
		for (i in eggPositions.indices) {
			if (i < initialPositions.size) {
				eggPositions[i] = initialPositions[i]
			}
		}
		onAction(BasketScreenAction.OnAfterResetButtonClicked)
	}

	// Handle animation triggered by state changes.
	LaunchedEffect(state.animationId) {
		val eggIndex = state.eggToAnimate

		// CRITICAL FIX: Don't use the targetPosition from state - this might be (0,0)
		// Instead, calculate the correct initial position for this egg based on canvas dimensions
		val correctInitialPosition = if (eggIndex >= 0 && eggIndex < initialEggPositions(canvasWidth, canvasHeight, padding).size) {
			initialEggPositions(canvasWidth, canvasHeight, padding)[eggIndex].point
		} else {
			Offset(canvasWidth / 2, padding) // Fallback.
		}

		if (eggIndex >= 0 && eggIndex < eggPositions.size && state.animationId > 0) {
			// Create animatables for X and Y coordinates.
			val currentPosition = eggPositions[eggIndex].point
			val animatableX = Animatable(currentPosition.x)
			val animatableY = Animatable(currentPosition.y)

			// Use a spring animation spec for more natural movement.
			val springSpec = androidx.compose.animation.core.spring<Float>(
				dampingRatio = 0.7f,  // Less than 1.0 for slight bounce effect.
				stiffness = 300f      // Higher value for faster return.
			)

			println("Animating egg $eggIndex from ${currentPosition.x},${currentPosition.y} " +
				"to ${correctInitialPosition.x},${correctInitialPosition.y}")

			// Run animations in parallel and update position on each frame.
			launch {
				animatableX.animateTo(
					targetValue = correctInitialPosition.x,
					animationSpec = springSpec
				) {
					// Update X position on each animation frame.
					val newX = this.value
					val currentY = eggPositions[eggIndex].point.y
					eggPositions[eggIndex] = eggPositions[eggIndex].copy(
						point = Offset(newX, currentY)
					)
				}
			}

			launch {
				animatableY.animateTo(
					targetValue = correctInitialPosition.y,
					animationSpec = springSpec
				) {
					// Update Y position on each animation frame.
					val newY = this.value
					val currentX = eggPositions[eggIndex].point.x
					eggPositions[eggIndex] = eggPositions[eggIndex].copy(
						point = Offset(currentX, newY)
					)
				}
			}.join() // Wait for vertical animation to complete.

			// Ensure the final position is exactly the correct initial position.
			eggPositions[eggIndex] = eggPositions[eggIndex].copy(
				point = correctInitialPosition
			)

			// Notify ViewModel that animation is complete.
			onAction(BasketScreenAction.UpdateEggPosition(eggIndex, correctInitialPosition))
		}
	}

	// Update values from state.eggPositions.
	LaunchedEffect(state.eggPositions) {
		if (state.eggPositions.isNotEmpty() && eggPositions.isNotEmpty()) {
			// Update positions and alpha values (except for the one being animated).
			for (i in eggPositions.indices) {
				if (i < state.eggPositions.size && i != state.eggToAnimate) {
					val stateEgg = state.eggPositions[i]
					eggPositions[i] = eggPositions[i].copy(
						point = stateEgg.point,
						alpha = stateEgg.alpha
					)
				}
			}
		}
	}

	// Create bitmap with remembered size.
	val bitmap = remember(canvasSize) {
		if (canvasSize != IntSize.Zero) {
			createBitmap(canvasSize.width, canvasSize.height)
		} else {
			createBitmap(1, 1) // Fallback.
		}
	}

	// Track the currently dragged egg index.
	var draggedEggIndex by remember { mutableIntStateOf(-1) }

	Canvas(
		modifier = modifier
			.fillMaxSize()
			.onGloballyPositioned { coordinates ->
				canvasSize = coordinates.size
			}
			.pointerInput(Unit) {
				// Handle drag gestures.
				detectDragGestures(
					onDragStart = { offset ->
						eggPositions.forEachIndexed { index, point ->
							val eggWidth = (eggNormal?.intrinsicWidth?.toFloat() ?: 0f)
							val eggHeight = (eggNormal?.intrinsicHeight?.toFloat() ?: 0f)
							val bounds = RectF(
								point.point.x,
								point.point.y,
								point.point.x + eggWidth * point.scale,
								point.point.y + eggHeight * point.scale
							)
							if (bounds.contains(offset.x, offset.y)) {
								point.isDragging = true
								draggedEggIndex = index
							}
						}
					},
					onDragEnd = {
						eggPositions.forEach { it.isDragging = false }
						// Call the new action when drag ends.
						if (draggedEggIndex >= 0) {
							onAction(
								BasketScreenAction.OnEggDragEnd(
									eggPositions = eggPositions.toList(),
									draggedEggIndex = draggedEggIndex
								)
							)
							draggedEggIndex = -1
						}
					}
				) { change, dragAmount ->
					eggPositions.forEachIndexed { index, point ->
						if (point.isDragging) {
							val eggWidth = (eggNormal?.intrinsicWidth?.toFloat() ?: 0f)
							val eggHeight = (eggNormal?.intrinsicHeight?.toFloat() ?: 0f)
							val newX = (point.point.x + dragAmount.x)
								.coerceIn(0f, size.width - eggWidth * point.scale)
							val newY = (point.point.y + dragAmount.y)
								.coerceIn(0f, size.height - eggHeight * point.scale)
							eggPositions[index] = point.copy(point = Offset(newX, newY))
							onAction(BasketScreenAction.CheckEggPositionsChanged(eggPositions))
							onAction(BasketScreenAction.CheckEggsInBasket(eggPositions))
						}
					}
					change.consume()
				}
				// Handle tap gestures to unselect eggs.
				detectTapGestures { offset ->
					var isEggTapped = false
					eggPositions.forEachIndexed { index, point ->
						val eggWidth = (eggNormal?.intrinsicWidth?.toFloat() ?: 0f)
						val eggHeight = (eggNormal?.intrinsicHeight?.toFloat() ?: 0f)
						val bounds = RectF(
							point.point.x,
							point.point.y,
							point.point.x + eggWidth * point.scale,
							point.point.y + eggHeight * point.scale
						)
						if (bounds.contains(offset.x, offset.y)) {
							isEggTapped = true
							point.isDragging = true
							draggedEggIndex = index
						}
					}
					if (!isEggTapped) {
						eggPositions.forEach { it.isDragging = false }
						draggedEggIndex = -1
					}
				}
			}
	) {
		val canvas = android.graphics.Canvas(bitmap)
		canvas.drawColor(android.graphics.Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR)

		// Draw eggs.
		eggPositions.forEach { point ->
			// Skip drawing eggs with alpha 0 (collected eggs).
			if (point.alpha > 0) {
				// Choose drawable based on isDragging.
				val eggDrawable = if (point.isDragging) eggShine else eggNormal
				eggDrawable?.let { draw ->
					val eggWidth = draw.intrinsicWidth
					val eggHeight = draw.intrinsicHeight
					val centerX = point.point.x + eggWidth / 2f
					val centerY = point.point.y + eggHeight / 2f
					canvas.rotate(point.rotation, centerX, centerY)
					draw.setBounds(
						point.point.x.toInt(),
						point.point.y.toInt(),
						(point.point.x + eggWidth * point.scale).toInt(),
						(point.point.y + eggHeight * point.scale).toInt()
					)
					draw.draw(canvas)
					canvas.rotate(-point.rotation, centerX, centerY)
				}
			}
		}

		// Draw fixed basket.
		val basketDrawable = if (state.hotZone) basketShine else basketNormal
		basketDrawable?.let { draw ->
			val width = draw.intrinsicWidth
			val height = draw.intrinsicHeight
			val basketX = (size.width - width) / 2
			val basketY = size.height - height

			// Update basket bounds in state.
			val basketBounds = RectF(
				basketX,
				basketY,
				basketX + width,
				basketY + height
			)
			onAction(BasketScreenAction.UpdateBasketBounds(basketBounds))
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
		),
		snackbarHostState = SnackbarHostState()
	)
}

@Preview(showBackground = true)
@Composable
private fun EggPointsPreview() {
	EggPoints(
		onAction = {},
		state = BasketScreenState()
	)
}