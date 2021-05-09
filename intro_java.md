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
