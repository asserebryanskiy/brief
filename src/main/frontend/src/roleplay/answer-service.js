import $ from "jquery";

export default class AnswerService {
    static createDoctorAnswerJson() {
        const dto = {
            'expertiseEstimations' : {},
            'comment' : $('.comments-input').val()
        };
        $('.ee-answer-variant.selected').each((i, el) => {
            const value = el.classList[1];
            const parentClass = $(el).parents('.expertise-estimation')[0].classList[1];
            const key = parentClass.substr('expertise-estimation-'.length);
            dto.expertiseEstimations[key] = value;
        });

        return JSON.stringify(dto);
    }

    static createSalesmanAnswerJson() {
        const dto = {
            'answers' : {}
        };

        $('.survey-salesman').find('input').each((i, el) => {
            const ind = el.classList[0].indexOf('-input');
            const key = el.classList[0].slice(0, ind);
            const value = $(el).val();
            dto.answers[key] = value.length === 0 ? 0 : parseInt(value);
        });

        return JSON.stringify(dto);
    }
}