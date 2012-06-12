/**
 * Base Filters
 * @Author Michael van Vliet
 * @Since 20101126
 * @see http://grails.org/Filters
 */
import grails.converters.*
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.codehaus.groovy.grails.web.context.ServletContextHolder;

import nl.nmc.metitree.Member;


class BaseFilters {

	def mconfService
	
	//define filters
	def filters = {
								
		memberCheck(controller:'*', action:'*') {
			before = {								
								
				if (mconfService.fromLabel("metitree.security.enabled") == true){								
					//check if a member is present!
					if (!session.member && ['auth', 'rapache', 'shorturl', 'bootstrap'].count(controllerName) != 1){
						redirect(controller:'auth', action:'login')
						return false
					} else {
						if (!session?.member?.admin && ['usergroup','member','config','dbutil','proxy','api','classdiagram'].count(controllerName) == 1){
							redirect(controller:'page')
							return false
						}
					}
				} else if (!session.member){
				
					// this means all will be done as the anonymous user
					def member 			= Member.findByUsernameAndPassword('anonymous', "anonymous".encodeAsMD5())
					session.member 		= member
					session.usergroup 	= member.usergroup
				}
			}
		}
	}
}

