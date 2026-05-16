CREATE TABLE IF NOT EXISTS users
(
    user_id       UUID PRIMARY KEY NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    login         VARCHAR(50)     NOT NULL UNIQUE,
    phone         VARCHAR(15)      NOT NULL UNIQUE CHECK (phone ~ '^[0-9]{12}$'),
    role          VARCHAR(20)                      DEFAULT 'USER' CHECK (role IN ('USER', 'ADMIN')),
    email         VARCHAR(100)     NOT NULL UNIQUE,
    password_hash VARCHAR(250)     NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE         DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT check_email_valid CHECK ( email ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$' )
);

CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_login ON users (login);

CREATE TABLE IF NOT EXISTS refresh_tokens (
    refresh_token_id UUID NOT NULL UNIQUE,
    user_id UUID NOT NULL,
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT pk_refresh_tokens PRIMARY KEY (refresh_token_id),

    CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
    );

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_id
    ON refresh_tokens(user_id);

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_token_hash
    ON refresh_tokens(token_hash);


CREATE TABLE IF NOT EXISTS otp_config (
    config_id       INTEGER PRIMARY KEY CHECK (config_id = 1),
    code_length     INTEGER NOT NULL DEFAULT 6 CHECK (code_length BETWEEN 4 AND 10),
    lifetime_minutes INTEGER NOT NULL DEFAULT 5 CHECK (lifetime_minutes > 0),
    updated_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by      UUID REFERENCES users(user_id) ON DELETE SET NULL
);

INSERT INTO otp_config (config_id, code_length, lifetime_minutes)
VALUES (1, 6, 5)
    ON CONFLICT (config_id) DO NOTHING;