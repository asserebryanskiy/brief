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

// on click on small img in grid load large image to popup and then show popup
$('.small-img-wrapper').click((event) => {
    const $target = $(event.currentTarget);
    const imgId = $target.find('img').attr('id').substr(0, 2);
    const $popup = $target.parent().find('.popup-wrapper');
    const $popupContent = $popup.find('.popup-content');
    if ($popupContent.children('img').length === 0) {
        const $placeholder = $popupContent.children('.img-placeholder');
        $('#' + imgId + '-large-img').insertBefore($placeholder);
        $placeholder.remove();
        // $popupContent.children('.answer-inputs').insertAf.append($('#' + imgId + '-large-img'))
    }
    $popup.show();
});

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

$('#show-correct-answers-btn').click(() => {
    $('.correct-answer-cover').slideToggle();
});

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