# API Java pour XML

----

## Introduction

- le XML est un langage de balises (comme le HTML), qui va donner un sens au contenu textuel qu'il contient
- standard géré le W3C (qui s'occupe aussi de HTML, CSS, XSL...)
- indépendant de n'importe quel langage informatique
- permet de transporter des données
- le XML est utilisé :
    - pour transférer des données via les services Web (de plus en plus remplacé par le JSON)
    - dans les fichiers de configuration (est concurrencé par JSON et YAML)

----

## API XML pour le langage Java

- API **Xerces** développée par Apache, adossée sur l'API **Xalan** développée aussi par Apache
    - disponible dans le JDK jusqu'à la version 10
    - assez anciennes et assez lourdes à utiliser
- API **DOM4J** opensource 
- API **JDOM**

----

## Structure d'un document XML (1)

- document textuel avec une unique balise racine
- langage de balise :
    - balise ouvrante et balise fermante : on parle d'**élément** XML. Dans l'exemple ci-dessous, *personne* est un élément, *age* est un autre élément
    - possibilité d'imbriquer des balises
    - possibilité d'ajouter des attributs dans une balise, il appartient à un élément
    - possibilité d'ajouter des commentaires
- on parle de **noeud** pour tout ce qu'on peut trouver dans un document XML : élément, attribut, balise, commentaire
- on parle de **branche** ce qui permet de mettre en relation 2 éléments XML entre-eux : *age* est fils de *personne*; *age* est également frère de *prenom*
- possibilité d'ajouter du texte brut qui ne sera pas parsé lorsqu'on a du texte qui contient des caractères qui ont un sens pour les analyseurs XML comme les signes `<` ou `>` avec **CDATA** pour *character data*, contrairement aux données parsées **PCDATA**

----

## Structure d'un document XML (2)

- définir un **espace de nom** dans un document XML permet de différencier les documents XML qui ont le même nom
    - attribut **xmlns** pour *XML namespace* sous la forme d'URI qui est un identificateur pour l'espace de nom de l'élément défini, mais ne correspond pas à une page web
    - 2 façons d'attacher un élément XML à un espace de nom : méthode d'association par défaut, ou de manière explicite en ajoutant un préfixe à l'attribut *xmlns* : **xmlns:i** et ajout de ce préfixe sur les balises, par exemple `<i:age></i:age>`

```xml
<personne id="15" xmlns="http://...">
    <adresse>
        <codePostal>92120</codePostal>
        <nomCommune>Montrouge</nomCommune>
    </adresse>
    <age>30</age>
    <prenom>Gaëtan</prenom>
    <!-- commentaire non analysé par les parseurs XML -->
    <texteLibre><![CDATA[droit<au>but]]></texteLibre>
</personne>
```

----

## Création d'un document XML avec DOM4J

```xml
<dependency>
    <groupId>org.dom4j</groupId>
    <artifactId>dom4j</artifactId>
    <version>2.1.3</version>
</dependency>
```

Exemple de création d'un document :

```java
// création d'un document XML en mémoire
Document document = DocumentHelper.createDocument();
// création d'un élément racine
Element root = document.addElement("user");
// ajout d'un attribut à cet élément
root.addAttribute("id", "2");
// ajout d'élément à l'élément racine
root.addElement("name").addText("Gaëtan");
root.addElement("age").addText("32");

System.out.println(document.asXML());
// <?xml version="1.0" encoding="UTF-8"?>
// <user id="2"><name>Gaëtan</name><age>32</age></user>
```

----

## Ecriture du document dans un fichier

- écriture dans un fichier via un **FileOutputStream**
- définition d'un **OutputFormat** pour définir comment le document va être écrit
    - possibilité de tout mettre sur une seule ligne avec la méthode **createCompactFormat()**, ou sauter des lines après chaque élément avec la méthode **createPrettyPrint()**
    - possibilité de définir des options comme l'encodage, l'indentation...
- création d'un **XMLWriter** et utilisation de la méthode *write(document)* pour écrire le fichier, puis fermeture du *XMLWriter*

```java
OutputStream outputStream = new FileOutputStream(new File("test.xml"));
OutputFormat outputFormat = OutputFormat.createPrettyPrint();
XMLWriter xmlWriter = new XMLWriter(outputStream, outputFormat);
xmlWriter.write(document);
xmlWriter.flush();
xmlWriter.close();
```

```xml
<?xml version="1.0" encoding="UTF-8"?>

<user id="2">
  <name>Gaëtan</name>
  <age>32</age>
</user>
```

----

## Modèle DOM et Modèle SAX

- l'exemple que nous venons de voir crée un **modèle objet** en mémoire qui est l'équivalent du document XML en lisant l'intégralité du fichier XML et en le montant en mémoire : on parle d'approche **DOM** pour **Document Object Model**
    - fonctionne pour les petits documents XML
    - trop coûteux en CPU et en mémoire les gros documents
- l'approche **SAX** permet de traiter le document XML morceau par morceau, ce qui permet de ne pas monter tout le fichier en mémoire
    - **SAX** fonctionne sur un **modèle d'événements**
    - création d'un analyseur SAX qui va regarder le document élément par élément, et générer des événements, par exemple **start document**, **start element**, apparition d'un élément texte, apparation d'une erreur...
    - captation de ces événéments sous forme de callback, et lorsque l'événement est émis par SAX, notre code est appelé
    - possibilité de capter uniquement certains éléments, par exemple un élément particulier

----

## Création d'un analyseur SAX avec l'API Xercès

```java
SAXParserFactory sp = SAXParserFactory.newInstance();
// les SAXParser doivent prendre en compte les espaces de noms définis dans les
// document XML
sp.setNamespaceAware(true);
// indique aux SAXParser qu'ils doivent vérifier que la validité du document XML
sp.setValidating(true);
SAXParser parser = sp.newSAXParser();
// un handler permet de définir comment on souhaite analyser le document XML
// par défaut, ils ne vont rien faire, il faut leur dire quoi faire en redéfinissant des méthodes
DefaultHandler dh = new DefaultHandler() {
    // exemple en écrivant dans la console au début du document
    public void startDocument() {
        System.out.println("Début du document");
    }

    @Override
    // méthode qui détecte l'ouverture des éléments XML
    public void startElement(String uri, String localName, String qName, Attributes attributes) {}
};
// utilisation de la méthode parse qui parse le document XML en paramètre
// avec l'handler en paramètre
parser.parse(new File("test.xml"), dh);

// écriture dans la console : Début du document
```

----

## Création d'un handler qui compte le nombre d'éléments

```java
// Création d'un handler perso qui étend DefaultHandler
public class CountingHandler extends DefaultHandler {
	private int countNbElements, countC, countCInB;
	private boolean inB = false;
	public int getCountNbElements() { return countNbElements; }
	public int getCountC() { return countC; }
	public int getCountCInB() { return countCInB; }
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		countNbElements++;
		if (localName.equals("c")) { countC++; }
		if (localName.equals("b")) { inB = true; }
		if (localName.equals("c") && inB) { countCInB++; }
	}
	@Override
	public void endElement(String uri, String localName, String qName) {
		if (localName.equals("b")) { inB = false; }
	}
}
```

----

## Utilisation de cet handler personnalisé

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<a>
  <b id="2">
    <c>Toto</c>
    <c>Tata</c>
    <d>100</d>
  </b>
  <c>Titi</c>
</a>
```

```java
SAXParserFactory sp = SAXParserFactory.newInstance();
sp.setNamespaceAware(true);
sp.setValidating(true);
SAXParser parser = sp.newSAXParser();
CountingHandler ch = new CountingHandler();
parser.parse(new File("test.xml"), ch);
System.out.println(ch.getCountNbElements()); // 6
System.out.println(ch.getCountC()); // 3
System.out.println(ch.getCountCInB()); // 2
```

----

## Nécessité de contrats dans l'échange de documents XML : DTD et XML Schema

- lorsque des serveurs échangent des documents XML, ils ont besoin de contrats qui permettent de fixer la forme des documents XML, de les valider/invalider
- 2 langages (tous les 2 standards du W3C) permettent cela :
    - la **DTD** pour **Document Type Definition**, permet de fixer la forme que doit avoir le document XML : le nom de ses éléments et de ses sous-éléments
    - le format **XML Schema** permet de compléter les limitations du DTD

----

## Ecriture d'une DTD

- Ecriture de la DTD dans le document XML :

```xml
<?xml version="1.0" encoding="UTF-8"?>
<user id="2">
<!DOCTYPE user [
    <!ELEMENT user(name, age)>
    <!ELEMENT name(#CDATA)>
    <!ELEMENT age(#PCDATA)>
    <!ATTLIST user id #REQUIRED>
]>
  <name>Gaëtan</name>
  <age>32</age>
</user>
```

- Ecriture de la DTD dans un fichier texte et attachement du fichier au document XML :

```xml
<?xml version="1.0" encoding="UTF-8"?>
<user id="2">
<!DOCTYPE user PUBLIC "nom que l'on donne au DTD" "URL du fichier texte contenu la DTD">
<!-- <!DOCTYPE user SYSTEM "nom que l'on donne au DTD" "endroit sur le sytème de fichier ou est le fichier"> -->
  <name>Gaëtan</name>
  <age>32</age>
</user>
```

----

## Attacher un élément XML à un XML Schema

- fait après la DTD, apporte des avantages, de la précision mais aussi de la complexité (par exemple dire que tel attribut doit avoir une valeur entière)


```xml
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee 
        http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
version="3.1">
```

La déclaration du XML Schéma ce fait via l'attribut **xsi:schemaLocation**
- premier élément est un URI, représente le nom d'un espace de nom
- deuxième élément est une URL, qui pointe sur le XML schéma (au format xsd) auquel le document *web.xml* est attaché. Permet de valider la grammaire de l'élément *web-app*, équivalent au fichier DTD avec une syntaxe différente
