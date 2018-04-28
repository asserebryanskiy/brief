package com.name.brief.model.games.roleplay;

import java.util.Arrays;

public enum PharmaRole {
    DOCTOR_GOOD("Доктор",
            "Вы хороший доктор. Вы советуете своим пациентам те лекарства, " +
                    "которые показали лучшие результаты клинических испытаний " +
                    "и при этом соответствуют их кошельку.", true),
    DOCTOR_BAD("Доктор",
            "Вы плохой доктор. Вы советуете своим пациентам те лекарства, " +
                    "которые дадут вам наибольшую комиссию.", true),
    DOCTOR_UGLY("Доктор",
            "Вы злой доктор. Вы советуете своим пациентам попробовать народную " +
                    "медицину вместо всех этих новомодных западных практик.", true),
    SALESMAN("Медицинский представитель",
            "Ваша задача продать лекарство. Любой ценой.", false);

    private final String russianName;
    private final String instruction;
    private final boolean doctorRole;

    PharmaRole(String russianName, String instruction, boolean doctorRole) {
        this.russianName = russianName;
        this.instruction = instruction;
        this.doctorRole = doctorRole;
    }

    public static PharmaRole[] getDoctorRoles() {
        return Arrays.stream(PharmaRole.values())
                .filter(PharmaRole::isDoctorRole)
                .toArray(PharmaRole[]::new);
    }

    public String getRussianName() {
        return this.russianName;
    }

    public String getInstruction() {
        return instruction;
    }

    public boolean isDoctorRole() {
        return doctorRole;
    }
}
