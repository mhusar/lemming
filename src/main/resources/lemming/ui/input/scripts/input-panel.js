var InputPanel, TagDialog;

InputPanel = (function () {
    "use strict";
    var documentIdentifier = "", duration = 500, inputPanelMargin = 15, fullscreenMode = false, locked = false,
    preventBlur = false, scrollIntoPosition = function () {
        var focusedElement = jQuery(":focus"), inputPanel = jQuery(".inputPanel"), overlap,
        windowHeight = jQuery(window).height(), windowScrollTop = jQuery(window).scrollTop(), container;

        if (focusedElement.length) {
            container = focusedElement.parents(".xmlEditor");

            if (container.length) {
                overlap = (container.offset().top + container.outerHeight()) -
                (windowHeight + windowScrollTop - inputPanel.outerHeight());
            } else {
                overlap = (focusedElement.offset().top + focusedElement.outerHeight()) -
                (windowHeight + windowScrollTop - inputPanel.outerHeight());
            }

            if (overlap > -1 * inputPanelMargin) {
                jQuery("html, body").animate({
                    "scrollTop" : "+=" + (inputPanelMargin + overlap)
                }, duration);
            }
        }
    },
    getPixelsAsNumber = function (value) {
        var number = parseFloat(value, 10);

        if (isNaN(number)) {
            return 0;
        } else {
            return number;
        }
    },
    adjust = function () {
        var body = jQuery("body"), inputPanel = jQuery(".inputPanel"),
        lemmatizationPanel = jQuery(".lemmatizationPanel"),
        lemmatizationPanelHeight = jQuery(".lemmatizationPanel .navbar").outerHeight();

        if (inputPanel.outerHeight() > getPixelsAsNumber(body.css("padding-bottom"))) {
            if (lemmatizationPanel.length) {
                body.animate({
                    "padding-bottom" : lemmatizationPanelHeight + inputPanel.outerHeight() + "px"
                }, duration);
            } else {
                body.animate({
                    "padding-bottom" : inputPanel.outerHeight() + "px"
                }, duration);
            }
        }

        if (lemmatizationPanel.length) {
            inputPanel.animate({
                "bottom" : lemmatizationPanelHeight + "px"
            }, duration, function () {
                scrollIntoPosition();
            });
        } else {
            inputPanel.animate({
                "bottom" : "0px"
            }, duration, function () {
                scrollIntoPosition();
            });
        }
    },
    focus = function () {
        var focusedElement = jQuery(":focus");

        if (focusedElement.length) {
            focusedElement.focus();
        }
    },
    findXmlEditor = function (formElement) {
        var mirror, id;

        if (formElement.is("textarea")) {
            mirror = jQuery(formElement).parents(".mirror");

            if (mirror.length) {
                id = mirror.attr("id");

                if (id !== undefined) {
                    return XmlEditor.editors[id];
                }
            }
        }
    },
    isEnabled = function () {
        if (jQuery(".inputPanel").hasClass("disabled")) {
            return false;
        }

        return true;
    },
    prepare = function () {
        var body = jQuery("body"), inputPanel = jQuery(".inputPanel"),
        lemmatizationPanel = jQuery(".lemmatizationPanel");

        body.stop(true, true);
        inputPanel.stop(true, true);

        if (inputPanel.css("visibility") === "hidden") {
            if (lemmatizationPanel.length) {
                body.css("padding-bottom", 51 + "px");
            } else {
                body.css("padding-bottom", "0px");
            }

            inputPanel.css({
                "bottom" : (-1 * inputPanel.outerHeight()) + "px",
                "visibility" : "visible"
            });
        }
    },
    setEnabled = function (state) {
        var inputPanel = jQuery(".inputPanel");

        if (state) {
            inputPanel.removeClass("disabled");
        } else {
            inputPanel.addClass("disabled");
        }
    },
    setLocked = function (state) {
        locked = state;
    },
    setFullscreenMode = function (state) {
        fullscreenMode = state;
    },
    appendShiftKey = function () {
        var shiftKeyElement = jQuery("<li></li>");

        shiftKeyElement.addClass("shift off");
        jQuery(".inputPanel .characters").append(shiftKeyElement);
    },
    appendCharacterElement = function (character) {
        var characterElement = jQuery("<li></li>");

        characterElement.text(character).addClass("character");
        jQuery(".inputPanel .characters").append(characterElement);
    },
    insertCharacter = function (formElement, character,
        selectionStart, selectionEnd) {
            var selectionRange, value;

            if (formElement instanceof jQuery) {
                value = formElement.val();

                if (selectionStart <= selectionEnd) {
                    formElement.val(value.substring(0, selectionStart) + character + value.substring(selectionEnd));
                    formElement[0].setSelectionRange(selectionStart + 1, selectionStart + 1);
                } else {
                    formElement.val(value.substring(0, selectionEnd) + character + value.substring(selectionStart));
                    formElement[0].setSelectionRange(selectionEnd + 1, selectionEnd + 1);
                }
            } else {
                selectionRange = formElement.getSelectionRange();

                formElement.session.getDocument().remove(selectionRange);
                formElement.insert(character);
                formElement.focus();
            }
        },
        initCharacterElements = function () {
            var charactersArray = jQuery("body").data("characters"), lastFocusedElement, selectionStart, selectionEnd;

            appendShiftKey();

            if (charactersArray instanceof Array) {
                jQuery.each(charactersArray, function (index, value) {
                    appendCharacterElement(value);
                });
            }

            jQuery(".inputPanel .characters")
            .on("click", ".shift.off", function () {
                if (!(isEnabled())) {
                    return;
                }

                jQuery(this).removeClass("off").addClass("on");
                jQuery(".inputPanel .characters .character").each(function (index, value) {
                    var text = jQuery(this).text();
                    jQuery(this).text(text.toUpperCase());
                });
            })
            .on("click", ".shift.on", function () {
                if (!(isEnabled())) {
                    return;
                }

                jQuery(this).removeClass("on").addClass("off");
                jQuery(".inputPanel .characters .character").each(function (index, value) {
                    var text = jQuery(this).text();
                    jQuery(this).text(text.toLowerCase());
                });
            })
            .on("click", ".character", function () {
                var character = jQuery(this).text(), editor;

                if (lastFocusedElement.length) {
                    editor = findXmlEditor(lastFocusedElement);

                    if (editor !== undefined) {
                        insertCharacter(editor, character);
                    } else {
                        insertCharacter(lastFocusedElement, character, selectionStart, selectionEnd);
                    }
                }
            })
            .on("mousedown", function () {
                preventBlur = true;
                lastFocusedElement = jQuery(":focus");

                if (lastFocusedElement.length) {
                    selectionStart = lastFocusedElement[0].selectionStart;
                    selectionEnd = lastFocusedElement[0].selectionEnd;
                }
            });
        },
        appendTagElement = function (tagObject) {
            var tagElement = jQuery("<li></li>");
            tagElement.text(tagObject.name).addClass("tag").data("tag", tagObject);
            jQuery(".inputPanel .tags").append(tagElement);
        },
        clearDocumentIdentifier = function () {
            documentIdentifier = "";
        },
        clearTagElements = function () {
            jQuery(".inputPanel .tags").empty();
        },
        setupTagElements = function (documentObject) {
            if (documentIdentifier === documentObject.identifier) {
                return;
            }

            documentIdentifier = documentObject.identifier;

            clearTagElements();
            jQuery.each(documentObject.tags, function (index, value) {
                appendTagElement(value);
            });
        },
        insertTag = function (formElement, tagObject, selectionStart, selectionEnd) {
            var cursorPosition, insertString, selectionRange, selectionString, value;

            if (formElement instanceof jQuery) {
                value = formElement.val();

                if (selectionStart <= selectionEnd) {
                    insertString = (tagObject.selfClosing) ? "<" + tagObject.name + "/>" : "<" + tagObject.name + ">" +
                    value.substring(selectionStart, selectionEnd) + "</" + tagObject.name + ">";
                    cursorPosition = selectionStart + insertString.length;

                    formElement.val(value.substring(0, selectionStart) + insertString + value.substring(selectionEnd));
                } else {
                    insertString = (tagObject.selfClosing) ? "<" + tagObject.name + "/>" : "<" + tagObject.name + ">" +
                    value.substring(selectionEnd, selectionStart) + "</" + tagObject.name + ">";
                    cursorPosition = selectionEnd + insertString.length;

                    formElement.val(value.substring(0, selectionEnd) + insertString + value.substring(selectionStart));
                }

                formElement[0].setSelectionRange(cursorPosition, cursorPosition);
            } else {
                selectionRange = formElement.getSelectionRange();
                selectionString = formElement.session.getTextRange(selectionRange);
                insertString = (tagObject.selfClosing) ? "<" + tagObject.name + "/>" : "<" + tagObject.name + ">" +
                selectionString + "</" + tagObject.name + ">";

                formElement.session.getDocument().remove(selectionRange);
                formElement.insert(insertString);
                formElement.focus();
            }
        },
        showTagDialog = function (formElement, tagObject, selectionStart, selectionEnd) {
            TagDialog.show(formElement, tagObject, selectionStart, selectionEnd);
        },
        initTagElements = function () {
            var lastFocusedElement, selectionStart, selectionEnd;

            jQuery("main").on("focus", "input[type=number], input:text", function () {
                var documentObject = jQuery(this).data("document");

                setEnabled(true);

                if (documentObject !== undefined) {
                    setupTagElements(documentObject);
                } else {
                    clearDocumentIdentifier();
                    clearTagElements();
                }

                prepare();
                adjust();
            }).on("focus", "textarea", function () {
                var container = jQuery(this).parents(".xmlEditor"), documentObject;

                setEnabled(true);

                if (container.length) {
                    documentObject = container.data("document");
                }

                if (documentObject !== undefined) {
                    setupTagElements(documentObject);
                } else {
                    clearDocumentIdentifier();
                    clearTagElements();
                }

                prepare();
                adjust();
            }).on("blur", "input[type=number], input:text, textarea", function () {
                var that = this;

                if (preventBlur) {
                    preventBlur = false;

                    setTimeout(function () {
                        jQuery(that).focus();
                    }, 0);

                    return;
                }

                if (locked) {
                    return;
                }

                if (fullscreenMode) {
                    setEnabled(false);
                    return;
                }

                setTimeout(function () {
                    var focusedElement = jQuery(":focus");

                    if (focusedElement.length) {
                        if (focusedElement.is("input[type=number]") || focusedElement.is("input:text") ||
                        focusedElement.is("textarea")) {
                            return;
                        }
                    } else {
                        clearDocumentIdentifier();
                        clearTagElements();
                        setEnabled(false);
                    }
                }, 200);
            });
            jQuery(".inputPanel .tags")
            .on("click", ".tag", function () {
                var tagObject = jQuery(this).data("tag"), editor;

                if (lastFocusedElement.length) {
                    editor = findXmlEditor(lastFocusedElement);

                    if (editor !== undefined) {
                        if (tagObject.attributes.length) {
                            showTagDialog(editor, tagObject);
                        } else {
                            insertTag(editor, tagObject);
                        }
                    } else {
                        if (tagObject.attributes.length) {
                            showTagDialog(lastFocusedElement, tagObject, selectionStart, selectionEnd);
                        } else {
                            insertTag(lastFocusedElement, tagObject, selectionStart, selectionEnd);
                        }
                    }
                }
            })
            .on("mousedown", function () {
                preventBlur = true;
                lastFocusedElement = jQuery(":focus");

                if (lastFocusedElement.length) {
                    selectionStart = lastFocusedElement[0].selectionStart;
                    selectionEnd = lastFocusedElement[0].selectionEnd;
                }
            });
        },
        init = function () {
            initCharacterElements();
            initTagElements();
        };

    return {
        init : init,
        focus : focus,
        setLocked : setLocked,
        setFullscreenMode : setFullscreenMode
    };
}());

TagDialog = (function () {
    "use strict";
    var addAttributes = function (tagObject) {
        var attributeObject, formControl, formGroup, i, tabDialogBody = jQuery(".tab-dialog .modal-body");

        for (i = 0; i < tagObject.attributes.length; i = i + 1) {
            attributeObject = tagObject.attributes[i];
            formControl = jQuery("<input type='text'/>").addClass("form-control")
            .attr("data-attribute", attributeObject.name);
            formGroup = jQuery("<div></div>").addClass("form-group");

            tabDialogBody.append(jQuery("<div></div>").addClass("row")
            .append(jQuery("<div></div>").addClass("col-md-12").append(formGroup)));
            formGroup.append(jQuery("<label></label>").addClass("control-label col-sm-3").text(attributeObject.name));
            formGroup.append(jQuery("<div></div>").addClass("col-sm-9").append(formControl));

            if (attributeObject.required) {
                formControl.attr("required", "required");
            }
        }
    },
    addTitle = function (tagObject) {
        var tabDialogTitle = jQuery(".tab-dialog .modal-title"), titleStart =  tabDialogTitle.data("title-start"),
        titleEnd = tabDialogTitle.data("title-end");

        tabDialogTitle.text(titleStart + tagObject.name + titleEnd);
    },
    createAttributeString = function () {
        var attributeName, attributeString = "", attributeValue = "",
        formControls = jQuery(".tab-dialog .form-control");

        /*jslint unparam: true*/
        formControls.each(function (index, value) {
            attributeValue = jQuery(this).val();
            attributeName = jQuery(this).data("attribute");

            if (attributeValue.length) {
                attributeString += " " + attributeName + "=" + "\"" + attributeValue + "\"";
            }
        });
        /*jslint unparam: false*/

        return attributeString;
    },
    insertTag = function (formElement, tagObject, selectionStart, selectionEnd) {
        var attributeString = createAttributeString(), cursorPosition, insertString, selectionRange,
        selectionString, value;

        if (formElement instanceof jQuery) {
            value = formElement.val();

            if (selectionStart <= selectionEnd) {
                insertString = (tagObject.selfClosing) ? "<" + tagObject.name + attributeString + "/>" :
                "<" + tagObject.name + attributeString + ">" + value.substring(selectionStart, selectionEnd) +
                "</" + tagObject.name + ">";
                cursorPosition = selectionStart + insertString.length;

                formElement.val(value.substring(0, selectionStart) + insertString + value.substring(selectionEnd));
            } else {
                insertString = (tagObject.selfClosing) ? "<" + tagObject.name + attributeString + "/>" :
                "<" + tagObject.name + attributeString + ">" + value.substring(selectionEnd, selectionStart) +
                "</" + tagObject.name + ">";
                cursorPosition = selectionEnd + insertString.length;

                formElement.val(value.substring(0, selectionEnd) + insertString + value.substring(selectionStart));
            }

            formElement.focus();
            formElement[0].setSelectionRange(cursorPosition, cursorPosition);
        } else {
            selectionRange = formElement.getSelectionRange();
            selectionString = formElement.session.getTextRange(selectionRange);

            if (tagObject.selfClosing) {
                insertString = "<" + tagObject.name + attributeString + "/>";
            } else {
                insertString = "<" + tagObject.name + attributeString + ">" + selectionString +
                "</" + tagObject.name + ">";
            }

            formElement.session.getDocument().remove(selectionRange);
            formElement.insert(insertString);
            formElement.focus();
        }
    },
    init = function (formElement, tagObject, selectionStart, selectionEnd) {
        jQuery(".tab-dialog")
        .on("shown.bs.modal", function () {
            InputPanel.setLocked(true);
            jQuery(".modal-body :input", this).first().focus();
        })
        .on("hide.bs.modal", function () {
            var that = this;

            setTimeout(function () {
                jQuery(that).remove();
            }, 500);

            formElement.focus();
            InputPanel.setLocked(false);
        })
        .on("submit", "form", function (event) {
            event.preventDefault();
            insertTag(formElement, tagObject, selectionStart, selectionEnd);
            jQuery(".tab-dialog").modal("hide");
        });
    },
    create = function (tagObject) {
        var body = jQuery("body"), tabDialogTemplate = jQuery(".tab-dialog-template");

        if (document.createElement("template").content !== undefined) {
            body.append(tabDialogTemplate[0].content.cloneNode(true));
        } else {
            body.append(tabDialogTemplate.find(".tab-dialog").clone());
        }

        addTitle(tagObject);
        addAttributes(tagObject);
    },
    show = function (formElement, tagObject, selectionStart, selectionEnd) {
        create(tagObject);
        init(formElement, tagObject, selectionStart, selectionEnd);
        jQuery(".tab-dialog").modal("show");
    };

    return {
        show : show
    };
}());

jQuery(document).ready(function () {
    "use strict";
    InputPanel.init();
    InputPanel.focus();
});
