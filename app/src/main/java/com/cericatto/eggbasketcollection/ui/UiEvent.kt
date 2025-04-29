package com.cericatto.eggbasketcollection.ui

sealed class UiEvent {
	data class ShowErrorSnackbar(val messages: List<UiText>): UiEvent()
	data class ShowSnackbar(val message: UiText): UiEvent()
}