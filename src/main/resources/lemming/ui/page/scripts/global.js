jQuery.ajaxSetup({
    cache : false
});

jQuery(document).ready(function() {
    fixRequiredAttributeForSafari();
    setupFeedbackPanel();

    //setupApostropheKey();
    changeFormTabOrder();

    enableLemmaAutoComplete();
    enablePosAutoComplete();
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
    if (jQuery("input[autofocus]").first().isInViewport()) {
        jQuery("input[autofocus]").first().focus();
    }
});

// see http://caniuse.com/#feat=form-validation
function fixRequiredAttributeForSafari() {
    if (navigator.userAgent.indexOf("Safari") != -1
            && navigator.userAgent.indexOf("Chrome") == -1) {
        jQuery("form").submit(function(event) {
            var requiredFields = jQuery(this).find("[required]");

            requiredFields.each(function() {
                if (jQuery(this).val() == "") {
                    alert("Bitte alle erforderlichen Felder ausfüllen.");
                    jQuery(this).focus();
                    event.preventDefault();

                    return false;
                }
            });

            return true;
        });
    }
}

function setupFeedbackPanel(id) {
    var listItems;

    if (typeof id !== "undefined") {
        listItems = jQuery(id + " li");
    } else {
        listItems = jQuery(".feedbackPanel li");
    }

    listItems.each(function(index) {
        var listElementClass = jQuery(this).attr("class");

        jQuery(this).removeClass(listElementClass).addClass(
                "alert alert-" + listElementClass);
    });
}

function setupApostropheKey() {
    jQuery(":input:text, textarea")
            .keydown(
                    function(event) {
                        var character = "’", formElement = jQuery(this), selectionStart, selectionEnd;

                        if (event.shiftKey && event.which === 163) {
                            event.preventDefault();

                            selectionStart = formElement[0].selectionStart;
                            selectionEnd = formElement[0].selectionEnd;

                            insertCharacter(formElement, character,
                                    selectionStart, selectionEnd);
                        }
                    });
}

function insertCharacter(formElement, character, selectionStart, selectionEnd) {
    var value = formElement.val();

    if (selectionStart <= selectionEnd) {
        formElement.val(value.substring(0, selectionStart) + character
                + value.substring(selectionEnd));
        formElement[0]
                .setSelectionRange(selectionStart + 1, selectionStart + 1);
    } else {
        formElement.val(value.substring(0, selectionEnd) + character
                + value.substring(selectionStart));
        formElement[0].setSelectionRange(selectionEnd + 1, selectionEnd + 1);
    }
}

function changeFormTabOrder() {
    jQuery(".basePage form").on(
            "keypress",
            function(event) {
                var tabulatorPressed = (event.keyCode === 9) ? true : false;
                var shiftPressed = event.shiftKey;

                if (tabulatorPressed && jQuery(event.target).is(":input")) {
                    var formDescendants = jQuery(event.target).closest("form")
                            .find("*");
                    var formInputs = formDescendants.filter(":input:visible")
                            .not(":disabled").not(":button").not(":submit");
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

function enableLemmaAutoComplete() {
    if (typeof lemmaSelector !== "undefined") {
        jQuery(lemmaSelector).autocomplete({
            autoFocus : true,
            delay : 0,
            source : lemmaCallbackUrl
        });
    }
}

function enablePosAutoComplete() {
    if (typeof posSelector !== "undefined") {
        jQuery(posSelector).autocomplete({
            autoFocus : true,
            delay : 0,
            source : posCallbackUrl
        });
    }
}
