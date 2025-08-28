package es.nellagames.codequestadventure;

public class CodeBlock {
    private String text;
    private boolean isDraggable;
    private boolean isPlaced;
    private float x, y;

    public CodeBlock(String text, boolean isDraggable) {
        this.text = text;
        this.isDraggable = isDraggable;
        this.isPlaced = false;
    }

    // Getters and setters
    public String getText() { return text; }
    public boolean isDraggable() { return isDraggable; }
    public boolean isPlaced() { return isPlaced; }
    public void setPlaced(boolean placed) { isPlaced = placed; }
    public float getX() { return x; }
    public void setX(float x) { this.x = x; }
    public float getY() { return y; }
    public void setY(float y) { this.y = y; }
}
