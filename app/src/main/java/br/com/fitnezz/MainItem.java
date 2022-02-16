package br.com.fitnezz;

public class MainItem {
    private int id;
    private int drawableId;
    private int testStringId;
    private int color;

    public MainItem(int id, int drawableId, int textStringId, int color) {
        this.id = id;
        this.drawableId = drawableId;
        this.testStringId = textStringId;
        this.color = color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }

    public void setTestStringId(int testStringId) {
        this.testStringId = testStringId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getColor() {
        return color;
    }

    public int getTextStringId() {
        return testStringId;
    }

    public int getDrawableId() {
        return drawableId;
    }
}
