package org.example;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static final String letters = "abc";
    private static final int length = 100_000;
    public static void main(String[] args) {

        ArrayBlockingQueue<String> arA = new ArrayBlockingQueue<>(100);
        ArrayBlockingQueue<String> arB = new ArrayBlockingQueue<>(100);
        ArrayBlockingQueue<String> arC = new ArrayBlockingQueue<>(100);


        ExecutorService service = Executors.newFixedThreadPool(4);

        service.submit(() -> {
            for (int i = 0; i < 10_000; i++) {
                try {
                    arA.put(generateText(letters, length));
                    arB.put(generateText(letters, length));
                    arC.put(generateText(letters, length));

                   } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        service.submit(() -> {
            String maxChars = null;
            int maxLengthChars = 0;
            for (int i = 0; i < 10_000; i++) {
                if(maxChars == null) {
                    try {
                        maxChars = arA.take();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    maxLengthChars = maxCharsText(maxChars, 'a');
                    continue;
                }
                String text;
                try {
                    text = arA.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                int lengthText = maxCharsText(maxChars, 'a');
                if(lengthText > maxLengthChars) {
                    maxChars = text;
                    maxLengthChars = lengthText;
                }
            }
            System.out.println("Самое большое количество символов в строке: " + maxLengthChars);
        });

        service.submit(() -> {
            String maxChars = null;
            int maxLengthChars = 0;
            for (int i = 0; i < 10_000; i++) {
                if(maxChars == null) {
                    try {
                        maxChars = arB.take();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    maxLengthChars = maxCharsText(maxChars, 'b');
                    continue;
                }
                String text;
                try {
                    text = arB.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                int lengthText = maxCharsText(maxChars, 'b');
                if(lengthText > maxLengthChars) {
                    maxChars = text;
                    maxLengthChars = lengthText;
                }
            }
            System.out.println("Самое большое количество символов в строке: " + maxLengthChars);
        });

        service.submit(() -> {
            String maxChars = null;
            int maxLengthChars = 0;
            for (int i = 0; i < 10_000; i++) {
                if(maxChars == null) {
                    try {
                        maxChars = arC.take();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    maxLengthChars = maxCharsText(maxChars, 'c');
                    continue;
                }
                String text ;
                try {
                    text = arC.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                int lengthText = maxCharsText(maxChars, 'c');
                if(lengthText > maxLengthChars) {
                    maxChars = text;
                    maxLengthChars = lengthText;
                }
            }
            System.out.println("Самое большое количество символов в строке: " + maxLengthChars);
        });

        service.shutdown();
    }

    public static int maxCharsText(String text, char ch) {
        return (int) text.codePoints().filter(c -> c == ch).count();
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}