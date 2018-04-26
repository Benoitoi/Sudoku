package fr.benoitjaouen.sudoku;

import java.io.Serializable;

public class Grid implements Serializable{
    int level;
    int number;
    int percentageDone;
    String model;
    String timeDone;
    String modelAnswer;

    public Grid(int level, int number, int percentageDone, String model, String timeDone, String modelAnswer) {
        this.level = level;
        this.number = number;
        this.percentageDone = percentageDone;
        this.model = model;
        this.timeDone = timeDone;
        this.modelAnswer = modelAnswer;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getPercentageDone() {
        return percentageDone;
    }

    public void setPercentageDone(int percentageDone) {
        this.percentageDone = percentageDone;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getTimeDone() {
        return timeDone;
    }

    public void setTimeDone(String timeDone) {
        this.timeDone = timeDone;
    }

    public String getModelAnswer() {
        return modelAnswer;
    }

    public void setModelAnswer(String modelAnswer) {
        this.modelAnswer = modelAnswer;
    }
}
