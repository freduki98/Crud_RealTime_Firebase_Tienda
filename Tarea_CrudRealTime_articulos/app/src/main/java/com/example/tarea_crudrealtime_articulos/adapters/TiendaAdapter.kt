package com.example.tarea_crudrealtime_articulos.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tarea_crudrealtime_articulos.R
import com.example.tarea_crudrealtime_articulos.models.ArticuloModel

class TiendaAdapter(
    var lista: MutableList<ArticuloModel>,
    private var onClickBorrar: (ArticuloModel) -> Unit,
    private var onClickEditar: (ArticuloModel) -> Unit): RecyclerView.Adapter<TiendaViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TiendaViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.articulo_layout, parent, false)
        return TiendaViewHolder(v)
    }

    override fun onBindViewHolder(holder: TiendaViewHolder, position: Int) {
        holder.render(lista[position], onClickBorrar, onClickEditar)
    }

    override fun getItemCount(): Int {
        return lista.size

    }
}