# Lemming

This web app allows to annotate _Keyword in Context_ indices ([KWIC](https://en.wikipedia.org/wiki/Key_Word_in_Context))
with lemmata, senses and parts of speech in an effective way.

## Documentation

* [API documentation](https://mhusar.github.io/lemming/apidocs/)

## Getting started

* [OpenJDK 8](http://openjdk.java.net/)
* [Maven](https://maven.apache.org/)
* [MariaDB](https://mariadb.org/)
* [Jetty](https://www.eclipse.org/jetty/)

### Database configuration

Configure properties for the JAVA Persistence API ([JPA](https://en.wikipedia.org/wiki/Java_Persistence_API)) from the
profiles section in the POM ([pom.xml](https://github.com/mhusar/lemming/blob/master/pom.xml)).

Set at least values for `db.username`, `db.password`, `db.url` and `db.hbm2ddl.auto` for the development profile and
configure your database accordingly.

```xml
<profile>
    <id>development</id>
    <properties>
        <environment>development</environment>
        <wicket.configuration>development</wicket.configuration>
        <db.username>lemming</db.username>
        <db.password></db.password>
        <db.url>jdbc:mysql://localhost/lemming</db.url>
        <db.hbm2ddl.auto>update</db.hbm2ddl.auto>
        ...
    </properties>
    ...
</profile>
```
### Installation

There is one local dependency for a Git submodule of diff-match-patch.

```
cd submodule/diff-match-patch
git checkout pom.xml
mvn clean package deploy
cd ../..
```

Just execute the following commands in the terminal. Maven downloads all additional dependencies automatically.
During first startup some supplied data is written to the database
([lemma.json](https://github.com/mhusar/lemming/blob/master/src/main/webapp/WEB-INF/json/lemma.json) and
[pos.json](https://github.com/mhusar/lemming/blob/master/src/main/webapp/WEB-INF/json/pos.json)). This will take a few
minutes to complete. Besides a default user is created.

**Username**: admin, **Password**: admin

#### Development

This command starts an embedded Jetty application server. After startup one can access the app on
[localhost:8080](http://localhost:8080).

`mvn clean compile package jetty:run-war -Denvironment=development`

#### Deployment

During the package phase a [WAR](https://en.wikipedia.org/wiki/WAR_(file_format)) file is created. Put it in Jetty’s
`$JETTY_HOME/webapps` directory as `root.war`. 

`mvn clean compile package -Denvironment=deployment`

## License

Lemming is available under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt).
