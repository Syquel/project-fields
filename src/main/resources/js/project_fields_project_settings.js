require(['jquery'], function($) {
    setTimeout(function() {
        $('form #custom-fields select').auiSelect2();
        disabledFields = $('input[readonly]');
        disabledFields.attr('title', 'Only system administrators can edit this field.');
        disabledFields.tooltip();
    }, 1);
});
