package com.cericatto.eggbasketcollection.ui.basket

import androidx.compose.ui.geometry.Offset

data class CanvasPoint(
	val point: Offset,
	val rotation: Float = 0f,
	val scale: Float = 1f,
	var isDragging: Boolean = false
)

data class BasketScreenState(
	val loading : Boolean = true
)