$(document).ready(function () {
    // reverts $.fn.button to jqueryui btn and assigns bootstrap button functionality to $.fn
    $.fn.btn = $.fn.button.noConflict()

    $(".main-menu").toggle();

    $("#set-level-dialog").dialog({
        modal: true,
        closeOnEscape: false,
        draggable: false,
        dialogClass: "set-level-dialog",
        close: function() {$(".game-menu").toggle();}
    });

    var numpadDialog = $("#numpad-dialog").dialog({
        autoOpen: false,
        modal: true,
        dialogClass: "numpad-dialog",
        width: 154
    });

    $(".easy-level-btn").click(function () {
        loadBoardFor("easy");
    });

    $(".hard-level-btn").click(function () {
        loadBoardFor("hard");
    });

    var throbber = $('#throbber');
    $(document).ajaxStart(function () {
        throbber.show();
    }).ajaxStop(function () {
        throbber.hide();
    });

    $(".check-puzzle-btn").click(function () {
        $.ajax({
            url: "/api/check-puzzle",
            type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: JSON.stringify(buildListFrom($(".game-board"), false)),
            success: function (data, status) {
                if (data['result']) {
                    _dialog("No conflict found.", "The numbers are in their correct positions.");
                } else {
                    _dialog("Conflicts!!!", "There are conflicting numbers in the puzzle.");
                }
            },
            error: function (xhr, status, error) {
                _dialog(status, "Failed to check the puzzle because of " + error);
            }
        });
    });

    $(".submit-puzzle-btn").click(function () {
        var puzzle = buildListFrom($(".game-board"));
        if (!_.every(puzzle, function (num) {
            return num != 0
        })) {
            _dialog("Incomplete puzzle", "This puzzle is incomplete and will not be submitted.");
            return;
        }
        ;
        $.ajax({
            url: "/api/submit-puzzle",
            type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: JSON.stringify(buildListFrom($(".game-board"), false)),
            success: function (data, status) {
                if (data['result']) {
                    _dialog("Congratulations!!!",
                        "You have solved this puzzle. Your achievements will be recorded if you have logged on.",
                        function() {$(".exit-to-main-btn").click()});
                } else {
                    _dialog("Rejected", "Your solution is rejected. The numbers in the puzzle conflict with the rule.");
                }
            },
            error: function (xhr, status, error) {
                _dialog(status, "Failed to submit the puzzle because of " + error);
            }
        });
    });

    $(".solve-puzzle-btn").click(function () {
        $.ajax({
            url: "/api/solve-puzzle",
            type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: JSON.stringify(buildListFrom($(".game-board"), true)),
            success: function (data, status) {
                $(".game-board").children().each(function (ndx) {
                    $(this).text(data[ndx]);
                });
            },
            error: function (jqXHR, status, error) {
                _dialog(status, "Failed to load puzzle. An error has occurred in the server, " + error);
            }
        });
    });

    $(".clear-puzzle-btn").click(function () {
        $(".game-board").children().each(function () {
            var self = $(this);
            if (self.hasClass("empty")) {
                self.html("&nbsp;");
            }
            ;
        });
    });

    $(".new-puzzle-btn").click(function () {
        var board = $(".game-board");
        board.empty();
        addNewCellsTo(board);
    });

    $(".one-btn").click(numpadNumericFunGen(numpadDialog, "1"));
    $(".two-btn").click(numpadNumericFunGen(numpadDialog, "2"));
    $(".three-btn").click(numpadNumericFunGen(numpadDialog, "3"));
    $(".four-btn").click(numpadNumericFunGen(numpadDialog, "4"));
    $(".five-btn").click(numpadNumericFunGen(numpadDialog, "5"));
    $(".six-btn").click(numpadNumericFunGen(numpadDialog, "6"));
    $(".seven-btn").click(numpadNumericFunGen(numpadDialog, "7"));
    $(".eight-btn").click(numpadNumericFunGen(numpadDialog, "8"));
    $(".nine-btn").click(numpadNumericFunGen(numpadDialog, "9"));
    $(".clear-btn").click(numpadNumericFunGen(numpadDialog, " "));
    $(".close-btn").click(function () {
        numpadDialog.dialog("close")
    });

    $(".exit-to-main-btn").click(function () {
        $(".nine-square-panel").effect("explode", {}, 500, function () {
            throbber.show();
            window.location = "/";
        });
    });
});

function _dialog(title, message, onClose) {
    $("<div id=\"dialog-message\"></div>")
        .attr("title", title)
        .html(message)
        .dialog({
            modal: true,
            minWidth: 400,
            buttons: {
                Ok: function () {
                    $(this).dialog("close");
                }
            },
            close: function() {onClose();}
        });
}

function numpadNumericFunGen(numpadDialog, c) {
    return function () {
        var cell = $(".game-board").children("#" + numpadDialog.data("id"));
        " " == c ? cell.html("&nbsp;") : cell.text(c);
        numpadDialog.dialog("close");
    }
}

function buildListFrom(board, buildEmptyPuzzle) {
    return board.children().map(function () {
        return (!buildEmptyPuzzle || $(this).hasClass("seed")) ? guardedParseInt($(this).text()) : 0
    }).toArray();
}

function guardedParseInt(i) {
    return '\xA0' == i ? 0 : parseInt(i);
}

function loadBoardFor(level) {
    $("#set-level-dialog").dialog("close");
    addNewCellsTo($(".game-board"));
    $(".nine-square-panel").effect("fade", {}, 1000, function () {
    });
}

function addNewCellsTo(board) {
    $.ajax({
        url: "/api/new-easy-puzzle",
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
                    div.click(function () {
                        $("#numpad-dialog").data("id", $(this).attr("id")).dialog("open");
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
