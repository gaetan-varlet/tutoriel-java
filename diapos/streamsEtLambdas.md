# Les streams

----

### Concepts

Les données sont généralement stockées dans une **Collection**
- pour accéder aux données, on peut utiliser le pattern **Iterator**
- depuis Java 8, on peut y accéder de manière beaucoup plus appronfondie avec l'API **Stream**
- on peut stocker dans une nouvelle collection le résultat du traitement réalisé avec un Stream grâce à l'API **Collector**

----

### Map / Filter / Reduce

- un stream est un objet vide, qui ne porte pas les données comme une collection, c'est donc un objet très léger
- un stream se connecte à une source de données et il va consommer les éléments de la source (collections, tableaux, chaînes de caractères, ligne d'un fichier...)
- un stream ne connaît pas le nombre d'éléments de la source, il faut consommer le stream pour compter les éléments
- un stream ne doit pas modifier la source de ces données

Les streams sont basés sur le pattern `Map/Filter/Reduce`. Il existe 2 types d'opérations :
- opérations intermédiaires : succession de stream (stream pipelines)
    - `Stream.filter` permet de filtrer les éléments d'une collection avec des `Predicate<T>`
    - `Stream.map` permet de choisir quel élément on veut récupérer dans notre stream avec une `Function<T, R>`. On peut aussi directement modifier ce qu'on va récupérer. Conserve le nombre d'objets ainsi que leur ordre.
- opérations terminales
    - `Stream.reduce` qui correspond souvent à une `BiFunction<T, U, R>` qui doit être associative
        - réductions simples : `Stream.sum`, `Stream.max`, `Stream.count`
        - réductions mutables
    - `Stream.collect` permet de récupérer notre résultat dans une collection

----

### Construction d'un stream

```java
Person p1 = new Person("Gaëtan", 30, true);
Person p2 = new Person("Florine", 29, false);		
Person p3 = new Person("Louis", 1, true);
Person p4 = new Person("Louis", 5, true);
Person p5 = new Person(null, 10, true);
List<Person> liste = Arrays.asList(p1, p2, p3, p4, p5);
Person[] tableau = {p1, p2, p3, p4, p5};

// construction d'un stream à partir d'une collection
Stream<Person> streamListe = liste.stream();
// construction d'un stream à partir d'un tableau
Stream<Person> streamTableau = Arrays.stream(tableau);
// construction d'un stream vide
Stream<Object> streamVide = Stream.empty();
// construction d'un stream en spécifiant des valeurs
Stream<String> streamValeurs = Stream.of("a", "b", "c");
// génération d'un stream avec n élément
Stream<String> streamGenerated = Stream.generate(() -> "element").limit(3); // element, element, element
Stream<Integer> streamIterated = Stream.iterate(40, n -> n + 2).limit(5); // 40, 42, 44, 46, 48
Stream<String> streamIterated = Stream.iterate("+", s -> s + "+").limit(5); // +, ++, +++, ++++, +++++
```

----

## Les streams de primitifs

ils sont plus performants que leurs homologues avec la classe wrapper

```java
Random random = new Random();
// création d'un IntStream avec les méthodes of et range
IntStream intStream = IntStream.of(1,2,3,4,5);
IntStream intStream1 = IntStream.range(1, 4); // 1 2 3
// création d'un IntStream avec 5 entiers aléatoires puis une deuxième avec 5 entiers aléatoires compris entre 1 (inclus) et 4 (exclus)
IntStream intStream2 = random.ints(5);
IntStream intStream3 = random.ints(5, 1, 4); // par exemple : 1 3 2 3 3

LongStream longStream = LongStream.range(1, 5); // 1 2 3 4
LongStream longStream2 = random.longs(2, 10, 15); // par exemple : 14 11

DoubleStream doubleStream = random.doubles(3); // génère 3 nombres aléatoires de type double
DoubleStream doubleStream2 = random.doubles(3, 0, 10); // génère 3 nombres aléatoires de type double entre 0 et 10
```

- passage des streams d'objets à des streams de nombres avec **mapToInt(i -> i)**, **mapToLong** et **mapToDouble**
- passage des streams de nombres à des streams d'objets avec **mapToObj(i -> i)** ou **boxed()**

----

## Le boxing

Lorsqu'on travaille avec des streams de primitifs, on peut les collecter dans des tableaux, mais pas dans des collections. Il faut d'abord "boxer" les éléments du stream

```java
// Création d'un IntStream (primitif) et d'un stream d'Integer
IntStream intStream = IntStream.of(1,2,3,4,5);
Stream<Integer> streamOfInteger = intStream.boxed();
Stream<Integer> streamOfInteger = Stream.of(1, 2, 3, 4);

// Collecte d'un IntStream dans un tableau et dans une liste
int[] tabInt = IntStream.of(1, 2, 3).toArray();
List<Integer> listeInteger = IntStream.of(1, 2, 3).boxed().collect(Collectors.toList());

// Collecte d'un Stream d'Integer dans un tableau et dans une liste
Integer[] tabInteger = Stream.of(1, 2, 3, 4).toArray(Integer[]::new);
int[] tabInt = Stream.of(1, 2, 3, 4, 5).mapToInt(i -> i).toArray(); // conversion en int des Integer pour les stocker dans un tableau de int
List<Integer> listeInteger = Stream.of(1, 2, 3, 4, 5).collect(Collectors.toList());

// Création d'un tableau de String à partir d'un stream de String
String[] tabString = Stream.of("a", "b").toArray(String[]::new);
```

----

## Découpage d'une chaîne de caractère en Stream

```java
// Découper une chaîne de caractères selon une regex
String hello = "Bonjour le monde";
String[] tab = hello.split(" "); // [Bonjour, le, monde]
Pattern pattern = Pattern.compile(" ");
// le découpage se fait entièrement pour stocker le résultat dans le tableau
String[] tab2 = pattern.split(hello); // [Bonjour, le, monde]
// le découpage se fait au fur et à mesure que le stream en à besoin
Stream<String> stream = pattern.splitAsStream(hello);

// Découper une chaîne de caractères par lettre
byte[] b = hello.getBytes(); // 66 111 110 106 111 117 114 32 108 101 32...
IntStream intStream = hello.chars(); // 66 111 110 106 111 117 114 32 108 101 32...
Stream<String> streamString = hello.chars().mapToObj(lettre -> Character.toString(lettre)); // B o n j o u r   l e   m o n d e
```

----

### Transformer une liste en chaîne de caractères

Ecrire les prénoms de la liste dans une chaîne de caractères :

```java
String maChaine = liste.stream()
	.map(Person::getPrenom)
	.collect(Collectors.joining(";")); // Gaëtan;Florine;Louis;Louis;null
```

Ajout d'un préfixe et d'un suffixe :

```java
String maChaine = liste.stream()
    .filter(p -> p.getPrenom() != null)
    .map(Person::getPrenom)
    .distinct()
    .collect(Collectors.joining(", ", "Les prénoms sont : ",".")); // Les prénoms sont : Gaëtan, Florine, Louis.
```

----

### Stream d'un fichier

Lecture d'un fichier en stream pour ne pas le monter entièrement en mémoire

```java
/*
aze
qsd
wxc
*/
Path path = Paths.get("src/main/resources/test.txt");
try(Stream<String> streamOfFile = Files.lines(path)){
    streamOfFile.forEach(System.out::println); // impression de chaque ligne dans la console
}

// il est possible de spécifier l'encodage du fichier (par défaut UTF_8)
Stream<String> streamOfFile = Files.lines(path, StandardCharsets.ISO_8859_1);
```

----

### Filtrer, mapper, trier et afficher

```java
liste.stream()
    .filter(p -> p.getPrenom() != null) // filtrage sur les prénoms non null
    .map(p -> p.getPrenom().toUpperCase()) // mapping : on ne conserve que le prénom que l'on met en majuscules
    .sorted() // tri sur l'ordre naturel, ici l'ordre alphabétique
    //.sorted(Comparator.reverseOrder()) // tri sur l'ordre inverse de l'ordre naturel
    .forEach(System.out::println); // impression des prénoms dans la console : FLORINE GAËTAN LOUIS LOUIS
```

----

### Mapper, supprimer les doublons, puis collecter dans une liste

```java
List<String> listePrenom = liste.stream()
    .map(Person::getPrenom)
    .distinct()
    .collect(Collectors.toList()); // [Gaëtan, Florine, Louis, null]
```

----

### Enchaînement d'instructions dans le map : utilisation des accolades, du point-virgule et du return

```java
List<String> listePrenom = liste.stream()
    .map(p -> {
	    return p.getPrenom();
	    })
	.distinct()
	.collect(Collectors.toList()); // [Gaëtan, Florine, Louis, null]
```

```java
List<Person> listePrenom = liste.stream()
    .map(p -> {
        if(p.getPrenom() == null) {
            p.setPrenom("Prénom par défaut");
        }
        return p;
    })
    .collect(Collectors.toList());
```

### Compter le nombre d'éléments filtrés
```java
long a = liste.stream()
.filter(p -> p.getPrenom() != null) // équivalent à .filter(p -> !(p.getPrenom() == null))
.count(); // 4 personnes ont un prénom non null
 ```

----

### Trier sur un ordre non naturel
```java
List<Person> listeTrie = liste.stream()
// tri des personnes dans l'ordre croissant de l'âge
.sorted(Comparator.comparing(Person::getAge))
//.sorted((p1, p2) -> (p1.getAge() - p2.getAge())) // équivalent à la ligne précédente
.collect(Collectors.toList());
```

```java
List<Person> listeTrie = liste.stream()
// tri sur le prénom par ordre alphabétique en mettant les prénoms null à la fin
.sorted(Comparator.comparing(Person::getPrenom, Comparator.nullsLast(Comparator.naturalOrder())))
.collect(Collectors.toList());
```

```java
List<Person> listeTrie = liste.stream()
// tri sur le prénom par ordre alphabétique puis sur l'âge par ordre décroissant
.sorted(Comparator.comparing(Person::getPrenom).thenComparing((Comparator.comparing(Person::getAge).reversed())))
.collect(Collectors.toList());
```

----

### Limiter le nombre de résultat aux n premiers

```java
// récupère les 2 persones les plus âgées
List<Person> listeTrie = liste.stream()
	.sorted(Comparator.comparing(Person::getAge).reversed())
	.limit(2)
	.collect(Collectors.toList());
 ```

 ### Ne pas garder les n premiers

```java
List<Person> listeTrie = liste.stream()
	.sorted(Comparator.comparing(Person::getAge).reversed())
	.skip(3) // ne garde pas les 3 premiers
	.collect(Collectors.toList());
 ```

----

### Récupérer le max ou le min
```java
// création d'un comparateur de personne selon l'âge
//Comparator<Person> comparator = Comparator.comparing(Person::getAge);

// personne la plus âgée
Person personnePlusAgee = liste.stream()
	.max(Comparator.comparing(Person::getAge)).get();

String maxChar = Stream.of("H", "T", "D", "I", "J")
    .max(Comparator.comparing(String::valueOf)).get();

Integer maxNumber =  Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
    .max(Comparator.comparing(Integer::valueOf)).get();

// transformer le stream en IntStream permet évite d'avoir à définir le comparateur
Integer maxNumber =  Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
	.mapToInt(i -> i).max().getAsInt();

Integer sum =  Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9).mapToInt(i -> i).sum();
Double mean =  Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9).mapToInt(i -> i).average().getAsDouble();
```

----

### Faire la moyenne et la somme

```java
// ces méthodes fonctionnent sur les types primitifs et sur les classes wrappers
// il n'y a pas besoin de faire une opération map avant l'opération de collecte
Double ageMoyen = liste.stream().collect(Collectors.averagingInt(Person::getAge));
Integer sommeAge = liste.stream().collect(Collectors.summingInt(Person::getAge));
```

### Calculer des statistiques sur une liste de nombres
```java
List<Integer> liste = Arrays.asList(1,2,3);
IntSummaryStatistics stats = liste.stream().mapToInt(i->i).summaryStatistics();
long nbElements = stats.getCount();
double moyenne = stats.getAverage();
int min = stats.getMin();
int max = stats.getMax();
long somme = stats.getSum();
```

----

### Aplatir une liste de liste en liste

```java
List<List<String>> listeDeListe = Arrays.asList(Arrays.asList("A", "B"), Arrays.asList("C", "D"));
List<String> liste = listeDeListe.stream().flatMap(l -> l.stream()).collect(Collectors.toList()); // [A, B, C, D]
```

----

### Collecter dans une map

```java
// Création d'un map à partir d'une liste où on met l'id en clé et l'objet en valeur
// ATTENTION : si la clé de la map n'est pas unique, il y aura une erreur
Map<Integer, Person> map = liste.stream()
	.collect(Collectors.toMap(Person::getId, Function.identity()));
	//.collect(Collectors.toMap(p -> p.getId(), p -> p));

// si la clé est en double, il faut dire si on souhaite garder l'ancienne ou la nouvelle valeur
// sinon faire un groupingBy pour avoir une Map<String, List<Person>>
Map<String, Person> map2 = liste.stream()
    .sorted(Comparator.comparing(Person::getAge).reversed())
    .collect(Collectors.toMap(Person::getPrenom, Function.identity(), (oldValue, newValue) -> oldValue));

// parcours d'une map avec entrySet()
map.entrySet().stream().limit(2).forEach(e -> System.out.println(e.getKey() + " - " + e.getValue()));
```

----

### Transformer une liste en map avec groupingBy et partitioningBy

`groupingBy` permet de créer N groupes de différents types, alors que `partitioningBy` permet de créer 2 groupes (true et false) à partir d'un booléen ou d'un précidat

```java
// Création d'une Map<Boolean, List<Person>> avec le booléen estUnHomme en clé et une liste de personnes en valeur
Map<Boolean, List<Person>> map = liste.stream()
	.collect(Collectors.groupingBy(Person::isEstHomme));

// Création d'une Map<Boolean, Set<String>> avec le booléen estUnHomme en clé et un ensemble de prénoms en valeur
Map<Boolean, Set<String>> map = liste.stream()
	.filter(p -> p.getPrenom() != null)
	.collect(Collectors.groupingBy(Person::isEstHomme, Collectors.mapping(Person::getPrenom, Collectors.toSet())));
// {false=[Florine], true=[null, Gaëtan, Louis]}
```

```java
// Création d'une map avec 2 clés : true et false selon un prédicat
Map<Boolean, List<Person>> map = liste.stream()
	.collect(Collectors.partitioningBy(p -> p.getAge()>18));

Map<Boolean, List<Person>> map = liste.stream()
	.collect(Collectors.partitioningBy(Person::isEstHomme));
```

----

### Savoir l'attribut le plus représenté

```java
String prenomLePlusFrequent = liste.stream()
    .filter(p -> p.getPrenom() != null)
    .collect(Collectors.groupingBy(Person::getPrenom, Collectors.counting()))
    .entrySet()
    .stream()
    .max(Comparator.comparingLong(Entry::getValue))
    .map(Entry::getKey)
    .get(); // Louis
```

----

### Débugger avec peek()

```java
liste.stream()
    .peek(p -> System.out.println("avant filtrage : "+p))
    .filter(p -> p.getPrenom() != null)
    .peek(p -> System.out.println("après filtrage : "+p))
    .map(Person::getPrenom)
    .peek(p -> System.out.println("avant mapping : "+p))
    .collect(Collectors.toList());

// console :
avant filtrage : Person [prenom=Gaëtan, age=30, estHomme=true]
après filtrage : Person [prenom=Gaëtan, age=30, estHomme=true]
avant mapping : Gaëtan
avant filtrage : Person [prenom=Florine, age=29, estHomme=false]
après filtrage : Person [prenom=Florine, age=29, estHomme=false]
avant mapping : Florine
avant filtrage : Person [prenom=Louis, age=1, estHomme=true]
après filtrage : Person [prenom=Louis, age=1, estHomme=true]
avant mapping : Louis
avant filtrage : Person [prenom=Louis, age=5, estHomme=true]
après filtrage : Person [prenom=Louis, age=5, estHomme=true]
avant mapping : Louis
avant filtrage : Person [prenom=null, age=10, estHomme=true]
```

----

### Utilisation de l'index courant

```java
String[] names = { "Gaëtan", "Florine", "Louis", "Kévin", "Thibaut" };

// récupération de l'indice à partir dun IntStream qui va de 0 à length-1
// et utilisation d'un mapToObj pour récupérer l'élément courant
List<String> l1 = IntStream.range(0, names.length)
    .filter(i -> names[i].length() <= 5)
    .mapToObj(i -> names[i])
    .collect(Collectors.toList()); // [Louis, Kévin]

// équivalent à :
List<String> l2 = Arrays.stream(names)
    .filter(name -> name.length() <= 5)
    .collect(Collectors.toList()); // [Louis, Kévin]
```

----

### findFirst, findAny, anyMatch, allMatch, noneMatch

```java
// renvoie le premier élément du stream, que l'on soit en parallel stream ou non
Optional<Person> person2 = liste.stream().filter(p -> p.getAge() >= 18).findFirst();
// renvoie n'importe quel élément du stream. Sans parallel stream, c'est généralement le premier élément mais ce n'est pas garanti
Optional<Person> person1 = liste.stream().filter(p -> p.getAge() >= 18).findAny();

// true si tous les éléments du flux correspondent au prédicat ou si le flux est vide
boolean test1 = liste.stream().allMatch(p -> p.getAge() >= 18); // false
// true si n'importe quel élément du flux correspond
boolean test2 = liste.stream().anyMatch(p -> p.getAge() >= 18); // true
// true si aucun élément du flux ne correspond ou si le flux est vide
boolean test3 = liste.stream().noneMatch(p -> p.getAge() >= 18); // false
boolean test4 = liste.stream().noneMatch(p -> p.getAge() >= 31); // true
```

----

### La méthode reduce

```java
// on précise l'accumulateur dans reduce qui est une fonction qui spécifie la logique d'agrégation 
OptionalInt reduced = IntStream.range(1, 4).reduce((a, b) -> a + b); // 6 = 1 + 2 + 3
// équivalent
OptionalInt reduced1 = IntStream.range(1, 4).reduce(Integer::sum); // 6 = 1 + 2 + 3
// ajout de l'identity qui est une valeur initiale ou par défaut ajouté à l'opération
// on récupère donc un int au lieu d'un optional car on est sûr qu'il y a une valeur
int reduced2 =IntStream.range(1, 4).reduce(10, (a, b) -> a + b); // 16 = 10 + 1 + 2 + 3
```

----

### Obtenir une collection non modifiable

```java
Set<Person> unmodifiableSet = liste.stream()
	.collect(Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet));
```

### Choisir l'implémentation de notre collection

```java
// exemple où l'on force à utiliser une LinkedHashSet
Set<Person> unmodifiableSet = liste.stream()
	.collect(Collectors.toCollection(LinkedHashSet::new));
```

----

### Streams vs Parallel Streams

- les opérations peuvent être **stateless** (traitements sur les éléments un à un sans prendre en compte les autres éléments) ou **stateful**
- les opérations **stateful** ont besoin de connaître l'ensemble du stream (par exemple, *distinct* ou *sorted*), il ne faut donc pas paralléliser ses streams

```java
// création d'un parallel stream à partir d'une liste
liste.parallelStream() // au lieu de liste.stream()

// si on part directement d'un stream, il faut utiliser la méthode parallel()
Stream<Person> stream = Stream.of(p1, p2);
stream.parallel()...

// pour savoir si le stream est parallel (true, false)
stream.isParallel()
```
