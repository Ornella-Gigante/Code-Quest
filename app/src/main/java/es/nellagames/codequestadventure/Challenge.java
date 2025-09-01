package es.nellagames.codequestadventure;

import java.util.List;

public class Challenge {
    private int id;
    private String title;
    private String description;
    private String codeSnippet;
    private List<String> options;
    private String correctAnswer;
    private String hint; // Hint en inglés para respuestas incorrectas
    private String correctExplanation; // Explicación para respuestas correctas

    public Challenge(int id, String title, String description, String codeSnippet,
                     List<String> options, String correctAnswer, String hint, String correctExplanation) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.codeSnippet = codeSnippet;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.hint = hint;
        this.correctExplanation = correctExplanation;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCodeSnippet() { return codeSnippet; }
    public List<String> getOptions() { return options; }
    public String getCorrectAnswer() { return correctAnswer; }
    public String getHint() { return hint; }
    public String getCorrectExplanation() { return correctExplanation; }

    // Método para verificar si la respuesta es correcta
    public boolean isCorrect(String answer) {
        if (answer == null || correctAnswer == null) return false;
        return correctAnswer.trim().equalsIgnoreCase(answer.trim());
    }

    // Método para obtener las opciones de código (alias de getOptions)
    public List<String> getCodeOptions() {
        return options;
    }

    // Método para obtener el código incompleto (alias de getCodeSnippet)
    public String getIncompleteCode() {
        return codeSnippet;
    }
    public String getExplanation() {
        return correctExplanation;
    }

}