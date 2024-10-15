package view;

import controller.ProdutoController;
import model.CriadorTabelas;
import model.Produto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.MouseInputAdapter;
import java.util.List;

public class ProdutoView extends JFrame {
    private ProdutoController controller;
    private JTextField campoNomeProduto, campoQuantidade, campoPreco;
    private JComboBox<String> comboStatus;
    private JButton btnAdicionar, btnAtualizar, btnExcluir, btnLimpar, btnBuscar;
    private JTextField campoBuscarNome;
    private JTable tabelaProdutos;

    public ProdutoView() {
        controller = new ProdutoController();
        CriadorTabelas.main(null);
        inicializarComponentes();
        atualizarTabela();
        adicionarTabelaMouseListener();
    }

    private void inicializarComponentes() {
        setTitle("Gerenciamento de Estoque de Produtos");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Painel de entrada de dados
        JPanel painelEntrada = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Campo de busca
        campoBuscarNome = new JTextField(15);
        btnBuscar = new JButton("Buscar produto pelo nome");
        btnBuscar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarProdutoPorNome();
            }
        });
        // Campo para buscar produto
        gbc.gridx = 0;
        gbc.gridy = 0;
        painelEntrada.add(new JLabel("Buscar: "), gbc);
        gbc.gridx = 1;
        painelEntrada.add(campoBuscarNome, gbc);
        gbc.gridx = 2;
        painelEntrada.add(btnBuscar, gbc);

        // Campo para nome do produto
        campoNomeProduto = new JTextField(15);
        gbc.gridx = 0;
        gbc.gridy = 1;
        painelEntrada.add(new JLabel("Nome do Produto: "), gbc);
        gbc.gridx = 1;
        painelEntrada.add(campoNomeProduto, gbc);

        // Campo para quantidade
        campoQuantidade = new JTextField(5);
        gbc.gridx = 0;
        gbc.gridy = 2;
        painelEntrada.add(new JLabel("Quantidade: "), gbc);
        gbc.gridx = 1;
        painelEntrada.add(campoQuantidade, gbc);

        // Campo para preço
        campoPreco = new JTextField(10);
        gbc.gridx = 0;
        gbc.gridy = 3;
        painelEntrada.add(new JLabel("Preço: "), gbc);
        gbc.gridx = 1;
        painelEntrada.add(campoPreco, gbc);

        // Campo para status
        String[] opcoesStatus = {"Estoque Normal", "Estoque Baixo"};
        comboStatus = new JComboBox<>(opcoesStatus);
        gbc.gridx = 0;
        gbc.gridy = 4;
        painelEntrada.add(new JLabel("Status: "), gbc);
        gbc.gridx = 1;
        painelEntrada.add(comboStatus, gbc);

        // Botões
        btnAdicionar = new JButton("Adicionar Produto");
        btnAdicionar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adicionarProduto();
            }
        });

        btnAtualizar = new JButton("Atualizar Produto");
        btnAtualizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                atualizarProduto();
            }
        });

        btnExcluir = new JButton("Excluir Produto");
        btnExcluir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                excluirProduto();
            }
        });

        btnLimpar = new JButton("Limpar Campos");
        btnLimpar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limparCampos();
            }
        });

        JPanel painelBotoes = new JPanel(new FlowLayout());
        painelBotoes.add(btnAdicionar);
        painelBotoes.add(btnAtualizar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnLimpar);

        // Tabela
        String[] colunas = {"ID", "Produto", "Quantidade", "Preço", "Status"};
        DefaultTableModel modeloTabela = new DefaultTableModel(colunas, 0);
        tabelaProdutos = new JTable(modeloTabela);
        JScrollPane scrollPane = new JScrollPane(tabelaProdutos);

        // Adicionando componentes ao frame
        add(painelEntrada, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);
    }

    private void buscarProdutoPorNome() {
        String nome = campoBuscarNome.getText();
        List<Produto> produtos = controller.buscarProdutosPorNome(nome);
        atualizarTabelaComProdutos(produtos);
    }

    private void atualizarTabela() {
        List<Produto> produtos = controller.listarTodosProdutos();
        atualizarTabelaComProdutos(produtos);
    }

    private void atualizarTabelaComProdutos(List<Produto> produtos) {
        DefaultTableModel modelo = (DefaultTableModel) tabelaProdutos.getModel();
        modelo.setRowCount(0); // Limpa a tabela antes de adicionar novos dados
        for (Produto produto : produtos) {
            modelo.addRow(new Object[]{produto.getId(), produto.getNome(), produto.getQuantidade(), produto.getPreco(), produto.getStatus()});
        }
    }

    private void adicionarProduto() {
        String nome = campoNomeProduto.getText();
        int quantidade = Integer.parseInt(campoQuantidade.getText());
        double preco = Double.parseDouble(campoPreco.getText().replace(',', '.'));
        String status = (String) comboStatus.getSelectedItem();
        controller.adicionarProduto(nome, quantidade, preco, status);
        atualizarTabela();
        limparCampos();
    }

    private void atualizarProduto() {
        int selectedRow = tabelaProdutos.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) tabelaProdutos.getValueAt(selectedRow, 0);
            String nome = campoNomeProduto.getText();
            int quantidade = Integer.parseInt(campoQuantidade.getText());
            double preco = Double.parseDouble(campoPreco.getText().replace(',', '.'));
            String status = (String) comboStatus.getSelectedItem();
            controller.atualizarProduto(id, nome, quantidade, preco, status);
            atualizarTabela();
            limparCampos();
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um produto para atualizar.");
        }
    }

    private void excluirProduto() {
        int selectedRow = tabelaProdutos.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) tabelaProdutos.getValueAt(selectedRow, 0);
            controller.excluirProduto(id);
            atualizarTabela();
            limparCampos();
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um produto para excluir.");
        }
    }
    
    private void adicionarTabelaMouseListener() {
        tabelaProdutos.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int selectedRow = tabelaProdutos.getSelectedRow();
                if (selectedRow != -1) {
                    preencherCamposComProdutoSelecionado(selectedRow);
                }
            }
        });
    }

    private void preencherCamposComProdutoSelecionado(int row) {
        int id = (int) tabelaProdutos.getValueAt(row, 0);
        String nome = (String) tabelaProdutos.getValueAt(row, 1);
        int quantidade = (int) tabelaProdutos.getValueAt(row, 2);
        double preco = (double) tabelaProdutos.getValueAt(row, 3);
        String status = (String) tabelaProdutos.getValueAt(row, 4);

        // Preenche os campos com os dados do produto
        campoNomeProduto.setText(nome);
        campoQuantidade.setText(String.valueOf(quantidade));
        campoPreco.setText(String.valueOf(preco));
        comboStatus.setSelectedItem(status);
    }

    private void limparCampos() {
        campoNomeProduto.setText("");
        campoQuantidade.setText("");
        campoPreco.setText("");
        comboStatus.setSelectedIndex(0);
        campoBuscarNome.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ProdutoView view = new ProdutoView();
            view.setVisible(true);
        });
    }
}
