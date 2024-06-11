package com.typhon.newsroom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.typhon.newsroom.presentation.theme.NewsroomTheme
import com.typhon.newsroom.util.NavGraphSetup
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NewsroomTheme {
                val navController= rememberNavController()
                NavGraphSetup(navController = navController)


            }
        }
    }
}

