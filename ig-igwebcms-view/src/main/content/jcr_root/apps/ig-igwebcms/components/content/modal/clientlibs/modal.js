;(function($){
    "use strict";

    $.fn.modal = function(opts) {
        return this.each(function() {
            var $modal = $(this);
            var $background = $modal.find(".background");
            var $modalBox = $modal.find('.modal-box');

            function showModal() {
                if(!$modalBox.is(":visible")) {
                    $modalBox.css({'margin-top': -$modalBox.height() / 2 }); // Center the modals on the page
                    $background.fadeIn("normal");
                    $modalBox.find('.section').trigger("show");
                    $modalBox.fadeIn("normal");
                    $(document).bind("keyup", keyUpHandler);
                    return false;
                }
            }

            function dismissModal() {
                if($modalBox.is(":visible")) {
                    $background.fadeOut("normal");
                    $modalBox.fadeOut("normal");
                    $(document).unbind("keyup");
                }
            }

            function keyUpHandler(evt) {
                if (evt.which == 27) {
                    return dismissModal();
                }
            }

            // events to close the popup
            $modal.find(".close").click(function() { $modal.trigger("closeModal"); });
            $modal.find(".background").click(function() { $modal.trigger("closeModal"); });

            $modal.on("showModal", function() { showModal(); });
            $modal.on("closeModal", dismissModal)
        });
    };

})(jQuery);
