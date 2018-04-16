const SEND_ANSWER_PHASE = 1;
const CORRECT_ANSWERS_PHASE = 2;

/************************************
 *        ON CLICK FUNCTIONS        *
 ************************************/

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

$('.how-it-scores-btn').click(() => $('.how-it-scores-popup').show());

/************************************
 *        CONTROLLER SETTINGS       *
 ************************************/

controller.setOnRoundChange(() => {
    onRoundChange();
    controller.sendResponses();
});

controller.setOnPhaseChange((phaseNumber) => {
    switch (phaseNumber) {
        case SEND_ANSWER_PHASE:
            controller.enableAnswerSend(true);
            break;
        case CORRECT_ANSWERS_PHASE: {
            onCorrectAnswerPhase();
            break;
        }
    }
});
$(window).ready(() => {
    controller.connect();
    controller.changePhase(currentPhaseNumber, '', '');
});

/************************************
 *         ABSTRACT METHODS         *
 ************************************/
// are overridden by concrete implementation of riskMap: riskMap-hotel/riskMap-office

function onCorrectAnswerPhase() {
    throw new Error("You must override this function!");
}

/*function getScoreForSector(row, column, answer) {
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
}*/

function onRoundChange() {
    throw new Error("You must override this function!");
}