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
 * Data utilities.
 *
 * @author Selcuk Onur Sumer
 */
function DataUtils(columns)
{
    var _columns = columns;

    function stringify(data, delimiter, separator)
    {
        delimiter = delimiter || "\t";
        separator = separator || "\n";

        var content = [];
        var header = [];

        _.each(_columns, function(column) {
           header.push(column.title);
        });

        content.push(header);

        _.each(data, function(rowData) {
            var rowContent = [];

            _.each(_columns, function(column){
                var value;

                if (_.isFunction(column.data)) {
                    value = column.data(rowData);
                }
                else {
                    value = rowData[column.data];
                }

                if (_.isObject(value))
                {
                    value = processMap(value);
                }

                rowContent.push(value);
            });

            content.push(rowContent);
        });

        _.each(content, function(row, idx) {
            content[idx] = row.join(delimiter);
        });

        return content.join(separator);
    }

    function processMap(map, delimiter, separator)
    {
        delimiter = delimiter || ":";
        separator = separator || "|";

        var result = [];
        var pairs = _.pairs(map).sort(function(a, b) {
            if (a[1] === b[1]) {
                // sort alphabetically (a-z)
                if (a[0] > b[0])
                    return 1;
                else
                    return -1;
            }
            else {
                // sort descending
                return (b[1] - a[1]) || -1;
            }
        });

        _.each(pairs, function(pair) {
            result.push(pair[0] + delimiter + pair[1]);
        });

        return result.join(separator);
    }

    this.stringify = stringify;
}
