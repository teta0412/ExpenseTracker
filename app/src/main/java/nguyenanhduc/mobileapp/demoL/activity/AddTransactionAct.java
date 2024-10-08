package nguyenanhduc.mobileapp.demoL.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import nguyenanhduc.mobileapp.demoL.R;
import nguyenanhduc.mobileapp.demoL.dao.CategoryDAO;
import nguyenanhduc.mobileapp.demoL.dao.TransactionDAO;
import nguyenanhduc.mobileapp.demoL.model.Category;
import nguyenanhduc.mobileapp.demoL.model.Transaction;

public class AddTransactionAct extends AppCompatActivity {
    private RadioGroup rgTransactionType;
    private RadioButton rbIncome, rbOutcome;
    private Spinner spCategory;
    private EditText etAmount, etDate, etNote;
    private Button btnAdd;
    private ImageButton btnAddCategory;
    private CategoryDAO categoryDAO;
    private TransactionDAO transactionDAO;
    private ArrayList<Category> categories;
    private ArrayAdapter<Category> categoryAdapter;
    private SimpleDateFormat dateFormatter;
    private Date selectedDate;
    private int transactionId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        initializeViews();
        setupInitialState();
        setupListeners();
    }

    private void initializeViews() {
        rgTransactionType = findViewById(R.id.rgTransactionType);
        rbIncome = findViewById(R.id.rbIncome);
        rbOutcome = findViewById(R.id.rbOutcome);
        spCategory = findViewById(R.id.spCategory);
        etAmount = findViewById(R.id.etAmount);
        etDate = findViewById(R.id.etDate);
        etNote = findViewById(R.id.etNote);
        btnAdd = findViewById(R.id.btnAdd);
        btnAddCategory = findViewById(R.id.btnAddCategory);
    }

    private void setupInitialState() {
        categoryDAO = new CategoryDAO(this);
        transactionDAO = new TransactionDAO(this);
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        categories = new ArrayList<>();
        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(categoryAdapter);

        rbIncome.setChecked(true);

        transactionId = getIntent().getIntExtra("TRANSACTION_ID", -1);
        if (transactionId != -1) {
            setTitle("Edit Transaction");
            btnAdd.setText("Update");
            loadTransaction(transactionId);
        } else {
            selectedDate = new Date();
            etDate.setText(dateFormatter.format(selectedDate));
        }

        updateCategorySpinner();
    }

    private void setupListeners() {
        etDate.setOnClickListener(v -> showDatePickerDialog());
        btnAdd.setOnClickListener(v -> addOrUpdateTransaction());
        btnAddCategory.setOnClickListener(this::showCategoryPopupMenu);
        rgTransactionType.setOnCheckedChangeListener((group, checkedId) -> updateCategorySpinner());
    }

    private void showCategoryPopupMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenuInflater().inflate(R.menu.category_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_add_category) {
                    navigateToAddCategory();
                    return true;
                }
                return false;
            }
        });
        popup.show();
    }

    private void navigateToAddCategory() {
        Intent intent = new Intent(this, AddCategoryAct.class);
        startActivity(intent);
    }

    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        if (selectedDate != null) {
            c.setTime(selectedDate);
        }
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        selectedDate = newDate.getTime();
                        etDate.setText(dateFormatter.format(selectedDate));
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private void addOrUpdateTransaction() {
        if (!validateInput()) {
            return;
        }

        Transaction transaction = new Transaction();
        if (transactionId != -1) {
            transaction.setId(transactionId);
        }
        transaction.setName(((Category) spCategory.getSelectedItem()).getName());
        transaction.setCategory(((Category) spCategory.getSelectedItem()).getCategoryInOut());
        transaction.setAmount(Double.parseDouble(etAmount.getText().toString()));
        transaction.setDay(selectedDate);
        transaction.setNote(etNote.getText().toString());

        boolean success;
        if (transactionId != -1) {
            success = transactionDAO.edit(transaction);
        } else {
            success = transactionDAO.add(transaction);
        }

        if (success) {
            Toast.makeText(this, transactionId != -1 ? "Transaction updated" : "Transaction added", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error saving transaction", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateCategorySpinner() {
        boolean isIncome = rbIncome.isChecked();
        categories.clear();
        categories.addAll(categoryDAO.searchByInOut(isIncome));
        categoryAdapter.notifyDataSetChanged();
    }

    private boolean validateInput() {
        if (spCategory.getSelectedItem() == null) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etAmount.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedDate == null) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void loadTransaction(int id) {
        Transaction transaction = transactionDAO.getById(id);
        if (transaction != null) {
            if (transaction.getCategory().getInOut().getName().equals("Income")) {
                rbIncome.setChecked(true);
            } else {
                rbOutcome.setChecked(true);
            }
            updateCategorySpinner();
            for (int i = 0; i < categories.size(); i++) {
                if (categories.get(i).getId() == transaction.getCategory().getCategory().getId()) {
                    spCategory.setSelection(i);
                    break;
                }
            }
            etAmount.setText(String.valueOf(transaction.getAmount()));
            selectedDate = transaction.getDay();
            etDate.setText(dateFormatter.format(selectedDate));
            etNote.setText(transaction.getNote());
        }
    }
}