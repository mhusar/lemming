function onShiftSelect(row) {
    var tbody = jQuery(row).closest("tbody"), allRows, rowIndex, firstSelectedRowIndex, lastSelectedRowIndex;

    allRows = tbody.find("tr");
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

function scrollIntoViewport(tbody, scrollDown) {
    var focusedRow = jQuery(tbody).find(".focused").first();
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

function onShiftDownSelect(tbody) {
    var focusedRow = jQuery(tbody).find(".focused").first(),
        lastSelectedRow = jQuery(tbody).find(".selected").last();

    if (focusedRow.length) {
        var nextRow = jQuery(focusedRow).next();

        if (focusedRow.is(lastSelectedRow)) {
            if (nextRow.length) {
                focusedRow.removeClass("focused");
                nextRow.find(":checkbox").first().prop("checked", true).trigger("change");
                nextRow.addClass("selected focused");
            }
        } else {
            if (nextRow.length) {
                focusedRow.removeClass("selected focused");
                focusedRow.find(":checkbox").first().prop("checked", false).trigger("change");
                nextRow.find(":checkbox").first().prop("checked", true).trigger("change");
                nextRow.addClass("selected focused");
            }
        }
    } else if (lastSelectedRow.length) {
        var nextRow = jQuery(lastSelectedRow).next();
        nextRow.find(":checkbox").first().prop("checked", true).trigger("change");
        nextRow.addClass("selected focused");
    }

    scrollIntoViewport(tbody, true);
}

function onShiftUpSelect(tbody) {
    var focusedRow = jQuery(tbody).find(".focused").first(),
        firstSelectedRow = jQuery(tbody).find(".selected").first();

    if (focusedRow.length) {
        var previousRow = jQuery(focusedRow).prev();

        if (focusedRow.is(firstSelectedRow)) {
            if (previousRow.length) {
                focusedRow.removeClass("focused");
                previousRow.find(":checkbox").first().prop("checked", true).trigger("change");
                previousRow.addClass("selected focused");
            }
        } else {
            if (previousRow.length) {
                focusedRow.removeClass("selected focused");
                focusedRow.find(":checkbox").first().prop("checked", false).trigger("change");
                previousRow.find(":checkbox").first().prop("checked", true).trigger("change");
                previousRow.addClass("selected focused");
            }
        }
    } else if (firstSelectedRow.length) {
         var previousRow = jQuery(firstSelectedRow).prev();
         previousRow.find(":checkbox").first().prop("checked", true).trigger("change");
         previousRow.addClass("selected focused");
    }

    scrollIntoViewport(tbody, false);
}

function onCtrlSelect(row) {
    var focusedRow = jQuery(row).closest("tbody").find(".focused").first(),
        checkbox = jQuery(row).find(":checkbox").first();

    jQuery(focusedRow).removeClass("focused");
    jQuery(row).toggleClass("selected").addClass("focused");
    jQuery(checkbox).prop("checked", !jQuery(checkbox).prop("checked"));
}

function onCtrlDownSelect(tbody) {
    var focusedRow = jQuery(tbody).find(".focused").first(),
        lastSelectedRow = jQuery(tbody).find(".selected").last();

    if (focusedRow.length) {
        focusedRow.removeClass("focused");
        jQuery(focusedRow).next().addClass("focused");
    } else if (lastSelectedRow.length) {
        jQuery(lastSelectedRow).next().addClass("focused");
    }

    scrollIntoViewport(tbody, true);
}

function onCtrlUpSelect(tbody) {
    var focusedRow = jQuery(tbody).find(".focused").first(),
        firstSelectedRow = jQuery(tbody).find(".selected").first();

    if (focusedRow.length) {
        focusedRow.removeClass("focused");
        jQuery(focusedRow).prev().addClass("focused");
    } else if (firstSelectedRow.length) {
        jQuery(firstSelectedRow).prev().addClass("focused");
    }

    scrollIntoViewport(tbody, false);
}

function onCtrlSpaceSelect(tbody) {
    var focusedRow = jQuery(tbody).find(".focused").first();

    if (focusedRow.length) {
        if (focusedRow.isInViewport(100)) {
            var checkbox = jQuery(focusedRow).find(":checkbox").first();
            jQuery(focusedRow).toggleClass("selected");
            jQuery(checkbox).prop("checked", !jQuery(checkbox).prop("checked"));
        }
    }
}

function onSelect(row) {
    jQuery(row).find(":checkbox").first().prop("checked", true).trigger("change");
    jQuery(row).addClass("selected").siblings(".selected,.focused").each(function (index, row) {
        jQuery(row).find(":checkbox").first().prop("checked", false).trigger("change");
        jQuery(row).removeClass("selected focused");
    });
}

function onDownSelect(tbody) {
    var lastSelectedRow = jQuery(tbody).find(".selected").last();

    if (lastSelectedRow.length) {
        var nextRow = jQuery(lastSelectedRow).next();

        if (nextRow.length) {
            nextRow.find(":checkbox").first().prop("checked", true).trigger("change");
            nextRow.addClass("selected focused").siblings(".selected,.focused").each(function (index, row) {
                jQuery(row).find(":checkbox").first().prop("checked", false).trigger("change");
                jQuery(row).removeClass("selected focused");
            });

            if (!nextRow.isInViewport(150)) {
                jQuery("html,body").animate({ scrollTop: "+=" + nextRow.outerHeight() + "px" }, 150);
            }
        }
    }
}

function onUpSelect(tbody) {
    var firstSelectedRow = jQuery(tbody).find(".selected").first();

    if (firstSelectedRow.length) {
        var previousRow = jQuery(firstSelectedRow).prev();

        if (previousRow.length) {
            previousRow.find(":checkbox").first().prop("checked", true).trigger("change");
            previousRow.addClass("selected focused").siblings(".selected,.focused").each(function (index, row) {
                jQuery(row).find(":checkbox").first().prop("checked", false).trigger("change");
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
            jQuery(row).find(":checkbox").first().prop("checked", true).trigger("change");
            jQuery(row).addClass("selected");
        } else {
            jQuery(row).filter(".selected,.focused").each(function (index, row) {
                jQuery(row).find(":checkbox").first().prop("checked", false).trigger("change");
                jQuery(row).removeClass("selected focused");
            });
        }
    });
}

jQuery(document)
    .on("click", "table.selectable tbody tr", function (event) {
        if (event.shiftKey) {
            onShiftSelect(this);
        } else if (event.ctrlKey || event.metaKey) {
            onCtrlSelect(this);
        } else {
            onSelect(this);
        }

        event.stopPropagation();
    }).on("mousedown", "table.selectable tbody td", function (event) {
    // disable native table cell selection in firefox
    event.preventDefault();
});

jQuery(document).on("keydown", function (event) {
    var tbody = jQuery("table.selectable tbody");

    // 32 = space, 38 = up, 40 = down
    if (tbody.length && (event.which === 32 || event.which === 38 || event.which === 40)) {
        if (event.shiftKey) {
            if (event.which === 40) {
                onShiftDownSelect(tbody);
            } else if (event.which === 38) {
                onShiftUpSelect(tbody);
            } else if (event.which === 32) {
                // prevent form submit; why does this happen?
                event.preventDefault();
            }
        } else if (event.ctrlKey || event.metaKey) {
            if (event.which === 40) {
                onCtrlDownSelect(tbody);
            } else if (event.which === 38) {
                onCtrlUpSelect(tbody);
            } else if (event.which === 32) {
                onCtrlSpaceSelect(tbody);
            }
        } else {
            if (event.which === 40) {
                onDownSelect(tbody);
            } else if (event.which === 38) {
                onUpSelect(tbody);
            }
        }
    }
});

function getSelectedRows() {
    return jQuery("table.selectable tbody").find(".selected");
}

function selectFirstRow() {
    var firstRow = jQuery("table.selectable tbody tr").first();

    if (firstRow.length) {
        jQuery(firstRow).find(":checkbox").first().prop("checked", true).trigger("change");
        jQuery(firstRow).addClass("selected");
    }
}

// select the first table row when a selectable table is added
jQuery(document).ready(function () {
    var target = jQuery("table.selectable").closest("div")[0],
        observer = new MutationObserver(function (mutations) {
            mutations.forEach(function (mutation) {
                if (mutation.addedNodes.length) {
                    mutation.addedNodes.forEach(function (addedNode) {
                        if (jQuery(addedNode).is("table")) {
                            if (getSelectedRows().length === 0) {
                                selectFirstRow();
                            }
                        }
                    });
                }
            });
        });

    observer.observe(target, { attributes: false, characterData: false, childList: true, subtree: true });
    // select first row after document ready
    // see https://stackoverflow.com/questions/3008696
    jQuery(window).load(function () {
        selectFirstRow();
    });
});
