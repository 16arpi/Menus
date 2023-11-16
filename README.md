# À Table !

Application android pour organiser les menus et les courses.

![./assets/preview.png](./assets/preview.jpg)

## Fonctionnalités

### Agenda des menus

Pour chaque jour des prochaines semaines, indiquer les plats et désserts prévus. Ils peuvent provenir du livre des recettes, mais peuvent aussi être créés à la volée. Une fois les plats ajoutés, une option permet d'ajouter les ingrédients à la liste de courses.

### Livre de recettes

Section d'exploration des recettes ajoutées à l'application. L'exploration peut-être libre, filtrée par type de recette (entrée, plat, dessert etc.) et/ou à partir d'une recherche par mots clés.

Les ingrédients des recettes utilisent une banque de produits enrichie par l'utilisateur. Les produits utilisent des unités génériques préalablement établies dans l'application. Cela permet le regroupement d'ingrédients similaires lors de la génération de la liste de courses.

### Liste de courses

L'utilisateur peut ajouter ses menus à la liste des courses. Les produits similaires entre recettes sont regroupés pour éviter les doublons.

## Etapes de développement

- [X] Données
  - [X] Structures de données
    - [X] Base de données
    - [X] Entité pour les recettes
    - [X] Entité pour les plats de l'agenda
    - [X] Entité pour les éléments des courses
    - [X] Classe de la liste de courses
    - [x] Entité pour les produits alimentaires (et leur unité)
    - [ ] Entité pour l'inventaire des produits
  - [X] Manipulation des données
    - [x] Produits des courses => liste des courses
  - [X] Utilisation de LiveData pour les objets de la base de données
- [X] Interface
  - [X] Agenda
  - [X] Recettes
  - [X] Courses
  - [X] Produits
  - [ ] Inventaire
- [X] Fonctionnalités supplémentaires
  - [X] Exporter les recettes
  - [ ] Importer les recettes

### Produits

Pour ajouter un ingrédient dans une recette, l'utilisateur doit choisir ou créer le produit associé à l'aide de l'interface ad hoc. Cela enrichie une banque de produits commune à toutes les recettes. L'avantage de cette banque est de faciliter le regroupement d'ingrédients quand ils passent dans la liste des courses (ex: 100g de farine dans la recette A et 50g de farine dans la recette B donne 150g de farine dans la liste des courses). 

### Inventaire EN PROJET

L'inventaire repertorirait les ingrédients/produits achetés. Deux moyens sont envisagés pour founir cet inventaire :

* lors du check d'un ingrédient/produit dans la liste des courses : l'appli demanderait une date de péremption et/ou ajouterait le produit à l'inventaire.
* après chaque course, l'utilisateur serait chargé de scanner ses produits, lier les achats scannés à un produit archivé (si cela n'a pas encore été fait) et le tout serait ajouté à l'inventaire.

Par la suite, la page de l'inventaire offrirait les fonctionnalités suivantes :

* la mise à jour des produits de l'inventaire
* les produits qui perimeront très rapidement
* les recettes les plus adaptées pour les produits disponibles
