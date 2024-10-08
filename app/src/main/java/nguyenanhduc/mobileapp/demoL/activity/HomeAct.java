package nguyenanhduc.mobileapp.demoL.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ArrayList;
import android.widget.ArrayAdapter;

import nguyenanhduc.mobileapp.demoL.R;
import nguyenanhduc.mobileapp.demoL.activity.AddTransactionAct;
import nguyenanhduc.mobileapp.demoL.adapter.TransactionAdapter;
import nguyenanhduc.mobileapp.demoL.dao.TransactionDAO;
import nguyenanhduc.mobileapp.demoL.model.Category;
import nguyenanhduc.mobileapp.demoL.model.CategoryInOut;
import nguyenanhduc.mobileapp.demoL.model.InOut;
import nguyenanhduc.mobileapp.demoL.model.Transaction;

public class HomeAct extends AppCompatActivity {
    private TextView tvToday, tvTotalIn, tvTotalOut;
    private ListView lvTransactions;
    private Button btnAddTransaction;
    private TransactionDAO transactionDAO;
    private ArrayList<Transaction> transactions;
    private TransactionAdapter transactionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvToday = findViewById(R.id.tvToday);
        tvTotalIn = findViewById(R.id.tvTotalIn);
        tvTotalOut = findViewById(R.id.tvTotalOut);
        lvTransactions = findViewById(R.id.lvTransactions);
        btnAddTransaction = findViewById(R.id.btnAddTransaction);

        transactionDAO = new TransactionDAO(this);

        // Set today's date
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        tvToday.setText(currentDate);

        // Initialize the transaction list
        transactions = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(this, transactions);
        lvTransactions.setAdapter(transactionAdapter);

        refreshTransactionList();

        btnAddTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeAct.this, AddTransactionAct.class);
                startActivity(intent);
            }
        });

        lvTransactions.setOnItemClickListener((parent, view, position, id) -> {
            showTransactionPopupMenu(view, position);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshTransactionList();
    }

    private void refreshTransactionList() {
        transactions.clear();
        ArrayList<Transaction> newTransactions = transactionDAO.search(new Date());
        transactions.addAll(newTransactions);
        transactionAdapter.notifyDataSetChanged();
        updateTotals();

        // Debug log
        System.out.println("Fetched " + newTransactions.size() + " transactions");

        if (newTransactions.isEmpty()) {
            // If no transactions, add a dummy one for testing
            Transaction dummyTransaction = new Transaction();
            dummyTransaction.setName("Dummy Transaction");
            dummyTransaction.setAmount(100.0);
            dummyTransaction.setDay(new Date());
            CategoryInOut dummyCategory = new CategoryInOut();
            InOut dummy = new InOut();
            dummy.setId(1);
            dummy.setName("Income");
            dummyCategory.setInOut(dummy);
            Category dummy1 = new Category();
            dummy1.setId(1);
            dummy1.setName("Salary");
            dummyCategory.setCategory(dummy1);
            dummyTransaction.setCategory(dummyCategory);

            transactions.add(dummyTransaction);
            transactionAdapter.notifyDataSetChanged();
            System.out.println("Added dummy transaction");
        }
    }

    private void updateTotals() {
        double totalIn = 0;
        double totalOut = 0;

        for (Transaction t : transactions) {
            if (t.getCategory().getInOut().getName().equals("Income")) {
                totalIn += t.getAmount();
            } else {
                totalOut += t.getAmount();
            }
        }

        tvTotalIn.setText(String.format(Locale.getDefault(), "Total In: $%.2f", totalIn));
        tvTotalOut.setText(String.format(Locale.getDefault(), "Total Out: $%.2f", totalOut));
    }

    private void showTransactionPopupMenu(View view, final int position) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.transaction_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            Transaction transaction = transactions.get(position);
//            switch (item.getItemId()) {
//                case R.id.menu_edit:
//                    editTransaction(transaction);
//                    return true;
//                case R.id.menu_delete:
//                    deleteTransaction(transaction);
//                    return true;
//                default:
//                    return false;
//            }
            if (item.getItemId() == R.id.menu_edit){
                editTransaction(transaction);
                return true;
            } else if (item.getItemId() == R.id.menu_delete){
                deleteTransaction(transaction);
                return true;
            } else return false;
        });

        popup.show();
    }

    private void editTransaction(Transaction transaction) {
        Intent intent = new Intent(this, AddTransactionAct.class);
        intent.putExtra("TRANSACTION_ID", transaction.getId());
        startActivity(intent);
    }

    private void deleteTransaction(Transaction transaction) {
        if (transactionDAO.delete(transaction.getId())) {
            refreshTransactionList();
        }
    }
}