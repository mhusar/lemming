var dropzone = jQuery("#${dropzoneId}"),
    fileUploadField = jQuery("#${fileUploadId}");

// prevent opening a file when it is dropped outside dropzone
jQuery(document).on("dragover drop", function (event) {
    if (jQuery(this).not(dropzone)) {
        event.preventDefault();
    }
});

dropzone
    .on("click", function (event) {
        event.stopPropagation();
        fileUploadField.click();
    })
    .on("dragenter", function (event) {
        dropzone.removeClass("dragleave").addClass("dragenter");
        event.preventDefault();
        event.stopPropagation();
    })
    .on("dragleave", function (event) {
        dropzone.removeClass("dragenter").addClass("dragleave");
        event.preventDefault();
        event.stopPropagation();
    })
    .on("dragover", function (event) {
        event.preventDefault();
        event.stopPropagation();
    })
    .on("drop", function (event) {
        if (event.originalEvent.dataTransfer) {
            if (event.originalEvent.dataTransfer.files.length) {
                var formData = new FormData(),
                    file = event.originalEvent.dataTransfer.files[0];

                formData.append("file", file);
                jQuery.data(document.body, "formData", formData);
                jQuery(".filename", dropzone).text(file.name).fadeIn();
                jQuery(".message", dropzone).hide();
            }
        }

        dropzone.removeClass("dragenter").addClass("dragleave");
        event.preventDefault();
        event.stopPropagation();
    });

fileUploadField
    .on("click", function (event) {
        event.stopPropagation();
    })
    .on("change", function (event) {
        var form = fileUploadField.closest("form"),
            formData = new FormData(),
            file = fileUploadField[0].files[0];

        form.trigger("reset");
        formData.append("file", file);
        jQuery.data(document.body, "formData", formData);
        jQuery(".filename", dropzone).text(file.name).show();
        jQuery(".message", dropzone).hide();
    });
