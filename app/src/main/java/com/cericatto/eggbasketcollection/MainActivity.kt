package com.cericatto.eggbasketcollection

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.cericatto.eggbasketcollection.ui.basket.BasketScreenRoot
import com.cericatto.eggbasketcollection.ui.theme.EggBasketCollectionTheme

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			EggBasketCollectionTheme {
				Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
					BasketScreenRoot(modifier = Modifier.padding(innerPadding))
				}
			}
		}
	}
}