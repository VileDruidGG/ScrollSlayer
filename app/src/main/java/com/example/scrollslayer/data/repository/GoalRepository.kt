package com.example.scrollslayer.data.repository

import com.example.scrollslayer.data.model.Goal
import com.example.scrollslayer.data.model.SocialUsage

class GoalRepository {

    fun getGoal(): Goal? {
        return Goal(
            id = "1",
            name = "Aprender francés",
            createdAt = System.currentTimeMillis()
        )
    }

}