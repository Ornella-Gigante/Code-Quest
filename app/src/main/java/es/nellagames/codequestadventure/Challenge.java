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

    public Challenge(int id, String title, String description, String incompleteCode,
                     List<String> codeOptions, String correctAnswer, String explanation) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.incompleteCode = incompleteCode;
        this.codeOptions = codeOptions;
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;
    }

    // Getters and setters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getIncompleteCode() { return incompleteCode; }
    public List<String> getCodeOptions() { return codeOptions; }
    public String getCorrectAnswer() { return correctAnswer; }
    public String getExplanation() { return explanation; }

    public boolean isCorrect(String answer) {
        return correctAnswer.equals(answer);
    }
}
