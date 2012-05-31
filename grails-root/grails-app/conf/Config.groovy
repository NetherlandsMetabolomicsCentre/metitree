// ****** Metitree Config ******

//image url for chemical structures
metitree.chemicalstructure.inchi = "http://localhost/inchi2image/png/"

// ConfigurationHolder.config.metitree.x
metitree.processing.jobs.parallel.max 	= 1		// maximum number of jobs that can run parallel over all users (0 = unlimited > kill your server)
metitree.processing.jobs.threads.max 	= 1		// maximum number of threads per job
metitree.processing.jobs.max.execution	= 30 * 60 * 1000 // job may run no longer than 30 minutes
metitree.processing.cml.occurrence	= 0.4	// node occurrence in tree(s) to fit in master tree
metitree.msnfiles.temp.location		= "temp" // name of temp storage of a usergroup
// ****** Metitree Config ******


// DEFAULT GRAILS
grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [ html: [
		'text/html',
		'application/xhtml+xml'
	],
	xml: [
		'text/xml',
		'application/xml'
	],
	text: 'text/plain',
	js: 'text/javascript',
	rss: 'application/rss+xml',
	atom: 'application/atom+xml',
	css: 'text/css',
	csv: 'text/csv',
	all: '*/*',
	json: [
		'application/json',
		'text/json'
	],
	form: 'application/x-www-form-urlencoded',
	multipartForm: 'multipart/form-data'
]
// The default codec used to encode data with ${}
grails.views.default.codec="none" // none, html, base64
grails.views.gsp.encoding="UTF-8"
// grails.views.javascript.library="dojo"
grails.converters.encoding="UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder=false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// whether to install the java.util.logging bridge for sl4j. Disable fo AppEngine!
grails.logging.jul.usebridge = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []

// set per-environment serverURL stem for creating absolute links
environments {
	development {
		grails.serverURL 	= "http://localhost:8081/metitree"
		grails.config.myip 	= "localhost"
		grails.config.myport 	= "8081"
		grails.rapache.url 	= "http://localhost/brew"
	}
}

grails.resources.modules = {
	overrides {
		'jquery-theme' {
			resource id:'theme', url:'/css/jquery-ui-1.7.3.custom.css'
		}
	}
}


// log4j configuration
log4j = {
	info 'grails.app' //log-info!
	error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
			'org.codehaus.groovy.grails.web.pages', //  GSP
			'org.codehaus.groovy.grails.web.sitemesh', //  layouts
			'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
			'org.codehaus.groovy.grails.web.mapping', // URL mapping
			'org.codehaus.groovy.grails.commons', // core / classloading
			'org.codehaus.groovy.grails.plugins', // plugins
			'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
			'org.springframework',
			'org.hibernate',
			'net.sf.ehcache.hibernate'
	warn   'org.mortbay.log'
}
