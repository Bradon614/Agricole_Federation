-- 1. Création des types ENUM
DO $$ BEGIN
CREATE TYPE gender_enum AS ENUM ('M','F');
EXCEPTION WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
CREATE TYPE member_status_enum AS ENUM ('active','inactive');
EXCEPTION WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
CREATE TYPE position_label_enum AS ENUM ('JUNIOR', 'SENIOR', 'SECRETARY', 'TREASURER', 'VICE_PRESIDENT', 'PRESIDENT');
EXCEPTION WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
CREATE TYPE frequency_enum AS ENUM ('WEEKLY', 'MONTHLY', 'ANNUALLY', 'PUNCTUALLY');
EXCEPTION WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
CREATE TYPE fee_status_enum AS ENUM ('ACTIVE', 'INACTIVE');
EXCEPTION WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
CREATE TYPE account_type_enum AS ENUM ('Cash', 'MobileMoney', 'Bank');
EXCEPTION WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
CREATE TYPE payment_mode_enum AS ENUM ('CASH', 'MOBILE_MONEY', 'BANK_TRANSFER');
EXCEPTION WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
CREATE TYPE mobile_operator_enum AS ENUM ('AIRTEL_MONEY', 'MVOLA', 'ORANGE_MONEY');
EXCEPTION WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
CREATE TYPE contribution_type_enum AS ENUM ('OneTime', 'Periodic');
EXCEPTION WHEN duplicate_object THEN null;
END $$;

-- 2. Table fédération
CREATE TABLE IF NOT EXISTS federation (
                                          id_federation VARCHAR(20) PRIMARY KEY DEFAULT 'fed-1',
    name VARCHAR(100) NOT NULL
    );
INSERT INTO federation (id_federation, name) VALUES ('fed-1', 'Fédération Agricole') ON CONFLICT DO NOTHING;

-- 3. Table collective
CREATE TABLE IF NOT EXISTS collective (
                                          id_collective VARCHAR(20) PRIMARY KEY,
    unique_number VARCHAR(50),
    unique_name VARCHAR(100),
    city VARCHAR(100),
    agricultural_specialty VARCHAR(100) DEFAULT 'Default specialty',
    creation_date DATE NOT NULL,
    opening_authorization_date DATE,
    id_federation VARCHAR(20) REFERENCES federation(id_federation)
    );

-- 4. Table member
CREATE TABLE IF NOT EXISTS member (
                                      id_member VARCHAR(20) PRIMARY KEY,
    last_name VARCHAR(50),
    first_names VARCHAR(50),
    birth_date DATE,
    gender gender_enum,
    address TEXT,
    profession VARCHAR(100),
    phone BIGINT,
    email VARCHAR(100),
    adhesion_date DATE,
    status member_status_enum DEFAULT 'active',
    current_collective_id VARCHAR(20) REFERENCES collective(id_collective)
    );

-- 5. Historique adhésion
CREATE TABLE IF NOT EXISTS adhesion_history (
                                                id_member VARCHAR(20) REFERENCES member(id_member),
    id_collective VARCHAR(20) REFERENCES collective(id_collective),
    adhesion_date DATE,
    PRIMARY KEY (id_member, id_collective, adhesion_date)
    );

-- 6. Position
CREATE TABLE IF NOT EXISTS position (
                                        id_position VARCHAR(10) PRIMARY KEY,
    label position_label_enum NOT NULL UNIQUE
    );
INSERT INTO position (id_position, label) VALUES
                                              ('pos-1', 'JUNIOR'),
                                              ('pos-2', 'SENIOR'),
                                              ('pos-3', 'SECRETARY'),
                                              ('pos-4', 'TREASURER'),
                                              ('pos-5', 'VICE_PRESIDENT'),
                                              ('pos-6', 'PRESIDENT')
    ON CONFLICT DO NOTHING;

-- 7. Mandat
CREATE TABLE IF NOT EXISTS collective_mandate (
                                                  id_mandate VARCHAR(20) PRIMARY KEY,
    id_collective VARCHAR(20) REFERENCES collective(id_collective),
    year INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL
    );

-- 8. Occupation de poste
CREATE TABLE IF NOT EXISTS collective_position_occupation (
                                                              id_mandate VARCHAR(20) REFERENCES collective_mandate(id_mandate),
    id_position VARCHAR(10) REFERENCES position(id_position),
    id_member VARCHAR(20) REFERENCES member(id_member),
    PRIMARY KEY (id_mandate, id_position)
    );

-- 9. Cotisation
CREATE TABLE IF NOT EXISTS membership_fee (
                                              id_membership_fee VARCHAR(30) PRIMARY KEY,
    id_collective VARCHAR(20) REFERENCES collective(id_collective),
    eligible_from DATE NOT NULL,
    frequency frequency_enum NOT NULL,
    amount DOUBLE PRECISION NOT NULL,
    label VARCHAR(100),
    status fee_status_enum DEFAULT 'ACTIVE'
    );

-- 10. Compte financier
CREATE TABLE IF NOT EXISTS account (
                                       id_account VARCHAR(30) PRIMARY KEY,
    id_collective VARCHAR(20) REFERENCES collective(id_collective),
    account_type account_type_enum NOT NULL,
    holder_name VARCHAR(100),
    balance DOUBLE PRECISION DEFAULT 0
    );

-- 11. Compte mobile money
CREATE TABLE IF NOT EXISTS mobile_money_account (
                                                    id_account VARCHAR(30) PRIMARY KEY REFERENCES account(id_account),
    phone_number VARCHAR(15),
    operator mobile_operator_enum
    );

-- 12. Transaction
CREATE TABLE IF NOT EXISTS transaction (
                                           id_transaction VARCHAR(30) PRIMARY KEY,
    id_collective VARCHAR(20) REFERENCES collective(id_collective),
    id_account_credited VARCHAR(30) REFERENCES account(id_account),
    id_member_debited VARCHAR(20) REFERENCES member(id_member),
    amount DOUBLE PRECISION NOT NULL,
    payment_mode payment_mode_enum NOT NULL,
    creation_date DATE NOT NULL
    );

-- 13. Paiement membre
CREATE TABLE IF NOT EXISTS member_payment (
                                              id_payment VARCHAR(30) PRIMARY KEY,
    id_member VARCHAR(20) REFERENCES member(id_member),
    amount INT NOT NULL,
    id_membership_fee VARCHAR(30) REFERENCES membership_fee(id_membership_fee),
    id_account_credited VARCHAR(30) REFERENCES account(id_account),
    payment_mode payment_mode_enum NOT NULL,
    creation_date DATE NOT NULL
    );

-- 14. Parrainage
CREATE TABLE IF NOT EXISTS sponsorship (
                                           id_new_member VARCHAR(20) REFERENCES member(id_member),
    id_sponsor VARCHAR(20) REFERENCES member(id_member),
    sponsorship_date DATE,
    PRIMARY KEY (id_new_member, id_sponsor)
    );

-- 15. Contributions
CREATE TABLE IF NOT EXISTS contribution (
                                            id_contribution SERIAL PRIMARY KEY,
                                            id_member VARCHAR(20) REFERENCES member(id_member),
    id_collective VARCHAR(20) REFERENCES collective(id_collective),
    contribution_type contribution_type_enum,
    amount DOUBLE PRECISION,
    payment_date DATE,
    payment_method VARCHAR(50),
    period VARCHAR(50)
    );