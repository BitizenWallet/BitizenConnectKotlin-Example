package org.bitizen.bitizenconnect_android_example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import org.bitizen.bitizenconnect_android_example.databinding.FragmentFirstBinding
import org.bitizen.connect.Client
import org.bitizen.connect.LOGO_URI
import org.bitizen.connect.Session
import org.bitizen.connect.impls.BCClient
import org.bitizen.connect.impls.MoshiPayloadAdapter
import org.bitizen.connect.impls.OkHttpTransport
import java.util.concurrent.TimeUnit

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    val okClient = OkHttpClient.Builder().pingInterval(1000, TimeUnit.MILLISECONDS).build()
    val moshi = Moshi.Builder().build()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        Glide.with(this).load(LOGO_URI).into(binding.buttonFirst)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
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

            val i = Intent(Intent.ACTION_VIEW)
            i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            i.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            i.data = Uri.parse(wsURL)
            startActivity(i)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}