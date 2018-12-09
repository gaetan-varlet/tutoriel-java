# L'API Servlet

----

## Introduction

- L’API Servlet, qui fait partie de JEE, est un jeu de classes Java qui permettent d’écrire des objets capables de recevoir des requêtes HTTP via un serveur web, de les traiter et d’y répondre
- L’API Servlet a elle-même deux extensions principales : JSP (Java Server Pages) et JSTL (Java Server Tag Libraries)
- une servlet a une vie permanente et son cycle de vie est entièrement géré par un container. Elle peut être commandée dès l’allumage du serveur web, ou à la première requête qui la concerne. Une fois créée, elle n’est pas détruite, et reste disponible pour traiter les requêtes suivantes
- une servlet vit dans un container de servlets (par exemple **Tomcat**), qui est capable de prendre en charge plusieurs servlets, et sait à laquelle transmettre une requête entrante. Un tel container possède certaines fonctionnalités propres à un serveur web, comme la possibilité d’écouter le port 80 et de décoder les requêtes HTTP
- Apache et Tomcat ont été construits pour dialoguer ensemble. L’architecture classique est donc la suivante : Apache gère les sites et leur contenu « statique », et fait suivre les requêtes « dynamiques » à Tomcat

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
