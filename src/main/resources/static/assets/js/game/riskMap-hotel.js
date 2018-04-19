$('.answer-input-slider').on('input', null, null, (event) => {
    const $statusWrapper = $(event.currentTarget).siblings('.answer-popup-status-wrapper');
    const val = parseInt(event.currentTarget.value);

    // change svg in popup
    const $oldSvg = $statusWrapper.find('svg');
    const $newSvg = $($('svg.level-' + val)[0]).clone();
    $newSvg.insertAfter($oldSvg);
    $oldSvg.remove();

    // show/hide confidence status, change violation status text
    const $confidenceStatus = $statusWrapper.find('.confidence-status');
    const $violationStatus = $statusWrapper.find('.violation-status');
    if (val < 3) {
        $confidenceStatus.show();
        $violationStatus.text('Есть нарушение');
    } else if (val > 3) {
        $confidenceStatus.show();
        $violationStatus.text('Нет нарушения');
    }
    else {
        $confidenceStatus.hide();
        $violationStatus.text('Нет ответа');
    }
    // change violation status color
    $violationStatus.removeClass().addClass('violation-status level-' + val);

    // change confidence status text
    const $confidenceLevelSpan = $confidenceStatus.find('.confidence-level-span');
    if (val === 0 || val === 6) $confidenceLevelSpan.text('Высокая');
    if (val === 1 || val === 5) $confidenceLevelSpan.text('Средняя');
    if (val === 2 || val === 4) $confidenceLevelSpan.text('Низкая');

    // change svg in risk-indicator both in phase-1 and phase-2
    const $oldIndicator = $('.' + $statusWrapper.parents('.risk-img-cell')[0].classList[1])
        .find('.risk-indicator svg');
    $newSvg.clone().insertAfter($oldIndicator);
    $oldIndicator.remove();
});

$('#show-correct-answers-btn').click(() => $('.correct-answer-cover').slideToggle());

/************************************
 *       OVERRIDDEN FUNCTIONS       *
 ************************************/

function onCorrectAnswerPhase() {
    const answerStr = getAnswerStr();
    const answerMatrix = getAnswerMatrix(answerStr);
    const totalScore = getTotalScore(answerMatrix);
    $('#score-text').text(totalScore);
    $('.sector-score-text').each((i, el) => {
        const row = Math.floor(i / 4);
        const col = i % 4;
        const answer = answerMatrix[row][col];
        const score = getScoreForSector(row, col, answer);
        $(el).text(score);
        const $caPopup = $(el).parents('.risk-img-cell').find('.correct-answer-popup');
        $caPopup.find('.answer-variants-grid > svg')
            .removeClass('selected')
            .filter('.level-' + answer)
            .addClass('selected');
        $caPopup.find('.your-answer-span').text(score);
    });

    let congratulationText = '';
    if (totalScore < 0) congratulationText = 'К сожалению, вы проиграли. Гости отеля либо очень недовольны, либо уехали с ощущением "ничего особенного". Может, вам удастся повысить планку?';
    else if (totalScore < 121) congratulationText = 'Неплохо! Вы попали в 50% лучших в рейтинге. Хотя общая ситуация в отеле кажется хорошей, гости возмущены поведением отдельных сотрудников. Есть над чем работать!';
    else if (totalScore < 300) congratulationText = 'Хорошая работа! Ваш результат попал в 20% лучших в рейтинге. Большинство клиентов приедут к вам снова, но некоторые из них все же уехали с ровными или не очень приятными впечатлениями';
    else congratulationText = 'Поздравляем! Вы попали в 5% лучших в рейтинге. Вы точно знаете свое дело, а гости буквально влюбились в ваш отель и уверенно советуют его всем своим знакомым.';
    $('.congratulation-text').text(congratulationText);
}

function onRoundChange() {

}

function getAnswerStr() {
    let answerStr = '';
    $('.answer-input-slider').each((i, el) => {
        answerStr += '' + i + '-' + $(el).val();
        if (i !== 11) answerStr += ',';
    });

    return answerStr;
}

/************************************
 *         HELPER FUNCTIONS         *
 ************************************/

function getTotalScore(answerMatrix) {
    let score = 0;
    for (let i = 0; i < 3; i++) {
        for (let j = 0; j < 4; j++) {
            score += getScoreForSector(i, j, answerMatrix[i][j]);
        }
    }

    return score;
}

const correctAnswers = [
    [0,0,0,0],
    [0,0,0,0],
    [0,0,0,0]
];
$('.correct-answer-store').children().each((i, el) => {
    correctAnswers[Math.floor(i / 4)][i % 4] = parseInt(el.innerText);
});

function getScoreForSector(row, column, answer) {
    // scoring varies depending on correct answer
    switch (correctAnswers[row][column]) {
        case 0: // no violation
            switch (answer) {
                case 0: return -50;
                case 1: return -25;
                case 2: return -10;
                case 3: return -5;
                case 4: return 10;
                case 5: return 20;
                case 6: return 30;
            }
            break;
        case 1: // violation
            switch (answer) {
                case 0: return 30;
                case 1: return 20;
                case 2: return 10;
                case 3: return -5;
                case 4: return -10;
                case 5: return -25;
                case 6: return -50;
            }
    }
}

function getAnswerMatrix() {
    const answerMatrix = [
        [3,3,3,3],
        [3,3,3,3],
        [3,3,3,3]
    ];
    $('.answer-input-slider').each((i, el) => {
        answerMatrix[Math.floor(i / 4)][i % 4] = parseInt(el.value);
    });
    return answerMatrix;
}