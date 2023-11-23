package com.example.reply.test

import androidx.activity.ComponentActivity
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.assertAny
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.example.reply.R
import com.example.reply.data.local.LocalEmailsDataProvider
import com.example.reply.ui.ReplyApp
import org.junit.Rule
import org.junit.Test

class ReplyAppTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()
    private val stateRestorationTester = StateRestorationTester(composeTestRule)
    // warning: Property 'stateRestorationTester' could be private

    // private val thirdEmailBody = composeTestRule.activity.getString(LocalEmailsDataProvider.allEmails[2].body) // test running causes error
    // private val thirdEmailSubject = composeTestRule.activity.getString(LocalEmailsDataProvider.allEmails[2].subject) // test running causes error

    @Test
    fun compactDevice_verifyUsingBottomNavigation() {
        composeTestRule.setContent {
            ReplyApp(windowSize = WindowWidthSizeClass.Compact)
        }
        composeTestRule.onNodeWithTagForStringId(R.string.navigation_bottom).assertExists()
    }
    @Test
    fun mediumDevice_verifyUsingNavigationRail() {
        composeTestRule.setContent {
            ReplyApp(windowSize = WindowWidthSizeClass.Medium)
        }
        composeTestRule.onNodeWithTagForStringId(R.string.navigation_rail).assertExists()
    }
    @Test
    fun expandedDevice_verifyUsingNavigationDrawer() {
        composeTestRule.setContent {
            ReplyApp(windowSize = WindowWidthSizeClass.Expanded)
        }
        composeTestRule.onNodeWithTagForStringId(R.string.navigation_drawer).assertExists()
    }
    @Test
    fun compactDevice_selectedEmailRetained_AfterConfigChange() {
        // Setup compact window
        // val stateRestorationTester = StateRestorationTester(composeTestRule)
        stateRestorationTester.setContent {
            ReplyApp(windowSize = WindowWidthSizeClass.Compact)
        }
        // Assert given third email is displayed
        getNodeWithEmailBody(composeTestRule,2).assertIsDisplayed()

        // Open details page
        getNodeWithEmailSubject(composeTestRule,2).performClick()

        // Verify it shows the details page for correct email
        checkBackButtonExists(composeTestRule)
        getNodeWithEmailBody(composeTestRule,2).assertExists()

        // Simulate a config change
        stateRestorationTester.emulateSavedInstanceStateRestore()

        // Verify it still shows the details page for the same email
        checkBackButtonExists(composeTestRule)
        getNodeWithEmailBody(composeTestRule,2).assertExists()
    }

    @Test
    fun mediumDevice_selectedEmailRetained_afterConfigChange() {
        stateRestorationTester.setContent {
            ReplyApp(windowSize = WindowWidthSizeClass.Medium)
        }

        getNodeWithEmailBody(composeTestRule,2).assertIsDisplayed()

        getNodeWithEmailSubject(composeTestRule,2).performClick()

        checkBackButtonExists(composeTestRule)
        getNodeWithEmailBody(composeTestRule,2).assertExists()

        stateRestorationTester.emulateSavedInstanceStateRestore()

        checkBackButtonExists(composeTestRule)
        getNodeWithEmailBody(composeTestRule,2).assertExists()
    }

    @Test
    fun expandedDevice_selectedEmailRetained_afterConfigChange() {
        stateRestorationTester.setContent {
            ReplyApp(windowSize = WindowWidthSizeClass.Expanded)
        }

        getNodeWithEmailBody(composeTestRule,2).assertExists()

        getNodeWithEmailSubject(composeTestRule,2).performClick()

        checkNodeWithEmailBodyInDetailsSection(composeTestRule,2)

        stateRestorationTester.emulateSavedInstanceStateRestore()

        checkNodeWithEmailBodyInDetailsSection(composeTestRule,2)
    }

    // Helper function to check if the text in the details section and not in the email list
    private fun checkNodeWithEmailBodyInDetailsSection(
        composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<ComponentActivity>, ComponentActivity>, n: Int
    ): SemanticsNodeInteractionCollection {
        return composeTestRule.onNodeWithTagForStringId(R.string.details_screen).onChildren()
            .assertAny(hasAnyDescendant(hasText(
                composeTestRule.activity.getString(LocalEmailsDataProvider.allEmails[2].body)
            )))
    }

    // Helper function to get the Node of back button (content description as of back button)
    private fun checkBackButtonExists(
        composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<ComponentActivity>, ComponentActivity>
    ): SemanticsNodeInteraction {
        return composeTestRule.onNodeWithContentDescriptionForStringId(R.string.navigation_back).assertExists()
    }

    // Helper function to get the Node with the n-th email body
    private fun getNodeWithEmailBody(
        composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<ComponentActivity>, ComponentActivity>, n: Int
    ): SemanticsNodeInteraction {
        return composeTestRule.onNodeWithText(
            composeTestRule.activity.getString(LocalEmailsDataProvider.allEmails[n].body)
        )
    }

    // Helper function to get the Node with the n-th email subject
    private fun getNodeWithEmailSubject(
        composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<ComponentActivity>, ComponentActivity>, n: Int
    ): SemanticsNodeInteraction {
        return composeTestRule.onNodeWithText(
            composeTestRule.activity.getString(LocalEmailsDataProvider.allEmails[n].subject)
        )
    }
}