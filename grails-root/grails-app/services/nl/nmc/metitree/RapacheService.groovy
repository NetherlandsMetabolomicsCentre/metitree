package nl.nmc.metitree

import org.springframework.context.*

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class RapacheService {

	static transactional = true
 
	def urlService
	def config = ConfigurationHolder.config

	def callXcms(mzxmlFile = null, settings = [:]) {

		if (mzxmlFile == null){
			return ""
		}

		def msnfilePathArray = mzxmlFile.canonicalPath.toString().tokenize("/")
		def encodedMsnfileLink = (urlService.shorlUrlFromLong("${config.grails.serverURL}/files/usergroup/${msnfilePathArray[-4]}/directories/${msnfilePathArray[-2]}/${msnfilePathArray[-1]}")).encodeAsURL() as String
		def getUrl = "${config.grails.rapache.url}/mzxml2tree?"
			getUrl += "snthresh=${settings['snthresh']}"
			getUrl += "&mzgap=${settings['mzgap']}"
			getUrl += "&urlMzxml=${encodedMsnfileLink}" // this is the new way to provide a URI (URLencoded)
		return new URL(getUrl).text
    }
	
	def callRgraphviz(csvFile = null, pdfFile = null){
		if (csvFile == null || pdfFile == null){
			return false
		}
	
		def csvfilePathArray = csvFile.location.tokenize("/")
		def encodedcsvfileLink = (urlService.shorlUrlFromLong("${config.grails.serverURL}/files/usergroup/${csvfilePathArray[-4]}/directories/${csvfilePathArray[-2]}/${csvfilePathArray[-1]}")).encodeAsURL() as String

		def getUrl = "${config.grails.rapache.url}/csv2pdfJob?"
			getUrl += "urlCsv=${encodedcsvfileLink}"
			getUrl += "&title=${csvFile?.compound()?.inchi ?: ''}"		
			getUrl += "&compoundname=${csvFile?.compound()?.name ?: ''}"
			
		def pdfLocation = ("${new URL(getUrl).text}" as String).replaceAll("\n", "").replaceAll(" ", "")
		
		def tempPDF = new File(pdfLocation)

		try {
			new File("${pdfFile.location}") << tempPDF?.text ?: ''
			tempPDF.setWritable(true, false)
			tempPDF.delete() //remove the tempPDF file
		} catch (e) {
			log.error e
		}
		return true
		
		
	}
}
