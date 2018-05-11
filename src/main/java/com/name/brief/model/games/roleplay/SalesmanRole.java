package com.name.brief.model.games.roleplay;

import javax.persistence.Embeddable;

@Embeddable
public enum SalesmanRole implements GameRole, PharmaRole {
    SALESMAN_1(
            "Медицинский представитель",
                     "<p class=\"bold\">Кейс</p>Вы продвигаете лечебные сборы алтайских трав, выпускаемые компанией «Алтай-трава». \n" +
                     "<br><br>В настоящее время компания готовится к сезону ОРЗ и ваш визит будет посвящен продвижению сборов, которые снижают основные симптомы простуды, а также являются хорошим профилактическим средством.\n" +
                     "<br><br>Вы знаете, что ваши сборы легко доступны в аптеках, в отличие от многих конкурентов. Сборы производятся на самом современном оборудовании для обеспечения стабильности состава и качества. Для удобства пациентов, все смеси трав расфасованы в одноразовые пакетики.\n" +
                     "<br><br><p class=\"bold\">Ваши задачи:</p>" +
                     "1) Необходимо выяснить у доктора:\n" +
                     "<br>   - какое количество пациентов приходит к нему с симптомами респираторных заболеваний  за неделю, " +
                     "<br>   - какое количество назначений приходится на ваши сборы трав, " +
                     "<br>   - насколько он лоялен к вашему продукту, назначает ли продукты конкурентов, и если да, то по какой причине." +
                     "<br>2) Используйте свои знания для того, чтобы увеличить долю вашего продукта в выписке.\n" +
                     "<br>3) Сформулируйте адекватный прогноз продаж вашего продукта на территории данного ЛПУ на следующий месяц.\n"
    );

    private final String russianName;
    private final String instruction;

    SalesmanRole(String russianName, String instruction) {
        this.russianName = russianName;
        this.instruction = instruction;
    }

    @Override
    public String getRussianName() {
        return russianName;
    }

    @Override
    public String getInstruction() {
        return instruction;
    }

    @Override
    public String getGameStartText() {
        return "Представьтесь доктору и начните диалог";
    }

    @Override
    public String getCrossingText() {
        return "Направляйтесь в поликлинику";
    }
}
