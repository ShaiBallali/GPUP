# G.P.U.P
Generic Platform for Utilizing Processes

GPUP is a multi-threaded platform that enables to model set of dependencies between components and handle them efficiently.

Insights: The platform enables getting various insights out of the ‘graph’ components: routes, circles, transitive dependencies etc.

Execution: The platform enables running various tasks on the graph’s nodes. It follows the Open-Close principle and can be extended with various tasks in the future. Tasks can be, for example: compilation task/testing task. Execution is done in parallel to maximize the efficiency of processing. It can be on part of the graph or only on the failed nodes from previous execution.

Distribution: The platform’s architecture enables distributed (remote) workers to connect to it and execute work with their private resources.

Running the application:

First, please make sure to copy “gpup.war” into your local Tomcat/webapps folder. The program is divided into 2 types of users (separate applications):

Admin:

The admin application starts with a login page, where a name should be selected. Duplicate names are forbidden. (Enforced with session ID)

Operations the admin can make:

• Loading XML files into the system (several XML examples are attached)

• The admin can choose any uploaded graph and get insights on it

• Execution: Execution describes performing a task (for example, compilation) on a selected graph. Each task gets a price from the XML’s price list. The admin will be able to create such executions.

• Within the Control-Panel screen, the admin will be able to see live updates on self-made execution’s progress (which will be done by workers). He can “Play” (start), “Resume” or “Stop” (terminate) the execution through that screen. When stopping an execution, workers will not be able to continue working for that execution.

• The admin can navigate through the application using his unique navigation bar.

Worker:

The worker application starts with a login page as well, with one difference from the admin’s login page: The user should also select the number of threads he wants to dedicate to the current worker.

Operations the worker can make:

• In his “Dashboard” page, the worker will be able to “Register” to an execution an admin published (not only created, but also pressed “Play”). Meaning, from this moment onwards, the worker starts dedicating threads to that execution, and is getting paid for each target he completes. (Prices are part of the XML details).

• A worker can also decide to stop applying resources to an execution, using his own “Control-Panel”.

General Details:

A lot of the tables are updated dynamically, using the “pull” method; Every fixed number of milliseconds, the UI asks for information from the server.

“Graphviz” feature will work only for users with Graphviz software installed.

The Compilation task uses a built-in Java process.

Topological sort was implemented in the engine.

To transfer data from the UI to the server vice-versa, Google’s Gson was used.

Http calls were implemented with OkHttp.
