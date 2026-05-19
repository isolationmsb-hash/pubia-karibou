package com.karibou.pubia.presentation.navigation

/**
 * Routes de navigation typees — une route par ecran du parcours utilisateur.
 * Chaque ecran correspond a une etape du flux dans le brief (section 4).
 */
sealed class Route(val path: String) {
    data object Home : Route("home")             // Accueil — liste projets
    data object Avatar : Route("avatar")         // Etape 1 — photo personne
    data object Product : Route("product")       // Etape 2 — photo produit
    data object Script : Route("script")         // Etape 3 — script texte
    data object Options : Route("options")       // Etape 4 — duree, format, voix
    data object Generation : Route("generation") // Etape 5 — generation en cours
    data object Preview : Route("preview")       // Etape 6 — apercu video
    data object PublishFB : Route("publish_fb")  // Etape 7 — publication Facebook
}
