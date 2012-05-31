<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="layout" content="metitree" />    
  </head>
  <body>  				
  	<div class="thin_green_border">
		<h2><g:message code="page.data.add.directory" /></h2>
		
		<div class="bordered">
			
			<p><g:message code="page.data.add.directory.intro" /></p>
		
			<g:form controller="directory" action="add">
				<g:message code="common.label.name" /> : <input name="directory" value="" type="text">
				<input type="submit" name="submit" value="<g:message code="common.label.create" />" id="submit" />
			</g:form>
		</div>	     		

		<h2><g:message code="page.directory.directories" /></h2>
		<div class="bordered">
			<g:render template="/page/directory/listing"/>
		</div>
	</div>			
  </body>
</html>	    