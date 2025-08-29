package es.nellagames.codequestadventure;

import es.nellagames.codequestadventure.Challenge;
import java.util.Arrays;
import java.util.List;

public class GameLogic {

    private List<Challenge> challenges;

    public GameLogic() {
        initializeChallenges();
    }

    private void initializeChallenges() {
        challenges = Arrays.asList(
                new Challenge(1, "Hello World! 👋",
                        "Complete your first program!",
                        "System.out.println(___);",
                        Arrays.asList("\"Hello World!\"", "Hello World!", "hello world"),
                        "\"Hello World!\"",
                        "Strings need quotes in Java!"),

                new Challenge(2, "Variables 📦",
                        "Declare an integer variable",
                        "int age = ___;",
                        Arrays.asList("10", "\"10\"", "10.0"),
                        "10",
                        "Integers are whole numbers without quotes!"),

                new Challenge(3, "Math Operations ➕",
                        "Complete the addition",
                        "int result = 5 + ___;",
                        Arrays.asList("3", "\"3\"", "five"),
                        "3",
                        "Great! 5 + 3 = 8"),

                new Challenge(4, "Boolean Logic 🤖",
                        "What makes this condition true?",
                        "boolean isKid = age ___ 18;",
                        Arrays.asList("<", ">", "=="),
                        "<",
                        "Less than (<) checks if age is under 18!"),

                new Challenge(5, "If Statements 🤔",
                        "Complete the condition",
                        "if (points ___ 100) { win(); }",
                        Arrays.asList("==", "<", ">"),
                        "==",
                        "Perfect score is exactly equal to 100!"),

                new Challenge(6, "Loops 🔄",
                        "How many times will this loop run?",
                        "for(int i = 0; i < ___; i++) { }",
                        Arrays.asList("5", "4", "6"),
                        "5",
                        "Loop runs from 0 to 4, that's 5 times!"),

                new Challenge(7, "Arrays 📚",
                        "Access the first element",
                        "int first = numbers[___];",
                        Arrays.asList("0", "1", "first"),
                        "0",
                        "Arrays start at index 0 in Java!"),

                new Challenge(8, "Methods 🛠️",
                        "Call the jump method",
                        "player.___();",
                        Arrays.asList("jump()", "jump", "Jump()"),
                        "jump()",
                        "Methods need parentheses to be called!"),

                new Challenge(9, "String Length 📏",
                        "Get the length of a string",
                        "int length = name.___;",
                        Arrays.asList("length()", "size()", "count()"),
                        "length()",
                        "Strings use length() method in Java!"),

                new Challenge(10, "Final Challenge! 🏆",
                        "Complete the game loop",
                        "while (game.___()) { play(); }",
                        Arrays.asList("isRunning()", "running", "play()"),
                        "isRunning()",
                        "Perfect! You've mastered basic Java concepts!")
        );
    }

    public Challenge getChallenge(int index) {
        if (index >= 0 && index < challenges.size()) {
            return challenges.get(index);
        }
        return challenges.get(0); // Return first challenge as fallback
    }

    public int getTotalChallenges() {
        return challenges.size();
    }

    public boolean isValidChallengeIndex(int index) {
        return index >= 0 && index < challenges.size();
    }
}
