#!/bin/bash

echo "=== Тестирование команды list ==="
./gradlew run --args="list --tasks-file='data/tasks.csv'"

echo "=== Тестирование команды show ==="
./gradlew run --args="show --task-id=ec247f48-f86c-45c8-ab2f-7510e4640052 --tasks-file='data/tasks.csv'"

echo "=== Тестирование команды list-eisenhower ==="
./gradlew run --args="list-eisenhower --urgent=true --tasks-file='data/tasks.csv'"
./gradlew run --args="list-eisenhower --urgent=false --tasks-file='data/tasks.csv'"
./gradlew run --args="list-eisenhower --important=false --tasks-file='data/tasks.csv'"
./gradlew run --args="list-eisenhower --important=false --tasks-file='data/tasks.csv'"
./gradlew run --args="list-eisenhower --important=true --urgent=true --tasks-file='data/tasks.csv'"
./gradlew run --args="list-eisenhower --important=false --urgent=true --tasks-file='data/tasks.csv'"
./gradlew run --args="list-eisenhower --important=true --urgent=false --tasks-file='data/tasks.csv'"
./gradlew run --args="list-eisenhower --important=false --urgent=false --tasks-file='data/tasks.csv'"

echo "=== Тестирование команды list-time ==="
./gradlew run --args="list-time --time=2024-01-01T00:00:00.0 --tasks-file='data/tasks.csv'"
./gradlew run --args="list-time --time=2024-01-05T12:30:00.0 --tasks-file=data/tasks.csv"

echo "=== Тестирование команды statistic ==="
./gradlew run --args="statistic --by-date=registration --tasks-file=data/tasks.csv"
./gradlew run --args="statistic --by-date=start --tasks-file=data/tasks.csv"
./gradlew run --args="statistic --by-date=end --tasks-file=data/tasks.csv"
