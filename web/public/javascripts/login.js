$(document).ready(function () {
    $(".open-form-btn").click(function () {
        slideDown($(".form-signin"));
    });

    $(".close-signin-btn").click(function () {
        slideUp($(".form-signin"))
    });

    function slideDown(e) {
        $(".btn-panel").hide();
        $(".form-signin").slideDown('slow', function () {
        });
    }

    function slideUp(e) {
        $(".form-signin").slideUp('slow', function () {
            $(".btn-panel").show();
        });
    }
});
