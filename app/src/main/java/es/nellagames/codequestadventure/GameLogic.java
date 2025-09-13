package es.nellagames.codequestadventure;

import android.graphics.Bitmap;

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
                new Challenge(1, "Hello World", "Print hello world",
                        "System.out.println(___);",
                        Arrays.asList("\"Hello World\"", "\"hello world\"", "Hello World"),
                        "\"Hello World\"",
                        "Think about what goes inside the parentheses. Strings need to be wrapped in quotes!",
                        "Correct! In Java, strings must be enclosed in double quotes. System.out.println(\"Hello World\") prints the text to the console."),

                new Challenge(2, "Declare Variable", "Declare integer variable",
                        "int age = ___;",
                        Arrays.asList("10", "\"10\"", "ten"),
                        "10",
                        "Remember: integers are numbers without quotes. Think of your age as a number!",
                        "Perfect! Integer literals in Java are written as plain numbers without quotes. 'int age = 10;' creates an integer variable."),

                new Challenge(3, "Addition", "Complete addition",
                        "int sum = 5 + ___;",
                        Arrays.asList("3", "\"3\"", "three"),
                        "3",
                        "Mathematical operations work with numbers, not text. What number would you add to 5?",
                        "Excellent! Arithmetic in Java uses numeric literals. '5 + 3' performs mathematical addition, resulting in 8."),

                new Challenge(4, "Boolean Compare", "Check if age is less than 18",
                        "boolean isMinor = age ___ 18;",
                        Arrays.asList("<", ">", "=="),
                        "<",
                        "Think about what symbol means 'less than'. Which symbol points to the smaller number?",
                        "Great! The '<' operator checks if the left value is smaller than the right value. This is perfect for age verification."),

                new Challenge(5, "If Statement", "Complete if condition",
                        "if(age ___ 18) { /* child logic */ }",
                        Arrays.asList("<", ">", "=="),
                        "<",
                        "The code comment says 'child logic'. What comparison would be true for children?",
                        "Correct! 'if(age < 18)' executes the block when age is less than 18, identifying minors or children."),

                new Challenge(6, "For Loop", "Loop from 0 to 4 (5 times)",
                        "for(int i=0; i < ___; i++) {}",
                        Arrays.asList("5", "4", "6"),
                        "5",
                        "If you start at 0 and want to loop 5 times, what should be your upper limit?",
                        "Perfect! 'i < 5' with i starting at 0 creates iterations: 0, 1, 2, 3, 4 - exactly 5 iterations total."),

                new Challenge(7, "Array Access", "Get the first element",
                        "int first = arr[___];",
                        Arrays.asList("0", "1", "first"),
                        "0",
                        "Arrays in Java start counting from zero, not one. What's the first index position?",
                        "Excellent! Java arrays are zero-indexed, meaning the first element is at position [0], not [1]."),

                new Challenge(8, "String Length", "Get length of string",
                        "int len = str.___();",
                        Arrays.asList("length", "size", "count"),
                        "length",
                        "Strings have a method to get their length. It's called exactly what you'd expect!",
                        "Correct! The 'length()' method returns the number of characters in a String object."),

                new Challenge(9, "Print Variable", "Print the value of variable age",
                        "System.out.println(___);",
                        Arrays.asList("age", "\"age\"", "'age'"),
                        "age",
                        "To print the VALUE of a variable, don't use quotes. Just use the variable name!",
                        "Great! Using 'age' without quotes prints the variable's value, while '\"age\"' would print the literal text 'age'."),

                new Challenge(10, "Comments", "Write a single line comment",
                        "___ This explains the code",
                        Arrays.asList("//", "/*", "#"),
                        "//",
                        "Single-line comments in Java start with two forward slashes.",
                        "Perfect! '//' creates single-line comments in Java. Everything after '//' on that line is ignored by the compiler.")
        );
    }

    private void initializeIntermediateChallenges() {
        intermediateChallenges = Arrays.asList(
                new Challenge(1, "While Loop", "Create a while loop condition",
                        "while(___) { count++; }",
                        Arrays.asList("count < 10", "true", "false"),
                        "count < 10",
                        "While loops need a condition that becomes false eventually to avoid infinite loops.",
                        "Excellent! 'count < 10' creates a proper terminating condition - the loop runs while count is less than 10."),

                new Challenge(2, "Switch Statement", "Add a case for value 1",
                        "switch(num) { case ___: result = \"one\"; break; }",
                        Arrays.asList("1", "\"1\"", "one"),
                        "1",
                        "Switch cases use the actual values to compare against, not strings unless switching on strings.",
                        "Correct! 'case 1:' matches when the switch variable equals the integer 1."),

                new Challenge(3, "Method Declaration", "Declare a method that returns nothing",
                        "public ___ printMessage() { System.out.println(\"Hi\"); }",
                        Arrays.asList("void", "int", "String"),
                        "void",
                        "When a method doesn't return any value, what keyword represents 'nothing'?",
                        "Perfect! 'void' means the method doesn't return any value - it just performs an action."),

                new Challenge(4, "Class Definition", "Define a public class",
                        "public class ___ { }",
                        Arrays.asList("MyClass", "myclass", "CLASS"),
                        "MyClass",
                        "Java class names follow PascalCase convention - first letter uppercase!",
                        "Great! Java class names should start with uppercase letter and follow PascalCase naming convention."),

                new Challenge(5, "Inheritance", "Make Dog extend Animal class",
                        "class Dog ___ Animal { }",
                        Arrays.asList("extends", "implements", "inherits"),
                        "extends",
                        "When one class inherits from another class, what keyword do you use?",
                        "Correct! 'extends' is used for class inheritance - Dog extends Animal means Dog inherits Animal's properties."),

                new Challenge(6, "Interface Implementation", "Implement the Runnable interface",
                        "class MyClass ___ Runnable { }",
                        Arrays.asList("implements", "extends", "inherits"),
                        "implements",
                        "Classes use a different keyword when working with interfaces, not extends.",
                        "Perfect! 'implements' is used when a class implements an interface contract like Runnable."),

                new Challenge(7, "Try-Catch", "Handle exceptions with try block",
                        "try { riskyCode(); } ___ (Exception e) { }",
                        Arrays.asList("catch", "throw", "finally"),
                        "catch",
                        "After 'try', what keyword is used to handle exceptions that might occur?",
                        "Excellent! 'catch' blocks handle exceptions thrown in the corresponding try block."),

                new Challenge(8, "ArrayList Add", "Add element to ArrayList",
                        "ArrayList<String> list = new ArrayList<>(); list.___(\"item\");",
                        Arrays.asList("add", "append", "insert"),
                        "add",
                        "ArrayList has a method to insert new elements. It's called exactly what the action is!",
                        "Correct! The 'add()' method inserts new elements into an ArrayList."),

                new Challenge(9, "Lambda Expression", "Filter stream for numbers > 5",
                        "stream.___(x -> x > 5);",
                        Arrays.asList("filter", "map", "reduce"),
                        "filter",
                        "When you want to select only certain elements that meet a condition, which stream method do you use?",
                        "Great! 'filter()' selects elements that satisfy the given predicate condition."),

                new Challenge(10, "Ternary Operator", "Complete the ternary operator",
                        "int max = (a > b) ? ___ : b;",
                        Arrays.asList("a", "b", "0"),
                        "a",
                        "In a ternary operator, what value is returned when the condition is true?",
                        "Perfect! In '(condition) ? valueIfTrue : valueIfFalse', when a > b is true, we return a.")
        );
    }

    private void initializeAdvancedChallenges() {
        advancedChallenges = Arrays.asList(
                new Challenge(1, "Generics", "Create a generic String list",
                        "List<___> names = new ArrayList<>();",
                        Arrays.asList("String", "Object", "Integer"),
                        "String",
                        "What type parameter would you use for a list that stores text values?",
                        "Excellent! 'List<String>' creates a type-safe list that can only contain String objects."),

                new Challenge(2, "Stream Filter", "Filter stream for values > 10",
                        "stream.___(x -> x > 10).collect(toList());",
                        Arrays.asList("filter", "map", "reduce"),
                        "filter",
                        "Which stream operation selects elements based on a condition?",
                        "Correct! 'filter()' creates a new stream containing only elements that match the predicate."),

                new Challenge(3, "Thread Creation", "Create a new Thread",
                        "Thread t = new Thread(___);",
                        Arrays.asList("runnable", "() -> {}", "new Runnable()"),
                        "runnable",
                        "Thread constructor expects a Runnable object that defines what code to run.",
                        "Perfect! Thread constructor takes a Runnable parameter that defines the code to execute."),

                new Challenge(4, "Override Annotation", "Mark method as overriding parent",
                        "@___ public void toString() { }",
                        Arrays.asList("Override", "Deprecated", "SuppressWarnings"),
                        "Override",
                        "Which annotation indicates that a method overrides a parent class method?",
                        "Great! '@Override' annotation marks methods that override parent class methods, helping catch errors."),

                new Challenge(5, "Exception Throwing", "Throw a new exception",
                        "if (error) ___ new IllegalArgumentException();",
                        Arrays.asList("throw", "throws", "catch"),
                        "throw",
                        "What keyword is used to actually throw an exception instance?",
                        "Correct! 'throw' is used to throw an exception instance, while 'throws' declares what exceptions a method might throw."),

                new Challenge(6, "Reflection", "Get class name using reflection",
                        "String className = obj.getClass().___();",
                        Arrays.asList("getName", "getSimpleName", "toString"),
                        "getName",
                        "Which method returns the full name of a class including package?",
                        "Perfect! 'getName()' returns the fully qualified class name including the package."),

                new Challenge(7, "File Reading", "Create BufferedReader for file",
                        "BufferedReader br = new BufferedReader(new ___(file));",
                        Arrays.asList("FileReader", "FileInputStream", "Scanner"),
                        "FileReader",
                        "Which class is used to read characters from a file and works with BufferedReader?",
                        "Excellent! 'FileReader' reads characters from files and is commonly wrapped in BufferedReader for efficiency."),

                new Challenge(8, "Synchronization", "Make method thread-safe",
                        "public ___ void increment() { count++; }",
                        Arrays.asList("synchronized", "static", "final"),
                        "synchronized",
                        "Which modifier makes a method thread-safe by allowing only one thread at a time?",
                        "Correct! 'synchronized' ensures only one thread can execute the method at a time, preventing race conditions."),

                new Challenge(9, "Stream Collect", "Collect stream results to List",
                        "List<String> result = stream.collect(Collectors.___());",
                        Arrays.asList("toList", "toSet", "toMap"),
                        "toList",
                        "Which Collectors method converts a stream into a List?",
                        "Great! 'Collectors.toList()' collects stream elements into a List collection."),

                new Challenge(10, "Lambda Function", "Identify lambda expression type",
                        "(String s) -> s.length() is a ___",
                        Arrays.asList("Function", "Predicate", "Consumer"),
                        "Function",
                        "A lambda that takes an input and returns a different output represents which functional interface?",
                        "Perfect! A lambda that transforms input to output represents a Function<T,R> functional interface.")
        );
    }

    public List<Challenge> getCurrentChallenges() {
        switch(settings.getDifficulty()) {
            case BEGINNER: return beginnerChallenges;
            case INTERMEDIATE: return intermediateChallenges;
            case ADVANCED: return advancedChallenges;
            default: return beginnerChallenges;
        }
    }

    public Challenge getChallenge(int index) {
        List<Challenge> list = getCurrentChallenges();
        if(index >= 0 && index < list.size()) {
            return list.get(index);
        }
        return null;
    }

    public int getTotalChallenges() {
        return 10; // SIEMPRE 10 desafíos
    }

    public boolean isValidChallengeIndex(int index) {
        return index >= 0 && index < 10; // SIEMPRE 10 desafíos
    }

    public DifficultyLevel getDifficulty() {
        return settings.getDifficulty();
    }
}