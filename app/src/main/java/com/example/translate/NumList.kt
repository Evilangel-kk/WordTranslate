package com.example.translate

object NumList {
    private val numbers = mutableSetOf<Int>()

    // 添加数字到集合中
    fun addNumber(number: Int) {
        numbers.add(number)
    }

    // 获取集合中的所有数字
    fun getNumbers(): Set<Int> {
        return numbers
    }

    // 检查数字是否在集合中
    fun contains(number: Int): Boolean {
        return numbers.contains(number)
    }

    // 移除集合中的数字
    fun removeNumber(number: Int) {
        numbers.remove(number)
    }

    // 清空集合
    fun clearAll(){
        numbers.clear()
    }
}