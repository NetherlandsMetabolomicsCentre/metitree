dataSource {
	pooled = true
	driverClassName = "org.hsqldb.jdbcDriver"
	username = "sa"
	password = ""
	dbCreate = "create-drop"
	url = "jdbc:hsqldb:mem:metitreeDB"
}
hibernate {
    cache.use_second_level_cache=true
    cache.use_query_cache=true
    cache.provider_class='net.sf.ehcache.hibernate.EhCacheProvider'
	flush.mode='commit' // this should speed-up the program
}
