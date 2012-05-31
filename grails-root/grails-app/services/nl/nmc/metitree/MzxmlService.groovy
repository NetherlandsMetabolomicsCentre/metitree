package nl.nmc.metitree

import grails.converters.*

import org.springframework.context.ApplicationContext
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class MzxmlService {
	
	ApplicationContext applicationContext
	
	def rapacheService
	
	def config = ConfigurationHolder.config

    static transactional = true

    def convertToTabularFormat(mzxmlFile = null, tabularFile = null, settings = [:]) {
		
		if (mzxmlFile == null || tabularFile == null){
			return false
		}
				
		tabularFile << rapacheService.callXcms(mzxmlFile, settings)
		
		return true
    }
}
