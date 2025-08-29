package es.nellagames.codequestadventure;

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
                        "Strings need quotes in Java!",
                        "Remember: Java strings must be enclosed in double quotes"),

                new Challenge(2, "Variables 📦",
                        "Declare an integer variable",
                        "int age = ___;",
                        Arrays.asList("10", "\"10\"", "10.0"),
                        "10",
                        "Integers are whole numbers without quotes!",
                        "Use whole numbers without quotes for int variables"),

                new Challenge(3, "Math Operations ➕",
                        "Complete the addition",
                        "int result = 5 + ___;",
                        Arrays.asList("3", "\"3\"", "five"),
                        "3",
                        "Great! 5 + 3 = 8",
                        "Mathematical operations use numbers, not words or strings"),

                new Challenge(4, "Boolean Logic 🤖",
                        "What makes this condition true?",
                        "boolean isKid = age ___ 18;",
                        Arrays.asList("<", ">", "=="),
                        "<",
                        "Less than (<) checks if age is under 18!",
                        "Think about which operator checks if one number is smaller than another"),

                new Challenge(5, "Final Challenge! 🏆",
                        "Complete the game loop",
                        "while (game.___()) { play(); }",
                        Arrays.asList("isRunning()", "running", "play()"),
                        "isRunning()",
                        "Perfect! You've mastered basic Java concepts!",
                        "Method calls in Java need parentheses () at the end")
        );
    }

    private void initializeIntermediateChallenges() {
        intermediateChallenges = Arrays.asList(
                // First 5 are beginner level
                new Challenge(1, "Hello World! 👋",
                        "Complete your first program!",
                        "System.out.println(___);",
                        Arrays.asList("\"Hello World!\"", "Hello World!", "hello world"),
                        "\"Hello World!\"",
                        "Strings need quotes in Java!",
                        "Remember: Java strings must be enclosed in double quotes"),

                new Challenge(2, "Variables 📦",
                        "Declare an integer variable",
                        "int age = ___;",
                        Arrays.asList("10", "\"10\"", "10.0"),
                        "10",
                        "Integers are whole numbers without quotes!",
                        "Use whole numbers without quotes for int variables"),

                new Challenge(3, "Math Operations ➕",
                        "Complete the addition",
                        "int result = 5 + ___;",
                        Arrays.asList("3", "\"3\"", "five"),
                        "3",
                        "Great! 5 + 3 = 8",
                        "Mathematical operations use numbers, not words or strings"),

                new Challenge(4, "Boolean Logic 🤖",
                        "What makes this condition true?",
                        "boolean isKid = age ___ 18;",
                        Arrays.asList("<", ">", "=="),
                        "<",
                        "Less than (<) checks if age is under 18!",
                        "Think about which operator checks if one number is smaller than another"),

                new Challenge(5, "If Statements 🤔",
                        "Complete the condition",
                        "if (points ___ 100) { win(); }",
                        Arrays.asList("==", "<", ">"),
                        "==",
                        "Perfect score is exactly equal to 100!",
                        "Use == to check if two values are exactly the same"),

                // Intermediate level challenges
                new Challenge(6, "Loops 🔄",
                        "How many times will this loop run?",
                        "for(int i = 0; i < ___; i++) { }",
                        Arrays.asList("5", "4", "6"),
                        "5",
                        "Loop runs from 0 to 4, that's 5 times!",
                        "Count from 0 to the number minus 1"),

                new Challenge(7, "Arrays 📚",
                        "Access the first element",
                        "int first = numbers[___];",
                        Arrays.asList("0", "1", "first"),
                        "0",
                        "Arrays start at index 0 in Java!",
                        "Array indexing starts from 0, not 1"),

                new Challenge(8, "Methods 🛠️",
                        "Call the jump method",
                        "player.___();",
                        Arrays.asList("jump()", "jump", "Jump()"),
                        "jump()",
                        "Methods need parentheses to be called!",
                        "Method calls require parentheses, even if no parameters"),

                new Challenge(9, "String Length 📏",
                        "Get the length of a string",
                        "int length = name.___;",
                        Arrays.asList("length()", "size()", "count()"),
                        "length()",
                        "Strings use length() method in Java!",
                        "String objects have a length() method to get character count"),

                new Challenge(10, "Final Intermediate! 🏆",
                        "Complete the game loop",
                        "while (game.___()) { play(); }",
                        Arrays.asList("isRunning()", "running", "play()"),
                        "isRunning()",
                        "Perfect! You've mastered intermediate Java concepts!",
                        "Method calls need parentheses to execute")
        );
    }

    private void initializeAdvancedChallenges() {
        advancedChallenges = Arrays.asList(
                // Include all intermediate challenges (first 10)
                new Challenge(1, "Hello World! 👋",
                        "Complete your first program!",
                        "System.out.println(___);",
                        Arrays.asList("\"Hello World!\"", "Hello World!", "hello world"),
                        "\"Hello World!\"",
                        "Strings need quotes in Java!",
                        "Remember: Java strings must be enclosed in double quotes"),

                new Challenge(2, "Variables 📦",
                        "Declare an integer variable",
                        "int age = ___;",
                        Arrays.asList("10", "\"10\"", "10.0"),
                        "10",
                        "Integers are whole numbers without quotes!",
                        "Use whole numbers without quotes for int variables"),

                new Challenge(3, "Math Operations ➕",
                        "Complete the addition",
                        "int result = 5 + ___;",
                        Arrays.asList("3", "\"3\"", "five"),
                        "3",
                        "Great! 5 + 3 = 8",
                        "Mathematical operations use numbers, not words or strings"),

                new Challenge(4, "Boolean Logic 🤖",
                        "What makes this condition true?",
                        "boolean isKid = age ___ 18;",
                        Arrays.asList("<", ">", "=="),
                        "<",
                        "Less than (<) checks if age is under 18!",
                        "Think about which operator checks if one number is smaller than another"),

                new Challenge(5, "If Statements 🤔",
                        "Complete the condition",
                        "if (points ___ 100) { win(); }",
                        Arrays.asList("==", "<", ">"),
                        "==",
                        "Perfect score is exactly equal to 100!",
                        "Use == to check if two values are exactly the same"),

                new Challenge(6, "Loops 🔄",
                        "How many times will this loop run?",
                        "for(int i = 0; i < ___; i++) { }",
                        Arrays.asList("5", "4", "6"),
                        "5",
                        "Loop runs from 0 to 4, that's 5 times!",
                        "Count from 0 to the number minus 1"),

                new Challenge(7, "Arrays 📚",
                        "Access the first element",
                        "int first = numbers[___];",
                        Arrays.asList("0", "1", "first"),
                        "0",
                        "Arrays start at index 0 in Java!",
                        "Array indexing starts from 0, not 1"),

                new Challenge(8, "Methods 🛠️",
                        "Call the jump method",
                        "player.___();",
                        Arrays.asList("jump()", "jump", "Jump()"),
                        "jump()",
                        "Methods need parentheses to be called!",
                        "Method calls require parentheses, even if no parameters"),

                new Challenge(9, "String Length 📏",
                        "Get the length of a string",
                        "int length = name.___;",
                        Arrays.asList("length()", "size()", "count()"),
                        "length()",
                        "Strings use length() method in Java!",
                        "String objects have a length() method to get character count"),

                new Challenge(10, "Classes & Objects 🏗️",
                        "Create a new object",
                        "Car myCar = new ___();",
                        Arrays.asList("Car()", "car()", "Car"),
                        "Car()",
                        "Constructor calls need parentheses!",
                        "Object creation requires calling the constructor with parentheses"),

                // Advanced level challenges
                new Challenge(11, "Inheritance 🧬",
                        "Extend a class",
                        "class Dog ___ Animal {}",
                        Arrays.asList("extends", "implements", "inherits"),
                        "extends",
                        "Use 'extends' for class inheritance!",
                        "The extends keyword creates an inheritance relationship"),

                new Challenge(12, "Exception Handling ⚠️",
                        "Catch an exception",
                        "___ (Exception e) { }",
                        Arrays.asList("catch", "try", "throw"),
                        "catch",
                        "Use 'catch' to handle exceptions!",
                        "Catch blocks handle exceptions thrown by try blocks"),

                new Challenge(13, "Collections 📋",
                        "Add to ArrayList",
                        "list.___(item);",
                        Arrays.asList("add", "append", "insert"),
                        "add",
                        "ArrayList uses add() method!",
                        "ArrayList provides an add() method to insert elements"),

                new Challenge(14, "Lambda Expressions ⚡",
                        "Filter a stream",
                        "stream.___(x -> x > 5)",
                        Arrays.asList("filter", "map", "reduce"),
                        "filter",
                        "Use filter for conditional operations!",
                        "Filter operations select elements that match a condition"),

                new Challenge(15, "Master Challenge! 🎓",
                        "Complete the interface",
                        "class MyClass ___ MyInterface {}",
                        Arrays.asList("implements", "extends", "uses"),
                        "implements",
                        "Amazing! You're now a Java master!",
                        "Classes implement interfaces using the implements keyword")
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
            case BEGINNER:
                return beginnerChallenges;
            case INTERMEDIATE:
                return intermediateChallenges;
            case ADVANCED:
                return advancedChallenges;
            default:
                return beginnerChallenges;
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
