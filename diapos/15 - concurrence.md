# La programmation concurrente

----

## Introduction

- La programmation concurrente consiste à programmer des applications qui font plusieurs choses en même temps alors que le CPU ne fait qu'une chose à la fois (ce n'est plus vrai maintenant), mais le fait qu'il enchaîne les tâches très rapidement donne l'impression qu'il fait plusieurs choses en même temps. Le temps du CPU est partagé entre les différentes tâches
    - **thread** (fil d'exécution) : ressource gérée par l'OS qui peut prendre en charge des tâches
    - **tâche** : sauvegarder un document, envoyer un mail...
- L'OS gère, grâce au **Thread Scheduler**, la répartition de la ressource *temps CPU* entre les tâches à exécuter. C'est un *chef d'orchestre*. Il existe différents critères qui font qu'on va passer à une autre tâche :
    - **équilibrage entre les tâches** : si une tâche a déjà eu beaucoup de temps CPU, le Thread Scheduler peut donner du temps à une autre tâche
    - si la **tâche est en attente**, car elle attend des données (du disque, du réseau, de la mémoire). Par exemple pour sauvegarder un document, la tâche demande l'écriture des données sur le disque et en attendant que le disque réponde, le CPU va mettre la tâche en attente
    - la **synchronisation**

----

## Interface Runnable et classe Thread

Une application Java peut être vu comme une tâche. La méthode **main()** est exécutée dans un thread particulier par la JVM appelé **thread main**
- la notion de tâche en Java est modélisé par **l'interface Runnable** avec une unique méthode abstraite **void run()**. Il s'agit d'une interface fonctionnelle.
- la notion de thread est modélisé par la **classe Thread**. Cette classe permet de créer des nouveaux threads, de confier des tâches de type Runnable à ces threads et d'exécuter ces tâches dans ces threads

```java
// création d'une tâche
Runnable task = () -> System.out.println("Hello World !");
// exécution de cette tâche
// il n'y a pas eu de Thread de créé, la tâche est donc exécutée par le thread courant, ici le thread main
task.run();

// création d'un Thread en lui passant la tâche qu'il doit prendre en charge
Thread t = new Thread(task);
// démarrage du Thread avec la méthode start()
// elle va exécuter la méthode run() de notre tâche dans un thread différent
t.start();
```

----

## Notion de thread daemon

- une fois qu'un thread a fini de s'exécuter, il va s'arrêter. Dans l'exemple précédent :
    - le thread *main* s'arrête une fois qu'il a lancé la méthode start() du thread t
    - le thread *t* s'arrête une fois que la méthode ru de l'objet task
- lorsque le thread *main* s'éteind, la JVM regarde si elle doit s'arrêter, ce qu'elle fera s'il n'y a plus que des threads de type **daemon** en fonctionnement
    - lors du démarrage de la JVM, il y a plein de threads qui démarrent, notamment un qui s'occupe du garbage collector. Ce thread ne bloque pas la fermeture de la JVM car c'est un thread particulier de type daemon
    - *daemon* est un attribut de la classe Thread de type booléen. Si ce booléen vaut true, la présence de ce thread ne va pas bloquer l'extinction de la JVM
    - le thread main et le thread t ne sont pas de type daemon, tant qu'ils sont vivants, la JVM ne va pas s'arrêter

----

## Affichage du thread courant et si le thread est daemon

```java
Runnable task = () -> System.out.println(
    // Thread.currentThread() renvoie une référence vers le thread qui est en train d'exécuter la tâche
    "Hello World ! : " + Thread.currentThread().getName() + " : " + Thread.currentThread().isDaemon());
task.run();

// affichage du nom du thread courant et s'il est de type Daemon
System.out.println(Thread.currentThread().getName() + " : " + Thread.currentThread().isDaemon());

Thread t = new Thread(task);
t.start();

// CONSOLE
Hello World ! : main : false
main : false
Hello World ! : Thread-0 : false
```

- la tâche *task* commence par être exécutée via sa méthode *run()* dans le thread main (qui n'est pas de type daemon)
- ensuite, affichage dans la console du thread courant et s'il est de de type daemon. Il s'agit ici du thread main
- enfin, création d'un thread qui va prendre en charge la tâche task et démarrage du thread via sa méthode *start()* qui va exécuter la tâche *task* dans son thread

----

## Pattern Singleton

- pattern dans lequel on ne veut qu'une **unique instance** d'une classe à l'échelle de toute l'application. Pour cela, il faut :
    - interdire de créer des instances de la classe en mettant le constructeur privé
    - créer une méthode statique qui va se charger de l'instanciation si elle n'a pas déjà eu lieu et renvoyer l'instance
- cette façon de faire est buguée :
    - elle ne garantie pas qu'il n'y aura qu'une instance de la classe à cause de la **race condition**, ou concurrence d'accès sur les données : 2 threads peuvent lire et écrire une même variable en même temps
    - ici le *Thread Scheduler* peut interrompre un thread après le if de *getInstance()* mais avant l'instanciation de Service, et donner la main à un autre thread qui va faire l'instanciation. Quand le premier thread reprend la main, il va faire une instanciation alors qu'une instance de Service existe déjà et écraser le Service existant

```java
public class Service {
    private static Service service;
    private Service() {}
    public static Service getInstance() {
        if (service == null) { service = new Service(); }
        return service;
    }
}
```

----

## Synchroniser un bloc de code

sur certains blocs de code, possibilité d'empêher le *Thread Scheduler* d'interrompre un thread, plus précisément d'empêcher un autre thread d'entrer dans ce bloc de code en utilisaant le mot clé **synchronized**
- il prend un objet **lock** en paramètre, qui va servir de verrou sur le bloc de code synchronisé. Cet objet lock (comme tous les objets en Java) possède une clé
- si un thread t1 exécute le code synchronisé, il prend la clé du lock, qui n'a donc plus sa clé. S'il est interrompu par le *Thread Scheduler*, et qu'un thread t2 essaie d'exécuter le code synchronisé, il va être bloqué à l'entrée du bloc de code dans une file d'attente appelé **WAIT_LIST** (ou sont mis en attente les threads) en attendant que lock récupère sa clé. Le *Thread Scheduler* va mettre t2 en attente, et redonner la main à d'autres threads. Quand t1 finit l'exécution du bloc synchronisé, il rend la clé à lock, et quand le *Thread Scheduler* redonne la main à t2, il peut récupérer la clé auprès de lock et exécuter le code du bloc synchronisé

```java
Object lock = new Object();
synchronized(lock){
    ...
}
```

----

## Les différentes méthodes de synchronisation

il existe plusieurs façon de synchroniser un bloc de code
- bloc synchronisé explicite :
    - voir exemple précédent
    - meilleure façon de faire car possibilité de cacher l'objet **lock** car s'il est exposé, risque de créer des ***deadlock**
- bloc synchronisé implicite :
    - sur une méthode d'instance : l'intégralité du corps de la méthode va être synchronisé. L'objet lock va être l'instance de la classe dans laquelle on se trouve : **this**
    - sur une méthode statique : l'intégralité du corps de la méthode statique va aussi être synchronisé. l'objet qui porte la clé est ici l'objet Class qui modélise la classe dans laquelle on est

```java
public synchronized void maMethode(){}
public synchronized static void maMethodeStatique(){}
```

----

## Synchronisation réentrante

Si 2 méthodes *m1* et *m2* sont synchronisées avec le même lock et que *m1* appelle *m2*, lorsqu'un thread rentre dans la méthode *m1*, il récupère la clé du lock, et lorsqu'il s'apprête à rentrer dans *m2*, il ne peut pas récupérer la clé car il la possède déjà. Dans ce cas, le thread va pouvoir exécuter la méthode *m2* car il possède déjà la clé. On parle de **réentrant**.

----

## Synchronisation avec une même clé

Si 2 méthodes sont synchronisées avec le même lock (par exemple de manière implicite en écrivant *synchronized* dans la signature de la méthode). alors si un thread *t1* exécute la méthode *getName()*, un thread *t2* ne pourra ni exécuter *getName()*, ni *getAge()* car les 2 méthodes sont synchronisées sur le même objet this.

```java
public class User {
    synchronized String getName(){}
    synchronized int getAge(){}
}
```

----

## Synchronisation clé d'instance vs clé statique

- avec une synchronisation explicite sur un objet lock qui est un champ de la classe : cette situation revient à la même situation que l'exemple précédent : un thread *t1* qui exécute une des 2 méthodes synchronisées empêche l'exécution par un thread *t2* l'exécution des 2 méthodes sur la même instance de la classe User

```java
public class User {
    Object lock = new Object();
    String getName(){ synchronized(lock){} }
    int getAge(){ synchronized(lock){} }
}
```

- cependant, si les 2 threads travaillent sur 2 instances différentes de User, *u1* et *u2*, alors un thread *t1* peut exécuter une méthode sur u1 pendant qu'un thread *t2* exécute la même méthode sur u2.
- si on veut rendre une méthode protégée quelque soit l'instance, il faut que l'objet lock soit partagé par toutes les instances, en rendant le lock **static**

```java
public class User {
    static Object lock = new Object();
    String getName(){ synchronized(lock){} }
    int getAge(){ synchronized(lock){} }
}
```

----

## Les deadlock

- exemple : un thread *t1* exécute *m1* (et récupère la clé de lock1) et un thread *t2* exécute *m2* (et récupère la clé de lock2). *t1* ne peut pas exécuter *m2* car la clé n'est pas disponible et *t2* ne peut pas exécuter *m3* car la clé n'est pas disponible. Les 2 threads possèdent chacun un lock, et sont en attente du fait que l'autre thread libère le lock qu'il possède. Cette situation n'a pas de solution, elle est bloquée. On parle de **deadlock**
- c'est une situation de blocage, la seule façon de se libérer d'un deadlock est de redémarrer l'application
- les IDE et la JVM peuvent aider à débuguer ces situations
- pour éviter que cela arrive, il faut privilégier les blocs synchronisés sur les méthodes privées. Le fait que ces blocs de code ne soient pas exposés permet de mieux contrôler et analyser qui va utiliser ces morceaux de code (uniquement les méthodes de la classe)

```java
public class User {
    Object lock1, lock2;
    void m1(){ synchronized(lock1){ m2() } }
    void m2(){ synchronized(lock2){ m3() } }
    void m3(){ synchronized(lock1){...} }
}
```

----

## Présentation du pattern Producteur / Consommateur

- création d'une classe qui permet d'ajouter un élément dans un tableau et supprimer un élément dans un tableau
- si 2 threads **producer** et **consumer** essaient d'exécuter simultanément respectivement *add()* et *remove()*, la variable *index* est soumise à une concurrence d'accès, de même que le tableau buffer qui va être lu de 2 threads différents. Il va falloir utilisé la synchronisation

```java
public class Buffer {
    int[] buffer = ...
    int index = 0

    public void add(){ buffer[index] = index; index++; }
    public int remove(){ int i = buffer[index]; index--; return i; }
}
```

----

## Essai de synchronisation du pattern Producteur / Consommateur

- protection de la méthodde *add()* en vérifiant qu'il reste de la place dans le tableau, et de la méthode *remove()* en vérifant que le tableau n'est pas vide, en ajoutant une boucle d'attente
- synchronisation des méthodes *add* et *remove* avec le même objet pour éviter qu'un un thread ajoute un élément au même moment qu'un autre thread retire un élément
- ce système ne fonctionne pas car si un thread rentre dans la méthode *produce* pour ajouter un élément et que le tableau est plein, il va rentrer dans la boucle d'attente en attendant que le tableau ait de la place disponible. Sauf qu'un autre thread ne pourra pas exécuter la méthode *consume* car la clé est déjà possédé par le premier thread qui est dans la méthode *produce*. Si un thread rentre dans une boucle d'attente, il y restera pour toujours

```java
public void produce(){ synchronized(lock){
    while(isFull(buffer)){}
    buffer[index] = index; index++;
}}
public int consume(){ synchronized(lock){
    while(isEmpty(buffer)){}
    int i = buffer[index]; index--; return i;
}}
```

----

## Utilisation du Wait / Notify

- une solution au problème précédant serait de pouvoir mettre le thread qui est dans la boucle d'attente, en attente, en rendant la clé, pour qu'un autre thread puisse venir consommer un élément du tableau. Ce thread de consommation une fois qu'il aura libéré de la place, viendra notifier le thread de production en attente. Cela s'appelle le mécanisme **Wait / Notify**
- utilisation de la méthode **wait()** qui ne doit être appelé que sur un objet dont le thread possède le moniteur (la clé), donc à l'intérieur d'un bloc synchronisé. Le thread courant est mis en sommeil, il va cesser de s'exécuter, dans une file d'attente de type WAIT particulière, et rend le moniteur de lock
- pour sortir un thread de cet état d'attente, il faut utiliser **notify()** sur l'objet lock. Il va prendre un thread qui est dans la file d'attente et le réveiller et lui donner le moniteur pour continuer son exécution là ou il s'était arrêté. Possibilité d'utiliser **notifyAll()** qui réveille tous les threads

```java
public void produce(){ synchronized(lock){
    while(isFull(buffer)){ lock.wait(); }
    buffer[index] = index; index++; lock.notify();
}}
public int consume(){ synchronized(lock){
    while(isEmpty(buffer)){ lock.wait(); }
    int i = buffer[index]; index--; return i; lock.notify();
}}
```

----

## Fonctionnement du Pattern Singleton synchronisé sur un CPU multicoeur

dans l'exemple sur le pattern Singleton, 2 threads *t1* et *t2* veulent accéder en même temps à la méthode synchronisée *getInstance()* :
- avec un CPU mono-coeur, t1 va avoir la main, rentrer dans la méthode. Le Thread Scheduler peut reprendre la main pour donner du temps à t2, qui ne pourra pas rentrer dans le bloc synchronisé tant que t1 est dedans. Immédiatement, le Thread Scheduler va donner la main à un autre thread. Quand t1 aura rendu la clé de la méthode, et que t2 reprendra la main, t2 pourra rentrer dans la méthode
- avec un CPU multi-coeur, 2 choses peuvent se dérouler en même temps. Si t1 rentre dans le bloc synchronisé sur le coeur 1, et t2 au même moment est exécuté sur le coeur 2. Le Thread Scheduler ne va pas rendre la main de suite pour t2, mais attendre un peu en se disant que la méthode synchronisée que veut exécuter t2 va peut-être se libérer en étant exécutée sur l'autre coeur, ce qui n'est pas possible sur une architecture avec un seul coeur. Le coeur 2 va donc exécuter le thread t2 ou passer à un autre thread s'il a attendu trop longtemps. Une fois que la méthode synchronisée aura été libérée, le thread t2 pourra être éxécutée sur le coeur 1 ou le coeur 2

----

## Impact de la lecture synchronisée du Singleton sur un CPU multi-coeur (1)

- sur un CPU mono-coeur, 2 threads qui veulent accéder à la méthode synchronisée *getInstance()* vont s'exécuter l'un à la suite de l'autre. Le second pourra s'exécuter quand le premier sera terminé et aura rendu la clé. Sur un CPU multi-coeur, si les 2 threads s'exécutent en même temps sur 2 coeurs dinstincts, le deuxième va devoir attendre que le premier ait terminé son exécution et rendu la clé. Il n'y a donc pas de gains de performance à avoir une architecture multi-coeur sur cette méthode ce qui est dommage pour une méthode en lecture (car l'instanciation n'a lieu qu'une seule fois, les fois suivantes, uniquement de la lecture)
- tentative de résolution de ce problème par le pattern **Double Check Locking** : plutôt que d'avoir un bloc synchronisé qui gère la lecture et l'écriture, on commence par écrire un bloc non synchronisé qui teste si l'instance est non null et renvoie l'instance si c'est le cas. Si elle est nulle, création d'un bloc synchronisé pour créer l'instance en commençant par tester si l'instance est non nulle car le Thread Scheduler a pu interrompre le thread dans la méthode avant d'entrer dans le bloc synchronisé, et renvoie de l'instance créé. Il n'y aura besoin de rentrer dans le bloc synchronisé qu'une seule fois pour créer le service, et après les threads pourront exécuter la méthode en même temps pour faire de la lecture

----

## Impact de la lecture synchronisée du Singleton sur un CPU multi-coeur (2)

- ce pattern est **buggé**, voir ci-après le fonctionnement des architectures multi-coeur

```java
public static Service getInstance() {
    if (service != null) { return service; }
    synchronized(lock) {
        if (service == null) { service = new Service(); }
        return service;
    }
}
```

----
