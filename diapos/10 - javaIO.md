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

## Modélisation de chemins sur un disque avec File et Path

- la classe **File** et l'interface **Path** l'interface permettent de modéliser des chemins vers le système de fichiers. L'intérêt d'avoir une interface est qu'on peut avoir une implémentation propre à chaque OS
- la création d'un *File* ou d'un *Path* crée un objet en mémoire qui modélise un chemin sur le système de fichier. Cela ne crée en aucun cas un fichier sur le système de fichiers. Aucun accès disque n'est réalisé

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
- les flux binaires en lecture : **InputStream**
- les flux binaires en écriture : **OutpoutStream**

Ces 4 classes sont abstraites. Elles définissent la façon de lire/écrire mais pas le médium de sortie : un **Fichier**, un **Socket** ou un **Buffer** (mémoire)

----

## Lecture de caractères avec un Reader

La classe abstraite **Reader** définie les opérations de base de lecture de texte :
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
- possibilité de mettre dans le try-with-resources toutes les ressources qui implémentent **AutoCloseable**

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

----

## Ecrire dans un fichier avec un Writer

La classe abstraite **Writer** définie les opérations de base d'écriture d'un caractère, de tableaux de caractères et de chaînes de caractères :
- **write(int i)** écrit un caractère
- **write(char[]c)** écrit un tableau de caractères dans le flux de sortie
- **write(String s)** écrit une chaîne de caractères
- **append(...)** permet d'ajouter du texte à un endroit précis
- **close()** ferme le write
- **flush()** garanti que ce qui a été écrit dans le writer est transmis vers le flux de sortie (l'appel à close appel flush)

```java
// écriture dans un fichier FileWriter
File file = new File("test");
try(Writer writer = new FileWriter(file); BufferedWriter bw = new BufferedWriter(writer);){
    bw.write("Hello"); bw.newLine(); bw.write("World");
} catch (IOException e){ e.printStackTrace(); }

// la classe PrintWriter a des méthodes supplémentaires (utilisée dans l'API Servlet)
PrintWriter pw = new PrintWriter(file);
pw.println("Coucou");
pw.close();
```

----

## Utilisation de Files pour créer des flux bufferisés

- **Files.newBufferedReader(path)** permet de créer un BufferedReader, possibilité de spécifier l'encodage en UTF-8 par défaut
- **Files.newBufferedWriter(path)** permet de créer un BufferedWriter

```java
Path path = Path.of("chemin");
BufferedReader br = Files.newBufferedReader(path); // par défaut en UTF8
BufferedReader br2 = Files.newBufferedReader(path, StandardCharsets.ISO_8859_1);
BufferedWriter bw = Files.newBufferedWriter(path);
```

----

## Résumé des Reader et Writer

| Reader          | Writer          | classes abstraites permettant de lire/écrire les flux textuels
| :---:           | :---:           | :---
| FileReader      | FileWriter      | lire/écrire dans un fichier texte
| StringReader    | StringWriter    | lire/écrire dans une chaîne de caractères
| CharArrayReader | CharArrayWriter | lire/écrire dans un tableau de caractères
| BufferedReader  | BufferedWriter  | **Pattern Decorator** : extension qui nécessite un Reader déjà construit
| -               | PrintWriter     | extension de Writer permettant de faire de la mise en forme

----

## OutputStream

- équivalent de Writer pour les flux binaires, elle modélise l'écriture d'octets sur des flux
- classe abstraire qui va étendre par un certain nombre de classe pour définir le médium de sortie :
**FileOutputStream**, **ByteArrayOutputStream**

Les principales méthodes :
- **write(byte[])** ou **write(int)** pour écrire sur le flux de sortie
- **flush()** comme le flush du Writer
- **close()** avec appel de la méthode flush

```java
// exemple d'écriture avec un FileOutputStream
File file = new File("test");
try (OutputStream os = new FileOutputStream(file);
        // création d'un BufferedOutputStream par décoration de l'OutputStream pour améliorer les performances des écritures
        BufferedOutputStream bos = new BufferedOutputStream(os);) {
    byte[] bytes = null;
    bos.write(bytes);
} catch (IOException e) { e.printStackTrace();}
```

----

## InputStream

InputStream est abstrait : **FileInputStream**, **ByteArrayInputStream**  
Les principales méthodes :
- **read()** renvoie un *int*, **read(byte[])** renvoie un *int* avec le nombre d'octets lus
- **readAll()** renvoie le tableau d'octets complet, risque de saturation de la mémoire de la JVM
- **mark()** permet de mettre un index dans l'inputStream, **reset()** permet de revenir à l'index posé
- **close()** pour fermer le flux

```java
File file = new File("test");
try (InputStream is = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(is);
        // stockage des octets lus dans le ByteArrayOutputStream
        ByteArrayOutputStream bos = new ByteArrayOutputStream();) {
    byte[] bytes = new byte[1024];
    int count = bis.read(bytes);
    while(count != 1){
        bos.write(bytes, 0, count);
        count = bis.read(bytes);
    }
    byte[] readBytes = bos.toByteArray(); // récupération du tableau d'octets lu
} catch (IOException e) { e.printStackTrace();}
```

----

## Les InputStream et OutputStream spécifiques

construction par décoration d'un *InputStream* ou d'un *OutputStream* et ajoutant des méthodes :
- possibilité de lire/écrire des types primitifs avec des **DataInputStream** et des **DataOutputStream**
- possibilité de lire/écrire des objets *Serializable* avec des **ObjectInputStream** et des **ObjectOutputStream**

----

## Les flux mixtes texte/binaire

**InputStreamReader** **OutputStreamWriter** sont des flux mixtes :
- *InputStreamReader* étend *Reader* et est construit par décoration d'un *InputStream*
    - lire des caractères sur un flux d'octets au lieu d'un flux de caractères
- *OutputStreamWriter* étend *Writer* et est construit par décoration d'un *OutputStream*
    - écrire des caractères sur un flux d'octets

flux caractères = flux d'octets interprété avec un **Charset**, par exemple UTF-8 ou ISO-8859, qui permet de convertir des octets en caractère.

----

## Résumé des flux binaires

| InputStream (Lecture) | OutputStream (Ecriture) | classes abstraites
| :---:                 | :---:                   | :---
| FileI.S.              | FileO.S.                | lire/écrire dans un fichier
| ByeArrayI.S.          | ByeArrayO.S.            | lire/écrire dans un tableau
| DataI.S.              | DataO.S.                | lire/écrire des types primitifs
| ObjectI.S.            | ObjectO.S.              | (dé)sérialisation des objets (déconseillé)
| GZipI.S. et ZipI.S.   | GZipO.S. et ZipO.S.     | gestion automatique des flux compressés
| InputStreamReader     | OutputStreamWriter      | lire/écrire des caractères sur un flux binaire