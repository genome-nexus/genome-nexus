/*
 * Copyright (c) 2016 Memorial Sloan-Kettering Cancer Center.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF MERCHANTABILITY OR FITNESS
 * FOR A PARTICULAR PURPOSE. The software and documentation provided hereunder
 * is on an "as is" basis, and Memorial Sloan-Kettering Cancer Center has no
 * obligations to provide maintenance, support, updates, enhancements or
 * modifications. In no event shall Memorial Sloan-Kettering Cancer Center be
 * liable to any party for direct, indirect, special, incidental or
 * consequential damages, including lost profits, arising out of the use of this
 * software and its documentation, even if Memorial Sloan-Kettering Cancer
 * Center has been advised of the possibility of such damage.
 */

/*
 * This file is part of cBioPortal Cancer Hotspots.
 *
 * cBioPortal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * @author Selcuk Onur Sumer
 */
function GenomeNexus(options)
{
    var _hotspotProxy = null;
    var _clusterProxy = null;
    var _metadataProxy = null;

    var _defaultOpts = {
        pageLoaderDelay: 50,
        tableLoaderDelay: 500,
        appContent: "#app_content",
        appTemplateId: "main_page",
        pageContent: "#page_content",
        pageLoader: "#page_loader",
        mainView: "#main_view",
        mainContent: "#main_content",
        homePage: "#home",
        homeTemplateId: "home_page",
        aboutPage: "#about",
        downloadPage: "#download",
        downloadTemplateId: "download_page",
        aboutTemplateId: "about_page",
        content: {
            app: {
                tagline: "A resource for statistically significant mutations in cancer",
                title: "Genome Nexus",
                logoStyle: "hotspot-fire"
            },
            home: {
                pageInfo: "Hello World!" // _.template($("#default_mutation_info").html())()
            },
            download: {
                content:[{
                    links: [
                        {href: 'href="files/hotspots.xls"',
                            text: "Hotspot Results V1"},
                        {href: 'href="https://github.com/taylor-lab/hotspots/blob/master/LINK_TO_MUTATIONAL_DATA"',
                            text: "V1 Mutational Data (MAF)"}
                    ],
                    info: "" // _.template($("#hotspots_v1_info").html())()
                }]
            }
        }
    };

    // merge options with default options to use defaults for missing values
    var _options = jQuery.extend(true, {}, _defaultOpts, options);

    function switchContent(routeFn, params)
    {
        // hide everything first
        $(_options.pageContent).children().hide();

        // show the loader image before starting the transition
        $(_options.pageLoader).show();

        setTimeout(function() {
            routeFn(params);

            // hide the loader image after transition completed
            $(_options.pageLoader).hide();
        }, _options.pageLoaderDelay);
    }

    function home(params)
    {
        // init section if not initialized yet
        if (!$(_options.homePage).length)
        {
            var templateFn = _.template($("#" + _options.homeTemplateId).html());
            $(_options.pageContent).append(templateFn(params));
        }

        $(_options.homePage).show();
    }

    function init()
    {
        // init router
        var router = new Router({
            '/home': function() {
                switchContent(home, _options.content.home);
            }
        });

        router.configure({notfound: function() {
            // TODO switch to the not found page! (a static error page)
            //switchContent(unknown);
            $(_options.pageContent).children().hide();
        }});

        // init static content
        var templateFn = _.template($("#" + _options.appTemplateId).html());
        $(_options.appContent).append(templateFn(_options.content.app));

        // load home page content initially
        router.init("/home");
    }

    this.init = init;
}
