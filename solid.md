# Principes SOLID et Design Patterns

## Introduction

- en programmation orientée objet, **SOLID** est un acronyme mnémonique qui regroupe cinq principes de conception destinés à produire des architectures logicielles plus compréhensibles, flexibles et maintenables.
- **design pattern** : ensemble de modèles de code, pour organiser correctement son code, qui vont permettre de respecter les principes SOLID
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

```java
public interface Employee {}
public class CDI implements Employee {}
```

- création d'une interface factory pour créer des employés : cela permet de créer une couche d'isolation entre les notions de haut niveau qui sont gérés par l'application, et les détails d'implémentation qui sont en détails de ces règles de haut niveau. L'application ne manipule pas directement des objets `CDD` et `CDI` mais des `Employee`
- le module *main* est le seul qui a l'intégralité des modules de l'application en dépendance. Le principe d'injecter les dépendances par le constructeur est une façon d'appliquer le principe d'**inversion de dépendance**, à ne confondre avec l'**injection de dépendance** qui se passe à l'exécution de l'application (injection des implémentations que l'on a créer en faisant des news ou avec des frameworks comme Spring)

```java
public interface EmployeeFactory {
	Employee createCDI(...);
	Employee createCDD(...);
}
```

Le fait d'avoir mis une méthode de création pour chaque implémentation d'Employee crée une dépendance car cela nécessite de modifier l'interface en cas d'ajout d'une noubelle classe d'implémentation
- il faut modifier l'interface pour qu'il n'y ait plus qu'une seule méthode qui prend un paramètre supplémentaire (par exemple un label) pour reconnaître la classe à implémenter
- cette façon de faire une factory en passant par une interface s'appelle le **Pattern Abstract Factory**

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

- le problème avec la gestion des différentes implémentations avec des switchs ou des if/else est qu'on va la retrouver dans différentes méthodes, et c'est dangereux car il y a un risque d'oublier d'en mettre un à jour lorsque les implémentations changent
- pour contourner ce problème, il faut utiliser le **Pattern Strategy**, qui permet de fournir une solution pour implémenter l'**Open Closed principle** 
- les paramètres pour créer les différentes implémentations d'`Employee` doivent être commun

```java
public abstract class EmployeeType {
	abstract Employee create(...);
}
public class CDDType extends EmployeeType {
	public Employee create(...){
		return new CDD(...);
	}
}
public class CDIType extends EmployeeType {
	public Employee create(...){
		return new CDI(...);
	}
}
// code client qui va remplacer la méthode create de EmployeeFactoryImpl pour ne plus dépendre des différentes implémentations existantes
EmployeeType type = EmployeeType.of(label);
Employee employee = type.create(...);
```

- à l'intérieur de la méthode `of()`, il y a encore un switch ou if/else, mais il n'applique pas la stratégie, il fait juste la choisir
- on dit que le code client est fermé à la modification : il n'est pas nécessaire de modifier le code client s'il y a des nouvelles implémentations
- on dit que le code est ouvert à l'extension de nouvelles classes d'implémentations
- cela correspond au deuxième principe SOLID **Open Closed principle** ou **principe ouvert fermé**, qui dit qu'une application doit êter fermée à la modification mais ouverte à l'extension
- pour augmenter les fonctionnalités présentes dans une application, il faut juste étendre des classes sans avoir à modifier le code client


## Autre implémentation de ce pattern par composition

- pour éviter d'avoir à utiliser une classe abstraire, et autant de classes abstraire qu'on a de stratégies dans le switch originel
- il est possible d'implémenter ce pattern différemment, en utilisant la **composition** à la place de l'héritage
- on aura alors une classe unique en utilisant des lambdas expressions

```java
public class EmployeeType {
	private String label;
	private Function<String, Employee> create;
	public EmployeeType(String label, Function<String, Employee> create){
		this.label = label;
		this.create = create;
	}
	public String type(){
		return label;
	}
	public Employee create(String name){
		return create.apply(name);
	}
}

EmployeeType cddType = new EmployeeType("CDD", name -> new CDD(name));
EmployeeType cdiType = new EmployeeType("CDI", name -> new CDI(name));
List<EmployeeType> types = List.of(cddType, cdiType);
// création d'un employée à partir d'un EmployeeType
Employee cdd = cddType.create("toto");
// création d'une méthode pour créer un employé à partir de la liste d'EmployeeType
Employee create(String label, String name){
	return types.stream()
		.filter(t -> t.getType().equals(label))
		.findAny()
		.map(t -> t.create(name))
		.orElseThrow();
}
```

## Design Patterns et GoF (Gang of Four)

- Livre de 1994 sur les Design Patterns, *Elements of Reusable Object-Oriented Software*, écrit par 4 auteurs, surnommés **Gang of Four**
- écrit avec le langage C++, mais s'adapte très bien aux autres langages objets, notamment `Java` et `C#`
- 23 patterns y sont décrits, qui permettent de résoudre quasiment tous les problèmes de la programmtion objet
- ils se regroupent dans 3 catégoris
	- patterns de **Construction** (5) : Factory, AbstractFactory, Singleton, Builder, Prototype
	- patterns **Structurels** (7)
	- patterns **Behavioral** (11) : TemplateMethod, Strategy, State, Chain of Command, Iterator, Visitor


 ## Pattern Singleton

- une classe qui suit le pattern singleton ne peut avoir qu'une seule instance
- l'intérêt est d'économiser de la mémoire et également du CPU (à cause du garbage collector)
- souvent utilisé pour les classes de service
- créaton d'un constructeur privé pour ne pas que l'on puisse instancier la classe

Une première façon simple d'implémenter le pattern Singleton est de passer par un attribut et un accesseur `static`.

```java
public class MyService {

	private MyService(){}

	private static MyService service = new MyService();

	public static MyService getInstance(){
		return service;
	}
}
```

L'instantiation n'est pas contrôlée, on souhaiterait qu'elle soit contrôlé, en mode **LAZY**, c'est-à-dire qu'au moment où on en a besoin, pour éviter de créer l'objet si jamais on ne s'en sert jamais.

L'alternative recommandée est de passer par une énumération, qui permet d'avoir une instanciation thread-safe

```java
public enum MyService {
	INSTANCE;
}
```

## Pattern Builder

- sert à construire des objets complexes
	- nombre de champs important : règles de validité complexes, problèmes de complétude (une bonne partie des champs restent souvent vide)
	- écrire un constructeur avec beaucoup de paramètres est une source d'erreur importante
	- si la validation est en erreur, obligation de jeter une exception dans le constructeur, ce qui n'est pas une bonne pratique
- en passant par une factory, il est possible d'appliquer la validation avant d'appeler le constructeur mais ça ne règle pas le problème du constructeur avec beaucoup de paramètres


Utilisation d'un objet intermédiaire (une classe interne statique), qui expose autant de méthode qu'il y a de champs
- la gestion de la validation se fait dans la méthode `build`

```java
public class Contrat {
	private final String champ1;
	private final String champ2;

	private Contrat(ContratBuilder builder){
		this.champ1 = builder.champ1;
		this.champ2 = builder.champ2;
	}

	// tous les getters et pas de setters pour garantir l'immutabilité
	public String getChamp1(){
		return champ1;
	}
	public String getChamp2(){
		return champ2;
	}

	public static class ContratBuilder {
		private final String champ1;
		private final String champ2;
		
		public ContratBuilder champ1(String s){
			this.champ1 = s;
			return this;
		}
		public ContratBuilder champ2(String s){
			this.champ2 = s;
			return this;
		}
		public build(){
			Contrat c = new Contrat(this);
			validateObject(c)
			return c;
		}
		private void validateObject(Contrat c){
			// ...
		}
	}
}

Contrat builder = new Contrat.ContratBuilder()
	.champ1(...)
	.champ2(...)
	.build();
```

## Interface Segregation Principle

- ce principe dit qu'il ne faut pas dépendre d'une interface qui possède des méthodes que l'on n'utilise pas
- une interface doit avoir le nombre minimum de méthodes pour fonctionner

```java
public interface ServiceTVA {
	int getTVA(String countryCode);

	int getTVA(CountryCode code);
	CountryCode getCountryCode(String countryCode);
}

public class InvoiceProcess {
	private ServiceTVA service;

	public InvoiceProcess(ServiceTVA s){
		this.service = s;
	}

	public int computeTVA(int amount){
		return service.getTVA("FR") * amount;
	}
}
```

- exemple de refactor de l'interface, ce qui oblige à recompiler le code client alors que ces méthodes ne sont pas utilisées dans le code client

```java
public interface ServiceTVA {
	int getTVA(String countryCode);
	int getTVA(CountryCode code);
	CountryCode getCountryCode(String countryCode);
}
```

## Bilan sur les principes SOLID

- Single Responsibility Principle : on ne doit avoir qu'une seule raison de modifier une méthode ou une classe
- Open-Closed Principle : s'il y a besoin de faire évoluer les fonctionnalités de l'application, il suffit d'étendre certaines classes
- Liskov Substitution Principle : concerne la relation d'héritage
- Interface Segregation Principle : quand une classe définit une dépendance vis à vis d'une interface, il faut que les méthodes de cette interface soient minimales par rapport aux méthodes dont le client a besoin
- Dependency Inversion Principle : pour inverser la dépendance entre 2 modules applicatifs, il faut insérer une interface entre les 2, et les dépendances à la compilation vont toutes les 2 pointer vers cette interface, ce qui protège le code client contre les modifications des implémentations

Cet acronyme **SOLID** a été créé par *Bob Martin*, alias *Uncle Bob*.

## Pattern Visitor

- c'est le pattern le plus compliqué à comprendre et à mettre en oeuvre, il traite des **hiérarchies d'objets**
- l'objectif est de **séparer les traitements du modèle objet**

Exemple :
- un objet `Company` référence une liste de `Department`, qui référence eux-mêmes des `Project`, référençant eux-mêmes des `Employee`
- cette hiérarchie est modélisée par des interfaces (les 4 objets ci-dessus), avec des implémentations qui peuvent être multiples
- le pattern Visitor permet d'ajouter des fonctionnalités sur la hiérarchie d'objet, comme par exemple compter le nombre de salariés, ou ceux d'un certain type. Il va donc falloir **visiter** tous les objets de la hiérarchie

Création d'un visiteur, modélisé par une interface, capable de visiter l'intégralité des objets de la hiérarchie. Il faut donc autant de méthodes que d'interface dans la hiérarchie
- ajout d'une méthode `accept(Visitor v)` dans les interfaces de nos objets, ce qui crée une dépendance pour nos objets vers l'interface Visitor, il va donc falloir la mettre dans le module avec les objets

```java
public interface Visitor {
	void visitCompany(Company c);
	void visitDepartment(Department d);
	void visitProject(Project c);
	void visitEmployee(Employee e);
}

public interface Company {
	void accept(Visitor v);
}
public interface Project {
	void accept(Visitor v);
}
...

public class HRDepartment implements Department {
	public void accept(Visitor v){
		// transmettre le visitor au noeuds enfants
		this.projects.forEach(p -> p.accept(v));
		// le visitor visite l'objet dans lequel il est (this) par callback
		v.visitDepartment(this);
	}
}
```

Implémentation du Visitor en fonction des traitements qu'on veut faire. Exemple qui compte le nombre d'employés

```java
public class CountingVisitor implements Visitor {
	private long count = 0L;
	void visitCompany(Company c){} // méthode ne faisant rien
	void visitDepartment(Department d){} // méthode ne faisant rien
	void visitProject(Project c){} // méthode ne faisant rien
	void visitEmployee(Employee e){
		count++;
	}
	public getCount(){
		return count;
	}
}

public class Main {
	public static void main(String[] args){
		Visitor countingVisitor = new CountingVisitor();
		Company company = ...;
		company.accept(countingVisitor);
		countingVisitor.getCount(); // nombre d'employés
	}
}
```

Bilan sur le pattern Visitor :
- utile quand il y a des hiérarchies d'objets, modélisés par des interfaces et des implémentations
- utile quand il y a des traitements à faire sur les objets


## Architecture d'une application

l'application va fonctioner en cercles concentriques, ou les cercles supérieurs dépendent des cercles inférieurs, mais les cercles inférieurs ne dépendent pas des cercles supérieurs

- il faut mettre au centre l'application ce qui bouge le moins souvent, en l'occurence le module objet
- le deuxième couche va contenir les traitements métier qui agissent sur les objets
- la couche suivante va concerner les liens avec l'extérieur : IHM (Controller), bases de données et services d'autres applications (Gateaway)
- à l'extérieur, il y a des IHM comme React qui vont appeler le Controller, et des services REST ou des bases de données qui vont être appelés dans les gateways

### Mise en place de la partie IHM

- utilisation de DTO (Data Transfert Object) pour communiquer avec l'extérieur

### Mise en place de l'accès à la BDD

- utilisation d'une interface DataAccess qui va être appelée par les traitements métiers
- création d'une implémentation, généralement en utilisant un framework qui implémente JPA
- cela crée une dépendance entre notre modèle objet et notre framework => il faut donc créer des objets spécifiques qui portent les annotations JPA

## Bilan sur les Design Patterns et les Principes SOLID

- ces principes sont indépendants des langages qu'on utilise et peuvent à chaque fois être appliqués
- l'objectif est de minimiser les coûts d'entretien et de livraison d'une application
