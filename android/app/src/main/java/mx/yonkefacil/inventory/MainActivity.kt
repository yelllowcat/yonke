package mx.yonkefacil.inventory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import mx.yonkefacil.inventory.data.DemoInventoryRepository
import mx.yonkefacil.inventory.data.NetworkInventoryRepository
import mx.yonkefacil.inventory.ui.YonkeApp
import mx.yonkefacil.inventory.ui.theme.YonkeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Android 15+ makes edge-to-edge the default for this target SDK. Keep
        // system icons readable over the warm paper background on every screen,
        // including after the PIN keyboard closes.
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }
        val repository = if (BuildConfig.API_BASE_URL.isBlank()) {
            DemoInventoryRepository()
        } else {
            NetworkInventoryRepository(applicationContext, BuildConfig.API_BASE_URL)
        }
        setContent {
            YonkeTheme {
                YonkeApp(repository)
            }
        }
    }
}
