$('.answer-input-slider').on('input', null, null, (event) => {
    const $slider = $(event.currentTarget);
    const val = parseInt($slider.val());

    // change svg in popup
    $slider.siblings('svg').remove();
    const $newSvg = $($('svg.level-' + val)[0]).clone();
    $newSvg.insertAfter($slider);

    // change svg in risk-indicator
    const $oldSvg = $slider.parents('.risk-img-cell').find('.risk-indicator svg');
    $newSvg.clone().insertAfter($oldSvg);
    $oldSvg.remove();
});

/************************************
 *       OVERRIDDEN FUNCTIONS       *
 ************************************/

function onCorrectAnswerPhase() {
    const answerStr = getAnswerStr();
    const answerMatrix = getAnswerMatrix(answerStr);
    $('#score-text').text(getTotalScore(answerMatrix));
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
    console.log(answer);
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