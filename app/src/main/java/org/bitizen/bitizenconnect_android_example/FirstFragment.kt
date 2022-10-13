package org.bitizen.bitizenconnect_android_example

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import org.bitizen.bitizenconnect_android_example.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        Glide.with(this).load("https://bitizen.org/sdk/assets/logo.png").into(binding.buttonFirst)
        viewModel.connected.observe(viewLifecycleOwner) {
            if (it == true) {
                findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            viewModel.connection.connect(
                context = requireContext(),
                dappName = "Example App",
                dappDescription = "bitizenconnect_android_example",
                dappUrl = "https://example.com",
                icons = listOf("https://bitizen.org/wp-content/uploads/2022/04/cropped-vi-192x192.png"),
                callbackUrl = "bitizendapp://wc",
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}