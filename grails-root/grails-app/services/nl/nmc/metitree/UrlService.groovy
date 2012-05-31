package nl.nmc.metitree

import org.springframework.context.*
import org.codehaus.groovy.grails.commons.ConfigurationHolder

import nl.nmc.metitree.Shorturl

class UrlService {

	def mconfService
	
    static transactional = false

    String shorlUrlFromLong(url = "") {
		def shortUrl = new Shorturl()
			shortUrl.longurl = url as String
			shortUrl.urlcode = UUID.randomUUID()
			shortUrl.save(flush: true)
		
		def newUrl = "${mconfService.fromLabel('grails.serverURL')}/shorturl/index?urlcode=${shortUrl.urlcode}"
		
		return newUrl
    }
	
	String longUrlFromShort(urlcode = ""){
		
		//fetch shortUrl object
		def shortUrl = Shorturl.findByUrlcode(urlcode)
		
		//if found and when valid return the url
		if (shortUrl?.active){
			return shortUrl.longurl as String
		} else {
			return "" // if not found or not valid return an empty string
		}
	}
}
