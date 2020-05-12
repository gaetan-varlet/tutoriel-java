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
