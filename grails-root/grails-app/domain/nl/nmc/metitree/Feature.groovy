package nl.nmc.metitree

class Feature {
		
	String name
	
	static hasMany = [treefeatures: TreeFeature]

    static constraints = {
    }
	
	def trees() {
		return treefeatures.collect{it.tree}
	}
	
	List addToTrees(Tree tree) {
		TreeFeature.link(this, tree)
		return trees()
	}

	List removeFromTrees(Tree tree) {
		TreeFeature.unlink(this, tree)
		return trees()
	}
}
