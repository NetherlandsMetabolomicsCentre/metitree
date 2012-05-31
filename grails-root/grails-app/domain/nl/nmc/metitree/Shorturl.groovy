package nl.nmc.metitree

import groovy.time.*

class Shorturl {
	
	String	longurl
	String	urlcode
	Long	systemTime // in milliseconds

	def beforeInsert() {
		systemTime = System.currentTimeMillis()
	}
	
    static constraints = {
		systemTime(nullable: true)
    }
	
	static transients = ['active']
	
	boolean getActive(){

		//TODO: move to configuration
		def ttl = 3600 // 1 hour
		
		//calculate duration
		def duration = (System.currentTimeMillis() - systemTime) / 1000
		
		//check if the duration is less then the ttl
		if (duration <= ttl){
			return true
		}
		
		return false
	}
}
