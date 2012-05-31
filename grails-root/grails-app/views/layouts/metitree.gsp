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
	        		    		
			<div style="font-size: 13px; text-align: right; padding-right: 25px; min-width: 400px;">
				<g:if test="${session?.member?.name}">
					<g:message code="common.label.welcome" /> <b>${session.member.name}</b> (${session.usergroup.name})<b> - 
					<g:link controller="auth" action="logout"><g:message code="common.label.logout" /></g:link></b> - 
				</g:if>
				<a href="${resource(dir:'documentation',file:'howto.pdf')}">help <img height="13px" class="borderless" src="${resource(dir:'images',file:'pdf.gif')}" alt="help" /></a>
			</div>	        
 		    <div class="logo">
	        	<g:link url="${resource(dir:'')}">          			        		
	        		<img class="borderless" src="${resource(dir:'images',file:'logo_metitree.png')}" alt="metItreeDB" />
	        	</g:link>	    		
	        </div>	    		
			<g:render template="/common/menu" />							         				       		
			<g:layoutBody />											
			<g:render template="/common/footer" />
		</div>			
       	<r:layoutResources/>   				
    </body>
</html>