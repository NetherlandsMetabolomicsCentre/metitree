package nl.nmc.metitree

class Usergroup {
	
	static hasMany = [ members:Member, databases:Database, directories:Directory ]
	
	static mapping = {
		members(lazy:false)
		databases(lazy:false)
		directories(lazy:false)
	}
	
	String		name
	String		website
	String		address
	Date		dateCreated
	Date		lastUpdated

    static constraints = {
		website(nullable: true)
		address(nullable: true)
    }
	
	String toString() { "${this.name}" }
}
