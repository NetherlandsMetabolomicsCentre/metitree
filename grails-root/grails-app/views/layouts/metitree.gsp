<html>
    <head>
        <title><g:layoutTitle default="Metabolite Identification by using Spectral Trees" /></title>
      	<META HTTP-EQUIV="CACHE-CONTROL" CONTENT="NO-CACHE">
        <META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
        <META HTTP-EQUIV="EXPIRES" CONTENT="Mon, 22 Jul 2002 11:12:01 GMT">      
        <link rel="stylesheet" href="${resource(dir:'css',file:'mi.css')}" />       
        <link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
        <r:layoutResources/>
        <g:layoutHead />        
        <g:javascript library="application" />        
    </head>
    <body>
     	<div class="page">   
       		<g:if test="${session?.member?.name}">
				<div style="font-size: 13px; text-align: right; padding-right: 25px; min-width: 400px;">
					<g:message code="common.label.welcome" /> <b>${session.member.name}</b> <g:if test="${session?.member?.name != 'anonymous'}">(${session.usergroup.name})</g:if><b> - 
					<g:link controller="auth" action="logout"><g:message code="common.label.logout" /></g:link></b><br />
					<g:if test="${session?.member?.name == 'anonymous'}">
				    	<font size="1.2em" color="red">
				    		Please do not save private data in this account since access is open to anyone!
			    		</font>
					</g:if>
				</div>		    	
			</g:if>
	    	<div style="margin-left: 22px; font: 39px verdana, arial, helvetica, sans-serif;">Metitree</div>		    	
			<g:render template="/common/menu" />							         				       		
			<g:layoutBody />											
			<g:render template="/common/footer" />
		</div>			
       	<r:layoutResources/>   				
    </body>
</html>