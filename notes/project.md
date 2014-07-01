Project Goals
------

Khoj is yet another experiment at code visualization. Read more about the initial thought at my [blog](http://rrevo.github.io/2014/06/10/java-program-graph.html)

The data-model for khoj is not graph based. The initial implementation uses neo4j. Using a graph that is no-sql based will allow for incremental changes to the model.

A visualization UI needs to be created as well.

Core project
--------------

The core project will act as a layer over the model. Operations like searching for classes, adding methods will be covered here. An important role of the project is around managing how partial model information gets merged. Like incremental information about class files can be persisted even though the actual class/type is not known. Later this infered model can be updated with the actual information. The state transitions of different models will have to be managed.

Parser project
--------------
 
Initially scan class files to load information. The parser will have to scale to jars and also become maven aware. Based on source, it should be possible to create a model that is always evolving. This is required for a case where creating visualizations of live programming.
