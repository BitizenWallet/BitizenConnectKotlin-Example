package org.bitizen.bitizenconnect_android_example

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bitizen.bitizenconnect_android_example.databinding.FragmentSecondBinding
import org.bitizen.connect.Transaction

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {
    private val uiScope = CoroutineScope(Dispatchers.Main)

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var dialog:AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.connection.disconnect()
                }
            })
        viewModel.chainId.observe(viewLifecycleOwner) {
            binding.txChainId.text = it?.toString() ?: ""
        }
        viewModel.account.observe(viewLifecycleOwner) {
            binding.txAddress.text = it?.joinToString("\r\n")
        }
        viewModel.connected.observe(viewLifecycleOwner) {
            if (it == false) {
                findNavController().popBackStack()
            }
        }
        binding.btnPersonSign.setOnClickListener {
            createDialog {
                viewModel.connection.personalSign(
                    requireContext(),
                    message = "0xff",
                    account = it
                ) {
                    uiScope.launch {
                        binding.txRespData.text = it.result.toString()
                    }
                }
            }
        }
        binding.btnSignTypedData.setOnClickListener {
            createDialog {
                viewModel.connection.ethSignTypedData(
                    requireContext(),
                    message = "{\"types\":{\"EIP712Domain\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"version\",\"type\":\"string\"},{\"name\":\"chainId\",\"type\":\"uint256\"},{\"name\":\"verifyingContract\",\"type\":\"address\"}],\"Person\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"wallet\",\"type\":\"address\"}],\"Mail\":[{\"name\":\"from\",\"type\":\"Person\"},{\"name\":\"to\",\"type\":\"Person\"},{\"name\":\"contents\",\"type\":\"string\"}]},\"primaryType\":\"Mail\",\"domain\":{\"name\":\"Ether Mail\",\"version\":\"1\",\"chainId\":1,\"verifyingContract\":\"0xCcCCccccCCCCcCCCCCCcCcCccCcCCCcCcccccccC\"},\"message\":{\"from\":{\"name\":\"Cow\",\"wallet\":\"0xCD2a3d9F938E13CD947Ec05AbC7FE734Df8DD826\"},\"to\":{\"name\":\"Bob\",\"wallet\":\"0xbBbBBBBbbBBBbbbBbbBbbbbBBbBbbbbBbBbbBBbB\"},\"contents\":\"Hello, Bob!\"}}",
                    account = it
                ) {
                    uiScope.launch {
                        binding.txRespData.text = it.result.toString()
                    }

                }
            }

        }
        binding.btnSendTransaction.setOnClickListener {
            createDialog {
                viewModel.connection.ethSendTransaction(
                    requireContext(),
                    Transaction(
                        from = it,
                        to = it,
                        nonce = null,
                        gasPrice = null,
                        gasLimit = null,
                        value = "0xff",
                        data = "0x",
                    )
                ) {
                    uiScope.launch {
                        binding.txRespData.text = it.result.toString()
                    }
                }
            }
        }
        binding.btnDisconnect.setOnClickListener {
            viewModel.connection.disconnect()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun createDialog(callback: (String) -> Unit) {
        val accounts = viewModel.account.value?.toTypedArray() ?: emptyArray()
        dialog = AlertDialog.Builder(requireContext())
            .setTitle("选择账户")
            .setSingleChoiceItems(accounts,0) { _, which ->
                val account = accounts[which]
                callback(account)
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }
}