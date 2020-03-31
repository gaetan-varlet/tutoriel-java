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

Les *Collection* Java sont des implémentations de l'interface `Collection<T>`. C'est un ensemble dans lequel on peut :
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
- exemple : `List<T> list = new ArrayList<>();` et `List<T> list = new LinkedList<>();`
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
- avec l'ArrayList, l'accès à un élément via son index est très rapide, en revanche ajouter un élément au mileu de la liste est lourd car il faut décaler tous les éléments
- avec la LinkedList, l'accès à un élément est long, en revanche l'ajout d'un élément au milieu de la liste est rapide car il s'agit d'un décalage de pointeur

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

## Les tables de hachage (1)

- c'est une table à deux colonnes avec une clé et une valeur
- les clés doivent être uniques, les valeurs peuvent être présentes plusieurs fois
- création d'une Map avec l'implémentation de référence : `Map<T,V> map = new HashMap<>();`
- les principales méthodes :
    - récupérer une valeur en précisant la clé :
        - `map.get(T) -> V`,
        - retourne `null` si la clé est absente
        - il est possible de stocker une valeur nulle pour une clé, ce qui fait qu'on ne sait pas lorsqu'on récupère null si la clé est absente de la map ou si la valeur associée à cette clé est nulle
        - `ma.getOrDefault(T, "valeur par défaut")` retourne V si la clé T est présente, la valeur par défaut sinon
    - ajouter une paire clé-valeur :
        - `map.put(K, V)` ajoute la clé K et la valeur V à la map
        - si la clé est déjà présente, la valeur V va remplacer l'ancienne valeur
        -  `map.putIfAbsent(K, V);` teste si la clé est déjà présente et ajoute la paire clé-valeur seulement si la clé n'est pas présente ou si la valeur associée est `null`
        - `map.putAll(otherMap)` ajoute le contenu d'une autre map dans map
    - remplacer
        - `map.replace(K, V)` associe la valeur V à la clé K uniquement si la clé est déjà présente dans la map
        - `map.replace(K, V1, V2)` associe la valeur V2 à la clé K uniquement si la clé est déjà présente dans la map associée à la valeur V1
    - retirer
        - `map.remove(K)` supprime la paire clé-valeur correspondant à la clé K
        - `map.remove(K, V)` supprime la paire clé-valeur correspondant à la clé K uniquement si la valeur associée vaut V

----

## Les tables de hachage (2)

- les principales méthodes (suite) :
    - tester
        - `map.containsKey(K)` retourne un booléen si la clé K est présente dans la map
        - `map.containsValue(V)` retourne un booléen si la valeur V est présente dans la map
    - cardinal : `map.size()`
    - effacer le contenu de la map : `map.clear()`
    - itérer :
        - itération sur les clés : `Set<V> keys = map.keySet()`
        - itération sur les valeurs : `Collection<V> values = map.values()`
        - itération sur les paires clé-valeur : `Set<Map.Entry<K,V>> entries = map.entrySet()`, qui est une vue qui permet de mettre à jour la map (mais pas possibilité d'ajout via la vue). L'objet `Map.Entry` a plusieurs méthodes :
            - `entry.getKey() -> K`
            - `entry.getValue() -> V`
            - `entry.setValue(V)` permet de mettre à jour la valeur associée à la clé K

----

## SortedMap et NavigableMap

- l'interface `NavigableMap` étend l'interface `SortedMap` qui elle-même étend l'interface `Map`
- l'implémentation par défaut de ces 2 interface est une TreeMap : `NavigableMap<T,V> map = new TreeMap<>();`
- basé sur l'algorithme `Red–black tree`
- les clés sont triées : même principe que pour les *SortedSet* avec *Comparable* et *Comparator*
- les principales méthodes :
    - `map.firstKey()`
    - `map.lastKey()`
    - `map.subMap(fromKey, toKey)` qui permet d'extraire une sous-map de la map principale

----

## Les buckets dans une HashMap

- mécanisme optimisé qui fait qu'aller chercher une paire clé-valeur prend toujours le même temps quelque soit la taille de la map

----

## Créer des map pré-remplies

```java
Map<K,V> copyOfMap = new HashMap<>(otherMap);
// création de Map immutables
Map<K,V> map1 = Map.of(K1,V1, K2,V2, K3,V3);
Map<K,V> map2 = Map.ofEntries(Map.entry(K1,V1), Map.entry(K2,V2), Map.entry(K3,V3));
```

----

## Classes obsolètes de l'API

Il ne faut plus utiliser les classes `Vector` et `Stack`, et les classes `Hashtable` et `Dictionnary`.  
Tout ce qu'elles font est fait de façon plus performante avec `ArrayList` et `HashMap`.

----

## Arrays et Collections

classes *Factory* avec plein de méthodes statiques qui permettent de faire plein de traitements comme du tri.

----

## Complexité algorithmique des collections

- https://www.baeldung.com/java-collections-complexity
- https://gist.github.com/psayre23/c30a821239f4818b0709