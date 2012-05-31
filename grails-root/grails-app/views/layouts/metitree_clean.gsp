<html>
    <head>
        <title><g:layoutTitle default="Metabolite Identification by using Spectral Trees" /></title>      
        <link rel="stylesheet" href="${resource(dir:'css',file:'mi.css')}" />       
        <link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
        <g:layoutHead />
        <g:javascript library="application" />
		<script type="text/javascript">
		
		  var _gaq = _gaq || [];
		  _gaq.push(['_setAccount', 'UA-24830338-1']);
		  _gaq.push(['_trackPageview']);
		
		  (function() {
		    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
		    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
		    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
		  })();
		
		</script>        
    </head>
    <body>
		<center>          			        		
	    	<img class="borderless" src="${resource(dir:'images',file:'logo_metitree.png')}" alt="metItreeDB" />		       		
			<g:layoutBody />								
			<g:render template="/common/footer" />
			</center>	    				
    </body>
</html>