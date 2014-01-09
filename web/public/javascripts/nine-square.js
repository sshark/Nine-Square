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

    $("#numpad-dialog").dialog({
        autoOpen: false,
        modal: true,
        dialogClass: "numpad-dialog",
        width : 154
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

    $(".check-puzzle-btn").click(function() {
        $.ajax({
            url: "check",
            type: "POST",
            contentType:"application/json; charset=utf-8",
            dataType: "json",
            data: JSON.stringify(buildListFrom($(".game-board"), false)),
            success: function(data, status) {
                if (data['result']) {
                    alert("No conflict found.");
                } else {
                    alert("Conflicts!!!");
                }
            }
        });
    });

    $(".solve-puzzle-btn").click(function() {
        $.ajax({
            url: "solve",
            type: "POST",
            contentType:"application/json; charset=utf-8",
            dataType: "json",
            data: JSON.stringify(buildListFrom($(".game-board"), true)),
            success: function(data, status) {
                $(".game-board").children().each(function(ndx) {
                   $(this).text(data[ndx]);
                });
            }
        });
    });

    $(".clear-puzzle-btn").click(function() {
        $(".game-board").children().each(function() {
            var self = $(this);
            if (self.hasClass("empty")) {
                self.html("&nbsp;");
            };
        });
    });

    $(".new-puzzle-btn").click(function() {
        var board = $(".game-board");
        board.empty();
        addNewCellsTo(board);
    });

    $(".exit-to-main-btn").click(function () {
        $(".nine-square-panel").effect("explode", {}, 500, function () {
            $(".welcome-panel").show();
            $(".game-board").empty();
        });
    });
});

function buildListFrom(board, buildEmptyPuzzle) {
    return board.children().map(function() {
        return (!buildEmptyPuzzle || $(this).hasClass("seed")) ? guardedParseInt($(this).text()) : 0}).toArray();
}

function guardedParseInt(i) {
    return '\xA0' == i ? 0 : parseInt(i);
}

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

            div.attr("id", i);

            if (puzzle[i] != 0) {
                div.addClass("seed");
            } else {
                div.addClass("empty");
                div.click(function() {
                    $("#numpad-dialog").dialog("open");
                })
            }

            div.addClass(bigCellIndexAt(i) % 2 == 0 ? "even" : "odd");

            div.html(spaceIfZero(puzzle[i]));
            self.append(div);
        }
    });
}

function spaceIfZero(num) {
    return num == 0 ? "&nbsp;" : num;
}

function bigCellIndexAt(pos) {
    return Math.floor(pos / 27) * 3 + Math.floor(pos / 3) % 3
}
