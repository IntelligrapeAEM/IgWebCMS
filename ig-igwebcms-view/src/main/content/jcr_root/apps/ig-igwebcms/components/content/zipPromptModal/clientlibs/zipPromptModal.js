;
(function ($) {
    "use strict";

    $.fn.zippromptmodal = function () {
        return this.each(function () {
            //Bind event on which you want to display modal
            $(this).trigger('showModal');
        });
    }
})(jQuery);
