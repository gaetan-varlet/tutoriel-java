# Introduction à Java

## Création d'une classe : gestion de la mémoire

- création de propriétés, de getters/setters, de constructeurs
- le mot clé **new** permet de créer une instance de la classe en invoquant un constructeur de la classe
- lors de l'instanciation d'un objet, la machine virtuelle Java réserve de la mémoire pour enregistrer les informations de l'instance dans cette zone mémoire
- cette zone mémoire va être étiquetée, et la JVM va créer un pointeur vers la zone mémoire et mettre la valeur du pointeur dans la variable où l'on stocke notre objet
- en Java, on ne parle pas de pointeur mais de **référence**. *user1* est donc une *référence vers un objet de type User*
- si une deuxième instance est créée, un autre espace mémoire est réservé et un deuxième pointeur est créé
- en langage C, lors de la réservation de mémoire, il faut ensuite faire une opération de libération de mémoire, sinon on obtient un bug appelé **fuite de mémoire**. Cela peut-être grave sur des applications qui durent longtemps
- en Java, la JVM gère la mémoire de l'application : allocation de mémoire et également réupèration automatique de mémoire grâce au **Gargabe Collector** qui va détecter les zones de mémoire qui ne sont plus utilisées, lorsqu'aucun pointeurs ne pointent vers un objet
- le Garbage Collector va s'exécuter lorque beaucoup de mémoire a été utilisé par l'application par rapport à la mémoire que la JVM a demandé à l'OS

```java
User user1 = new User("Varlet","Gaëtan");
User user2 = new User("Varlet","Louis");
```

## La classe Object

- toutes les classes Java étendent la classe **Object**
- toutes les méthodes d'*Object* sont disponibles dans les autres classes par **héritage**

### La méthode equals

- test d'égalité par référence entre 2 objets avec `==` : est-ce qu'ils pointent vers la même zone mémoire
- test d'égalite entre 2 objets avec la méthode `equals()`
	- elle renvoie un booléen en programmant la façon de comparer les objets (les IDE peuvent le faire pour nous)
	- comportement par défaut de la méthode `equals` : comparer les valeurs de référence comme `==`
	- redéfinition de `equals()` par **surcharge** dans une classe nous appartenant qui sera donc utilisé à la place de celle de `Object`
- test d'égalité des primitifs avec `==`

```java
Person p1 = new Person("toto", 20);
Person p2 = new Person("toto", 20);
System.out.println(p1 == p2); // false
System.out.println(p1.equals(p2)); // false


public class Person {
	private String nom;
	private int age;
	public boolean equals(Object o) {
		if (!(o instanceof Person)) {
			return false;
		}
		Person u = (Person) o;
		if (this.nom.equals(u.getNom()) && this.age == u.getAge()) {
			return true;
		}
		return false;
	}
}
// comparaison après surcharge de la méthode equals
System.out.println(p1.equals(p2)); // true
```

### La méthode hashCode

- il existe une classe **String** pour gérer les chaînes de caractères
- la méthode `equals()` définie dans la classe String est une surcharge de celle définie dans la méthode *Object*, et compare les caractères et renvoie *true* si ce sont exactement les mêmes
- la méthode `hashCode()` est liée à la méthode `equals()`
	- il faut également surcharger `hashCode()` lorsuq'on surcharge `equals()` (fourni par l'IDE également), sinon cela peut entraîner des bugs
	- elle renvoie un entier qui est un code de hachage de l'objet, censé être représentatif de l'instance : 2 objets égaux au sens de `equals` DOIVENT avoir le même hashCode

```java
String s1 = new String("Hello"); // création d'une nouvelle instance de la classe String
String s2 = "Hello"; // création d'une nouvelle instance de la classe String dans certains cas
System.out.println("Hello".equals("Hello")); // true
System.out.println("Hello".equals("hello")); // false
```

### La méthode toString

- elle permet d'afficher des objets, en convertissant les objets en String
- par défaut, la méthode `toString()` d'Object écrit quelque chose comme `Person@2ba26...` qui correspondant à une représentation hexadécimale de l'objet
- possibilité de surcharger la méthode (également fourni par l'IDE)

```java
System.out.println(p1); // exemple.Person@6ff3c5b5
// surcharge dans la classe Person
public String toString() {
		return "Person [age=" + age + ", nom=" + nom + "]";
	}
System.out.println(p1); // Person [age=20, nom=toto]
```

### Autres méthodes d'Object

- il existe des méthodes `wait()`, `notify()` et `notifyAll()` utilisées dans la programmation concurrente
- il existe une méthode `clone()` qu'il ne faut plus utiliser
- il existe aussi une méthode `finalized()` qui est dépréciée

Chaque objet Java possède un pointeur vers une instance d'une classe particulière qui s'appelle `Class`
- cette classe porte des informations sur la classe dont l'objet est une instance (nom de la clasee...)
- récupèration avec la méthode `getClass()`

```java
System.out.println(p1.getClass()); // class exemple.Person
```

## La classe String

- la classe **String** permet de créer des chaînes de caractères
- il existe différentes façons de créer des chaînes de caractères :

```java
String s1 = "Hello",
String s2 = new String("Hello");
String s3 = s1 + s2;
```

- une chaîne de caractères est un objet classique avec un pointeur vers une zone mémoire qui contient un tableau de caractères
- `length()` renvoie le nombre de caractères d'une chaîne de caractères
- `toUpperCase()` et `toLowerCase()` permettent de mettre en majuscules/minuscules une chaîne de caractères. Elles retournent une nouvelle chaîne de caractères sans modifier car la première car la classe String est **immutable**, ce qui veut dire qu'elle ne peut pas être modifiée une fois qu'elle a eu une valeur
- `toCharArray()` renvoie un tableau de caractères `char[]`
- `indexOf()` permet de rechercher des caractères ou chaînes de caractères dans une chaîne de caractères et renvoie la position de la première occurence trouvée (il y a également `lastIndexOf()`)
- `substring()` permet d'extraire une chaîne de caractères
- `split()` permet de découper une chaîne de caractères et renvoie un tableau de String

```java
String s = "Hello";
System.out.println(s.length()); // 5
String sMaj = s.toUpperCase();
System.out.println(s); // Hello
System.out.println(sMaj); // HELLO

char[] tab = s.toCharArray(); // duplication du tableau de caractères à l'intérieur du String dans une autre zone mémoire
tab[0] = 'A';
System.out.println(s); // Hello et pas Aello
System.out.println(tab); // Aello

System.out.println(s.indexOf("e")); // 1
System.out.println(s.indexOf("l")); // 2
System.out.println(s.indexOf("lo")); // 3
System.out.println(s.indexOf("a")); // -1

System.out.println(s.substring(1)); // ello
System.out.println(s.substring(1, 3)); // el

String[] elements = s.split("e");
System.out.println(Arrays.toString(elements)); // [H, llo]
```

- il faut éviter d'utiliser le `+` pour faire des concaténations car comme une chaîne de caractères est immutable, alors plein de chaînes de caractères sont créées inutilement. Il faut utiliser un **StringBuilder** (c'est une chaîne de caractères mutable)
- dans les versions récentes de Java (à partir de Java 7), ce n'est cependant pas grave car la JVM ne va pas créer des chaînes de caractères intermédiaires
- à partir de Java 11, c'est même plus performant de faire des concaténations qu'utiliser un StringBuilder

```java
String s1 = "un";
String s2 = "deux"
String s3 = "trois";
String s4 = s1 + " " + s2 + " " + s3; // création de 3 chaînes de caractères pour chaque concaténation

StringBuilder sb = new StringBuilder();
sb.append(s1).append("").append(s2).append("").append(s3); // toutes ces étapes se passent dans le même objet
String s5 = sb.toString();
```

- création d'une chaîne de caractères avec ou sans `new`

```java
String s3 = new String("Hello");
String s4 = new String("Hello");
String s1 = "Hello";
String s2 = "Hello";
System.out.println(s1 == s2); // true car utilisation du même objet
System.out.println(s1.equals(s2)); // true

System.out.println(s3 == s4); // false
System.out.println(s3.equals(s4)); // true

System.out.println(s2 == s3); // false
System.out.println(s2.equals(s3)); // true
```


# Types et compatibilité entre types

## Affectation et compatibilités entre types

- lors de la création d'une variable, on crée 3 éléments : le type, le nom et la valeur
- il faut que le type et la valeur soient **compatible**, il n'est par exemple pas possible de mettre une chaîne de caractères dans un *int*, il y aura une erreur de compilation

```java
int a = "toto"; // erreur de compilation
int i = 6; // compilation OK
int j = 3.14 // erreur de compilation car il n'est pas possible d'écrire un nombre à virgule dans un entier
int k = (int) 3.14 // compilation OK, le nombre va être tronqué
float f = 3; // compilation OK car pas de perte de précision
```

## Les types primitifs

- il n'est pas possible de définir de pointer sur des types primitfs, on manipule directement la valeur, il n'est pas possible de manipuler l'adresse à laquelle la valeur est stockée
- les différents types de primitifs
	- les types primitifs entiers : byte (1 octet), short (2 octets), int (4 octets), long (8 octets)
	- les types primitfs à virgule : float (4 octets), double (8 octets)
	- boolean : true ou false (1 bit)
	- char : permet d'encoder un caractère (2 octets)

## Types référence vers des objets, héritage de type

- lors de l'instanciation d'un objet, récupération d'une **référence** (**pointeur**) vers une zone mémoire qui peut contenir un objet
- la différence entre les types primitifs et les références et qu'on peut appeler des méthodes

```java
String s = new User(...); // erreur de compilatiob
Object o = "Hello"; // compilation OK car la classe String hérite de la classe Object
```

## Les tableaux

- un tableau va être un pointeur vers une zone en mémoire qui va permettre d'enregister des types primitfs ou des références

```java
int[] tabInt = new int[10]; // initialisation d'un tableau avec 10 emplacements
int[] tabInt = {10,12}; // création d'un tableau directement initialisé
String[] tabString = {"a","b"};
```

- itération sur un tableau

```java
for(int i : tabInt){
	System.out.println(i); // affichage des éléments d'un tableau
}
for(int i = 0 ; i < tabInt.length ; i++){
	tabInt[i]=10; // modification des éléments d'un tableau
}
```

- il n'est pas possible de surcharger la méthode `toString()`, elle a donc été définie sur la classe `Arrays`

```java
Arrays.toString(tabInt); // permet d'afficher le contenu du tableau
```


# Structure d'une classe

Les éléments d'une classe :
- les **champs** de la classe
- les **constructeurs** (même nom que la classe, pas de type de retour, un bloc de paramètres)
- les **méthodes**
- le mot clé **this** fait référence au champ de la classe, c'est un pointeur vers l'objet dans lequel on se trouve

La visibilité des éléments :
- **public** : élément visible de n'importe où
- **protected** : élément accessible dans le même package et les classes héritant de cette classe
- *sans mot clé* : élément accessible dans le même package
- **private** : élément visible uniquement dans la classe

La **signature d'une méthode** :
- nom de la méthode + bloc de paramètres
- il est possible d'avoir des méthodes avec le même nom tant que la signature est différente
- la visibilité et le type de retour ne font pas partie de la signature
- si 2 méthodes ont le même nom, on parle de **surcharge**, ou **overload**, alors que la surcharge dans le cadre de l'héritage, on parle d'**override**

La notion de **package** :
- comme il y a souvent beaucoup de classes, organisation des classes dans une hiérarchie de répertoires
- ce chemin de répertoires va devenir le package de la classe
- convention de nommage des packages :
	- garantir l'unicité du nom de complet des classes avec le nom de package dans le monde entier, si on utilise des bibliothèques externes
	- utilisation des noms de domaine lus à l'envers pour préfixer les packages

```java
// chemin : a/b/c/User.java
package a.b.c;
public class User {}
```

Classes **wrapper** et **auto-boxing** :
- pour les types primitifs, il existe des classes associées (`byte -> Byte`...) appelées classes **Wrapper**
- possibilité via le mécanisme **auto-boxing** de créer des objets à partir des types primitifs
- accès à des méthodes comme `int nb = Integer.parseInt("5");`

```java
Long l1 = new Long(2); // création d'un Long
Long l2 = 3; // écriture d'un int qui est converti en long puis création du Long via l'auto-boxing
```

Le mot clé **static**
- lors de l'instanciation d'un objet, stockage dans le pointeur de la zone mémoire où est stocké l'objet instancié
- il y a une autre zone mémoire qui contient un objet de type `Class` qui représente la classe instanciée, en un seul exemplaire : on parle de **singleton** : l'utilisation de la méthode `getClass()` pointe donc vers le même espace mémoire peut importe dans quelle instance d'un même objet on est
- les informations **static** sont indépendants des instances et sont donc enregistrées dans cet espace mémoire et non pas dans l'espace mémoire de chaque instance
- il n'y a donc pas besoin d'instance de la classe pour accéder à un élément static
- les champs et les méthodes peuvent être static
- les méthodes utilitaires sont généralement static
- on parle de **méthodes statiques** ou **méthodes de classe** (par opposition aux méthodes d'instance)
- les méthodes d'instance voit les éléments statiques, en revanche les méthodes statiques ne voient pas les éléments d'instance

```java
public class User{
	public static n = 0;
	public User(){ n++; }
}
// accès à un champ static sans instancier la classe
User.n;
```

Le mot clé **final**
- peut-être utilisé sur la classe, `public final class User`, empêchant l'héritage de cette classe
- peut-être utilisé sur des méthodes, empêchant la surcharge de la méthode
- peut-être sur la déclaration champ , `final String name`, la valeur de ce champ ne peut pas être modifié, il devient immutable. Il faut alors obligatoirement donner une valeur aux champs *final* dans le constructeur
- peut-être utilisé sur des variables locales, elles ne peuvent plus être modifiées
- la classe String et les classes Wrapper sont des classes finales

# Construction d'objets

## Création d'une méthode Factory

- déclaration du constructeur d'une classe en privée pour ne plus pouvoir l'instancier
- il n'est alors possible d'appeler que les éléments statiques de la classe
- création d'une méthode statique pour instancier des objets, on parle de **méthode factory**
- l'intérêt principal est de faire de la vérification dans la méthode factory avant d'appeler le constructeur

```java
public class User {
	private User(){} // déclaration du constructeur en privé
	// création d'une méthode Factory
	public static User newInstance(){
		return new User();
	}

}
// autre classe
public static void main(String... args){
	User u = User.newInstance();
}
```

## Constructeur qui appelle un autre construceur de la même classe

```java
public class User {
	public User(String name){
		this(name, 0); // appel du constructeur défini en dessous avec this si c'est la première ligne de code dans le constructeur
	}
	public User(String name, int age){}
}
```

## Construction d'objet qui héritent d'autres objets, constructeur vide par défaut

- par défaut, sans déclarer de constructeur, un constructeur vide (constructeur par défaut) est généré à la compilation
- si on ajoute un autre constructeur, le constructeur par défaut n'est alors plus généré
- un constructeur doit toujours pouvoir appeler le constructeur de la classe qu'il étend
	- la suppression du constructeur vide d'une classe *User* va donc générer une erreur de compilation dans la classe *Employee* pour la génération de son constructeur vide s'il existe
	- par défaut, appel de la classe qu'on étend avec le mot clé **super**
	- laisser un constructeur vide dans une classe est une bonne pratique, car ça permet d'instancier les classes par réflexion

```java
public class User {
	String name;
}
public class Employee extends User {
	int salary;
	public Employee(){
		super();
	}
}
```

# Java Beans, énumérations et records

## Java Bean

Notion utilisé par tous les frameworks Java
- c'est une classe Java
- qui possède un constructeur vide (explicite ou par défaut)
- possède des propriétés (repose sur la notion de getter, correspond souvent à la notion de champ récupéré via un getter)
- il faut qu'il soit **Serializable**

```java
public class User {
	private String name; // champ de la classe
	public getName(){ return this.name; } // getter
	public setName(String name){ this.name = name; } // setter qui rend la propriété mutable
}
```

## Création d'énumérations

- c'est une classe, où l'on peut contrôler les instances de cette énumération (le constructeur est privé)
- possibilité d'ajouter des méthodes et des champs

```java
// exemple simple
public enum PointCardinal {
	NORTH, SOUTH, EAST, WEST;
}
// exemple avec l'ajout d'un champ label
public enum PointCardinal {
	NORTH("N"), SOUTH("S"), EAST("E"), WEST("W");

	String label;

	private PointCardinal(String label) {
		this.label = label;
	}
	public String getLabel(){
		return this.label;
	}
}
// exemple d'appel
System.out.println(PointCardinal.NORTH); // NORTH
System.out.println(PointCardinal.NORTH.getLabel()); // N
```

## Création de records

- disponible depuis le JDK 16
- analogue aux Java Beans
- les **composants** du record sont des champs
- le compilateur créé des champs, et des méthodes d'accès `int x()` et `int y()`, un constructeur, et des méthodes `toString()`, `equals()` et `hashCode()`
- possibilité d'ajouter des méthodes dans le record, ou des constructeurs
- les records sont des objets immutables

```java
public record Point(int x, int y) {}
```

# Classes, classes abstraites et interfaces

##  Ecriture et utilisation d'une classe abstraite

- il est possible d'imposer d'étendre une classe pour pouvoir l'instancier avec le mot clé **abstract**
- possibilité d'écrire comme une classe normale d'écrire des champs, des constructeurs et des méthodes
- possibilité également d'écrire des méthodes abstraites qui n'ont pas de corps de méthodes

```java
// création d'une classe abstraite
public abstract class Operation {
	abstract int calculate(int a, int b);
}
// création d'une implémentation de la méthode calculate
public class Addition extends Operation {
	public int calculate(int a, int b){
		return a+b;
	}
}
// appel de la méthode
Operation op = new Addition();
int result = op.calculate(1,2);
```

## Interface, exemple de Comparable de User

- notion au coeur du design de tous les frameworks et applications Java
- à l'intérieur d'une interface, il peut y avoir 3 types d'éléments :
	- méthodes abstraites
	- méthodes concrètes avec le mot clé **default**
	- méthodes statiques
- lors de l'implémentation d'une interface par une classe, obligation de surcharger les méthodes abstraites

```java
public interface Comparable<T> {
	int compareTo(T t);
}
public class User implements Comparable<User> {
	private String name;
	public int compareTo(User other){
		// t1.compareTo(t2), renvoie i<0 si t1<t2, i=0 si t1=t2, i>0 si t1>t2
	}
}
```

# Programmatiob Objet : encapsulation, héritage et polymorphisme

## Encapsulation de l'état d'un objet

- la notion d'**état d'un objet**, c'est la valeur que l'on donne à ses champs
- la notion d'**encapsulation** cache l'état d'un objet, c'est-à-dire qu'il n'est pas directement accessible de l'extérieur de l'objet
	- il faut pour cela ajouter le mot clé **private** devant les champs
	- il faut ajouter des getters pour accéder au contenu des champs

```java
public class User {
	private String name;
	private int age;
	public String getName() { this.name; }
}
```

## Héritage de type entre classes et interfaces

- lors de création d'un objet (`X x = new Y()`), l'implémentation Y doit étendre ou implémenter le type X
- on parle d'**héritage de type**

```java
public class User implements Comparable<User> {
	public String getName(){...}
	public int compareTo(User other){...}
}
public class Employee extends User {
	public int getSalary(){...}
}

User u = new User(); // ok
User u = new Employee(); // ok car l'implémentation Employee étend la classe User
Comparable<User> u = new User(); // ok aussi car l'implémentation User implémente l'interface Comparable
Comparable<User> u = new Employee(); // ok

User u = new Comparable<User>(); // KO car interdit d'instancier une interface
Employee e = new User(); // KO car User n'étend pas Employee
```

## Héritage de comportement : utilisation des méthodes d'une super-classe

- quand un type étend un autre type, l'intégralité de ce que sait faire ce deuxième type est disponible dans ce premier type
- on parle d'**héritage de comportement**

```java
User u = new new User();
u.toString(); // utilisation d'une méthode de la classe Object car User hérite de la classe Object
Employee e = new Employee();
e.getName(); // utilisation d'une méthode de User
```

## Résolution d'un appel de méthode polymorphique

- le polymorphisme, notion fondamentale en programmation objet, est relatif aux méthodes des objets. Il permet de redéfinir une méthode dans des classes héritant d'une autre classe
- le choix de la méthode est décidé à l'exécution en fonction de l'implémentation, on parle de **LATE BINDING**

```java
public class User {
	public String toString(){}
}
public class Employee extends User {
	public String toString(){}
}
// utilisation
User u = new Employee();
void display(User u){
	System.out.println(u.toString());
}
display(u); // exécution de la méthode toString d'Employee
```
