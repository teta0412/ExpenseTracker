package nguyenanhduc.mobileapp.demoL.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Transaction {
    private int id;
    private String name;
    private CategoryInOut category;
    private double amount;
    private Date day;
    private String note;

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public CategoryInOut getCategory() { return category; }
    public void setCategory(CategoryInOut category) { this.category = category; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public Date getDay() { return day; }
    public void setDay(Date day) { this.day = day; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String dateStr = sdf.format(day);
        String type = category.getInOut().getName();
        String categoryName = category.getCategory().getName();

        return String.format(Locale.getDefault(), "%s - %s\n%s: $%.2f\n%s",
                dateStr, categoryName, type, amount, note);
    }
}
