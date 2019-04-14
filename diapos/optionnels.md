# Les optionnels

----

## Définition et intérêt

- existe depuis Java 8
- c'est un conteneur d'objet T : `Optional<T>`
- utilisation de valeurs optionnelles au lieu de valeurs `null`
- contient soit une instance de T, soit un optionnel vide
- l'idée est de protéger son code contre les `NullPointerException` lorsqu'une valeur peut être optionnelle sans avoir à vérifier si la variable est `null`

----

## Quand utiliser les optionnels ?

Attention à ne pas abuser des optionnels ! D’après Brian Goetz, créateur de cette classe, les optionnels sont à utiliser avec les **retours de méthodes** [(source)](http://stackoverflow.com/questions/26327957/should-java-8-getters-return-optional-type/26328555#26328555) :

- si la méthode retourne un container (liste, tableau, map...), alors il ne faut pas utiliser un optionel mais un container vide
- ne pas utiliser les optionnels pour les attributs de classe
- ne pas utiliser les optionnels pour les paramètres de méthode

```java
public class Person{
    private String prenom;

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public Optional<String> getPrenom() {
        return Optional.ofNullable(this.prenom);
    }
}
```

----

## Création d'un optionnel

- Optionnel null :

```java
Optional<String> optional = null; // NE DEVRAIT PAS ARRIVER
optional.isPresent(); // NullPointerException
```

- Optionnel vide :

```java
Optional<String> optional = Optional.empty(); // Optional.empty
```

- Optionnel non vide (`NullPointerException` si la valeur est null) :

```java
Optional<String> opt = Optional.of("Louis"); // Optional[Louis]
opt = Optional.of(""); // Optional[]
opt = Optional.of(null); // NullPointerException
```

- Optionnel vide ou contenant une valeur :

```java
Optional<String> opt = Optional.ofNullable("Louis"); // Optional[Louis]
Optional<String> opt = Optional.ofNullable(null); // Optional.empty
```

----

## Vérifier la présence d'une valeur

- méthode `isPresent()` retourne un `boolean` :

```java
Optional.ofNullable("Louis").isPresent() // true
Optional.ofNullable("").isPresent() // true
Optional.ofNullable(null).isPresent() // false
```

- méthode `isEmpty()` depuis Java 11 qui retourne l'inverse de `isPresent()`

----

## Réaliser une action conditionnelle

- avec la méthode `ifPresent()` qui va exécuter une fonction *Consumer* (méthode qui prend un unique argument T et ne retourne rien) si l'optionel n'est pas vide

```java
String nom = "Louis";

// avant Java 8
if (nom != null) {
    System.out.println(nom.length());
}

// avec les optionnels
Optional<String> optional = Optional.ofNullable(nom);
optional.ifPresent(name -> System.out.println(name.length()));
```

- à partir de Java 9, il existe une méthode `ifPresentOrElse()` qui correspond au if, else :

```java
Optional<String> optional = Optional.ofNullable("Louis");
optional.ifPresentOrElse(
    name -> System.out.println(name.length()),
    () -> System.out.println("le nom en paramètre est null")
);
```

----

## Valeurs par défaut

- la méthode `orElse(T t)` retourne la valeur de l'optionnel s'il est présent, la valeur spécifiée dans le orElse sinon :

```java
String res = Optional.ofNullable("Louis").orElse("nom par défaut"); // Louis
Optional<String> optional = Optional.ofNullable(null);
res = optional.orElse("nom par défaut"); // nom par défaut
```

- la méthode `orElseGet(Supplier<T>)` (fonction qui ne prend pas d'argument et retourne un T) fonctionne de la même manière que `orElse` mais de manière **lazy**, c'est-à-dire que la méthode par défaut n'est pas invoquée tant que la valeur est présente, alors qu'avec `orElse`, la méthode par défaut est créée et ne sert à rien :

```java
Optional<String> optional = Optional.ofNullable(null);
String res = optional.orElseGet(() -> "nom par défaut"); // nom par défaut

public String getDefaultValue(){
    System.out.println("passage dans la méthode par défaut");
    return "Default Value";
}
System.out.println("orElse :");
Optional.ofNullable("Louis").orElse(getDefaultValue()); // "orElse :" et "passage dans la méthode par défaut"
System.out.println("orElseGet :");
Optional.ofNullable("Louis").orElseGet(() -> getDefaultValue()); // uniquement "orElseGet :"
```

----

## Chaîner les optionnels

- à partir de Java 9, l'opérateur `or()` permet de chaîner l'appel à plusieurs méthodes si l'optionnel retourné est vide :
```java
// exemple avec 3 méthodes qui retournent toutes un optionnel de Customer
public Optional<Customer> findCustomer(String customerId) {
    return customers.findInMemory(customerId)
        .or(() -> customers.findOnDisk(customerId))
        .or(() -> customers.findRemotely(customerId));
}
```

----

## Lever une exception si l'optionnel est vide

- avec la méthode `orElseThrow(Supplier<Exception>)` :

```java
String res = Optional.ofNullable(null).orElseThrow(IllegalArgumentException::new);
```

- avec la méthode `get()`, qui retourne la valeur de l'optionnel s'il est présent, une `NoSuchElementException` sinon (RuntimeException) :

```java
String res = Optional.ofNullable(null).get(); // va lever une NoSuchElementException
```

Privilégier l'utilisation des exceptions personnalisées avec `orElseThrow()` car l'objectif des optionnels est de gérer proprement le cas des valeurs null ce que ne fait pas `get()` qui sera probablement *deprecated* dans une future version

----

## Les conditions

Les optionels permettent d'écrire les conditions différemment, sans devoir vérifier que l'objet est null et sans risquer d'avoir des `NullPointerException` :

```java
// avant Java 8
public static boolean isTeenager(Person person){
    return person != null && person.getAge() != null && person.getAge()>=10 && person.getAge()<20;
}

// avec les optionnels
public static boolean isTeenager2(Person person){
    return Optional.ofNullable(person).map(Person::getAge).filter(a -> a>=10 && a<20).isPresent();
}
```

La méthode `map()` a simplement permis de transformer l'objet personne en entier en ne conservant que l'attribut âge de la personne.

----

## Transfomer une valeur avec map()

- création d'une méthode qui retourne la taille d'une liste, où null si la liste est null :

```java
// avant Java 8
public static Integer sizeOfList(List<Person> liste){
    if(liste==null) return null;
    return liste.size();
}

// à l'identique avec les optionnels
public static Integer sizeOfList2(List<Person> liste){
    return Optional.ofNullable(liste).map(List::size).orElse(null);
}

// dans la philosophie des optionnels
// retourne un optionnel d'integer qui correspond à la taille de liste, un optionnel vide si la liste est null
public static Optional<Integer> sizeOfList3(List<Person> liste){
    return Optional.ofNullable(liste).map(List::size);
}

// alternative si on considère qu'une liste ne devrait pas être nulle
public static Integer sizeOfList4(List<Person> liste){
    return Optional.ofNullable(liste).map(List::size).orElseThrow(IllegalArgumentException::new);
}
```

----

## Transformer une valeur avec flatMap()

- lorsque l'on mappe un attribut d'un objet qui est un optionnel tout comme l'objet, on se retrouve avec un optionnel d'optionnel. Pour éviter cela, on peut utiliser `flatMap()` :

```java
public class Person{
    private String prenom;
    public Optional<String> getPrenom() {
        return Optional.ofNullable(this.prenom);
    }
}

Optional<Person> optionalPerson = Optional.ofNullable(new Person("Louis", null, true));
Optional<Optional<String>> optionalOfOptionalPrenom = optionalPerson.map(Person::getPrenom);
Optional<String> optionalOfPrenom = optionalPerson.flatMap(Person::getPrenom);
```

----

## Utiliser les stream sur les optionnels

- depuis Java 9, il est possible de transformer un optionnel en stream :

```java
// exemple avec une liste d'identifiants de personnes pour lesquels on souhaite récupérer un stream de Person

// méthode qui renvoie un Optional<Person> en fonction de l'id en paramètre
Optional<Person> findPerson(String personId);

public Stream<Person> findPersons(Collection<String> personIds) {
	return personIds.stream()
		.map(this::findPerson)
		// on a ici un Stream<Optional<Person>>
		.filter(Optional::isPresent)
		.map(Optional::get);
}

// en java 9
public Stream<Person> findPersons(Collection<String> personIds) {
	return personIds.stream()
		.map(this::findPerson)
        .flatMap(Optional::stream)
        // ou encore : personIds.stream().flatMap(id -> findPerson(id).stream());
}
```
