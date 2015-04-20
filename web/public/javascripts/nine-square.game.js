$(document).ready(function () {
    // reverts $.fn.button to jqueryui btn and assigns bootstrap button functionality to $.fn
    $.fn.btn = $.fn.button.noConflict()

    $(".main-menu").toggle();

    $("#set-level-dialog").dialog({
        modal: true,
        closeOnEscape: false,
        draggable: false,
        dialogClass: "set-level-dialog",
        close: function () {
            $(".game-menu").toggle();
        }
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
                        function () {
                            $(".exit-to-main-btn").click()
                        });
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

    $(document).keydown(function (e) {
        switch (e.which) {
            case 37: // left
                var newXYPos = nextAvailableCell(cellId, LEFT);
                $('#' + newXYPos.toNdx()).css('background-color', 'red');
                $('#' + cellId).css('background-color', '');
                cellId = newXYPos.toNdx();
                break;

            case 38: // up
                var newXYPos = nextAvailableCell(cellId, UP);
                $('#' + newXYPos.toNdx()).css('background-color', 'red');
                $('#' + cellId).css('background-color', '');
                cellId = newXYPos.toNdx();
                break;

            case 39: // right
                var newXYPos = nextAvailableCell(cellId, RIGHT);
                $('#' + newXYPos.toNdx()).css('background-color', 'red');
                $('#' + cellId).css('background-color', '');
                cellId = newXYPos.toNdx();
                break;

            case 40: // down
                var newXYPos = nextAvailableCell(cellId, DOWN);
                $('#' + newXYPos.toNdx()).css('background-color', 'red');
                $('#' + cellId).css('background-color', '');
                cellId = newXYPos.toNdx();
                break;

            case 49: // 1
                $('#' + cellId).html('1');
                break;

            case 50: // 2
                $('#' + cellId).html('2');
                break;

            case 51: // 3
                $('#' + cellId).html('3');
                break;

            case 52: // 4
                $('#' + cellId).html('4');
                break;

            case 53: // 5
                $('#' + cellId).html('5');
                break;

            case 54: // 6
                $('#' + cellId).html('6');
                break;

            case 55: // 7
                $('#' + cellId).html('7');
                break;

            case 56: // 8
                $('#' + cellId).html('8');
                break;

            case 57: // 9
                $('#' + cellId).html('9');
                break;

            case 67: // C
                $('#' + cellId).html('&nbsp;');
                break;

            default:
                console.log(e.which + " pressed.");
                return; // exit this handler for other keys
        }
        e.preventDefault(); // prevent the default action (scroll / move caret)
    });

    $(".btn-1").click(assignToCell("1"));
    $(".btn-2").click(assignToCell("2"));
    $(".btn-3").click(assignToCell("3"));
    $(".btn-4").click(assignToCell("4"));
    $(".btn-5").click(assignToCell("5"));
    $(".btn-6").click(assignToCell("6"));
    $(".btn-7").click(assignToCell("7"));
    $(".btn-8").click(assignToCell("8"));
    $(".btn-9").click(assignToCell("9"));
    $(".btn-clear").click(assignToCell("&nbsp;"));

    function  assignToCell(c) {
        return function() {
            $("#" + cellId).html(c);
        }
    }

    $(".exit-to-main-btn").click(function () {
        $(".nine-square-panel").effect("explode", {}, 500, function () {
            throbber.show();
            window.location = "/";
        });
    });
});

var UP = 0;
var RIGHT = 1;
var DOWN = 2;
var LEFT = 3;

var cellId = 41;

function PosXY(x, y) {
    this.x = x;
    this.y = y;

    this.incX = function () {
        if (x + 1 > 8) {
            return new PosXY(0, y);
        }
        else {
            return new PosXY(x + 1, y);
        }
    }

    this.decX = function () {
        if (x - 1 < 0) {
            return new PosXY(8, y);
        }
        return new PosXY(x - 1, y);
    }

    this.incY = function () {
        if (y + 1 > 8) {
            return new PosXY(x, 0);
        }
        else {
            return new PosXY(x, y + 1);
        }
    }

    this.decY = function () {
        if (y - 1 < 0) {
            return new PosXY(x, 8)
        }
        else {
            return new PosXY(x, y - 1);
        }
    }

    this.toNdx = function () {
        return this.x + this.y * 9;
    }
}

function nextAvailableCell(ndx, dir) {
    var pos = toXYPos(ndx);
    switch(dir) {
        case UP:
            var newPos = pos.decY();
            if ($("#" + newPos.toNdx()).attr("class").indexOf("seed") < 0) {
                return newPos;
            } else {
                return nextAvailableCell(newPos.toNdx(), dir);
            }
            break;
        case RIGHT:
            var newPos = pos.incX();
            if ($("#" + newPos.toNdx()).attr("class").indexOf("seed") < 0) {
                return newPos;
            } else {
                return nextAvailableCell(newPos.toNdx(), dir);
            }
            break;
        case DOWN:
            var newPos = pos.incY();
            if ($("#" + newPos.toNdx()).attr("class").indexOf("seed") < 0) {
                return newPos;
            } else {
                return nextAvailableCell(newPos.toNdx(), dir);
            }
            break;
        case LEFT:
            var newPos = pos.decX();
            if ($("#" + newPos.toNdx()).attr("class").indexOf("seed") < 0) {
                return newPos;
            } else {
                return nextAvailableCell(newPos.toNdx(), dir);
            }
            break;
    }
}

function toXYPos(ndx) {
    return new PosXY(ndx % 9, Math.floor(ndx / 9));
}

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
            close: function () {
                onClose();
            }
        });
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
        cellId = nextAvailableCell(cellId, LEFT).toNdx();
        $("#" + cellId).css('background-color', 'red');
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
                div.click(function() {
                    if ($(this).attr("class").indexOf('seed') < 0) {
                        $("#" + cellId).css("background-color", "");
                        cellId = $(this).attr("id");
                        $(this).css("background-color", "red");
                    }
                });
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
