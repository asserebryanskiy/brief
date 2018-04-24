
let mobileSearchIsShowing = false;
$("#mobile-search-icon").click(function() {
    $("h1").hide();
    $(".user-pane").hide();
    $("#mobile-search-icon").hide();
    const $search = $(".search-field");
    $search.show();
    $search.css("width", "100%");
    $search.css("display", "flex");
    $search.css("align-content", "center");
    $("#mobile-close-search-icon").show();
});

$("#mobile-close-search-icon").click(function() {
    $("h1").show();
    $(".user-pane").show();
    $("#mobile-search-icon").show();
    $(".search-field").hide();
    $("#mobile-close-search-icon").hide();
});

// make tab-btn toggle
const $activeSessionBtn = $("#active-sessions-tab-btn");
const $pastSessionBtn = $("#past-sessions-tab-btn");
$activeSessionBtn.click(function () {
    $activeSessionBtn.addClass('chosen');
    $pastSessionBtn.removeClass('chosen');
    $("#past-sessions-table").hide();
    $("#active-sessions-table").show();
});
$pastSessionBtn.click(function () {
    $activeSessionBtn.removeClass('chosen');
    $pastSessionBtn.addClass('chosen');
    $("#active-sessions-table").hide();
    $("#past-sessions-table").show();
});

// create new session btn settings
$("#new-session-btn").click((event) => {
    $(event.currentTarget).siblings('.popup-wrapper').show();
});

$('.edit-session-btn').click((event) => {
    const $menu = $(event.currentTarget).siblings('.edit-menu-wrapper').children('.edit-menu');
    $menu.slideToggle();
});

$('.edit-menu-item').click((event) => {
    const $target = $(event.currentTarget);
    $target.parents('.column3').children('.edit-popup').show();
    $target.parents('.edit-menu').hide();
});

$('.edit-menu').mouseleave((event) => {
    const $el = $(event.currentTarget);
    window.setTimeout(() => {
        if (!$el.is(':hover')) $el.slideUp();
    }, 1000)
});

$('tr').mouseleave((event) => {
    const $el = $(event.currentTarget);
    window.setTimeout(() => {
        if (!$el.is(':hover')) $el.find('.edit-menu').slideUp();
    }, 1000)
});

$('.delete-menu-item').click((event) => {
    const $target = $(event.currentTarget);
    $target.parents('.column3').children('.delete-popup').show();
    $target.parents('.edit-menu').hide();
});

$('.delete-past-session-icon').click((event) => {
    $('.preloader').show();
    $(event.currentTarget).siblings('form')[0].submit();
});

$('.flash').delay(7000).slideUp().delay(400);

$('.popup-submit-btn').click(() => {
    $('.popup-submit-btn').attr('disable', true);
    $('.preloader').show();
});

const $gameTypeSelect = $('.game-type-select');
$gameTypeSelect.change(() => {
    const $riskMapTypeSelect = $('.risk-map-type-select');
    const $riskMapTypeLabel = $('.risk-map-type-label');
    const gameType = $gameTypeSelect.find('option:selected').text();
    if (gameType === 'Карта рисков') {
        $riskMapTypeLabel.slideDown();
        $riskMapTypeSelect.slideDown();
    } else {
        $riskMapTypeLabel.slideUp();
        $riskMapTypeSelect.slideUp();
    }
});

document.onload = $('.preloader').hide();