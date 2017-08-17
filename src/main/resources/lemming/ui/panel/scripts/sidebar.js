var sidebarWidth = 300,
    duration = 300,
    Orientation = Object.freeze({ LEFT: {}, RIGHT: {} });

function slideIn(orientation) {
    var margin = (jQuery("body").outerWidth(true) - jQuery("main .container").first().outerWidth()) / 2,
        marginDiff;

    if (margin < sidebarWidth) {
        marginDiff = (sidebarWidth - margin) * 2.0;

        if (orientation === Orientation.LEFT) {
            jQuery("body").animate({ marginRight: "-" + marginDiff + "px" }, duration, "linear");
        } else if (orientation === Orientation.RIGHT) {
            jQuery("body").animate({ marginLeft: "-" + marginDiff + "px" }, duration, "linear");
        }
    }

    window.setTimeout(function () {
        if (orientation === Orientation.LEFT) {
            jQuery(".sidebar-left").addClass("active");
        } else if (orientation === Orientation.RIGHT) {
            jQuery(".sidebar-right").addClass("active");
        }
    }, duration / 2);
}

function slideOut(orientation) {
    var margin = (jQuery("body").outerWidth(true) - jQuery("main .container").first().outerWidth()) / 2,
        marginDiff;

    if (margin < sidebarWidth) {
        marginDiff = (sidebarWidth - margin) * 2.0;

        window.setTimeout(function () {
            if (orientation === Orientation.LEFT) {
                jQuery("body").animate({ marginRight: "0px" }, duration, "linear");
            } else if (orientation === Orientation.RIGHT) {
                jQuery("body").animate({ marginLeft: "0px" }, duration, "linear");
            }
        }, duration / 2);
    }

    if (orientation === Orientation.LEFT) {
        jQuery(".sidebar-left").removeClass("active");
    } else if (orientation === Orientation.RIGHT) {
        jQuery(".sidebar-right").removeClass("active");
    }
}