package org.example;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static final String letters = "abc";
    private static final int length = 10_000;

    static ArrayBlockingQueue<String> arA = new ArrayBlockingQueue<>(100);
    static ArrayBlockingQueue<String> arB = new ArrayBlockingQueue<>(100);
    static ArrayBlockingQueue<String> arC = new ArrayBlockingQueue<>(100);

    public static void main(String[] args) {


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

        service.submit(new MyRunnable('a', arA));
        service.submit(new MyRunnable('b', arB));
        service.submit(new MyRunnable('c', arC));

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

    static class MyRunnable implements Runnable {

        private final char aChar;
        private final ArrayBlockingQueue<String> arr;

        public MyRunnable(char aChar, ArrayBlockingQueue<String> arr) {
            this.aChar = aChar;
            this.arr = arr;
        }

        @Override
        public void run() {
            String maxChars = null;
            int maxLengthChars = 0;
            for (int i = 0; i < 10_000; i++) {
                if (maxChars == null) {
                    try {
                        maxChars = arr.take();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    maxLengthChars = maxCharsText(maxChars, aChar);
                    continue;
                }
                String text;
                try {
                    text = arr.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                int lengthText = maxCharsText(maxChars, aChar);
                if (lengthText > maxLengthChars) {
                    maxChars = text;
                    maxLengthChars = lengthText;
                }
            }
            System.out.println("Самое большое количество символов в строке: " + maxLengthChars);
        }
    }
}