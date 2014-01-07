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

    $(".play-single-game-btn").click(function() {
        createBoard($(".board"));
    });
});

function createBoard(board) {
    board.css("display", "inline-block");
    for (i = 0; i < 81; i++) {
        var div = $("<div class='cell'>");

        if (bigCellIndexAt(i) % 2 == 0) {
            div.addClass("even");
        } else {
            div.addClass("odd");
        }

        div.html(doubleDigit(i));
        board.append(div);
    }

    function doubleDigit(i) {
        if (i < 10) {
            return "0" + i;
        } else {
            return "" + i;
        }
    }

    function bigCellIndexAt(pos) {return Math.floor(pos / 27) * 3 + Math.floor(pos / 3) % 3}
}
