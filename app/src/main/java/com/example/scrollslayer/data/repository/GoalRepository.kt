package com.example.scrollslayer.data.repository

import com.example.scrollslayer.data.model.Goal

class GoalRepository {

    fun getGoal(): Goal? {
        return Goal(
            id = "1",
            name = "Aprender francés",
            createdAt = System.currentTimeMillis()
        )
    }

}