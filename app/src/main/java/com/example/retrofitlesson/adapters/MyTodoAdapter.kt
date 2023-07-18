package com.example.retrofitlesson.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.retrofitlesson.databinding.ItemRvBinding
import com.example.retrofitlesson.models.TodoGetResponse

class MyTodoAdapter(private val list: ArrayList<TodoGetResponse>, val rvAction: RvAction) : RecyclerView.Adapter<MyTodoAdapter.Vh>() {

    inner class Vh(private val itemRvBinding: ItemRvBinding) :
        RecyclerView.ViewHolder(itemRvBinding.root) {

        fun onBind(todoGetResponse: TodoGetResponse, position: Int) {
            itemRvBinding.tvId.text = todoGetResponse.id.toString()
            itemRvBinding.tvSarlavha.text = todoGetResponse.sarlavha
            itemRvBinding.tvSana.text = todoGetResponse.sana
            itemRvBinding.tvBajarildi.text = todoGetResponse.bajarildi.toString()
            itemRvBinding.tvBatafsil.text = todoGetResponse.batafsil
            itemRvBinding.tvZarurlink.text = todoGetResponse.zarurlik
            itemRvBinding.tvOxirgiMuddat.text = todoGetResponse.oxirgi_muddat

            itemRvBinding.imageMore.setOnClickListener {
                rvAction.popupMenu(todoGetResponse, position,itemRvBinding.imageMore)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemRvBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position],position)
    }

    interface RvAction{
        fun popupMenu(todoGetResponse: TodoGetResponse,position: Int,imageView: ImageView)
    }

}