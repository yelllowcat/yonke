package mx.yonkefacil.inventory

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasScrollToIndexAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

class LoginAccessibilityTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun loginStartsWithPlainStaffChoice() {
        composeRule.onNodeWithText("¿Quién va a usar el teléfono?").assertIsDisplayed()
        composeRule.onNodeWithText("Ana").assertIsDisplayed()
        composeRule.onNodeWithText("David").assertIsDisplayed()
    }

    @Test
    fun staffCanReachDailyActionsWithLargeLabels() {
        composeRule.onNode(hasText("Ana") and hasClickAction()).performClick()
        composeRule.onNodeWithText("PIN").performTextInput("1234")
        composeRule.onNodeWithText("Entrar").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("¿Qué necesita hacer?").assertIsDisplayed()
        composeRule.onNodeWithText("Recibir vehículo").assertIsDisplayed()
        composeRule.onNodeWithText("Agregar pieza").assertIsDisplayed()
        composeRule.onNodeWithText("Buscar").assertIsDisplayed()
        composeRule.onNodeWithText("Vender").assertIsDisplayed()
    }

    @Test
    fun publishingPartRequiresPhoto() {
        composeRule.onNodeWithText("Ana").performClick()
        composeRule.onNodeWithText("PIN").performTextInput("1234")
        composeRule.onNodeWithText("Entrar").performClick()
        composeRule.waitForIdle()
        composeRule.onNode(hasText("Agregar pieza") and hasClickAction()).performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Vehículo de origen").assertIsDisplayed()
        composeRule.onNode(hasScrollToIndexAction()).performScrollToIndex(3)
        composeRule.onNodeWithText("Mostrar en el catálogo").assertIsDisplayed()
        composeRule.onNode(isToggleable()).performClick()
        composeRule.onNodeWithText("Guardar").assertIsNotEnabled()
    }
}
