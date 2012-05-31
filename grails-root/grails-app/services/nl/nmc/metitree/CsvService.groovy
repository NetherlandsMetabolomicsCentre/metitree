package nl.nmc.metitree

import grails.converters.*

class CsvService {

    static transactional = true

	def csvFromCML(jsonMasterMsnFile, csvMsnFile) {
		
		def csvFile = new File(csvMsnFile.location)
		def jsonMasterTreeFile = new File("${jsonMasterMsnFile.location}")
		
		csvFile.append("\"tree\",\"node\",\"parent\",\"msLevel\",\"mass\",\"intensity\",\"EF\",\"EF_loss\",\"inchi\",\"inchiloss\"\n" as String)
		
		JSON.parse(jsonMasterTreeFile.text).each { group, nodes ->
			nodes.sort().each { nodeID, node ->
				
				def ec = ""
				if(node.inchi.split("/").length > 1)
					ec = node.inchi.split("/")[1]
				def ec_loss = ""
				if(node.inchiloss.split("/").length > 1)
					ec_loss = node.inchiloss.split("/")[1]
				def msLevel = extractPrecursors(nodeID,nodes).size()
				csvFile.append("\"${node.group}\",\"${node.id}\",\"${node.parent}\",\"${msLevel}\",${node.mass},${node.intensity},\"${ec}\",\"${ec_loss}\",\"${node.inchi}\",\"${node.inchiloss}\"\n" as String)
			}
		}	
	}
	
    def tabFromCML(jsonMasterMsnFile, tabMsnFile) {
		
		def tabFile = new File(tabMsnFile.location)
		def jsonMasterTreeFile = new File("${jsonMasterMsnFile.location}")
		
		tabFile.append("tree\tnode\tparent\tmsLevel\tmass\tintensity\tinchi\tinchiloss\n" as String)
		
		JSON.parse(jsonMasterTreeFile.text).each { group, nodes ->
			nodes.sort().each { nodeID, node ->
				tabFile.append("${group}\t${node.id}\t${node.parent}\t${node.mass}\t${node.intensity}\t${node.inchi}\t${node.inchiloss}\n" as String)
			}
		}
		
    }
	public static List<Integer> extractPrecursors(def nodeIDTo, def nodes) {

		def precurLS = new ArrayList<Integer>();
		nodes.sort().each { nodeID, node ->
			if(nodeIDTo==nodeID){
				precurLS.add(nodeIDTo);
				List<Double> precurL = extractPrecursors(node.parent,nodes);
				precurLS.addAll(precurL);
			}
		}
		return precurLS;
	}
}
