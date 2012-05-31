package nl.nmc.metitree

class TreeFeature {
	
	Tree	tree
	Feature feature
	
	static belongsTo = [tree:Tree]
	
    static constraints = {
    }
	
	static TreeFeature link(feature, tree) {
		def m = TreeFeature.findByFeatureAndTree(feature, tree)
		if (!m)
		{
			m = new TreeFeature()
			feature?.addToTreefeatures(m)
			tree?.addToTreefeatures(m)
			m.save()
		}
		return m
	}

	static void unlink(feature, tree) {
		def m = TreeFeature.findByFeatureAndTree(feature, tree)
		if (m)
		{
			feature?.removeFromTreefeatures(m)
			tree?.removeFromTreefeatures(m)
			m.delete()
		}
	}
}