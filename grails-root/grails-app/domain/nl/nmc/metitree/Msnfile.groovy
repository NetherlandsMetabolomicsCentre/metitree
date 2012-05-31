package nl.nmc.metitree

import org.openscience.cdk.tools.manipulator.ReactionSchemeManipulator
import org.sams.FingerMZData
import org.sams.MZData
import org.sams.io.CMLReader
import org.sams.manipulator.FingerMZDataManipulator
import org.sams.manipulator.MZDataManipulator

import grails.converters.*
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class Msnfile {
	
	def mconfService
	def urlService
	def config = ConfigurationHolder.config
		
	static belongsTo = [directory: Directory]
		
	static mapping = {
		compound(lazy:false)
		directory(lazy:false)
		processjob(lazy:false)
	}
	
	Member		member		// is owned by a member
	Compound	compound	// represents a compound
	Msnfile		parent		// can be a child of another msnfile
	ProcessJob	processjob	// was created either by upload or a processjob
	String		name		// label
	String		filename	// name of file on disk
	String		comments	// information about the file
	boolean		active = true //is active or not, default is true
	
	Date		dateCreated
	Date		lastUpdated

	static transients = ['location', 'children', 'url', 'displayname']
		
    static constraints = {
		parent(nullable: true)
		processjob(nullable: true)
		compound(nullable: true)
		comments(nullable: true)
    }
		
	def getChildren(processJob = null) {
		if (processJob == null){ 
			return Msnfile.findAllByParent(this)
		} else {
			return Msnfile.findAllByParentAndProcessjob(this, processJob)
		}
	}

	def getDisplayname(){
		return "${this.name} - ${this.processjob.name}"
	}
	
	def getUrl() {
		return "${config.grails.serverURL}/files/usergroup/${this.directory.usergroup.id}/directories/${this.directory.name.encodeAsMD5()}/${this.filename}" as String
	}
	
	def getLocation() {
		return "${this.directory.path}/${this.filename}"		
	}
	
	def compound(){
		
		def compound = this.compound // first see if this msnfile has a compound reference 
		
		if (!compound && this.parent){
			// find the compund of the top-level msnfile
			compound = this.parent.compound()
		}
		
		return compound
				
	}
	
	def isCML(){

		//check if msnFile ends with .cml as only the cml files can be added to the database
		def cmlFile = new File(this.location)
		def arrSplitFilename = "${cmlFile.canonicalPath.toString()}".tokenize('.')
		if (arrSplitFilename[arrSplitFilename.size()-1] != 'cml'){
			return false
		}
		
		return true
				
	}
	
	def storeFingerprints(){
		
		try {
		
			if (!this.isCML()){
				return false // we can only add fingerprints from a CML file
			}

			def cml = new BufferedInputStream(new FileInputStream("${this.location}".toString()))
			def mzReader = new CMLReader(cml)
			def mzData = new MZData()
				mzData = mzReader.read(mzData)
				mzData = MZDataManipulator.group(mzData,mconfService.fromLabel("metitree.processing.cml.occurrence").toBigDecimal())
			cml.close() //close BufferInputStream
						
			if(mzData.getListReactions() == null || ReactionSchemeManipulator.getAllReactions(mzData.getListReactions()).getReactionCount() == 0){
				log.info("this cml ${this} doesn't contain any reaction")
			} else {
			
				//prepare tree
				def tree = null
				tree = Tree.findByMsnfile(this) //every msnFile has one (master)tree
				if (tree != null){
					//before we set the fingerprints we have to make sure we delete any old fingerprints.
					log.error("removing features from tree, to add them again later (refresh)")
					tree.features().each { ->
						tree.removeFromFeatures(it)
					}
				} else {
					//create a new tree
					tree = new Tree(msnfile: this, name: this.name).save()
				}
			
				//open FP MZdata
				def fp =  new FingerMZData();
				
				def fingerprints = FingerMZDataManipulator.getbs24_24_1(mzData)
				
				fingerprints.each { fingerprint ->
					
					//check if this is an existing feature
					def feature = null
					feature = Feature.findByName(fingerprint.toString())
					if (feature == null){
						feature = new Feature(name: fingerprint.toString()).save()
					}
					
					//iterate over features and add them to the tree
					tree.addToFeatures(feature)
				}
			}
		} catch (e) {
			log.error(e)
		}
							
		return true
		
	}
		
	// inject this msnFile into a database
	def addToDatabase(Database database){
		
		if (!this.isCML()){
			return false // we can only add CML
		}
			
		try {							

			if (!Tree.findByMsnfile(this)){ 
				log.info("No Tree found. Try to generate it.")
				//add the tree + fingerprints to the database first
				if (!this.storeFingerprints()){
					return false //must not add file to the database of which we have no fingerprints
				} 
			}
		
			//add a reference to the database that it was added to the database
			database.addToMsnfiles(this)
						
			return true
			
		} catch(e) {
		
			log.error("Was unable to add Msnfile (${this}) to database (${database}) ...")
			log.error e
			return false
		}		
	}
}
