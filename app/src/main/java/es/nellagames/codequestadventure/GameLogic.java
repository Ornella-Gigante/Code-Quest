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
                new Challenge(1, "¡Hola Mundo! 👋",
                        "¡Completa tu primer programa!",
                        "System.out.println(___);",
                        Arrays.asList("\"¡Hola Mundo!\"", "¡Hola Mundo!", "hola mundo"),
                        "\"¡Hola Mundo!\"",
                        "¡Los textos necesitan comillas en Java!"),

                new Challenge(2, "Variables 📦",
                        "Declara una variable entera",
                        "int edad = ___;",
                        Arrays.asList("10", "\"10\"", "10.0"),
                        "10",
                        "¡Los enteros son números sin comillas!"),

                new Challenge(3, "Operaciones Matemáticas ➕",
                        "Completa la suma",
                        "int resultado = 5 + ___;",
                        Arrays.asList("3", "\"3\"", "cinco"),
                        "3",
                        "¡Genial! 5 + 3 = 8"),

                new Challenge(4, "Lógica Booleana 🤖",
                        "¿Qué hace esta condición verdadera?",
                        "boolean esNiño = edad ___ 18;",
                        Arrays.asList("<", ">", "=="),
                        "<",
                        "¡Menor que (<) verifica si la edad es menor a 18!"),

                new Challenge(5, "Condicionales If 🤔",
                        "Completa la condición",
                        "if (puntos ___ 100) { ganar(); }",
                        Arrays.asList("==", "<", ">"),
                        "==",
                        "¡El puntaje perfecto es exactamente igual a 100!"),

                new Challenge(6, "Bucles 🔄",
                        "¿Cuántas veces se ejecuta este bucle?",
                        "for(int i = 0; i < ___; i++) { }",
                        Arrays.asList("5", "4", "6"),
                        "5",
                        "¡El bucle va de 0 a 4, eso son 5 veces!"),

                new Challenge(7, "Arrays 📚",
                        "Accede al primer elemento",
                        "int primero = numeros[___];",
                        Arrays.asList("0", "1", "primero"),
                        "0",
                        "¡Los arrays empiezan en índice 0 en Java!"),

                new Challenge(8, "Métodos 🛠️",
                        "Llama al método saltar",
                        "jugador.___();",
                        Arrays.asList("saltar()", "saltar", "Saltar()"),
                        "saltar()",
                        "¡Los métodos necesitan paréntesis para ser llamados!"),

                new Challenge(9, "Longitud de String 📏",
                        "Obtén la longitud de un texto",
                        "int longitud = nombre.___;",
                        Arrays.asList("length()", "size()", "count()"),
                        "length()",
                        "¡Los Strings usan el método length() en Java!"),

                new Challenge(10, "¡Desafío Final! 🏆",
                        "Completa el bucle del juego",
                        "while (juego.___()) { jugar(); }",
                        Arrays.asList("estaEjecutando()", "ejecutando", "jugar()"),
                        "estaEjecutando()",
                        "¡Perfecto! ¡Has dominado los conceptos básicos de Java!")
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
