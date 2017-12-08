# Conversion d'un String en nombre

----

## Conversion de String en int et Integer

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
