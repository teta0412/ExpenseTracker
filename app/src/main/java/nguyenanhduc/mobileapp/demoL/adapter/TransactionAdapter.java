package nguyenanhduc.mobileapp.demoL.adapter;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import nguyenanhduc.mobileapp.demoL.R;
import nguyenanhduc.mobileapp.demoL.model.Transaction;

public class TransactionAdapter extends ArrayAdapter<Transaction> {
    private Context mContext;
    private ArrayList<Transaction> mTransactions;

    public TransactionAdapter(@NonNull Context context, ArrayList<Transaction> transactions) {
        super(context, 0, transactions);
        mContext = context;
        mTransactions = transactions;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.transaction_list_item, parent, false);
        }

        Transaction currentTransaction = mTransactions.get(position);

        TextView tvCategory = listItem.findViewById(R.id.tvCategory);
        TextView tvAmount = listItem.findViewById(R.id.tvAmount);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        tvCategory.setText(currentTransaction.getCategory().getCategory().getName());

        String amountText = String.format(Locale.getDefault(), "$%.2f",
                currentTransaction.getAmount());
        tvAmount.setText(amountText);
        if (currentTransaction.getCategory().getInOut().getName().equals("Income")){
            tvAmount.setTextColor(Color.parseColor("#4CAF50"));
        }else tvAmount.setTextColor(Color.parseColor("#F44336"));


        return listItem;
    }
}
