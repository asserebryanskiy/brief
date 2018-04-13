function getAnswerStr() {
    let answerStr = '';
    $('.answer-input-slider').each((i, el) => {
        answerStr += '' + i + '-' + $(el).val();
        if (i !== 11) answerStr += ',';
    });

    return answerStr;
}

$('.answer-input-slider').on('input', null, null, (event) => {
    const $slider = $(event.currentTarget);
    const val = parseInt($slider.val());

    // change svg in popup
    $slider.siblings('svg').remove();
    const $newSvg = $($('svg.level-' + val)[0]).clone();
    $newSvg.insertAfter($slider);

    // change svg in risk-indicator
    const $oldSvg = $slider.parents('.risk-img-cell').find('.risk-indicator svg');
    $newSvg.clone().insertAfter($oldSvg);
    $oldSvg.remove();
});