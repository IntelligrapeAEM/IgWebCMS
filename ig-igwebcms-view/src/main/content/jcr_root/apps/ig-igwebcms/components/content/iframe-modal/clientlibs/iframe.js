;(function($){
    "use strict";

    $.fn.iframeModal = function(opts) {
        var options = $.extend({
            src: ''
        }, opts);

        return this.each(function() {
            $(this).on("showModal", function() {
                $('<iframe />').attr('src', options['src']).css('height', '100%').css('width', '100%').appendTo($(this).find('.modal_content'));
            });
            $(this).on("closeModal", function() {
                $(this).find('iframe').remove();
            });
        });
    };

})(jQuery);
