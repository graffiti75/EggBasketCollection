package com.cericatto.eggbasketcollection.ui.basket

import android.graphics.RectF
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cericatto.eggbasketcollection.R
import com.cericatto.eggbasketcollection.ui.UiEvent
import com.cericatto.eggbasketcollection.ui.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BasketScreenViewModel @Inject constructor() : ViewModel() {

	private val _state = MutableStateFlow(BasketScreenState())
	val state: StateFlow<BasketScreenState> = _state.asStateFlow()

	private val _events = Channel<UiEvent>()
	val events = _events.receiveAsFlow()

	// Store initial egg positions to animate back to.
	private var initialPositions = listOf<CanvasPoint>()

	// Animation counter to trigger new animations.
	private var animationCounter = 0L

	fun onAction(action: BasketScreenAction) {
		when (action) {
			is BasketScreenAction.UpdateCanvasDimensions -> updateCanvasDimensions(
				action.canvasWidth,
				action.canvasHeight,
				action.padding
			)
			is BasketScreenAction.OnResetButtonClicked -> onResetButtonClicked()
			is BasketScreenAction.CheckEggPositionsChanged -> checkEggPositionsChanged(action.eggPositions)
			is BasketScreenAction.OnAfterResetButtonClicked -> onAfterResetButtonClicked()
			is BasketScreenAction.CheckEggsInBasket -> checkEggsInBasket(action.eggPositions)
			is BasketScreenAction.UpdateBasketBounds -> updateBasketBounds(action.bounds)
			is BasketScreenAction.CollectedAllEggs -> collectedAllEggs()
			is BasketScreenAction.OnEggDragEnd -> onEggDragEnd(action.eggPositions, action.draggedEggIndex)
			is BasketScreenAction.UpdateEggPosition -> updateEggPosition(action.index, action.position)
		}
	}

	init {
		_state.update { state ->
			state.copy(
				loading = false
			)
		}
	}

	private fun updateCanvasDimensions(
		canvasWidth: Float = 0f,
		canvasHeight: Float = 0f,
		padding: Float = 0f
	) {
		val positions = initialEggPositions(
			canvasWidth = canvasWidth,
			canvasHeight = canvasHeight,
			padding = padding,
		)

		// Store initial positions for animations
		initialPositions = positions.map { it.copy() }

		_state.update { state ->
			state.copy(
				canvasWidth = canvasWidth,
				canvasHeight = canvasHeight,
				padding = padding,
				eggPositions = positions
			)
		}
	}

	private fun onResetButtonClicked() {
		_state.update { state ->
			state.copy(
				reset = true,
				changedEggPositions = false,
				eggsInBasket = 0
			)
		}

		// Update initial positions when reset.
		val currentState = _state.value
		initialPositions = initialEggPositions(
			canvasWidth = currentState.canvasWidth,
			canvasHeight = currentState.canvasHeight,
			padding = currentState.padding
		)
	}

	private fun onAfterResetButtonClicked() {
		_state.update { state ->
			state.copy(
				reset = false
			)
		}
	}

	private fun checkEggPositionsChanged(eggPositions: List<CanvasPoint>) {
		_state.update { state ->
			state.copy(
				changedEggPositions = eggPositionsChanged(
					eggPositions = eggPositions,
					canvasWidth = state.canvasWidth,
					canvasHeight = state.canvasHeight,
					padding = state.padding
				)
			)
		}
	}

	private fun updateBasketBounds(bounds: RectF) {
		_state.update { state ->
			state.copy(basketBounds = bounds)
		}
	}

	private fun checkEggsInBasket(eggPositions: List<CanvasPoint>) {
		val currentState = _state.value
		val basketBounds = currentState.basketBounds ?: return

		// Create a copy of egg positions to modify.
		val updatedEggPositions = eggPositions.toMutableList()

		// Count eggs that have their center point inside the basket.
		var count = 0
		for (i in eggPositions.indices) {
			val eggPosition = eggPositions[i]

			// Calculate egg center point.
			val eggWidth = 100f * eggPosition.scale // Approximate egg width.
			val eggHeight = 130f * eggPosition.scale // Approximate egg height.

			val eggCenterX = eggPosition.point.x + eggWidth / 2
			val eggCenterY = eggPosition.point.y + eggHeight / 2

			// Check if center is in basket bounds.
			if (basketBounds.contains(eggCenterX, eggCenterY)) {
				count++
				// Update the alpha directly in our copy
				updatedEggPositions[i] = eggPosition.copy(alpha = 0)
			} else {
				// Make sure eggs outside basket are visible
				updatedEggPositions[i] = eggPosition.copy(alpha = 255)
			}
		}

		// Update state with new positions and count.
		_state.update { state ->
			state.copy(
				hotZone = count > 0,
				eggsInBasket = count,
				eggPositions = updatedEggPositions
			)
		}
	}

	private fun onEggDragEnd(eggPositions: List<CanvasPoint>, draggedEggIndex: Int) {
		// Get the current state.
		val currentState = _state.value
		val basketBounds = currentState.basketBounds ?: return

		// If index is invalid, return.
		if (draggedEggIndex < 0 || draggedEggIndex >= eggPositions.size) return

		val draggedEgg = eggPositions[draggedEggIndex]

		// Calculate egg center point.
		val eggWidth = 100f * draggedEgg.scale
		val eggHeight = 130f * draggedEgg.scale
		val eggCenterX = draggedEgg.point.x + eggWidth / 2
		val eggCenterY = draggedEgg.point.y + eggHeight / 2

		// Check if the egg was dropped outside the basket.
		if (!basketBounds.contains(eggCenterX, eggCenterY)) {
			// Show a snackbar.
			viewModelScope.launch {
				callSnackbar(UiText.StringResource(R.string.aim_basket))
			}

			// Get the initial position for this egg.
			val initialPosition = if (initialPositions.size > draggedEggIndex) {
				initialPositions[draggedEggIndex].point
			} else {
				// Fallback if initialPositions isn't available.
				Offset(x = currentState.canvasWidth / 2, y = currentState.padding)
			}

			// Instead of animating here, update the state with values that will trigger animation in the UI
			animationCounter++
			_state.update { state ->
				state.copy(
					eggToAnimate = draggedEggIndex,
					targetPosition = initialPosition,
					animationId = animationCounter
				)
			}
		}
	}

	/**
	 * Updates egg position after animation completes in the UI.
	 */
	private fun updateEggPosition(index: Int, position: Offset) {
		if (index < 0 || index >= _state.value.eggPositions.size) return

		_state.update { state ->
			val updatedPositions = state.eggPositions.toMutableList()
			updatedPositions[index] = updatedPositions[index].copy(point = position)
			state.copy(
				eggPositions = updatedPositions,
				eggToAnimate = -1 // Reset animation state.
			)
		}
	}

	private fun collectedAllEggs() {
		viewModelScope.launch {
			callSnackbar(UiText.StringResource(R.string.collected_all))
		}
	}

	private suspend fun callSnackbar(text: UiText) {
		_events.send(UiEvent.ShowSnackbar(text))
	}
}