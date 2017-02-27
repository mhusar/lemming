jQuery(document).on("click", ".senses .edit-tree", function () {
    if (jQuery(".senses").data("transitioning") !== true) {
        jQuery(".senses").toggleClass("edit");
    }
});

jQuery(document).on("transitionrun", ".senses", function () {
    jQuery(".senses").data("transitioning", true);
});

jQuery(document).on("transitionend", ".senses", function () {
    jQuery(".senses").data("transitioning", false);
});
