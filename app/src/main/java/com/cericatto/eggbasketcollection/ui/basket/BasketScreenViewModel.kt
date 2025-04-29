package com.cericatto.eggbasketcollection.ui.basket

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class BasketScreenViewModel @Inject constructor(): ViewModel() {

	private val _state = MutableStateFlow(BasketScreenState())
	val state: StateFlow<BasketScreenState> = _state.asStateFlow()

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
		_state.update { state ->
			state.copy(
				canvasWidth = canvasWidth,
				canvasHeight = canvasHeight,
				padding = padding,
				eggPositions = initialEggPositions(
					canvasWidth = canvasWidth,
					canvasHeight = canvasHeight,
					padding = padding,
				)
			)
		}
	}

	private fun onResetButtonClicked() {
		println("BasketScreenViewModel.onResetButtonClicked()")
		_state.update { state ->
			state.copy(
				reset = true,
				changedEggPositions = false
			)
		}
	}

	private fun onAfterResetButtonClicked() {
		println("BasketScreenViewModel.onAfterResetButtonClicked()")
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
}