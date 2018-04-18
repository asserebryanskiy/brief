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
            score += getScoreForSector(i, j, answerMatrix[i][j])
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
    const val = el.innerText;
    console.log(Math.floor(i / 4));
    correctAnswers[Math.floor(i / 4)][i % 4] = val;
});

function getScoreForSector(row, column, answer) {
    // scoring varies depending on correct answer
    switch (correctAnswers[row][column]) {
        case -1: return 0;
        case 0:
            switch (answer) {
                case -1: return -100;
                case 0: return 100;
                case 1: return 50;
                case 2: return 25;
                case 3: return 0;
            }
            break;
        case 1:
            switch (answer) {
                case -1: return -200;
                case 0: return 50;
                case 1: return 200;
                case 2: return 100;
                case 3: return 50;
            }
            break;
        case 2:
            switch (answer) {
                case -1: return -300;
                case 0: return 25;
                case 1: return 100;
                case 2: return 300;
                case 3: return 150;
            }
            break;
        case 3:
            switch (answer) {
                case -1: return -400;
                case 0: return 0;
                case 1: return 50;
                case 2: return 150;
                case 3: return 400;
            }
            break;
    }
}

function getAnswerMatrix(answer) {
    const answerMatrix = new Array(3);
    for (let i = 0; i < 3; i++) {
        const inner = new Array(4);
        inner.fill(-1);
        answerMatrix[i] = inner;
    }
    if (answer.length === 0) return answerMatrix;
    let acc = '';
    let sector = 0;
    for (let i = 0; i < answer.length; i++) {
        const letter = answer.charAt(i);
        if (letter === '-') {
            sector = parseInt(acc);
            acc = '';
        } else if (letter === ',') {
            answerMatrix[Math.floor(sector / 4)][sector % 4] = parseInt(acc);
            acc = '';
            sector = 0;
        } else {
            acc += letter;
        }
    }
    answerMatrix[Math.floor(sector / 4)][sector % 4] = parseInt(acc);
    return answerMatrix;
}