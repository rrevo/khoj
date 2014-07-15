## Random thoughts and learnings

### Relationships

In the core service, I started of with Packages, Classes and Methods as nodes. The Clazz node has a Set of Methods for methods that are part of a Class.

When I get a Clazz to perist, the representation needs to be merged with the already peristed model. Now with Methods as part of the Clazz this becomes complicated.

At the same time there are many other relationships between these nodes. I do not see Class-has-methods as any special. Optimizations per use-case can be done separately.

So at a later point in time I will remove the field of methods and have it just as a Clazz to Method relationship that clients will need to explicitly maintain. This makes merge of single nodes easier.

Also right now the plan for merge is to add or update properties in the provided model and not delete anything. This is to allow for incremental updates.

### Data Providers

The Parser project inputs a class file (or jar) and convert that into the core model. However the Parser can be thought of as a single Data Provider. Another Data Provider is one that can create the core model by reading actual source files; or how about the Onyem runtime information.

All the Data Providers can add different dimensions to the data. Like source can add javadoc or variable names.

### Event Bus for various parsing events

An event bus is also needed for working on events raised by the Data Providers. For example for optimization we can copy method links from a super class to an optimization. These can be calculated after the super class parsing is complete.
