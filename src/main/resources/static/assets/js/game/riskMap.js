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
    console.log($indicator);
    $indicator.removeClass('low-level mid-level high-level');

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
    controller.sendResponses();
});
controller.setOnPhaseChange((phaseNumber) => {
    if (phaseNumber === CORRECT_ANSWERS_PHASE) {
        $('#score-text').text(getScore());
    }
});
controller.connect();
controller.changePhase(currentPhaseNumber, '', '');

/************************************
 *            ON PAGE LOAD          *
 ************************************/

function getScore() {
    const answer = getAnswerStr();
    const answerMatrix = getAnswerMatrix(answer);
    const correctAnswers = [
        [-1,1,1,-1],
        [1,1,1,3],
        [2,0,-1,-1],
    ];
    let score = 0;
    for (let i = 0; i < 3; i++) {
        for (let j = 0; j < 4; j++) {
            switch (correctAnswers[i][j]) {
                case -1: continue;
                case 0:
                    switch (answerMatrix[i][j]) {
                        case -1:
                            score += -100;
                            continue;
                        case 0:
                            score += 100;
                            continue;
                        case 1:
                            score += 50;
                            continue;
                        case 2:
                            score += 25;
                            continue;
                        case 3:
                            score += 0;
                            continue;
                    }
                    break;
                case 1:
                    switch (answerMatrix[i][j]) {
                        case -1:
                            score += -200;
                            continue;
                        case 0:
                            score += 50;
                            continue;
                        case 1:
                            score += 200;
                            continue;
                        case 2:
                            score += 100;
                            continue;
                        case 3:
                            score += 50;
                            continue;
                    }
                    break;
                case 2:
                    switch (answerMatrix[i][j]) {
                        case -1:
                            score += -300;
                            continue;
                        case 0:
                            score += 25;
                            continue;
                        case 1:
                            score += 100;
                            continue;
                        case 2:
                            score += 300;
                            continue;
                        case 3:
                            score += 150;
                            continue;
                    }
                    break;
                case 3:
                    switch (answerMatrix[i][j]) {
                        case -1:
                            score += -400;
                            continue;
                        case 0:
                            score += 0;
                            continue;
                        case 1:
                            score += 50;
                            continue;
                        case 2:
                            score += 150;
                            continue;
                        case 3:
                            score += 400;
                            continue;
                    }
                    break;
            }
        }
    }

    return score;
}

function getScoreForSector(sector, answer) {
    const correctAnswers = [
        [-1,1,1,-1],
        [1,1,1,3],
        [2,0,-1,-1],
    ];

    // scoring varies depending on correct answer
    switch (correctAnswers[sector / 4][sector % 4]) {
        case -1:
            return 0;
        case 0:

        case 1:
            switch (answer) {
                case -1: return -200;
                case 0: return 50;
                case 1: return 200;
            }
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
