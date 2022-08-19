package org.bitizen.bitizenconnect_android_example

import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import org.bitizen.bitizenconnect_android_example.databinding.ActivityMainBinding
import org.bitizen.connect.Client
import org.bitizen.connect.Session
import org.bitizen.connect.impls.*
import java.io.File
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()

            val okClient = OkHttpClient.Builder().pingInterval(1000, TimeUnit.MILLISECONDS).build()
            val moshi = Moshi.Builder().build()

            val client = BCClient(
                Session.PeerMeta(
                    name = "Example App",
                    url = "https://example.com",
                    description = "example app",
                    icons = listOf("https://bitizen.org/wp-content/uploads/2022/04/cropped-vi-192x192.png")
                ),
                MoshiPayloadAdapter(moshi),
                OkHttpTransport.Builder(okClient, moshi),
                { status ->
                    println("onStatus $status")
                },
                { call ->
                    println("onCall $call")
                }
            )

            val wsURL = client.connect(
                Client.Config(
                    bridge = "https://bridge.walletconnect.org",
                    topic = null,
                    key = null,
                    peerId = null
                )
            ) { response ->
                println("connect response $response")
            }
            Log.e("BC_example", "onCreate: $wsURL")
            Thread.sleep(2000)
            client.disconnect()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}