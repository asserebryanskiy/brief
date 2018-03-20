function drawChart(statsList) {
    const commandNameStr = $('.command-name').text();
    const commandName = commandNameStr.substr(commandNameStr.indexOf(' ') + 1);
    const $cols = $('.bar-chart-row');
    let statistics = statsList != null ? statsList.statistics : null;

    // draw chart
    $cols.each((i, col) => {
        if (statistics != null) $(col).find('.row-total-score').text(statistics[i].totalScore);
        $(col).find('.stats-cell').each((j, cell) => {
            const $cell = $(cell);
            const $cellValue = $(cell).find('.cell-value');
            if (statsList != null) {
                $cellValue.text(statistics[i].roundScoreMap[j]);
            }
            const points = parseInt($cellValue.text());
            let width = points * 5;
            /*switch (points) {
                case 15:
                    width = 75;
                    break;
                case 10:
                    width = 50;
                    break;
                case 5:
                    width = 25;
                    break;
                case 2:
                    width = 10;
                    break;
                default:
                    width = 0;
            }*/
            if (width === 0) $cellValue.hide();
            else $cellValue.show();
            if ($(col).find('.chart-command-name').text() !== commandName) {
                $(col).css('opacity', '0.3')
            }
            $cell.css('width', width + 'px');
        })
    });
    $cols.slideDown();
}

const $chart = $('.bar-chart');
$chart.scroll(() => {
    const $icon = $chart.find('.scroll-icon');
    if ($chart.scrollLeft() < 10) $icon.show();
    else                          $icon.hide();
});

$chart.ready(() => {
    console.log($chart.prop('scrollWidth'));
    console.log($chart.width());
    if ($chart.prop('scrollWidth') > $chart.width()) {
        $chart.find('.scroll-icon').show();
    }
});
