import os.path
import unittest


class TestClass(unittest.TestCase):
    def setUp(self):
        self.ethanol = 'InChI=1/C2H6O/c1-2-3/h3H,2H2,1H3'
        self.complex = 'InChI=1S/C33H40O19/c1-10-19(36)23(40)26(43)31(47-10)46-9-17-21(38)25(42)28(45)33(51-17)52-30-22(39)18-15(35)7-14(49-32-27(44)24(41)20(37)11(2)48-32)8-16(18)50-29(30)12-3-5-13(34)6-4-12/h3-8,10-11,17,19-21,23-28,31-38,40-45H,9H2,1-2H3/t10-,11-,17+,19-,20-,21-,23+,24+,25-,26+,27+,28+,31+,32-,33-/m0/s1'
        self.subsup = 'InChI=1/C2H5NS/c3-1-2-4/h1,3-4H,2H2/p+1'

    def testInchi2Image(self):
        '''Simple test to create images in both formats.'''
        from inchi2image.inchi2image import convert_ichi_to_image

        #Ethanol
        image = convert_ichi_to_image(self.ethanol)
        self.assertTrue(900 < os.path.getsize(image))
        os.remove(image)
        image = convert_ichi_to_image(self.ethanol, contenttype='svg')
        self.assertTrue(3000 < os.path.getsize(image))
        os.remove(image)

        #Complex
        image = convert_ichi_to_image(self.complex)
        self.assertTrue(900 < os.path.getsize(image))
        os.remove(image)
        image = convert_ichi_to_image(self.complex, contenttype='svg')
        self.assertTrue(3000 < os.path.getsize(image))
        os.remove(image)

        #Superscript and subscript (would have to be inspected)
        image = convert_ichi_to_image(self.subsup)
        self.assertTrue(900 < os.path.getsize(image))
        os.remove(image)
        image = convert_ichi_to_image(self.subsup, contenttype='svg')
        self.assertTrue(3000 < os.path.getsize(image))
        os.remove(image)

    def testInchi2Key(self):
        '''Simple test to convert full inchis into inchikeys.'''
        from inchi2image.inchi2key import convert_ichi_to_key
        #Simple ethanol
        self.assertEqual('LFQSCWFLJHTTHZ-UHFFFAOYAB', convert_ichi_to_key(self.ethanol))
        #More complex
        self.assertEqual('PEFASEPMJYRQBW-HKWQTAEVSA-N', convert_ichi_to_key(self.complex))

    def testServer(self):
        '''Test web.py application works as expected.'''
        from inchi2image.webapplication import URLS
        import web
        app = web.application(URLS, globals())
        response = app.request('/')
        self.assertEqual('404 Not Found', response.status)
        print app.request('/svg/' + self.ethanol)

if __name__ == "__main__":
    #import sys;sys.argv = ['', 'Test.testName']
    unittest.main()
