package com.ramonpsatu.studyorganizer.features.collections.listeners


interface ToggleClickListener {

    fun updateToggle(isCompleted: Int, itemId: String, adapterPosition: Int)

}