<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="layout" content="metitree" />      
  </head>
  <body>  				
  	<div class="thin_green_border">
  		<h2>${msnFile.compound()?.name}</h2>
  		<div class="bordered">
			<g:if test="${previewData['json']}">
				<!-- preview JSON contents -->
				<iframe src="${serverUrl}/viewer/view/?name=${previewData['filename'].replace('.','_')}&treeFile=${previewData['json'].toString().bytes.encodeBase64().toString()}" width="100%" height="1000px" frameborder="0" scrolling="auto"></iframe>
			</g:if>
			
			<g:if test="${previewData['xml']}">
				<!-- preview XML contents -->		
				<pre>${previewData['xml']}</pre>
			</g:if>		
			
			<g:if test="${previewData['text']}">
				<!-- preview TEXT contents -->
				<pre>${previewData['text']}</pre>
			</g:if>
		</div>
	</div>			
  </body>
</html>		