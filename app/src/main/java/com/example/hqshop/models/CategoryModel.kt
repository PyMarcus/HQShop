package com.example.hqshop.models

sealed class CategoryModel(val category: String){
    data object Marvel: CategoryModel("marvel")
    data object DCUniverse: CategoryModel("dc")
    data object Mangas: CategoryModel("manga")
    data object Disney: CategoryModel("disney")
    data object TurmaDaMonica: CategoryModel("monica")
}
