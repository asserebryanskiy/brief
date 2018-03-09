function drawChart(statsList) {
    const commandNameStr = $('.command-name').text();
    const commandName = commandNameStr.substr(commandNameStr.indexOf(' ') + 1);
    console.log(commandName);
    const $cols = $('.bar-chart-col');
    let statistics = statsList != null ? statsList.statistics : null;
    $cols.each((i, col) => {
        if (statistics != null) $(col).find('.col-total-score').text(statistics[i].totalScore);
        $(col).find('.stats-cell').each((j, cell) => {
            const $cell = $(cell);
            const $cellValue = $(cell).find('.cell-value');
            if (statsList != null) {
                $cellValue.text(statistics[i].roundScoreMap[j]);
            }
            // statsList.statistics[i].roundScoreMap[j]
            let height = parseInt($cellValue.text()) * 20;
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
