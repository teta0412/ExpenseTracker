package nguyenanhduc.mobileapp.demoL.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

import nguyenanhduc.mobileapp.demoL.R;
import nguyenanhduc.mobileapp.demoL.dao.CategoryDAO;
import nguyenanhduc.mobileapp.demoL.model.Category;
import nguyenanhduc.mobileapp.demoL.model.CategoryInOut;
import nguyenanhduc.mobileapp.demoL.model.InOut;

public class AddCategoryAct extends AppCompatActivity {
    private Spinner spInOut, spIcon, spParent;
    private EditText etName;
    private Button btnAdd, btnReset;
    private CategoryDAO categoryDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        initializeViews();
        setupSpinners();
        setupListeners();
    }

    private void initializeViews() {
        spInOut = findViewById(R.id.spInOut);
        spIcon = findViewById(R.id.spIcon);
        spParent = findViewById(R.id.spParent);
        etName = findViewById(R.id.etName);
        btnAdd = findViewById(R.id.btnAdd);
        btnReset = findViewById(R.id.btnReset);

        categoryDAO = new CategoryDAO(this);
    }

    private void setupSpinners() {
        // Setup In/Out spinner
        ArrayAdapter<CharSequence> inOutAdapter = ArrayAdapter.createFromResource(this,
                R.array.in_out_array, android.R.layout.simple_spinner_item);
        inOutAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spInOut.setAdapter(inOutAdapter);

        // Setup Icon spinner (you'll need to create an array of icon names or resources)
        ArrayAdapter<CharSequence> iconAdapter = ArrayAdapter.createFromResource(this,
                R.array.icon_array, android.R.layout.simple_spinner_item);
        iconAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spIcon.setAdapter(iconAdapter);

        // Setup Parent spinner
        ArrayList<Category> parentCategories = categoryDAO.getAllParentCategories();
        ArrayAdapter<Category> parentAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, parentCategories);
        parentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spParent.setAdapter(parentAdapter);
    }

    private void setupListeners() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCategory();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetFields();
            }
        });
    }

    private void addCategory() {
        String name = etName.getText().toString().trim();
        String inOut = spInOut.getSelectedItem().toString();
        String icon = spIcon.getSelectedItem().toString();
        Category parentCategory = (Category) spParent.getSelectedItem();

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter a category name", Toast.LENGTH_SHORT).show();
            return;
        }

        Category newCategory = new Category();
        newCategory.setName(name);
        newCategory.setParent(parentCategory != null ? parentCategory.getId() : 0);
        newCategory.setNote(icon); // Assuming we store the icon name in the note field

        CategoryInOut categoryInOut = new CategoryInOut();
        InOut inOutObj = new InOut();
        inOutObj.setId(inOut.equals("Income") ? 1 : 2);
        inOutObj.setName(inOut);
        categoryInOut.setInOut(inOutObj);
        newCategory.setCategoryInOut(categoryInOut);

        boolean success = categoryDAO.add(newCategory);
        if (success) {
            Toast.makeText(this, "Category added successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to add category", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetFields() {
        etName.setText("");
        spInOut.setSelection(0);
        spIcon.setSelection(0);
        spParent.setSelection(0);
    }
}
