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
    - captation de ces événéments sous forme de callback (fonction passée en paramètre, qui va être appelée à une condition, le plus souvent “quand le traitement est terminé”), et lorsque l'événement est émis par SAX, notre code est appelé
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
    - le format **XML Schema** permet de compléter les limitations du DTD. Il a été fait après la DTD, apporte des avantages, de la précision (par exemple dire que tel attribut doit avoir une valeur entière) mais aussi de la complexité

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

Exemple d'attachement d'un XML Schéma sur le document XML **web.xml**, document qui permet de spécifier une application web Java EE, avec comme élément racine `<web-app>` :

```xml
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee 
        http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
version="3.1">
```

- l'attribut **xmlns** est l'espace de nom du document
- pour dire que cet espace de nom est attaché à un *XML Schema* particulier, on utilise l'attribut standard défini par le W3C **xmlns:xsi**, qui est un espace de nom standard que les parseurs XML connaissent
- la déclaration du XML Schéma ce fait via l'attribut **xsi:schemaLocation**, qui est un attribut standard attaché à l'espace de nom `XMLSchema-instance`. C'est une chaîne de caractères composé de 2 éléments :
    - premier élément est un URI, représente le nom d'un espace de nom
    - deuxième élément est une URL, qui pointe sur le XML schéma (au format xsd) auquel le document *web.xml* est attaché. Permet de valider la grammaire de l'élément *web-app*, équivalent au fichier DTD avec une syntaxe différente

----

## Ecriture d'un XML Schema

- c'est un document XML avec un élément racine, des sous-éléments...
- permet de spécifier le type : *xsd:string*, *xsd:integer*, *xsd:deciaml*, *xsd:boolean*, *xsd:date*, *xsd:time*
- beaucoup plus précis et plus complexe que la DTD

```xml
<!-- exemple de XML Schema pour l'exemple de fichier XML produit avant -->
<xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns="nom de l'espace de nom" targetNamespace="...">
    <xsd:element name="user">
        <xsd:complexType>
            <xsd:sequence>
                <xsd:element name="nom" type="xsd:string"/>
                <xsd:element name="age">
                    <xsd:simpleType>
                        <xsd:restriction base="xsd:integer">
                            <xsd:minInclusive value="0">
                            <xsd:maxInclusive value="150">
                        </xsd:restriction>
                    </xsd:simpleType>
                </xsd:element>
            </xsd:sequence>
            <xsd:attribute name="id" type="xsd:int"/>
        </xsd:complexType>
    </xsd:element>
</xsd:schema>
```

----

## Ecrire des requêtes sur des documents XML avec XPath

- XPath est un moteur de requête qui permet d'aller chercher des informations par requête dans un document XML
- il est normalisé par le W3C, en V1, V2 et V3
- une requête XPath est une chaîne de caractères qui suit une syntaxe
- supporté par les API Xerces et DOM4J

```java
Document document = ...;
String xPath = "...";

List<Node> nodes = document.selectNodes(xPath);
Node node = document.selectSingleNode(xPath);
String value = document.valueOf(xPath);
```

----

## Exemples de requêtes XPath

- une requête commence par un / (chemin absolu depuis a racine) ou // (chemin relatif)
- __/A__ le noeud XML qui est racine
- __/A/B__ les noeuds B enfants de A
- __//B__ l'intégralité des noeuds du document peut importe où ils se trouvent dans le document
- __//C/D__ les noeuds D enfants de C
- __/A/B/*__ tous les éléments enfants de B enfants de A
- __/A/B[2]__ le 2e élément B qui est enfant de A
- __//B/*[last()]__ le dernier enfant de B
- __//@id__ sélectionne l'attribut id
- __//B[@id]__ sélectionne les éléments B qui ont l'attribut id
-__//B[@id='15]__ sélectionne les éléments B qui ont l'attribut id qui vaut 15
- __/A/B/descendant::*__ sélectionne tous les descendants de B enfant de A, et pas seulement les enfants directs

----

## JAXP vs JAXB

- **JAXP** pour *Java API for XML Parsing*, est l'API Java permettant la création, la manipulation et le traitement de fichiers XML à bas niveau
    - les 2 principales API sont **SAX** et **DOM**
    - une troisième API est apparue avec Java 6 : **StAX** est l'acronyme de Streaming Api for XML : c'est une API qui permet de traiter un document XML de façon simple en consommant peu de mémoire tout en permettant de garder le contrôle sur les opérations d'analyse ou d'écriture
- **JAXB**, pour *Java Architecture for XML Binding*, est une spécification qui permet de faire correspondre un document XML à un ensemble de classes et vice versa au moyen d'opérations de sérialisation/désérialisation nommées marshalling/unmarshalling
