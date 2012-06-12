<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="layout" content="metitree_clean" />
  </head>
  <body>				
	<div style="width: 320px;" class="thin_green_border">
		<h1 class="ptitle" style="margin-left: 275px;"><g:message code="page.auth.title" /></h1>
		<center>
	   	<div style="margin: 0px; padding: 5px;">   	
	    	<g:form name="frm_login" controller="auth" action="login">
		    	<table>
		    		<tr><td align="right"><g:message code="page.auth.username" /> : </td><td><input type="text" name="username" value=""></td></tr>
		    		<tr><td align="right"><g:message code="page.auth.password" /> : </td><td><input type="password" name="password" value=""></td></tr>
		    		<tr><td></td><td><input type="submit" name="submit" value="login"></td></tr>
		    	</table>
		    	<br />
		    	<font size="1.2em" color="red">
		    		You can try Metitree anonymously <g:link controller="page" action="data">here</g:link>. Please do not save private data in this account since access is open to anyone!
		    	</font>	    	
		    </g:form>
		</div>	  
		</center>  	
	</div>	
  </body>
</html>	    
