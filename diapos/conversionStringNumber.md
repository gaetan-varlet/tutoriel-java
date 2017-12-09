## Conversion d'un String en nombre

### Conversion d'un String en int et Integer

Exemple de conversion de String en int et Integer :
```java
String str = "123";
int x = Integer.parseInt(str);
Integer y = Integer.valueOf(str);
```
Les String `null` ne sont pas acceptés

----

### Exemple de méthode pour convertir un String en Integer
 * accepte `null` en argument

```java
public static Integer convertStringToInteger(String nbAConvertir){
	Integer integer = null;
	if(nbAConvertir!=null){
		integer = Integer.valueOf(nbAConvertir);			
	}
	return integer;
}
```

----

### Exemple de méthode pour convertir un String en Double
 * accepte `null` en argument
 * convertit les virgules dans les nombres en points

```java
public static Double convertStringToDouble(String nbAConvertir){
	Double nombre = null;
	if(nbAConvertir!=null){
		nombre = Double.valueOf(nbAConvertir.replace(",", "."));
	}
	return nombre;
}
```

----

## Conversion d'un nombre en String

### Conversion d'un int et d'un Integer en String

Exemple de conversion de String en int et Integer :
```java
int x = 12;
String str = String.valueOf(x);
Integer y = 15;
String str2 = String.valueOf(y);
```
Autre possibilité :
```java
String str3 = Integer.toString(145);
String str4 = y.toString;
```
Ces deux dernières possibilités n’acceptent pas un Integer null.

