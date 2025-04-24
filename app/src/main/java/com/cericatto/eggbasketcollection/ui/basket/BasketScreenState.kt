package com.cericatto.eggbasketcollection.ui.basket

import androidx.compose.ui.geometry.Offset

data class CanvasPoint(
	val point: Offset,
	val rotation: Float,
	val scale: Float = 1f
)

data class BasketScreenState(
	val loading : Boolean = true
)