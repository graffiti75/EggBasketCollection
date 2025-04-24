package com.cericatto.eggbasketcollection.ui.basket

sealed interface BasketScreenAction {
	data object FlipCard : BasketScreenAction
}