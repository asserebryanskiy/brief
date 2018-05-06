package com.name.brief.model.games.roleplay;

/**
 * This enum represents a list of salesman's competencies that are evaluated
 * by his partner with Doctor role during rolePlay game.
 */
public enum SalesmanCompetency {
    KNOWLEDGE("knowledge", "Знание продукта"),
    REVEALING_NEEDS("revealing-needs", "Выявление потребностей"),
    PRESENTATION("presentation", "Навыки презентации"),
    OBJECTIONS("objections", "Работа с возражениями");

    /**
     * cssClassName is used to name corresponding input in rolePlay.html template.
     *
     * Further, this name is used by client to package player's answer in
     * DoctorAnswerDto. It is then used by RolePlayUtils to retrieve player's
     * answer from DoctorAnswerDto.
     */
    private final String cssClassName;
    private final String russianName;

    SalesmanCompetency(String cssClassName, String russianName) {
        this.cssClassName = cssClassName;
        this.russianName = russianName;
    }

    public String getCssClassName() {
        return cssClassName;
    }

    public String getRussianName() {
        return russianName;
    }
}
