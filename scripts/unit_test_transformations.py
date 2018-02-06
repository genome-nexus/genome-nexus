#!/usr/bin/env python2.7

"""
Copyright (c) 2018 The Hyve B.V.
This code is licensed under the GNU Affero General Public License (AGPL),
version 3, or (at your option) any later version.
"""

import unittest
import difflib
import os
import transform_gff_to_tsv_for_exon_info_from_ensembl

class TransformTestCase(unittest.TestCase):

    """Superclass for testcases that test the transformation steps.
    """

    def assertFileGenerated(self, tmp_file_name, expected_file_name):
        """Assert that a file has been generated with the expected contents."""
        self.assertTrue(os.path.exists(tmp_file_name))
        with open(tmp_file_name, 'rU') as out_file, \
            open(expected_file_name, 'rU') as ref_file:
            base_filename = os.path.basename(tmp_file_name)
            base_input = os.path.basename(expected_file_name)
            diff_result = difflib.context_diff(
                ref_file.readlines(),
                out_file.readlines(),
                fromfile='Expected {}'.format(base_input),
                tofile='Generated {}'.format(base_filename))
        diff_line_list = list(diff_result)
        self.assertEqual(diff_line_list, [], msg='\n' + ''.join(diff_line_list))
        # remove temp file if all is fine:
        try:
            os.remove(tmp_file_name)
        except WindowsError:
            # ignore this Windows specific error...probably happens because of virus scanners scanning the temp file...
            pass

    def test_pfam_transformation_step(self):
        """Test pfam TSV to internal data structure transformation"""
        # TODO

    def test_exon_transformation_step(self):
        """Test exon gff to internal data structure transformation"""
        # Build up arguments and run
        out_file_name = 'test_files/exon_information/ensembl_exon_info.txt~'
        gff_input_file_name = 'test_files/exon_information/sub_Homo_Sapiens.gff3.gz'
        transform_gff_to_tsv_for_exon_info_from_ensembl.transform_gff_to_tsv(gff_input_file_name, out_file_name)
        self.assertFileGenerated(out_file_name,'test_files/exon_information/ensembl_exon_info.txt')
