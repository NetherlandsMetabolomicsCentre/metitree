package nl.nmc.metitree

class AuthController {
	
	def index = {
		redirect(action: 'login')
	}

	def login = {

		// see if a member entered any valid credentials
		if (params.username && params.password){
			
			//check if valid
			def member = Member.findByUsernameAndPassword(params.username, "${params.password}".encodeAsMD5())
			
			if (member){
				session.member = member
				session.usergroup = member.usergroup
				
				// redirect to home page
				redirect(controller:'page', action:'data')
				
				return false //make sure we don't do anything after that!
			}
		}
		
	}
	
	def logout = {
		
		// clear member from session		
		session.member = null
		session.usergroup = null
		
		// redirect to login page
		redirect(controller:'auth', action:'login')
		
		return false //make sure we don't do anything after that!
	}
}
