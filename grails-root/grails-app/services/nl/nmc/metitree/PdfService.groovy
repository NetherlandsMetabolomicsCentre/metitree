package nl.nmc.metitree

import grails.converters.*

import org.springframework.context.ApplicationContext
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class PdfService {
	
	ApplicationContext applicationContext
	
	def rapacheService
	
	def config = ConfigurationHolder.config

    static transactional = true

    def pdfFromCSV(csvFile = null, pdfFile = null) {
		
		if (csvFile == null || pdfFile == null){
			return false
		}
		
		rapacheService.callRgraphviz(csvFile, pdfFile)
		
		return true
    }
}
