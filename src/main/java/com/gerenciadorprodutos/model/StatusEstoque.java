package com.gerenciadorprodutos.model;

/**
 * Antes o status era escolhido manualmente em um combo box, o que permitia
 * inconsistencias (ex.: marcar "Estoque Baixo" com 500 unidades). Aqui o status
 * deixa de ser uma escolha do usuario e passa a ser calculado a partir da
 * quantidade real em estoque, sempre atualizado e sempre coerente.
 */
public enum StatusEstoque {

    ESGOTADO("Esgotado", "tag-esgotado"),
    BAIXO("Estoque Baixo", "tag-baixo"),
    NORMAL("Estoque Normal", "tag-normal");

    private static final int LIMITE_ESTOQUE_BAIXO = 10;

    private final String rotulo;
    private final String estiloCss;

    StatusEstoque(String rotulo, String estiloCss) {
        this.rotulo = rotulo;
        this.estiloCss = estiloCss;
    }

    public static StatusEstoque calcular(int quantidade) {
        if (quantidade <= 0) {
            return ESGOTADO;
        }
        if (quantidade < LIMITE_ESTOQUE_BAIXO) {
            return BAIXO;
        }
        return NORMAL;
    }

    public String getRotulo() {
        return rotulo;
    }

    /** Nome da classe CSS usada para colorir a "tag" de status na tabela. */
    public String getEstiloCss() {
        return estiloCss;
    }
}
