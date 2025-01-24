package com.example.tarea_crudrealtime_articulos

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tarea_crudrealtime_articulos.databinding.ActivityAddBinding
import com.example.tarea_crudrealtime_articulos.models.ArticuloModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.UUID

class AddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBinding

    private var nombre = ""
    private var descripcion = ""
    private var precio = 0F
    private var id = ""

    private lateinit var db: DatabaseReference

    private var edit = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recogerExtras()

        if (edit) {
            binding.tvTitulo.text = "Editar"
            binding.btnAnadir.text = "Editar"
            binding.etNombre.isEnabled = false
        }

        db = FirebaseDatabase.getInstance().getReference("tienda")

        setListeners()
    }

    private fun recogerExtras() {
        if (intent.extras != null) {
            val bundle = intent.extras
            edit = true
            var articulo: ArticuloModel? = null
            if (bundle != null) {
                articulo = bundle.getSerializable("ITEM") as ArticuloModel
            }
            binding.etNombre.setText(articulo?.nombre)
            binding.etDescripcion.setText(articulo?.descripcion)
            binding.etPrecio.setText(articulo?.precio.toString())
        }
    }


    private fun setListeners() {
        binding.btnCancelar.setOnClickListener {
            finish()
        }
        binding.btnAnadir.setOnClickListener {
            addItem()

        }
    }

    private fun addItem() {
        if (!datosOk()) return

        if (!edit) {
            id = UUID.randomUUID().toString().replace("-", "")
        }

        val item = ArticuloModel(nombre, descripcion, precio)

        db.orderByChild("nombre").equalTo(nombre)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists() && !edit) {
                        Toast.makeText(
                            this@AddActivity,
                            "Ya existe el artículo",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (snapshot.exists() && edit){
                        val id = snapshot.children.first().key.toString()
                        db.child(id).setValue(item)
                        Toast.makeText(
                            this@AddActivity,
                            "Añadido correctamente",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        db.child(id).setValue(item)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this@AddActivity,
                                    "Añadido correctamente",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    this@AddActivity,
                                    "Error al añadir",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Error", error.message)
                }
            })
    }

    private fun datosOk(): Boolean {
        nombre = binding.etNombre.text.trim().toString().trim()
        descripcion = binding.etDescripcion.text.trim().toString().trim()
        precio = binding.etPrecio.text.trim().toString().toFloat()

        if (nombre.isEmpty() || nombre.length < 3) {
            binding.etNombre.error = "Nombre inválido"
            return false
        }
        if (descripcion.isEmpty() || descripcion.length < 10) {
            binding.etDescripcion.error = "Descripcion incorrecta"
            return false
        }
        if (precio <= 0 || precio.toString().isEmpty()) {
            binding.etPrecio.error = "Precio incorrecto"
            return false
        }
        return true


    }


}