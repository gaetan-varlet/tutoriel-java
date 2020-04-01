# L'API Java Time

----

## Principes

Création d'une nouvelle API `java.time` en remplacement des classes historiques `java.util.Date` et `java.util.Calendar`.  
Cette nouvelle API a 2 conceptions du temps :
- temps machine (nombre de seconde depuis le 1er janvier 1970) ou timestamp
- temps humain (jour, mois, année...)

----

## Le temps machine

```java
// Exemple de temps machine avec la classe Instant
Instant debut = Instant.EPOCH;
Instant now = Instant.now(); // maintenant avec l'heure UTC
System.out.println(debut); // 1970-01-01T00:00:00Z
System.out.println(now); // 2019-12-12T13:33:05.470185500Z
System.out.println(debut.isAfter(now)); // false

System.out.println(debut.getEpochSecond()); // 0
System.out.println(now.getEpochSecond()); // 1 576 157 585 (nombre de secondes depuis le 1er janvier 1970)
```

----

## Le temps humain (1)

```java
// Exemple de temps humain avec LocalDate, LocalDateTime et LocalTime
// Exemple avec la date du moment
LocalDateTime currentTime = LocalDateTime.now(); // maintenant avec l'heure du sytème horaire par défaut (Paris UTC+1)
System.out.println(currentTime); // 2019-12-12T15:04:28.173887
System.out.println(LocalDate.now()); // 2019-12-12
System.out.println("jour : " + currentTime.getDayOfMonth() + ", mois : " + currentTime.getMonthValue() + ", année : " + currentTime.getYear()); // jour : 12, mois : 12, année : 2019
System.out.println(currentTime.getDayOfWeek() + " " + currentTime.getDayOfMonth() + " " + currentTime.getMonth()); // THURSDAY 12 DECEMBER
// Exemple avec une date donnée
LocalDate dateNaissanceLouis = LocalDate.of(2018, Month.MARCH, 28);
LocalDate dateNaissanceGaetan = LocalDate.parse("1988-04-12"); // formatter par défaut
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
LocalDate dateNaissanceFlorine = LocalDate.parse("19-03-1990", formatter); // avec le formatteur que l'on vient de définir
System.out.println(dateNaissanceLouis); // 2018-03-28
System.out.println(dateNaissanceGaetan); // 1988-04-12
System.out.println(dateNaissanceFlorine); // 1990-03-19
// Afficher une date avec un formatteur
DateTimeFormatter f = DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy HH:mm:ss");
System.out.println(LocalDateTime.now().format(f)); // jeudi 12 décembre 2019 15:04:28
// Calculer une durée entre 2 dates
Period period = Period.between(dateNaissanceGaetan, dateNaissanceFlorine); // retourne en années, mois, jours
long nbJours = ChronoUnit.DAYS.between(dateNaissanceGaetan, dateNaissanceFlorine); // retourne en jours
System.out.println(period); // P1Y11M7D
System.out.println(String.valueOf(nbJours)); // 706
```

----

## Le temps humain (2)

```java
// Conversions
// Convertir une java.util.Date en java.time.LocaDate
java.text.DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
java.util.Date date = df.parse("12-04-1990");
LocalDate date2 = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
System.out.println(date2); // 1990-04-12
// Convertir une java.time.LocalDate en java.util.Date
LocalDate ld = LocalDate.of(1988, 10, 28);
java.util.Date ud = java.sql.Date.valueOf(ld);
System.out.println(ud); // 1988-10-28

// TemporalAdjusters : permettent d'ajuster des LocalDate pour se positionner sur le prochain jeudi ou le premier lundi du mois...
LocalDate maNaissance = LocalDate.of(1988, Month.APRIL, 12);
System.out.println(maNaissance.getDayOfWeek()); // TUESDAY
//Le prochain samedi
LocalDate prochainSamedi = maNaissance.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
System.out.println(prochainSamedi); // 1988-04-16	
//Le premier mardi du mois suivant : création d'une date au premier jour du mois suivant avant de trouver le 1er mardi
LocalDate premierJourMoisSuivant = maNaissance.with(TemporalAdjusters.firstDayOfNextMonth());
System.out.println(premierJourMoisSuivant); // 1988-05-01
LocalDate premierMardiDuMoisSuivant = premierJourMoisSuivant.with(TemporalAdjusters.nextOrSame(DayOfWeek.TUESDAY));
System.out.println(premierMardiDuMoisSuivant);
```

----

## Le temps humain (3)

```java
// Fuseaux horaires
System.out.println(ZoneId.systemDefault()); // Europe/Paris
System.out.println(ZoneId.of("Europe/Paris").getRules()); // ZoneRules[currentStandardOffset=+01:00]
System.out.println(ZoneId.of("America/Indiana/Indianapolis").getRules()); // ZoneRules[currentStandardOffset=-05:00]
ZonedDateTime paris = ZonedDateTime.now();
System.out.println(paris); // 2019-12-12T16:17:01.053596+01:00[Europe/Paris]
System.out.println(paris.withZoneSameInstant(ZoneId.of("America/Indiana/Indianapolis"))); // 2019-12-12T10:17:01.053596-05:00[America/Indiana/Indianapolis]

// Conversions de Instant en LocalDateTime et inversement
// Instant -> LocalDateTime
Instant instant = Instant.now();
ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("Europe/Paris"));
LocalDateTime localDateTime = instant.atZone(ZoneId.of("Europe/Paris")).toLocalDateTime();
LocalDateTime localDateTime2 = LocalDateTime.ofInstant(instant, ZoneId.of("Europe/Paris"));
System.out.println(instant); //2019-12-12T15:23:46.766189600Z
System.out.println(zonedDateTime); // 2019-12-12T16:23:46.766189600+01:00[Europe/Paris]
System.out.println(localDateTime); // 2019-12-12T16:23:46.766189600
System.out.println(localDateTime2); // 2019-12-12T16:23:46.766189600
// LocalDateTime -> Instant
LocalDateTime ldt = LocalDateTime.now();
System.out.println(ldt);
Instant i = ldt.atZone(ZoneId.of("Europe/Paris")).toInstant(); // 2019-12-12T16:35:20.772508800
System.out.println(i); // 2019-12-12T15:35:20.772508800Z
```
