package com.cericatto.eggbasketcollection.ui.basket

import androidx.compose.ui.geometry.Offset
import java.util.UUID

const val EGG_NUMBER = 6

data class CanvasPoint(
	var key: String = UUID.randomUUID().toString(),
	var point: Offset,
	val rotation: Float = 0f,
	val scale: Float = 1f,
	val alpha: Int = 255,
	var isDragging: Boolean = false
)

data class BasketScreenState(
	val loading : Boolean = true,
	var eggPositions: List<CanvasPoint> = initialEmptyEggPositions(),
	val canvasWidth: Float = 0f,
	val canvasHeight: Float = 0f,
	val padding: Float = 0f,
	var changedEggPositions: Boolean = false,
	var reset: Boolean = true,
	// Basket scoring:
	val hotZone: Boolean = false,
	val eggsInBasket: Int = 0,
	val basketBounds: android.graphics.RectF? = null,
	// Animation control:
	val eggToAnimate: Int = -1,
	val targetPosition: Offset = Offset.Zero,
	val animationId: Long = 0 // This changes when a new animation should start.
)

fun initialEmptyEggPositions() = List(6) { CanvasPoint(point = Offset(0f, 0f)) }

fun initialEggPositions(
	canvasWidth: Float = 0f,
	canvasHeight: Float = 0f,
	padding: Float = 0f
): List<CanvasPoint> {
	val unit = 300f
	return listOf(
		CanvasPoint(point = Offset(canvasWidth / 3, padding), rotation = 10f),
		CanvasPoint(point = Offset(10f, canvasHeight / 4), rotation = -25f),
		CanvasPoint(point = Offset(canvasWidth / 3, canvasHeight / 3), rotation = 0f),
		CanvasPoint(point = Offset(canvasWidth - unit, canvasHeight / 4), rotation = 20f, scale = 0.8f),
		CanvasPoint(point = Offset(10f, canvasHeight / 2), rotation = -20f),
		CanvasPoint(point = Offset(canvasWidth - unit, canvasHeight / 2), rotation = 20f, scale = 0.9f)
	)
}

fun eggPositionsChanged(
	eggPositions: List<CanvasPoint>,
	canvasWidth: Float = 0f,
	canvasHeight: Float = 0f,
	padding: Float = 0f,
): Boolean {
	val initialPositions = initialEggPositions(canvasWidth, canvasHeight, padding)
	if (eggPositions.size != initialPositions.size) return true
	return eggPositions.zip(initialPositions).any { (current, initial) ->
		current.point != initial.point ||
			current.rotation != initial.rotation ||
			current.scale != initial.scale
	}
}