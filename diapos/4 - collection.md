# L'API Collection

permet de stocker des données en mémoire

----

## Limitation des tableaux

- gestion de l'index du tableau
- taille fixe, gestion d'un tableau plus grand quand on arrive à la limite
- gestion des trous lors de la suppression d'un élément

Les collections vont gérer ces problèmes pour nous

----

## Collection

une `Collection` est une interface `Collection<T>`. C'est un ensemble dans lequel on peut :
- `add(T)` ajouter un élément, `addAll(Collection<T>)` fait l'union des 2 collections, `retainAll(Collection<T>)` fait l'intersection entre les 2 collections
- `remove(Object)` retirer un élément, `removeAll(Collection<Object>)`
- `contains(Object)` tester si un élément est présent, `containsAll(Collection<Object>)` tester si tous les les éléments de la collection en paramètre sont présents
- `size()` demander le nombre d'éléments dans la collection
- `clear()` effacer le contenu de la collection
- `toArray()` permet de mettre dans un tableau les éléments de la collection
- itérer sur la collection (non ordonné) avec `Ierator` et `ForEach`

La taille d'une collection est extensible.

Pout créer une collection, il faut utiliser une implémentation : `Collection<String> c = new ArrayList<>()`

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
Collection<String> al = new ArrayList<>();
Collection<String> ll = new LinkedList<>();

Iterator<T> it1 = al.iteror();
Iterator<T> it2 = ll.iteror();
```

`it1` et `it2` sont tous les 2 des objets `Iterator` (interface) mais le code de ces 2 objets est différent car il est défini dans chacune des implémentations des 2 listes.

----

## Interdire les doublons dans une Collection avec un Set

- l'interface `Set` implémente l'interface `Collection`
- exemple : `Set<String> set = new HashSet<>();`
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