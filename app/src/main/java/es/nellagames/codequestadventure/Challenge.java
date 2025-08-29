package es.nellagames.codequestadventure;

import java.util.List;

public class Challenge {
    private int id;
    private String title;
    private String description;
    private String incompleteCode;
    private List<String> codeOptions;
    private String correctAnswer;
    private String explanation;
    private String hint;  // ✅ NEW: Added hint field

    // ✅ UPDATED: Constructor now includes hint parameter
    public Challenge(int id, String title, String description, String incompleteCode,
                     List<String> codeOptions, String correctAnswer, String explanation, String hint) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.incompleteCode = incompleteCode;
        this.codeOptions = codeOptions;
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;
        this.hint = hint;  // ✅ Initialize hint field
    }

    // Getters and setters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getIncompleteCode() { return incompleteCode; }
    public List<String> getCodeOptions() { return codeOptions; }
    public String getCorrectAnswer() { return correctAnswer; }
    public String getExplanation() { return explanation; }
    public String getHint() { return hint; }  // ✅ NEW: Added getHint() method

    public boolean isCorrect(String answer) {
        return correctAnswer.equals(answer);
    }

    // ✅ Optional: Method to check if hint is available
    public boolean hasHint() {
        return hint != null && !hint.trim().isEmpty();
    }

    // ✅ Optional: Setter for hint (if needed for dynamic updates)
    public void setHint(String hint) {
        this.hint = hint;
    }
}
