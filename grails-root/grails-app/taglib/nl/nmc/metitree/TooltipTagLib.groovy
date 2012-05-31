package nl.nmc.metitree

class TooltipTagLib {
	
	static namespace = 'tt'
	
	
	def simple = { attrs ->
				
		out << '<a class="tooltip" href="#">'
		out << '	<img border="0" class="borderless" width="20px" src="' + resource(dir:'images',file:'information-frame.png') + '" alt="help" />'
		out << '	<span>'
		out << '		<img border="0" style="float: right; margin: 0 0 10px 10px;" width="40px" src="' + resource(dir:'images',file:'information-frame.png') + '" alt="help" />'
		out << '		<h2><b>Help</b></h2><br /><div style=\"font-weight: normal;\">'
		
		if (attrs.code){
			out << message(code: attrs.code)
		} else {
			out << attrs.body
		}
		out << '		</div>'
		out << '	</span>'
		out << '</a>'
	}
	
	def img = { attrs ->
		out << '<div valign="top" style="border: thin solid #fff; text-align: left;">'
		out << '<a class="tooltip" href="#">'
		if (attrs.imgthumbsize){
			out << '	<img border="0" height="' + attrs.imgthumbsize + '" src="' + attrs.imgurl + '" />'
		}
		if (attrs.label){
			out << attrs.label
		}
		out << '	<span>'
		out << '		<img border="0" style="margin: 10px;" height="' + attrs.imgsize + '" src="' + attrs.imgurl + '" /><br />'
		out << '		<b>' + attrs.imglabel + '</b>'
		out << '	</span>'
		out << '</a>'
		out << '</div>'
	}
	
	def compound = { attrs ->
		
		def compound = attrs.compound
		
		out << '<a class="tooltip" href="#">'
		out << '	<img border="0" class="borderless" width="20px" src="' + resource(dir:'images',file:'information-frame.png') + '" alt="help" />'
		out << '	<span>'
		out << '		<img border="0" style="float: right; margin: 0 0 10px 10px;" width="50px" src="' + resource(dir:'images',file:'information-frame.png') + '" alt="more info" />'
		out << '		<h2><b>' +  compound.name  +'</b></h2><br /><div style=\"font-weight: normal;\">'
		
		out << '		<b>' + g.message(code: 'common.label.id') + ':</b> ' + compound.id
		out << '		<br /><img style=\"border: thin solid #cdcdcd; padding:15px; max-height: 200px;\" src=\"' + compound.imageUrl + '\">'
		out << '		<br /><b>' + g.message(code: 'common.label.inchikey') + '</b>' + compound.inchiKey
		out << '		<br /><b>' + g.message(code: 'common.label.inchi') + '</b>' + compound.inchi
//		out << '			<g:if test=\"${compound.inchiKey}\"><a target=\"_blank\" href='http://www.ncbi.nlm.nih.gov/sites/entrez?cmd=search&db=pccompound&term=\"${compound.inchiKey}\"[InChIKey]'><img class=\"borderless\" src=\"${resource(dir:'images',file:'pubchemlogob.gif')}\" alt=\"view\" /></a><br /></g:if>
//		out << '			<g:if test=\"${compound.chemspiderId}\"><a target=\"_blank\" href='http://www.chemspider.com/Chemical-Structure.${compound.chemspiderId}.html'><img class=\"borderless\" src=\"${resource(dir:'images',file:'chemspider.png')}\" alt=\"view\" /></a></g:if>'
//		if (compound.files) {
//			out << '	<h2>' + g.message(code: 'common.label.msnfiles') + '</h2>'
//			compound.files.each { msnfile ->
//				out << g.link(controller:'msnfile', action:'preview', id: msnfile.id) { msnfile.name } + '<br />'
//			}
//		}
		out << '		</div>'
		out << '	</span>'
		out << '</a>'
	}

}