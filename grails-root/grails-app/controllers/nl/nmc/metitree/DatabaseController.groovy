package nl.nmc.metitree

class DatabaseController {

	def add = {
		new Database(name: params.database, usergroup: session.member.usergroup).save(flush: true)
		
		//return to where you came from...
		redirect(url: request.getHeader('Referer'))
	}
	
	def remove = {
		def database = Database.findByIdAndUsergroup(params.id, session.member.usergroup)
		
		if (!database.msnfiles.size()){
			database.delete()
		}
		
		//return to where you came from...
		redirect(url: request.getHeader('Referer'))
	}
	
	def addmsnfile = {
		
		def database	= Database.findByIdAndUsergroup(params.id, session.member.usergroup)
		def msnfile		= Msnfile.get(params.msnfile)
		
		if (session.member.usergroup.id == msnfile.member.usergroup.id){ // users can only add to databases of the usergroup
			if (database && msnfile){
				msnfile.addToDatabase(database)
			}
		}
		
		//return to where you came from...
		redirect(url: request.getHeader('Referer'))
	}
	
	def removemsnfile = {
		
		def database	= Database.findByIdAndUsergroup(params.id, session.member.usergroup)
		def msnfile		= Msnfile.get(params.msnfile)
		
		if (session.member.usergroup.id == msnfile.member.usergroup.id){ // users can only add to databases of the usergroup
			if (database && msnfile){
				database.removeFromMsnfiles(msnfile)
			}
		}
		
		//return to where you came from...
		redirect(url: request.getHeader('Referer'))
	}
	
	def details = {
		
		Usergroup usergroup = session.member.usergroup
		Member member = session.member
		
		def database = Database.findByIdAndUsergroup(params.id, session.member.usergroup)
	
		def j = ProcessJob.createCriteria()
		def jobs = j {			
			'in'("member", usergroup.members)
			eq("active", true)
			ne("lastStatus", 1)
		}
			
		/*
		 * fetch all cml files,
		 * from usergroup processJobs,
		 * that have not already been added to the database
		 * grouped by directory
		 */
		ProcessJob jobSelected
		
		// filter on selected job
		if (params.jobSelected && params.jobSelected != 'null'){
			jobSelected = ProcessJob.get(params.jobSelected)
		} 

		// adding all possible candidates
		if (params.removeListed){
									
			//remove all from database that originate from the processjob
			database.msnfiles.asList().each { msnfile ->
				if ((jobSelected != null && jobSelected?.id == it.processjob.id) || (jobSelected == null)){
					database.removeFromMsnfiles(msnfile)
				}
			}
		}		
				
		def candidates = []
		
		jobs.each { processJob ->			
			if (processJob?.active && (processJob.member.usergroup.id == session.member.usergroup.id) && processJob?.lastStatus >= 2){
				def msnfiles = Msnfile.findAllByProcessjob(processJob)
				msnfiles.each { msnfile ->
					if (msnfile.location.tokenize(".")[-1] == 'cml'){ //we can only add cml to our database
						if (!database.msnfiles.collect {it.id}.contains(msnfile.id)){ //also ignore it when it is already in the database
							candidates.add(msnfile)
						}
					}
				}
			}
		}
		
		// adding all possible candidates
		if (params.addCandidates){
			//add all candidates
			candidates.each {
				if ((jobSelected != null && jobSelected?.id == it.processjob.id) || (jobSelected == null)){
					it.addToDatabase(database)
				}
			}
			candidates = []
		}
		
		[ database: database, candidates: candidates, jobs: jobs, jobSelected: jobSelected ]
	}
	
}
