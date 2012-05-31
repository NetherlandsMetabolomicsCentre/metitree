/**
 * Base Filters
 * @Author Michael van Vliet
 * @Since 20101126
 * @see http://grails.org/Filters
 */
import grails.converters.*
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.codehaus.groovy.grails.web.context.ServletContextHolder;

class BaseFilters {
	
	//define filters
	def filters = {
								
		memberCheck(controller:'*', action:'*') {
			before = {								
								
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
			}
		}
	}
}

