function drawChart(statsList) {
    const commandNameStr = $('.command-name').text();
    const commandName = commandNameStr.substr(commandNameStr.indexOf(' ') + 1);
    const $cols = $('.bar-chart-col');
    let statistics = statsList != null ? statsList.statistics : null;

    // draw chart
    $cols.each((i, col) => {
        if (statistics != null) $(col).find('.col-total-score').text(statistics[i].totalScore);
        $(col).find('.stats-cell').each((j, cell) => {
            const $cell = $(cell);
            const $cellValue = $(cell).find('.cell-value');
            if (statsList != null) {
                $cellValue.text(statistics[i].roundScoreMap[j]);
            }
            const points = parseInt($cellValue.text());
            let height;
            switch (points) {
                case 15:
                    height = 80;
                    break;
                case 10:
                    height = 60;
                    break;
                case 5:
                    height = 40;
                    break;
                case 2:
                    height = 20;
                    break;
                default:
                    height = 0;
            }
            if (height === 0) $cellValue.hide();
            else $cellValue.show();
            if ($(col).find('.chart-command-name').text() !== commandName) {
                $(col).css('opacity', '0.5')
            }
            $cell.css('height', height + 'px');
        })
    });
    $cols.slideDown();
}

const $chart = $('.bar-chart');
$chart.scroll(() => {
    const $icon = $chart.find('.scroll-icon');
    if ($chart.scrollLeft() < 10) $icon.fadeIn(400);
    else                          $icon.fadeOut(400);
});

$chart.ready(() => {
    console.log($chart.prop('scrollWidth'));
    console.log($chart.width());
    if ($chart.prop('scrollWidth') > $chart.width()) {
        $chart.find('.scroll-icon').fadeIn();
    }
});
