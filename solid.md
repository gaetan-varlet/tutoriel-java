# Principes SOLID et Design Patterns

## Introduction

- en programmation orientée objet, **SOLID** est un acronyme mnémonique qui regroupe cinq principes de conception destinés à produire des architectures logicielles plus compréhensibles, flexibles et maintenables.
- **design pattern** : ensemble de modèles de code, qui vont permettre de respecter les principes SOLID
- les qualités d'une architecture logicielle
	- permettre de tester l'application de manière automatique
	- permettre à l'application d'évoluer
	- les choix techniques peuvent être revus en les isolant

## Single Responsability Principle

- le premier principe SOLID est le **Single Responsability Principle** ou **Principe de responsabilité unique**
- il dit qu'il ne doit y avoir qu'une raison de changer un bloc de code, autrement dit qu'une classe ou méthode ne doit avoir qu'une seule responsabilité
- il faut pour cela faire de la **delégation**, en faisant porter une tâche particulière à un objet tiers, au lieu de la faire directement dans la classe ou la méthode
- faire de la delégation permet de **diminuer le nombre de responsabilités**

Exemple où l'on retire la responsabilité de la sauvegarde en base de données de l'objet à la classe `Employee` pour la donner à la classe `DBService`. 

```java
public class Employee {
	saveToDB()
}
```

```java
public class Employee {
	DBService dbService;
	saveToDB(){
		dbService.saveToDB(this);
	}
}
public class DBService {
	saveToDB(Employee e){
		...
	}
}
```

Cependant, création d'une dépendance de la classe `Employee` vers `DBService` à la compilation : une modification de `DBService` va donc nécessiter une recompilation et une relivraison de `Employee`.

## Dependency Inversion Principle

- le cinquième principe de SOLID est **Dependency Inversion Principle**, ou **Principe d'inversion des dépendances**
- il dit qu'il faut dépendre des abstractions et pas des implémentations
- ce sont les détails d'implémentation (BDD MySQL ou Postgre) qui doivent dépendre des notions de haut niveau (enregistrer les employés dans une BDD)
- lorsqu'un détail d'implémentation change, cela n'a alors aucun impact sur aucune des parties de l'application
- création d'une **interface** à la place d'une classe pour inverser la dépendance
	- on a alors un premier module avec `Employee` qui dépend de l'interface `DBService`, et un second module avec `DBServiceImpl` qui dépend du premier module
	- la mise à jour du second module n'a alors pas d'impact sur le premier module qui n'a pas besoin d'être recompilé
```java
public class Employee {
	DBService dbService;
	saveToDB(){
		dbService.saveToDB(this);
	}
}
public interface DBService{
	saveToDB(Employee e);
}
public class DBServiceImpl implements DBService {
	saveToDB(Employee e){
		...
	}
}
```

Pour que ça fonctionne, il faut donner une implémentation de l'interface `DBService`. Il est possible d'utiliser une méthode factory (mauvaise façon) :
- cela permet d'appliquer le **pattern Singleton** pour qui permet de n'avoir qu'une instance d'une classe à l'échelle de l'application
- cette méthode fait que l'on doit avoir le nom de l'implémentation dans la méthode `getInstance()` et va créer une dépendance circulaire entre l'interface et la classe d'implémentation. Ce n'est donc pas la bonne façon de faire

```java
DBService dBService = DBService.getInstance(...);
```

Pour conserver l'inversion de dépendance, il faut déclarer que la classe `Employee` dépend d'une instance de `DBService`. On parle d'**injection de dépendance**

```java
public class Employee {
	private DBService dBService;
	public Employee(DBService service){
		this.dBService = service;
	}
}
```

Toutes les instanciations des implémentations vont se faire dans la méthode `main()`
- instanciation d'une implémentation de `DBService`
- instanciation d'`Employee` en lui passant l'instance du service
- la méthode `main` est alors la seule à connaître les détails d'implémentation

```java
DBService service = new DBServiceImpl();
Employee employee = new Employee(service);
```

## Méthodes métier de la classe Employee

- avec cette organisation, il y a une dépendance circulaire entre `DBService` et `Employee` car `DBService` prend un objet `Employee` en paramètre
- la méthode `saveToDB()` ne devrait pas être dans la classe `Employee`. Cette classe sert uniquement à transporter un **état**, il faut plutôt avoir une classe de service


## Liskov Substitution Principle

- le troisième principe SOLID est le **Liskov Substitution Principle**, ou **Principe de substitution de Liskov**
- si A se comporte comme B, alors A étend B. Partout où sont utilisées des instances de B, il est possible d'utiliser des instances de A de la même façon
- il dit qu'une instance de type T doit pouvoir être remplacée par une instance de type G, tel que G sous-type de T, sans que cela ne modifie la cohérence du programme

Exemple
- modélidation de la gestion d'employées en CDI et en CDD
- création d'une interface `Employee` et de 2 classes `CDI` et `CDD` qui implémentent cette interface
- l'application consomme cette interface pour appeler les différents processus métiers
- création d'une interface factory pour créer des employés : cela permet de créer une couche d'isolation entre les notions de haut niveau qui sont gégrés par l'application, et les détails d'implémentation qui sont en détails de ces règles de haut niveau. L'application ne manipule pas directement des objets `CDD` et `CDI` mais des `Employee`. Le module *main* est le seul qui a l'intégralité des modules de l'application en dépendance

```java
public interface EmployeeFactory {
	Employee createCDI(...);
	Employee createCDD(...);
}
```

Le fait d'avoir mis une méthode de création pour chaque implémentation d'Employee crée une dépendance car cela nécessite de modifier l'interface en cas d'ajout d'une noubelle classe d'implémentation
- il faut modifier l'inteface pour qu'il n'y ait plus qu'une seule méthode qui prend un paramètre supplémentaire (par exemple un label) pour reconnaître la classe à implémenter
- cette façon de faire une factory en passant par une interface s'appelle le pattern **Abstract Factory**

```java
public interface EmployeeFactory {
	Employee create(String label, ...);
}
public class EmployeeFactoryImpl implements EmployeeFactory {
	public Employee create(String label, ...){
		if("CDD".equals(label)){
			return new CDD(...);
		} else if ("CDI".equals(label)){
			return new CDI(...);
		}
	}
}
```

- le problème avec la gestion des différentes implémentations avec des switchs ou des if/else est dangereux car il y a un risque d'oublier d'en mettre un à jour lorsque les implémentations changent
- pour contourner ce problème, il faut utiliser le **Pattern Strategy**, permet de fournir une solution pour implémenter l'**Open Closed principle**

```java
public abstract class EmployeeType {
	abstract Employee create(...);
}
public class CDDType extends EmployeeType {
	public Employee create(...){ return new CDD(...); }
}
public class CDIType extends EmployeeType {
	public Employee create(...){ return new CDI(...); }
}
// code client qui va remplacer la méthode create de EmployeeFactoryImpl pour ne plus dépendre des différentes implémentations existantes
EmployeeType type = EmployeeType.of(label);
Employee employee = type.create(...);
```

- on dit que le code client est fermé à la modification : il n'est pas nécessaire de modifier le code client s'il y a des nouvelles implémentations
- on dit que le code est ouvert à l'extension de nouvelles classes d'implémentations
- cela correspond au deuxième principe SOLID **Open Closed principle** ou **principe ouvert fermé**, qui dit qu'une application doit êter fermée à la modification mais ouverte à l'extension
- pour augmenter les fonctionnalités présentes dans une application, il faut juste étendre des classes sans avoir à modifier le code client


## Autre implémentation de ce pattern par composition

- utilisation d'une classe unique en utilisant des lambdas expressions

```java
public class EmployeeType {
	private String label;
	Function<String, Employee> create;
	public EmployeeType(String label, Function<String, Employee> create){
		...
	}
	public Strung type(){
		return label;
	}
	public Employee create(String name){
		return create.apply(name);
	}
}

EmployeeType cdd = new EmployeeType("CDD", name -> new CDD(name));
EmployeeType cdd = new EmployeeType("CDI", name -> new CDI(name));
List<EmployeeType> types = List.of(cdd, cdi);
```
