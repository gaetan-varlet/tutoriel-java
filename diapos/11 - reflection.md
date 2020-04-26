# Java Reflection

---

## Introduction

- L'API reflection est au coeur des principaux frameworks : Jakarta EE (anciennement Java EE), Hibernate, Spring...
- Elle permet de manipuler les objets, les classes :
  - création des instances de classe
  - invocation des méthodes de cette classe
  - accès aux champs de cette classe
- On parle d'introspection, c'est à dire qu'on peut examiner le contenu d'un objet sans connaître le nom de sa classe à la compilation

---

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

---

## Instancier une classe et récupérer ses attributs

- création d'une instance d'un objet avec la méthode **newInstance()** qui appelle le constructeur vide (méthode dépréciée)
- récupérer les attributs de la classe (noms et types) avec les méthodes **getFields()** et **getField("attribut")** qui renvoientt les champs publics de la classe et des superclasses, et les méthodes **getDeclaredFields()** et **getDeclaredField("attribut")** qui renvoient les champs privés uniquement de la classe

```java
Class cl = Class.forName("exemple.Person");
Person person = (Person) cl.newInstance();

Field[] attributsPublics = cl.getFields();
Field[] attributsPrives = cl.getDeclaredFields();
System.out.println(Arrays.toString(attributsPublics)); // []
System.out.println(Arrays.toString(attributsPrives));
// [private java.lang.String exemple.Person.prenom, private int exemple.Person.age, private boolean exemple.Person.homme]
Field field = cl.getDeclaredField("age");
System.out.println(field); // private int exemple.Person.age
```
