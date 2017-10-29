require(['jquery'], function($) {
    $(document).ready(function() {
        prev = $('th.project-key');
        $('<th class="project-field" scope="col">Test</th>').insertAfter(prev);
    });
});
