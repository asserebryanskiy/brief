package com.name.brief.model.games.roleplay;

/**
 * This enum represents a list of answers on question that salesman receives
 * after an interview with his partner with Doctor role during RolePlay game.
 */
public enum SalesmanAnswerType {
    NUMBER_OF_PATIENTS(
            "patient-number",
            "Количество пациентов",
            "Какое количество пациентов приходит к врачу с симптомами респираторных заболеваний  за неделю?"
    ),
    NUMBER_OF_RECIPES(
            "recipe-number",
            "Количество текущих назначений",
            "Какое количество назначений приходится на ваши сборы трав?"
    ),
    SELL_FORECAST(
            "forecast",
            "Прогноз продаж",
            "Ваш прогноз продаж продукта на территории этого ЛПУ:"
    );

    private final String cssClassName;
    private final String russianName;
    private final String russianQuestion;

    SalesmanAnswerType(String cssClassName, String russianName, String russianQuestion) {
        this.cssClassName = cssClassName;
        this.russianName = russianName;
        this.russianQuestion = russianQuestion;
    }

    public String getCssClassName() {
        return cssClassName;
    }

    public String getRussianName() {
        return russianName;
    }

    public String getRussianQuestion() {
        return russianQuestion;
    }
}
