bulk fcrepo import tool
=======================
Java analog of the active C# application [ladybird] (http://ladybird.library.yale.edu/) at Yale University. 
The project began in 2014.

### Installation

```
mvn clean install
cd bfit-webapp
mvn cargo:run
```

Some tests that rely on access to the library's OAI-PMH server might fail. Please set up your VPN before doing a build.

The webapp is launched at: http://localhost:8080/bfit-webapp. You should see something like:

![ladybird](lb2.png)

The application requires CAS credentials.

### Deployment

The application comes with an embedded Tomcat 7 and an in-memory relational database. The generated .war file should ideally be placed
in a regular servlet container, however. Similarly MySQL should be used as the data store for Hibernate.

### Limitations

The application does not currently publish to Fedora. The project is currently on hold.

### Authors

Lead Developer: Osman Din
