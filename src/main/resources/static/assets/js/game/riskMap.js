const SEND_ANSWER_PHASE = 1;
const CORRECT_ANSWERS_PHASE = 2;

const currentSectorNumber = parseInt($('#currents-sector-number').text());

$('.small-img-wrapper').click((event) => {
    const $target = $(event.currentTarget);
    const imgId = $target.find('img').attr('id').substr(0, 2);
    const $popup = $target.parent().find('.popup-wrapper');
    const $popupContent = $popup.find('.popup-content');
    if ($popupContent.children('img').length === 0) {
        $('#' + imgId + '-large-img').insertBefore($popupContent.children('.answer-inputs'));
        // $popupContent.children('.answer-inputs').insertAf.append($('#' + imgId + '-large-img'))
    }
    $popup.show();
});

$('.answer-input').click((event) => {
    if (answerSendingEnabled) {
        toggleSelected(event);
    }
});

$('#show-correct-answers-btn').click(() => {
    $('.correct-answer-cover').slideToggle();
    $('#phase-2 .small-img-wrapper').each((i, el) => {
        const $correctAnswerCover = $(el).find('.correct-answer-cover');

        function getDangerLevel($element) {
            return $element[0].classList[$element[0].classList.length - 1];
        }

        if ($correctAnswerCover.length > 0 &&
            getDangerLevel($(el).find('.risk-indicator')) === getDangerLevel($correctAnswerCover)) {
            $(el).find('.correct-answer-icon').toggle();
        }
    })
});
/************************************
 *       OVERRIDDEN FUNCTIONS       *
 ************************************/

function getAnswerStr() {
    let answerStr = '';
    $('#phase-1 .risk-img-cell').each((i, el) => {
        const $indicator = $(el).find('.risk-indicator');
        if ($indicator.hasClass('no-level')) answerStr += i + '-0,';
        if ($indicator.hasClass('low-level')) answerStr += i + '-1,';
        if ($indicator.hasClass('mid-level')) answerStr += i + '-2,';
        if ($indicator.hasClass('high-level')) answerStr += i + '-3,';

        // delete last comma
        if (i === 11 && answerStr.length > 0) answerStr = answerStr.substr(0, answerStr.length - 1);
    });
    return answerStr;
}

function toggleSelected(event) {
    const $target = $(event.currentTarget);
    // clear self-made radio-buttons
    $target.siblings().removeClass('selected');

    // toggle selection of clicked input
    $target.toggleClass('selected');

    // remove indicator from img-cell if any
    const $indicator = $('.' + $target.parents('.risk-img-cell')[0].classList[1])
        .find('.risk-indicator');
    $indicator.removeClass('no-level low-level mid-level high-level');

    if ($target.hasClass('selected')) {
        // add new indicator to img-cell
        const className = $target.hasClass('low-level') ? 'low-level'
            : $target.hasClass('no-level') ? 'no-level'
                : $target.hasClass('mid-level') ? 'mid-level' : 'high-level';
        $indicator.addClass(className);
    }
}

/************************************
 *       OVERRIDDEN FUNCTIONS       *
 ************************************/


/************************************
 *        CONTROLLER SETTINGS       *
 ************************************/

controller.setOnRoundChange(() => {
    $('.risk-indicator').removeClass('no-level low-level mid-level high-level')
        .addClass('no-answer');
    $('.answer-input').removeClass('selected');
    controller.sendResponses();
});
controller.setOnPhaseChange((phaseNumber) => {
    if (phaseNumber === CORRECT_ANSWERS_PHASE) {
        const answerMatrix = getAnswerMatrix(getAnswerStr());
        $('#score-text').text(getTotalScore(answerMatrix));
        $('.correct-answer-cover').each((i, el) => {
            const className = $(el).parents('.risk-img-cell')[0].classList[1];
            const sector = parseInt(className.substr(className.lastIndexOf('-') + 1));
            const row = Math.floor(sector / 4);
            const col = sector % 4;
            const score = getScoreForSector(row, col, answerMatrix[row][col]);
            $(el).find('.sector-score-text').text(score > 0 ? '+' + score : score);
        })
    }
});
controller.connect();
controller.changePhase(currentPhaseNumber, '', '');

/************************************
 *            ON PAGE LOAD          *
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

function getScoreForSector(row, column, answer) {
    const correctAnswers = [
        [-1,1,1,-1],
        [1,1,1,3],
        [2,0,-1,-1],
    ];

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
