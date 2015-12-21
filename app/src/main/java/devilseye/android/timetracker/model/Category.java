package devilseye.android.timetracker.model;

public class Category {
    int _id;
    String _name;
    boolean isSelected;

    public Category(){}

    public Category(int id, String name){
        this._id=id;
        this._name=name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public Category(String name){
        this._name=name;
    }

    public int get_id() {
        return this._id;
    }

    public void set_id(int id) {
        this._id = id;
    }

    public String get_name() {
        return this._name;
    }

    public void set_name(String name) {
        this._name = name;
    }
}
