CREATE TABLE IF NOT EXISTS produtos (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    nome          TEXT NOT NULL,
    quantidade    INTEGER NOT NULL DEFAULT 0,
    preco         REAL NOT NULL DEFAULT 0,
    categoria     TEXT,
    data_cadastro TEXT NOT NULL DEFAULT (datetime('now', 'localtime'))
);

CREATE INDEX IF NOT EXISTS idx_produtos_nome ON produtos (nome);
