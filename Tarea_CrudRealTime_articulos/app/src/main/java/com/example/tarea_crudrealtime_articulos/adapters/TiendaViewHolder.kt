package com.example.tarea_crudrealtime_articulos.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.tarea_crudrealtime_articulos.databinding.ArticuloLayoutBinding
import com.example.tarea_crudrealtime_articulos.models.ArticuloModel

class TiendaViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    var binding = ArticuloLayoutBinding.bind(v)

    fun render(item: ArticuloModel, onClickBorrar: (ArticuloModel) -> Unit, onClickEditar: (ArticuloModel) -> Unit){
        binding.tvNombre.text = item.nombre
        binding.tvDescripcion.text = item.descripcion
        binding.tvPrecio.text = item.precio.toString()

        binding.btnBorrar.setOnClickListener{
            onClickBorrar(item)
        }
        binding.btnEditar.setOnClickListener{
            onClickEditar(item)
        }

    }
}