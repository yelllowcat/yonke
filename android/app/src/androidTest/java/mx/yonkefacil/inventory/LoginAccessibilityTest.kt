package mx.yonkefacil.inventory

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasScrollToIndexAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import org.junit.Rule
import org.junit.Test

class LoginAccessibilityTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private fun signInAsAna() {
        composeRule.onNode(hasText("Ana") and hasClickAction()).performClick()
        listOf("1", "2", "3", "4").forEach { digit ->
            composeRule.onNode(hasText(digit) and hasClickAction()).performClick()
        }
        composeRule.onNodeWithText("Entrar al patio").performClick()
        composeRule.waitForIdle()
    }

    @Test
    fun loginStartsWithPlainStaffChoiceAndKeypad() {
        composeRule.onNodeWithText("¿Quién entra al turno?").assertIsDisplayed()
        composeRule.onNodeWithText("Ana").assertIsDisplayed()
        composeRule.onNodeWithText("David (Dueño)").assertIsDisplayed()
        composeRule.onNodeWithText("Entrar al patio").assertIsNotEnabled()
    }

    @Test
    fun staffCanReachInventoryAndDailyActions() {
        signInAsAna()

        composeRule.onNodeWithText("Piezas activas").assertIsDisplayed()
        composeRule.onNodeWithText("Autos en patio").assertIsDisplayed()
        composeRule.onNodeWithText("Inicio").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("¿Qué necesitas hacer?").assertIsDisplayed()
        composeRule.onNodeWithText("Recibir vehículo").assertIsDisplayed()
        composeRule.onNodeWithText("Agregar pieza").assertIsDisplayed()
        composeRule.onNodeWithText("Buscar").assertIsDisplayed()
        composeRule.onNodeWithText("Vender").assertIsDisplayed()
    }

    @Test
    fun publishingPartRequiresPhoto() {
        signInAsAna()
        composeRule.onNodeWithText("Cámara").performClick()
        composeRule.waitForIdle()

        composeRule.onNode(hasScrollToIndexAction()).performScrollToIndex(5)
        composeRule.onNodeWithText("Mostrar en el catálogo").assertIsDisplayed()
        composeRule.onNode(isToggleable()).performClick()
        composeRule.onNode(hasScrollToIndexAction()).performScrollToIndex(1)
        composeRule.onNodeWithText("Guardar").assertIsNotEnabled()
    }
}
