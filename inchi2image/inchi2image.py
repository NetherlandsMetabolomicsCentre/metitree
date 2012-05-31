import oasa
import sys

def convert_ichi_to_image(inchi):
	#InChI=1S/C33H40O19/c1-10-19(36)23(40)26(43)31(47-10)46-9-17-21(38)25(42)28(45)33(51-17)52-30-22(39)18-15(35)7-14(49-32-27(44)24(41)20(37)11(2)48-32)8-16(18)50-29(30)12-3-5-13(34)6-4-12/h3-8,10-11,17,19-21,23-28,31-38,40-45H,9H2,1-2H3/t10-,11-,17+,19-,20-,21-,23+,24+,25-,26+,27+,28+,31+,32-,33-/m0/s1
	mol = oasa.inchi.text_to_mol( inchi, calc_coords=1, include_hydrogens=False)
	mol.normalize_bond_length( 30)
	mol.remove_unimportant_hydrogens()
	c = oasa.cairo_out.cairo_out( color_bonds=True, color_atoms=True)
	c.show_hydrogens_on_hetero = True
	c.font_size = 20
	mols = list( mol.get_disconnected_subgraphs())
	c.mols_to_cairo( mols, "image.png")
	c.mols_to_cairo( mols, "image.svg", format="svg")

if __name__ == "__main__":
	convert_ichi_to_image(sys.argv[1])
