package com.example.tarea_crudrealtime_articulos

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tarea_crudrealtime_articulos.adapters.TiendaAdapter
import com.example.tarea_crudrealtime_articulos.databinding.ActivityPrincipalBinding
import com.example.tarea_crudrealtime_articulos.models.ArticuloModel
import com.example.tarea_crudrealtime_articulos.providers.TiendaProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PrincipalActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPrincipalBinding
    var adapter = TiendaAdapter(
        mutableListOf(),
        {item -> borrar(item)},
        {item -> editar(item)})
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("tienda")


        setListeners()
        setRecycler()
        setMenuLateral()
    }

    private fun setMenuLateral() {
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item_borrarTodo -> {
                    borrarTodo()
                    binding.main.closeDrawer(GravityCompat.START)
                    true
                }

                R.id.item_logout -> {
                    auth.signOut()
                    finish()
                    true
                }

                R.id.item_salir -> {
                    finishAffinity()
                    true
                }

                else -> {false}
            }
        }
    }

    private fun borrarTodo() {

        //confirmacion
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¿Seguro que quieres borrar todo?")
        builder.setMessage("Esta acción no se puede deshacer")
        builder
            .setPositiveButton("Sí") { _, _ ->
                database.removeValue().addOnCompleteListener() {
                    if (it.isSuccessful){
                        Toast.makeText(this, "Borrado correctamente", Toast.LENGTH_SHORT).show()
                        recuperarDatosTienda()
                    }
                    else{
                        Toast.makeText(this, "Error al borrar", Toast.LENGTH_SHORT).show()
                    }
                }

            }
            .setNegativeButton("No", null)
        builder.create()
        builder.show()
    }

    private fun setRecycler() {
        var layoutManager = LinearLayoutManager(this)
        binding.rvTienda.layoutManager = layoutManager
        binding.rvTienda.adapter = adapter

        recuperarDatosTienda()
    }

    private fun recuperarDatosTienda() {
        val tiendaProvider = TiendaProvider()
        tiendaProvider.getDatos { todosLosRegistros ->
            binding.ivListaArticulos.visibility = if (todosLosRegistros.isEmpty()) View.VISIBLE else View.INVISIBLE
            adapter.lista = todosLosRegistros
            adapter.notifyDataSetChanged()
        }
    }


    private fun setListeners() {
        binding.fabArticulo.setOnClickListener {
            irActivityAdd()
        }
    }

    private fun irActivityAdd(bundle: Bundle ?= null) {
        val i = Intent(this, AddActivity::class.java)
        if(bundle != null){
            i.putExtras(bundle)
        }
        startActivity(i)
    }

    private fun borrar (item : ArticuloModel){
        database.orderByChild("nombre").equalTo(item.nombre)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        database.child(snapshot.children.first().key.toString()).removeValue()
                            .addOnSuccessListener {
                                val position = adapter.lista.indexOf(item)
                                if(position != -1){
                                    adapter.lista.removeAt(position)
                                    adapter.notifyItemRemoved(position)
                                }
                                Toast.makeText(this@PrincipalActivity, "Borrado correctamente", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener{
                                Toast.makeText(this@PrincipalActivity, "Error al borrar", Toast.LENGTH_SHORT).show()
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Error", error.message)
                }
            })
    }

    private fun editar (item : ArticuloModel){
        val bundle = Bundle().apply {
            putSerializable("ITEM", item)
        }
        irActivityAdd(bundle)
    }

    override fun onResume() {
        super.onResume()
        recuperarDatosTienda()
    }
}