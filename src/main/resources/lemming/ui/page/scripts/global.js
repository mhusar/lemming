jQuery.ajaxSetup({
    cache : false
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

jQuery.debounce = function (delay, callback) {
    var timeout = null;

    return function () {
        var _arguments = arguments;

        if (timeout) {
            clearTimeout(timeout);
        }

        timeout = setTimeout(function () {
            callback.apply(null, _arguments);
            timeout = null;
        }, delay);
    };
}
