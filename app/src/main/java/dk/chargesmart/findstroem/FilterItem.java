package dk.chargesmart.findstroem;

/**
 * Created by Ibrahim on 10/11/14.
 */
public class FilterItem {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    private String name;
    private boolean isChecked;

    public FilterItem(String name, boolean isChecked)
    {
        this.name = name;
        this.isChecked = isChecked;
    }
}
