var sidebarWidth = 300,
    duration = 300,
    Orientation = Object.freeze({ LEFT: {}, RIGHT: {} });

function slideIn(orientation) {
    var margin = (jQuery("body").outerWidth(true) - jQuery("main .container").first().outerWidth()) / 2,
        marginDiff;

    if (margin < sidebarWidth) {
        marginDiff = (sidebarWidth - margin);
        document.body.style.setProperty("--margin-diff", marginDiff);

        if (orientation === Orientation.LEFT) {
            jQuery("main").addClass("slide-in-main-left").removeClass("slide-out-main-left");
        } else if (orientation === Orientation.RIGHT) {
            jQuery("main").addClass("slide-in-main-right").removeClass("slide-out-main-right");
        }
    } else {
        document.body.style.setProperty("--margin-diff", 0);
    }

    if (orientation === Orientation.LEFT) {
        jQuery(".sidebar-left").addClass("slide-in").removeClass("slide-out");
    } else if (orientation === Orientation.RIGHT) {
        jQuery(".sidebar-right").addClass("slide-in").removeClass("slide-out");
    }
}

function slideOut(orientation) {
    var margin = (jQuery("body").outerWidth(true) - jQuery("main .container").first().outerWidth()) / 2,
        marginDiff;

    if (margin < sidebarWidth) {
        marginDiff = (sidebarWidth - margin);
        document.body.style.setProperty("--margin-diff", marginDiff);

        if (orientation === Orientation.LEFT) {
            jQuery("main").addClass("slide-out-main-left").removeClass("slide-in-main-left");
        } else if (orientation === Orientation.RIGHT) {
            jQuery("main").addClass("slide-out-main-right").removeClass("slide-in-main-right");
        }
    } else {
        document.body.style.setProperty("--margin-diff", 0);
    }

    if (orientation === Orientation.LEFT) {
        jQuery(".sidebar-left").removeClass("slide-in").addClass("slide-out");
    } else if (orientation === Orientation.RIGHT) {
        jQuery(".sidebar-right").removeClass("slide-in").addClass("slide-out");
    }
}