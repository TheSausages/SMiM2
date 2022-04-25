package project.dwa.activities

import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase
import org.hamcrest.core.AllOf.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import project.dwa.R
import project.dwa.models.Player

@RunWith(AndroidJUnit4::class)
internal class MainMenuActivityTest : TestCase() {
    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(MainMenuActivity::class.java)

    @Before
    fun prepareForTest() {
        Intents.init()
    }

    @After
    fun cleanAfterTest() {
        Intents.release()
    }

    @Test
    fun test_ifMachineButtonClicked_StartScalableBoardActivity() {
        //given
        val expectedSize = 10
        val expectedElementsToWin = 5
        val expectedPLayerList = arrayListOf(
            Player.createNormalPlayer("Player 1", R.drawable.x),
            Player.createMachinePlayer()
        )

        //When
        launchActivity<MainMenuActivity>().use {
            onView(withId(R.id.play_machine_button_id))
                .perform(click())
        }

        //then
        intended(allOf(
            hasComponent(ScalableBoardActivity::class.java.name),
            hasExtra("size", expectedSize),
            hasExtra("elementsToWin", expectedElementsToWin),
            hasExtra("players", expectedPLayerList)
        ))
    }

    @Test
    fun test_ifPlayerButtonClicked_StartScalableBoardActivity() {
        //given
        val expectedSize = 10
        val expectedElementsToWin = 5
        val expectedPLayerList = arrayListOf(
            Player.createNormalPlayer("Player 1", R.drawable.x),
            Player.createNormalPlayer("Player 2", R.drawable.o)
        )

        //When
        launchActivity<MainMenuActivity>().use {
            onView(withId(R.id.play_another_button_id))
                .perform(click())
        }

        //then
        intended(allOf(
            hasComponent(ScalableBoardActivity::class.java.name),
            hasExtra("size", expectedSize),
            hasExtra("elementsToWin", expectedElementsToWin),
            hasExtra("players", expectedPLayerList)
        ))
    }

    @Test
    fun test_ifHistoryButtonClicked_StartHistoryActivity() {
        //given

        //When
        launchActivity<MainMenuActivity>().use {
            onView(withId(R.id.see_history_button_id))
                .perform(click())
        }

        //then
        intended(allOf(
            hasComponent(SeeHistoryActivity::class.java.name)
        ))
    }

    @Test
    fun test_ifExitButtonClicked_ExitApp() {
        //given

        //When
        launchActivity<MainMenuActivity>().use {
            onView(withId(R.id.see_history_button_id))
                .perform(click())
        }

        //then
        intended(allOf(
            hasComponent(SeeHistoryActivity::class.java.name)
        ))
    }
}