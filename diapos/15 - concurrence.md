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
// il n'y a pas eu de Thread de créé, la tâche est donc exécuté par le thread courant, ici le thread main
task.run();

// création d'un Thread en lui passant la tâche qu'il doit prendre en charge
Thread t = new Thread(task);
// démaragge du Thread avec la méthode start()
// elle va exécuter la méthode run() de notre tâche dans un thread différent
t.start();
```

----
