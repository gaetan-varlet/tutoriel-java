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

sur certains blocs de code, possibilité d'empêher le *Thread Scheduler* d'interrompre un thread, plus précisément d'empêcher un autre thread d'entrer dans ce bloc de code. Pour cela il faut utiliser le mot clé **synchronized**
- qui prend un objet **lock** en paramètre, qui va servir de verrou sur le bloc de code synchronisé. Cet objet lock (comme tous les objets en Java) possède une clé
- si un thread t1 exécute le code synchronisé, il prend la clé du lock, qui n'a donc plus sa clé. S'il est interrompu par le *Thread Scheduler*, et qu'un thread t2 essaie d'exécuter le code synchronisé, il va être bloqué à l'entrée du bloc de code dans une file d'attente appelé **WAIT_LIST** (ou sont mis en attente les threads) en attendant que lock récupère sa clé. Le *Thread Scheduler* va mettre t2 en attente, et redonner la main à d'autres threads. Quand t1 finit l'exécution du bloc synchronisé, il rend la clé à lock, et quand le *Thread Scheduler* redonne la main à t2, il peut récupérer la clé auprès de lock et exécuter le code du bloc synchronisé

```java
Object lock = new Object();
synchronized(lock){
    ...
}
```

----

