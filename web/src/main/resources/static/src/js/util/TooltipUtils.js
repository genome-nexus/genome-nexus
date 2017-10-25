var TooltipUtils = (function() {
    function defaultTooltipOpts()
    {
        return {
            content: {text: 'NA'},
            show: {event: 'mouseover'},
            hide: {fixed: true, delay: 100, event: 'mouseout'},
            style: {classes: 'cancer-hotspots-tooltip qtip-shadow qtip-light qtip-rounded'},
            position: {my:'top left', at:'bottom right', viewport: $(window)}
        };
    }

    function tooltipOptions(colData, viewOpts)
    {
        var tooltipOpts = defaultTooltipOpts();

        // this will overwrite the default content
        tooltipOpts.events = {
            render: function(event, api) {
                var tableData = [];

                var defaultViewOpts = {
                    el: $(this).find('.qtip-content'),
                    colData: colData,
                    data: tableData
                };

                // only update table data in case no data provided within view options!
                if (_.isEmpty(viewOpts) ||
                    _.isEmpty(viewOpts.data))
                {
                    var map = colData.composition || colData;

                    _.each(_.pairs(map), function(pair) {
                        tableData.push({type: pair[0], count: pair[1]});
                    });
                }

                var opts = jQuery.extend(true, {}, defaultViewOpts, viewOpts);
                var tableView = new CompositionView(opts);

                tableView.render();

                // this is a workaround for the misaligned table headers
                // due to the scroll bar feature
                setTimeout(function() {
                    tableView.getDataTable().columns.adjust();
                }, 0);
            }
        };

        return tooltipOpts;
    }

    return {
        defaultTooltipOpts: defaultTooltipOpts,
        tooltipOptions: tooltipOptions
    };
})();
