package com.example.spring.solid;

import java.util.ArrayList;

public class Srp {
    public static class Journal {
        private ArrayList<String> entries = new ArrayList<>();

        void add(String text) {
            entries.add(text);
        }

        String getText() {
            StringBuilder sb = new StringBuilder();

            for (String e : entries) {
                sb.append("- ").append(e).append("\n");
            }

            return sb.toString();
        }
    }

    public static class JournalSaver {
        void print(Journal j) {
            System.out.println(j.getText());
        }
    }
}
