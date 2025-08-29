package es.nellagames.codequestadventure;

import es.nellagames.codequestadventure.Challenge;
import java.util.Arrays;
import java.util.List;


import es.nellagames.codequestadventure.Challenge;
import es.nellagames.codequestadventure.DifficultyLevel;
import java.util.Arrays;
import java.util.List;

public class GameLogic {

    private List<Challenge> beginnerChallenges;
    private List<Challenge> intermediateChallenges;
    private List<Challenge> advancedChallenges;
    private GameSettings settings;

    public GameLogic(GameSettings settings) {
        this.settings = settings;
        initializeAllChallenges();
    }

    private void initializeAllChallenges() {
        initializeBeginnerChallenges();
        initializeIntermediateChallenges();
        initializeAdvancedChallenges();
    }

    private void initializeBeginnerChallenges() {
        beginnerChallenges = Arrays.asList(
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

                new Challenge(5, "Final Challenge! 🏆",
                        "Complete the game loop",
                        "while (game.___()) { play(); }",
                        Arrays.asList("isRunning()", "running", "play()"),
                        "isRunning()",
                        "Perfect! You've mastered basic Java concepts!")
        );
    }

    private void initializeIntermediateChallenges() {
        intermediateChallenges = Arrays.asList(
                // Include all beginner challenges plus 5 more
                new Challenge(1, "Hello World! 👋", "Complete your first program!", "System.out.println(___);", Arrays.asList("\"Hello World!\"", "Hello World!", "hello world"), "\"Hello World!\"", "Strings need quotes in Java!"),
                new Challenge(2, "Variables 📦", "Declare an integer variable", "int age = ___;", Arrays.asList("10", "\"10\"", "10.0"), "10", "Integers are whole numbers without quotes!"),
                new Challenge(3, "Math Operations ➕", "Complete the addition", "int result = 5 + ___;", Arrays.asList("3", "\"3\"", "five"), "3", "Great! 5 + 3 = 8"),
                new Challenge(4, "Boolean Logic 🤖", "What makes this condition true?", "boolean isKid = age ___ 18;", Arrays.asList("<", ">", "=="), "<", "Less than (<) checks if age is under 18!"),
                new Challenge(5, "If Statements 🤔", "Complete the condition", "if (points ___ 100) { win(); }", Arrays.asList("==", "<", ">"), "==", "Perfect score is exactly equal to 100!"),

                new Challenge(6, "Loops 🔄", "How many times will this loop run?", "for(int i = 0; i < ___; i++) { }", Arrays.asList("5", "4", "6"), "5", "Loop runs from 0 to 4, that's 5 times!"),
                new Challenge(7, "Arrays 📚", "Access the first element", "int first = numbers[___];", Arrays.asList("0", "1", "first"), "0", "Arrays start at index 0 in Java!"),
                new Challenge(8, "Methods 🛠️", "Call the jump method", "player.___();", Arrays.asList("jump()", "jump", "Jump()"), "jump()", "Methods need parentheses to be called!"),
                new Challenge(9, "String Length 📏", "Get the length of a string", "int length = name.___;", Arrays.asList("length()", "size()", "count()"), "length()", "Strings use length() method in Java!"),
                new Challenge(10, "Final Challenge! 🏆", "Complete the game loop", "while (game.___()) { play(); }", Arrays.asList("isRunning()", "running", "play()"), "isRunning()", "Perfect! You've mastered intermediate Java concepts!")
        );
    }

    private void initializeAdvancedChallenges() {
        advancedChallenges = Arrays.asList(
                // Include all intermediate challenges plus 5 advanced ones
                new Challenge(1, "Hello World! 👋", "Complete your first program!", "System.out.println(___);", Arrays.asList("\"Hello World!\"", "Hello World!", "hello world"), "\"Hello World!\"", "Strings need quotes in Java!"),
                new Challenge(2, "Variables 📦", "Declare an integer variable", "int age = ___;", Arrays.asList("10", "\"10\"", "10.0"), "10", "Integers are whole numbers without quotes!"),
                new Challenge(3, "Math Operations ➕", "Complete the addition", "int result = 5 + ___;", Arrays.asList("3", "\"3\"", "five"), "3", "Great! 5 + 3 = 8"),
                new Challenge(4, "Boolean Logic 🤖", "What makes this condition true?", "boolean isKid = age ___ 18;", Arrays.asList("<", ">", "=="), "<", "Less than (<) checks if age is under 18!"),
                new Challenge(5, "If Statements 🤔", "Complete the condition", "if (points ___ 100) { win(); }", Arrays.asList("==", "<", ">"), "==", "Perfect score is exactly equal to 100!"),
                new Challenge(6, "Loops 🔄", "How many times will this loop run?", "for(int i = 0; i < ___; i++) { }", Arrays.asList("5", "4", "6"), "5", "Loop runs from 0 to 4, that's 5 times!"),
                new Challenge(7, "Arrays 📚", "Access the first element", "int first = numbers[___];", Arrays.asList("0", "1", "first"), "0", "Arrays start at index 0 in Java!"),
                new Challenge(8, "Methods 🛠️", "Call the jump method", "player.___();", Arrays.asList("jump()", "jump", "Jump()"), "jump()", "Methods need parentheses to be called!"),
                new Challenge(9, "String Length 📏", "Get the length of a string", "int length = name.___;", Arrays.asList("length()", "size()", "count()"), "length()", "Strings use length() method in Java!"),
                new Challenge(10, "Classes & Objects 🏗️", "Create a new object", "Car myCar = new ___();", Arrays.asList("Car()", "car()", "Car"), "Car()", "Constructor calls need parentheses!"),

                new Challenge(11, "Inheritance 🧬", "Extend a class", "class Dog ___ Animal {}", Arrays.asList("extends", "implements", "inherits"), "extends", "Use 'extends' for class inheritance!"),
                new Challenge(12, "Exception Handling ⚠️", "Catch an exception", "___ (Exception e) { }", Arrays.asList("catch", "try", "throw"), "catch", "Use 'catch' to handle exceptions!"),
                new Challenge(13, "Collections 📋", "Add to ArrayList", "list.___(item);", Arrays.asList("add", "append", "insert"), "add", "ArrayList uses add() method!"),
                new Challenge(14, "Lambda Expressions ⚡", "Filter a stream", "stream.___(___ -> ___ > 5)", Arrays.asList("filter, x, x", "map, x, x", "reduce, x, x"), "filter, x, x", "Use filter for conditional operations!"),
                new Challenge(15, "Master Challenge! 🎓", "Complete the interface", "class MyClass ___ MyInterface {}", Arrays.asList("implements", "extends", "uses"), "implements", "Amazing! You're now a Java master!")
        );
    }

    public Challenge getChallenge(int index) {
        List<Challenge> currentChallenges = getCurrentChallenges();
        if (index >= 0 && index < currentChallenges.size()) {
            return currentChallenges.get(index);
        }
        return currentChallenges.get(0); // Fallback to first challenge
    }

    private List<Challenge> getCurrentChallenges() {
        switch (settings.getDifficulty()) {
            case BEGINNER: return beginnerChallenges;
            case INTERMEDIATE: return intermediateChallenges;
            case ADVANCED: return advancedChallenges;
            default: return beginnerChallenges;
        }
    }

    public int getTotalChallenges() {
        return settings.getTotalChallenges();
    }

    public boolean isValidChallengeIndex(int index) {
        return index >= 0 && index < getTotalChallenges();
    }

    public DifficultyLevel getCurrentDifficulty() {
        return settings.getDifficulty();
    }
}
