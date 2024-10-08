package nguyenanhduc.mobileapp.demoL.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import nguyenanhduc.mobileapp.demoL.model.Category;
import nguyenanhduc.mobileapp.demoL.model.CategoryInOut;
import nguyenanhduc.mobileapp.demoL.model.InOut;

public class CategoryDAO {
    private SQLiteDatabase db;
    private DBHelper dbHelper;

    public CategoryDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public ArrayList<Category> searchByInOut(boolean isIncome) {
        db = dbHelper.getReadableDatabase();
        ArrayList<Category> categories = new ArrayList<>();

        String query = "SELECT c.*, ci.id as categoryInOutId " +
                "FROM category c " +
                "JOIN categoryInOut ci ON c.id = ci.idCategory " +
                "JOIN inOut i ON ci.idInOut = i.id " +
                "WHERE i.name = ?";

        Cursor cursor = db.rawQuery(query, new String[]{isIncome ? "Income" : "Outcome"});

        if (cursor.moveToFirst()) {
            do {
                Category category = new Category();
                category.setId(cursor.getInt(cursor.getColumnIndex("id")));
                category.setName(cursor.getString(cursor.getColumnIndex("name")));
                category.setParent(cursor.getInt(cursor.getColumnIndex("idParent")));
                category.setNote(cursor.getString(cursor.getColumnIndex("note")));

                CategoryInOut categoryInOut = new CategoryInOut();
                categoryInOut.setId(cursor.getInt(cursor.getColumnIndex("categoryInOutId")));
                categoryInOut.setCategory(category);

                InOut inOut = new InOut();
                inOut.setId(isIncome ? 1 : 2);
                inOut.setName(isIncome ? "Income" : "Outcome");
                categoryInOut.setInOut(inOut);

                category.setCategoryInOut(categoryInOut);
                categories.add(category);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return categories;
    }

    public ArrayList<Category> getAllParentCategories() {
        db = dbHelper.getReadableDatabase();
        ArrayList<Category> parentCategories = new ArrayList<>();

        String query = "SELECT * FROM category WHERE idParent IS NULL OR idParent = 0";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Category category = new Category();
                category.setId(cursor.getInt(cursor.getColumnIndex("id")));
                category.setName(cursor.getString(cursor.getColumnIndex("name")));
                category.setParent(cursor.getInt(cursor.getColumnIndex("idParent")));
                category.setNote(cursor.getString(cursor.getColumnIndex("note")));
                parentCategories.add(category);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return parentCategories;
    }

    public boolean add(Category category) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", category.getName());
        values.put("idParent", category.getParent());
        values.put("note", category.getNote());

        long categoryId = db.insert("category", null, values);

        if (categoryId != -1) {
            // Category inserted successfully, now insert into categoryInOut
            ContentValues categoryInOutValues = new ContentValues();
            categoryInOutValues.put("idCategory", categoryId);
            categoryInOutValues.put("idInOut", category.getCategoryInOut().getInOut().getId());

            long categoryInOutId = db.insert("categoryInOut", null, categoryInOutValues);

            return categoryInOutId != -1;
        }

        return false;
    }

    // You might want to add an update method as well
    public boolean update(Category category) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", category.getName());
        values.put("idParent", category.getParent());
        values.put("note", category.getNote());

        int rowsAffected = db.update("category", values, "id = ?", new String[]{String.valueOf(category.getId())});

        if (rowsAffected > 0) {
            // Category updated successfully, now update categoryInOut if needed
            ContentValues categoryInOutValues = new ContentValues();
            categoryInOutValues.put("idInOut", category.getCategoryInOut().getInOut().getId());

            db.update("categoryInOut", categoryInOutValues, "idCategory = ?", new String[]{String.valueOf(category.getId())});

            return true;
        }

        return false;
    }
}