package com.aman.foodordering.repo

import androidx.lifecycle.LiveData
import com.aman.foodordering.room.entity.Food
import io.reactivex.Single

interface OrderRepoI {
    fun update(food: Food): Single<String>

    fun getAllItem(): Single<LiveData<List<Food>>>
}