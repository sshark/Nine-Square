$(document).ready(function () {
    // reverts $.fn.button to jqueryui btn and assigns bootstrap button functionality to $.fn
    $.fn.btn = $.fn.button.noConflict()

    var throbber = $('#throbber');
    $(document).ajaxStart(function () {
        throbber.show();
    }).ajaxStop(function () {
        throbber.hide();
    });

    if ($("span.error").text().length == 0) {
        $('.form-signin').hide();
    } else {
        $(".open-form-btn").click();
    }

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
        $(".form-signin").slideDown('slow', function () {});
        $(".form-signin .usernameText").focus();
    });

    $("#new-user-btn").click(function (e) {
        $(".container").slideUp('slow', function() {
            $('#throbber').show();
            window.location='new-user';
        });
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

    $(".play-single-game-btn").click(function () {
        $(".welcome-panel").effect("puff", {}, 500, function () {
            loadGame();
        });
    });

    $(".form-play-single-game-btn").click(function () {
        $(".form-signin").slideUp('slow', function () {
            $(".welcome-panel").effect("puff", {}, 500, function () {
                loadGame();
            });
        });
    });

    // Open log in fields if there was a failed to log in
    if ($("span.error").text().length == 0) {
        $('.form-signin').hide();
    } else {
        $(".open-form-btn").click();
    }
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
    $('.alert').remove();       // clear login error messages, otherwise
    $('.help-inline').text('')  // log in fields with error messages will be displayed
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

function loadGame() {
    $('#throbber').show();
    window.location = "game"
}

function spaceIfZero(num) {
    return num == 0 ? "&nbsp;" : num;
}

function bigCellIndexAt(pos) {
    return Math.floor(pos / 27) * 3 + Math.floor(pos / 3) % 3
}
