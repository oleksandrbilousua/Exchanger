package com.bilous.exchanger.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bilous.exchanger.databinding.BalanceItemBinding
import com.bilous.exchanger.model.UserCurrency

class BalanceAdapter(var items: List<UserCurrency>) : RecyclerView.Adapter<BalanceAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: BalanceItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = BalanceItemBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.binding.balanceText.text = items[position].formatBalance()
    }

    override fun getItemCount() = items.size

}

fun UserCurrency.formatBalance(): String {
    return "${"%.2f".format(balance)} $currency"
}