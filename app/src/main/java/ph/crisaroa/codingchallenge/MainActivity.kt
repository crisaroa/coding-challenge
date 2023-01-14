package ph.crisaroa.codingchallenge

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.widget.doOnTextChanged
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), View.OnClickListener {
    //TextView declarations
    private lateinit var tvOutput: TextView
    private lateinit var tvBonusInfo: TextView

    //TextInputLayout declarations
    private lateinit var tilBulbColors: TextInputLayout
    private lateinit var tilBulbsPerColor: TextInputLayout
    private lateinit var tilTotalBulbs: TextInputLayout
    private lateinit var tilBulbsToPick: TextInputLayout
    private lateinit var tilSimulationRuns: TextInputLayout

    //MaterialButton declarations
    private lateinit var btnClear: MaterialButton
    private lateinit var btnRandomize: MaterialButton
    private lateinit var btnRun: MaterialButton

    //MaterialCardView declarations
    private lateinit var mcvBonusInfo: MaterialCardView

    //CircularProgressIndicator declarations
    private lateinit var pbLoading: CircularProgressIndicator

    //Double declarations
    private var confidence = 0.99
    private var averageOutput = 0.0

    //Integer declarations
    private var totalBulbs: Int? = 0
    private var bulbColors: Int? = 0
    private var bulbsPerColor: Int? = 0
    private var bulbsToPick: Int? = 0
    private var simRuns: Int? = 0

    //Class declarations
    private lateinit var bounds: Pair<Double, Double>
    private lateinit var simulation: Simulation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeVariables()
        initializeListeners()
        clearFields()
    }

    private fun initializeVariables() {
        //TextView initializations
        tvOutput = findViewById(R.id.tv_result)
        tvBonusInfo = findViewById(R.id.tv_bonus_info)

        //TextInputLayout initializations
        tilBulbColors = findViewById(R.id.til_colors_quantity)
        tilBulbsPerColor = findViewById(R.id.til_bulbs_per_color)
        tilTotalBulbs = findViewById(R.id.til_total_bulbs)
        tilBulbsToPick = findViewById(R.id.til_bulbs_to_pick)
        tilSimulationRuns = findViewById(R.id.til_simulations_count)

        //MaterialButton initializations
        btnClear = findViewById(R.id.btn_clear_fields)
        btnRandomize = findViewById(R.id.btn_randomize)
        btnRun = findViewById(R.id.btn_run_simulations)

        //MaterialCardView initializations
        mcvBonusInfo = findViewById(R.id.mcv_bonus_info)

        //CircularProgressIndicator initializations
        pbLoading = findViewById(R.id.pb_loading)
    }

    private fun initializeListeners() {
        //MaterialButton listeners
        btnClear.setOnClickListener(this)
        btnRandomize.setOnClickListener(this)
        btnRun.setOnClickListener(this)

        //TextInputLayout listeners
        tilBulbColors.editText?.doOnTextChanged { _, _, _, _ ->
            bulbColors = tilBulbColors.editText?.text.toString().toIntOrNull()
            bulbsPerColor = tilBulbsPerColor.editText?.text.toString().toIntOrNull()
            if (tilBulbsPerColor.editText?.text.toString().isNotBlank()
                && bulbColors != null
                && bulbsPerColor != null
            ) {
                tilTotalBulbs.editText?.setText((bulbColors!! * bulbsPerColor!!).toString())
            } else {
                tilTotalBulbs.editText?.setText("")
            }
        }
        tilBulbsPerColor.editText?.doOnTextChanged { _, _, _, _ ->
            bulbColors = tilBulbColors.editText?.text.toString().toIntOrNull()
            bulbsPerColor = tilBulbsPerColor.editText?.text.toString().toIntOrNull()
            if (tilBulbColors.editText?.text.toString().isNotBlank()
                && bulbColors != null
                && bulbsPerColor != null
            ) {
                tilTotalBulbs.editText?.setText((bulbColors!! * bulbsPerColor!!).toString())
            } else {
                tilTotalBulbs.editText?.setText("")
            }
        }
        tilBulbColors.editText?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                tilBulbsPerColor.editText?.selectAll()
            }
            false
        }
        tilBulbsPerColor.editText?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                tilBulbsToPick.editText?.selectAll()
            }
            false
        }
        tilBulbsToPick.editText?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                tilSimulationRuns.editText?.selectAll()
            }
            false
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_clear_fields -> {
                clearFields()
                tilBulbColors.editText?.requestFocus()
            }
            R.id.btn_randomize -> {
                val bc = kotlin.random.Random.nextInt(3, 100)
                val bpc = kotlin.random.Random.nextInt(3, 100)
                val btp = kotlin.random.Random.nextInt(2, sqrt((bc * bpc).toFloat()).toInt())
                val sr = kotlin.random.Random.nextInt(10, 10000)
                tilBulbColors.editText?.setText(bc.toString())
                tilBulbsPerColor.editText?.setText(bpc.toString())
                tilBulbsToPick.editText?.setText(btp.toString())
                tilSimulationRuns.editText?.setText(sr.toString())
            }
            R.id.btn_run_simulations -> {
                closeKeyboard()
                confidence = 0.99 //99%
                bulbColors = tilBulbColors.editText?.text.toString().toIntOrNull() //j
                bulbsPerColor = tilBulbsPerColor.editText?.text.toString().toIntOrNull() //k
                bulbsToPick = tilBulbsToPick.editText?.text.toString().toIntOrNull() //m
                simRuns = tilSimulationRuns.editText?.text.toString().toIntOrNull() //n
                totalBulbs = tilTotalBulbs.editText?.text.toString().toIntOrNull() // j*k

                if (areFieldsValid(bulbColors, bulbsPerColor, bulbsToPick, simRuns, totalBulbs)) {
                    try {
                        simulation = Simulation(
                            totalBulbs!!,
                            bulbColors!!,
                            bulbsPerColor!!,
                            bulbsToPick!!,
                            simRuns!!
                        )
                        // Code that continues to execute if no exception is thrown
                        averageOutput = 0.0
                        bounds = Pair(0.0, 0.0)
                        runBlocking {
                            launch {
                                averageOutput = simulation.repeatPickColors()
                                bounds = simulation.generateBounds(confidence, averageOutput)
                            }
                        }
                        showOutput()
                        tvOutput.text = "The average output of the simulation after $simRuns " +
                                "simulations is: ${"%.2f".format(averageOutput).toDouble()}. "
                        tvBonusInfo.text =
                            "The lower bound and the upper bound of the interval is " +
                                    "${"%.2f".format(bounds.first).toDouble()} and " +
                                    "${"%.2f".format(bounds.second).toDouble()}, " +
                                    "respectively, with a ${confidence * 100}% confidence.\n\n" +
                                    "The runtime notation of my implementation is O(n*m) as it needs to run " +
                                    "the simulation n number of times and in each iteration, it is " +
                                    "selecting m number of light bulbs from the bucket."

                        //generateChart(bounds.first, bounds.second, averageOutput)
                    } catch (e: IllegalArgumentException) {
                        // Code that handles the exception
                        Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Invalid input: Fill up all the fields.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun areFieldsValid(
        bulbColors: Int?,
        bulbsPerColor: Int?,
        bulbsToPick: Int?,
        simRuns: Int?,
        totalBulbs: Int?
    ): Boolean {
        //Return true if all the necessary variables have a valid integer value, otherwise return false
        return bulbColors != null && bulbsPerColor != null && bulbsToPick != null && simRuns != null && totalBulbs != null
    }

    private fun closeKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun clearFields() {
        tilBulbColors.editText?.setText("")
        tilBulbsPerColor.editText?.setText("")
        tilBulbsToPick.editText?.setText("")
        tilSimulationRuns.editText?.setText("")
        tilTotalBulbs.editText?.setText("")
        tvOutput.text = ""
        hideOutput()
        pbLoading.visibility = View.GONE
    }

    private fun showOutput() {
        mcvBonusInfo.visibility = View.VISIBLE
    }

    private fun hideOutput() {
        mcvBonusInfo.visibility = View.GONE
    }
}