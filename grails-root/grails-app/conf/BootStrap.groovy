import org.codehaus.groovy.grails.commons.ApplicationHolder
import grails.util.GrailsUtil

import nl.nmc.metitree.*;

class BootStrap {

	def grailsApplication

	def init = { servletContext ->

		/* ----------------------------------------------------------------------------------------- */
		/* Bootstrap when database is empty */
		if (!Usergroup.findByName("Demo Usergroup")){

			// DEMO ###################
			// WITH LIMITED ACCESS
			def demoUserGroup = new Usergroup(name: "Demo Usergroup", website: "http://www.demo.com", address: "").save(flush: true)
			def demo = new Member(usergroup: demoUserGroup, name:"Mr. D. Emo", username:"demo", email:"user@demo.com", password:"demo".encodeAsMD5(), admin: false).save()
			def anonymous = new Member(usergroup: demoUserGroup, name:"anonymous", username:"anonymous", email:"anonymous@metitree.nl", password:"anonymous".encodeAsMD5(), admin: false).save()
			//def admin = new Member(usergroup: demoUserGroup, name:"Mrs. A. min", username:"admin", email:"admin@demo.com", password:"admin".encodeAsMD5(), admin: true).save()
		}

		if (!Usergroup.findByName("usrgrp")){
			//def usrgrp = new Usergroup(name: "usrgrp", website: "http://www.metitree.nl", address: "").save(flush: true)
			//def usr = new Member(usergroup: usergroup, name:"usr", username:"usrnm", email:"usr@metitree.nl", password:"usrpsswrd".encodeAsMD5(), admin: false).save()
		}

		/* ----------------------------------------------------------------------------------------- */
	}

	def destroy = {
	}
}
