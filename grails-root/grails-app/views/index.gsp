<html>
    <head>
        <title>Welcome to Grails</title>
		<meta name="layout" content="main" />
    </head>
    <body>
		<div id="nav">
			<div class="homePagePanel">
				<div class="panelTop">

				</div>
				<div class="panelBody">
					<h1>Application Status</h1>
					<ul>
						<li>App version: <g:meta name="app.version"></g:meta></li>
						<li>Grails version: <g:meta name="app.grails.version"></g:meta></li>
						<li>JVM version: ${System.getProperty('java.version')}</li>
						<li>Controllers: ${grailsApplication.controllerClasses.size()}</li>
						<li>Domains: ${grailsApplication.domainClasses.size()}</li>
						<li>Services: ${grailsApplication.serviceClasses.size()}</li>
						<li>Tag Libraries: ${grailsApplication.tagLibClasses.size()}</li>
					</ul>
					<h1>Installed Plugins</h1>
					<ul>
						<g:set var="pluginManager"
						       value="${applicationContext.getBean('pluginManager')}"></g:set>

						<g:each var="plugin" in="${pluginManager.allPlugins}">
							<li>${plugin.name} - ${plugin.version}</li>
						</g:each>

					</ul>
				</div>
				<div class="panelBtm">
				</div>
			</div>


		</div>
		<div id="pageBody">
	        <h1>Welcome to Grails</h1>
	        <p>Congratulations, you have successfully started your first Grails application! At the moment
	        this is the default page, feel free to modify it to either redirect to a controller or display whatever
	        content you may choose. Below is a list of controllers that are currently deployed in this application,
	        click on each to execute its default action:</p>

	        <div id="controllerList" class="dialog">
				<h2>Available Controllers:</h2>
	            <ul>
	              <g:each var="c" in="${grailsApplication.controllerClasses}">
	                    <li class="controller"><g:link controller="${c.logicalPropertyName}">${c.fullName}</g:link></li>
	              </g:each>
	            </ul>
	        </div>
		</div>
    </body>
</html>