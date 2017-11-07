require(['jquery'], function($) {
    $(function() {
        resequence = function() {
            $('table#custom-fields input[name^="seq_"]').each(function(i, e) {
                $(e).val('' + i);
                console.log('Set ' + e + ' to ' + i);
            });
        };

        $('table#custom-fields tbody').sortable({
            update: resequence,
        });

        $('table#custom-fields button#add-field').click(function() {
            newID = 'new' + $('table#custom-fields tr.new-field').length;
            newRow = $(
                '<tr class="new-field">' +
                '<input type="hidden" name="add" value="' + newID + '">' +
                '<input type="hidden" name="seq_' + newID + '" value="0">' +
                '<td><input class="text" type="text" name="name_' + newID + '"></td>' +
                '<td><textarea class="textarea long-field expanding expanding-init" name="description_' + newID + '" rows="4"></textarea></td>' +
                '<td><textarea class="textarea long-field expanding expanding-init" name="options_' + newID + '" rows="4"></textarea></td>' +
                '<td><aui-toggle name="visible_' + newID + '" checked></aui-toggle></td>' +
                '<td><aui-toggle name="editable_' + newID + '" checked></aui-toggle></td>' +
                '<td><button type="button" class="aui-button remove-field"><span class="aui-icon aui-icon-small aui-iconfont-remove"></span></button></td>' +
                '</tr>'
            );
            $(this).parents('tr').before(newRow);
            resequence();
            $('input', newRow).focus();
            $('button.remove-field', newRow).click(function() {
                $(this).parents('tr').remove();
                resequence();
            });
        });

        $('table#custom-fields button.remove-field').click(function() {
            id = $(this).data('field-id');
            newRow = $('<tr/>');
            newRow.append($('<td/>').attr('colspan', '6').attr('id', 'row_' + id));
            newRow.append($('<input/>').attr('type', 'hidden').attr('name', 'delete').attr('value', id));
            $(this).parents('tr').replaceWith(newRow);
            resequence();

            project_count = parseInt($(this).data('project-count'), 10);
            AJS.messages.warning('#row_' + id, {
                closeable: false,
                title: 'Removing field ' + AJS.escapeHtml($(this).data('field-name')),
                body: project_count
                    ? 'This field is used by ' + project_count + (project_count == 1 ? ' project' : ' projects') + ' &mdash; click Save to finish removing this field and the associated project ' + (project_count == 1 ? 'value.' : 'values.')
                    : 'Click Save to finish removing this field.',
            });
        });
    });
});
