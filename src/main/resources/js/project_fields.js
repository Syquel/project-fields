require(['jquery'], function($) {
    $(function() {
//        prev = $('th.project-key');
//        $('<th class="project-field" scope="col">Test</th>').insertAfter(prev);
        data = WRM.data.claim('com.mrozekma.atlassian.bitbucket.projectFields:project-fields-project-list-resource.fields');
        console.log(data);

        cell = $('#projects-table th.project-key');
        $.each(data['fields'], function(_, name) {
            cell = $("<th/>").addClass('project-custom-field').attr('scope', 'col').text(name).insertAfter(cell);
        });

        $('#projects-table td.project-key').each(function() {
            cell = $(this);
            projectKey = $(this).text();
            $.each(data['fields'], function(_, name) {
                newCell = $("<td/>").addClass('project-key');
                vals = data['projects'][projectKey];
                if(vals) {
                    val = vals[name];
                    if(val) {
                        newCell.text(val);
                    }
                }
                cell = newCell.insertAfter(cell);
            });
        });
    });
});
