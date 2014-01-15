$(document).ready(function () {
    // reverts $.fn.button to jqueryui btn and assigns bootstrap button functionality to $.fn
    $.fn.btn = $.fn.button.noConflict()

    $("#about-btn").click(function () {
        _dialog("About 9 Square", "<h5><strong>How to play...</strong></h5><p>" +
            "Fill in the grid so that every row, every column and every 3x3 box contains the digits 1 through 9.<br/><br/></p>" +
            "<h5><strong>Copyright 2013 (c) Lim, Teck Hooi</strong></h5>" +
            "<p>Licensed under the Apache License, Version 2.0 (the \"License\");" +
            "you may not use this file except in compliance with the License." +
            "You may obtain a copy of the License at<br/><br/>" +
            "    <a class=\"code\" target='_blank' href='http://www.apache.org/licenses/LICENSE-2.0'>http://www.apache.org/licenses/LICENSE-2.0</a><br/><br/>" +
            "Unless required by applicable law or agreed to in writing, software" +
            "distributed under the License is distributed on an \"AS IS\" BASIS," +
            "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied." +
            "See the License for the specific language governing permissions and" +
            "limitations under the License.</p>");
    });

    $(".open-form-btn").click(function () {
        $(".btn-panel").hide();
        $(".form-signin").slideDown('slow', function () {
        });
        $(".form-signin .usernameText").focus();
    });

    $(".form-signin").submit(function (e) {
        e.preventDefault();
        _dialog("Under development", "User sign in is not available yet.");
    });

    $("#register-btn").click(function (e) {
        $("#register-new-user-dialog").dialog("open");
    });

    $(".close-signin-btn").click(function () {
        $(".form-signin").slideUp('slow', function () {
            $(".btn-panel").show();
        });
    });

    $("#set-level-dialog").dialog({
        autoOpen: false,
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

    $("#register-new-user-dialog").dialog({
        autoOpen: false,
        modal: true,
        closeOnEscape: false,
        draggable: false,
        dialogClass: "register-new-user-dialog"
    });

    $("#register-new-user-dialog .cancel-btn").click(function() {
        $("#register-new-user-dialog").dialog("close");
    })

    $(".play-single-game-btn").click(function () {
        $(".welcome-panel").effect("puff", {}, 500, function () {
            switchToGameBoard();
        });
    });

    $(".form-play-single-game-btn").click(function () {
        $(".form-signin").slideUp('slow', function () {
            $(".btn-panel").show();
            $(".welcome-panel").effect("puff", {}, 500, function () {
                switchToGameBoard();
            });
        });
    });

    $(".easy-level-btn").click(function () {
        loadBoardFor("easy");
    });

    $(".hard-level-btn").click(function () {
        loadBoardFor("hard");
    });

    var $loading = $('#throbber').hide();
    $(document).ajaxStart(function () {
        $loading.show();
    }).ajaxStop(function () {
        $loading.hide();
    });

    $(".check-puzzle-btn").click(function () {
        $.ajax({
            url: "check",
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
            url: "submit",
            type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: JSON.stringify(buildListFrom($(".game-board"), false)),
            success: function (data, status) {
                if (data['result']) {
                    _dialog("Congratulations!!!", "You have solved this puzzle. Your achievements will be recorded if you have logged on.");
                    $(".exit-to-main-btn").click()
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
            url: "solve",
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
            $(".game-board").empty();
            $(".welcome-panel").show();
            $(".game-menu").toggle();
            $(".main-menu").toggle();
        });
    });
});

function _dialog(title, message) {
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
            }
        });
}

function switchToGameBoard() {
    $(".main-menu").toggle();
    $("#set-level-dialog").dialog("open");
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
