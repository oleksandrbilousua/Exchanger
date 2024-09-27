package com.bilous.exchanger.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import com.bilous.exchanger.R
import com.bilous.exchanger.databinding.FragmentExchangeBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ExchangeFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels()
    private val userBalanceAdapter = BalanceAdapter(listOf())
    private lateinit var binding: FragmentExchangeBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentExchangeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBalanceList()
        addListeners()
        viewModel.rates.observe(viewLifecycleOwner, object : Observer<Map<String, Float>> {
            override fun onChanged(value: Map<String, Float>) {
                if (value.isNotEmpty()) {
                    val spinnerAdapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_dropdown_item, value.keys.toList()
                    )
                    binding.sellCurrency.adapter = spinnerAdapter
                    binding.resultCurrency.adapter = spinnerAdapter
                    viewModel.rates.removeObserver(this)
                }

            }

        })
        viewModel.resultValue.observe(viewLifecycleOwner) {
            binding.resultValue.text = it.toString()
        }
        viewModel.status.observe(viewLifecycleOwner) {
            showDialog(it)
            //calculate after each exchange because of fee rules
            startCalculate()
        }

        lifecycleScope.launch {
            viewModel.readUserBalance().collectLatest {
                userBalanceAdapter.items = it
                userBalanceAdapter.notifyDataSetChanged()

            }
        }
    }

    private fun setupBalanceList() {
        binding.userBalance.layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
        binding.userBalance.adapter = userBalanceAdapter
    }

    private fun addListeners() {
        binding.btnSubmit.setOnClickListener {
            viewModel.exchange()
        }

        binding.sellValue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                startCalculate()
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        binding.sellCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                startCalculate()
            }
        }
        binding.resultCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                startCalculate()
            }

        }

    }

    private fun startCalculate() {
        if (binding.sellValue.text.isNotEmpty()) {
            viewModel.calculateResult(
                binding.sellCurrency.selectedItem.toString(),
                binding.sellValue.text.toString().toFloat(),
                binding.resultCurrency.selectedItem.toString()
            )
        }
    }

    private fun showDialog(status: MainViewModel.Result) {
        val (title, message) = dialogTexts(status)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setNeutralButton(resources.getString(R.string.dialog_button), null)
            .show()

    }

    private fun dialogTexts(status: MainViewModel.Result): Pair<String, String> {
        return when (status) {
            MainViewModel.Result.Failure ->
                Pair(
                    resources.getString(R.string.dialog_title_fail),
                    resources.getString(R.string.dialog_message_fail)
                )

            is MainViewModel.Result.Success ->
                Pair(
                    resources.getString(R.string.dialog_title_ok),
                    resources.getString(
                        R.string.dialog_message_ok,
                        status.sellValue,
                        status.sellCurrency,
                        status.resultValue,
                        status.resultCurrency
                    )
                )

            is MainViewModel.Result.SuccessWithFee ->
                Pair(
                    resources.getString(R.string.dialog_title_ok),
                    resources.getString(
                        R.string.dialog_message_ok_with_fee,
                        status.sellValue,
                        status.sellCurrency,
                        status.resultValue,
                        status.resultCurrency, status.fee
                    )
                )

        }
    }

}