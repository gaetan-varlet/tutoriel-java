# JPA

----

## Introduction à JPA et au mapping objet relationnel

- JPA, pour **Java Persistance API**, consiste à écrire les champs d'un objet dans une base de données
- JPA est une spécification (interfaces et annotations), Hibernate (date de 2003), EclipseLink, OpenJPA sont des implémentations
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
- contient la description et le paramétrage de l'application JPA notamment des *Entity*
- une unité de persistance est un ensemble d'entités JPA qui appartiennent à la même application JPA. 2 attributs obligatoire :
    - **name** : nom logique qu'on référence dans le code pour dire que le code Java s'adresse à cette unité de persistance
    - **transaction-type** : avec 2 valeurs possibles JTA (les transactions sont gérées automatiquement) et RESOURCE-LOCAL (transactions gérées à la main)
- le sous-élément **provider** : classe qui implémente l'interface JPA **PersistanceProvider**. Pour Hibernate, il s'agit *org.hibernate.jpa.HibernatePersistenceProvider*
- le sous-élément **class** liste les classes gérées par JPA
- le sous-élément **properties** permet de configurer l'unité de persitance dans des sous-éléments *property*
    - propriétés standard JPA (commence par javax.persistence) et propriétés propres à l'implémentation (commence par hibernate pour Hibernate)
    - les propriétés *javax.persistence.jdbc.url*, *jdbc.driver*, *jdbc.user*, *jdbc.password* pour se connecter à la base
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

- la classe **Persistence** de JPA a une méthode factory **createEntityManagerFactory("nom persistence-unit")** qui crée un EntityManagerFactory (EMF). Il sera construit si toute la configuration est correcte
- le rôle de l'EMF est de créer un **EntityManager** qui va permettre de réaliser toutes les opérations de persistance
- l'EMF et l'EM sont des interfaces de JPA, les implémentations sont fournies par Hibernate

```java
EntityManagerFactory emf = Persistence.createEntityManagerFactory("test-jpa");
EntityManager em = emf.createEntityManager();
```

----

## Ecriture d'un premier bean en base

- étant donné qu'il s'agit d'une opération de modification de la BDD, elle doit se dérouler dans une transaction
- un EM est associé à une seule transaction

```java
User user = ...
EntityManager em = ...
em.getTransaction().begin(); // démarrage de la transaction
em.persist(user);
em.getTransaction().commit();
```

----

## Récupérer un objet en fonction de son id

- utilisation de la méthode **find()** où l'on précise le type de l'objet et la valeur de la clé primaire
- opération de type select, pas obligatoire de l'attacher à une transaction

```java
User user = em.find(User.class, 12);
```

----

## Mise à jour des champs d'un bean

- récupération de l'objet puis mise à jour de l'objet dans une transaction
- pas de méthode update. Le simple fait de prendre une entité JPA récupérée via un EM va générer une requête en base lorsqu'on modifie un de ces champs et qu'on demande le commit de la transaction

```java
em.getTransaction().begin();
User user = em.find(User.class, 12);
user.setAge(10);
em.getTransaction().commit();
```

----

## Effacement d'un bean d'une base

- inconvénient du delete et qu'il faut d'abord faire un **find()** avant de faire un **remove()**

```java
em.getTransaction().begin();
User user = em.find(User.class, 12);
em.remove(user);
em.getTransaction().commit();
```

----

## Confier la génération des valeurs de clés primaires à JPA

- lors de l'enregistrement en base d'une nouvelle entité JPA, il faut gérer la valeur de la clé primaire
- on peut confier la génération des clés primaires à la base
- on peut aussi confier la génération à Hibernate
- utilisation de l'annotation **@GeneratedValue** sur la clé primaire pour dire que ce n'est pas nous qui gérons la valeur de la clé primaire
- l'annotation prend un attribut **strategy** qui peut prendre comme valeur 
    - **GenerationType.AUTO** : Hibernate choisit la meilleure stratégie pour gérer les clés primaires
    - **GenerationType.IDENTITY**
    - **GenerationType.TABLE**
    - **GenerationType.SEQUENCE**

----

## Choisir le mode d'accès aux valeurs des champs d'une entité JPA

Hibernate utilise l'API Reflection :
- utilisation du constructeur vide
- accès direct aux champs privés ou via les getters et les setters
- possibilité d'annoter les *Entity* avec l'annotation **@Access** pour dire à Hibernate d'utiliser les champs ou les getters/setters

```java
@Access(AccessType.FIELD) // @Access(AccessType.PROPERTY)
@Entity
public class User {}
```

----

## Préciser le mapping d'une entité

- possibilité de mapper l'Entity avec une table qui a un nom différent avec l'annotation **@Table**
    - possibilité de dire à JPA de créer des contraintes d'intégrité dans cette annotation
    - il s'agit de contraintes en BDD, il est possible de ne pas les respecter en Java, mais il y aura une exception SQL lorsqu'on voudra sauvegarder les beans en base
- possibilité d'utiliser l'annotation **@Column** pour préciser le nom en base de données du champ de l'Entity

```java
@Entity
@Table(name="utilisateur", uniqueConstraints={
    @UniqueConstraint=(name="..." columnNames={"first_name", "last_name"})
})
public class User {
    @Column(name="first_name", length=40)
    private String firstName;
    private String lastName;
}
```

----

## Mapper les trois types de dates avec @Temporal

- les types "Date" ne sont pas gérés automatiquement
- il existe 3 types de date : **DATE**, **TIME** et **TIMESTAMP**
- il faut le préciser à JPA avec l'annotation **@Temporal(TemporalType.DATE)** en précisant DATE, TIME ou TIMESTAMP
- **java.sql.Date** n'est pas utilisable en JPA, il faut utiliser **java.util.Date**

----

## Mapper les énumération avec @Enumerated

- utilisation de l'annotation **@Enumerated** qui prend 2 valeurs possibles : **EnumType.STRING** et **EnumType.ORDINAL**
- ORDINAL va enregistrer en base le numéro d'index de la valeur de l'énumération (commence à 0) au lieu du nom de la valeur énumérée avec le type STRING

```java
enum Civility { MRS, MR }

public class User {
    @Enumerated(EnumType.STRING)
    Civility civility;
}
```

----

## Mapper les champs Serializable dans des BLOB avec @Lob

- les champs restant peuvent être classés dans 2 catégories :
    - les champs non sérialisables, ne pourront pas aller en base de données
    - les champs sérialisables, par exemple un tableau d'entiers, de String
- les champs sérialisables doivent être mappés dans des BinaryObject, avec l'annotation **@Lob** (pour Large Object Binary) et iront en BDD dans un champ de type BLOB
- ces champs sont coûteux en BDD en lecture et en écriture
- ces champs ne peuvent pas être requêtés dans une clause WHERE

```java
@Lob
private int [] securityKey
```

----

## Opérations detach, merge et refresh, hypothèse optimiste

- **persist()** permet d'écrire une entité JPA en base, **remove()** permet d'effacer une entité JPA en base
- il existe 3 autres opérations de persistance :
    - **detach()** permet de couper le lien entre l'EM et le bean Entity pour libérer l'EntityManager. Si des modifications sont faites et doivent être persistées en base, il va falloir recréer le lien avec *merge()*
    - **merge()** attache une entité JPA à l'EM sur lequel on appelle le merge. 2 raisons d'utilisation : s'il a été détaché, soit s'il est attaché à un autre EM
    - fonctionne dans le cas d'une hypothèse *OPTIMISTE*, c'est-à-dire qu'un autre opérateur n'a pas fait de modification sur la même entité, auquel cas, une *OptimisticLockException* est généré
    - **refresh()** permet de prendre les modifications faites en base pour rafraîchir l'objet Java

```java
em.detach(user);
em.merge(user);
em.refresh(user);
```

----

## Cycle de vie d'une entité JPA

- lors de la création d'une Entity, son état est **NEW**. L'entitée n'a pas encore vu la base et n'est pas attaché à un EM
- lors de l'utilisation de la méthode *persist()*, le bean passe à l'état **MANAGED**, ça veut dire qu'il est géré par un EM
- lors de l'utilisation de la méthode *remove()*, le bean passe à l'état **REMOVED**
- lors de l'utilisation de la méthode *detach()*, le bean passe à l'état **DETACHED**. Il y a toujours des informations notamment de clé primaire qui permettent de le localiser en base, ainsi que des informations sur la version, ce qui permet si l'hypothèse optimiste a été violée ou pas
- lors de l'utilisation de la méthode *merge()*, le bean passe de l'état *DETACHED* à *MANAGED*
- la méthode *refresh()* n'a de sens que sur un objet à l'état *MANAGED*

```java
User user = new User(...) // NEW
em.persist(user); // NEW -> MANAGED
em.remove(user); // MANAGED -> REMOVED
em.detached(user); // MANAGED -> DETACHED
em.merge(user);  // DETACHED -> MANAGED
em.refresh(user); // MANAGED -> MANAGED
```

----

## Les clés primaires composites

- il s'agit d'une clé primaire définit sur plusieurs champs
- il existe 2 façons de gérer les clés primaires composites en JPA

Solution 1
```java
@IdClass(PrimaryKey.class)
@Entity
class User {
    @Id Integer id1;
    @Id Integer id2;
}
@Embeddable
class PrimaryKey {
    Integer id1;
    Integer id2;
}
```

Solution 2
```java
@Entity
class User {
    @EmbeddedId PrimaryKey primaryKey;
}
@Embeddable
class PrimaryKey {
    Integer id1;
    Integer id2;
}
```

## Relation unidirectionnelle et bidirectionnelle entre 2 entités JPA

## Le comportement Cascade d'une relation pour persister

## Charger une relation en mode LAZY ou EAGER

## Gestion du caractère bidirectionnel d'une relation en création et en lecture

## Bilan sur la festion des relations one to one

## Relation one to many unidirectionnelle et bidirectionnelle

## Cas des relations one to many unidirectionnelles

## Protéger le contenu d'une relation multivaluée par copie défensive

## Gérer manuellement le caractère bidirectionnel d'une relation multivaluée

## Bilan sur les relations one to many et many to one en JPA

## Créer une relation de composition avec des objets inclus

## Mapper les structures de l'API Collection en base avec JPA

## Associer une relation one to many à un Set ou à une List

## Créer une structure de liste à l'aide d'une colonne portant un index

## Créer une liste en garantissant l'ordre des éléments

## Annoter une relation one to many pour fixer le comportement du mapping

## Mapper une table de hachage dont les clés sont des types primitifs

## Mapper une table de hachage dont les clés sont des entités JPA

## Mapper des collections d'objet qui ne sont pas des entités JPA

## Bilan sur le mapping des structures de l'API Collection

## Introduction au mapping de l'héritage

## Introduction au trois stratégies de mapping de l'héritage

## Mapper une hiérarchie de classes en mode TABLE_PER_CLASS
## Mapper une hiérarchie de classes en mode SINGLE_TABLE

## Mapper une hiérarchie de classes en mode JOINED

## Configurer le type de mapping pour une hiérarchie de classes

## Mapperdes champs factorisés dans une classe abstraite avec MappedSuperClass

## Bilan sur le mapping de l'héritage en JPA

## Introduction aux requêtes SQL et JPQL supportées en JPA

## Exécuter et analyser le résultat d'une première requête native

## Analyser le résultat d'une requête native qui retourne une ligne de plusieurs résultats

## Analyser le résultat d'une requête native qui retourne plusieurs lignes

## Ecrire et utiliser des requêtes natives paramétrées

## Mapper le résultat d'une requête native dans une entité JPA

## Ecrire et utiliser des requêtes nommées natives

## Mapper le résultat d'une requête primitive nommée dans une entité JPA

## Mapper le résultat dans un objet quelconque à l'aide d'un ResulSetMapping

## Compléments et bilan sur le RésultSetMapping

## Ecrire et exécuter une première requête JPQL

## Paramètrer des requêtes JPQL en nommant les paramètres

## Exemples de requêtes JPQL simples

## Exemple d'une requête JPQL avec une jointure implicite

## Déclarer des jointures explicites pour en faire des jointures externes

## Charger des entités en relation à l'aide d'un JOIN FETCH

## Mapper le résultat d'une requête JPQL dans un objet quelconque

## Ecrire et utiliser des requêtes JPQL nommés

## Paginer les résultats d'une requête native ou JPQL

## Utiliser les requêtes JPQL DELETE et UPDATE pour des mises à jour massives

## Bilan sur JPA
