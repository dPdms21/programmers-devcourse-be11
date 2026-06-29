package com.example.spring.exception;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class FileLogger {
    private final File logDir;
    private final String minLevel;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final Map<String, Integer> LEVELS = Map.of(
            "INFO", 1,
            "WARN", 2,
            "ERROR", 3
    );

    FileLogger(String minLevel) {
        if (!LEVELS.containsKey(minLevel)) {
            throw new IllegalArgumentException("지원하지 않는 로그 레벨: " + minLevel);
        }

        this.logDir = new File("src/main/resources/logs");
        this.minLevel = minLevel;
    }

    private File getLogFile() {
        String date = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        return new File(logDir, "app-" + date + ".log");
    }

    void log(String level, String message) {
        if (LEVELS.get(level) < LEVELS.get(minLevel)) {
            return;
        }

        if (!logDir.exists()) {
            logDir.mkdirs();
        }

        File logFile = getLogFile();

        String line = LocalDateTime.now().format(FMT)
                + " [" + level + "] "
                + message
                + System.lineSeparator();

        try (FileWriter fw = new FileWriter(logFile, true)) {
            fw.write(line);
        }
        catch (IOException e) {
            System.out.println("로그 기록 실패: " + e.getMessage());
        }
    }

    String getLogFilePath() {
        return getLogFile().getAbsolutePath();
    }
}
