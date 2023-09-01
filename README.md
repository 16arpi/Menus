# À Table !

Application android pour organiser les menus et les courses.

## Fonctionnalités

### Agenda des menus

Pour chaque jour des prochaines semaines, indiquer les plats et désserts prévus. Ils peuvent provenir du livre des recettes, mais peuvent aussi être édités à la volée. Une fois les plats ajoutés, une option permet d'ajouter les ingrédients à la liste de courses. 

### Livre de recettes

Section d'exploration des recettes ajoutées à l'application. L'exploration peut-être libre, filtrée par type de recette (entrée, plat, dessert etc.) et/ou à partir d'une recherche par mots clés.

Les ingrédients des recettes utilisent des unités génériques préalablement établies dans l'application. Cela permet le regroupement d'ingrédients similaires lors de la génération de la liste de courses.

### Liste de courses

Générée à chaque ouverture de la liste.

## Etapes de développement

- [X] Données
  - [X] Structures de données
    - [X] Base de données
    - [X] Entité pour les recettes
    - [X] Entité pour les plats de l'agenda
    - [X] Entité pour les éléments des courses
    - [ ] Classe de la liste de courses
    - [ ] Entité pour les produits alimentaires (et leur unité)
    - [ ] Entité pour l'inventaire des produits
  - [ ] Manipulation des données
    - [ ] Produits des courses => liste des courses
- [X] Interface
  - [X] Agenda
  - [X] Recettes
  - [X] Courses
  - [ ] Inventaire
  - [ ] Produits
