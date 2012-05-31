package nl.nmc.metitree

import grails.converters.*
import org.springframework.context.ApplicationContext
import org.codehaus.groovy.grails.commons.ApplicationHolder

class Directory {
	
	static belongsTo = [ usergroup:Usergroup ]
		
	static hasMany = [ msnfiles : Msnfile ]
	
	static mapping = {
		msnfiles(lazy:false)
	}
	
	Usergroup	usergroup
	String		name
	Date		dateCreated
	Date		lastUpdated

	static transients = ['path', 'toplevelmsnfiles', 'cmlfiles']
	
    static constraints = {
    }
	
	def afterInsert = {
		// new directories must first be created on disc
		def applicationLoc = ApplicationHolder.getApplication().getParentContext().getResource("/").getFile().toString()
		new File("${applicationLoc}/files/usergroup/${this.usergroup.id}/directories/${this.name.encodeAsMD5()}").mkdirs()
	}

	String getPath() {
		return ApplicationHolder.getApplication().getParentContext().getResource("/").getFile().toString() + "/files/usergroup/${this.usergroup.id}/directories/${this.name.encodeAsMD5()}"
	}
	
	List getToplevelmsnfiles() {		
		return Msnfile.findAllByDirectoryAndParent(this, null).findAll{ it.active == true }
	}
	
	List getCmlfiles() {
		return Msnfile.findAllByDirectory(this).findAll{ (it.isCML() == true) && (it.processjob?.active == true) } 
	}
}
