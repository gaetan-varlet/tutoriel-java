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