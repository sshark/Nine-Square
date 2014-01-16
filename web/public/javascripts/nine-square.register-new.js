$(document).ready(function () {
    var throbber = $('#throbber');
    $(document).ajaxStart(function () {
        throbber.show();
    }).ajaxStop(function () {
            throbber.hide();
    });

    $(".game-menu").toggle();
    $(".main-menu").toggle();

    $(".exit-to-main-btn").click(function () {
        $(".container").slideUp('slow', function () {
            throbber.show();
            window.location = "/";
        });
    })

    $(".edit-user").slideDown("slow", function () {});
});