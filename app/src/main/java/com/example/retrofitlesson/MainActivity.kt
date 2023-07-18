package com.example.retrofitlesson

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.retrofitlesson.adapters.MyTodoAdapter
import com.example.retrofitlesson.databinding.ActivityMainBinding
import com.example.retrofitlesson.databinding.ItemDialogBinding
import com.example.retrofitlesson.models.TodoGetResponse
import com.example.retrofitlesson.models.TodoPostRequest
import com.example.retrofitlesson.network.ApiClient
import com.example.retrofitlesson.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class MainActivity : AppCompatActivity(), MyTodoAdapter.RvAction {

    private lateinit var binding: ActivityMainBinding
    private lateinit var apiService: ApiService
    private lateinit var myTodoAdapter: MyTodoAdapter
    private lateinit var list: ArrayList<TodoGetResponse>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        list = ArrayList()
        apiService = ApiClient.getRetrofitService(this)

        binding.btnAdd.setOnClickListener {
            postData()
        }

        getData()

    }

    private fun getData() {
        apiService.getData().enqueue(object : Callback<List<TodoGetResponse>> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<List<TodoGetResponse>>,
                response: Response<List<TodoGetResponse>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    list.addAll(response.body()!!)
                    myTodoAdapter = MyTodoAdapter(list, this@MainActivity)
                    binding.myRv.adapter = myTodoAdapter

                }
            }

            override fun onFailure(call: Call<List<TodoGetResponse>>, t: Throwable) {
                Log.d("@getData", "onFailure: ${t.message}")
            }
        })
    }

    @SuppressLint("SuspiciousIndentation")
    override fun popupMenu(todoGetResponse: TodoGetResponse, position: Int, imageView: ImageView) {
        val popupMenu = PopupMenu(this, imageView)

        popupMenu.inflate(R.menu.my_popup_menu)

        popupMenu.setOnMenuItemClickListener {

            when (it.itemId) {
                R.id.menu_delete -> {
                    ApiClient.getRetrofitService(this).deleteTodo(todoGetResponse.id)
                        .enqueue(object : Callback<TodoGetResponse> {
                            override fun onResponse(
                                call: Call<TodoGetResponse>,
                                response: Response<TodoGetResponse>
                            ) {
                                if (response.isSuccessful) {
                                    list.removeAt(position)
                                    myTodoAdapter.notifyItemRemoved(position)
                                    Toast.makeText(this@MainActivity, "Deleted", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }

                            override fun onFailure(call: Call<TodoGetResponse>, t: Throwable) {

                                Toast.makeText(
                                    this@MainActivity,
                                    "Error ${t.message}",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        })
                }

                R.id.menu_edit -> {

                    //dialog

                    var todo = todoGetResponse
                    val dialog = AlertDialog.Builder(this).create()
                    val itemDialogBinding = ItemDialogBinding.inflate(layoutInflater)
                    dialog.setView(itemDialogBinding.root)

                    val zarurlikItems = arrayOf("shart", "foydali", "hayot_mamot", "tavsiya")
                    val myAdapter = ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_list_item_1,
                        zarurlikItems
                    )
                    myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    itemDialogBinding.spinnerZarurlik.adapter = myAdapter

                    itemDialogBinding.editSarlavha.setText(todoGetResponse.sarlavha)
                    itemDialogBinding.editSana.setText(todoGetResponse.sana)
                    itemDialogBinding.editBatafsil.setText(todoGetResponse.batafsil)
                    itemDialogBinding.checkBajarildi.isChecked = todoGetResponse.bajarildi
                    itemDialogBinding.btnSelectDate.text = todoGetResponse.oxirgi_muddat
                    val selectedItemPosition = zarurlikItems.indexOf(todoGetResponse.zarurlik)
                    itemDialogBinding.spinnerZarurlik.setSelection(selectedItemPosition)

                    itemDialogBinding.btnSelectDate.setOnClickListener {
                        val currentDate = Calendar.getInstance()
                        val year = currentDate.get(Calendar.YEAR)
                        val month = currentDate.get(Calendar.MONTH)
                        val day = currentDate.get(Calendar.DAY_OF_MONTH)

                        val datePicker = DatePickerDialog(
                            this,
                            { _, selectedYear, selectedMonth, selectedDay ->
                                val selectedDate =
                                    "$selectedYear-${selectedMonth + 1}-$selectedDay"
                                itemDialogBinding.btnSelectDate.text = selectedDate
                            },
                            year,
                            month,
                            day
                        )
                        datePicker.show()
                    }

                    itemDialogBinding.btnSave.setOnClickListener {
                        val bajarildi = itemDialogBinding.checkBajarildi.isChecked
                        val sana = itemDialogBinding.editSana.text.toString()
                        val sarlavha = itemDialogBinding.editSarlavha.text.toString()
                        val batafsil = itemDialogBinding.editBatafsil.text.toString()
                        val oxirgiSana = itemDialogBinding.btnSelectDate.text.toString()
                        val selectedItem = itemDialogBinding.spinnerZarurlik.selectedItem.toString()

                        todo = TodoGetResponse(
                            bajarildi,
                            batafsil,
                            todoGetResponse.id,
                            oxirgiSana,
                            sana,
                            sarlavha,
                            selectedItem
                        )
                        list[position] = todo
                        myTodoAdapter.notifyItemChanged(position)

                        ApiClient.getRetrofitService(this).editTodo(todoGetResponse.id, todo)
                            .enqueue(object : Callback<TodoGetResponse> {
                                override fun onResponse(
                                    call: Call<TodoGetResponse>,
                                    response: Response<TodoGetResponse>
                                ) {
                                    if (response.isSuccessful) {
                                         Toast.makeText(
                                            this@MainActivity,
                                            "Edited",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                                }

                                override fun onFailure(call: Call<TodoGetResponse>, t: Throwable) {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Error ${t.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            })

                        dialog.cancel()

                    }

                    dialog.show()
                }
            }
            true
        }
        popupMenu.show()

    }

    private fun postData() {
        val dialog = AlertDialog.Builder(this).create()
        val itemDialogBinding = ItemDialogBinding.inflate(layoutInflater)
        dialog.setView(itemDialogBinding.root)
        itemDialogBinding.editSana.visibility = View.GONE
        val zarurlikItems = arrayOf("shart", "foydali", "hayot_mamot", "tavsiya")
        val myAdapter =
            ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, zarurlikItems)
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        itemDialogBinding.spinnerZarurlik.adapter = myAdapter

        itemDialogBinding.btnSelectDate.setOnClickListener {
            val currentDate = Calendar.getInstance()
            val year = currentDate.get(Calendar.YEAR)
            val month = currentDate.get(Calendar.MONTH)
            val day = currentDate.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate =
                        "$selectedYear-${selectedMonth + 1}-$selectedDay"
                    itemDialogBinding.btnSelectDate.text = selectedDate
                },
                year,
                month,
                day
            )
            datePicker.show()
        }

        itemDialogBinding.btnSave.setOnClickListener {
            val todoGiveResponse = TodoPostRequest(
                itemDialogBinding.checkBajarildi.toString().toBoolean(),
                itemDialogBinding.editBatafsil.text.toString(),
                itemDialogBinding.btnSelectDate.text.toString(),
                itemDialogBinding.editSarlavha.text.toString(),
                itemDialogBinding.spinnerZarurlik.selectedItem.toString()
            )
            apiService.postData(todoGiveResponse)
                .enqueue(object : Callback<TodoGetResponse> {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onResponse(
                        call: Call<TodoGetResponse>,
                        response: Response<TodoGetResponse>
                    ) {
                        if (response.isSuccessful) {
                            val todo = response.body()!!
                            list.add(todo)
                            myTodoAdapter.notifyDataSetChanged()
                            Toast.makeText(this@MainActivity, "Added", Toast.LENGTH_SHORT)
                                .show()

                        }
                    }

                    override fun onFailure(call: Call<TodoGetResponse>, t: Throwable) {
                        Toast.makeText(
                            this@MainActivity,
                            "Error ${t.message}",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                })
            dialog.cancel()
        }
        dialog.show()
    }

}