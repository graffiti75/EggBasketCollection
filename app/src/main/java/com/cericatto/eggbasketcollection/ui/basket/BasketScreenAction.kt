package com.cericatto.eggbasketcollection.ui.basket

import android.graphics.RectF
import androidx.compose.ui.geometry.Offset

sealed interface BasketScreenAction {
	data class UpdateCanvasDimensions(
		val canvasWidth: Float,
		val canvasHeight: Float,
		val padding: Float
	) : BasketScreenAction
	data object OnResetButtonClicked : BasketScreenAction
	data object OnAfterResetButtonClicked : BasketScreenAction
	data class CheckEggPositionsChanged(val eggPositions: List<CanvasPoint>) : BasketScreenAction
	data class UpdateBasketBounds(val bounds: RectF) : BasketScreenAction
	data class CheckEggsInBasket(val eggPositions: List<CanvasPoint>) : BasketScreenAction
	data object CollectedAllEggs : BasketScreenAction
	data class OnEggDragEnd(
		val eggPositions: List<CanvasPoint>,
		val draggedEggIndex: Int
	) : BasketScreenAction
	data class UpdateEggPosition(
		val index: Int,
		val position: Offset
	) : BasketScreenAction
}