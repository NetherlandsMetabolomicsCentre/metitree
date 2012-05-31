package nl.nmc.metitree

import grails.converters.*;

class ProcessSettings {
		
	Member		member
	String		settingsHash
	String		settings
	Date		dateCreated
	Date		lastUpdated
	
    static constraints = {
    }
	
	static transients = [ 'settingsmap', 'shortname' ]
	
	Map getSettingsmap() {
		
		def smap = [:]
		
		def jsonArray = JSON.parse(this.settings)
		jsonArray.each {
			smap[it.key] = it.value as String // make sure we only have Strings as setting values 
		}
				
		return smap.sort { a,b -> a.key <=> b.key } // return sorted by key
	}
	
	String getShortname() {
		
		def shortname = ""		
		
		//returns a short name for the settings
		this.settingsmap.each {
			shortname += "${it.key as String}:${it.value as String} " // make sure we only have Strings as setting values 
		}
		
		//shorten Accuracy to A
		shortname = shortname.replace("accuracy-L","MS-")
		
		return shortname
	}
}
