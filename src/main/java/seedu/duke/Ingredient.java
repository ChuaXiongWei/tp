package seedu.duke;

public class Ingredient {
    protected String description;

    public Ingredient(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
