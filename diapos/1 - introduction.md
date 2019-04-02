# Introduction

----

## JVM / JRE / JDK

- précompilation du code source (`.java`) en **byte code** (`), .class`) par le JDK
- le byte code est un code intermédiaire entre celui du programme et celui que que la machine peut comprendre. La machine ne peut pas comprendre le byte code, elle a besoin de la JVM (machine virtuelle Java) pour le traduire en code machine
- programmes utilisables avec tous les OS
- JRE (Java Runtime Environnement): exécution des programmes Java, précompilé en byte code, contient la JVM
- JDK : JRE + nécessaire pour compiler le code Java en byte code

----

# Compilation en ligne de commande

- `javac <nomDeFichier.java>` compile le code source et crée le fichier **nomDeFichier.class**
- `java <nomFichierClassSansExtension>` exécute le byte code et affiche le résultat du programme