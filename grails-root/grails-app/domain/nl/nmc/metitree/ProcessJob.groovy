package nl.nmc.metitree

import grails.converters.*

class ProcessJob {
	
	static belongsTo = [ member: Member]
	
	static mapping = {
		settings(lazy:false)
	}

	Member		member	
	ProcessSettings	settings
	String		fids // file id's to process (e.g. 1,2,3,4)
	String		report //a history of what happened when running this job
	int			lastStatus
	boolean		active
	Date		dateCreated
	Date		lastUpdated

    static constraints = {
		lastStatus(nullable: true)
		report(nullable: true)
    }
	
	static transients = ['name', 'msnfiles', 'fragments']
	
	String getName(){
		
		def name = "${this.id}"
		while (name.size() < 12){ name = "0${name}" }
		
		return "JOB#${name}"
	}
	
	def getFragments(){
		
		def fragments = [:]
			fragments['total'] = 0
		
		def msnfiles = Msnfile.findAllByProcessjob(this)
		
		//fill them with 0 to make sure we don't show null
		msnfiles?.each { msnfile ->
			fragments[msnfile.parent.id] = '..'
		}
		
		//find actual fragments
		msnfiles?.each { msnfile ->
			if (msnfile.active){
				def tree = Tree.findByMsnfile(msnfile)
				if (tree){
					fragments[msnfile.parent.id] = tree.treefeatures?.size() ?: '..'
					fragments['total'] += tree.treefeatures?.size() ?: 0
				}
			}
		}
		
		return fragments
	}
	
	def getMsnfiles(){
		
		def msnfiles = []
		
		this.fids.split(',').toList()?.each { mzXMLFileID ->			
			msnfiles.add(Msnfile.get(mzXMLFileID))
		}
		
		return msnfiles
	}
	
	/*
	 * Queue this job to be picked-up by job scheduler
	 */
	ProcessJobQueue queue(){
		return new ProcessJobQueue(processjob: this, status: 0).save(flush: true)
	}
}
