# Les streams et les lambdas

----

### Concepts
- on l'appelle généralement sur une collection
- 2 types d'opérations
  - opérations intermédiaires : succession de stream (stream pipelines)
    - `Stream.filter` permet de filtrer les éléments d'une collection
    - `Stream.map` permet de choisir quel élément on veut récupérer dans notre stream. On peut aussi directement modifier ce qu'on va récupérer
  - opérations terminales
    - `Stream.reduce`
      - réductions simples : `Stream.sum`, `Stream.max`, `Stream.count`
      - réductions mutables
    - `Stream.collect` permet de récupérer notre résultat dans une collection

----

### Création d'une liste d'objets

```java
Person p1 = new Person("Gaëtan", 30, true);
Person p2 = new Person("Florine", 29, false);		
Person p3 = new Person("Louis", 1, true);
Person p4 = new Person("Louis", 5, true);
Person p5 = new Person(null, 10, true);
List<Person> liste = Arrays.asList(p1, p2, p3, p4, p5);
```

### Filtrer, mapper, trier et afficher
```java
liste.stream()
    .filter(p -> p.getPrenom() != null) // filtrage sur les prénoms non null
    .map(p -> p.getPrenom().toUpperCase()) // mapping : on ne conserve que le prénom que l'on met en majuscules
    .sorted() // tri sur l'ordre naturel, ici l'ordre alphabétique
    //.sorted(Comparator.reverseOrder()) // tri sur l'ordre inverse de l'ordre naturel
    .forEach(System.out::println); // impression des prénoms dans la console : FLORINE GAËTAN LOUIS LOUIS
```

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
.filter(p -> p.getPrenom() != null)
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

### Récupérer le max ou le min (utilisation de GET)
```java
// création d'un comparateur de personne selon l'âge
//Comparator<Person> comparator = Comparator.comparing(Person::getAge);

// personne la plus âgée
Person personnePlusAgee = liste.stream()
	.max(Comparator.comparing(Person::getAge))
	.get();

Integer maxNumber =  Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
    .max(Comparator.comparing(Integer::valueOf)).get();

String maxChar = Stream.of("H", "T", "D", "I", "J")
    .max(Comparator.comparing(String::valueOf)).get();
```

----

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

### Transformer la liste en map selon une clé

Création d'une `Map<Boolean, List<Person>>` avec le booléen estUnHomme en clé et une liste de personnes en valeur :

```java
Map<Boolean, List<Person>> map = liste.stream()
	.collect(Collectors.groupingBy(Person::isEstHomme));
```

Création d'une `Map<Boolean, Set<String>>` avec le booléen estUnHomme en clé et un ensemble de prénoms en valeur :

```java
Map<Boolean, Set<String>> map = liste.stream()
	.filter(p -> p.getPrenom() != null)
	.collect(Collectors.groupingBy(Person::isEstHomme, Collectors.mapping(Person::getPrenom, Collectors.toSet())));
// {false=[Florine], true=[null, Gaëtan, Louis]}
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

### Création d'un Stream

```java
// Création d'un stream avec données et d'un stream vide
Stream<String> stream = Stream.of("a","b");
Stream<String> streamEmpty = Stream.empty();
// Conversion du stream en tableau
System.out.println(Arrays.toString(stream.toArray())); // [a, b]
System.out.println(Arrays.toString(streamEmpty.toArray())); // []

// Création d'un stream à partir d'un tableau
String[] arr = new String[]{"a", "b", "c"};
Stream<String> streamOfArrayFull = Arrays.stream(arr);

// Généréer un stream avec n élément
Stream<String> streamGenerated = Stream.generate(() -> "element").limit(3); // element, element, element
Stream<Integer> streamIterated = Stream.iterate(40, n -> n + 2).limit(5); // 40, 42, 44, 46, 48

// Générer des streams de primitifs
IntStream intStream = IntStream.range(1, 4); // 1, 2, 3

// Générer n nombres aléatoires
DoubleStream doubleStream = random.doubles(3); // génère 3 nombres aléatoires de type double
```

----

## Le boxing

Lorsqu'on travaille avec des streams de primitifs, on peut les collecter dans des tableaux, mais pas dans des collections. Il faut d'abord "boxer" les éléments du stream

```java
IntStream intStream = IntStream.of(1,2,3,4,5);
Stream<Integer> streamOfInteger = intStream.boxed();

int[] tab = IntStream.of(1,2,3,4,5).toArray(); // [1, 2, 3, 4, 5]
List<Integer> liste = IntStream.of(1,2,3,4,5).boxed().collect(Collectors.toList()); // [1, 2, 3, 4, 5]
```