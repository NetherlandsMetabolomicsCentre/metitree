<html>
  <head>
	<meta name="layout" content="msnviewer" />
	<script type="text/javascript" src="${serverUrl}/js/raphael.js" ></script>
 	<script type="text/javascript" src="${serverUrl}/js/MSnViewer.js"></script>
	<script type="text/javascript">
		function run(){   
			var jsonString_${nameTree} = ${jsonTree};
			drawing_${nameTree} = new MSnViewer(jsonString_${nameTree}, "${nameTree}");
		}
	</script> 	 	       
  </head>
<body>
<div>
min. m/z: <input type="text" id="min" name="min">&nbsp;max. m/z: <input type="text" id="max" name="max">
<input type="button" onclick="drawing_${nameTree}.zoom()" value="apply m/z filter">&nbsp;
<input type="button" name="show_all" value="reset filter" class="button" onclick="drawing_${nameTree}.DrawFullSpectraOnCurrentView()" /> - 
<input type="button" name="show_cum" value="cumalitive spectra" class="button" onclick="drawing_${nameTree}.DrawCumulativeSpectra()" />
<br/>
</div>
<div id="${nameTree}"></div>
</body>
</html>