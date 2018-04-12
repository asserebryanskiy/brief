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

function onRoundChange() {
    throw new Error("You must override this function!");
}