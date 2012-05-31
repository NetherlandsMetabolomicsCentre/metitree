package nl.nmc.metitree

import grails.util.GrailsUtil

class DirectoryTagLib {
	
	static namespace = 'dir'
	
	def childLevel = 1
	
	def downloadMsnFile = { attrs ->
		def msnfile = attrs.msnfile
		
		if (msnfile?.member?.usergroup?.id == session?.member?.usergroup?.id){
			out << g.link(action:"download", controller:"msnfile", id: msnfile.id) { attrs.label }
		}
	}
	
	def msnfilechildren = { attrs ->
			
		def children = null
		
		if (childLevel == 1 || childLevel == 2){
		
			if (childLevel == 1){
				if (params.sortby == "name") {
					children = attrs.children.sort { a,b -> a.name <=> b.name }
				} else {
					children = attrs.children.sort { a,b -> b.dateCreated <=> a.dateCreated }
				}
			} else {
				children = attrs.children.sort { a,b -> b.processjob.id <=> a.processjob.id }
			}	
			
			out << '	<table width="100%" border="0">\n'
						
			children.each { child ->
				
				if (child.active){
				
					out << '	<tr>'
						
						def levelID = UUID.randomUUID().toString()
		
						//display view image
						if (childLevel == 1){
							out << '		<td style="border-top: thin solid #dcdcdc;" nowrap min-width="300px" valign="top" align="left">\n'
							out << 	g.link(action:"preview", controller:"msnfile", id: child.id) { '<img class="borderless" width="20px" src="' + resource(dir:'images',file:'view.png') + '" alt="view" />' }
							if (child.member.id == session.member.id){ //only let the owner delete the file
								out << 	g.link(action:"delete", controller:"msnfile", id: child.id) { '<img class="borderless" width="20px" src="' + resource(dir:'images',file:'delete.png') + '" alt="view" />' }
							}
							out << '		</td>\n'
						} 				 
									
		
						if (childLevel == 1){
								out << '<td style="border-top: thin solid #dcdcdc;" nowrap min-width="300px" valign="top" align="left">\n'
								out <<  '<table border="0">'
								out << 	'<tr><td valign="top" align="right" nowrap>'
								out <<  message(code: "common.label.name") + ' :</td><td valign="top"><b>' + child.name + '</b></td>'
								out <<  '<td align="right">'
								if (child.compound()?.inchiKey) { out <<  '<a target="top" href=\'http://www.ncbi.nlm.nih.gov/sites/entrez?cmd=search&db=pccompound&term="' + child.compound()?.inchiKey + '"[InChIKey]\'><img class="borderless" height="15px" src="' + resource(dir:'images',file:'pubchemlogob.gif') + '" alt="' + child.compound()?.inchiKey + '" longdesc=" ' + child.compound()?.inchiKey + '" /></a>' }
								if (child.compound()?.chemspiderId) { out <<  '<a target="top" href="http://www.chemspider.com/Chemical-Structure.' + child.compound()?.chemspiderId + '.html"><img class="borderless" height="15px" src="' + resource(dir:'images',file:'chemspider.png') + '" alt="' +  child.compound()?.inchiKey + '" longdesc="' + child.compound()?.inchiKey + '" /></a>' }
								out <<  '</td></tr>'
								out <<  '<tr><td valign="top" align="right" nowrap>' + message(code: "common.label.by") + ' :</td><td colspan="2" valign="top">' + child.member.name + '</td></tr>'
								out <<  '<tr><td valign="top" align="right" nowrap>' + message(code: "common.label.created") + ' :</td><td colspan="2" valign="top">' + child.dateCreated + '</td></tr>'
								if (child.compound()?.inchiKey){						
									out <<  '<tr><td valign="top" align="right" nowrap>' + message(code: "common.label.inchikey") + ' :</td><td colspan="2" valign="top"><a target="top" href=\'http://www.ncbi.nlm.nih.gov/sites/entrez?cmd=search&db=pccompound&term="' + child.compound()?.inchiKey + '[InChIKey]\'>' + child.compound()?.inchiKey + '</a></td></tr>'
								}	
		
								if (child.member.id == session.member.id){ //only let the owner see this information
									def topLevelChildInchi = child?.compound?.inchi ?: ''
									
									out <<	g.form(id: child.id, name:"update_inchi", url:[action:'setinchi',controller:'msnfile']) { 
										'<tr>' +
										'	<td valign="top" align="right" nowrap>' + message(code: "common.label.inchi") + ' :</td>' +
										'	<td valign="top">' +
										'		<input style="font-size: 0.8em; min-width: 450px;" type="text" name="inchi" value="' + topLevelChildInchi + '">' +
										'		<input type="hidden" name="msnfile" value="' + child.id + '">' +
										'	</td>' +
										'	<td valign="top">' +
										'		<input type="submit" value="' + message(code: "common.label.update") + '">' +
										'	</td>' +
										'</tr>'
									}
								}
								
							out <<  '</table>'
						} else {					
							if (childLevel == 2){
								out << '<td nowrap min-width="300px" valign="top" align="left">&nbsp;</td><td>'
								out << 	g.link(action:"details", controller:"processJob", id: child?.processjob?.id) { '<b>' + child?.processjob?.name + '</b>' }
							}
						}
										
						
						if (child.children){
							// create an extra row below with the children
							childLevel++
							out << msnfilechildren(children:child.children)
							childLevel--
						}
						
						out << '		</td>\n'
										
						if (childLevel == 1){
							out << '		<td style="border-top: thin solid #dcdcdc;" nowrap min-width="300px" valign="top" align="right">\n'
						} else {		
						
							def fragments = child.processjob.fragments
							out << '		<td nowrap valign="top" align="right">'+ fragments[child.id] + '/' + fragments['total'] + ' features(s) found\n'
							
						}
						
						if (childLevel == 1){
							//display image when available
							if (child.compound?.inchi){
								out << '<img height="150px" src="' + mconfService.fromLabel("metitree.chemicalstructure.inchi") + (child.compound?.inchi) + '">'						
							}
						} else {
							out << '</td><td width="100%">'
						}
						
						out << '		</td>\n'
					out << '	</tr>\n'
				}
			}
			out << '	</table>\n'
		}
	 }
}
