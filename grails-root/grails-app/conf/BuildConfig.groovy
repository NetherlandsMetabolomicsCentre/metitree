grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir	= "target/test-reports"
grails.server.port.http = 8081 //default for demo app

//grails.project.war.file = "target/${appName}-${appVersion}.war"
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits( "global" ) {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {        
        grailsPlugins()
        grailsHome()
        grailsCentral()
		mavenRepo "http://nexus.nmcdsp.org/content/repositories/releases"
		mavenCentral()
		mavenRepo 'http://repository.jboss.org/maven2/'
    }
    dependencies {
		compile 'org.apache.ant:ant:1.7.1'    //you can also use runtime
		compile 'org.apache.ant:ant-launcher:1.7.1'
    }

}
