#!/usr/bin/env python
"""Web.py server."""

import oasa
import os
import web
import tempfile

URLS = ('/(svg|png)/(InChI=.*)', 'Server')

class Server(object):
    """Class to serve svg or png images of InChI structures."""

    typeheaders = {'png':'image/png', 'svg':'image/svg+xml'}

    def GET(self, contenttype, inchi):
        """Get image for inchikey"""

        try:
            #Create structure from Inchi in OASA
	    mol = oasa.inchi.text_to_mol(str(inchi), calc_coords = 1, include_hydrogens = False)
            mol.normalize_bond_length(30)
            mol.remove_unimportant_hydrogens()
            cairo_object = oasa.cairo_out.cairo_out(color_bonds = True, color_atoms = True)
            cairo_object.show_hydrogens_on_hetero = True
            cairo_object.font_size = 20
            mols = list(mol.get_disconnected_subgraphs())

            #Create image from structure
            imagefile = tempfile.mkstemp(suffix='.'+contenttype, prefix='inchi2image_')[1]
            cairo_object.mols_to_cairo(mols, imagefile, format = contenttype)

            #Response is the binary content of the created file
            response = open(imagefile, 'rb').read()
            os.remove(imagefile)
        except (oasa.oasa_exceptions.oasa_error, IOError) as err:
			web.header('Content-Type', 'image/png')
            return open('not-found.png', 'rb').read()

        #Set correct contenttype based on contenttype
        web.header('Content-Type', self.typeheaders[contenttype])
        return response

if __name__ == "__main__":
    APP = web.application(URLS, globals())
    APP.run()

