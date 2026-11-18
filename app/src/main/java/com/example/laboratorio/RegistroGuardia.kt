package com.example.laboratorio

import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegistroGuardia : AppCompatActivity() {

    private lateinit var etSoldado : EditText
    private lateinit var spRango : Spinner
    private lateinit var cbEstado : CheckBox
    private lateinit var btnGuardar: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_guardia)
        // 1. Vinculación de componentes gráficos
        etSoldado = findViewById(R.id.etSoldado)
        spRango = findViewById(R.id.spRango)
        cbEstado = findViewById(R.id.cbEstado)
        btnGuardar = findViewById(R.id.btnGuardarGuardia)

        // 2. Configuración del Spinner (0: Recluta, 1: Capitán, 2: Veterano)
        val rangos = arrayOf("Recluta", "Capitán", "Veterano")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, rangos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spRango.adapter = adapter

        // 3. Evento de guardado defensivo
        btnGuardar.setOnClickListener {
            val nombreEfectivo = etSoldado.text.toString().trim()
            val rangoSeleccionado = spRango.selectedItemPosition
            val estadoTurno = cbEstado.isChecked

            if(nombreEfectivo.isNotEmpty()) {
                guardarPreferencia(nombreEfectivo,rangoSeleccionado,estadoTurno)
                etSoldado.text.clear()
                Toast.makeText(this, "Datos guardados en SharedPreferences", Toast.LENGTH_SHORT).show()
            } else{
                etSoldado.error ="El nombre no puede estar vacio"
            }
        }



    }

    //Requisito 2: Leemos el valor guardado cada vez que la pantalla vuelve a estar visible
    override fun onResume() {
        super.onResume()
        cargarDatosGuardia()
    }

    private fun guardarPreferencia(soldado: String, rango: Int, estado: Boolean) {
        val prefs = getSharedPreferences("RegistroGuardia", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("clave_soldado", soldado)
        editor.putInt("clave_rango", rango)
        editor.putBoolean("clave_estado",estado)
        editor.apply() // Guarda de forma asíncrona evitando congelamientos de UI
    }

    private fun cargarDatosGuardia() {
        val prefs = getSharedPreferences("RegistroGuardia", Context.MODE_PRIVATE)
        val soldadoGuardado = prefs.getString("clave_soldado", "")
        val rangoGuardado = prefs.getInt("clave_rango", 0)
        val estadoGuardado = prefs.getBoolean("clave_estado", false)

        etSoldado.setText(soldadoGuardado)
        spRango.setSelection(rangoGuardado)
        cbEstado.isChecked = estadoGuardado
    }

}