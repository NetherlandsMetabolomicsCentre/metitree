package nl.nmc.metitree

import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.openscience.cdk.inchi.InChIGeneratorFactory
import org.openscience.cdk.DefaultChemObjectBuilder

class Compound {
	
	String		name
	String		inchi
	String		inchiKey
	String		pubchemId
	String		chemspiderId
	
    static constraints = {
		name(nullable: true)
		inchi(nullable: true)
		inchiKey(nullable: true, unique:true)
		pubchemId(nullable: true)
		chemspiderId(nullable: true)
    }
	
	def config = ConfigurationHolder.config
	
	def afterInsert = {
		this.fetchExternalDatabaseData()
		this.fetchImage()
	}
	
	static transients = ['imageUrl', 'files']
	
	String getImageUrl(){
		if (this.inchi){
			return config.grails.serverURL + "/images/compounds/" + this.inchi.encodeAsMD5() + ".png" 
		} else {
			return config.grails.serverURL + "/images/compounds/unknown.png"
		}
	}
	
	def getFiles() {
		return Msnfile.findAllByCompound(this) ?: []
	}
	
	def fetchImage(){
		// compound.image ?: fetch >> cache
		if (this.inchi){
			def applicationLoc = ApplicationHolder.getApplication().getParentContext().getResource("/").getFile().toString()
			new File("${applicationLoc}/images/compounds").mkdirs()
			try {
				
				def urlToDownload 		= "${config.metitree.chemicalstructure.inchi}${this.inchi}" as String
				def imageDestination 	= "${applicationLoc}/images/compounds/${this.inchi.encodeAsMD5()}.png" as String

				def file = new FileOutputStream(imageDestination)
				def out = new BufferedOutputStream(file)
				out << new URL(urlToDownload).openStream()
				out.close()
				
			} catch (e) {
				log.error e
			}
		}
	}
	
	def fetchExternalDatabaseData(){
		
		if (this.inchi){
			
			def strChemspiderCompoundName 	= ''
			def chemspiderId 				= null
			def inchiKey 					= ''
			def inchi						= cleanInchi(inchi)

			try { // try to fetch the InChIKey from Chemspider
				new XmlSlurper().parseText(new URL("http://www.chemspider.com/InChI.asmx/InChIToInChIKey?inchi=" + inchi.encodeAsURL()).openConnection().content.text).each { inchiKey = it as String }
			} catch(e){
				log.error(e)
			}

	
			try { // try to fetch a ChemSpiderID from Chemspider
				new XmlSlurper().parseText(new URL("http://www.chemspider.com/InChI.asmx/InChIKeyToCSID?inchi_key=" + inchiKey.encodeAsURL()).openConnection().content.text).each { chemspiderId = it as String }
			} catch(e){
				log.error(e)
			}
	
			try { // try to fetch title of Chemspider page to use as name of compound
				if(chemspiderId){
					strChemspiderCompoundName = (new URL('http://www.chemspider.com/Chemical-Structure.' + chemspiderId + '.html').openConnection().content.text.split('<title>')[1].split('</title>')[0]).replace('|', ':').split(':')[1]
				}
			} catch(e){
				log.error(e)
			}
							
			this.name 			= strChemspiderCompoundName
			this.inchi 			= inchi
			this.inchiKey 		= inchiKey
			this.chemspiderId 	= chemspiderId
			this.save()
		}		
	}
	
	public static cleanInchi(dirtyInchi = '') {
		
		def inchi = ''
		
		try {
			def inchiParts	= (dirtyInchi).split('/')
			
			// filter and format the inchi input
			inchiParts.size().times { index ->
				if (index == 0) {
					inchi += 'InChI=' + (((inchiParts[index]).toUpperCase()).replaceAll("INCHI",'').replaceAll('=',''))
				} else {
					inchi += '/' + inchiParts[index]
				}
			}
			
			//filter out wrong characters from new InChI
			def replaceCharacters = [:]
				replaceCharacters["\n"] = ""
				replaceCharacters["\t"] = ""
				
				// do the replace
				replaceCharacters.each { wrong, right ->
					inchi.replaceAll(wrong, right)
				}
			
			// and make sure the blanks at the start and end are gone...
			inchi = inchi.trim()
			inchi = inchi.replaceAll("\r\n|\n\r|\n|\r","")
		} catch (e) {
			// static methods do no have the option to log.error(e)
			println e
		}

		return inchi
	}
}
