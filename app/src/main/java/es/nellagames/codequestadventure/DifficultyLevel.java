package es.nellagames.codequestadventure;


public enum DifficultyLevel {
    BEGINNER("🌱 Beginner", "Perfect for first-time coders", 5, "#4CAF50"),
    INTERMEDIATE("🔥 Intermediate", "Ready for more challenges", 10, "#FF9800"),
    ADVANCED("⚡ Advanced", "Master level programming", 15, "#F44336");

    private final String displayName;
    private final String description;
    private final int totalChallenges;
    private final String color;

    DifficultyLevel(String displayName, String description, int totalChallenges, String color) {
        this.displayName = displayName;
        this.description = description;
        this.totalChallenges = totalChallenges;
        this.color = color;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public int getTotalChallenges() { return totalChallenges; }
    public String getColor() { return color; }

    public static DifficultyLevel fromString(String level) {
        try {
            return valueOf(level.toUpperCase());
        } catch (IllegalArgumentException e) {
            return BEGINNER; // Default fallback
        }
    }
}
