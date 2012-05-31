package nl.nmc.metitree

import groovyx.gpars.*
import java.util.concurrent.TimeUnit

class ImportService {

    static transactional = true
	
	def jsonService
	def csvService
	def pdfService
	def mconfService

	/**
	 * Allows import script(s) to create metitree job entries with associated files
	 * 
	 * args['usergroup']		- name of the usergroup
	 * args['member']			- name of the member (within the usergroup)
	 * args['directory']		- name of the directory where to store the files to
	 * args['database']			- name of the database where to (optionally) link the files
	 * 
	 * args['settings']			- contain the process settings as HashMap
	 * args['inchi']			- InChI of the parent compound
	 * args['comments']			- comments about this mzxml file
	 * args['mzxml']			- full path to File()
	 * args['xcms']				- full path to File()
	 * args['cml']				- full path to File()
	 * args['json']				- full path to File()
	 * args['master.json']		- full path to File()
	 * args['csv']				- full path to File()
	 * args['tab']				- full path to File()
	 * args['pdf']				- full path to File()
	 * args['...				- ...
	 * 
	 * @param args
	 * @return
	 */
    boolean importSet(List toImport) {
		
		def existingMzXml = [:] 
		
		def filesToAddToDatabase = [:]
		
		//GParsExecutorsPool.withPool(1) { //import with 1 file at a time
			
			toImport.each { args ->
			//toImport.eachParallel { args ->
				
				Msnfile.withTransaction(){
					
					// set usergroup, member, directory and optionally the database
					def usergroup 	= Usergroup.findByName(args['usergroup'])
					def member 		= Member.findByUsergroupAndName(usergroup, args['member'])
					def directory	= Directory.findByUsergroupAndName(usergroup, args['directory']) ?: new Directory(usergroup: usergroup, name: args['directory']).save()
					def database	= Database.findByUsergroupAndName(usergroup, args['database']) ?: new Database(usergroup: usergroup, name: args['database']).save()
					
					// check if we have at least a member and a directory to import to
					if (member && directory){
								
						def mzxmlToImport 	= new File(args['mzxml'])
				
						if (mzxmlToImport.exists()){
							
							//add the mzxml to the directory
							def mzxml
							if (existingMzXml[args['mzxml']]){
								
								// use an existing mzXml file
								mzxml = existingMzXml[args['mzxml']]
								
							} else {
							
								// add a new mzXML file
								mzxml = addMsnfile('mzxml', mzxmlToImport, member, directory)
								
								//add inchi
								if (args['inchi']){
									mzxml.compound = Compound.findByInchi(args['inchi']) ?: new Compound(inchi: args['inchi']).save()
									mzxml.save()
								}
								
								//add comments
								if (args['comments']){
									mzxml.comments = args['comments']
									mzxml.save()
								}
								
								//keep track of previous imported mzXml files
								existingMzXml[args['mzxml']] = mzxml
							}
							
							// now build a dummy ProcessJob to link all the files based on the provided settings
							def pjs = processSettingsFromHashMap(args['settings'], member)
							def pj = new ProcessJob(active:true, member:member, lastStatus:2, report:"import job", settings: pjs, fids:mzxml.id as String).save()
							
							// now add the xcms data. as this is not required for metitree to work it is optional. however a xcms msnfile should always be created
							def xcms = null
							if (mzxml){
								xcms = addMsnfile('xcms', new File(args['xcms'] ?: ''), member, directory, mzxml, pj)
							}
							
							// add cml
							def cml = null
							if (xcms != null){
								cml = addMsnfile('cml', new File(args['cml'] ?: ''), member, directory, xcms, pj)
							}
							
							// add json & master.json
							def json		= null
							def masterjson	= null
							if (cml != null){
								json 		= addMsnfile('json', new File(args['json'] ?: ''), member, directory, cml, pj)
								masterjson	= addMsnfile('master.json', new File(args['master.jason'] ?: ''), member, directory, cml, pj)
							}
							
							// add csv & tab
							def csv		= null
							def tab	= null
							if (masterjson != null){
								csv	= addMsnfile('csv', new File(args['csv'] ?: ''), member, directory, masterjson, pj)
								tab	= addMsnfile('tab', new File(args['tab'] ?: ''), member, directory, masterjson, pj)
							}
							
							// add pdf
							if (csv != null){
								def pdf	= addMsnfile('pdf', new File(args['pdf'] ?: ''), member, directory, csv, pj)
							}
							
							//add the entry to the database
							if (database != null && cml != null){
								filesToAddToDatabase[cml.id] = database.id
								//cml.addToDatabase(database)
							}
						}
					}
				}
			}
		//}
			
		//now add the files to the correct database
		if (filesToAddToDatabase){
			filesToAddToDatabase.each { cmlKey, dbKey ->
				
				def file
				def database
				
				try {
					file = Msnfile.get(cmlKey as int)
					database = Database.get(dbKey as int)
					
					//add it
					file.addToDatabase(database)
					
				} catch (e) {
					log.error("Was unable to add this file ${file} to the database (${database})")
				}
			}
		}	
			
		return true	
    }
	
	/**
	 * Get the full path, filename and extension of a file as a HashMap
	 * 
	 * @param file
	 * @return HashMap
	 */
	HashMap fileparts(File file){
		
		def fileparts = [:]
		
		fileparts['filename']	= file.absolutePath.toLowerCase()
		fileparts['name']		= fileparts['filename'].tokenize("/")[-1]
		fileparts['ext']		= fileparts['name'].tokenize(".")[-1]
		
		return fileparts
		
	}
	
	/**
	 * Get ProcessSettings object from HashMap
	 * 
	 * expected settings are:
	 * settings['mzgap']
	 * settings['elements']
	 * settings['accuracy']
	 * settings['snthresh']
	 * settings['rules']
	 */
	ProcessSettings processSettingsFromHashMap(HashMap settings, Member member){
		
		def processSettings = null
		
		def jsonSettings 	=	'{'
			jsonSettings 	+=	'"mzgap":"' + (settings['mzgap'] ?: 5) + '", "elements":"' + (settings['elements'] ?: '') + '", "snthresh":"' + (settings['snthresh'] ?: 10) + '"'
		
		def accuracySettings = settings['accuracy']?.tokenize(',')
		
		if (accuracySettings){
			jsonSettings	+=	',"accuracy-L1":"' + (accuracySettings[0] ?: 15) + '"'
			jsonSettings	+=	',"accuracy-L2":"' + (accuracySettings[1] ?: 15) + '"'
			jsonSettings	+=	',"accuracy-L3":"' + (accuracySettings[2] ?: 15) + '"'
			jsonSettings	+=	',"accuracy-L4":"' + (accuracySettings[3] ?: 15) + '"'
			jsonSettings	+=	',"accuracy-L5":"' + (accuracySettings[4] ?: 15) + '"'
		}

		if (settings['rules'].toLowerCase()?.contains('rdber')){
			jsonSettings	+=	',"rulerdber":"RDBER"'
		}
		if (settings['rules'].toLowerCase()?.contains('nitrogenr')){
			jsonSettings	+=	',"rulenitrogenr":"nitrogenR"'
		}
		
			jsonSettings	+=	'}'

		processSettings = ProcessSettings.findByMemberAndSettingsHash(member, jsonSettings.encodeAsMD5()) ?: new ProcessSettings(member: member, settings: jsonSettings, settingsHash: jsonSettings.encodeAsMD5()).save()

		return processSettings
	}
	
	Msnfile addMsnfile(String type, File f, Member member, Directory directory, Msnfile parent = null, ProcessJob pj = null){
				
		def genName 	= UUID.randomUUID().toString()
		def msnfile 	= null
		def name		= null
		def filename	= null
		
		//determine what to do with the name and filename
		if (parent != null){
			name	 = parent.name.replace(".${parent.name.tokenize('.')[-1]}", ".${type}")
			if (parent.filename.tokenize('.')[-1] == 'mzxml'){
				filename = parent.filename.replace(".${parent.filename.tokenize('.')[-1]}", ".${pj.id}.${type}")
			} else {
				filename = parent.filename.replace(".${parent.filename.tokenize('.')[-1]}", ".${type}")
			}
		} else {
			name		= fileparts(f).name // leave this set to the original filename as it is used in the cml
			filename	= genName + '.mzxml' //only mzxml files can be orphans	
		}
		
		// add a Msnfile entry
		msnfile = new Msnfile(member: member, directory: directory, parent: parent, processjob: pj, name: name, filename: filename).save()
		
		// set File()
		def file = new File(msnfile.location)
			file.setWritable(true, false)

		if (f.exists()){		
			// move the actual file to the correct location
			file << f.text
		} else {
			//based on the type we either generate the data or we add a placeholder file
			switch(type){
				case 'json'			:	file << jsonService.treeFromCML(parent); break;
				case 'master.json'	:	file << jsonService.treeFromCML(parent, mconfService.fromLabel("metitree.processing.cml.occurrence").toBigDecimal()); break;//mconfService.fromLabel("metitree.processing.cml.occurrence") as Float); break;
				case 'csv'			:	csvService.csvFromCML(parent, msnfile); break;
				case 'tab'			:	csvService.tabFromCML(parent, msnfile); break;
				case 'pdf'			:	pdfService.pdfFromCSV(parent, msnfile); break;
				default : file << "#placeholder - was unable to import this data"
			}
		}
		
		return msnfile
	}
}
