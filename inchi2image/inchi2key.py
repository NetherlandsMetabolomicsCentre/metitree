#!/usr/bin/env python
import oasa
import sys

__author__ = "Tim te Beek"
__contact__ = "brs@nbic.nl"
__copyright__ = "Copyright 2012, Netherlands Bioinformatics Centre"


def convert_ichi_to_key(inchi):
    """Convert inchi to inchi key."""
    #inchi = 'InChI=1/C2H6O/c1-2-3/h3H,2H2,1H3'
    return oasa.inchi_key.key_from_inchi(inchi)

if __name__ == "__main__":
    print convert_ichi_to_key(sys.argv[1])
