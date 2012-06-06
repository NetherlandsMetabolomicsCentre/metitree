#!/usr/bin/env python
import oasa
import sys


def convert_ichi_to_key(inchi):
    """Convert inchi to inchi key."""
    #inchi = 'InChI=1/C2H6O/c1-2-3/h3H,2H2,1H3'
    return oasa.inchi_key.key_from_inchi(inchi)

if __name__ == "__main__":
    print convert_ichi_to_key(sys.argv[1])
