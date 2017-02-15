function onShiftSelect(row) {
    var table = jQuery(row).closest("table"), allRows, rowIndex, firstSelectedRowIndex, lastSelectedRowIndex;

    allRows = table.find("tr");
    rowIndex = allRows.index(row);
    firstSelectedRowIndex = allRows.index(allRows.filter(".selected").first());
    lastSelectedRowIndex = allRows.index(allRows.filter(".selected").last());

    if (rowIndex < firstSelectedRowIndex) {
        selectRows(allRows, rowIndex, firstSelectedRowIndex);
    } else if (rowIndex === firstSelectedRowIndex) {
        selectRows(allRows, rowIndex, lastSelectedRowIndex);
    } else if (rowIndex > firstSelectedRowIndex && rowIndex < lastSelectedRowIndex) {
        selectRows(allRows, rowIndex, lastSelectedRowIndex);
    } else if (rowIndex === lastSelectedRowIndex) {
        selectRows(allRows, firstSelectedRow, lastSelectedRowIndex);
    } else if (rowIndex > lastSelectedRowIndex) {
        selectRows(allRows, firstSelectedRowIndex, rowIndex);
    }
}

function scrollIntoViewport(table, scrollDown) {
    var focusedRow = jQuery(table).find(".focused").first();
    if (focusedRow.length) {
        if (!focusedRow.isInViewport(150)) {
            if (scrollDown) {
                jQuery("html,body").animate({ scrollTop: "+=" + focusedRow.outerHeight() + "px" }, 150);
            } else {
                jQuery("html,body").animate({ scrollTop: "-=" + focusedRow.outerHeight() + "px" }, 150);
            }
        }
    }
}

function onShiftDownSelect(table) {
    var focusedRow = jQuery(table).find(".focused").first(),
        lastSelectedRow = jQuery(table).find(".selected").last();

    if (focusedRow.length) {
        var nextRow = jQuery(focusedRow).next();

        if (focusedRow.is(lastSelectedRow)) {
            if (nextRow.length) {
                focusedRow.removeClass("focused");
                nextRow.find(":checkbox").first().prop("checked", true);
                nextRow.addClass("selected focused");
            }
        } else {
            if (nextRow.length) {
                focusedRow.removeClass("selected focused");
                focusedRow.find(":checkbox").first().prop("checked", false);
                nextRow.find(":checkbox").first().prop("checked", true);
                nextRow.addClass("selected focused");
            }
        }
    } else if (lastSelectedRow.length) {
        var nextRow = jQuery(lastSelectedRow).next();
        nextRow.find(":checkbox").first().prop("checked", true);
        nextRow.addClass("selected focused");
    }

    scrollIntoViewport(table, true);
}

function onShiftUpSelect(table) {
    var focusedRow = jQuery(table).find(".focused").first(),
        firstSelectedRow = jQuery(table).find(".selected").first();

    if (focusedRow.length) {
        var previousRow = jQuery(focusedRow).prev();

        if (focusedRow.is(firstSelectedRow)) {
            if (previousRow.length) {
                focusedRow.removeClass("focused");
                previousRow.find(":checkbox").first().prop("checked", true);
                previousRow.addClass("selected focused");
            }
        } else {
            if (previousRow.length) {
                focusedRow.removeClass("selected focused");
                focusedRow.find(":checkbox").first().prop("checked", false);
                previousRow.find(":checkbox").first().prop("checked", true);
                previousRow.addClass("selected focused");
            }
        }
    } else if (firstSelectedRow.length) {
         var previousRow = jQuery(firstSelectedRow).prev();
         previousRow.find(":checkbox").first().prop("checked", true);
         previousRow.addClass("selected focused");
    }

    scrollIntoViewport(table, false);
}

function onCtrlSelect(row) {
    var checkbox = jQuery(row).find(":checkbox").first();
    jQuery(row).toggleClass("selected").addClass("focused");
    jQuery(checkbox).prop("checked", !jQuery(checkbox).prop("checked"));
}

function onCtrlDownSelect(table) {
    var focusedRow = jQuery(table).find(".focused").first(),
        lastSelectedRow = jQuery(table).find(".selected").last();

    if (focusedRow.length) {
        focusedRow.removeClass("focused");
        jQuery(focusedRow).next().addClass("focused");
    } else if (lastSelectedRow.length) {
        jQuery(lastSelectedRow).next().addClass("focused");
    }

    scrollIntoViewport(table, true);
}

function onCtrlUpSelect(table) {
    var focusedRow = jQuery(table).find(".focused").first(),
        firstSelectedRow = jQuery(table).find(".selected").first();

    if (focusedRow.length) {
        focusedRow.removeClass("focused");
        jQuery(focusedRow).prev().addClass("focused");
    } else if (firstSelectedRow.length) {
        jQuery(firstSelectedRow).prev().addClass("focused");
    }

    scrollIntoViewport(table, false);
}

function onCtrlSpaceSelect(table) {
    var focusedRow = jQuery(table).find(".focused").first();

    if (focusedRow.length) {
        if (focusedRow.isInViewport(100)) {
            var checkbox = jQuery(focusedRow).find(":checkbox").first();
            jQuery(focusedRow).toggleClass("selected");
            jQuery(checkbox).prop("checked", !jQuery(checkbox).prop("checked"));
        }
    }
}

function onSelect(row) {
    jQuery(row).find(":checkbox").first().prop("checked", true);
    jQuery(row).addClass("selected").siblings(".selected,.focused").each(function (index, row) {
        jQuery(row).find(":checkbox").first().prop("checked", false);
        jQuery(row).removeClass("selected focused");
    });
}

function onDownSelect(table) {
    var lastSelectedRow = jQuery(table).find(".selected").last();

    if (lastSelectedRow.length) {
        var nextRow = jQuery(lastSelectedRow).next();

        if (nextRow.length) {
            nextRow.find(":checkbox").first().prop("checked", true);
            nextRow.addClass("selected focused").siblings(".selected,.focused").each(function (index, row) {
                jQuery(row).find(":checkbox").first().prop("checked", false);
                jQuery(row).removeClass("selected focused");
            });

            if (!nextRow.isInViewport(150)) {
                jQuery("html,body").animate({ scrollTop: "+=" + nextRow.outerHeight() + "px" }, 150);
            }
        }
    }
}

function onUpSelect(table) {
    var firstSelectedRow = jQuery(table).find(".selected").first();

    if (firstSelectedRow.length) {
        var previousRow = jQuery(firstSelectedRow).prev();

        if (previousRow.length) {
            previousRow.find(":checkbox").first().prop("checked", true);
            previousRow.addClass("selected focused").siblings(".selected,.focused").each(function (index, row) {
                jQuery(row).find(":checkbox").first().prop("checked", false);
                jQuery(row).removeClass("selected focused");
            });

            if (!previousRow.isInViewport(150)) {
                jQuery("html,body").animate({ scrollTop: "-=" + previousRow.outerHeight() + "px" }, 150);
            }
        }
    }
}

function selectRows(allRows, minIndex, maxIndex) {
    jQuery(allRows).each(function (index, row) {
        if (index >= minIndex && index <= maxIndex) {
            jQuery(row).find(":checkbox").first().prop("checked", true);
            jQuery(row).addClass("selected");
        } else {
            jQuery(row).filter(".selected,.focused").each(function (index, row) {
                jQuery(row).find(":checkbox").first().prop("checked", false);
                jQuery(row).removeClass("selected focused");
            });
        }
    });
}

jQuery(document).on("click", "table.selectable tr", function (event) {
    if (event.shiftKey) {
        onShiftSelect(this);
    } else if (event.ctrlKey) {
        onCtrlSelect(this);
    } else {
        onSelect(this);
    }

    event.stopPropagation();
}).on("mousedown", function (event) {
    // disable native table cell selection in firefox
    event.preventDefault();
});

jQuery(document).on("keydown", function (event) {
    var table = jQuery("table.selectable");

    // 32 = space, 38 = up, 40 = down
    if (table.length && event.which === 32 || event.which === 38 || event.which === 40) {
        if (event.shiftKey) {
            if (event.which === 40) {
                onShiftDownSelect(table);
            } else if (event.which === 38) {
                onShiftUpSelect(table);
            } else if (event.which === 32) {
                // prevent form submit; why does this happen?
                event.preventDefault();
            }
        } else if (event.ctrlKey) {
            if (event.which === 40) {
                onCtrlDownSelect(table);
            } else if (event.which === 38) {
                onCtrlUpSelect(table);
            } else if (event.which === 32) {
                onCtrlSpaceSelect(table);
            }
        } else {
            if (event.which === 40) {
                onDownSelect(table);
            } else if (event.which === 38) {
                onUpSelect(table);
            }
        }
    }
});
