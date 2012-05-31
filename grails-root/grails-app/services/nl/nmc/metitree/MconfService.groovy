package nl.nmc.metitree

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class MconfService {

    static transactional = true

    String fromLabel(String label) {
		
		def value = ""
		
		try {
			def valueFromDB = Mconf.findByLabel(label)
					
			if (valueFromDB){
				value = valueFromDB.value
			} else {
				try {
					value = ConfigurationHolder.config.toProperties().getProperty(label)
				} catch(e){
					log.error "Label ${label} not found in DB not Config"
					log.error e
				}
			}
		} catch(e){
			log.error e
		}
		
		return value
    }
}
