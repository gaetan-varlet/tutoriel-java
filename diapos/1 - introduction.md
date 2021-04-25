# Introduction

----

## JVM / JRE / JDK

- JRE (*Java Runtime Environnement*): exécution des programmes Java, précompilé en byte code, contient la JVM (machine virtuelle Java)
- JDK (*Java Development Kit*) : JRE + compilateur *javac* nécessaire pour compiler le code Java en byte code
- Java SE (Standard Edition) : coeur du langage, contenant les principales API
- Java EE (Enterprise Edition) remplacé par Jakarta EE : construit au dessus de Java SE, permet de s'adresser au BDD, de faire du web...

----

## Les versions de Java SE

- 2011 : Java 7 (numéro de version 1.7), première version de Java publiée par Oracle
- 2014 : Java 8 (numéro de version 1.8) ; introduction des **lambdas**
- 2017 : Java 9 (numéro de version 9) ; introduction des **modules**
- à partir de 2018, 2 versions par an, en mars (Java 10) et en septembre (Java 11)
- tous les 3 ans, il y a une version **LTS** (Long Term Support) : Java 11, puis Java 17

Java assure la compabilité ascendante. Cela assure plusieurs choses :
- un code source Java 7 peut être compilé sans modifications et sans erreurs avec un compilateur Java 8 ou version suivante
- un code source Java 7, compilé avec un compilateur Java 7, peut être exécuté dans une JRE Java 8 ou version suivante


----

## Compilation et exécution en ligne de commande

- le code source d'une application Java est écrite dans une collection de fichiers texte au format `.java` encodé en *UTF-8*
- le compilateur Java, **javac**, crée le code compilé en **byte code** au format `.class`
- le code n'est pas compilé pour un OS/CPU particulier comme en C, c'est toujours le même *byte code* qui est produit, ce qui le rend utilisable avec tous les OS
- exécution du *byte code* par la JVM, qui est un code intermédiaire entre celui du programme et celui que que la machine peut comprendre
- la machine ne peut pas comprendre le byte code, elle a besoin de la JVM pour le traduire en code machine
- la JVM est revanche différente selon les OS/CPU, alors que le compilateur est toujours le même
- avant Java 11 :
    - `javac <nomDeFichier.java>` compile le code source et crée le fichier **nomDeFichier.class**
    - `java <nomFichierClassSansExtension>` exécute le byte code et affiche le résultat du programme
- à partir de Java 11 :
    - compilation à la volée dans la mémoire et exécution dans une seule commande
    - `java <nomDeFichier.java>`
- il est possible de créer un jar avec la commande `jar...` puis de l'exécuter avec la commande `java -jar monApp.jar`

----

## Le classpath

- à l'exécution, la JVM recherche les classes dans :
    - les classes de la plate-forme Java (stockées dans le fichier rt.jar)
    - les classes d'extension de la plate-forme Java
    - le classpath : c'est un ensemble de chemins (absolus ou relatifs) vers des répertoires ou des fichiers `.jar`
        - le répertoire qui contient les packages (pour les ficiers `.class`)
        - le chemin vers les fichiers `.zip` ou `.jar`
- chaque élément est séparé par un caractère (`;` sous Windows, `:` sous Unix). Exemple de classpath avec 3 éléments : `.;C:\java\tests\bin;C:\java\lib\log4j-1.2.11.jar`
    - par défaut, si le classpath n'est pas défini, il est composé uniquement du répertoire courant `.`
    - on peut le rédéfinir avec l'option `-classpath` ou `-cp` ou la variable d'environnement `CLASSPATH`. Exemple : `java -cp . MaClasse.java`