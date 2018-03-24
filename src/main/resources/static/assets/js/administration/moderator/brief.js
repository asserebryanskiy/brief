// phase number constants
const SEND_ANSWER_PHASE = 3;
const SEND_STATISTICS_PHASE = 5;

controller.setOnNextPhase((phaseOrder) => {
    // if newActive order is greater than 2 show player-table
    if (phaseOrder >= SEND_ANSWER_PHASE && !$('.players-table').is(':visible')) togglePlayersView();

    // if new phase is SEND_STATISTICS send statistics to players and projector
    if (phaseOrder === SEND_STATISTICS_PHASE) {
        stompClient.send('/app/' + gameSessionId + '/sendStatistics')
    }
});
controller.setOnPrevPhase((phaseOrder) => {
    // if newActive order is greater than 2 show player-table
    if (phaseOrder < SEND_ANSWER_PHASE && $('.players-table').is(':visible')) togglePlayersView();

    // if new phase is SEND_STATISTICS send statistics to players and projector
    if (phaseOrder === SEND_STATISTICS_PHASE) {
        stompClient.send('/app/' + gameSessionId + '/sendStatistics')
    }
});
controller.setOnNextRound(() => {
    // hide player-table
    if ($('.players-table').is(':visible')) {
        togglePlayersView();
    }

    // set current-score-td to zero
    $('.current-score-td').text(0);

    // erase all sent answers
    $('.answer-td').text('');

    // erase calculated places
    $('.place-td').text('');

    // remove received-answers class from all commands' tr
    $('.players-table tbody tr').removeClass('received-answers');

    // remove all phases' classes except phase
    $('.phase').attr('class', 'phase animated');

    // set first phase active and second next
    $('#phase-0').addClass('active');
    $('#phase-1').addClass('next');
});
controller.setLastPhase($('#phase-5'));

function onWsConnect(stompClient) {
    stompClient.subscribe('/queue/' + gameSessionId + '/answer', (message) => {
        const body = JSON.parse(message.body);
        const username = body.username;
        const answerStr = body.answerStr;
        const newScore = parseInt(body.score);
        const $playerRow = $('#player-row-' + username);
        const $currentScoreTd = $playerRow.children('.current-score-td');
        const $totalTd = $playerRow.children('.total-score-td');
        const currentScore = parseInt($currentScoreTd.text());

        // display received answer to user
        $playerRow.children('.answer-td').text(answerStr === '' ? '-' : answerStr);

        // change current score
        $currentScoreTd.text(newScore);

        // change accumulated score
        $totalTd.text(parseInt($totalTd.text()) - currentScore + newScore);

        // change style
        $playerRow.addClass('received-answers');

        // update table
        const $table = $('.players-table');
        $table.trigger('updateCell', $currentScoreTd[0]);
        $table.trigger('updateCell', $totalTd[0]);
        sortingByTotalResult = false;
        $table.trigger('sorton', [[[3,1]]]);
    });
}

controller.connect(onWsConnect);

// if current phase order is greater than 2, show players-table
if (getPhaseOrder($('.phase.active').attr('id')) > 2) togglePlayersView();

function togglePlayersView() {
    $('.players').toggle();
    $('.players-table').toggle();
}

/********************************
 *         TABLE SORTING        *
 ********************************/

let sortingByTotalResult = true;
$('th').click((event) => {
    const heading = event.currentTarget;
    sortingByTotalResult = $(heading).text() === 'Накопленный результат';
});

$(document).ready(() => {
    const $table = $('.players-table');
    $table.tablesorter({cssHeader:'', sortList: [[4,1]], headers: {0 : {sorter:'integer'}, 3 : {sorter:'integer'}, 4: {sorter:'integer'}}});
    $table.bind('sortEnd', () => {
        if (sortingByTotalResult) {
            computePlaces();
        }
        console.log('sorted places');
    });
    computePlaces();
});

/**
 * Helper function to calculate current places of connected commands.
 * Computes places according to total-score column. 
* */
function computePlaces() {
    const $placeCells = $('.place-td');
    const $connectedCells = $('.connection-td');
    const $answerCells = $('.answer-td');
    const $totalScoreCells = $('.total-score-td');
    const numberOfTeams = $placeCells.length;

    // if order of sort is reverse apply places in reverse order
    if (parseInt($($totalScoreCells[0]).text())
        < parseInt($($totalScoreCells[numberOfTeams - 1]).text())) {
        let numberOfPlaces = 0;
        // count number of places
        for (let i = 0; i < numberOfTeams; i++) {
            if ($($connectedCells[i]).text() === 'Connected' &&
                $($answerCells[i]).text() !== '') {
                numberOfPlaces++;
            }
        }
        // apply places in reverse order
        let place = numberOfPlaces;
        for (let i = 0; i < numberOfTeams; i++) {
            if ($($connectedCells[i]).text() === 'Connected' &&
                $($answerCells[i]).text() !== '') {
                $($placeCells[i]).text(place--);
            }
        }
    } else {
        let place = 1;
        for (let i = 0; i < numberOfTeams; i++) {
            if ($($connectedCells[i]).text() === 'Connected' &&
                $($answerCells[i]).text() !== '') {
                $($placeCells[i]).text(place++);
            }
        }
    }
}
