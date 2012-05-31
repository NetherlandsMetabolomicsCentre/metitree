package nl.nbic.msnviewer

import grails.converters.*;
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class ViewerController {
	
	def config = ConfigurationHolder.config

	def index = {
		
		def url = null
		
		if (params.tree && params.name){
			url = "${config.grails.serverURL}/viewer/view/?name=${params.name.encodeAsMD5()}&tree=${params.tree.toString().bytes.encodeBase64().toString()}"
		}
		
		if (params.example){
			
			//load example tree
			params.tree = """
{
	"1" : {
		"id" : "1",
		"parent" : "",
		"mass" : "164.01",
		"intensity" : "2601726.5555",
		"inchi" : "InChI=1S/C5H9NO3S/c1-3(7)6-4(2-10)5(8)9/h4,10H,2H2,1H3,(H,6,7)(H,8,9)/p+1,InChI=1S/C3H7NO2S/c4-2(1-7)3(5)6/h2,7H,1,4H2,(H,5,6)/p+1",
		"inchiloss" : ""
	},
	"2" : {
		"id" : "2",
		"parent" : "1",
		"mass" : "122.01",
		"intensity" : "378006.0052",
		"inchi" : "InChI=1S/C3H7NO2S/c4-2(1-7)3(5)6/h2,7H,1,4H2,(H,5,6)/p+1",
		"inchiloss" : "InChI=1S/C2H2O/c1-2-3/h1H2"
	},
	"3" : {
		"id" : "3",
		"parent" : "1",
		"mass" : "146.01",
		"intensity" : "115943.7254",
		"inchi" : "InChI=1S/C5H7NO2S/c1-4(8)6-5(2-7)3-9/h5H,3H2,1H3,(H-,6,8,9)/p+1",
		"inchiloss" : "InChI=1S/H2O/h1H2"
	},
	"4" : {
		"id" : "4",
		"parent" : "1",
		"mass" : "118.01",
		"intensity" : "85871.6974",
		"inchi" : "InChI=1S/C4H7NOS/c1-4(6)5-2-3-7/h2,7H,3H2,1H3/p+1/b5-2-",
		"inchiloss" : "InChI=1S/CO/c1-2,InChI=1S/H2O/h1H2"
	},
	"5" : {
		"id" : "5",
		"parent" : "2",
		"mass" : "76.01",
		"intensity" : "231647.1666",
		"inchi" : "InChI=1S/C2H5NS/c3-1-2-4/h1,3-4H,2H2/p+1",
		"inchiloss" : "InChI=1S/CO/c1-2,InChI=1S/H2O/h1H2"
	},
	"6" : {
		"id" : "6",
		"parent" : "2",
		"mass" : "105.01",
		"intensity" : "65240.4713",
		"inchi" : "InChI=1S/C3H4O2S/c4-3(5)1-2-6/h1H,2H2,(H-,4,5,6)/p+1",
		"inchiloss" : "InChI=1S/H3N/h1H3"
	},
	"7" : {
		"id" : "7",
		"parent" : "2",
		"mass" : "87.01",
		"intensity" : "20058.1733",
		"inchi" : "InChI=1S/C3H2OS/c4-2-1-3-5/h2-3H/p+1",
		"inchiloss" : "InChI=1S/C2H2O1"
	},
	"8" : {
		"id" : "8",
		"parent" : "3",
		"mass" : "118.01",
		"intensity" : "28512.2361",
		"inchi" : "InChI=1S/C4H7NOS/c1-4(6)5-2-3-7/h2,7H,3H2,1H3/p+1/b5-2-",
		"inchiloss" : "InChI=1S/CO/c1-2"
	},
	"9" : {
		"id" : "9",
		"parent" : "3",
		"mass" : "76.01",
		"intensity" : "95240.4713",
		"inchi" : "InChI=1S/C2H5NS/c3-1-2-4/h1,3-4H,2H2/p+1",
		"inchiloss" : "InChI=1S/C2H2O/c1-2-3/h1H2,InChI=1/CO/c1-2"
	},
	"10" : {
		"id" : "10",
		"parent" : "4",
		"mass" : "76.01",
		"intensity" : "48397.7845",
		"inchi" : "InChI=1S/C2H5NS/c3-1-2-4/h1,3-4H,2H2/p+1",
		"inchiloss" : "InChI=1S/C2H2O/c1-2-3/h1H2"
	},
	"11" : {
		"id" : "11",
		"parent" : "8",
		"mass" : "76.01",
		"intensity" : "10072.9954",
		"inchi" : "InChI=1S/C2H5NS/p+1",
		"inchiloss" : "InChI=1S/C2H2O/c1-2-3/h1H2"
	}
}"""
			
			//make unique name based on date
			params.name = new Date()
		}
					
		return [ tree: params?.tree ?: "", url: url ]
	}
	
    def view = {
		
		def nameTree		= params?.name ? params.name.decodeURL() : "" 
		def jsonTree		= params?.tree ? params.tree.decodeURL() : ""
		def jsonTreeFile	= params?.treeFile ? params.treeFile.decodeURL() : ""
		
		if (jsonTree == "") {
			if (jsonTreeFile != ""){
				byte[] jsonTreeFileDecoded = jsonTreeFile.decodeBase64()
				try {
					jsonTree = (JSON.parse(new File(new String(jsonTreeFileDecoded)).text)).get('1')
				} catch (e) {
					//no Json found in the file
					jsonTree = ''
				}
 			}
		} else {	
			byte[] jsonTreeDecoded = jsonTree.decodeBase64()
			jsonTree = new String(jsonTreeDecoded)
		}
		
		return [ nameTree: nameTree, jsonTree: jsonTree, serverUrl: "${config.grails.serverURL}" ]
	}
}
