package com.example.covid_19tracker

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class HealthStateActivity: AppCompatActivity() {

    private lateinit var showedSymptoms: Switch
    private lateinit var positiveExam: Switch
    private lateinit var metInfected: Switch
    private lateinit var cured: Switch
    private lateinit var calendar: Calendar
    private lateinit var showedSymptomsEditText: EditText
    private lateinit var metInfectedEditText: EditText
    private lateinit var positiveExamEditText: EditText

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
    }

    fun selectDate(view: View){
        val textView:EditText = view as EditText
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView(textView)
            }
        DatePickerDialog(this@HealthStateActivity,
            dateSetListener,
            // set DatePickerDialog to point to today's date when it loads up
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    fun saveInformation(view: View){
        val showedSymptoms: Switch = findViewById(R.id.showed_symptoms_switch)
        val cured: Switch  = findViewById(R.id.cured_switch)
        val metInfected: Switch = findViewById(R.id.met_infected_switch)
        val positiveExam: Switch = findViewById(R.id.positive_exam_switch)
        val errorMessage = "Este campo deve ser preenchido!"

        if(showedSymptoms.isChecked){
            if(showedSymptomsEditText.text.isNullOrBlank()){
                showedSymptomsEditText.error = errorMessage
            }
        }
        if(metInfected.isChecked){
            if(metInfectedEditText.text.isNullOrBlank()){
                metInfectedEditText.error = errorMessage
            }
        }
        if(positiveExam.isChecked){
            if(positiveExamEditText.text.isNullOrBlank()){
                positiveExamEditText.error = errorMessage
            }
        }

        // TODO: Save the helth state somewhere
    }

    private fun updateDateInView(editText:EditText) {
        val myFormat = "dd/MM/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        editText!!.setText(sdf.format(calendar.time))
    }

}