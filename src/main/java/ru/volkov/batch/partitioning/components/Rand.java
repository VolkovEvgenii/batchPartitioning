package ru.volkov.batch.partitioning.components;

import java.time.LocalDateTime;

public class Rand {

    private int id;
    private String randomText;
    private LocalDateTime randomData;

    public Rand() {
    }

    public Rand(int id, String randomText, LocalDateTime randomData) {
        this.id = id;
        this.randomText = randomText;
        this.randomData = randomData;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRandomText() {
        return randomText;
    }

    public void setRandomText(String randomText) {
        this.randomText = randomText;
    }

    public LocalDateTime getRandomData() {
        return randomData;
    }

    public void setRandomData(LocalDateTime randomData) {
        this.randomData = randomData;
    }

    @Override
    public String toString() {
        return "Rand{" +
                "id=" + id +
                ", text='" + randomText + '\'' +
                ", dateTime=" + randomData +
                '}';
    }
}
