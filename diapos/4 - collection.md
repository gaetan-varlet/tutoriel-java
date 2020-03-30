# L'API Collection

----

## Intérêt des tableaux

Les collections, comme les tableaux, permettent de stocker des données en mémoire. Les 3 grandes sources de récupération de données sont :
- des bases de données (en utilisant l'API JDBC du JDK)
- des services Web (en utilisant le client HTTP du JDK)
- des fichiers sur le disque (en utilisant Java I/O)

## Limitation des tableaux

- gestion de l'index du tableau
- taille fixe, gestion d'un tableau plus grand quand on arrive à la limite
- gestion des trous lors de la suppression d'un élément

Les collections vont gérer ces problèmes pour nous

----

## Collection

une `Collection` est une interface `Collection<T>`. C'est un ensemble dans lequel on peut :
- `add(T)` ajouter un élément, `addAll(Collection<T>)` fait l'union des 2 collections, `retainAll(Collection<T>)` fait l'intersection entre les 2 collections
- `remove(T)` retirer un élément, `removeAll(Collection<T>)`
- `contains(T)` tester si un élément est présent, `containsAll(Collection<T>)` tester si tous les les éléments de la collection en paramètre sont présents
- `size()` demander le nombre d'éléments dans la collection
- `clear()` effacer le contenu de la collection
- `toArray()` permet de mettre dans un tableau les éléments de la collection
- itérer sur la collection (qui est non ordonnée) avec `Ierator` et `ForEach`

La taille d'une collection est extensible.

Pout créer une collection, il faut utiliser une implémentation : `Collection<T> c = new ArrayList<>()`

----

## Itérer sur les éléments d'une collection

**Pattern itérator**

- création d'un itérator : `Iterator<T> it = c.iteror()`
- 2 méthodes :
    - `hasNext()` renvoie un booléen pour savoir s'il y a encore des éléments dans la collection
    - `next()` renvoie l'élément suivant. S'il n'y a plus d'élément, une erreur sera générée

```java
while(it.hasNext()){
    System.out.println(it.next());
}
```

**Pattern ForEach**

```java
for(T t : c){
    System.out.println(t);
}
```

----

## Supprimer un élément d'une collection en itérant


```java
while(it.hasNext()){
    T element = t.next();
    if(...){
        it.remove(); // on ne peut pas faire c.remove(element), une exception serait levée
    }
}
```

----

## Implémentation de l'interface Iterator

```java
Collection<T> al = new ArrayList<>();
Collection<T> ll = new LinkedList<>();

Iterator<T> it1 = al.iteror();
Iterator<T> it2 = ll.iteror();
```

`it1` et `it2` sont tous les 2 des objets `Iterator` (interface) mais le code de ces 2 objets est différent car il est défini dans chacune des implémentations des 2 listes.

----

## Interdire les doublons dans une Collection avec un Set

- l'interface `Set` étend l'interface `Collection`
- exemple : `Set<T> set = new HashSet<>();`
- `HashSet` est l'unique implémentation de `Set` fournie pa le JDK
- pas de nouvelles méthodes par rapport à `Collection`
- change le comportement de `Collection` en interdisant d'avoir des doublons
- la méthode `add()` retourne un booléen pour dire si l'ajout s'est bien réalisé. Elle retourne `false` si on essaie d'ajouter un élément déjà présent dans un `Set` (toujours `true` dans le cas d'une liste)
- pour savoir si un objet est déjà présent, le JDK utilise les méthodes `equals()` et `hashCode()`
    - 2 objets qui sont `equals` doivent avoir le même `hashCode`
    - 2 objets qui ont 2 `hashCode` différents sont différents
    - `HashSet` calcule le `hashCode` de chaque objet et les compare. Si les `hashCode` sont différents, on sait que les objets sont différents
    - si 2 objets ont le même `hashCode`, ils peuvent être égaux, mais pas nécessairement. A ce moment-là, `HashSet` va calculer la méthode `equals` pour voir si les 2 objets sont égaux.
    - comparer les `hashCode` est une optimisation car c'est beaucoup rapide de les comparer (un entier) que d'utiliser la méthode `equals`
    - il faut bien redéfinir `hashCode()` lorsqu'on redéfini `equals()` pour s'assurer du bon fonctionnement du `HashSet`

----

## L'interface List

- l'interface `List` étend l'interface `Collection`
- exemple : `List<T> list = new ArrayList<>();`
- les éléments d'une liste sont ordonnées, avec un index (entre 0 et n-1)
- nouvelles méthodes par rapport à `Collection` :
    - `get(i)` renvoie l'élément à l'index i
    - `remove(i)` supprime l'élément à l'index i
    - `set(i, T)` remplace l'élément à l'index i par T
    - `add(i, T)` ajoute T à l'index i et décale les éléments suivants
    - `subList(fromIndexInclusive, toIndexExclusive)` renvoie une vue sur la portion de la liste sélectionnée
    - `indexOf(T)` retourne l'index de l'objet passé en paramètre
- possibilité également d'utiliser une `ListIterator` avec la méthode `listIterator()`. Cet objet a des méthodes en plus qu'`Iterator` :
    - `hasPrevious()` et `previous()` ce qui permet d'itérer dans le sens inverse.
    - et 2 autres méthodes qui permettent d'obtenir les index suivant et précédent : `nextIndex()` et `previousIndex`

----

## L'interface SortedSet

- l'interface `SortedSet` étend l'interface `Set`
- l'implémentation est un `TreeSet` : `SortedSet<T> set = new TreeSet<>();`
- en plus du `Set`, les éléments sont automatiquement triés dans l'ordre croissant (ce qui est différent d'une liste ou les éléments sont ordonnés via leur index dans l'ordre où ils sont ajoutés
- les éléments doivent étendre l'interface `Comparable`, ou alors le `SortedSet` doit être créé avec un `Comparator` pour que le `SortedSet` puisse les trier

----

## NavigableSet

- l'interface `NavigableSet` étend l'interface `SortedSet`
- l'implémentation fournie est également un `TreeSet` : `NavigableSet<T> set = new TreeSet<>();`
- l'interface est plus récente que `SortedSet` qui n'a plus d'intérêt à être utilisée
- méthodes en plus par rapport à `SortedSet` :
    - `headSet()` et `tailSet()` retourne une vue contenant les éléments plus petits (et plus grand) que l'élément passé en paramètre
    - `ceiling(T)`, `floor(T)`, `higher(T)` et `lower(T)` retournent le plus petit élément égal ou plus grand (ou strictement plus grand) que l'élément passé en paramètre, et respectivement l'inverse

----

## Créer des collections préremplies

Création de collections immutables :
- `List<String> list = Arrays.asList("a", "b", "c");` => les listes ne sont pas modifiables, les méthodes `add()` et `remove()` ne fonctionnent pas. En revanche il est possible de remplacer des éléments
- `List.of("a", "b", "c")` et `Set.of("a", "b", "c")` sont immutables, on ne peut pas ajouter, retirer ou remplacer les éléments de ces collections

Création d'une liste mutable par copie (possibilité de faire la même chose avec un Set) :

```java
List<String> list1 = Arrays.asList("a", "b", "c");`
List<String> list2 = new ArrayList(list1);
```

----

## Queue et Deque

- c'est une file d'attente, `Queue` étend l'interface `Collection`
- sert de tampon entre une source d'objets et un consommateur de ces objets
- 3 principaux types de méthodes :
    - ajout d'un objet dans la file
    - examen de l'objet suivant disponible
    - consommation de l'objet suivant disponible et retrait de la liste
- files d'attente FIFO (premier entré, premier sorti) et LIFO (dernier entré, premier sorti)
- `Deque` étend `Queue`, avec la possibilité d'ajouter des éléments des éléments au début ou à la fin de la file
- interfaces étendues dans `l'API Concurrent` avec `Blocking` dans leur nom et c'est dans ce domaine qu'elles sont le plus importante


----

## Les tables de hachage

- c'est une table à deux colonnes avec une clé et une valeur
- les clés doivent être unique, les valeurs peuvent être présentes plusieurs fois
- les principales méthodes :
    - ajouter
    - retirer
    - remplacer
    - cardinal
    - effacer
    - itérer
    - tester