# JPA

----

## Introduction à JPA et au mapping objet relationnel

- JPA, pour **Java Persistance API**, consiste à écrire les champs d'un objet dans une base de données
- JPA est une spécification (interface et annotations), Hibernate (date de 2003), EclipseLink, OpenJPA sont des implémentations
- JPA permet de faire le lien entre les champs d'un objet Java et les colonnes d'une table en BDD. On parle d'ORM (**Object Relationnal Mapping**)
- plutôt qu'écrire du code JDBC pour chaque classe, JPA va faire ces associations automatiquement grâce à de la déclaration que l'on doit faire sur nos classes

----

## Problème de l'impedance mismatch en mapping objet relationnel

- correspondance entre les objets de l'univers Java (List, Map, Héritage) et les relations de l'univers des bases de données (1:1, 1:p, p:1, n:p)
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
- dire à JPA que la classe correspond à une table avec l'annotation **@Entity** (on parle d'entité JPA) et  dire quel champ correspond à la clé primaire avec l'annotation **@Id**

```java
@Entity
public class User implements Serializable {
    @Id private int id;
    private String name;
    // getters et setters
}
```

----

## Décrire une unité de persistence dans persistence.xml

- pour créer une application JPA, il faut un descripteur XML **persistance.xml** dans le répertoire **META-INF**
- contient la description et le paramétrage de l'application JPA notamment des entités JPA
- une unité de persistance est un ensemble d'entités JPA qui appartiennent à la même application JPA. 2 attributs obligatoire :
    - **name** : nom logique qu'on référence dans le code pour dire que le code Java s'adresse à cette unité de persistance
    - **transaction-type** : avec 2 valeurs possibles JTA (les transactions sont gérées automatiquement) RESOURCE-LOCAL (transactions gérées à la main)
- le sous-élément **provider** est une classe qui implémente une interface JPA qui est PersistanceProvider. Pour Hibernate, il s'agit *org.hibernate.jpa.HibernatePersistenceProvider*
- le sous-élément **class** liste les classes gérées par JPA
- le sous-élément **properties** permet de configurer l'unité de persitance dans des sous-éléments *property*
    - propriétés standard JPA (commence par javax.persistence) et propriétés propres à l'implémentation (commence par hibernate pour Hibernate)
    - les propriétés *javax.persistence.jdbc.url* *jdbc.driver* *jdbc.user* *jdbc.password* permettent de se connecter à la base
    - la propriété *hibernate.hbm2ddl* permet de fixer le comportement d'Hibernate vis à vis de l'existance ou non des tables associées aux entités JPA : **validate**, **update**, **create**, **create-drop**
    - la propriété *hibernate.dialect* permet de préciser à quelle base de données s'adresse l'application pour générer du code SQL optimisé pour cette base, sinon ce sera du code SQL standard basique

----

## Exemple de fichier persistence.xml

```xml
<persistence>
    <persistence-unit name="test-jpa" transaction-type="RESOURCE-LOCAL">
        <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
        <class>fr.exemple.model.User</class>
        <properties>
            <property name="javax.persistence.jdbc.url" value ="jdbc:mysql://127.0.0.1:3306/db"/>
            <property name="..." value ="..."/>
        </properties>
    </persistence-unit>
</persistence>
```

----

## Créer un entity manager pour une unité de persistence
