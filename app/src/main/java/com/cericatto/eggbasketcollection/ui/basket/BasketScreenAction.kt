package com.cericatto.eggbasketcollection.ui.basket

sealed interface BasketScreenAction {
	data class UpdateCanvasDimensions(
		val canvasWidth: Float,
		val canvasHeight: Float,
		val padding: Float
	) : BasketScreenAction
	data object OnResetButtonClicked : BasketScreenAction
	data object OnAfterResetButtonClicked : BasketScreenAction
	data class CheckEggPositionsChanged(val eggPositions: List<CanvasPoint>) : BasketScreenAction
}