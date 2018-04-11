const FORM_GAME_PHASE = 0;

let currentPhaseNumber = getActivePhaseNumber();
let gameId = $('#game-id').text();

switch (currentPhaseNumber) {
    case FORM_GAME_PHASE: {
        showGameSettings(true);
    }
}
controller.setOnNextPhase((phaseOrder) => {
     switch (phaseOrder) {
         case FORM_GAME_PHASE + 1: {
            stompClient.send("/app/rolePlay/" + gameId + "/rolePlaySettings", {},
                JSON.stringify({
                    'strategy' : $('.role-play-strategy-option:selected').attr('value')
                }));
             showGameSettings(false);
         }
     }
});
controller.setOnPrevPhase((phaseOrder) => {
    switch (phaseOrder) {
        case FORM_GAME_PHASE: showGameSettings(true);
    }
});
controller.connect();

function showGameSettings(value) {
    if (value) {
        $('.players-wrapper').hide();
        $('.form-game-wrapper').show();
    } else {
        $('.players-wrapper').show();
        $('.form-game-wrapper').hide();
    }
}

