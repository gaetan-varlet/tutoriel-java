# JPA

----

## Introduction à JPA et au mapping objet relationnel

- JPA, pour **Java Persistance API**, consiste à écrire les champs de cet objet dans une base de données
- JPA est une spécification (interface et annotations), Hibernate (date de 2003), EclipseLink, OpenJPA sont des implémentations
- JPA permet de faire le lien entre les champs d'un objet Java et les colonnes d'une table en BDD. On parle d'ORM (**Object Relationnal Mapping**)
- plutôt qu'écrire du code JDBC pour chaque classe, JPA va faire ces associations automatiquement grâce à de la déclaration que l'on doit faire sur nos classes

----

## Problème de l'impedance mismatch en mapping objet relationnel

- correspondance entre les objets de l'univers Java (List, Map, Héritage) et les relations de l'unviers des bases de données (1:1, 1:p, p:1, n:p)
- on parle d'**Impedance Mismatch** ou problème d'adaptation d'impédance, qui n'est pas simple

----

## Créer une première entité JPA simple à partir d'un bean

- une classe Java et une table en base qui ont le même nom
- les champs de la classe portent le même nom que les colonnes de la table en base
- pouvoir associer la classe à une table en base, la classe doit être un **Bean**
    - implémenter **Serializable** (optionnel)
    - avoir un constructeur vide (présent si aucun autre constructeur)
    - avoir un getter et un setter pour chacun des champs
- en BDD, une table doit posséder une clé primaire et doit correspondre à un champ de la classe
- dire à JPA que la classe correspond à une table avec l'annotation **@Entity** (on parle d'entité JPA) et de dire quel champ correspond à la clé primaire avec l'annotation **@Id**

```java
@Entity
public class User implements Serializable {
    @Id private int id;
    private String name;
    // getters et setters
}
```
