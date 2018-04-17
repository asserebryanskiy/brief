let blockAnswerInput = false;
let timerInd = 0;

$('.how-it-scores-btn').click(() => $('.how-it-scores-popup').show());

$('.logout-text').click(() => $('.logout-popup').show());

$('.logout-popup-yes-btn').click(() => window.location = 'https://igrator.org');

$('.next-phase-btn').click((event) => {
    blockAnswerInput = false;
    const activePhaseNumber = $(event.currentTarget).parents('.phase-container').attr('id').substr(6);
    const next = (activePhaseNumber + 1) % 3;

    // 2 - is a phase of correct answers
    if (next === 2) {
        // stop timer
        window.clearInterval(timerInd);

        const answerStr = getAnswerStr();
        const answerMatrix = getAnswerMatrix(answerStr);
        // set score text
        const totalScore = getTotalScore(answerMatrix);
        $('#score-text').text(totalScore);
        // set up score on every correct-answer-cover
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
        }

        let congratulationText = '';
        if (totalScore < -1000) congratulationText = 'Потренирутейсь еще';
        else if (totalScore >= -1000 && totalScore < 0) congratulationText = 'Ниже, чем средненько, чувак';
        else if (totalScore >= 0 && totalScore < 1000) congratulationText = 'Выше, чем средненько, чувак';
        else congratulationText = 'Поздравляем вы вошли в 20% лучших игроков!';
        $('.congratulation-text').text(congratulationText);
    }

    $('.phase-container').hide();
    const $nextPhase = $('#phase-' + next);
    $nextPhase.show();
    const $timer = $nextPhase.children('.timer');
    if ($timer.length > 0) {
        // make timer black again
        $timer.removeClass('last-ten-seconds');
        const $alertDigit = $('.alert-digit');
        $alertDigit.hide();
        timerInd = window.setInterval(() => {
            // parse current values
            let min = parseInt($timer.text().substr(0, 2));
            let sec = parseInt($timer.text().substr(3));

            // on timer finish
            if (min === 0 && sec === 0) {
                window.clearInterval(timerInd);
                blockAnswerInput = true;
                $('.time-is-over-popup').show();
                $alertDigit.hide();
                return;
            }

            // decrease minutes
            if (sec === 0) {
                sec = 60;
                min--;
            }

            // decrease seconds
            sec--;

            // if only ten seconds left make timer red
            if (min === 0 && sec < 11) {
                $timer.addClass('last-ten-seconds');
            }

            // on last five seconds show big digits in the top of the screen
            if (min === 0 && sec < 6) {
                if (!$alertDigit.is(':visible')) $alertDigit.show();
                $alertDigit.text(sec);
            }

            // add trailing zero if needed
            min = min < 10 ? '0' + min : min;
            sec = sec < 10 ? '0' + sec : sec;

            // change timer text
            $timer.text(min + ':' + sec);
        }, 1000)
    }
});

$('.try-again-btn').click(() => location.reload());

$('.small-img-wrapper').click((event) => {
    const $target = $(event.currentTarget);
    const imgId = $target.find('img').attr('id').substr(0, 2);
    const $popup = $target.parent().find('.popup-wrapper');
    const $popupContent = $popup.find('.popup-content');
    if ($popupContent.children('img').length === 0) {
        console.log('entered');
        const $placeholder = $popupContent.children('.img-placeholder');
        $('#' + imgId + '-large-img').insertBefore($placeholder);
        $placeholder.remove();
        // $popupContent.children('.answer-inputs').insertAf.append($('#' + imgId + '-large-img'))
    }
    $popup.show();
});

$('.answer-input').click((event) => {
    if (!blockAnswerInput) {
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
        [2,1,1,-1],
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