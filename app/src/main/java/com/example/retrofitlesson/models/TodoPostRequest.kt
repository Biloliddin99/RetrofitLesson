package com.example.retrofitlesson.models

data class TodoPostRequest(
    val bajarildi: Boolean,
    val batafsil: String,
    val oxirgi_muddat: String,
    val sarlavha: String,
    val zarurlik: String
)