require(['jquery'], function($) {
    $(function() {
        data = WRM.data.claim('com.mrozekma.atlassian.bitbucket.projectFields:project-fields-project-list-resource.fields');

        cell = $('#projects-table th.project-key');
        $.each(data['fields'], function(_, field) {
            cell = $("<th/>").addClass('project-custom-field').attr('scope', 'col').attr('title', field['description']).text(field['name']).tooltip().insertAfter(cell);
        });

        $('#projects-table td.project-key').each(function() {
            cell = $(this);
            projectKey = $(this).text();
            $.each(data['fields'], function(_, field) {
                newCell = $("<td/>").addClass('project-custom-field');
                vals = data['projects'][projectKey];
                if(vals) {
                    val = vals[field['name']];
                    if(val) {
                        newCell.text(val);
                    }
                }
                cell = newCell.insertAfter(cell);
            });
        });
    });
});
