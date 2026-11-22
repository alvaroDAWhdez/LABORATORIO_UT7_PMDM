package com.example.laboratorio

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileNotFoundException

class PergaminoActivity : AppCompatActivity() {
    private lateinit var etNombreArchivo: EditText
    private lateinit var etContenidoPergamino: EditText

    private lateinit var btnGuardarInterno: Button
    private lateinit var btnLeerInterno: Button

    private lateinit var btnGuardarExterno: Button
    private lateinit var btnLeerExterno: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pergaminos)

        //Vinculamos los componentes graficos de nombre de fichero y contenido de el
        etNombreArchivo = findViewById(R.id.etNombreArchivo)
        etContenidoPergamino = findViewById(R.id.etContenidoPergamino)

        //Vinculamos componentes gráficos de la memoria privada
        btnGuardarInterno = findViewById(R.id.btnGuardarInterno)
        btnLeerInterno = findViewById(R.id.btnLeerInterno)

        //Operación de ESCRITURA en almacenamiento específico de la app
        btnGuardarInterno.setOnClickListener {
            val nombre = etNombreArchivo.text.toString().trim()
            val texto = etContenidoPergamino.text.toString()

            if (nombre.isNotEmpty()){
                try{
                    // MODE_PRIVATE garantiza aislamiento absoluto frente a otras apps
                    openFileOutput(nombre, Context.MODE_PRIVATE).use { flujoSalida ->
                        flujoSalida.write(texto.toByteArray())
                    }
                    Toast.makeText(this, "Pergamino sellado con éxito en files/", Toast.LENGTH_SHORT).show()
                    etContenidoPergamino.text.clear() // Limpiamos la consola tras guardar
                } catch (e: Exception) {
                    Toast.makeText(this, "Error al escribir en el disco: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                etNombreArchivo.error = "Especifica un nombre para el pergamino"
            }
        }

        //Operación de LECTURA desde almacenamiento específico de la app
        btnLeerInterno.setOnClickListener {
            val nombre = etNombreArchivo.text.toString().trim()

            if(nombre.isNotEmpty()){
                try {
                    // Abrimos el flujo de lectura y lo transformamos a texto plano de vuelta
                    val textoLeido = openFileInput(nombre).bufferedReader().use { lector ->
                        lector.readText()
                    }
                    etContenidoPergamino.setText(textoLeido)
                    Toast.makeText(this, "Pergamino leído correctamente", Toast.LENGTH_SHORT).show()
                } catch (e: FileNotFoundException) {
                    etNombreArchivo.error = "El archivo especificado no existe en la ciudadela"
                    Toast.makeText(this, "Fichero no encontrado", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(this, "Error de E/S: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                etNombreArchivo.error = "Especifica qué pergamino deseas consultar"
            }
        }


        //Vinculamos componentes gráficos de la memoria externa
        btnGuardarExterno = findViewById(R.id.btnGuardarExterno)
        btnLeerExterno = findViewById(R.id.btnLeerExterno)


        //Evento de Escritura en el espacio externo privado de la app
        btnGuardarExterno.setOnClickListener {
            val nombre = etNombreArchivo.text.toString().trim()
            val texto = etContenidoPergamino.text.toString()

            // Validación de arquitectura defensiva: Comprobación del Soporte Físico
            if (!esAlmacenamientoExternoEscribible()) {
                Toast.makeText(this, "Error: El almacenamiento externo no está montado", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if(nombre.isNotEmpty()){
                try{
                    val rutaDirectorio = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                    val archivoTarget = File(rutaDirectorio, nombre)

                    // Persistimos los datos directamente mediante la extensión de Kotlin
                    archivoTarget.writeText(texto)
                    Toast.makeText(this, "Pergamino guardado en: Android/data/.../Documents/", Toast.LENGTH_SHORT).show()
                    etContenidoPergamino.text.clear()

                } catch (e: Exception) {
                    Toast.makeText(this, "Error de hardware al escribir: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                etNombreArchivo.error = "Especifica un nombre para el pergamino"
            }

        }

        btnLeerExterno.setOnClickListener {
            val nombre = etNombreArchivo.text.toString().trim()

            // Validación defensiva para lectura en hilos físicos
            if (!esAlmacenamientoExternoLeible()) {
                Toast.makeText(this, "Error: Soporte no disponible para lectura", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (nombre.isNotEmpty()) {
                try {
                    val rutaDirectorio = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                    val archivoTarget = File(rutaDirectorio, nombre)
                    if (archivoTarget.exists()) {
                        val textoRecuperado = archivoTarget.readText()
                        etContenidoPergamino.setText(textoRecuperado)
                        Toast.makeText(this, "Pergamino cargado correctamente", Toast.LENGTH_SHORT).show()
                    }  else {
                        etNombreArchivo.error = "El plano especificado no existe en este sector"
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error al acceder al flujo de lectura: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                etNombreArchivo.error = "Especifica qué pergamino deseas consultar"
            }


        }


    }

    // Métodos auxiliares para la verificación física del estado del medio
    private fun esAlmacenamientoExternoEscribible(): Boolean {
        val estado = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == estado
    }

    private fun esAlmacenamientoExternoLeible(): Boolean {
        val estado = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == estado || Environment.MEDIA_MOUNTED_READ_ONLY == estado
    }

}