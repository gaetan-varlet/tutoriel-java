# La programmation asynchrone

----

## Introduction

Lorsqu’une tâche est exécutée de manière **synchrone**, il faut attendre la fin d’une tâche avant de passer à une autre. En **asynchrone**, lorsqu’une tâche est exécutée, on peut directement passer à une autre tâche avant qu’elle ne soit terminé.

----

## CompletableFuture

- nouveauté Java 8 dans le package java.util.concurrent
- permet de lancer une tâche et récupérer un résultat plus tard avec `get()` ou `join()`

Exemple synchrone qui va durer 4 secondes :
```java
public static String calculateSync() throws InterruptedException {
	Thread.sleep(2000);
    return "calcul synchrone";
}

System.out.println(LocalTime.now()); // 19:58:35.324
System.out.println(calculateSync()); // calcul synchrone
System.out.println(LocalTime.now()); // 19:58:37.341
System.out.println(calculateSync()); // calcul synchrone
System.out.println(LocalTime.now()); // 19:58:39.342
```

----

Il est possible de lancer le deuxième une fois le premier lancé sans attendre le résultat du premier grâce à la méthode `supplyAsync()` d'un **CompletableFuture**. Cette méthode initialise une tâche qui sera mise en file d’attente et exécutée via un pool d’exécution Fork/Join. Cela prendra donc le temps du plus long appel, ici 2 secondes :

```java
public static CompletableFuture<String> calculateAsync() throws InterruptedException {
	return CompletableFuture.supplyAsync(() -> {
		try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }
		return "calcul asynchrone 2 sec";
	});
}

System.out.println(LocalTime.now()); // 19:53:54.330
CompletableFuture<String> cf1 = calculateAsync();
CompletableFuture<String> cf2 = calculateAsync();
System.out.println(LocalTime.now()); // 19:53:54.349
System.out.println(cf1.join()); // calcul asynchrone 2 sec
System.out.println(LocalTime.now()); // 19:53:56.350
System.out.println(cf2.join()); // calcul asynchrone 2 sec
System.out.println(LocalTime.now()); // 19:53:56.351
```

----

On peut combiner des CompletableFuture. La méthode statique `allOf()` prend un ensemble de CompletableFuture pour en faire un nouveau qui rendra la main lorsque tous seront terminés après l'appel de la méthode `join()` :

```java
System.out.println(LocalTime.now()); // 20:25:17.048
CompletableFuture<String> cf1 = (CompletableFuture<String>) calculateAsync();
CompletableFuture<String> cf2 = (CompletableFuture<String>) calculateAsync();
CompletableFuture<Void> combinedFuture  = CompletableFuture.allOf(cf1, cf2);
System.out.println(LocalTime.now()); // 20:25:17.059
combinedFuture.join();
System.out.println(LocalTime.now()); // 20:25:19.060
System.out.println(cf1.join()); // calcul asynchrone 2 sec
System.out.println(LocalTime.now()); // 20:25:19.061
System.out.println(cf2.join()); // calcul asynchrone 2 sec
System.out.println(LocalTime.now()); // 20:25:19.062
```
