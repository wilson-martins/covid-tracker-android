package com.example.covid_19tracker.ui

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.covid_19tracker.R
import com.example.covid_19tracker.common.SharedPreferenceKeys
import com.example.covid_19tracker.common.SharedPreferencesManager
import com.example.covid_19tracker.model.HealthState
import com.example.covid_19tracker.model.StatusHistory
import com.example.covid_19tracker.service.StatusHistoryService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class HealthStateActivity : AppCompatActivity() {

    private lateinit var showedSymptoms: Switch
    private lateinit var positiveExam: Switch
    private lateinit var metInfected: Switch
    private lateinit var cured: Switch
    private lateinit var showedSymptomsEditText: EditText
    private lateinit var metInfectedEditText: EditText
    private lateinit var positiveExamEditText: EditText

    private lateinit var calendar: Calendar
    private lateinit var statusDate: Date
    private lateinit var statusHistoryService: StatusHistoryService
    private val curFormater = SimpleDateFormat("dd/MM/yyyy", Locale.US)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_information_edit)
        showedSymptoms = findViewById(R.id.showed_symptoms_switch)
        positiveExam = findViewById(R.id.positive_exam_switch)
        metInfected = findViewById(R.id.met_infected_switch)
        cured = findViewById(R.id.cured_switch)
        calendar = Calendar.getInstance()
        showedSymptomsEditText = findViewById(R.id.start_symptoms_date)
        metInfectedEditText = findViewById(R.id.met_infected_date)
        positiveExamEditText = findViewById(R.id.diagnose_date)
        statusHistoryService = StatusHistoryService.create()
    }

    fun selectDate(view: View) {
        val textView: EditText = view as EditText
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView(textView)
            }
        DatePickerDialog(
            this@HealthStateActivity,
            dateSetListener,
            // set DatePickerDialog to point to today's date when it loads up
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun saveInformation(view: View) {
        if (!validateForm()) {
            return
        }

        var healthState = getHealthStateAndSetDate()
        val personId = SharedPreferencesManager.loadLong(SharedPreferenceKeys.PERSON_ID)

        if (personId != 0L) {

            val statusHistory = StatusHistory(
                statusDt = statusDate!!,
                value = healthState,
                personId = personId!!
            )
            statusHistoryService.addStatusHistory(statusHistory).enqueue(object :
                Callback<StatusHistory?> {
                override fun onFailure(call: Call<StatusHistory?>, t: Throwable) {
                    Toast.makeText(
                        this@HealthStateActivity,
                        "Oops something went wrong please check your internet connection",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onResponse(
                    call: Call<StatusHistory?>,
                    response: Response<StatusHistory?>
                ) {
                    if (response.body() != null && response.body()?.personId != 0L) {
                        SharedPreferencesManager.setLong(
                            SharedPreferenceKeys.PERSON_ID,
                            response.body()?.personId ?: 0
                        )
                        Toast.makeText(this@HealthStateActivity, "OK", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(
                            this@HealthStateActivity,
                            "Something went terribly wrong",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            })
        }
    }

    private fun validateForm(): Boolean {
        val errorMessage = "Este campo deve ser preenchido!"

        if (showedSymptoms.isChecked) {
            if (showedSymptomsEditText.text.isNullOrBlank()) {
                showedSymptomsEditText.error = errorMessage
                return false
            }
        }
        if (metInfected.isChecked) {
            if (metInfectedEditText.text.isNullOrBlank()) {
                metInfectedEditText.error = errorMessage
                return false
            }
        }
        if (positiveExam.isChecked) {
            if (positiveExamEditText.text.isNullOrBlank()) {
                positiveExamEditText.error = errorMessage
                return false
            }
        }
        return true
    }

    private fun getHealthStateAndSetDate(): HealthState {
        if (cured.isChecked) {
            statusDate = Date()
            return HealthState.CURED
        }
        if (positiveExam.isChecked) {
            statusDate = curFormater.parse(positiveExamEditText.text.toString())
            return HealthState.POSITIVE
        }
        if (showedSymptoms.isChecked) {
            statusDate = curFormater.parse(showedSymptomsEditText.text.toString())
            return HealthState.SYMPTOMATIC
        }
        if (metInfected.isChecked) {
            statusDate = curFormater.parse(metInfected.text.toString())
            return HealthState.POSSIBLY_INFECTED
        }
        statusDate = Date()
        return HealthState.ASYMPTOMATIC
    }

    private fun updateDateInView(editText: EditText) {
        val myFormat = "dd/MM/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        editText!!.setText(sdf.format(calendar.time))
    }

}