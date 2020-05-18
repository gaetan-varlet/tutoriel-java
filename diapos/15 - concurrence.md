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

## Essai de synch du pattern Producteur/Consommateur

- protection de la méthodde *add()* en vérifiant qu'il reste de la place dans le tableau, et de la méthode *remove()* en vérifant que le tableau n'est pas vide, en ajoutant une boucle d'attente
- synchronisation des méthodes *add* et *remove* avec le même objet pour éviter qu'un un thread ajoute un élément au même moment qu'un autre thread retire un élément
- ce système ne fonctionne pas car si un thread rentre dans la méthode *produce* pour ajouter un élément et que le tableau est plein, il va rentrer dans la boucle d'attente en attendant que le tableau ait de la place. Sauf qu'un autre thread ne pourra pas exécuter la méthode *consume* car la clé est déjà possédé par le premier thread qui est dans la méthode *produce*. Si un thread rentre dans une boucle d'attente, il y restera pour toujours

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
    buffer[index] = index; index++; lock.notify(); }}
public int consume(){ synchronized(lock){
    while(isEmpty(buffer)){ lock.wait(); }
    int i = buffer[index]; index--; return i; lock.notify(); }}
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

## Les CPU multicoeurs

- depuis environ 2005, les processeurs ont plusieurs coeurs
- impact sur le fonctionnement et les patterns de la programmation concurrente
- vitesse des processeurs :
    - dans les années 90, les processeurs sont connectés à la RAM, en environ 70 ns (10⁻⁹ sec)
    - aujourd'hui, le temps d'accès à la RAM est toujours d'environ 70 ns, mais les CPU ne sont plus connectés directement à la RAM, ils sont capable de traiter les données à environ 1 ns par donnée.
- pour traiter ce problème de différence de vitesse, un empilement de cache a été mis en place sur le processseur, entre les coeurs du proccesseur et la RAM. Les caches les plus proches du processeurs sont plus rapides mais de taille plus petite :
    - au plus proche du processeur, les cache L1, un par coeur, avec une quantité de mémoire de l'ordre de 32ko. Il est capable de fournir des données à environ 1 ns
    - viennent ensuite les cache L2, d'environ 256ko, fourni des données à environ 3 ns
    - un cache L3, d'environ 8Mo, partagé entre tous les coeurs, fourni des données à environ 15 ns

----

## Les liens Happens Before

- l'architecture des CPU multicoeurs cela impacte la façon de programmer
- si un thread travaille sur un coeur du CPU et initialise une variable dans le cache du coeur, et qu'un autre thread veut utiliser cette variable, comment va t'il connaître sa valeur ? On parle de **visiblité** : comment un thread voit-il les modifications effectuées dans un autre thread
- à l'intérieur du langage du Java, il y a la notion de **Happens Before**, qui est un lien entre une écriture et une lecture. Un lien existe entre toute écriture synchronisée ou volatile et toute lecture synchronisée ou volatile qui suit
- par exemple, on a une classe avec une méthode qui incrémente un attribut *i* de la classe, et une autre méthode affichant sa valeur. La première méthode va être exécutée par un thread *t1* dans une boucle, et la deuxième méthode dans un thread *t2* dans une autre boucle. Comme il n'y a pas de synchronisation, il n'y a pas de lien Happens Before, il n'y a donc pas de garantie que t2 lise bien la valeur de *i*. Pour qu'il s'exécute correctement, il faut synchroniser le contenu des 2 méthodes
- les traitements qui se font entre les 2 threads vont devoir repasser par le cache L3 au lieu de rester dans le cache L1, ce qui ralenti les performances

----

## Notion de champs volatiles

- la *volatilité* est l'écriture ou la lecture sur un champ déclaré volatile
- donne la **visibilité** : les opérations de modifications sont visibles d'un thread à l'autre
- contrairement à la synchronisation, la volatilité **ne garantie pas l'atomicité** (impossibilité de donner la main à un autre thread sur un bloc synchronisé)

```java
private volatile int index = 0;

index++; // l'opération peut être interrompue par le Thread Scheduler entre la lecture de l'index et l'écriture de la nouvelle valeur de l'index
```

- revenons sur notre problème de **Double Check Locking** sur le pattern **Singleton**, le problème est que l'opération de lecture au début de la méthode *getInstance()* est non synchronisée, donc il n'y a pas de garantie de lire la bonne valeur si elle a été écrite dans un autre coeur. Pour éviter ce problème, on peut rendre le champ volatile

----

## Problèmes posés par le pattern Thread / Runnable

- avec la généralisation des CPU multicoeurs, le modèle de programmation concurrente évolue
- les notions de Thread et Runnable va être revue
- lorsqu'on thread est créé via le constructeur de la classe Thread puis démarré via sa méthode *start()*, on crée un objet sur une ressource système, et une fois que l'exécution est terminée, le thread va disparaître
- ce pattern est coûteux du fait des requêtes faites sur l'OS
- ce pattern laisse l'application créer des threads à la demande, ce qui peut poser problème si ce nombre devient trop important
- à partir de Java 5 (2004), nouveau pattern **Executor**

----

## Introduction de la notion d'ExecutorService

- appelé **Executor** ou **ExecutorService**, c'est une réserve de threads, qui sont créés au moment où cette réserve de threads est créée, et restent disponible durant toute la vie de l'objet Executor
- lors de la création d'une tâche Runnable, on la soumet à Executor, qui va prendre un thread disponible, et une fois terminé, rendre le thread à la réserve des threads
- cela règle le problème de création/destruction des threads inutiles et le problème de création de threads à la demande. Le pattern **Thread / Runnable** existe toujours mais il n'est plus souhaite de l'utiliser
- **ExecutorService** est une interface qui étend l'interface **Executor**. Il y a une classe factory **Executors** qui permet de retourner des instances d'ExecutorService :
    - `newSingleThreadExecutor()` : création d'un pool de thread avec un seul thread
    - `newFixedThreadPoolExecutor(i)` : permet de créer un pool de i threads
- la taille de l'ExecutorService (nombre de threads) dépend de la nature des tâches et du nombre de coeurs dans le CPU. Si les threads font essentiellement des calculs qui ne sortent pas de la mémoire, on va définir autant de threads que de coeurs sur le CPU. Si les threads font des opérations I/O, ils vont peu utiliser le CPU car ils vont passer leur temps à attendre, on peut avoir plus de threads que de coeurs sur le CPU
- l'*ExecutorService* gère une file d'attente des tâches s'il n'y a plus de threads disponible
- pour exécuter une tâche de type *Runnable*, il faut utiliser la méthode **execute(runnable)** de *ExecutorService*

----

## Créer et exécuter des tâches de type Callable

- jusqu'en Java 5, le modèle de tâche est l'interface Runnable avec une seule méthode abstraire **void run()**
- cette interface à quelques défauts : elle ne retourne pas de valeur et ne jette pas d'exception en cas de problème
- Java 5 introduit une nouvelle interface fonctionnelle **Callable<V>** qui a une unique méthode abstraire **V call() throws Exception**
- pour exécuter une tâche Callable, il faut utiliser la méthode **submit(callable)** d'ExecutorSubmit

```java
ExecutorService es = Executors.newFixedThreadPool(4);
Runnable r = () -> System.out.println("Coucou depuis le thread " + Thread.currentThread().getName());
es.execute(r); // Coucou depuis le thread pool-1-thread-1
Callable<Void> c = () -> {
    System.out.println("Bonjour depuis le thread " + Thread.currentThread().getName());
    return null;
};
es.submit(c); // Bonjour depuis le thread pool-1-thread-2
```

----

## Récupérer le résultat d'une tâche Callable au travers d'un Future

- *submit(callable)* renvoie un **Future<V>** qui modélise la communication entre le thread qui a créé la tâche et le thread qui exécute la tâche
- la méthode **get()** renvoie l'objet généré par le Callable. Si la tâche prend du temps à s'exécuter, la méthode **get()** va prendre du temps pour répondre. On dit que la méthode constitue un appel **bloquant**. Il existe une version de **get(timeout)** qui jette une exception si la tâche dépasse le temps spécifiée
- si la méthode *get()* renvoie une exception, elle va être captée par l'objet future et wrapée dans une **ExecutionException**, on va pouvoir la récupérer dans un try/catch et la rootCause est bien l'exception capturée dans l'objet future
- l'intérêt des Future est de pouvoir faire d'autres choses entre la soumission de la tâche et la récupération de son résultat, comme par exemple soumettre d'autres tâches

```java
ExecutorService es = Executors.newFixedThreadPool(4);
Callable<String> c = () -> "Bonjour";
Future<String> future = es.submit(c);
System.out.println(future.get()); // Bonjour
```

----

## Etats d'un Thread, transitions entre ces états

- lors de la création d'un thread, il est dans l'état **NEW** qui est un état transitoire. Aucune ressource système ne lui est encore affectée
- lorsqu'il exécute une tâche, il est dans l'état **RUNNABLE**. Sa méthode start() a été invoquée
- lorsqu'il attend à l'entrée d'un bloc synchronisé, il est dans l'état **BLOCKED**. Pour en sortir, et revenir à l'état RUNNABLE, il faut que la clé du bloc synchronisé se libère pour qu'il rentre dans le bloc
- il peut être dans l'état **WAITING** pour plusieurs raisons : attente à cause d'un accès I/O (retour à l'état Runnable lorsque l'accès est terminé), appel à la méthode *wait()* (appel de *notify()* pour en sortir)
- il peut être dans l'état **TIMED_WAITING** en appelant la méthode **sleep(timeout)** (revient à l'état Runnable lorsque le timeout est écoulé)
- lorsqu'il a fini d'exécuter une tâche, il est dans l'état **TERMINATED**


----

## Arrêt et interruption d'un thread

- on ne peut pas arrêter un thread
- il existe une méthode **stop()** qui est dépréciée qu'il ne faut donc pas appelé
- la méthode **interrupt()** permet de dire à un thread de s'arrêter
    - cela va passer le booléen *interrupt* du thread à true. Le thread lorsqu'il s'exécute, scrute régulièrement ce booléen et met un terme à la tâche en cours d'exécution
    - cependant, si un thread n'est pas dans l'état RUNNABLE, cela ne fonctionne pas
    - si le thread est dans l'état WAINTING ou TIMED_WAITING, cela va jeter une InterruptedException
    - si le thread est dans l'état BLOCKED, on ne peut rien faire pour l'arrêter, il faut qu'il récupère la clé du moniteur sur lequel il est synchronisé

----

## Arrêter un ExecutorService

- **shutdown()**
    - doit être appelé à la fin d'une application si on a créé un ExecutorService car ses threads sont ne sont pas de type daemon, ce qui fait que la JVM ne va pas pouvoir s'arrêter
    - les tâches en cours d'exécution vont se terminer normalement, les tâches dans la file d'attente vont être exécutées, les nouvelles tâches vont être refusées
- **shutdownNow()** : les tâches en cours d'exécution vont se terminer normalement, les tâches dans la file d'attente vont être retirées de la file d'attente et ne seront pas exécutées, les nouvelles tâches seront refusées

----

## Utilisation des variables atomiques

- dans notre classe *Compteur* qui a un champ privé index et 2 méthodes *increment()* et *getIndex()*, si plusieurs threads incrémentent en même temps cet index, on a va avoir de la concurrence d'accès et on va rater des comptages, et si un autre thread lit la valeur du compteur, on va avoir des problèmes de visibilté
- possibilité de gérer le problème en mettant le contenu des 2 méthodes dans un bloc synchronisé
- à partir de Java 5, il y a des nouvelles classes qui commencent par **Atomic** (*AtomicInteger*, *AtomicLong*, *AtomicReference*) qui vont gérer la synchronisation comme si on synchronisait les blocs des méthodes

```java
AtomicLong index = new AtomicLong(0L);
index.incrementAndGet(); // fait l'incrémentation et retourne la nouvelle valeur
// compilé en une seule instruction assembleur
```

----

## Primitives de synchronisation introduites en Java 5

nouveautés Java 5 qui rendent obsolète la synchronisation introduite dans les débuts de Java :
- l'interface **Lock**
- l'interface **ReadWriteLock**
- la classe **Semaphore**
- la classe **CyclicBarrier**
- l'interface **Latch**

----

## Synchronisation interruptible avec l'interface Lock

- l'objet **Lock** peut remplacer le bloc synchronisé grâce à ses méthodes **lock()** et **unlock()**, un seul thread peut exécuter le bloc de code entre le lock et le unlock
- la méthode unlock doit impérativement être appelée après la méthode lock, sinon l'objet lock sera verrouillé en permanance et aucun thread ne pourra exécuter la méthode. Il faut donc faire attention aux exceptions qui peuvent être levé dans notre code
- les méthodes **tryLock()** et **tryLock(timeout)** permettent aux threads de ne pas attendre dans la file d'attente (ou le temps en paramètre) si le lock est est verrouillé par un autre thread
- il existe un objet Condition : **Condition c = lock.newCondition();** et 2 méthodes **await()** et **signal()** équivalentes à *wait()* et *notify()* dans les blocs synchronisés

```java
Lock lock = new ReentrantLock();
try {
    lock.lock();
    // code
} finally {
    lock.unlock();
}
```

----

## ReadWriteLock pour les lectures concurrentes

- l'interface **ReadWriteLock** permet de s'assurer qu'on ne puisse faire qu'une seule écriture à la fois et plusieurs lectures en même temps en assurant la visibilité sur les modifications faites dans un coeur par les autres coeurs (lien Happens Before)
- le **writeLock** va empêcher d'autres threads d'exécuter le bloc qu'il garde (celui en écriture) et également le bloc de code gardé par le *readLock*
pendant la mise à jour de l'objet, aucun autre thread ne pourra lire l'objet
- le **readLock** ne va pas empêcher d'autre threads d'exécuter le bloc qu'il garde (celui en lecture), mais il empêche l'exécution du bloc gardé par le *writeLock*

```java
ReadWriteLock lock = new ReentrantReadWriteLock();
Lock readLock = lock.readLock();
Lock writeLock = lock.writeLock();

Map<String, Object> cache = ...
public void put(String key, Object o){
    try { writeLock.lock();
        cache.put(key, o);
    } finally { writeLock.unlock(); }
}
public Object get(String key){
    try { readLock.lock();
        return map.get(key);
    } finally { readLock.unlock(); }
}
```

----

## La classe Semaphore

- fonctionne comme un lock mais en spécifiant le nombre de threads qui peuvent rentrer dans le bloc de code en même temps, contrairement au lock qui n'en laisse rentrer qu'un

```java
Semaphore semaphore = new Semaphore(3);
try{
    semaphore.tryAcquire();
    // code
} finally { semaphore.release(); }
```

----

## La CyclicBarrier

- classe qui prend en paramètre le nombre de threads que la barrière peut gérer
- gros traitement que l'on va découper en plusieurs tâches pour qu'il soit exécuté par plusieurs threads
- récupération d'un signal quand toutes les tâches sont terminées. Chaque thread va exécuter la méthode **await()** et quand chaque thread aura exécuté cette méthode, alors la barrière sera "levée" et le code pourra continuer

```java
CyclicBarrier barrier = new CyclicBarrier(3);
```

----

## Utilisation du CountDownLatch pour lancer une application

- créer avec un nombre qui va être décrémenté en interne à chaque fois qu'une certaine méthode est appelé, et quand le latch (serrure) arrive à 0, il va s'ouvrir et laisser passer le thread en attente
- cela peut servir au démarrage d'une application en laissant les différents services qui ont besoin de s'exécuter pour que l'application fonctionne correctement
- à la différence d'une barrière, une fois qu'un latch est ouvert, il ne se referme jamais

----

## Les collections concurrentes

Les collections suivantes sont thread-safes, c'est-à-dire qu'elles peuvent être utilisé en multithread avec les garanties de performance, de visibilité et de synchronisation :
- CopyOnWriteArrayList
- BlockingQueue
- ConcurrentHashMap

----

## CopyOnWriteArrayList

- c'est un tableau en mémoire qui à une particularité lorsqu'il y a une opération de modification : le tableau est copié avec le nouvel élément et le pointeur du tableau est ensuite déplacé de manière atomique sur le nouveau tableau : le tableau est donc **immutable**
- c'est une opération lourde qui a l'avantage de pouvoir être lu pour tous les threads en même temps car le tableau n'est jamais modifié
- utile quand il y a beaucoup de lecture et peu d'écriture

----

## Les files d'attente concurrentes

- LIFO (Last In First Out) et FIFO (First In First Out)
- interface **Queue** et **Deque** qui est une extension de *Queue* qui permet d'aller chercher des éléments des 2 côtés de la file d'attente
- les interfaces **BlockingQueue** et **BlockingDeque** supportent la concurrence
- les implémentations sont **ArrayBlockingQueue** (taille fixée à la construction), **LinkedBlockingQueue** (taille fixe ou taille extensible au choix) et **SynchronousBlockingQueue** (file de taille 0). Et enfin **LinkedBlockingDeque**
- il existe aussi une autre implémentation de **Queue** : **ConcurrentLinkedQueue** (taille extensible)

----

## Comportements de BlockingQueue lorsque la file d'attente est vide ou pleine

- lorsque la file d'attente est pleine et qu'on souhaite ajouter un élément ou vide et qu'on souhaite en retirer un
    - on peut retourner une valeur particulière (booléen)
    - bloquer jusqu'à ce qu'une case se libère ou un élément soit disponible
    - bloquer avec un timeout
    - jeter une exception
- la *SynchronousBlockingQueue* est juste un transmetteur d'élément entre un consommateur et un producteur. Très performant quand il y a beaucoup de producteurs et de consommateurs

----

## La ConcurrentHashMap

- changement d'implémentation à partir du JDK 8. Jusqu'en Java 7, elle fonctionne correctement jusqu'à 16 threads, beaucoup plus dans l'implémentation de Java 8
- utile pour faire des caches