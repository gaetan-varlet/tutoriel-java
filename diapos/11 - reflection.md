# Java Reflection

----

## Introduction

- L'API reflection est au coeur des principaux frameworks : Jakarta EE (anciennement Java EE), Hibernate, Spring...
- Elle permet de manipuler les objets, les classes :
  - création des instances de classe
  - invocation des méthodes de cette classe
  - accès aux champs de cette classe
- On parle d'introspection, c'est à dire qu'on peut examiner le contenu d'un objet sans connaître le nom de sa classe à la compilation

----

## la classe Class

- la classe **Class** modélise les classes en Java
- 3 façons de récupérer une classe :
  - la méthode **getClass()** de la classe _Object_, disponible sur toutes les instances d'objets
  - possibilité de récupérer une classe avec son nom
  - possibilité de récupérer une classe avec **Class.forName()**
- ces 3 méthodes retourne le même objet qui modélise la classe, car une instance de classe qui modélise une classe n'existe qu'en un seul exemplaire dans la JVM, on parle de pattern **Class Singleton**. Pour comparer 2 objets _Class_, le `==` est suffisant, pas besoin de la méthode `equals()`

```java
List<String> l = new ArrayList<>();
System.out.println(l.getClass()); // class java.util.ArrayList

Class c = String.class;
System.out.println(c); // java.lang.String

Class cl = Class.forName("java.lang.String");
System.out.println(cl); // java.lang.String

System.out.println(c == cl); // true
```

----

## Instancier une classe et récupérer ses attributs

- création d'une instance d'un objet avec la méthode **newInstance()** qui appelle le constructeur vide (méthode dépréciée)
- récupérer les attributs de la classe (noms et types) avec les méthodes **getFields()** et **getField("attribut")** qui renvoientt les champs publics de la classe et des superclasses, et les méthodes **getDeclaredFields()** et **getDeclaredField("attribut")** qui renvoient les champs privés uniquement de la classe

```java
Class cl = Class.forName("exemple.Person");
Person p1 = (Person) cl.newInstance();

Field[] attributsPublics = cl.getFields();
Field[] attributsPrives = cl.getDeclaredFields();
System.out.println(Arrays.toString(attributsPublics)); // []
System.out.println(Arrays.toString(attributsPrives));
// [private java.lang.String exemple.Person.prenom, private int exemple.Person.age, private boolean exemple.Person.homme]
Field field = cl.getDeclaredField("age");
System.out.println(field); // private int exemple.Person.age
```

----

## Accéder à la valeur d'un attribut et le mettre à jour

- **field.getName()** donne le nom de l'attribut, **field.getType()** son type
- pour accéder à la valeur d'un attribut ou le modifier, il faut préciser sur quelle instance de la classe on souhaite le faire, en utilisant les méthodes **get(object)** et **set(object, val)**
- en dehors de la classe, on ne peut accéder ou mettre à jour un attribut que s'il est public. S'il est privé, il faut utiliser une porte dérobée en rendant accessible l'attribut privé en dehors de la classe avec la méthode **setAccessible(true)**. Cela ne rend pas le champ privé public mais demande à la JVM de ne pas vérifier si on a le droit de lire/modifier cet attribut. Il est possibe d'activer des règles de sécurité au niveau de la JVM en activant le SecurityManager pour que cela ne soit pas possible. Si l'accessibilité à l'attribut n'est pas activée, une *IllegalAccessException* est jetée lorsqu'on essaie d'accéder ou de modifier l'attribut

```java
System.out.println(field.getName()); // age
System.out.println(field.getType()); // int

field.setAccessible(true);
System.out.println(field.get(p1)); // 0
field.set(p1, 3);
System.out.println(field.get(p1)); // 3
```

----

## Les méthodes d'une classe

- **getMethod(nomMethode)** et **getMethods()**renvoient les méthodes publiques sur la classe et les superclasses, et les méthodes par défaut des interfacecs qu'elle implémente
- **getDeclaredMethod("nomMethode")** et **getDeclaredMethods()** renvoient les méthodes déclarées à l'intérieur de la classe quelque soit leur visiblité
- pour récupérer une méthode, il faut préciser le type des arguments de la méthode s'il y en a
- pour invoquer une méthode sur une instance, il faut utiliser la méthode **invoque(object, params...)** qui prend l'objet sur lequel appliquer la méthode et les éventuels paramètres de la méthode


```java
System.out.println(Arrays.toString(cl.getDeclaredMethods()));
Method methodToString = cl.getMethod("toString");
System.out.println(methodToString); // public java.lang.String exemple.Person.toString()
System.out.println(methodToString.getName()); // toString

Method methodSetPrenom = cl.getMethod("setPrenom", String.class);
// appel de setPrenom sur p1 avec le paramètre toto
methodSetPrenom.invoke(p1, "toto");
// appel de getPrenom()
System.out.println(cl.getMethod("getPrenom").invoke(p1)); // toto
```

----

## Les constructeurs d'une classe

- **getConstructor(...)**, **getDeclaredConstructors()**, **getDeclaredConstructor(...)** et **getDeclaredConstructors()** renvoient les constructeurs publics pour les 2 premiers, les constructeurs de toutes visiblité pour les 2 derniers. Il n'y a pas d'exploration des superclasses
- le nom des constructeur est forcément le nom de la classe, pas besoin de le passer en paramètre. Il faut passer en paramètre les types des paramètres des constructeurs
- création d'une instance de la classe avec la méthode **newInstance(...)** qui vérifie si on a bien accés au constructeur. Pattern à privilégier au pattern déprécié qui utiliser la méthode *newInstance()* sur l'objet de type Class

```java
Constructor co1 = cl.getConstructor(); // constructeur par défaut
System.out.println(co1); // public exemple.Person()
Constructor co2 = cl.getConstructor(String.class, int.class, boolean.class);
System.out.println(co2); // public exemple.Person(java.lang.String,int,boolean)

Person p2 = (Person) co1.newInstance();
System.out.println(p2); // Person [age=0, homme=false, prenom=null]
Person p3 = (Person) co2.newInstance("Toto", 20, true);
System.out.println(p3); // Person [age=20, homme=true, prenom=Toto]
```

----

## Les modificateurs

- possibilité de récupérer de l'information sur les constructeurs, les attributs et les méthodes qui sont les **modificateurs** (visibilité (public, private...), static/non static, final, transient)
- utilisation de la méthode **getModifiers()** qui renvoi un *int* qu'il faut interpréter en le passant en paramètre des méthodes statiques de la classe *Modifier*

```java
int a = co1.getModifiers(); // 1
System.out.println(Modifier.isPublic(a)); // true
System.out.println(Modifier.isStatic(a)); // false
```