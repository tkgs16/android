package com.example.advizors.models.notes.delete

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.advizors.databinding.FragmentDeleteUiBinding

class DeleteUiFragment : Fragment() {

    private var _binding: FragmentDeleteUiBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val deleteUiViewModel =
            ViewModelProvider(this).get(DeleteUiViewModel::class.java)

        _binding = FragmentDeleteUiBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDeleteUi
        deleteUiViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}