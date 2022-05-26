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

- pour se connecter à une BDD, on a besoin d'une **chaîne de connexion** qui est propre à chaque éditeur. On y retrouve le mot *jdbc* suivi du nom de l'éditeur, puis l'adresse IP/nom de domaine de la machine, le port d'écoute, puis le nom de la base de données
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

## Envoie d'une requête SQL et analyse du résultat 

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

## Passer un paramètre à la requête

- possibilité de concaténer dans la requête SQL un paramètre
- il ne faut JAMAIS LE FAIRE car cela est une faille de sécurité connue sous le nom d'**injection SQL**
- il faut utiliser pour cela un **PreparedStatement**

```java
String requete = "select * from user where name = '" + name + "';";
ResultSet rs = statement.executeQuery(requete);
```

----

## Créer un PreparedStatement sur une requête SQL simple

- un **PreparedStatement** ressemble beaucoup à un **Statement**, il est aussi créé à partir de l'objet *Connection*
- la première différence est qu'il faut la requête SQL pour créer le *PreparedStatement*. Il est construit sur une requête fixé à la création du PreparedStatement
- il existe 3 méthodes analogues au *Statement* : **executeQuery()**, **executeUpdate()** et **execute()**
- la différence principale est que la requête peut être paramétrée pour fixer la valeur des paramètres

```java
PrepapedStatement ps = connection.prepareStatement("select * from user");
ResultSet rs = ps.executeQuery();
```

----

## Créer une requête SQL paramétrée avec PreparedStatement

- utilisation du point d'interrogation à la place de la concaténation, qui est un paramètre de la requête
- remplissage du/des paramètre(s) avec les méthodes **setString()**... en renseignant le numéro du paramètre (le premier est 1) et la valeur du paramètre
- éléments de sécurité mis en place par JDBC pour qu'il n'y ait pas d'injections SQL
- le PreparedStatement est réutilisable avec d'autres valeurs de paramètres

```java
PrepapedStatement ps = connection.prepareStatement("select * from user where name = ?");
ps.setString(1, "Paul");
ResultSet rs = ps.executeQuery();

ps.setString(1, "Ines");
rs = ps.executeQuery();
```

----

## Comprendre l'intérêt des requêtes en Batch

- lorsqu'on fait une requête sur une base de données depuis notre application Java, le temps de transit de la requête vers de la base de données, et de la base de données vers l'application, surtout si le résultat est volumineux
- il faut éviter d'avoir trop d'aller/retour entre les 2 entités, notamment lors d'une mise à jour de nombreuses lignes, il faut limiter le nombre de requêtes pour être performant
- on va pouvoir utiliser le mode **batch** pour envoyer toutes les requêtes d'un seul coup et récupérer les résultats en un seul retour
- il existe une méthode **addBatch()** pour ajouter une requête à exécuter et une méthode **executeBatch()** pour exécuter toutes les requêtes

```java
PrepapedStatement ps = ...
ps.setString(1, "Paul");
ps.addBatch();
ps.setString(1, "Ines");
ps.addBatch();
int[] update = ps.executeBatch();
```

----

## Analyser un ResultSet simple

- pour récupérer les valeurs des colonnes d'un ResultSet, il existe toute une collection de méthode : **getString()**, **getLong()**, **getInt()**
- on peut donner le numéro de la colonne ou le nom de la colonne. Il ne faut pas utiliser le numéro si `select *` est utilisé car on ne peut pas prédire l'ordre des colonnes


```java
while(rs.next()){
    String name = rs.getString("name");
}
```

----

## Analyser le contenu d'un ResultSet avec ResultSetMetaData

- méthode **getMetaData()** renvoie un objet **ResultSetMetaData** qui a plusieurs méthodes

```java
ResultSetMetaData md = rs.getMetaData();
int nbCol = md.getColumnCount(); // nombre de colonnes dans le RS
String columnName = md.getColumnName(index); // nom de la colonne à l'index i
int columnType = md.getColumnType(index); // type SQL de la colonne à l'index i : CHAR, VARCHAR, INT...
```

----

## Naviguer dans un ResultSet, répétabilité des données ou non

- **previous()**, au contraire de *next()* permet de remonter dans le ResultSet. Toutes les implémentations ne supportent pas cette méthode
- pour savoir si c'est le cas, la méthode **getType()** permet de le savoir. Elle prend 3 valeurs :
    - **TYPE_FORWARD_ONLY** : la méthode *previous* ne fonctionne pas
    - **TYPE_SCROLL_INSENSITIVE** : la méthode *previous* fonctionne et le RS est insensible aux éventuelles modifications de la BDD pendant qu'on analyse le ResultSet, lorsqu'on fait un *previous*, on aura le même résultat que lorsqu'on est passé sur cette ligne avant
    - **TYPE_SCROLL_SENSITIVE** : la méthode *previous* fonctionne et le ResultSet est sensible aux modifications faites dans la base

----

## Mettre à jour des données au travers d'un ResultSet

- lorsqu'on est sur une ligne du ResulSet, il est possible de mettre à jour certaines valeurs des colonnes
- la fonctionnalité n'est pas systématique, il faut utiliser la méthode **getConcurrency()** pour savoir si cette fonctionnalité est supportée. **CONCUR_READ_ONLY** (pas de mise à jour possible) et **CONCUR_UPDATABLE**

----

## Gérer des dates avec les ResultSet

- il existe 3 types de dates en SQL qui se translatent en 3 objets Java de l'API JDBC : **Date** (par exemple 15/06/2001), **Time** ( par exemple 14h45) et **TimeStamp** (par exemple 15/06/2001 14h45)
- il existe 3 méthodes *get* dans le ResultSet pour chacun des types **getDate()**, **getTime()** et **getTimeStamp()**
- à partir de Java 8, arrivée de l'API Java Time : **LocalDate**, **LocalTime** et **LocalDateTime**
- possibilité de passer de l'un à l'autre avec des méthodes faites pour

```java
LocalDate localDate = LocalDate.of(2001, Month.APRIL, 17);
LocalTime localTime = LocalTime.of(14, 45, 0);
LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
// passer des dates Java 8 aux dates JDBC
Date d = Date.valueOf(localDate);
Time t = Time.valueOf(localTime);
Timestamp ts = Timestamp.valueOf(localDateTime);
// passer des dates JDBC aux dates Java 8
LocalDate ld = date.toLocalDate();
LocalTime lt = time.toTime();
LocalDateTime ldt = timestamp.toLocalDateTime();
```

----

## Fermeture des Connection, Statement et ResultSet

- les objets que l'on a vu (Connection, Statement, PreparedStatement, ResultSet) sont des ressources systèmes. Lorsqu'on ouvre une ressource système, il faut la fermer
- possibilité de les fermer automatiquement avec le pattern *try-with-resources* à partir de Java 7 car ils étendent l'interface **AutoClosable**
- lors de la fermeture d'une connexion, les Statement, PreparedStatement, ResultSet créé à partir de cette connexion sont égalements fermés
- si on garde la connexion ouverte tout le long de la vie de l'application, il faut ferme les Statement et les ResultSet car on ne peut en avoir qu'un certain nombre ouvert à un instant donné

----

## Transactions ACID pour les bases de données relationnelles

- les transactions dans les bases SQL sont caractérisées par l'acronyme **ACID**. C'est un ensemble d'opérations de modifications en base de données
- exemple du transfert d'argent entre un compte 1 et un compte 2. Il y a 2 opérations : retrait du compte 1 et ajout sur le compte 2
- **A comme Atomicité** ou indivisibilité. L'ajout ne doit pas avoir lieu si le retrait échoue
- **C comme Cohérence** respect des contraintes d'intégrité (contraintes de clés primaire et clés étrangères). Pendant la transaction, il se peut qu'il y ait une incohérence mais elle doit avoir disparue à la fin de la transaction
- **I comme Isolation** comment les modifications faites dans une transaction sont vues dans une autre transaction
- **D comme Durabilité** les données ne sont pas volatiles, elles persistent au redémarrage de la base de données ou de la machine hôte

----

## Gérer une transaction manuellement avec JDBC

- en JDBC, les transactions sont gérées au niveau de la connexion, avec l'autoCommit activée. Cela veut dire que le commit va être réalisé à la fin de chaque requête
- pour changer ce mécanisme, il faut utiliser la méthode **setAutoCommit(boolen)**, testable avec la méthode **isAutoCommit()** de l'objet Connection
- pour gérer les transactions manuellement, il faudra utiliser les méthodes **commit()** et **rollback()** de l'objet Connection

----

## Les niveaux d'isolation des transactions

il existe différents niveaux d'isolation des transactions :
- **NONE** : pas de transaction
- **READ_UNCOMMITED** : les insertions d'une transactions T1 sont vues par T2 avant le commit de T1. Risque de **dirty reads**
- **READ COMMITTED** : protection contre les *dirty reads*. T2 ne verra pas les insertions de T1 tant qu'il n'aura pas commité. Cependant, les *UPDATE* ne sont pas protégés entre les différentes transactions même avant le commit. On parle de **non repetable reads**
- **REPEATABLE READ** : protection contre les *non repetable reads*. Il reste une opération que T1 peut faire qui sera vue par T2 : lors d'insertions par T1, si T2 fait un select sur des plages d'objets (select between), il peut voir les données insérées par T1 avant son commit. On parle de **phantom reads**
- **SERIALIZABLE** : protection contre les *phantom reads*. Tout se passe comme si aucune transaction n'avait lieu en même temps qu'une autre

----

## Isolation des transaction en JDBC

- sur l'objet **Connection**, il y a la méthode **getTransactionIsolation()** qui retourne le niveau d'isolation
- la méthode **setTransactionIsolation(...)** permet de changer le niveau de l'isolation
- tous les drivers JDBC ne gèrent pas tous les niveaux
- il existe un niveau par défaut pour chaque driver
- plus le niveau d'isolation est élevé, plus il y a de risques de performance sur la BDD


----

## DataSource

- alternative au `DriverManager` pour créer des connexions à une BDD
- utilisation de la bibliothèque **HikariCP**

```java
public DataSource getDataSource(){
    HikariDataSource ds = new HikariDataSource();
    ds.setJdbcUrl("jdbc:mysql://localhost:3306/simpsons");
    ds.setUsername("bart");
    ds.setPassword("51mp50n");
    return ds;
}

DataSource ds = getDataSource();
Connection connection = ds.getConnection();
...
connection.close();
```
