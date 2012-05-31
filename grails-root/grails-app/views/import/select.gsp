<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="layout" content="metitree" />    
  </head>
  <body>  				
  	<div class="thin_green_border">
		<h2><g:message code="page.import.title" /></h2>
		
		<div class="bordered">
		
			<p>${flash.message}</p>
			
			<p><g:message code="page.import.intro" /></p>
		
			<g:uploadForm controller="import" action="select" name="myUpload">
	   			<g:message code="page.import.addfile" /> : <input type="file" name="importfile" /><br /><br />
				<g:message code="common.label.directory" /> : <input name="directory" value="" type="text"><br />
				<g:message code="common.label.database" /> : <input name="database" value="" type="text"><br /><br />
				<input type="submit" name="submit" value="import" id="submit" />
			</g:uploadForm>
		</div>	     		
	</div>			
  </body>
</html>	    