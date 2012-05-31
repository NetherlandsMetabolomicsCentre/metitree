<html>
  <head>
	<meta name="layout" content="metitree" />
  </head>
<body>
	<g:if test="${url != null}">
		<br /><g:link controller="viewer" action="index" params="[name: '', tree: '']"><<< BACK: Post JSON formatted Tree Structure</g:link><br /><br />
		<iframe style="border: 0px;" src="${url}" width="100%" height="2500"></iframe>
	</g:if>
	<g:if test="${url == null}">
		<p>Paste a JSON formatted Tree Structure to view with the MSnViewer.js application (<b><g:link controller="viewer" action="index" params="[example: true]">load example</g:link></b>)</p>
		<g:form name="view" action="index">		
			<input type="hidden" name="name" value="${new Date()}">
			<textarea style="height: 120px; width: 100%;" name="tree">${tree}</textarea>		
			<br /><input type="submit" name="Submit" value="View">
		</g:form>	
	
	</g:if>
</body>
</html>