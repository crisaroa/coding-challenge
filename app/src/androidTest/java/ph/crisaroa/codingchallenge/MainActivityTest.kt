package ph.crisaroa.codingchallenge

import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun disableAnimations() {
        val scale = ScaleAnimation(1f, 1f, 1f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        scale.duration = 0
        val fade = AlphaAnimation(1f, 1f)
        fade.duration = 0
        val animation = AnimationSet(false)
        animation.addAnimation(scale)
        animation.addAnimation(fade)
        activityScenarioRule.scenario.onActivity { activity ->
            activity.window.decorView.animation = animation
        }
    }

    @Test
    fun clickClearButton_clearsInputFields() {
        // Fill in the input fields with some text
        onView(withId(R.id.tie_colors_quantity)).perform(typeText("3"))
        onView(withId(R.id.tie_bulbs_per_color)).perform(typeText("5"))
        onView(withId(R.id.tie_bulbs_to_pick)).perform(typeText("7"))
        onView(withId(R.id.tie_simulations_count)).perform(typeText("1000"))

        // Click the "Clear Fields" button
        onView(withId(R.id.btn_clear_fields)).perform(click())

        // Assert that the input fields are empty
        onView(withId(R.id.tie_colors_quantity)).check(matches(withText("")))
        onView(withId(R.id.tie_bulbs_per_color)).check(matches(withText("")))
        onView(withId(R.id.tie_total_bulbs)).check(matches(withText("")))
        onView(withId(R.id.tie_bulbs_to_pick)).check(matches(withText("")))
        onView(withId(R.id.tie_simulations_count)).check(matches(withText("")))
    }
}