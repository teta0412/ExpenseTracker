package nguyenanhduc.mobileapp.demoL.model;

public class Category {
    private int id;
    private String name;
    private Integer parent;
    private String note;
    private CategoryInOut categoryInOut;


    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getParent() { return parent; }
    public void setParent(Integer parent) { this.parent = parent; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public CategoryInOut getCategoryInOut() { return categoryInOut; }
    public void setCategoryInOut(CategoryInOut categoryInOut) { this.categoryInOut = categoryInOut; }
    @Override
    public String toString() {
        return name;
    }
}
