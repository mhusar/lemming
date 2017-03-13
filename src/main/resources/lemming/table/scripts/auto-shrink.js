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
                jQuery(column).addClass("overflow").css("max-width", newColumnWidth + "px");
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

