package com.example.tarea_crudrealtime_articulos.providers

import com.example.tarea_crudrealtime_articulos.models.ArticuloModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TiendaProvider {
    private val database = FirebaseDatabase.getInstance().getReference("tienda")
    fun getDatos (datosTienda : (MutableList<ArticuloModel>) -> Unit){
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = mutableListOf<ArticuloModel>()
                for (item in snapshot.children){
                    val tienda = item.getValue(ArticuloModel::class.java)
                    lista.add(tienda!!)
                }
                datosTienda(lista)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error al obtener los datos ${error.message}")
            }

        })
    }
}