#!/usr/bin/env python
"""Web.py server."""

from inchi2image import convert_ichi_to_image
import oasa
import os
import web

URLS = ('/(svg|png)/(InChI=.*)', 'Server')


class Server(object):
    """Class to serve svg or png images of InChI structures."""

    def __init__(self):
        self.typeheaders = {'png': 'image/png', 'svg': 'image/svg+xml'}

    def GET(self, contenttype, inchi):
        """Get image for inchikey"""
        try:
            #Create image from  Inchi with OASA
            imagefile = convert_ichi_to_image(inchi, contenttype)
            #Response is the binary content of the created file
            response = open(imagefile, 'rb').read()
            #Clean up the file
            os.remove(imagefile)
        except (oasa.oasa_exceptions.oasa_error, IOError) as err:
            #Handle errors that might have been raised in the above code
            if contenttype == 'png':
                #Return the 1x1 png image that is expected in the JavaScript code
                #as error handling proved to difficult in JavaScript by MKo
                web.header('Content-Type', 'image/png')
                return open('/var/www/inchi2image/python/1x1.png', 'rb').read()
            else:
                raise web.webapi.internalerror(err)

        #Set correct contenttype based on contenttype
        web.header('Content-Type', self.typeheaders[contenttype])
        return response

if __name__ == "__main__":
    #web.config.debug = False
    #web.wsgi.runwsgi = lambda func, addr = None: web.wsgi.runfcgi(func, addr)
    APP = web.application(URLS, globals())
    APP.run()

#To run this app through Apache mod_wsgi enable the following two lines
APP = web.application(URLS, globals(), autoreload=False)
application = APP.wsgifunc()
