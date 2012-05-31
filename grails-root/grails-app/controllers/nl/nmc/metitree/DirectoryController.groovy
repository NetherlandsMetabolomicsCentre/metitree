package nl.nmc.metitree

class DirectoryController {
	
	def add = {
		if (params.directory != ""){	
			new Directory(name: params.directory, usergroup: session.member.usergroup).save(flush: true)
		} 
		
		redirect(url: request.getHeader('Referer'))
	} 	
	
	def remove = {
		def directory = Directory.findByIdAndUsergroup(params.id, session.member.usergroup)
		
		if (!directory.msnfiles.size()){
			directory.delete()
		}
		
		//return to where you came from...
		redirect(url: request.getHeader('Referer'))
	}
}
