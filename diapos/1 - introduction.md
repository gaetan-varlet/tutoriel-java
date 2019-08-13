# Introduction

----

## JVM / JRE / JDK

- précompilation du code source (`.java`) en **byte code** (`.class`) par le JDK
- le byte code est un code intermédiaire entre celui du programme et celui que que la machine peut comprendre. La machine ne peut pas comprendre le byte code, elle a besoin de la JVM (machine virtuelle Java) pour le traduire en code machine
- programmes utilisables avec tous les OS
- JRE (Java Runtime Environnement): exécution des programmes Java, précompilé en byte code, contient la JVM
- JDK : JRE + nécessaire pour compiler le code Java en byte code

----

## Compilation et exécution en ligne de commande

avant Java 11 :
- `javac <nomDeFichier.java>` compile le code source et crée le fichier **nomDeFichier.class**
- `java <nomFichierClassSansExtension>` exécute le byte code et affiche le résultat du programme

à partir de Java 11 :
- compilation à la volée dans la mémoire et exécution dans une seule commande
- `java <nomDeFichier.java>`

Il est possible de créer un jar avec la commande `jar ...` puis de l'exécuter avec la commande `java -jar monApp.jar`

----

## Le classpath

A l'exécution, la JVM recherche les classes dans :
- les classes de la plate-forme Java (stockées dans le fichier rt.jar)
- les classes d'extension de la plate-forme Java
- le classpath : c'est un ensemble de chemins (absolus ou relatifs) vers des répertoires ou des fichiers `.jar`
    - le répertoire qui contient les packages (pour les ficiers `.class`)
    - le chemin vers kes fichiers `.zip` ou `.jar`

Chaque élément est séparé par un caractère (`;` sous Windows, `:` sous Unix). Exemple de classpath avec 3 éléments : `.;C:\java\tests\bin;C:\java\lib\log4j-1.2.11.jar`
- par défaut, si le classpath n'est pas défini, il est composé uniquement du répertoire courant `.`
- on peut le rédéfinir avec l'option `-classpath` ou `-cp` ou la variable d'environnement `CLASSPATH`. Exemple : `java -cp . MaClasse.java`