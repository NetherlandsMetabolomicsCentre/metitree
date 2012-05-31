<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="layout" content="metitree" />
  </head>
  <body>			
	<div class="thin_green_border">
		<h2><g:message code="page.database.adddatabase" /></h2>
		<div class="bordered">			 
			<g:form controller="database" action="add" name="add_database">
	   			<g:message code="common.label.name" /> : <input type="text" name="database" value="" />
	   			<input type="hidden" name="jobSelected" value="${jobSelected}" />
	   			<input type="submit" name="submit" value="<g:message code="common.label.add" />" id="submit" />
			</g:form>
		</div>

		<h2><g:message code="page.database.databases" /></h2>
		<div class="bordered">			 	
			<div id="databases">	
				<g:render template="/page/database/listing"/>
			</div>
		</div>
	</div>
  </body>
</html>	    