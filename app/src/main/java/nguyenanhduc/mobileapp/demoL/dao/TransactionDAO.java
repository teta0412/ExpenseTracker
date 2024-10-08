package nguyenanhduc.mobileapp.demoL.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import nguyenanhduc.mobileapp.demoL.model.Category;
import nguyenanhduc.mobileapp.demoL.model.CategoryInOut;
import nguyenanhduc.mobileapp.demoL.model.InOut;
import nguyenanhduc.mobileapp.demoL.model.Transaction;

public class TransactionDAO {
    private SQLiteDatabase db;
    private DBHelper dbHelper;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public TransactionDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public boolean add(Transaction t) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", t.getName());
        values.put("idCategoryInOut", t.getCategory().getId());
        values.put("amount", t.getAmount());
        values.put("date", sdf.format(t.getDay())); // Use consistent date format
        values.put("note", t.getNote());
        long result = db.insert("transactions", null, values);
        return result != -1;
    }

    public boolean edit(Transaction t) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", t.getName());
        values.put("idCategoryInOut", t.getCategory().getId());
        values.put("amount", t.getAmount());
        values.put("date", t.getDay().toString());
        values.put("note", t.getNote());
        int rowsAffected = db.update("transactions", values, "id = ?", new String[]{String.valueOf(t.getId())});
        return rowsAffected > 0;
    }

    public boolean delete(int id) {
        db = dbHelper.getWritableDatabase();
        int rowsAffected = db.delete("transactions", "id = ?", new String[]{String.valueOf(id)});
        return rowsAffected > 0;
    }

    public ArrayList<Transaction> search(Date day) {
        db = dbHelper.getReadableDatabase();
        ArrayList<Transaction> transactions = new ArrayList<>();
        String dateString = sdf.format(day);

        String query = "SELECT t.*, c.id as categoryId, c.name as categoryName, i.id as inOutId, i.name as inOutName " +
                "FROM transactions t " +
                "JOIN categoryInOut ci ON t.idCategoryInOut = ci.id " +
                "JOIN category c ON ci.idCategory = c.id " +
                "JOIN inOut i ON ci.idInOut = i.id " +
                "WHERE t.date = ?";

        Cursor cursor = db.rawQuery(query, new String[]{dateString});

        if (cursor.moveToFirst()) {
            do {
                Transaction t = new Transaction();
                t.setId(cursor.getInt(cursor.getColumnIndex("id")));
                t.setName(cursor.getString(cursor.getColumnIndex("name")));
                t.setAmount(cursor.getDouble(cursor.getColumnIndex("amount")));
                try {
                    t.setDay(sdf.parse(cursor.getString(cursor.getColumnIndex("date"))));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                t.setNote(cursor.getString(cursor.getColumnIndex("note")));

                CategoryInOut category = new CategoryInOut();
                category.setId(cursor.getInt(cursor.getColumnIndex("idCategoryInOut")));

                Category cat = new Category();
                cat.setId(cursor.getInt(cursor.getColumnIndex("categoryId")));
                cat.setName(cursor.getString(cursor.getColumnIndex("categoryName")));

                InOut inOut = new InOut();
                inOut.setId(cursor.getInt(cursor.getColumnIndex("inOutId")));
                inOut.setName(cursor.getString(cursor.getColumnIndex("inOutName")));

                category.setCategory(cat);
                category.setInOut(inOut);
                t.setCategory(category);

                transactions.add(t);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // Debug log
        System.out.println("Fetched " + transactions.size() + " transactions for date: " + dateString);

        return transactions;
    }
    public Transaction getById(int id) {
        db = dbHelper.getReadableDatabase();
        Transaction transaction = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        String query = "SELECT t.*, c.id as categoryId, c.name as categoryName, i.id as inOutId, i.name as inOutName " +
                "FROM transactions t " +
                "JOIN categoryInOut ci ON t.idCategoryInOut = ci.id " +
                "JOIN category c ON ci.idCategory = c.id " +
                "JOIN inOut i ON ci.idInOut = i.id " +
                "WHERE t.id = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});

        if (cursor.moveToFirst()) {
            transaction = new Transaction();
            transaction.setId(cursor.getInt(cursor.getColumnIndex("id")));
            transaction.setName(cursor.getString(cursor.getColumnIndex("name")));
            transaction.setAmount(cursor.getDouble(cursor.getColumnIndex("amount")));
            try {
                transaction.setDay(sdf.parse(cursor.getString(cursor.getColumnIndex("date"))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            transaction.setNote(cursor.getString(cursor.getColumnIndex("note")));

            CategoryInOut category = new CategoryInOut();
            category.setId(cursor.getInt(cursor.getColumnIndex("idCategoryInOut")));

            Category cat = new Category();
            cat.setId(cursor.getInt(cursor.getColumnIndex("categoryId")));
            cat.setName(cursor.getString(cursor.getColumnIndex("categoryName")));

            InOut inOut = new InOut();
            inOut.setId(cursor.getInt(cursor.getColumnIndex("inOutId")));
            inOut.setName(cursor.getString(cursor.getColumnIndex("inOutName")));

            category.setCategory(cat);
            category.setInOut(inOut);
            transaction.setCategory(category);
        }
        cursor.close();
        return transaction;
    }
}