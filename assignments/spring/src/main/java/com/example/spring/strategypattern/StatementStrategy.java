package com.example.spring.strategypattern;

@FunctionalInterface
public interface StatementStrategy {
    void run(Database db);
}
