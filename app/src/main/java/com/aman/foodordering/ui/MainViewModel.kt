package com.aman.foodordering.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aman.foodordering.repo.OrderRepoI
import com.aman.foodordering.room.entity.Food
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainViewModel(private val orderRepoI: OrderRepoI): ViewModel() {

    var observableState: MutableLiveData<MainState> = MutableLiveData()

    lateinit var observableList: LiveData<List<Food>>

    private val compositeDisposable = CompositeDisposable()

    private var state = MainState()
        set(value) {
            field = value
            publishState(value)
        }

    fun updateItem(food: Food) {
        compositeDisposable.add(
            orderRepoI.update(food)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        )
    }

    fun getOrderList() {
        state = state.copy(loading = true)
        compositeDisposable.add(
            orderRepoI.getAllItem()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    observableList = it
                    state = state.copy(loading = false, success = true, failure = false)
                },{
                    state = state.copy(
                        loading = false,
                        failure = true,
                        success = false,
                        message = it.localizedMessage)
                })
        )
    }

    private fun publishState(state: MainState) {
        Log.d(TAG," >>> Publish State : $state")
        observableState.postValue(state)
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, " >>> Clearing compositeDisposable")
        compositeDisposable.clear()
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}