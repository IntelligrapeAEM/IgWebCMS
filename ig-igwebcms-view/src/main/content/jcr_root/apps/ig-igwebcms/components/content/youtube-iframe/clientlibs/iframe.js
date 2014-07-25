;(function($){
    "use strict";

    $.fn.ytIframeModal = function(opts) {
        var options = $.extend({
            src: ''
        }, opts);

        $('<iframe />').attr('src', options['src']).css('height', '100%').css('width', '100%').appendTo($(this).find('.modal_content'));
        return this.each(function() {
            $(this).on("closeModal", function() {
                $(this).find('iframe').remove();
            });
        });
    };

})(jQuery);
