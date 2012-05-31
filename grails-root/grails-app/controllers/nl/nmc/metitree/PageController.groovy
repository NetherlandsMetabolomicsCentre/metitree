package nl.nmc.metitree

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class PageController {
	
	def mzdataService

	def index = {
		redirect(action: 'data')
		return false
	}
	
	def data = {
		[ directories: Directory.findAllByUsergroup(session.member.usergroup) ]
	}
	
	def directory = {
		[ directory: Directory.findByUsergroupAndId(session.member.usergroup, params.id) ]
	}
		
	def database = {	
		[ databases: Database.findAllByUsergroup(session.member.usergroup), jobSelected: params.processjob ?: '' ]
	}

	def compound = {
		
		def compound = null
		def msnfiles = []
		
		try {
			if (params.id){ //use id to load a compound
				compound = Compound.get(params.id)
			} else if (params.inchi) { //or see if there is an inchi present in the url
				compound = Compound.findByInchi(params.inchi)
			}
			
			//pass only the msnfiles accessable for the user logged in
			msnfiles = compound.files.findAll{ (it.member.usergroup.id as int) == (session.member.usergroup.id as int) }
			
		} catch (e) {
			log.error e
		}
		
		[ compound: compound, msnfiles: msnfiles ]
	}
	
	def process = {
				
		def directorySelected = null
		
		if (params.directorySelected){
			directorySelected = Directory.get(params.directorySelected)
		}
		
		if (directorySelected != null && params.submit == 'process directory'){
			forward(controller: "msnfile", action:"settings",id:4, params:[directories: directorySelected.id])
		}
		
		[ processjobs: ProcessJob.findAllByMember(session.member), directories: Directory.findAllByUsergroup(session.member.usergroup), directorySelected: directorySelected ]
	}
	
	def query = {
		
		// prepare query dashboard data
		def directories 			= Directory.findAllByUsergroup(session.member.usergroup)
		def databases				= Database.findAllByUsergroup(session.member.usergroup)		
		def simScoreFileVsDatabase	= [:]
			
		// determine selected database
		if (params.database){
			session.selectedQueryDirectory = null // change database means we select a new directory
			session.selectedQueryDatabase = Database.findByIdAndUsergroup(params.database as int, session.member.usergroup)
		}
			
		// determine selected directory		
		if (params.directory){
			session.selectedQueryFile = null // new directory means new file!
			session.selectedQueryDirectory = Directory.findByIdAndUsergroup(params.directory as int, session.member.usergroup) 
		}
		
		// determine selected file
		if (params.file && session.selectedQueryDirectory){
			session.selectedQueryFile = Msnfile.findByIdAndDirectory(params.file as int, session.selectedQueryDirectory)
		}
				
		// get the results from the selected database and file
		if (session.selectedQueryDatabase && session.selectedQueryDirectory && session.selectedQueryFile){
			
			// fetch score		
			if (params.search){
				def treeSelectedMsnFile = Tree.findByMsnfile(session.selectedQueryFile)
				if (treeSelectedMsnFile){
					session.selectedQueryDatabase.msnfiles.each { f ->
						if (f.processjob?.active == true) {
						//if (f.id != session.selectedQueryFile.id){ //hide selected file from results
							simScoreFileVsDatabase[f] = treeSelectedMsnFile.similarTo(Tree.findByMsnfile(f))
						//} 
						}
					}
				}
			}
		}
		
		//make sure we refresh the session objects
		if (session.selectedQueryDatabase) 	{ session.selectedQueryDatabase 	= Database.findByIdAndUsergroup(session.selectedQueryDatabase.id, session.member.usergroup) }
		if (session.selectedQueryDirectory) { session.selectedQueryDirectory 	= Directory.findByIdAndUsergroup(session.selectedQueryDirectory.id, session.member.usergroup) }
		if (session.selectedQueryFile) 		{ session.selectedQueryFile 		= Msnfile.findByIdAndDirectory(session.selectedQueryFile.id, session.selectedQueryDirectory) }
			
		
		[ directories: directories, databases: databases, simScoreFileVsDatabase: simScoreFileVsDatabase ]	
	}
}
