package ro.gradinamea

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ro.gradinamea.navigation.AppNavGraph
import ro.gradinamea.ui.theme.GradinaMeaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GradinaMeaTheme {
                AppNavGraph()
            }
        }
    }
}
