# Lambda expressions et interfaces fonctionnelles

----

## Interface fonctionnelle

- interface qui ne possède qu'une seule méthode abstraite
- elle est annotée `@FunctionalInterface` (pas obligatoire, pour la rétrocompatibilité). Si l'interface n'est pas fonctionnelle, cela crée une erreur de compilation
- il peut donc y avoir d'autres méthodes qui ne sont pas abstraites, qui ont donc une implémentation par défaut. Autant de méthodes par défaut et de méthodes statiques que l'on veut

## Lambda expressions

- ajout de Java 8, en 2014
- impact l'ensemble des API Java
- c'est l'implémentation d'une interface fonctionnelle

----

## Exemple de Lambda expressions

```java
// exemple d'interface fonctionnelle Consumer de type T, qui a une méthode accept qui prend un objet de type T en paramètre qui le consomme et ne retourne rien
public interface Consumer<T> {
    public void accept(T t);
}

// implémentation de l'interface
// 1 - possibilité de créer une classe qui implémente l'interface
// 2 - implémentation à la volée de l'interface en redéfinissant la méthode
Consumer<String> consumer = new Consumer<>() {
    @Override
    public void accept(String arg0) {
        System.out.println("coucou " + arg0);
    }
};
consumer.accept("Gaëtan");
// 3 - implémentation de l'interface fonctionnelle avec une lambda expression
Consumer<String> consumerLambda1 = (String s) -> {System.out.println("bonjour " + s);};
consumerLambda1.accept("Gaëtan");
// 4 - lambda expression simplifée
Consumer<String> consumerLambda2 = s -> System.out.println("hello " + s);
consumerLambda2.accept("Gaëtan");
```

----

## la méthode forEach() d'une liste

```java
// création d'une liste
List<String> list = List.of("a", "b", "c");
// bouche for
for(String s : list){
    System.out.println(s);
}
// méthode forEach() qui prend comme argument un Consumer de String
list.forEach(s -> System.out.println(s));
```

----

## L'interface Predicate<T>

```java
// comme Consummer mais retourne un booléen
public interface Predicate<T> {
    public boolean test(T t);
}

// Exemples
// prédicat qui retourne true si la chaine de caractère est non null, false sinon
Predicate<String> p1 = s -> s != null;
// retourne vrai si la chaine fait moins de 10 caractères
Predicate<String> p2 = s -> s.length() < 10;

System.out.println(p1.test(null)); // false
System.out.println(p1.test("a")); // true
System.out.println(p2.test("toto")); // true
System.out.println(p2.test("1234567890")); // false
System.out.println(p2.test(null)); // NullPointerException
```

----

## Lambda à 2 paramètres : BiConsumer et BiPredicate

```java
// comme Consummer mais avec 2 paramètres
public interface BiConsumer<T,U> {
    public void accept(T t, U u);
}
public interface BiPredicate<T,U> {
    public boolean test(T t, U u);
}

BiConsumer<String, Integer> bc = (s, i) -> System.out.println(s + " - " + i);
bc.accept("a", 2); // a - 2
BiPredicate<String, String> bp = (s1, s2) -> s1.contains(s2);
System.out.println(bp.test("toto", "to")); // true
System.out.println(bp.test("toto", "ta")); // false
```

----

## Transformer un BiPredicate en Predicate par application partielle

```java
BiPredicate<String, String> bp = (s1, s2) -> s1.contains(s2);
String varTest = "toto";
System.out.println(bp.test(varTest, "ERROR")); // false
varTest = "AERRORA";
System.out.println(bp.test(varTest, "ERROR")); // true
// on transforme le biprédicat en prédicat en fixant un des deux paramètres : on parle d'application partielle
Predicate<String> p = s -> s.contains("ERROR");
System.out.println(p.test(varTest)); // true
```

----

## Les interfaces Function<T,R> et BiFunction<T,U,R>

```java
// prend un objet T en paramètre et retourne un objet R
public interface Function<T,R> {
    public R apply(T t);
}
// prend un objet T et un objet U en paramètres et retourne un objet R
public interface BiFunction<T,U,R> {
    public R apply(T t, U u);
}

Function<String, Integer> length = s -> s.length();
System.out.println(length.apply("toto")); // 4
BiFunction<String, String, Integer> indexOf = (s1, s2) -> s1.indexOf(s2);
System.out.println(indexOf.apply("toto", "ot")); // 1
```

----

## Les interfaces UnaryOperator<T> et BinaryOperator<T>

```java
// extension de Function qui ne change pas le type des objets qu'elle manipule
public interface UnaryOperator<T> extends Function<T,T> {}
public interface BinaryOperator<T> extends BiFunction<T,T,T> {}

UnaryOperator<String> upperCase = s -> s.toUpperCase();
System.out.println(upperCase.apply("toto")); // TOTO
BinaryOperator<String> concat = (s1, s2) -> s1.concat(s2);
System.out.println(concat.apply("to", "ta")); // tota
```

----

## L'interface Supplier

```java
// ne prend pas de paramètre et renvoi un objet de type T, par exemple un constructeur
public interface Supplier<T> {
    public T get();
}

Supplier<String> bonjour = () -> "Bonjour";
System.out.println(bonjour.get()); // Bonjour
Supplier<Double> pi = () -> 3.14;
System.out.println(pi.get()); // 3.14
```

----

## Résumé des interfaces fonctionnelles

Les 4 principales catégories d'interfaces fonctionnelles fournies par le JDK (dans le package `java.util.function` disponible à partir de Java 8) :
- `Consumer<T> -> void accept(T t)`
- `Supplier<T> -> T get()`
- `Predicate<T> -> boolean test(T t)`
- `Function<T, R> -> R apply(T t)`

Il y a une cinquième catégorie, **Runnable**, qui existe depuis Java 1, qui ne prend aucun paramètre et ne retourne rien.

```java
public abstract interface Runnable {
    public abstract void run();
}

Runnable runnable = () -> System.out.println("toto");
runnable.run(); // toto
```

----

## Les comparaisons : l'interface Comparable

En Java, pour trier les éléments d'une collection, il faut que le type de l'objet implémente l'interface **Comparable** en redéfinissant la méthode **int compareTo(T other)**. Certaines classes comme *String* ou *Integer* le font déjà :

```java
// exemple en utilisant le tri naturel sur des chaînes de caractères
List<String> listeString = Arrays.asList("a", "c", "d", "b");
Collections.sort(listeString); System.out.println(listeString); // [a, b, c, d]

// exemple d'implémentation de l'interface Comparable
public class Person implements Comparable<Person> {
	private String prenom;
	private int age;
    private boolean homme;  
    @Override
	public int compareTo(Person o) {
		return this.age - o.age; // trie des personnes selon leur âge dans l'ordre croissant
    }
}

// il est mainteant possible de trier notre liste
List<Person> list = Arrays.asList(new Person("Gaëtan", 32, true), new Person("Louis", 2, true),
    new Person("Florine", 30, true), new Person("Louis", 1, true));
Collections.sort(list); System.out.println(list); // 1 2 30 32
```

----

## Les comparaisons : l'interface Comparator (1)

Il est également possible de définir un **Comparator**, qui est une interface fonctionnelle, via une lambda expression. Cela permet de ne pas implémenter l'interface *Comparable* ou de changer l'ordre naturel pour les classes l'implémentant :

```java
public interface Comparator<T> {
    public int compare(T t1, T t2);// < à 0 si s1 < à s2, = à 0 si s1 = à s2, > à 0 si s1 > à s2
}

Comparator<String> comparatorStringParDefaut = (s1, s2) -> s1.compareTo(s2);
Comparator<String> comparatorInverse = (s1, s2) -> s2.compareTo(s1);
Comparator<String> comparatorLength = (s1, s2) -> Integer.compare(s1.length(), s2.length());
System.out.println(comparatorStringParDefaut.compare("a", "b")); // -1
System.out.println(comparatorInverse.compare("a", "b")); // 1
System.out.println(comparatorLength.compare("a", "b")); // 0
System.out.println(comparatorLength.compare("aa", "b")); // 1
System.out.println(comparatorLength.compare("a", "bb")); // -1

List<String> listeString = Arrays.asList("a", "c", "d", "b");
Collections.sort(listeString, comparatorStringParDefaut); System.out.println(listeString); // [a, b, c, d]
Collections.sort(listeString, comparatorInverse); System.out.println(listeString); // [d, c, b, a]
// possibilité d'utiliser la méthode sort de List depuis Java 8 en spécifiant un comparateur
listeString.sort(comparatorStringParDefaut); System.out.println(listeString); // [a, b, c, d]
```

----

## Les comparaisons : l'interface Comparator (2)

Avec Java 8, de nouvelles méthodes sont arrivées dans l'interface Comparator qui facilitent la création de Comparator :

```java
// Exemple sur des String
List<String> listeString = Arrays.asList("a", "c", "d", "b");
Comparator<String> c1 = Comparator.comparing(String::valueOf);
Comparator<String> c2 = Comparator.naturalOrder();
Comparator<String> c3 = Comparator.reverseOrder();
listeString.sort(c1); System.out.println(listeString); // [a, b, c, d]
listeString.sort(c2); System.out.println(listeString); // [a, b, c, d]
listeString.sort(c3); System.out.println(listeString); // [d, c, b, a]

// Exemple sur l'objet Person
// tri sur l'aĝe par ordre croissant (ancienne et nouvelle syntaxe)
Comparator<Person> compPerson1 = (p1, p2) -> (p1.getAge() - p2.getAge());
Comparator<Person> compPerson2 = Comparator.comparing(Person::getAge);
// tri sur le prénom par ordre croissant (ancienne et nouvelle syntaxe)
Comparator<Person> compPerson3 = (p1, p2) -> (p1.getPrenom().compareTo(p2.getPrenom()));
Comparator<Person> compPerson4 = Comparator.comparing(Person::getPrenom);
// tri sur le prénom par ordre alphabétique puis l'âge par ordre décroissant
Comparator<Person> compPerson5 = Comparator.comparing(Person::getPrenom).thenComparing(Comparator.comparing(Person::getAge).reversed());
// tri sur le prénom par ordre alphabétique en mettant les prénoms null à la fin
Comparator<Person> compPerson6 = Comparator.comparing(Person::getPrenom, Comparator.nullsLast(Comparator.naturalOrder()));
```

----

## Chaîner des Consumer

- possibilité de chaîner des Consumer en définissant un consumer qui en consomme deux autres
- possibilité également d'utiliser la méthode `andThen(Consumer c)` qui prend un consumer à consommer après le premier

```java
Consumer<String> c1 = s -> System.out.println("c1 = " + s);
Consumer<String> c2 = s -> System.out.println("c2 = " + s);
Consumer<String> c3 = s -> {
    c1.accept(s);
    c2.accept(s);
};
c3.accept("toto");
// c1 = toto
// c2 = toto

Consumer<String> c4 = c1.andThen(c2);
c4.accept("titi");
// c1 = titi
// c2 = titi
```

----

## Protéger le chaînage de lambda expression contre les NullPointerException

```java
// utilisation de Objects.requireNonNull qui va renvoyer une NullPointerException avec le message spécifié pour éviter que lorsque la méthode soit appelé avec un paramètre null, un NullPointerException surgisse dans la méthode andThen
public static void executeConsumer(Consumer<String> consumer){
    Consumer<String> c = s -> System.out.println("c = " + s);
    Objects.requireNonNull(consumer, "consumer ne doit pas être nul");
    c.andThen(consumer).accept("toto");
}

public static void main(String[] args) {
    Consumer<String> c1 = s -> System.out.println("c1 = " + s);
    executeConsumer(c1);
}
```

----

## Réaliser le ET logique de deux prédicats

possibilité de combiner des précidats avec les méthodes `and(Predicate<T>)`,  `or(Predicate<T>)`, `negate()`...

```java
Predicate<String> isNotNull = s -> s != null;
Predicate<String> isNotEmpty = s -> !s.isEmpty();
Predicate<String> etLogique = isNotNull.and(isNotEmpty);

System.out.println(etLogique.test(null)); // false
System.out.println(etLogique.test("")); // false
System.out.println(etLogique.test("toto")); // true
```

----

## Les références de méthodes

C'est une autre façon d'écrire une lamba expression, qui permet de gagner en lisibilité et très légèrement en performance

```java
// lambda expression
Consumer<String> c1 = s -> System.out.println("hello " + s);
// référence de méthode
Consumer<String> c2 = System.out::println;
```

Il existe 4 types de références de méthodes :
- **BoundInstance** : invocation d'uné méthode sur une instance d'un objet (exemple ci-dessus)
- **Static** : appel statique. Exemple : `d -> Math.sqrt(d);` devient `Math::sqrt`
- **UnboundInstance** : invocation de la méthode sur le premier paramètre, le reste des paramètres est passé en paramètre de la méthode. Exemple : `(s1, s2) -> s1.indexOf(s2);` devient `String::indexOf;`
- **Constructor** : `name -> new User(name);` `() -> new User();` et `(name, age) -> new User(name, age);` peuvent toutes être écrites avec la référence de méthode `User::new;`

----

## Quelques nouvelles méthodes sur les Collections

```java
List<String> list = Stream.of("one", "two", "three", "four", "five").collect(Collectors.toList());
// méthode forEach, définie sur Iterable, qui prend un Consumer en paramètre
list.forEach(System.out::println);
// méthode removeIf, définie sur Collection, prend un Predicate en paramètre
list.removeIf(s -> s.startsWith("f"));
System.out.println(list); // [one, two, three]
// méthode replaceAll, définie sur List, prend un UranyOperator en paramètre (Function qui renvoie un objet de même type que celui reçu)
list.replaceAll(s -> s.replace("o", "O"));
System.out.println(list); // One, twO, three]
// méthode sort(), définie sur List, prend un Comparator en paramètre
list.sort(Comparator.reverseOrder());
System.out.println(list); // [twO, three, One]
list.sort(((x, y) -> x.compareTo(y))); // [One, three, twO]
System.out.println(list);
```

----

## Quelques nouvelles méthodes sur les Map (1)

```java
Map<Integer, String> map = new HashMap<>();
map.put(75, "Paris");
map.put(59, "Nord");
map.put(50, "Manche");
// forEach qui prend un BiConsumer en paramètre
map.forEach((k, v) -> System.out.println(k + " - " + v));

// replaceAll prend une BiFunction de type clé-valeur et retourne une nouvelle valeur,
// ici un String associée à la même clé
map.replaceAll((cp, dep) -> dep.toUpperCase());
System.out.println(map); // {50=MANCHE, 75=PARIS, 59=NORD}

// compute prend en paramètre une clé et une BiFunction de rampping comme pour replaceAll.
// Cette méthode permet de mettre à jour la valeur d'une seule clé
map.compute(75, (cp, dep) -> dep.toLowerCase());
System.out.println(map); // {50=MANCHE, 75=paris, 59=NORD}
// si la nouvelle valeur est nulle, la clé est retirée de la map
map.compute(75, (cp, dep) -> null);
System.out.println(map); // {50=MANCHE, 59=NORD}
map.compute(80, (cp, dep) -> "Somme");
System.out.println(map); // {50=MANCHE, 59=NORD}
```

----

## Quelques nouvelles méthodes sur les Map (2)

```java
// computeIfPresent, prend les mêmes paramètres que compute, et va agir si la clé est présente
// agit si la clé est présente et associée à une valeur non nulle
// comme pour compute, si la nouvelle valeur est nulle, la clé est retirée de la map
map.compute(59, (cp, dep) -> null);
map.compute(80, (cp, dep) -> "SOMME");
map.computeIfPresent(60, (cp, dep) -> "Oise");
System.out.println(map); // {80=SOMME, 50=MANCHE}

// computeIfAbsent, prend en paramètre une clé et une fonction de remapping
// qui ne sera exécuté que si la clé est absente de la map.
// computeIfAbsent retourne la valeur associée à la clé créé par la fonction pour une nouvelle clé,
// ou la valeur correspondant à la clé existante
map.computeIfAbsent(50, cp -> "Toto");
System.out.println(map); // {80=SOMME, 50=MANCHE}
map.computeIfAbsent(29, cp -> "Finistère");
System.out.println(map); // {80=SOMME, 50=MANCHE, 29=Finistère}
Map<String, List<String>> mapDeListe = new HashMap<>();
System.out.println(mapDeListe); // {}
mapDeListe.computeIfAbsent("Picardie", k -> new ArrayList<>()).add("Aisne");
System.out.println(mapDeListe); // {Picardie=[Aisne]}
mapDeListe.computeIfAbsent("Picardie", k -> new ArrayList<>()).add("Somme");
System.out.println(mapDeListe); // {Picardie=[Aisne, Somme]}
```

----

## Quelques nouvelles méthodes sur les Map (3)

```java
// merge, prend en paramètre une clé, une valeur
// et une BiFunction de fusion de la valeur si la clé existe déjà dans la map
Map<String, String> mapString = new HashMap<>();
BiFunction<String, String, String> biFunction = (oldValue, newValue) -> oldValue + "/" + newValue;
System.out.println(mapString); // {}
mapString.merge("Picardie", "Aisne", biFunction);
System.out.println(mapString); // {Picardie=Aisne}
mapString.merge("Picardie", "Somme", biFunction);
mapString.merge("IDF", "Paris", biFunction);
System.out.println(mapString); // {IDF=Paris, Picardie=Aisne/Somme}
```