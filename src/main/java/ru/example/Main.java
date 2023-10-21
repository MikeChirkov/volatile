package ru.example;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static AtomicInteger countThree = new AtomicInteger(0);
    public static AtomicInteger countFour = new AtomicInteger(0);
    public static AtomicInteger countFive = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        Random random = new Random();
        String[] texts = new String[100_000];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("abc", 3 + random.nextInt(3));
        }

        Thread threadPoly = new Thread(() -> {
            Arrays.stream(texts).forEach((x) -> {
                if (x.equalsIgnoreCase(new StringBuilder(x)
                        .reverse().toString())
                        && !oneLitterCheck(x)) {
                    addCounter(x);
                }
            });
        });

        Thread threadOneLetter = new Thread(() -> {
            Arrays.stream(texts).forEach((x) -> {
                if (oneLitterCheck(x)) {
                    addCounter(x);
                }
            });
        });

        Thread threadSorted = new Thread(() -> {
            Arrays.stream(texts).forEach((x) -> {
                if (x.equalsIgnoreCase(sortString(x))) {
                    addCounter(x);
                }
            });
        });

        threadPoly.start();
        threadOneLetter.start();
        threadSorted.start();

        threadPoly.join();
        threadOneLetter.join();
        threadSorted.join();

        System.out.printf("Красивых слов с длиной %d: %d шт\n", 3, countThree.get());
        System.out.printf("Красивых слов с длиной %d: %d шт\n", 4, countFour.get());
        System.out.printf("Красивых слов с длиной %d: %d шт\n", 5, countFive.get());
    }

    private static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    private static void addCounter(String text) {
        switch (text.length()) {
            case 3:
                countThree.incrementAndGet();
                break;
            case 4:
                countFour.incrementAndGet();
                break;
            case 5:
                countFive.incrementAndGet();
                break;
            default:
                break;
        }
    }

    private static String sortString(String text) {
        return Stream.of(text.split(""))
                .sorted()
                .collect(Collectors.joining());
    }

    private static Boolean oneLitterCheck(String text) {
        String first = String.valueOf(text.charAt(0));
        return Stream.of(text.split("")).allMatch(x -> x.equalsIgnoreCase(first));
    }
}