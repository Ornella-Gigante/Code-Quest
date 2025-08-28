package es.nellagames.codequestadventure;


public class CodeBlock {
    private String text;
    private boolean isDraggable;
    private boolean isPlaced;
    private float x, y;
    private float originalX, originalY;

    public CodeBlock(String text, boolean isDraggable) {
        this.text = text;
        this.isDraggable = isDraggable;
        this.isPlaced = false;
        this.x = 0f;
        this.y = 0f;
        this.originalX = 0f;
        this.originalY = 0f;
    }

    // Getters and setters básicos
    public String getText() { return text; }
    public boolean isDraggable() { return isDraggable; }
    public boolean isPlaced() { return isPlaced; }
    public void setPlaced(boolean placed) { isPlaced = placed; }

    // Posición actual
    public float getX() { return x; }
    public void setX(float x) { this.x = x; }
    public float getY() { return y; }
    public void setY(float y) { this.y = y; }

    // ✅ MÉTODOS FALTANTES - Posición original
    public float getOriginalX() { return originalX; }
    public void setOriginalX(float originalX) { this.originalX = originalX; }
    public float getOriginalY() { return originalY; }
    public void setOriginalY(float originalY) { this.originalY = originalY; }

    // Métodos de utilidad
    public void resetToOriginalPosition() {
        this.x = originalX;
        this.y = originalY;
        this.isPlaced = false;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setOriginalPosition(float x, float y) {
        this.originalX = x;
        this.originalY = y;
    }
}
