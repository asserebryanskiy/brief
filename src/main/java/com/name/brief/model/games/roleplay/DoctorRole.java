package com.name.brief.model.games.roleplay;

import javax.persistence.Embeddable;

public enum DoctorRole {
    GOOD("Доктор",
            "Вы хороший доктор. Вы советуете своим пациентам те лекарства, " +
            "которые показали лучшие результаты клинических испытаний " +
            "и при этом соответствуют их кошельку."),
    BAD("Доктор",
            "Вы плохой доктор. Вы советуете своим пациентам те лекарства, " +
            "которые дадут вам наибольшую комиссию."),
    UGLY("Доктор",
            "Вы злой доктор. Вы советуете своим пациентам попробовать народную " +
            "медицину вместо всех этих новомодных западных практик.");

    private final String russianName;
    private final String instruction;

    DoctorRole(String russianName, String instruction) {
        this.russianName = russianName;
        this.instruction = instruction;
    }

    public String getRussianName() {
        return this.russianName;
    }

    public String getInstruction() {
        return this.instruction;
    }
}
