package nl.nmc.metitree

class Tree {
	
	Msnfile msnfile
	String name
	
	static hasMany = [treefeatures: TreeFeature]

    static constraints = {
    }
	
	def features() {
		return treefeatures.collect{it.feature}
	}
	
	def topSimilarTrees(){

		def similarityScore = [:]
		
		Tree.list().each { tree ->
			similarityScore[tree] = this.similarTo(tree)
		}
							
		return similarityScore	
	}
	
	def similarTo(Tree tree) {
		
		def simScore = 0
		
		if (this.features()?.size() && tree?.features()?.size()){
		
			def featuresA = this.features().size()
			def featuresB = tree.features().size()
					
			def numberOfFeaturesInCommon = (this.features().intersect(tree.features())).size()
			
			simScore = numberOfFeaturesInCommon / (featuresA + featuresB - numberOfFeaturesInCommon)
		}
		
		return simScore
	}
	
	List addToFeatures(Feature feature) {
		TreeFeature.link(feature, this)
		return features()
	}
	
	List removeFromFeatures(Feature feature) {
		TreeFeature.unlink(feature, this)
		return features()
	}	
}
