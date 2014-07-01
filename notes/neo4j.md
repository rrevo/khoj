Neoj
----

[Neo4j](http://neo4j.org/) is a property graph implementation in Java. Khoj uses neo4j as the primary graph data store. View the [manual](http://docs.neo4j.org/chunked/stable/introduction.html)

Various java elements like packages, classes, methods will be nodes. The links between them are the relationship names. These are currently structure based, like class extends class. There might be other types of relationships in the future. Properties on the nodes are used for other bits like access controls.

Labels were not working as expected and so a default property called type was added. This is the java class name corresponding to tht enode.

Editions
-------
The main editions are client-server for larger installations and In-JVM for simpler applications. An in memory implementation for testing is also present.

Shell
----
To connect to q running neo4j instace use the neo4j shell. 

Cypher
------

Cypher is the query language for neo4j. The current version of the syntax is 2.x. The best reference for the syntax is the following [cheatsheet](http://docs.neo4j.org/refcard/2.1/). There are various examples floating around with the older versions which will throw exceptions when executed.

Example Cypher queries
----------------------

Get all nodes

* `match n return n;`

  