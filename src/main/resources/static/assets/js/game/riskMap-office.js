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

$('.answer-input').click((event) => {
    if (answerSendingEnabled) {
        toggleSelected(event);
    }
});

/************************************
 *       OVERRIDDEN FUNCTIONS       *
 ************************************/

function onCorrectAnswerPhase() {
    const answerStr = getAnswerStr();
    const answerMatrix = getAnswerMatrix(answerStr);
    const totalScore = getTotalScore(answerMatrix);
    $('#score-text').text(totalScore);
    $('.correct-answer-cover').each((i, el) => {
        const className = $(el).parents('.risk-img-cell')[0].classList[1];
        const sector = parseInt(className.substr(className.lastIndexOf('-') + 1));
        const row = Math.floor(sector / 4);
        const col = sector % 4;
        const score = getScoreForSector(row, col, answerMatrix[row][col]);
        $(el).find('.sector-score-text').text(score > 0 ? '+' + score : score);
    });
    if (answerStr.length > 0) {
        let acc = '';
        let sector = 0;
        for (let i = 0; i < answerStr.length; i++) {
            const letter = answerStr.charAt(i);
            if (letter === '-') {
                sector = parseInt(acc);
                acc = '';
            } else if (letter === ',') {
                const $circles = $('.risk-img-cell-' + sector).find('.possible-results').find('.correct-answer-circle');
                $circles.removeClass('selected');
                $($circles[parseInt(acc) + 1]).addClass('selected');
                acc = '';
                sector = 0;
            } else {
                acc += letter;
            }
        }
        const $circles = $('.risk-img-cell-' + sector).find('.possible-results').find('.correct-answer-circle');
        $circles.removeClass('selected');
        $($circles[parseInt(acc) + 1]).addClass('selected');

        let congratulationText = '';
        if (totalScore < 0) congratulationText = 'Сотрудники все еще в большой опасности! Может, попробуете еще раз?';
        else if (totalScore >= 0 && totalScore < 1000) congratulationText = 'Вы вышли в "плюс", однако не достигли верхних позиций рейтинга. Предлагаем попробовать еще раз!';
        else if (totalScore >= 1000 && totalScore < 1500) congratulationText = 'Хорошая работа! Ваш результат находится в числе 35% лучших в рейтинге.';
        else if (totalScore >= 1500 && totalScore < 1900) congratulationText = 'Поздравляем! Вы вошли в 20% лучших в рейтинге. Вы значительно повысили безопасность в офисе, однако есть еще над чем работать.';
        else congratulationText = 'Великолепно! Вы вошли в 5% лучших в рейтинге. Благодаря вам, офис безопасен и сотрудники могут комфортно работать.';
        $('.congratulation-text').text(congratulationText);
    }
}

function onRoundChange() {
    $('.risk-indicator').removeClass('no-level low-level mid-level high-level')
        .addClass('no-answer');
    $('.answer-input').removeClass('selected');
}

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

/************************************
 *         HELPER FUNCTIONS         *
 ************************************/

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
                : $target.hasClass('no-answer') ? 'no-answer'
                : $target.hasClass('no-level') ? 'no-level'
                : $target.hasClass('mid-level') ? 'mid-level' : 'high-level';
        $indicator.addClass(className);
    }
}

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
        [3,1,1,1],
        [2,1,1,-1]
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