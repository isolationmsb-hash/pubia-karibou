package com.karibou.pubia.util

/**
 * Constantes partagees — URLs, cles de config, etc.
 *
 * IMPORTANT : aucune cle API ne doit jamais figurer ici. Toutes les cles
 * (HeyGen, ElevenLabs, Replicate, Anthropic, Meta) sont detenues par le
 * backend uniquement. L'app ne connait que l'URL du backend.
 */
object Constants {

    // URL du backend — a remplacer par l'URL Railway en production
    const val BACKEND_BASE_URL_DEBUG = "http://10.0.2.2:3000/"  // emulateur -> host
    const val BACKEND_BASE_URL_RELEASE = "https://api.pubia-karibou.tld/" // a configurer

    // Timeouts reseau (en secondes) — generations video prennent jusqu'a 5 min
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 300L
    const val WRITE_TIMEOUT = 60L

    // Compression image avant upload (brief section 7)
    const val IMAGE_MAX_DIMENSION = 1920
    const val IMAGE_JPEG_QUALITY = 85

    // Polling generation
    const val GENERATION_POLL_INTERVAL_MS = 5_000L
    const val GENERATION_TIMEOUT_MS = 10 * 60 * 1000L // 10 min
}
