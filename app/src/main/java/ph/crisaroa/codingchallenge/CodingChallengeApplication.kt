package ph.crisaroa.codingchallenge

import android.app.Application
import com.google.android.material.color.DynamicColors

class CodingChallengeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}