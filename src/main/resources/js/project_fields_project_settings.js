require(['jquery'], function($) {
    setTimeout(function() {
        disabledFields = $('input[readonly]');
        disabledFields.attr('title', 'Only system administrators can edit this field.');
        disabledFields.tooltip();
    }, 1000);
});
