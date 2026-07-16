-- Script de criação da tabela usada pela aplicação. É executado automaticamente
-- pelo DatabaseManager sempre que a aplicação inicia; o "IF NOT EXISTS" garante
-- que ele não recrie (nem apague) a tabela se ela já existir.
CREATE TABLE IF NOT EXISTS produtos (
    id            INTEGER PRIMARY KEY AUTOINCREMENT, -- gerado automaticamente pelo SQLite a cada novo produto
    nome          TEXT NOT NULL,
    quantidade    INTEGER NOT NULL DEFAULT 0,
    preco         REAL NOT NULL DEFAULT 0,
    categoria     TEXT,
    data_cadastro TEXT NOT NULL DEFAULT (datetime('now', 'localtime')) -- preenchida sozinha na hora do INSERT
);

-- Acelera as buscas por nome (usadas pela busca da tela e por ORDER BY nome).
CREATE INDEX IF NOT EXISTS idx_produtos_nome ON produtos (nome);
