package org.bitizen.bitizenconnect_android_example

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.bitizen.connect.BitizenConnectApi
import org.bitizen.connect.BitizenConnectDelegate

class MainViewModel : ViewModel(), BitizenConnectDelegate {

    val connection = BitizenConnectApi(this)

    private val _connected = MutableLiveData<Boolean>()
    private val _chainId = MutableLiveData<Int?>()
    private val _account = MutableLiveData<List<String>>()

    val connected: LiveData<Boolean> = _connected
    val chainId: LiveData<Int?> = _chainId
    val account: LiveData<List<String>?> = _account

    override fun didConnect(chainId: Int?, accounts: List<String>?) {
        if(accounts==null){
            throw Exception()
        }
        _chainId.postValue(chainId)
        _account.postValue(accounts)
        _connected.postValue(true)
    }

    override fun didDisconnect() {
        _chainId.postValue(null)
        _account.postValue(null)
        _connected.postValue(false)
    }

    override fun failedToConnect() {
        _chainId.postValue(null)
        _account.postValue(null)
        _connected.postValue(false)
    }
}