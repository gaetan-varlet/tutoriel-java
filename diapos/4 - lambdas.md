# Lambda expressions et interfaces fonctionnelles

----

## Interface fonctionnelle

- interface qui ne possède qu'une seule méthode

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

## BiConsumer et BiPredicate : une lambda à 2 paramètres

```java
// comme Consummer mais avec 2 paramètres
public interface BiConsumer<T,U> {
    public void accept(T t, U u);
}
public interface BiPredicate<T,U> {
    public void test(T t, U u);
}

BiConsumer<String, Integer> bc = (s, i) -> System.out.println(s + " - " + i);
bc.accept("a", 2); // a - 2
BiPredicate<String, String> bp = (s1, s2) -> s1.contains(s2);
System.out.println(bp.test("toto", "to")); // true
System.out.println(bp.test("toto", "ta")); // false
```