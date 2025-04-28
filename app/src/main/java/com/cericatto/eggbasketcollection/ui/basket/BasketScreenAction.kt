package com.cericatto.eggbasketcollection.ui.basket

import androidx.compose.ui.geometry.Offset

sealed interface BasketScreenAction {
	data class UpdateCanvasDimensions(
		val canvasWidth: Float,
		val canvasHeight: Float,
		val padding: Float
	) : BasketScreenAction

//	data class UpdateEggPosition(val index: Int, val offset: Offset) : BasketScreenAction
//	data class UpdateEggPositions(val eggPositions: List<CanvasPoint>) : BasketScreenAction
	data object OnResetButtonClicked : BasketScreenAction
	data object OnAfterResetButtonClicked : BasketScreenAction
	data class CheckEggPositionsChanged(val eggPositions: List<CanvasPoint>) : BasketScreenAction
}