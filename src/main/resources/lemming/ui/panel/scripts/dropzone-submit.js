jQuery(document).one("click", "#${submitButtonId}", function () {
    var dropzone = jQuery("#${dropzoneId}"),
        formData = jQuery.data(document.body, "formData");

    if (typeof formData !== "undefined") {
        jQuery.removeData(document.body, "formData");
        jQuery.post({
            url: "${callbackUrl}",
            data: formData,
            cache: false,
            contentType: false,
            processData: false
        }).done(function (data) {
            var javaScript = jQuery(data).find("#dropzone-submit").first().text(),
                script = jQuery("<script id='dropzone-submit'/>");

            jQuery("#dropzone-submit").remove();
            script[0].appendChild(document.createCDATASection(javaScript));
            jQuery("body").append(script[0]);
            jQuery(".filename", dropzone).hide().text("");
            jQuery(".message", dropzone).fadeIn();
        });
    }
});
