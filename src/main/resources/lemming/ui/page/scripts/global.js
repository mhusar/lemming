jQuery.ajaxSetup({
    cache : false
});

jQuery(document).ready(function () {
    changeFormTabOrder();
    setupFeedbackPanel();
});

jQuery.fn.isInViewport = function (offset) {
    var elementTop, elementBottom, viewportTop, viewportBottom;

    if (typeof offset === "undefined") {
        offset = 0;
    }

    elementTop = jQuery(this).offset().top;
    elementBottom = elementTop + jQuery(this).outerHeight();
    viewportTop = jQuery(window).scrollTop();
    viewportBottom = viewportTop + jQuery(window).height();

    return elementBottom - offset > viewportTop && elementTop + offset < viewportBottom;
};

// focus autofocus inputs when they become visible
jQuery(window).on("resize scroll", function () {
    var autofocusInput = jQuery("input[autofocus]").first();

    if (autofocusInput.length && autofocusInput.isInViewport()) {
        jQuery("input[autofocus]").first().focus();
    }
});

function changeFormTabOrder() {
    jQuery(".basePage form").on("keypress", function (event) {
        var tabulatorPressed = (event.keyCode === 9) ? true : false;
        var shiftPressed = event.shiftKey;

        if (tabulatorPressed && jQuery(event.target).is(":input")) {
            var formDescendants = jQuery(event.target).closest("form").find("*");
            var formInputs = formDescendants.filter(":input:visible").not(":disabled").not(":button").not(":submit");
            var firstInput = formInputs.first();
            var lastInput = formInputs.last();

            if (firstInput.is(event.target) && shiftPressed) {
                lastInput.focus();
                event.preventDefault();
            } else if (lastInput.is(event.target) && !(shiftPressed)) {
                firstInput.focus();
                event.preventDefault();
            }
        }
    });
}

function setupFeedbackPanel(id) {
    var listItems;

    if (typeof id !== "undefined") {
        listItems = jQuery(id + " li");
    } else {
        listItems = jQuery(".feedbackPanel li");
    }

    listItems.each(function (index) {
        var listElementClass = jQuery(this).attr("class");
        jQuery(this).removeClass(listElementClass).addClass("alert alert-" + listElementClass);
    });
}

function insertCharacter(formElement, character, selectionStart, selectionEnd) {
    var value = formElement.val();

    if (selectionStart <= selectionEnd) {
        formElement.val(value.substring(0, selectionStart) + character + value.substring(selectionEnd));
        formElement[0].setSelectionRange(selectionStart + 1, selectionStart + 1);
    } else {
        formElement.val(value.substring(0, selectionEnd) + character + value.substring(selectionStart));
        formElement[0].setSelectionRange(selectionEnd + 1, selectionEnd + 1);
    }
}

function setupApostropheKey() {
    jQuery(":input:text, textarea").keydown(function (event) {
        var character = "’", formElement = jQuery(this), selectionStart, selectionEnd;

        if (event.shiftKey && event.which === 163) {
            event.preventDefault();
            selectionStart = formElement[0].selectionStart;
            selectionEnd = formElement[0].selectionEnd;

            insertCharacter(formElement, character, selectionStart, selectionEnd);
        }
    });
}
