
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
    $activeSessionBtn.css("background-color", "rgba(78,53,73,0.3)");
    $pastSessionBtn.css("background-color", "transparent");
    $("#past-sessions-table").hide();
    $("#active-sessions-table").show();
});
$pastSessionBtn.click(function () {
    $pastSessionBtn.css("background-color", "rgba(78,53,73,0.3)");
    $activeSessionBtn.css("background-color", "transparent");
    $("#active-sessions-table").hide();
    $("#past-sessions-table").show();
});

// create new session btn settings
$("#new-session-btn").click(function () {
    $(".new-session-popup").show();
});
$(".new-session-popup-background").click(function () {
    $(".new-session-popup").hide();
});

// enable close-popup-btn
$("#close-popup-button").click(function () {
    $(".new-session-popup").hide();
});