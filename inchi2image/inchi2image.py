#!/usr/bin/env python
import oasa
import sys
import tempfile


def convert_ichi_to_image(inchi, contenttype='png'):
    """Convert a inchi to an image in both svg and png."""
    #Create structure from inchi using OASA
    mol = oasa.inchi.text_to_mol(str(inchi), calc_coords=1, include_hydrogens=False)
    mol.normalize_bond_length(30)
    mol.remove_unimportant_hydrogens()
    cairo_object = oasa.cairo_out.cairo_out(color_bonds=True, color_atoms=True)
    cairo_object.show_hydrogens_on_hetero = True
    cairo_object.font_size = 20
    mols = list(mol.get_disconnected_subgraphs())

    #Create image from structure
    imagefile = tempfile.mkstemp(suffix='.' + contenttype, prefix='inchi2image_')[1]
    cairo_object.mols_to_cairo(mols, imagefile, format=contenttype)
    return imagefile


if __name__ == "__main__":
    convert_ichi_to_image(sys.argv[1])
