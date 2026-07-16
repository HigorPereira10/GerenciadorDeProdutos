package com.gerenciadorprodutos.model;

/**
 * Representa a situação do estoque de um produto: Esgotado, Baixo ou Normal.
 *
 * Antes o status era escolhido manualmente em uma caixa de seleção (combo box), o que permitia inconsistências 
 * (por exemplo, marcar "Estoque Baixo" em um produto com 500 unidades). 
 * Aqui o status deixa de ser uma escolha do usuário e passa a ser calculado a partir da quantidade real em estoque,
 * sempre atualizado e sempre coerente com o número de itens.
 *
 * Cada valor do enum carrega, além do nome interno (ESGOTADO, BAIXO, NORMAL),
 * um rótulo para exibir na tela e o nome de uma classe CSS para colorir a "tag" de status na tabela.
 */
public enum StatusEstoque {

    ESGOTADO("Esgotado", "tag-esgotado"),
    BAIXO("Estoque Baixo", "tag-baixo"),
    NORMAL("Estoque Normal", "tag-normal");

    /** Abaixo de 10 unidades (e acima de zero) o estoque já é considerado baixo. */
    private static final int LIMITE_ESTOQUE_BAIXO = 10;

    private final String rotulo;
    private final String estiloCss;

    StatusEstoque(String rotulo, String estiloCss) {
        this.rotulo = rotulo;
        this.estiloCss = estiloCss;
    }

    /**
     * Calcula qual status corresponde a uma quantidade de itens em estoque:
     * 0 unidades = Esgotado; 
     * menos de 10 = Baixo;
     * qualquer outro valor = Normal.
     */
    public static StatusEstoque calcular(int quantidade) {
        if (quantidade <= 0) {
            return ESGOTADO;
        }
        if (quantidade < LIMITE_ESTOQUE_BAIXO) {
            return BAIXO;
        }
        return NORMAL;
    }

    /** Texto exibido para o usuário na tabela (ex.: "Estoque Baixo"). */
    public String getRotulo() {
        return rotulo;
    }

    /** Nome da classe CSS usada para colorir a "tag" de status na tabela. */
    public String getEstiloCss() {
        return estiloCss;
    }
}
