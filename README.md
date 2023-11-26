# À Table !

Android application to organise menus and shopping list.

![./assets/preview.png](./assets/preview.jpg)

## Features

### Menus' calendar

For each day of the next weeks, plan the meals and desserts you want to cook. They can come your cookbook or they can be created directly. Once meals are added, an option allows to add all their ingredients to the shopping list. 

### Cookbook

Exploration section of all your recipes. See the details of your recipes or edit them.

The ingredients of the recipes use generic products shared among all the recipes. It allows the app to easily create a clean and simple shopping list.

### Shopping list

Users can add their menus to the shopping list. Similar products are gathered together to avoid duplicates.

## Etapes de développement

- [X] Data
  - [X] Data structure
    - [X] Database
    - [X] Entity for recipes
    - [X] Entity for meals' calendar
    - [X] Entity for shopping list items
    - [X] Class for gathered shopping list items
    - [x] Entity for products
    - [ ] Entity for products inventory
  - [X] Data manipulation
    - [x] Calendar's recipes ingredients => shopping list
  - [X] Usage of LiveData for database objects
- [X] Interface
  - [X] Calendar
  - [X] Cookbook
  - [X] Shopping list
  - [X] Products
  - [ ] Inventory
- [X] Other features
  - [X] Export recipes
  - [X] Import recipes
- [X] Langages
  - [X] English
  - [X] French

### Products

To add ingredients in a recipe, user needs to choose or create the related product using the ad hoc interface. This feeds a product batabase shared among all the recipes. The advantage of this system is to merge similar products and units (i.g. 100g of flour in recipe A and 1kg of flour in recipe B become 1.1kg of flour in the shopping list)

### Inventaire (en projet)

L'inventaire repertorirait les ingrédients/produits achetés. Deux moyens sont envisagés pour founir cet inventaire :

* lors du check d'un ingrédient/produit dans la liste des courses : l'appli demanderait une date de péremption et/ou ajouterait le produit à l'inventaire.
* après chaque course, l'utilisateur serait chargé de scanner ses produits, lier les achats scannés à un produit archivé (si cela n'a pas encore été fait) et le tout serait ajouté à l'inventaire.

Par la suite, la page de l'inventaire offrirait les fonctionnalités suivantes :

* la mise à jour des produits de l'inventaire
* les produits qui perimeront très rapidement
* les recettes les plus adaptées pour les produits disponibles
