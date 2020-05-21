# Java EE

----

## Introduction

- Java SE (Standard Edition)
- Java EE (Enterprise Edition) ajoute des API pour gérer le Web et les bases de données. C'est une surcouche de Java SE
- Historique de Java
    - Java SE : 1995-96
    - Java 2 : 1998 (première version vraiment utilisable de Java)
    - Java J2EE : 1999 (première version Java EE)
    - J2EE 1.2 ... J2EE 1.4, Java EE 5 ... Java EE 8 (septembre 2017). Maintenant géré par la fondation Eclipse, renommé en Jakarta EE 8 (en septembre 2019)

----

## Spécifications Java EE

- Java EE est un jeu de spécifications
    - chaque spécification est précisé dans un document appelé **JSR** (Java Specification Request). Il y en a environ 20 dans Java EE 8
    - chaque JSR est associée à un Expert Group (**EC**)
    - l'EC doit fournir une Reference Implementation (**RI**). Par exemple, la RI de JPA est EclipseLink
    - Java EE est lui-même spécificié dans une JSR qui liste les JSR avec leur version qui font une version de Java EE. Il a aussi une implémentation de référence, c'est un serveur d'application nommé **Glassfish**

----

## Les serveurs d'applications

A partir de 2009, Java EE définit 2 profils :
- **WebProfile**
    - regroupe les Servlet, JSF, JSP
    - 2 serveurs d'application qui implémentent le WebProfile : **Tomcat** et **Jetty**
- **FulProfile** : regroupe l'ensemble des composants d'un serveur Java EE
    - **Glassfish** implémentation de référence libre
    - **Weblogic** commercialisé par Oracle
    - **Webshpere** commercialisé par IBM
    - **JBoss EAP** en version payante par RedHat, ou **Wild Fly** en version gratuite
    - **Payara** qui est un fork de *Glassfish* commercialisé
    - **TomEE** opensource, commercialisé par Tomitribe

----

## Architecture de Java EE

- JPA : *Java Persistance API* gère le mapping objet relationnel
- JTA : *Java Transaction API*, gère les transactions avec la base de données
- JMS : *Java Message Service* permet de faire de la messagerie
- EJB : *Entreprise Java Bean* plutôt obsolète à cause de CDI
- CDI : *Context and Dependency Injection*
- API Servlet : pour gérer le web
- JSP : *Java Server Page*
- JSF : *Java Server Face*, une évolution de JSP
- EL : *Expression Language* utilisé des objets dans les JSP/JSF
- JAX-WS et JAW-RS : permettent de servir des données sur le web
    - JAX-WS : *Java API for XML Web Services*, implémente **SOAP**
    - JAX-RS : *Java API for RESTful Web Services*, implémente **REST**

----

## Microprofile

- une spécification alternative à Java EE est apparue avec l'arrivée des microservices, appelée **Microprofile**, essentiellement du service de données au format JSON.
- on y retrouve les spécifications **JSON-B** (Binding) et **JSON-P** (Processing), la spécification JAX-RS, et CDI, le moteur d'injection de dépendance de Java EE. Ainsi que d'autres spécifications ajoutés par Microprofile
- certains serveurs d'applications sont certifiés microprofile : **Payara** (à la fois serveur JAva EE et Microprofile), **Halidum**....

----

## Différence entre Java EE et Spring

- Spring est un framework Java, concurrent technique de Java EE
- c'est un ensemble de composant Java, qui fait les choses un peu différemment
- on peut faire du Spring dans un serveur Java EE
- Spring est développé par la société *Pivotal*, produit opensource mais propriétaire

----

## Traitement d'une requête HTTP

lorsqu'une requête HTTP arrive sur un serveur, l'URL est analysé. Par exemple, l'URL `http://www.data.gouv.fr/stats/maires/liste`
- en commençant par le **nom de domaine**, résolu par un DNS (serveur de noms)
    - correspond à `gouv.fr`
    - le DNS va envoyer la requête vers la bonne adresse IP
- sur cette adresse IP, un serveur web écoute sur le port 80 par défaut
- le serveur web va s'intéresser au prefixe de l'URL : `www.data`
    - un serveur web est capable de gérer des zones virtuelles qui correspond au prefixe au nom de domaine
    - en fonction de la requête, le serveur va traiter la requête en local ou la rediriger avec un autre serveur de l'infrastructure web du domaine
- ensuite, on s'intéresse à la partie **ressources**, partie à droite du nom de domaine
    - correspond soit à des répertoires, soit à des applications web
    - les applications Web sont géré dans un serveur Java EE comme Tomcat
    - le serveur d'applications peut gérer plusieurs applications web et aiguiller vers la bonne application en fonction de l'URL
    - à l'intérieur de l'application web, il existe différentes ressources
        - ressources statiques : ressources servies telles quel (fichiers html, css, js, images...)
        - ressources dynamiques : ressources calculées

----

## Générer du Contenu Dynamique avec des Servlets

- l'API Servlet est une classe Java qui permet de mapper une URI et avec une classe Java auprès de Tomcat
- il n'y a pas de méthode *main()* dans les servlets, c'est Tomcat qui va déclencher l'exécution des servlets
- la méthode *main()* se trouve dans Tomcat pour démarrer l'application
- pour créer une servlet, il faut étendre la classe **HttpServlet** et mapper l'URL avec l'annotation **@WebServlet**
- Tomcat va construire une instance de **HttpServletRequest** et de **HttpServletResponse** et les fournir à la servlet

```java
@WebServlet("/hello-world")
public class HelloWordServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ... {
        // accès à toutes les caractéristiques de la requête dans l'objet HttpServletRequest
        // écriture de la réponse en manipulant l'objet HttpServletResponse, notamment via la méthode getWriter()
        PrintWriter pw = response.getWriter();
        pw.write("Hello World");
    }
}
```

----

## Rediriger une requête d'une Servlet vers une autre

```java
User user = ...
request.setAttribute("user", user);
request.getRequestDispatcher("tempalte.jsp").forward(request, response);
```

----

## Utiliser les cookies pour créer des sessions en HTTP

- le protocole HTTP est déconnecté : pas de notion de session
- les **cookies**, attaché au protocole HTTP, permet de créer la notion de session
- c'est un petit fichier texte de quelques Ko au maximum
- c'est un fichier versionné, avec une date de péremption
- un cookie à le domaine qui a émis le cookie, et des informations optionnels comme un ID
- lors d'une requête vers un serveur, ce serveur renvoie la réponse, par exemple un page HTML et des cookies qui vont être enregistré par mon navigateur sur le disque dur
- lors d'une autre requête vers le même serveur, le navigateurr va regarder dans tous les cookies enregistrés et va sélectionner les cookies correspondant au nom de domaine et les envoyer en même temps que la requête. En recevant les cookies, le serveur reçoit l'ID de l'utilisateur ce qui permet de faire le lien avec les requêtes précédentes

----

## Gérer les sessions avec l'API Servlet

- `HttpSession session = request.getSession();` permet d'avoir une même session pour les différentes requêtes faites par un même utilisateur
- `session.setAttribute(key, value);` permet d'enregistrer des informations que l'on récupère dans une autre requête en faisant `session.getAttribute(key);`
- possibilité de gérer les cookies de manière explicite

```java
Cookie cookie = ...
response.addCookie(cookie) ;
request.getCookies();
```

----

## Echanger des données XML avec SOAP

- **SOAP**, pour *Simple Object Access Protocol*, permet de faire des requêtes sur des serveurs et récupérer des données au format XML
- développé en 98-99, devient une norme du W3C en 2003
- l'idée est de séparer le transfert des données de la présentation des données
- indépendant du langage de programmation et du protocole (fonctionne en HTTP, STMP, TCP, UDP)
- le fait d'être indépendant du protocole fait qu'on ne peut pas profiter ce qu'offre le protocole : par exemple toutes les requêtes faites en HTTP sont faites en POST
- l'intégralité des informations de la requête sont dans le XML
- un incovéniant de SOAP est qu'il est très verbeux car XML produit beaucoup de méta-données
- protocole legacy, REST est utilisé pour les nouveaux web services

----

## Echanger des données avec REST

- REST, pour *Representational State Transfert*, présenté par Roy Fielding dans sa thèse en 2000
- comme SOAP, l'idée est de transférer des données sur internet au format XML (aujourd'hui davantage JSON)
- le protocole HTTP s'étend imposé, REST est basé sur ce protocole et profite de ce que sait faire HTTP ce qui permet de générer des requêtes beaucoup plus légères
- REST s'appuie sur les verbes HTTP et les URI pour faire les requêtes, par exemple une requête GET `commune/12` pour obtenir la commune avec l'id 12
- GET permet de récupérer une donnée, POST créer une donnée, PUT modifier une donnée existante, DELETE supprimer une donnée, PATCH permet de modifier une partie d'un objet
- possibilité d'utiliser l'API Servlet avec les méthodes *doGet()*, *doPost()*... mais la spécification  **JAX-RS** permet de faire simplement des services REST

----

## Ecrire une premier service REST

- JAX-RS fournit une servlet qui gère toute la partie technique

```java
@ApplicationPath("rest")
public class RestService extends Application { // javax.ws.rs
}

@Path("hello-world")
public class HelloWorldRS {
    @GET
    public String helloWorld(){
        return "Hello World";
    }
}
```

----

## Implémenter un service REST GET

- renvoie un objet Java au format XML ou JSON. Possibilité de configurer avec `@Produces`

```java
@Path("commune")
public class Commune {
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON) // MediaType.TEXT_XML
    public Commune findById(@PathParam("id") long id){
        Commune c = ...
        return c;
    }
}
```

----

## Fixer le retour d'un service REST avec l'objet Response

- renvoyer un code 200 si tout est ok, 404 si l'objet n'existe pas

```java
@Path("commune")
public class Commune {
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON) // MediaType.TEXT_XML
    public Response findById(@PathParam("id") long id){
        Commune c = ...
        if(c==null)
            return Response.status(404).build();
        else
            return Response.ok(c).build();
    }
}
```

----

## Créer une donnée avec un service REST POST

- saisie d'un nom formulaire dans une page HTML et envoie du contenu à la méthode suivante :

```java
@POST
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
public Response create(@FormParam("codePostal") String codePostal, @FormParam("name") String name){
    Commune c = ...
    return Response.status(201).build();
}
```

----

## Mettre à jour ou créer une donnée avec un service REST PUT

- PUT peut faire de la mise à jour en spécifiant la clé primaire de l'objet que l'on met à jour
- même URI que le GET (convention)

```java
@PUT
@Consumes(MediaType.APPLICATION_JSON)
public Response create(@PathParam("codePostal") String codePostal, Commune commune){
    Commune c = ...
    c.setName(commune.getName());
    return Response.status(201).build();
}
```

----

## Ecrire un client pour créer une requête PUT en JSON

```java
// création d'un client HTTP de JAX-RS
Client client = ClientBuilder.newClient();
WebTarget target = client.target("http://localhost:8080");
WebTarget communeUpdate = target.target("commune/75000");
Invocation.Builder builder = communeUpdate.request(MediaType.APPLICATION_JSON);
Entity<Commune> entity = Entity.entity(paris, MediaType.APPLICATION_JSON);
builder.put(entity);
```

----

## Effacer une donnée avec un service REST DELETE


```java
@DELETE
@Path("{id}")
public Response delete(@PathParam("id") long id){
    Commune c = ...
    c.setName(commune.getName());
    return Response.status(201).build();
}
```

----

## Les différentes annotations

- `@FormParam`, `@PathParam`, `@QueryParam`, `@HeaderParam`, `@CookieParam`, `@MatrixParam`
- `@Context`, peut, et permet d'aller chercher des infos dans le contexte du service REST : **UriInfo**, **Request** (propre à JAX-RS), **HttpServletRequest**, **HttpHeaders**, **Configuration**
- *HttpServletRequest* permet de gérer une session, mais il ne faut pas le faire car c'est une mauvaise pratique

```java
@GET @Path("{id}")
public getById(@PathParam("id") long id, @Context HttpHeaders headers){...}
```

----

## Conversion d'objets en documents XML avec JAXB

- JAX-RS est capable de convertir des objets Java en XML via l'API **JAX-B**, ou en JSON l'API **JSON-B**
- possibilité de personnaliser le XML ou le JSON produit en annotant l'objet Java

Exemple pour le XML avec les annotations JAX-B :
- **@XmlRootElement** permet de spécifier le nom de l'élément racine
- **@XmlType** permet de préciser l'ordre des champs pour écrire le document XML
- **@XmlAccessorType** permet de dire que l'on veut travailler avec les champs plutôt que sur les getters (valeur par défaut)
- **@XmlElement** permet de créer un sous-élément XML pour le champ Java
- **@XmlAttribute** permet de mettre un champ Java en attribut XML
- **@XmlElementWrapper** permet de définir le nom d'un élément wrapper contenant d'autres éléments XML


```java
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"name","communes"})
@XmlRootElement(name = "dept")
public class Departement {

    @XmlAttribute(name = "code-postal")
    private String codePostal;

    @XmlElement(name = "name")
    private String name;

    @XmlElementWrapper(name = "communes")
    private List<Commune>;
}
```

----

## Modèle de maturité de Richardson

- la création de services REST répond à une théorie appelée *Modèle de maturité de Richardson*
- article de Martin Fowler : https://www.martinfowler.com/articles/richardsonMaturityModel.html
- 4 niveaux :
    - niveau 0 : échange de données au format XML/JSON
    - niveau 1 : notion de ressources, assocations des objets à des URI
    - niveau 2 : convention qui repose sur l'utilisation des méthodes HTTP GET/POST/PUT/DELETE...
    - niveau 3 : notion HATEOAS, retourne des informations supplémentaires au client, pour l'informer d'autres requêtes qu'il peut faire sur l'API

----

## Persister des données en Java EE avec JPA et des EJB

- solution ancienne remplacée par CDI, qui risque de devenir dépréciée
- toutes les méthodes d'un EJB sont transactionnelles
- le serveur Java EE est responsable de l'instanciation de l'Entity Manager et de la classe CommuneEJB, et de l'injecter dans les champs annotés, on parle d'**injection**

```java
public class CommuneResource {
    @EJB private CommuneEJB communeEJB;

    public Response create (Commmune commune){ communeEJB.persist(commune); }
}

@Stateless
public class CommuneEJB {
    @PersistanceContext(unitName = "jpa") private EntityManager em; // unitName prend le nom de l'unité de persistance définie dans le fichier META-INF/persistance.xml

    public void persist(Commune commune){ em.persist(commune); }
}
```

----

## Persister des données en Java EE avec JPA et CDI

- même principe qu'EJB en utilisant d'autres annotations
- les méthodes ne sont pas transactionnelles par défaut, il faut ajouter l'annotation **@Transactional** sur la classe ou la méthode pour que ce soit le cas
- CDI propose plus d'options qu'EJB pour contrôler ce qu'on va injecter, par exemple utiliser une base de test au lieu de la base de prod

```java
public class CommuneResource {
    @Inject private CommuneService communeService;

    public Response create (Commmune commune){ communeService.persist(commune); }
}

public class CommuneService {
    @PersistanceContext(unitName = "jpa") private EntityManager em;

    @Transactional public void persist(Commune commune){ em.persist(commune); }
}
```

----

## Configurer JPA et CDI en Java EE

- pour utiliser CDI et JPA, il faut configurer le fichier **META-INF/persistance.xml**
- pour CDI, il faut ajouter un fichier **META-INF/beans.xml** avec dans le fichier `<beans/>` car aucune configuration spécifique n'est requise
- dans le fichier *persistance.xml*, il faut modifier un peu les choses

```xml
<!-- le conteneur Java EE gère les transactions et qu'on ne peut plus les gérer à la main -->
<persistance-unit name="jpa" transaction-type="JTA">
    <provider>...</provider>
    <!-- ajout d'un élément jta-data-source qui permet à JTA de se connecter à la base de données à la place des properties concernant les éléments de connexion à la BDD -->
    <jta-data-source>jdbc/MySQLDS</jta-data-source>
```

----

## Architecture des appels asynchrones et réactifs
