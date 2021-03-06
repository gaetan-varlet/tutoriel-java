# Les exceptions

----

## Les erreurs

- les **bugs** : doivent être corrigé, par exemple la nom protection contre une *NullPointerException*
- les problèmes lié au **Runtime** : par exemple un dépassement de la mémoire allouée par la JVM
- les erreurs **anticipables** : par exemple, l'accès aux ressources externes ne fonctionnent plus

----

## La classe Throwable

-Toutes les classes qui étendent **Throwable** vont modéliser des erreurs et seront gérées de façon particulière par la JVM.

3 propriétés fondamentales :
- **message** : message d'erreur au format chaîne de caractères, récupérable via la méthode `getMessage()`
- **stack trace** : pile d'appel qui contient l'enchainement de la liste des méthodes qui ont généré l'erreur. La méthode `printStackTrace()` affiche la pile d'appel dans la console ou ailleurs on lui passant des paramètres
- **rootCause** : permet de savoir si une erreur est généré à cause d'une erreur en amont

----

## Extensions de Throwable

La classe **Error** :
- modélise en général les erreurs de Runtime, par exemple **OutOfMemoryError** (quand la JVM n'a plus de mémoire disponible) et **StackOverflowError** (méthode qui s'appelle elle-même à l'infini)
- pas de gestion explicite de ces erreurs

La classe **Exception** :
- par exemple les classes **IOException**, **FileNotFoundException**, **SQLException** sont des extensions directes d'Exceptions
- besoin de gérer explicitement ces erreurs (appelées **Checked** Exception)

La classe **RuntimeException** :
- extension directe d'**Exception** et non de Throwable, par exemple **NullPointerException** ou **ArrayIndexOutOfBoundsException** ou **ArithmeticException** (division par 0 par exemple)
- pas de gestion explicite de ces erreurs (appelées **Unchecked** Exception), correction du bug quand il arrive

----

## Gestion des Checked Exception

Le JDK oblige à gérer certains cas d'erreurs lors de certaines actions comme par exemple lors de la lecture d'un fichier, gérer le cas où le fichier n'est pas lisible.

2 approches possibles :
- attraper l'exception et la traiter localement
- transmettre l'exception au code appelant

----

## Transmettre une exception avec throws

- ajout du mot clé **throws** suivi du nom de l'exception à la fin de la signature de la méthode

```java
public String save(Person p) throws SQLException {
    // code pouvant générer une SQLException
    return "person save";
}
```

## Traiter l'exception localement

- pattern **try/catch**, la partie *catch* n'est exécutée qui si une erreur survient dans la partie *try*

```java
public String save(Person p) {
    try {
        // code pouvant générer une SQLException	
        return "person save";
    } catch (SQLException e) {
        // affichage de l'erreur
    }
}
```

----

## Attraper plusieurs exceptions

- possibilité de catcher la classe **Exception** voire **Throwable** qui va capturer toutes les exceptions mais ce n'est pas une bonne idée car on ne veut pas forcément capturer toutes les exceptions
- possibilité d'avoir plusieurs blocs *catch* à la suite, ou d'utiliser le **multicatch** pour capturer différentes exceptions dans le même bloc *catch* :

```java
// enchaînement de catch
try { // code pouvant générer une SQLException
    return "person save";
} catch (SQLException e) {
    /* traitement de l'erreur */
} catch (IOException e) {
    /* traitement de l'erreur */
}

// multicatch
try { // code pouvant générer une SQLException
    return "person save";
} catch (SQLException | IOException e) {
    /* traitement de l'erreur */
}
```

----

## Créer et jeter une exception applicative à partir d'une exception du JDK

- création d'une exception applicative que l'on va transmettre lorsqu'on attrape une exception du JDK avec le mot clé **throw**
- il faut transmettre l'exception dans le constructeur de notre exception applicative pour récupérer la *rootCause* et savoir l'origine de l'erreur

```java
try{
    String chaine = "t";
    int n = Integer.parseInt(chaine);
    System.out.println(n);
} catch (NumberFormatException e){
    throw new MyException(e);
}
```

----

## Le bloc finally

- possibilité d'ajouter une clause **finally** qui garantie que le code soit exécuté qu'une exception soit levée ou pas
- ne jamais écrire de *return* dans un catch ou un finally

```java
try {
} catch (Exception e) {
} finally {
}
```