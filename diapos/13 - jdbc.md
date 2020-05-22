# JDBC

----

## Introduction

- JDBC, pour **Java Database Connectivity**, est une API du JDK qui permet à une application Java d'interagir de façon standard avec des données enregistrées dans une base de données relationnelles (via le langage SQL)
- pour interagir avec les bases NoSQL, il existe d'autres API
- pour interagir avec une base, il faut utiliser un module appelé **driver JDBC** spécifique à la base de données (MySQL, Postgre...) qui sont des JAR
- JDBC contient essentiellement des interfaces (Connection, Statement, PreparedStatement, ResultSet...), les implémentations sont fournies par les éditeurs de base de données dans leur driver

----

## Fonctionnement général de JDBC

- **Connection** : connection à une BDD (URL, username, password)
- **Statement** et **PreparedStatement** permettent de transporter des requêtes SQL vers la base de données
- une fois les requêtes exécutées en base, on peut récupérer le résultat dans un objet **ResultSet**, correspondant à une table en base de données avec des lignes et des colonnes

----

## Connexion à une base de données

- pour se connecter à une BDD, on a besoin d'une **chaîne de connexion** qui est propre à chaque éditeur. On y retrouve le mot *jdbc* suivi deu nom de l'éditeur, puis l'adresse IP/nom de domaine de la machine, le port d'écoute, puis le nom de la base de données
- **DriverManager** (classe fournie par JDBC) va regarder si l'application possède le pilote JDBC correspondant à la BDD dans son classpath, et interroger ce pilote et lui demander de construire une instance de l'interface **Connection**
- le code peut compiler même si on a omis le driver JDBC. A l'exécution, on aura alors une erreur

```java
String connectionString = "jdbc:mysql://127.0.0.1:3306/db";
Connection connection = DriverManager.getConnection(connectionString, username, password);
```

----

## Chargement des drivers JDBC (1)

- le DriverManager va savoir qu'il possède sur son classpath un driver MySQL qu'il a besoin pour instancier la connexion à la base MySQL grâce au mécanisme de **Service Loader** introduit en Java 6
- dans le JAR du driver MySQL, il y a un répertoire **META-INF** qui contient des métadonnées sur les classes qui se trouvent dans le JAR. Dans ce dossier, il y a un répertoire standard appelé **services** utilisé par le ServiceLoader du JDK. Il contient un fichier texte **java.jdbc.Driver**, qui correspond au nom complet de l'interface Driver de JDBC. Ce fichier texte va contenir par convention les noms des classes du driver qui implémente l'interface *java.jdbc.Driver*. Ici il y a la classe **com.mysql.jdbc.Driver**
- quand la machine Java va ouvrir ce JAR et voir les fichiers dans services, elle va enregistrer l'information que pour l'interface Driver, il y a cette classe d'implémentation qui existe. Et cette classe, grâce à un mécanisme particulier, va se déclarer auprès du DriverManager de JDBC comme étant un pilote MySQL

----

## Chargement des drivers JDBC (2)

Jusqu'en Java 5, il est nécessaire de charger la classe qui implémente le driver manuellement avec l'API Reflection

```java
// récupération de la classe Driver dans mysql, une exception sera jeté si le JAR est absent du classpath
Class driverClass = Class.forName("com.mysql.jdbc.Driver");
// instanciation de la classe en objet Driver qui est l'interface Driver de JDBC
driverClass.newInstance(); // jusqu'en Java 8
Driver mySqlDriver = driverClass.getConstructor().newInstance(); // à partir de Java 9 (lié à la sécurité dans l'API Réflection)
// enregistrement du driver auprès du DriverManager
DriverManager.registerDriver(mySqlDriver);
```

----

## Envoie d'une requêtes SQL et analyse du résultat 

- la méthode **createStatement()** permet de créer un objet **Statement** qui va permettre d'exécuter une requête SQL avec la méthode **executeQuery(requete)**
- récupération du résultat dans un objet **ResultSet**
- possibilité d'avoir une **SQLException** s'il y a une erreur dans la requête
- pour lire le résultat, il faut utiliser la méthode **next()** de *ResultSet*. Elle renvoie un booléen pour savoir s'il y a encore des données à lire dans *ResultSet* et avance le pointeur vers la première ligne du résultat (la ligne suivante ensuite)
- pour lire les colonnes, il faut utiliser les méthodes **getInt(nomCol)**, **getString(nomCol)**...

```java
Statement statement = connection.createStatement();
ResultSet rs = statement.executeQuery("select id, name from user");
while(rs.next()){
    int id = rs.getInt("id");
    String nom = rs.getString("name");
}
```

----

## Les méthodes de Statement
- la méthode **executeQuery(requete)** retourne un objet ResultSet
- la méthode **executeUpdate(requete)** retourne un entier correspondant au nombre d'enregistrements affectés par la requête (les requêtes UPDATE, DELETE)
- la méthode **execute(requete)** pour les requêtes de type SELECT et de MISE A JOUR. Elle renvoie un booléen (true pour requête de type SELECT, false si de type UPDATE). Possibilité de récupérer un ResultSet ou un entier pour le nombre de lignes modifiées

```java
Statement statement = connection.createStatement();
boolean select = statement.execute("requete");
if(select){
    ResultSet rs = statement.getResultSet();
} else {
    int update = statement.getUpdateCount();
}
```

----

## Passer un paramètre à la requête dans une clause where

- possibilité de concaténer dans la requête SQL un paramètre
- il ne faut JAMAIS LE FAIRE car cela est une faille de sécurité connue sous le nom d'**injection SQL**
- il faut utiliser pour cela un **PreparedStatement**

```java
String requete = "select * from user where name = '" + name + "';";
ResultSet rs = statement.executeQuery(requete);
```
