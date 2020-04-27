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

## Structure d'un document XML

- document textuel avec une unique balise racine
- langage de balise :
    - balise ouvrante et balise fermante : on parle d'**élément** XML. Dans l'exemple ci-dessous, *personne* est un élément, *age* est un autre élément
    - possibilité d'imbriquer des balises
    - possibilité d'ajouter des attributs dans une balise, il appartient à un élément
    - possibilité d'ajouter des commentaires
- on parle de **noeud** pour tout ce qu'on peut trouver dans un document XML : élément, attribut, balise, commentaire
- on parle de **branche** ce qui permet de mettre en relation 2 éléments XML entre-eux : *age* est fils de *personne*; *age* est également frère de *prenom*
- possibilité d'ajouter du texte brut qui ne sera pas parsé lorsqu'on a du texte qui contient des caractères qui ont un sens pour les analyseurs XML comme les signes `<` ou `>` avec **CDATA** pour *character data*
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