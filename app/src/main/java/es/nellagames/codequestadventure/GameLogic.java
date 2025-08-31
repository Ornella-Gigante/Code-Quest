package es.nellagames.codequestadventure;

import java.util.Arrays;
import java.util.List;

import es.nellagames.codequestadventure.Challenge;
import es.nellagames.codequestadventure.GameSettings;

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
                        "Strings need quotes",
                        "Remember strings require quotes"),

                new Challenge(2, "Declare Variable", "Declare integer variable",
                        "int age = ___;",
                        Arrays.asList("10", "\"10\"", "ten"),
                        "10",
                        "Integer no quotes",
                        "Use numeric literals without quotes"),

                new Challenge(3, "Addition", "Complete addition",
                        "int sum = 5 + ___;",
                        Arrays.asList("3", "\"3\"", "three"),
                        "3",
                        "Numbers not strings",
                        "Use numbers for math"),

                new Challenge(4, "Boolean Compare", "Check less than",
                        "age ___ 18;",
                        Arrays.asList("<", ">", "=="),
                        "<",
                        "Less than operator",
                        "Use the '<' operator"),

                new Challenge(5, "If Statement", "Complete if condition",
                        "if(age ___ 18) {}",
                        Arrays.asList("<", ">", "=="),
                        "<",
                        "If block runs for kids",
                        "Use less than operator"),

                new Challenge(6, "For Loop", "Loop from 0 to 4",
                        "for(int i=0; i < ___; i++) {}",
                        Arrays.asList("5", "4", "6"),
                        "5",
                        "Loop 5 times",
                        "Use upper bound on loop"),

                new Challenge(7, "Array Access", "Get first item",
                        "arr[___];",
                        Arrays.asList("0", "1", "first"),
                        "0",
                        "Arrays in Java start at 0",
                        "Indices start from zero"),

                new Challenge(8, "String Length", "Get length of string",
                        "str.___;",
                        Arrays.asList("length()", "size()", "count()"),
                        "length()",
                        "Strings use length()",
                        "Use method length()"),

                new Challenge(9, "Print Variable", "Print variable",
                        "System.out.println(___);",
                        Arrays.asList("age", "\"age\"", "’age’"),
                        "age",
                        "Print variable value",
                        "Do not use quotes when printing variables"),

                new Challenge(10, "Comments", "Single line comment",
                        "// ___",
                        Arrays.asList("This is a comment", "Comment", "/* comment */"),
                        "This is a comment",
                        "Use // for single line comment",
                        "Use // to comment")
        );
    }

    private void initializeIntermediateChallenges() {
        intermediateChallenges = Arrays.asList(
                new Challenge(1, "While Loop", "Loop while condition true",
                        "while(___) {}",
                        Arrays.asList("condition", "true", "false"),
                        "condition",
                        "Loops until condition false",
                        "Use a condition for while loop"),

                new Challenge(2, "Switch Statement", "Switch case selection",
                        "switch(var) { case ___: break; }",
                        Arrays.asList("1", "one", "default"),
                        "1",
                        "Case labels are values",
                        "Use exact value for case"),

                new Challenge(3, "Method Declaration", "Declare void method",
                        "public ___ myMethod() {}",
                        Arrays.asList("void", "int", "String"),
                        "void",
                        "Void means no return",
                        "Use void for no return"),

                new Challenge(4, "Class Definition", "Define class",
                        "public class ___ {}",
                        Arrays.asList("MyClass", "myclass", "class"),
                        "MyClass",
                        "Class names start uppercase",
                        "Classes uppercase names"),

                new Challenge(5, "Inheritance", "Extend class",
                        "class Dog ___ Animal {}",
                        Arrays.asList("extends", "implement", "inherits"),
                        "extends",
                        "Inheritance uses extends",
                        "Use extends keyword"),

                new Challenge(6, "Interface Implementation", "Implement interface",
                        "class MyClass ___ Runnable {}",
                        Arrays.asList("implements", "extends", "inherits"),
                        "implements",
                        "Classes use implements for interfaces",
                        "Use implements"),

                new Challenge(7, "Try-Catch", "Catch exception",
                        "try { } ___ (Exception e) {}",
                        Arrays.asList("catch", "throw", "finally"),
                        "catch",
                        "Catch exceptions after try",
                        "Use catch block"),

                new Challenge(8, "ArrayList Add", "Add element",
                        "list.___(value);",
                        Arrays.asList("add", "append", "insert"),
                        "add",
                        "Add inserts elements",
                        "Use add method"),

                new Challenge(9, "Lambda Expression", "Filter stream",
                        "stream.___(x -> x > 5);",
                        Arrays.asList("filter", "map", "reduce"),
                        "filter",
                        "Filter selects elements",
                        "Use filter for selection"),

                new Challenge(10, "Ternary Operator", "Use ternary",
                        "int x = (a > b) ? ___ : b;",
                        Arrays.asList("a", "b", "c"),
                        "a",
                        "Returns a if condition true",
                        "Use ternary operator")
        );
    }

    private void initializeAdvancedChallenges() {
        advancedChallenges = Arrays.asList(
                new Challenge(1, "Generics", "Create generic list",
                        "List<___> list = new ArrayList<>();",
                        Arrays.asList("String", "int", "Object"),
                        "String",
                        "Generics require type parameter",
                        "Use String for list of strings"),

                new Challenge(2, "Streams", "Use stream filter",
                        "stream.___(x -> x > 10);",
                        Arrays.asList("filter", "map", "reduce"),
                        "filter",
                        "Filter selects matching elements",
                        "Use filter for selection"),

                new Challenge(3, "Concurrency", "Create thread",
                        "Thread t = new Thread(___);",
                        Arrays.asList("runnable", "thread", "run"),
                        "runnable",
                        "Threads need Runnable object",
                        "Pass Runnable to Thread constructor"),

                new Challenge(4, "Annotations", "Use override annotation",
                        "@___ public void run() {}",
                        Arrays.asList("Override", "Implement", "Run"),
                        "Override",
                        "Override marks method overriding",
                        "Use @Override annotation"),

                new Challenge(5, "Exception Handling", "Throw exception",
                        "throw new ___();",
                        Arrays.asList("IOException", "Exception", "Error"),
                        "Exception",
                        "Throw exceptions on error",
                        "Use throw keyword"),

                new Challenge(6, "Reflection", "Get class name",
                        "obj.getClass().___();",
                        Arrays.asList("getName", "getClass", "toString"),
                        "getName",
                        "Returns class name",
                        "Use getName() method"),

                new Challenge(7, "File IO", "Read file",
                        "BufferedReader br = new BufferedReader(new ___(file));",
                        Arrays.asList("FileReader", "File", "InputStream"),
                        "FileReader",
                        "Reads characters from file",
                        "Use FileReader with BufferedReader"),

                new Challenge(8, "Synchronization", "Synchronize method",
                        "public synchronized void ___() {}",
                        Arrays.asList("run", "execute", "methodName"),
                        "run",
                        "Synchronize method",
                        "Add synchronized keyword"),

                new Challenge(9, "Streams API", "Collect to list",
                        "stream.collect(Collectors.___());",
                        Arrays.asList("toList", "toSet", "toMap"),
                        "toList",
                        "Collect stream to list",
                        "Use Collectors.toList()"),

                new Challenge(10, "Lambdas", "Create lambda expression",
                        "(a, b) -> a + b is ___",
                        Arrays.asList("bi-function", "function", "lambda"),
                        "lambda",
                        "Lambdas implement functional interfaces",
                        "Use lambdas for brevity")
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
        return 10;
    }

    public boolean isValidChallengeIndex(int index) {
        return index >= 0 && index < getTotalChallenges();
    }

    public DifficultyLevel getDifficulty() {
        return settings.getDifficulty();
    }
}
