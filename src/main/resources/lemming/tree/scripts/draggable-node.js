var DraggableNode, DropzoneAnimation;

DraggableNode = (function () {
    var init, onDragenter, onDragover, onDragleave, onDragstart, onDragend, onDrop;

    init = function () {
        // prevent redirect on dragover or drop
        jQuery(document).on("dragover drop", ":not(.dropzone)", function (event) {
            event.preventDefault();
        });
    };

    onDragenter = function (dropzoneId, nodeId) {
        jQuery(document).on("dragenter", "#" + dropzoneId, function (event) {
            var active, node = jQuery("#" + nodeId);

            // increase number of "active" dropzones
            active = node.data("active") || 0;
            node.data("active", active + 1);

            node.not(".node-dragging").addClass("node-dragover");
            jQuery(this).addClass("dropzone-dragover");
            event.preventDefault();
        });
    };

    onDragover = function (dropzoneId) {
        jQuery(document).on("dragover", "#" + dropzoneId, function (event) {
            event.originalEvent.dataTransfer.dropEffect = "move";
            event.preventDefault();
        });
    };

    onDragleave = function (dropzoneId, nodeId) {
        jQuery(document).on("dragleave", "#" + dropzoneId, function (event) {
            var active, node = jQuery("#" + nodeId);

            // decrease number of "active" dropzones
            active = node.data("active") - 1;
            node.data("active", active);

            if (active < 1) {
                node.removeClass("node-dragover");
            }

            jQuery(this).removeClass("dropzone-dragover");
            event.preventDefault();
        });
    };

    onDragstart = function (dropzoneId, nodeId, treeId, nodeRelativePath) {
        jQuery(document).on("dragstart", "#" + dropzoneId, function (event) {
            var tree = jQuery(this).closest("#" + treeId);

            tree.attr("data-between", "true");
            jQuery("#" + nodeId).addClass("node-dragging");
            event.originalEvent.dataTransfer.clearData();
            event.originalEvent.dataTransfer.setData("text/plain", nodeRelativePath);
            event.originalEvent.dataTransfer.effectAllowed = "move";
        });
    };

    onDragend = function (dropzoneId, nodeId, treeId) {
        jQuery(document).on("dragend", "#" + dropzoneId, function (event) {
            var tree = jQuery(this).closest("#" + treeId);

            tree.removeAttr("data-between");
            // reset dropzones and nodes
            jQuery("#" + nodeId).removeClass("node-dragging");
            tree.find(".dropzone-dragover").removeClass("dropzone-dragover");
            tree.find(".node-dragover").removeClass("node-dragover").data("active", 0);
            event.preventDefault();
        });
    };

    onDrop = function (dropzoneId) {
        jQuery(document).on("drop", "#" + dropzoneId, function (event) {
            event.preventDefault();
        });
    };

    return {
        init: init,
        onDragenter: onDragenter,
        onDragover: onDragover,
        onDragleave: onDragleave,
        onDragstart: onDragstart,
        onDragend: onDragend,
        onDrop: onDrop
    };
})();

DropzoneAnimation = (function () {
    var animate, setup, init;

    animate = function (canvas) {
        var context = canvas.getContext("2d"), parent = jQuery(canvas).parent(), canvasHeight, canvasWidth, color,
        lineWidth = 2, lineSize = 5, lineSpace = 5, inset = 1, offset = 0;

        function adjustDimensions() {
            jQuery(canvas).attr("height", parent.innerHeight());
            // always substract 1 since jQuery delivers rounded values
            jQuery(canvas).attr("width", parent.innerWidth() - 1);
        }

        function draw() {
            adjustDimensions();
            canvasHeight = context.canvas.clientHeight;
            canvasWidth = context.canvas.clientWidth;
            color = getComputedStyle(canvas).getPropertyValue("color");

            context.clearRect(0, 0, canvasWidth, canvasHeight);
            context.lineWidth = lineWidth;
            context.setLineDash([lineSize, lineSpace]);
            context.lineDashOffset = -offset;
            context.strokeStyle = color;
            context.strokeRect(inset, inset, canvasWidth - 2 * inset, canvasHeight - 2 * inset);
        }

        function march() {
            offset += 1;

            if (offset > lineSize + lineSpace) {
                offset = 0;
            }

            draw();
            setTimeout(march, 40);
        }

        march();
    };

    setup = function (dropzone) {
        var canvas = jQuery("<canvas></canvas>");

        dropzone.append(canvas);
        animate(canvas[0]);
    };

    init = function () {
        var target = jQuery("body")[0],
        observer = new MutationObserver(function (mutations) {
            mutations.forEach(function (mutation) {
                if (mutation.addedNodes.length) {
                    mutation.addedNodes.forEach(function (addedNode) {
                        if (jQuery(addedNode).hasClass("branch")) {
                            jQuery(addedNode).find(".dropzone-bottom, .dropzone-top").each(function () {
                                setup(jQuery(this));
                            });
                        }
                    });
                }
            });
        });

        observer.observe(target, { attributes: false, characterData: false, childList: true, subtree: true });
        jQuery(".dropzone-bottom, .dropzone-top").each(function () {
            setup(jQuery(this));
        });
    };

    return {
        init: init
    };
})();

jQuery(document).ready(function () {
    DraggableNode.init();
    DropzoneAnimation.init();
});
