# L'API Servlet

----

## Introduction

- L’API Servlet, qui fait partie de JEE, est un jeu de classes Java qui permettent d’écrire des objets capables de recevoir des requêtes HTTP via un serveur web, de les traiter et d’y répondre
- L’API Servlet a elle-même deux extensions principales : JSP (Java Server Pages) et JSTL (Java Server Tag Libraries)
- une servlet a une vie permanente et son cycle de vie est entièrement géré par un container. Elle peut être commandée dès l’allumage du serveur web, ou à la première requête qui la concerne. Une fois créée, elle n’est pas détruite, et reste disponible pour traiter les requêtes suivantes
- une servlet vit dans un container de servlets (par exemple **Tomcat**), qui est capable de prendre en charge plusieurs servlets, et sait à laquelle transmettre une requête entrante. Un tel container possède certaines fonctionnalités propres à un serveur web, comme la possibilité d’écouter le port 80 et de décoder les requêtes HTTP

----

## Version de Tomcat et de l'API Servlet

| Version Servlet/JSP | Version Expression Language | Version de Tomcat      | Version de Java |
| :---:               | :---:                       | :---:                  | :---:           |
| 4.0 / 2.3           | 3.0                         | 9.0                    | 8+              |
| 3.1 / 2.3           | 3.0                         | 8.5                    | 7+              |
| 3.1 / 2.3           | 3.0                         | 8.0                    | 7+              |
| 3.0 / 2.2           | 2.2                         | 7.0                    | 6+              |
| 2.5 / 2.1           | 2.1                         | 6.0                    | 5+              |
| 2.4 / 2.0           | N/A                         | 5.5                    | 1.4+            |

Depuis l'API Servlet 3.0, il est possible d'utiliser des annotations pour ne plus déclarer les servlets dans le fichier **web.xml**.

----

## Tomcat

- Tomcat possède toutes les fonctionnalités d’un serveur web, et est donc capable de gérer des sites web complets. Toutefois, il n’est pas aussi complet en termes de fonctionnalités que peut l’être Apache. Comme il se trouve que ces deux projets sont développés par la même association (ASF : Apache Software Foundation), Apache et Tomcat ont été construits pour dialoguer ensemble. L’architecture classique est donc la suivante : Apache gère les sites et leur contenu « statique », et fait suivre les requêtes « dynamiques » à Tomcat.

- les répertoires de Tomcat :
    - **bin** : contient tous les fichiers qui permettent de lancer Tomcat
    - **conf** : contient les fichiers de configuration de Tomcat
    - **lib** :  contient les librairies Java dont Tomcat a besoin pour fonctionner
    - **log** : c'est ici que Tomcat écrit ses fichiers de journalisation dans sa configuration par défaut
    - **temp** : répertoire contenant des fichiers temporaires
    - **webapp** :  contient les applications web gérées par Tomcat. Cet endroit peut bien sûr être redéfini. Par       - **défaut**, il contient cinq applications, très utiles lorsque l'on débute
        - **docs** : contient les pages de documentation de Tomcat, également accessibles en ligne ;
        - **examples** : contient des exemples simples de servlets et de pages JSP ;
        - **host-manager et manager** : contiennent l'application de gestion des applications web de Tomcat. Cette application permet de charger des applications web à chaud, et de les visualiser dans une interface web.
        - **ROOT** : racine des applications web chargées par défaut.
    - **work** : répertoire de travail de Tomcat, dans lequel, entre autres, les classes Java correspondant aux pages JSP sont créées et compilées

----

## Lancement de Tomcat