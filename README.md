# API REST Contract-First - Guides touristiques

Master Réseaux et Système - UASZ - Services Web (évaluation pratique).
Implémentation du contrat officiel `guide-touristique.yml` (fourni, **non modifié**, à la racine du dépôt).

## Lancement

```bash
mvn clean verify          # compile + tests
mvn spring-boot:run       # démarre sur http://localhost:8080
```

Java 17+, Maven 3.9+. Base H2 en mémoire (recréée à chaque démarrage).
Sous Eclipse : activer l'annotation processing Maven (m2e-apt) pour MapStruct.

## Authentification (jetons simulés)

```bash
curl -X POST localhost:8080/auth/token -H 'Content-Type: application/json' \
     -d '{"username":"awa","role":"GUIDE"}'
```

Rôles : `GUIDE`, `GESTIONNAIRE`, `EVALUATEUR`, `ADMIN`. Jeton JWT (HS256) à passer en
`Authorization: Bearer <jeton>`. Le `subject` du jeton identifie le propriétaire d'un profil.

## Matrice d'accès

| Opération | GUIDE | GESTIONNAIRE | EVALUATEUR | ADMIN |
|---|---|---|---|---|
| POST /guides | oui | oui | non | oui |
| GET /guides (recherche) | non | oui | oui | oui |
| GET /guides/{id}, /cv, /cv/export, /candidatures | propriétaire | oui | oui | oui |
| PUT, DELETE /guides/{id} | propriétaire | oui | non | oui |
| PUT /guides/{id}/cv, POST /guides/{id}/candidatures | propriétaire | non | non | oui |
| GET /candidatures/{id} | propriétaire | oui | oui | oui |
| GET /sessions-evaluation | oui | oui | oui | oui |
| POST /sessions-evaluation | non | non | non | oui |
| POST /candidatures/{id}/convocation | non | oui | non | oui |
| POST /candidatures/{id}/resultats | non | non | oui | non |
| GET /candidatures/{id}/resultats | propriétaire, si PUBLIE | non | oui | oui |
| POST /candidatures/{id}/resultats/publication | non | non | oui | oui |

## Règles métier (section 3 du sujet)

- Guide : BROUILLON -> DOSSIER_SOUMIS (soumission) -> EN_EVALUATION (convocation) -> ADMIS / NON_ADMIS.
- Candidature : SOUMISE (recevabilité acquise) -> CONVOQUEE -> EVALUEE -> ADMISE / NON_ADMISE.
  Décision AJOURNE : candidature EVALUEE, guide reste EN_EVALUATION.
- `noteTotale = (somme note / somme noteMaximale) x 20`, arrondie à 2 décimales (HALF_UP).
- Seuil d'admission : `app.resultat.seuil-admission` (application.yml, 10 par défaut), jamais codé en dur
  (test `SeuilConfigurableTest`). ADMIS sous le seuil ou note > noteMaximale : 400.
- Session : candidature acceptée seulement si session OUVERTE, non complète, et sans autre candidature du
  guide sur cette session (409).
- CV : version +1 à chaque `PUT`, 409 si dossier soumis, 400 sans langue. À la soumission, une **copie figée**
  du CV (JSON) est stockée dans la candidature (`versionCV` + `cvFige`).
- Résultat créé en BROUILLON, visible du candidat uniquement après publication.

## Hypothèses et choix de conception

- « Dossier soumis » = guide dont le statut est différent de `BROUILLON`.
- Le contrat ne définit pas de moyen d'ouvrir une session : **une session créée est OUVERTE**.
- `GET /guides` renvoie un **tableau simple** (contrat) ; la pagination passe par `page` / `taille`, et le total est
  fourni dans l'en-tête `X-Total-Count`.
- Schéma `Erreur` exactement `{code, message, details}` (details : chaîne ou null), codes `REQUETE_INVALIDE`,
  `RESSOURCE_INTROUVABLE`, `CONFLIT`, `NON_AUTORISE`, `ACCES_REFUSE`. Les erreurs 500 inattendues ne sont pas
  interceptées (aucun code du contrat ne leur correspond).
- `additionalProperties: false` du contrat : les champs inconnus des corps de saisie correspondants sont refusés (400).
- Profil complet pour candidater : téléphone renseigné ; CV existant avec au moins une langue (sinon 400).
- Email de guide unique (409 à la création ; aussi en modification si pris par un autre guide).
- Une session identique (intitulé + région + date de début) ne peut être créée deux fois (409).
- **Publication du résultat** : le sujet la demande (« opération ajoutée ») mais le contrat n'en définit pas ; elle est
  ajoutée hors contrat en `POST /api/v1/candidatures/{id}/resultats/publication`.
- La copie figée du CV n'est pas exposée par le contrat (`Candidature` n'a que `versionCV`) : le test de preuve
  lit la copie directement en base (`TestSupport.lireCvFige`).
- Pas d'entité JPA exposée : DTO (records) + MapStruct. Contrôle de propriété centralisé dans `GuideAccessPolicy`.
- PDF du CV généré avec OpenPDF.

## Tests

`GuideApiTest` (Parties 1 et 4), `CandidatureApiTest` (Partie 2 et sessions/convocations), `ResultatApiTest`
(Partie 3), `SeuilConfigurableTest`. Test de preuve du CV figé :
`uneModificationUlterieureDuCvNAltereJamaisLeCvRattacheALaCandidature`.

## Avancement

- [x] Partie 1 : Guides et CV
- [x] Partie 2 : Candidatures
- [x] Partie 3 : Sessions, convocations, résultats
- [x] Partie 4 : erreurs normalisées, Spring Security JWT, MapStruct
