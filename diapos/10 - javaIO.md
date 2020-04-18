# Java I/O

----

## Introduction

- Java I/O signifie **Input / Output**
- s'intéresse à l'intégralité des mécanismes qui permettent d'échanger des données avec l'extérieur
    - le disque
    - le réseau : le web (HTTP) et les bases de données
    - l'accès à la mémoire "off heep" (non traité)

Historique :
- 1995, en Java 1 : Java I/O
- 2002, en Java 4 : Java NIO (nouvelles classes, nouvelles fonctionnalités)
- 2011, en Java 7 : Java NIO2 (nouvelles classes, nouveaux concepts)

----

## Modélisation de chemins sur un disque avec File et Path

- la classe **File** et l'interface **Path** l'interface permettent de modéliser des chemins vers le système de fichiers. L'intérêt d'avoir une interface est qu'on peut avoir une implémentation propre à chaque OS

- La création d'un *File* ou d'un *Path* crée un objet en mémoire qui modélise un chemin sur le système de fichier. Cela ne crée en aucun cas un fichier sur le système de fichiers. Aucun accès disque n'est réalisé

```java
// création à partir de chemin absolu ou relatif
File file = new File("chemin");
// c:/toto/... ou c:\\toto\\...
Path path = Path.of("chemin");
// il est aussi possible de créer un File ou un Path à partir d'URI
```

----

## Analyse du chemin modélisé par l'objet File

méthodes fonctionnant sans accès disque :
- **getPath()** donne le nom complet du chemin avec le nom du fichier
- **getName()** donne le nom du fichier
- **getParent()** donne le nom du chemin sans le nom du chemin

méthode nécessitant un accès disque :
- **getCanonicalPath()** crée le chemin absolu à partir du chemin relatif renseigné et du répertoire courant, généralement où s'exécute le projet en faisant un accès au FS (FileSystem) pour connaître le répertoire courant. Si l'accès au FS n'est pas disponible, une IOException sera jetée

```java
File file = new File("/a/b/c.txt");
System.out.println(file.getPath()); // /a/b/c.txt
System.out.println(file.getName()); // c.txt
System.out.println(file.getParent()); // /a/b
System.out.println(file.getCanonicalPath()); // /a/b/c.txt (ne change rien car il s'agit déjà d'un chemin absolu)

File file2 = new File("a/b/c.txt");
System.out.println(file2.getCanonicalPath()); // /home/gaetan/depot-github/tutoriel-java/a/b/c.txt
```

----

## Tester et manipuler un fichier avec une instance de File

toutes les méthodes suivantes font des requêtes sur le FS et sont donc suceptibles de jeter une exception. Elles retournent toutes des booléens :
- **isFile()** et **isDirectory()** testent si le chemin passé en paramètre correspond à un fichier ou à un répertoire sur le FS
- **canRead()**, **canWrite()** et **canExecute()** testent s'il est possible de lire, écrire et exécuter le fichier
- **exists()** permet de tester si ce fichier/répertoire existe
- **createNewFile()** permet de créer un fichier correspondant au chemin, **mkdir()** et **mkdirs()** permettent de créer le chemin en tant que répertoire, *mkdir* ne créant que le dernier dossier du chemin, *mkdirs* va créer tous les dossiers du chemin
- **delete()** et **renameTo(File file)** permettent de supprimer un fichier et de le renommer

```java
File f = new File("toto.txt");
// création du fichier toto.txt dans le répertoire courant du projet
System.out.println(f.createNewFile()); // true
System.out.println(f.createNewFile()); // false car le fichier existe déjà
System.out.println(f.isFile()); // true
```

----

## Organisation de Java I/O

Java I/O est séparé en 4 catégories :
- les flux de texte en lecture : **Reader**
- les flux de texte en écriture : **Writer**
- les flux binaire en lecture : **InputStream**
- les flux binaire en écriture : **OutpoutStream**

Ces 4 classes sont abstraites. Elles définissent la façon de lire/écrire mais pas le médium de sortie : un **Fichier**, un **Socket** ou un **Buffer** (mémoire)

----

## Lecture de caractères avec un Reader

La classe **Reader** est abstraite. Elle définie les opérations de base de lecture de texte :
- **read()** permet de lire un caractère à partir d'un médium de sortie
- **read(char[])** permet de lire un tableau de caractères
- **skip()** permet de sauter un certain nombre de caractères
- **close()** permet de fermer les ressources que l'on a ouverte

```java
// Exemple de lecture d'un fichier avec un FileReader
Reader reader = null;
try {
    reader = new FileReader(new File("test"));
    char[] chars = new char[1024];
    StringBuilder sb = new StringBuilder();
    int n = reader.read(chars); // renvoie le nombre de caractères lu
    // quand n vaut -1, la lecture du fichier est terminée
    while (n > -1) {
        sb.append(chars, 0, n);
        n = reader.read(chars);
    }
    System.out.println(sb.toString()); // impresssion du contenu
} catch (IOException e) {
    e.printStackTrace();
} finally { // fermeture du reader dans le finally au ca où une exception est jetée
    try { if(reader != null){reader.close();} } catch (IOException e) { e.printStackTrace(); }
}
```

----

## Utilisation d'un Reader avec try-with-resources

- arrivée avec Java 7
- les ressources ouvertes dans le *try* vont automatiquement être fermées
- possibilité de mettre dans le try-with-resources toutes les ressources qui implémentent ***AutoCloseable**

```java
File file = new File("test");
try(Reader reader = new FileReader(file)){
    char[] chars = new char[1024];
    StringBuilder sb = new StringBuilder();
    int n = reader.read(chars); // renvoie le nombre de caractères lu
    // quand n vaut -1, la lecture du fichier est terminée
    while (n > -1) {
        sb.append(chars, 0, n);
        n = reader.read(chars);
    }
    System.out.println(sb.toString()); // impresssion du contenu
} catch (IOException e) {
    e.printStackTrace();
}
```

----

## Utilisation d'un BufferedReader

**BufferedReader** est une extension de Reader qui a 2 méthodes supplémentaires intéressantes :
- **readLine()** renvoie un String qui contient une ligne du fichier texte
- **lines()** renvoie un `Stream<String>`

**LineNumberReader** étend lui-même BufferedReader est à une méthode supplémentaire intéressante :
- **getLineNumber()** permet de retourner le numéro de ligne du fichier
- **setLineNumber(int i)** permet de changer ce numéro de ligne

```java
File file = new File("test");
// fermeture automatique du BufferedReader puis du Reader
try(Reader reader = new FileReader(file); BufferedReader br = new BufferedReader(reader);){
    br.lines().forEach(System.out::println);
} catch (IOException e){
    e.printStackTrace();
}
```