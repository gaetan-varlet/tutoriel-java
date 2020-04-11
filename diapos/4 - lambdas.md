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

## Ecrire des implémentations de Comparator

Exemples d'implémentations de Comparator avec des lambdas :

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

## Création de comparateurs

```java
User gaetan = new User("Gaëtan", 32);
User florine = new User("Florine", 30);
User louis = new User("Louis", 2);
User louisPetit = new User("Louis", 1);


// création d'un comparateur de User basé sur le nom
Comparator<User> comp = (u1, u2) -> u1.getName().compareTo(u2.getName());
// création du même comparateur en utilisant la méthode statique comparing qui prend une function en paramètre
Comparator<User> comp2 = Comparator.comparing(u -> u.getName());
// création d'un comparateur de User basé sur l'âge
Comparator<User> comp3 = Comparator.comparing(u -> u.getAge());
// création d'un comparateur de User sur le nom puis sur l'âge en les combinant
Comparator<User> compCombine = comp2.thenComparing(comp3);
// création d'un comparateur de User sur le nom puis sur l'âge décroissant en les combinant
Comparator<User> compCombine2 = comp2.thenComparing(comp3.reversed());

System.out.println("Gaëtan et Florine : " + comp.compare(gaetan, florine)); // 1
System.out.println("Gaëtan et Louis : " + comp.compare(gaetan, louis)); // -5
System.out.println("Louis et Florine : " + comp.compare(louis, florine)); // 6
System.out.println("Louis et Louis Petit : " + compCombine.compare(louis, louisPetit)); // 1
System.out.println("Louis et Louis Petit : " + compCombine2.compare(louis, louisPetit)); // -1
```

----

## Les références de méthodes

C'est une autre façon d'écrire une lamba expression
- meilleure lisibilité
- très léger gain de performance

```java
// lambda expression
Consumer<String> c1 = s -> System.out.println("hello " + s);
// référence de méthode
Consumer<String> c2 = System.out::println;
```

il existe 4 types de références de méthodes :
- **BoundInstance** : invocation d'uné méthode sur une instance d'un objet (exemple ci-dessus)
- **Static** : appel statique. Exemple : `d -> Math.sqrt(d);` => `Math::sqrt`
- **UnboundInstance** : invocation de la méthode sur le premier paramètre, le reste des paramètres est passé en paramètre de la méthode.
    - `(s1, s2) -> s1.indexOf(s2);` => `String::indexOf;`
- **Constructor** : `name -> new User(name);`, `() -> new User();` et `(name, age) -> new User(name, age);` peuvent toutes être écrit avec la référence de méthode `User::new;`