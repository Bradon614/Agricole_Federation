-- =============================================
-- MLD COMPLET - Fédération de Collectivités Agricoles
-- Base : PostgreSQL (compatible MySQL avec adaptations mineures)
-- Auteur : Grok (basé sur le sujet PROG3 - Exercice 2)
-- Date : 20 avril 2026
-- =============================================

-- =============================================
-- 1. TYPES ENUM (contraintes métier fortes)
-- =============================================
CREATE TYPE genre_enum AS ENUM ('M', 'F');
CREATE TYPE poste_libelle_enum AS ENUM ('Président', 'Président Adjoint', 'Trésorier', 'Secrétaire', 'Membre Confirmé', 'Membre Junior');
CREATE TYPE compte_type_enum AS ENUM ('Caisse', 'Bancaire', 'MobileMoney');
CREATE TYPE mode_paiement_enum AS ENUM ('Espèce', 'Virement Bancaire', 'Mobile Money');
CREATE TYPE cotisation_type_enum AS ENUM ('Periodique', 'Ponctuelle');
CREATE TYPE activite_type_enum AS ENUM (
    'Assemblee_Generale_Mensuelle',
    'Formation_Obligatoire_Juniors',
    'Activite_Exceptionnelle',
    'Assemblee_Generale_Annuelle_Federation',
    'Autre'
);
CREATE TYPE statut_membre_enum AS ENUM ('actif', 'demissionne');

-- =============================================
-- 2. TABLES PRINCIPALES + CLÉS + CONTRAINTES
-- =============================================

-- =============================================
-- FÉDÉRATION (une seule entité)
-- =============================================
CREATE TABLE federation (
                            id_federation          SERIAL PRIMARY KEY,
                            nom                    VARCHAR(100) NOT NULL DEFAULT 'Fédération Nationale des Collectivités Agricoles',
                            date_creation          DATE NOT NULL,
                            siege                  VARCHAR(100) NOT NULL,
                            CONSTRAINT uk_federation_nom UNIQUE (nom)
);

-- =============================================
-- COLLECTIVITÉ
-- =============================================
CREATE TABLE collectivite (
                              id_collectivite             SERIAL PRIMARY KEY,
                              numero_unique               VARCHAR(20) NOT NULL,
                              nom_unique                  VARCHAR(150) NOT NULL,
                              ville                       VARCHAR(100) NOT NULL,
                              specialite_agricole         VARCHAR(100) NOT NULL,
                              date_creation               DATE NOT NULL,
                              date_autorisation_ouverture DATE NULL,                     -- NULL = pas encore autorisée (section A)
                              id_federation               INTEGER NOT NULL DEFAULT 1,
                              CONSTRAINT uk_collectivite_numero UNIQUE (numero_unique),
                              CONSTRAINT uk_collectivite_nom UNIQUE (nom_unique),
                              CONSTRAINT fk_collectivite_federation FOREIGN KEY (id_federation)
                                  REFERENCES federation(id_federation) ON DELETE RESTRICT,
                              CONSTRAINT chk_date_autorisation CHECK (
                                  date_autorisation_ouverture IS NULL OR date_autorisation_ouverture >= date_creation
                                  )
);

-- =============================================
-- POSTE (table de référence)
-- =============================================
CREATE TABLE poste (
                       id_poste   SERIAL PRIMARY KEY,
                       libelle    poste_libelle_enum NOT NULL UNIQUE
);

-- =============================================
-- MEMBRE
-- =============================================
CREATE TABLE membre (
                        id_membre           SERIAL PRIMARY KEY,
                        nom                 VARCHAR(80) NOT NULL,
                        prenoms             VARCHAR(150) NOT NULL,
                        date_naissance      DATE NOT NULL,
                        genre               genre_enum NOT NULL,
                        adresse             TEXT NOT NULL,
                        metier              VARCHAR(100) NOT NULL,
                        telephone           VARCHAR(20) NOT NULL,
                        email               VARCHAR(150) NOT NULL,
                        date_adhesion       DATE NOT NULL,
                        statut              statut_membre_enum NOT NULL DEFAULT 'actif',
                        id_collectivite_actuelle INTEGER NULL,                    -- NULL si démissionné ou en transition
                        CONSTRAINT uk_membre_telephone UNIQUE (telephone),
                        CONSTRAINT uk_membre_email UNIQUE (email),
                        CONSTRAINT fk_membre_collectivite FOREIGN KEY (id_collectivite_actuelle)
                            REFERENCES collectivite(id_collectivite) ON DELETE SET NULL,
                        CONSTRAINT chk_date_naissance CHECK (date_naissance < CURRENT_DATE),
                        CONSTRAINT chk_date_adhesion CHECK (date_adhesion >= date_naissance + INTERVAL '16 years')
    );

-- =============================================
-- HISTORIQUE D'ADHÉSION (gère changement de collectivité + démission)
-- =============================================
CREATE TABLE adhesion_historique (
                                     id_adhesion          SERIAL PRIMARY KEY,
                                     id_membre            INTEGER NOT NULL,
                                     id_collectivite      INTEGER NOT NULL,
                                     date_adhesion        DATE NOT NULL,
                                     date_depart          DATE NULL,
                                     CONSTRAINT fk_adhesion_membre FOREIGN KEY (id_membre)
                                         REFERENCES membre(id_membre) ON DELETE CASCADE,
                                     CONSTRAINT fk_adhesion_collectivite FOREIGN KEY (id_collectivite)
                                         REFERENCES collectivite(id_collectivite) ON DELETE RESTRICT,
                                     CONSTRAINT chk_date_depart CHECK (date_depart IS NULL OR date_depart > date_adhesion)
);

-- =============================================
-- MANDAT COLLECTIVITÉ (1 an)
-- =============================================
CREATE TABLE mandat_collectivite (
                                     id_mandat_coll   SERIAL PRIMARY KEY,
                                     id_collectivite  INTEGER NOT NULL,
                                     annee            INTEGER NOT NULL,
                                     date_debut       DATE NOT NULL,
                                     date_fin         DATE NOT NULL,
                                     CONSTRAINT fk_mandat_coll_collectivite FOREIGN KEY (id_collectivite)
                                         REFERENCES collectivite(id_collectivite) ON DELETE CASCADE,
                                     CONSTRAINT uk_mandat_coll UNIQUE (id_collectivite, annee),
                                     CONSTRAINT chk_mandat_duree CHECK (date_fin = date_debut + INTERVAL '1 year')
    );

-- =============================================
-- OCCUPATION POSTE COLLECTIVITÉ (relation avec contrainte forte)
-- =============================================
CREATE TABLE occupation_poste_collectivite (
                                               id_occupation       SERIAL PRIMARY KEY,
                                               id_mandat_coll      INTEGER NOT NULL,
                                               id_poste            INTEGER NOT NULL,
                                               id_membre           INTEGER NOT NULL,
                                               CONSTRAINT fk_opc_mandat FOREIGN KEY (id_mandat_coll)
                                                   REFERENCES mandat_collectivite(id_mandat_coll) ON DELETE CASCADE,
                                               CONSTRAINT fk_opc_poste FOREIGN KEY (id_poste)
                                                   REFERENCES poste(id_poste) ON DELETE RESTRICT,
                                               CONSTRAINT fk_opc_membre FOREIGN KEY (id_membre)
                                                   REFERENCES membre(id_membre) ON DELETE RESTRICT,

    -- Un seul membre par poste spécifique par mandat
                                               CONSTRAINT uk_poste_mandat UNIQUE (id_mandat_coll, id_poste),

    -- Limite de 2 mandats maximum par membre par poste spécifique (gérée par trigger ou application)
                                               CONSTRAINT chk_poste_specifique CHECK (
                                                   (id_poste IN (SELECT id_poste FROM poste WHERE libelle IN ('Président','Président Adjoint','Trésorier','Secrétaire')))
                                                   )
);

-- =============================================
-- MANDAT FÉDÉRATION (2 ans)
-- =============================================
CREATE TABLE mandat_federation (
                                   id_mandat_fed   SERIAL PRIMARY KEY,
                                   id_federation   INTEGER NOT NULL DEFAULT 1,
                                   annee_debut     INTEGER NOT NULL,
                                   date_debut      DATE NOT NULL,
                                   date_fin        DATE NOT NULL,
                                   CONSTRAINT fk_mandat_fed_federation FOREIGN KEY (id_federation)
                                       REFERENCES federation(id_federation) ON DELETE RESTRICT,
                                   CONSTRAINT uk_mandat_fed UNIQUE (annee_debut),
                                   CONSTRAINT chk_mandat_fed_duree CHECK (date_fin = date_debut + INTERVAL '2 years')
    );

-- =============================================
-- OCCUPATION POSTE FÉDÉRATION
-- =============================================
CREATE TABLE occupation_poste_federation (
                                             id_occupation_fed   SERIAL PRIMARY KEY,
                                             id_mandat_fed       INTEGER NOT NULL,
                                             id_poste            INTEGER NOT NULL,
                                             id_membre           INTEGER NOT NULL,
                                             CONSTRAINT fk_opf_mandat FOREIGN KEY (id_mandat_fed)
                                                 REFERENCES mandat_federation(id_mandat_fed) ON DELETE CASCADE,
                                             CONSTRAINT fk_opf_poste FOREIGN KEY (id_poste)
                                                 REFERENCES poste(id_poste),
                                             CONSTRAINT fk_opf_membre FOREIGN KEY (id_membre)
                                                 REFERENCES membre(id_membre),
                                             CONSTRAINT uk_poste_mandat_fed UNIQUE (id_mandat_fed, id_poste)
);

-- =============================================
-- COMPTE (générique)
-- =============================================
CREATE TABLE compte (
                        id_compte           SERIAL PRIMARY KEY,
                        type_compte         compte_type_enum NOT NULL,
                        nom_titulaire       VARCHAR(200) NOT NULL,
                        solde               NUMERIC(15,2) NOT NULL DEFAULT 0,
                        devise              VARCHAR(3) NOT NULL DEFAULT 'MGA',
                        date_solde          DATE NOT NULL DEFAULT CURRENT_DATE,
                        id_collectivite     INTEGER NULL,
                        id_federation       INTEGER NULL,
                        CONSTRAINT fk_compte_collectivite FOREIGN KEY (id_collectivite)
                            REFERENCES collectivite(id_collectivite) ON DELETE CASCADE,
                        CONSTRAINT fk_compte_federation FOREIGN KEY (id_federation)
                            REFERENCES federation(id_federation) ON DELETE RESTRICT,
                        CONSTRAINT chk_un_seul_type CHECK (
                            (id_collectivite IS NOT NULL AND id_federation IS NULL) OR
                            (id_collectivite IS NULL AND id_federation IS NOT NULL)
                            ),
                        CONSTRAINT chk_solde_positif CHECK (solde >= 0)
);

-- Contrainte : UNE SEULE CAISSE par collectivité / fédération (index partiel PostgreSQL)
CREATE UNIQUE INDEX idx_unique_caisse ON compte(id_collectivite, type_compte)
    WHERE type_compte = 'Caisse';
CREATE UNIQUE INDEX idx_unique_caisse_fed ON compte(id_federation, type_compte)
    WHERE type_compte = 'Caisse';

-- =============================================
-- COMPTE BANCAIRE (spécialisation)
-- =============================================
CREATE TABLE compte_bancaire (
                                 id_compte           INTEGER PRIMARY KEY,
                                 numero_compte       VARCHAR(23) NOT NULL,
                                 nom_banque          VARCHAR(50) NOT NULL,   -- enum possible en CHECK
                                 CONSTRAINT fk_cb_compte FOREIGN KEY (id_compte)
                                     REFERENCES compte(id_compte) ON DELETE CASCADE,
                                 CONSTRAINT uk_numero_compte UNIQUE (numero_compte),
                                 CONSTRAINT chk_format_numero CHECK (numero_compte ~ '^[0-9]{23}$')   -- exactement 23 chiffres
    );

-- =============================================
-- COMPTE MOBILE MONEY (spécialisation)
-- =============================================
CREATE TABLE compte_mobile_money (
                                     id_compte           INTEGER PRIMARY KEY,
                                     numero_telephone    VARCHAR(20) NOT NULL,
                                     operateur           VARCHAR(50) NOT NULL,   -- Orange Money, Mvola, Airtel Money
                                     CONSTRAINT fk_cmm_compte FOREIGN KEY (id_compte)
                                         REFERENCES compte(id_compte) ON DELETE CASCADE,
                                     CONSTRAINT uk_numero_telephone UNIQUE (numero_telephone)
);

-- =============================================
-- COTISATION & PAIEMENTS
-- =============================================
CREATE TABLE cotisation (
                            id_cotisation       SERIAL PRIMARY KEY,
                            id_membre           INTEGER NOT NULL,
                            id_collectivite     INTEGER NOT NULL,
                            type_cotisation     cotisation_type_enum NOT NULL,
                            montant             NUMERIC(12,2) NOT NULL,
                            date_versement      DATE NOT NULL,
                            mode_paiement       mode_paiement_enum NOT NULL,
                            periode             VARCHAR(50) NULL,          -- mensuel / annuel / spécifique
                            id_compte           INTEGER NULL,              -- compte où l'argent a été versé
                            CONSTRAINT fk_cotisation_membre FOREIGN KEY (id_membre)
                                REFERENCES membre(id_membre) ON DELETE RESTRICT,
                            CONSTRAINT fk_cotisation_collectivite FOREIGN KEY (id_collectivite)
                                REFERENCES collectivite(id_collectivite) ON DELETE RESTRICT,
                            CONSTRAINT fk_cotisation_compte FOREIGN KEY (id_compte)
                                REFERENCES compte(id_compte) ON DELETE SET NULL,
                            CONSTRAINT chk_montant_positif CHECK (montant > 0),
                            CONSTRAINT chk_adhesion_50k CHECK (
                                (type_cotisation = 'Ponctuelle' AND montant >= 50000) OR type_cotisation = 'Periodique'
                                )
);

-- =============================================
-- ACTIVITÉ
-- =============================================
CREATE TABLE activite (
                          id_activite         SERIAL PRIMARY KEY,
                          id_collectivite     INTEGER NULL,          -- NULL = activité fédération
                          id_federation       INTEGER NULL DEFAULT 1,
                          type_activite       activite_type_enum NOT NULL,
                          date_activite       DATE NOT NULL,
                          obligatoire         BOOLEAN NOT NULL DEFAULT TRUE,
                          cible               VARCHAR(50) NOT NULL DEFAULT 'Tous',   -- Tous / Juniors / ...
                          CONSTRAINT fk_activite_collectivite FOREIGN KEY (id_collectivite)
                              REFERENCES collectivite(id_collectivite) ON DELETE CASCADE,
                          CONSTRAINT fk_activite_federation FOREIGN KEY (id_federation)
                              REFERENCES federation(id_federation) ON DELETE RESTRICT,
                          CONSTRAINT chk_activite_niveau CHECK (
                              (id_collectivite IS NOT NULL AND id_federation IS NULL) OR
                              (id_collectivite IS NULL AND id_federation IS NOT NULL)
                              )
);

-- =============================================
-- PRÉSENCE (assiduité)
-- =============================================
CREATE TABLE presence (
                          id_presence             SERIAL PRIMARY KEY,
                          id_activite             INTEGER NOT NULL,
                          id_membre               INTEGER NOT NULL,
                          est_present             BOOLEAN NOT NULL DEFAULT FALSE,
                          est_excuse              BOOLEAN NOT NULL DEFAULT FALSE,
                          motif_excuse            TEXT NULL,
                          appartient_a_sa_collectivite BOOLEAN NOT NULL DEFAULT TRUE,  -- false = visiteur d'une autre collectivité
                          CONSTRAINT fk_presence_activite FOREIGN KEY (id_activite)
                              REFERENCES activite(id_activite) ON DELETE CASCADE,
                          CONSTRAINT fk_presence_membre FOREIGN KEY (id_membre)
                              REFERENCES membre(id_membre) ON DELETE RESTRICT,
                          CONSTRAINT chk_presence_logique CHECK (
                              (est_present = TRUE AND est_excuse = FALSE) OR
                              (est_present = FALSE AND est_excuse = TRUE)
                              )
);

-- =============================================
-- PARRAINAGE (admission membre - section B)
-- =============================================
CREATE TABLE parrainage (
                            id_parrainage       SERIAL PRIMARY KEY,
                            id_nouveau_membre   INTEGER NOT NULL,
                            id_parrain          INTEGER NOT NULL,
                            date_parrainage     DATE NOT NULL DEFAULT CURRENT_DATE,
                            CONSTRAINT fk_parrainage_nouveau FOREIGN KEY (id_nouveau_membre)
                                REFERENCES membre(id_membre) ON DELETE CASCADE,
                            CONSTRAINT fk_parrainage_parrain FOREIGN KEY (id_parrain)
                                REFERENCES membre(id_membre) ON DELETE RESTRICT,
                            CONSTRAINT uk_parrainage_nouveau UNIQUE (id_nouveau_membre)
);

-- =============================================
-- VIEWS UTILES POUR STATISTIQUES (section G)
-- =============================================
-- Taux d'assiduité (exemple)
CREATE OR REPLACE VIEW vue_taux_assiduite AS
SELECT
    m.id_membre,
    m.nom || ' ' || m.prenoms AS membre,
    c.nom_unique AS collectivite,
    COUNT(CASE WHEN p.est_present = TRUE THEN 1 END) * 100.0 /
    NULLIF(COUNT(*), 0) AS taux_assiduite_pourcent
FROM membre m
         JOIN presence p ON p.id_membre = m.id_membre
         JOIN activite a ON a.id_activite = p.id_activite
         JOIN collectivite c ON c.id_collectivite = m.id_collectivite_actuelle
WHERE a.obligatoire = TRUE
GROUP BY m.id_membre, m.nom, m.prenoms, c.nom_unique;

-- Montant impayé / encaissé par membre (à compléter selon besoin)

-- =============================================
-- TRIGGERS (exemples de contraintes métier complexes)
-- =============================================
-- Exemple : Vérifier qu'un parrain est Membre Confirmé + ancienneté > 90 jours
-- (à implémenter selon votre SGBD)

COMMENT ON TABLE collectivite IS 'Section A : Ouverture nouvelle collectivité';
COMMENT ON TABLE membre IS 'Section B : Admission membre + parrainage';
COMMENT ON TABLE cotisation IS 'Section C : Cotisations et paiements';
COMMENT ON TABLE compte IS 'Section D : Situation de trésorerie (1 caisse max)';
COMMENT ON TABLE activite IS 'Section E : Activités';
COMMENT ON TABLE presence IS 'Section F : Assiduité';
COMMENT ON TABLE occupation_poste_collectivite IS 'Limite 2 mandats max par poste spécifique';
