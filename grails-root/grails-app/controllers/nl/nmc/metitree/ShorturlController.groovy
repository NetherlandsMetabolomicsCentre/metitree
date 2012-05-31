package nl.nmc.metitree

class ShorturlController {
	
	def urlService

    def index = { 
		if (params.urlcode){
			
			def longUrl = urlService.longUrlFromShort("${params.urlcode}" as String)
			
			if (longUrl != ""){
				response.status = 200 //Success
				render new URL(longUrl).text
			} else {
				//mmm invalid URL
				response.status = 404 //Not Found
				render "URL expired."
			}
		} else {
		
			//mmm no URL found...
			response.status = 404 //Not Found
	        render "URL not found."
		}
	}
}
