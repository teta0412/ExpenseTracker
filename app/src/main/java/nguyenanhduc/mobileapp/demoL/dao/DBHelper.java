package nguyenanhduc.mobileapp.demoL.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.Date;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "expense_tracker1.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE inOut (id INTEGER PRIMARY KEY, name TEXT)");
        db.execSQL("CREATE TABLE category (id INTEGER PRIMARY KEY, name TEXT, idParent INTEGER, note TEXT)");
        db.execSQL("CREATE TABLE categoryInOut (id INTEGER PRIMARY KEY, idCategory INTEGER, idInOut INTEGER)");
        db.execSQL("CREATE TABLE transactions (id INTEGER PRIMARY KEY, name TEXT, idCategoryInOut INTEGER, amount REAL, date TEXT, note TEXT)");

        // Insert default data
        insertDefaultData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades here
    }

    private void insertDefaultData(SQLiteDatabase db) {
        // Insert default InOut data
        db.execSQL("INSERT INTO inOut (name) VALUES ('Income'), ('Outcome')");

        // Insert default categories
        db.execSQL("INSERT INTO category (name, idParent, note) VALUES ('Salary', NULL, 'Regular income'), ('Part-time', NULL, 'Additional income'), ('Scholarship', NULL, 'Educational support'), ('ParentGive', NULL, 'Family support'), ('Present', NULL, 'Gifts received')");
        db.execSQL("INSERT INTO category (name, idParent, note) VALUES ('Tuition', NULL, 'Educational expenses'), ('Daily Pay', NULL, 'Regular expenses')");

        // Insert child categories for Daily Pay
        long dailyPayId = getLastInsertId(db);
        db.execSQL("INSERT INTO category (name, idParent, note) VALUES ('Home Pay', " + dailyPayId + ", 'Housing expenses'), ('Electric Pay', " + dailyPayId + ", 'Electricity bills'), ('Water Pay', " + dailyPayId + ", 'Water bills'), ('Telephone Pay', " + dailyPayId + ", 'Phone bills'), ('Eat Pay', " + dailyPayId + ", 'Food expenses'), ('Transport Pay', " + dailyPayId + ", 'Transportation costs'), ('Present Pay', " + dailyPayId + ", 'Gift expenses')");

        // Link categories to InOut
        db.execSQL("INSERT INTO categoryInOut (idCategory, idInOut) SELECT id, 1 FROM category WHERE name IN ('Salary', 'Part-time', 'Scholarship', 'ParentGive', 'Present')");
        db.execSQL("INSERT INTO categoryInOut (idCategory, idInOut) SELECT id, 2 FROM category WHERE name IN ('Tuition', 'Daily Pay') OR idParent IS NOT NULL");
    }

    private long getLastInsertId(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT last_insert_rowid()", null);
        cursor.moveToFirst();
        long id = cursor.getLong(0);
        cursor.close();
        return id;
    }
}