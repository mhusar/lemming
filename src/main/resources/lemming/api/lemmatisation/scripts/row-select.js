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
        selectRows(allRows, firstSelectedRowIndex, rowIndex);
    } else if (rowIndex == lastSelectedRowIndex) {
        selectRows(allRows, rowIndex, lastSelectedRowIndex);
    } else if (rowIndex > lastSelectedRowIndex) {
        selectRows(allRows, firstSelectedRowIndex, rowIndex);
    }
}

function onCtrlSelect(row) {
    jQuery(row).toggleClass("selected");
}

function onSelect(row) {
    jQuery(row).addClass("selected").siblings(".selected").removeClass("selected");
}

function selectRows(allRows, minIndex, maxIndex) {
    jQuery(allRows).each(function (index, row) {
        if (index >= minIndex && index <= maxIndex) {
            jQuery(row).addClass("selected");
        } else {
            jQuery(row).filter(".selected").removeClass("selected");
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

    event.preventDefault();
    event.stopPropagation();
}).on("mousedown", function (event) {
    // disable native table cell selection in firefox
    event.preventDefault();
});
