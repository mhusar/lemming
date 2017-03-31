function autoShrink(table) {
    var tableWidth = jQuery(table).innerWidth(), parentWidth = jQuery(table).parent().innerWidth();

    if (tableWidth > parentWidth) {
        var columnWidthSum = 0, columnWidths = [], tableWidthDifference = tableWidth - parentWidth, newColumnWidth;

        jQuery(table).find("tbody tr").first().find("td.auto-shrink").each(function (index, column) {
            var width = jQuery(column).innerWidth();
            columnWidthSum += width;
            columnWidths.push(width);
        });

        if (columnWidths.length === 0 || tableWidthDifference >= columnWidthSum) {
            return;
        }

        newColumnWidth = (columnWidthSum - tableWidthDifference) / columnWidths.length;

        jQuery(table).find("tbody tr").each(function (index, row) {
            jQuery(row).find("td.auto-shrink").each(function (index, column) {
                var width = jQuery("span", column).width();

                while (width > newColumnWidth) {
                    var text = jQuery("span.string", column).text();

                    if (jQuery(column).hasClass("auto-shrink-left")) {
                        text = "…" + text.replace(/^…/, "").substring(1).replace(/^\s+/, "");
                    } else {
                        text = text.replace(/\…$/, "").slice(0, -1).replace(/\s+$/, "") + "…";
                    }

                    jQuery("span.string", column).text(text);
                    width = jQuery("span", column).width();
                }
            });
        });
    }
}

function observeTables() {
    var target = jQuery("table").parent()[0],
        observer = new MutationObserver(function (mutations) {
            mutations.forEach(function (mutation) {
                if (mutation.addedNodes.length) {
                    mutation.addedNodes.forEach(function (addedNode) {
                        if (jQuery(addedNode).is("table")) {
                            autoShrink(addedNode);
                        }
                    });
                }
            });
        });

    observer.observe(target, { attributes: false, characterData: false, childList: true, subtree: false });
}

jQuery(document).ready(function () {
    observeTables();

    jQuery("table").each(function (index, table) {
        autoShrink(table);
    });
});

