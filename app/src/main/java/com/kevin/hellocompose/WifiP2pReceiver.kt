package com.kevin.hellocompose

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log

/**
 *    @author : 王康
 *    @date   : 2021/12/13
 *    @desc   :
 */
class WifiP2pReceiver(
    manager: WifiP2pManager ?= null,
    channel: WifiP2pManager.Channel ?= null,
    nsdActivity: NsdActivity ?= null
) : BroadcastReceiver() {

    private var manager: WifiP2pManager? = null
    private var channel: WifiP2pManager.Channel? = null
    private var nsdActivity: NsdActivity? = null

    init {
        this.manager = manager
        this.channel = channel
        this.nsdActivity = nsdActivity
    }

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {

        when (intent.action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                // 确定是否启用了 Wifi P2P 模式，提醒 Activity。
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                Log.i(TAG,"确定是否启用了 Wifi P2P 模式，提醒 Activity :$state")
//                activity.isWifiP2pEnabled = state == WifiP2pManager.WIFI_P2P_STATE_ENABLED
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                Log.i(TAG,"同行名单已更改！我们或许应该为此做些什么。")
                manager?.requestPeers(channel, peerListListener)
                // 同行名单已更改！我们或许应该为此做些什么。

            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {

                // 连接状态改变！我们或许应该为此做些什么。
                Log.i(TAG,"连接状态改变！我们或许应该为此做些什么。")

            }
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                Log.i(TAG,"WIFI P 2 P 此设备已更改操作")

//                (activity.supportFragmentManager.findFragmentById(R.id.frag_list) as DeviceListFragment)
//                    .apply {
//                        updateThisDevice(
//                            intent.getParcelableExtra(
//                                WifiP2pManager.EXTRA_WIFI_P2P_DEVICE) as WifiP2pDevice
//                        )
//                    }
            }
        }
    }

    private val peers = mutableListOf<WifiP2pDevice>()

    private val peerListListener = WifiP2pManager.PeerListListener { peerList ->
        val refreshedPeers = peerList.deviceList
        if (refreshedPeers != peers) {
            peers.clear()
            peers.addAll(refreshedPeers)
            peers.forEach(action = {
                Log.i(TAG, it.toString())
            })
        }

        if (peers.isEmpty()) {
            Log.d(TAG, "No devices found")
            return@PeerListListener
        }
    }


}