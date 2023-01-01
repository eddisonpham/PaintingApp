package com.example.paintingapp.model

class ToolsItem {
    private var icon: Int
    private var name: String
    constructor(icon: Int, name: String){
        this.icon=icon
        this.name=name
    }
    fun getIcon(): Int {
        return this.icon
    }
    fun setIcon(icon: Int){
        this.icon=icon
    }
    fun getName(): String {
        return this.name
    }
    fun setName(name: String){
        this.name=name
    }
}