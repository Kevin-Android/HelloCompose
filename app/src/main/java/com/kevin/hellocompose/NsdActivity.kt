package com.kevin.hellocompose

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import com.kevin.hellocompose.ui.theme.HelloComposeTheme
import java.net.ServerSocket


val TAG = "NsdActivity666666"
var mServiceName: String? = null
var nsdManager: NsdManager? = null
var mLocalPort: Int? = 0
private val intentFilter = IntentFilter()
private lateinit var channel: WifiP2pManager.Channel
private lateinit var manager: WifiP2pManager
var receiver: BroadcastReceiver? = null


class NsdActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HelloComposeTheme {
                Surface(color = MaterialTheme.colors.background) {
                    Greeting("Android")
                }
            }
        }
        // 表示 Wi-Fi P2P 状态发生变化。
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        // 指示可用对等点列表中的更改。
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        // 表示 Wi-Fi P2P 连接状态已更改。
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        // 表示此设备的详细信息已更改。
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)

        manager = getSystemService(WIFI_P2P_SERVICE) as WifiP2pManager
        channel = manager.initialize(this, mainLooper, null)
    }


    /** 使用要匹配的意图值注册 BroadcastReceiver  */
    override fun onResume() {
        super.onResume()
        receiver = WifiP2pReceiver(manager, channel, this@NsdActivity)
        registerReceiver(receiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }


    fun initializeServerSocket() {
        // 在下一个可用端口上初始化服务器套接字。
        ServerSocket(0).also { socket ->
            // 存储选择的端口。
            mLocalPort = socket.localPort
        }
    }


    fun registerService(port: Int) {
        // 创建 NsdServiceInfo 对象，并填充它。
        val serviceInfo = NsdServiceInfo().apply {
            // 名称可能会根据冲突而更改
            // 与在同一网络上宣传的其他服务。
            serviceName = "NsdChat"
            serviceType = "_nsdchat._tcp"
            setPort(port)
        }

        nsdManager = (getSystemService(NSD_SERVICE) as NsdManager).apply {
            registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener)
        }

    }

    private val registrationListener = object : NsdManager.RegistrationListener {

        override fun onServiceRegistered(NsdServiceInfo: NsdServiceInfo) {
            // 保存服务名称。
            // Android 可能已更改它以解决冲突，
            // 因此请使用 Android 实际使用的名称更新您最初请求的名称。
            mServiceName = NsdServiceInfo.serviceName
            Log.d(TAG, "注册成功$mServiceName")
            nsdManager?.discoverServices(
                "_http._tcp.",
                NsdManager.PROTOCOL_DNS_SD,
                discoveryListener
            )


        }

        override fun onRegistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
            // 注册失败！将调试代码放在这里以确定原因。
            Log.d(TAG, "注册失败")

        }

        override fun onServiceUnregistered(arg0: NsdServiceInfo) {
            // 服务已注销。这仅在您调用 NsdManager.unregisterService() 并传入此侦听器时发生。
            Log.d(TAG, "服务已注销")

        }

        override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
            // 注销失败。将调试代码放在这里以确定原因。
            Log.d(TAG, "注销失败")

        }
    }

    @Composable
    fun Greeting(name: String) {
        Row {
            Text(text = "Hello $name!")
            Button(
                onClick = {
                    manager.discoverPeers(channel, object : WifiP2pManager.ActionListener {

                        override fun onSuccess() {
                            Log.i(TAG,"此处为发现启动成功时的代码")
                            // 此处为发现启动成功时的代码。
                            // 实际上尚未发现任何服务，因此此方法通常可以留空。
                            // 对等点发现的代码位于 onReceive 方法中，详述如下。
                        }

                        override fun onFailure(reasonCode: Int) {
                            Log.i(TAG,"此处为发现启动失败时的代码: $reasonCode")
                            // 此处为发现启动失败时的代码。
                            // 提醒用户出现问题。
                        }
                    })
                }
            ) {
                Text("开启")
            }
        }
    }


}


// 实例化一个新的 DiscoveryListener
private val discoveryListener = object : NsdManager.DiscoveryListener {

    // 服务发现开始后立即调用。
    override fun onDiscoveryStarted(regType: String) {
        Log.d(TAG, "服务发现开始$regType")
    }

    override fun onServiceFound(service: NsdServiceInfo) {
        // 找到了一个服务！用它做点什么。
        Log.d(TAG, "服务发现成功$service")
//        when {
//            service.serviceType != SERVICE_TYPE ->
//                // 服务类型是包含协议和此服务的传输层。
//                Log.d(TAG, "未知的服务类型: ${service.serviceType}")
//            service.serviceName == mServiceName ->
//                // 服务的名称告诉用户他们将连接到什么。它可能是“鲍勃的聊天应用程序”。
//                Log.d(TAG, "同一台机器: $mServiceName")
//            service.serviceName.contains("NsdChat") -> nsdManager.resolveService(
//                service,
//                resolveListener
//            )
//        }
    }

    override fun onServiceLost(service: NsdServiceInfo) {
        // 当网络服务不再可用时。内部簿记代码放在这里。
        Log.e(TAG, "服务丢失: $service")
    }

    override fun onDiscoveryStopped(serviceType: String) {
        Log.i(TAG, "Discovery stopped: $serviceType")
    }

    override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
        Log.e(TAG, "Discovery failed: Error code:$errorCode")
        nsdManager?.stopServiceDiscovery(this)
    }

    override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
        Log.e(TAG, "Discovery failed: Error code:$errorCode")
        nsdManager?.stopServiceDiscovery(this)
    }
}


//private fun startRegistration() {
//    //  创建包含有关您的服务的信息的字符串映射。
//    val record: Map<String, String> = mapOf(
//        "listenport" to SERVER_PORT.toString(),
//        "buddyname" to "John Doe${(Math.random() * 1000).toInt()}",
//        "available" to "visible"
//    )
//
//    // 服务信息。向它传递一个实例名称、
//    // 服务类型 _protocol._transportlayer ，
//    // 以及包含其他设备一旦连接到这个设备所需的信息的地图。
//    val serviceInfo =
//        WifiP2pDnsSdServiceInfo.newInstance("_test", "_presence._tcp", record)
//
//    // 添加本地服务，发送将用于指示请求成功或失败的服务信息、网络通道和侦听器。
//    manager.addLocalService(channel, serviceInfo, object : WifiP2pManager.ActionListener {
//        override fun onSuccess() {
//            // Command successful! Code isn't necessarily needed here,
//            // Unless you want to update the UI or add logging statements.
//        }
//
//        override fun onFailure(arg0: Int) {
//            // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
//        }
//    })
//}

