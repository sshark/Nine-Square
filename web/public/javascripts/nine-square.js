$(document).ready(function () {
    $(".open-form-btn").click(function () {
        slideDown($(".form-signin"));
        $(".form-signin .usernameText").focus();
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

    $("#set-level-dialog").dialog({
        autoOpen: false,
        modal: true,
        closeOnEscape: false,
        draggable: false,
        dialogClass: "set-level-dialog"
    });

    $(".play-single-game-btn").click(function () {
        $(".welcome-panel").effect("puff", {}, 500, function () {
            $("#set-level-dialog").dialog("open");
        });
    });

    $(".easy-level-btn").click(function () {
        loadBoardFor("easy");
    });

    $(".hard-level-btn").click(function () {
        loadBoardFor("hard");
    });

    $(".main-screen-btn").click(function () {
        $(".nine-square-panel").effect("explode", {}, 500, function () {
            $(".welcome-panel").show();
            $(".game-board").empty();
        });
    });
});

function loadBoardFor(level) {
    $("#set-level-dialog").dialog("close");
    addNewCellsTo($(".game-board"));
    $(".nine-square-panel").effect("fade", {}, 1000, function () {});
}

function addNewCellsTo(board) {
    $.ajax({
        url: "/new-easy",
        context: board
    }).done(function (puzzle) {
        var self = $(this);
        for (var i = 0; i < puzzle.length; i++) {
            var div = $("<div class='cell'>");

            if (bigCellIndexAt(i) % 2 == 0) {
                div.addClass("even");
            } else {
                div.addClass("odd");
            }

            if (puzzle[i] != 0) {
                div.addClass("seed");
            }

            div.html(spaceIfZero(puzzle[i]));
            self.append(div);
        }
    });
}

function spaceIfZero(num) {
    return num == 0 ? "&nbsp;" : num;
}

function doubleDigit(i) {
    if (i < 10) {
        return "0" + i;
    } else {
        return "" + i;
    }
}

function bigCellIndexAt(pos) {
    return Math.floor(pos / 27) * 3 + Math.floor(pos / 3) % 3
}
