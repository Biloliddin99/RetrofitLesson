package com.example.retrofitlesson.models

data class TodoGetResponse(
    val bajarildi: Boolean,
    val batafsil: String,
    val id: Int,
    var oxirgi_muddat: String,
    val sana: String,
    val sarlavha: String,
    val zarurlik: String
)