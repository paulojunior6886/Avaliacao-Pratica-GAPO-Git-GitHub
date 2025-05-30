/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package br.com.pcar.view;

import br.com.pcar.ctr.OpcionalCTR;
import br.com.pcar.dto.OpcionalDTO;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.Dimension;

public class OpcionalVIEW extends javax.swing.JInternalFrame {

    private OpcionalCTR opcionalCTR = new OpcionalCTR();
    private OpcionalDTO opcionalDTO = new OpcionalDTO();  
    private DefaultTableModel modeloTabelaOpcionais;

    private int gravar_alterar; // 1 para gravar (novo), 2 para alterar

    public OpcionalVIEW() {
        initComponents();
        modeloTabelaOpcionais = (DefaultTableModel) tblOpcionais.getModel();
        
        setPosicao();
        configuraEstadoInicialComponentes();
        listarTodosOpcionaisNaTabela();
    }

    private void configuraEstadoInicialComponentes() {
        limparCamposFormulario();
        habilitarDesabilitarCampos(false);
        
        btnNovoOpcional.setEnabled(true);
        btnSalvarOpcional.setEnabled(false);
        btnEditarOpcional.setEnabled(false); 
        btnExcluirOpcional.setEnabled(false); 
        btnCancelarOpcional.setEnabled(false);
        
        txtPesquisarOpcional.setText("");
        btnPesquisarOpcional.setEnabled(true);
        
        tblOpcionais.setEnabled(true);
        tblOpcionais.clearSelection();
        gravar_alterar = 0; 
    }

    private void configuraComponentesParaNovoOuEditar() {
        habilitarDesabilitarCampos(true);
        
        btnNovoOpcional.setEnabled(false);
        btnSalvarOpcional.setEnabled(true);
        btnEditarOpcional.setEnabled(false); 
        btnExcluirOpcional.setEnabled(gravar_alterar == 2); 
        btnCancelarOpcional.setEnabled(true); 
        
        btnPesquisarOpcional.setEnabled(false);
        txtPesquisarOpcional.setEnabled(false);
        tblOpcionais.setEnabled(false); 
    }
    
    public void setPosicao() {
        try {
            javax.swing.SwingUtilities.invokeLater(() -> {
                if (this.getDesktopPane() != null) {
                    Dimension d = this.getDesktopPane().getSize();
                    // Para evitar que a janela seja maior que o painel e para melhor cálculo
                    this.setSize(Math.min(d.width - 20, this.getPreferredSize().width),
                            Math.min(d.height - 20, this.getPreferredSize().height));

                    int x = (d.width - this.getWidth()) / 2;
                    int y = (d.height - this.getHeight()) / 2;

                    // Garante que a janela não saia da área visível
                    if (x < 0) {
                        x = 0;
                    }
                    if (y < 0) {
                        y = 0;
                    }

                    this.setLocation(x, y);
                }
            });
        } catch (Exception e) {
            System.err.println("Erro ao centralizar ClienteVIEW: " + e.getMessage());
        }
    }

    private void habilitarDesabilitarCampos(boolean habilitar) {
        txtNomeOpcional.setEnabled(habilitar);
        txtDescricaoOpcional.setEnabled(habilitar);
        txtPrecoAdicional.setEnabled(habilitar);
    }

    private void limparCamposFormulario() {
        txtNomeOpcional.setText("");
        txtDescricaoOpcional.setText("");
        txtPrecoAdicional.setText("");
        
        this.opcionalDTO = new OpcionalDTO(); // Limpa o DTO da classe
        
        if (txtNomeOpcional.isEnabled()) {
             txtNomeOpcional.requestFocusInWindow();
        }
    }

    private void carregarOpcionaisNaTabela(String nomeFiltro) {
        modeloTabelaOpcionais.setRowCount(0);
        OpcionalDTO dtoConsulta = new OpcionalDTO();
        ResultSet rs = null;
        int opcao;

        if (nomeFiltro.trim().isEmpty()) {
            opcao = 1; // Listar todos
        } else {
            opcao = 3; // Pesquisar por nome
            dtoConsulta.setNome_opcional(nomeFiltro);
        }
        
        rs = opcionalCTR.consultarOpcionais(opcao, dtoConsulta);

        try {
            if (rs != null) {
                while (rs.next()) {
                    modeloTabelaOpcionais.addRow(new Object[]{
                        rs.getInt("id_opcional"),
                        rs.getString("nome_opcional"),
                        rs.getString("descricao_opcional"),
                        rs.getDouble("preco_adicional")
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao preencher tabela de opcionais: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados dos opcionais:\n" + e.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (rs != null) {
                opcionalCTR.closeDbConnection();
            }
        }
    }
    
    private void listarTodosOpcionaisNaTabela() {
        carregarOpcionaisNaTabela("");
    }

    private void carregarOpcionalSelecionadoParaCampos(int idOpcionalParam) {
        OpcionalDTO dtoConsulta = new OpcionalDTO();
        dtoConsulta.setId_opcional(idOpcionalParam);
        ResultSet rs = null;
        
        try {
            rs = opcionalCTR.consultarOpcionais(2, dtoConsulta); // Opção 2 para buscar por ID
            if (rs != null && rs.next()) {
                limparCamposFormulario(); 

                this.opcionalDTO.setId_opcional(rs.getInt("id_opcional"));
                this.opcionalDTO.setNome_opcional(rs.getString("nome_opcional"));
                this.opcionalDTO.setDescricao_opcional(rs.getString("descricao_opcional"));
                this.opcionalDTO.setPreco_adicional(rs.getDouble("preco_adicional"));

                txtNomeOpcional.setText(this.opcionalDTO.getNome_opcional());
                txtDescricaoOpcional.setText(this.opcionalDTO.getDescricao_opcional());
                txtPrecoAdicional.setText(String.valueOf(this.opcionalDTO.getPreco_adicional()));
                
                habilitarDesabilitarCampos(false); 
                btnNovoOpcional.setEnabled(true);
                btnSalvarOpcional.setEnabled(false);
                btnEditarOpcional.setEnabled(true);
                btnExcluirOpcional.setEnabled(true);
                btnCancelarOpcional.setEnabled(true);
                gravar_alterar = 0; 
            } else {
                 JOptionPane.showMessageDialog(this, "Opcional não encontrado.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar dados do opcional: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados do opcional:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (rs != null) {
                opcionalCTR.closeDbConnection();
            }
        }
    }

    private void gravarNovoOpcional() {
        try {
            if (txtNomeOpcional.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nome do opcional é obrigatório!", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                txtNomeOpcional.requestFocusInWindow();
                return;
            }

            OpcionalDTO dtoParaGravar = new OpcionalDTO();
            dtoParaGravar.setNome_opcional(txtNomeOpcional.getText().trim());
            dtoParaGravar.setDescricao_opcional(txtDescricaoOpcional.getText().trim());
            String precoStr = txtPrecoAdicional.getText().trim().replace(",", ".");
            dtoParaGravar.setPreco_adicional(precoStr.isEmpty() ? 0.0 : Double.parseDouble(precoStr));
            
            String PcarSTATUS = opcionalCTR.inserirOpcional(dtoParaGravar);
            JOptionPane.showMessageDialog(this, PcarSTATUS, "Cadastro de Opcional", JOptionPane.INFORMATION_MESSAGE);
            
            if (PcarSTATUS.toLowerCase().contains("sucesso")) {
                listarTodosOpcionaisNaTabela();
                configuraEstadoInicialComponentes();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Erro de Formato: Verifique o campo Preço Adicional. Use ponto para decimais.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            System.err.println("Erro ao Gravar Opcional: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Erro ao Gravar Opcional:\n" + e.getMessage(), "Erro Crítico", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void alterarOpcionalExistente() {
        try {
            if (this.opcionalDTO.getId_opcional() == 0) { 
                JOptionPane.showMessageDialog(this, "Nenhum opcional selecionado ou ID inválido para alteração!", "Erro de Alteração", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (txtNomeOpcional.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nome do opcional é obrigatório!", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                txtNomeOpcional.requestFocusInWindow();
                return;
            }

            this.opcionalDTO.setNome_opcional(txtNomeOpcional.getText().trim());
            this.opcionalDTO.setDescricao_opcional(txtDescricaoOpcional.getText().trim());
            String precoStr = txtPrecoAdicional.getText().trim().replace(",", ".");
            this.opcionalDTO.setPreco_adicional(precoStr.isEmpty() ? 0.0 : Double.parseDouble(precoStr));

            String PcarSTATUS = opcionalCTR.alterarOpcional(this.opcionalDTO);
            JOptionPane.showMessageDialog(this, PcarSTATUS, "Alteração de Opcional", JOptionPane.INFORMATION_MESSAGE);

            if (PcarSTATUS.toLowerCase().contains("sucesso")) {
                listarTodosOpcionaisNaTabela();
                configuraEstadoInicialComponentes();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Erro de Formato: Verifique o campo Preço Adicional. Use ponto para decimais.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            System.err.println("Erro ao Alterar Opcional: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Erro ao Alterar Opcional:\n" + e.getMessage(), "Erro Crítico", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirOpcionalSelecionado() {
        if (this.opcionalDTO.getId_opcional() == 0) {
            JOptionPane.showMessageDialog(this, "Nenhum opcional selecionado para exclusão.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (JOptionPane.showConfirmDialog(this, "Deseja realmente excluir o Opcional '" + this.opcionalDTO.getNome_opcional() + "'?", "Confirmação de Exclusão",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
            
            String PcarSTATUS = opcionalCTR.excluirOpcional(this.opcionalDTO.getId_opcional());
            JOptionPane.showMessageDialog(this, PcarSTATUS, "Exclusão de Opcional", JOptionPane.INFORMATION_MESSAGE);

            if (PcarSTATUS.toLowerCase().contains("sucesso")) {
                listarTodosOpcionaisNaTabela();
                configuraEstadoInicialComponentes();
            }
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlBotoes = new javax.swing.JPanel();
        btnNovoOpcional = new javax.swing.JButton();
        btnSalvarOpcional = new javax.swing.JButton();
        btnEditarOpcional = new javax.swing.JButton();
        btnExcluirOpcional = new javax.swing.JButton();
        btnCancelarOpcional = new javax.swing.JButton();
        btnSairOpcional = new javax.swing.JButton();
        pnlCamposOpcional = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtNomeOpcional = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtDescricaoOpcional = new javax.swing.JTextArea();
        txtPrecoAdicional = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        pnlTabela = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtPesquisarOpcional = new javax.swing.JTextField();
        btnPesquisarOpcional = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblOpcionais = new javax.swing.JTable();

        setTitle("Gerenciamento de Opcionais de Veículos");

        btnNovoOpcional.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/novo_1.png"))); // NOI18N
        btnNovoOpcional.setText("Novo");
        btnNovoOpcional.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNovoOpcionalActionPerformed(evt);
            }
        });

        btnSalvarOpcional.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/salvar_1.png"))); // NOI18N
        btnSalvarOpcional.setText("Salvar");
        btnSalvarOpcional.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarOpcionalActionPerformed(evt);
            }
        });

        btnEditarOpcional.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/editar.png"))); // NOI18N
        btnEditarOpcional.setText("Editar");
        btnEditarOpcional.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarOpcionalActionPerformed(evt);
            }
        });

        btnExcluirOpcional.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/apagar.png"))); // NOI18N
        btnExcluirOpcional.setText("Excluir");
        btnExcluirOpcional.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirOpcionalActionPerformed(evt);
            }
        });

        btnCancelarOpcional.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/cancelar_1.png"))); // NOI18N
        btnCancelarOpcional.setText("Limpar");
        btnCancelarOpcional.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarOpcionalActionPerformed(evt);
            }
        });

        btnSairOpcional.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/sair_1.png"))); // NOI18N
        btnSairOpcional.setText("Sair");
        btnSairOpcional.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairOpcionalActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlBotoesLayout = new javax.swing.GroupLayout(pnlBotoes);
        pnlBotoes.setLayout(pnlBotoesLayout);
        pnlBotoesLayout.setHorizontalGroup(
            pnlBotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBotoesLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(btnNovoOpcional)
                .addGap(38, 38, 38)
                .addComponent(btnSalvarOpcional)
                .addGap(43, 43, 43)
                .addComponent(btnEditarOpcional)
                .addGap(39, 39, 39)
                .addComponent(btnExcluirOpcional)
                .addGap(38, 38, 38)
                .addComponent(btnCancelarOpcional)
                .addGap(26, 26, 26)
                .addComponent(btnSairOpcional)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlBotoesLayout.setVerticalGroup(
            pnlBotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBotoesLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(pnlBotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSalvarOpcional)
                    .addComponent(btnEditarOpcional)
                    .addComponent(btnExcluirOpcional)
                    .addComponent(btnNovoOpcional)
                    .addComponent(btnCancelarOpcional)
                    .addComponent(btnSairOpcional))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        jLabel2.setText("Nome:");

        jLabel3.setText("Descrição:");

        txtDescricaoOpcional.setColumns(20);
        txtDescricaoOpcional.setRows(5);
        jScrollPane1.setViewportView(txtDescricaoOpcional);

        txtPrecoAdicional.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPrecoAdicionalActionPerformed(evt);
            }
        });

        jLabel4.setText("Preço Adicional:");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 8)); // NOI18N
        jLabel5.setText("Dados do Opcional");

        javax.swing.GroupLayout pnlCamposOpcionalLayout = new javax.swing.GroupLayout(pnlCamposOpcional);
        pnlCamposOpcional.setLayout(pnlCamposOpcionalLayout);
        pnlCamposOpcionalLayout.setHorizontalGroup(
            pnlCamposOpcionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCamposOpcionalLayout.createSequentialGroup()
                .addGroup(pnlCamposOpcionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlCamposOpcionalLayout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(0, 327, Short.MAX_VALUE))
                    .addGroup(pnlCamposOpcionalLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnlCamposOpcionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(pnlCamposOpcionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel4)
                                .addComponent(jLabel3))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlCamposOpcionalLayout.createSequentialGroup()
                                .addGap(51, 51, 51)
                                .addComponent(jLabel2)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlCamposOpcionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNomeOpcional)
                            .addComponent(txtPrecoAdicional)
                            .addComponent(jScrollPane1))))
                .addContainerGap())
        );
        pnlCamposOpcionalLayout.setVerticalGroup(
            pnlCamposOpcionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCamposOpcionalLayout.createSequentialGroup()
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addGroup(pnlCamposOpcionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtNomeOpcional, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlCamposOpcionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlCamposOpcionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPrecoAdicional, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addContainerGap(50, Short.MAX_VALUE))
        );

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Cadastro de Opcionais");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 8)); // NOI18N
        jLabel6.setText("Opcionais Cadastrados");

        jLabel7.setText("Pesquisar por Nome:");

        btnPesquisarOpcional.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/busca.png"))); // NOI18N
        btnPesquisarOpcional.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesquisarOpcionalActionPerformed(evt);
            }
        });

        tblOpcionais.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nome", "Descrição", "Preço Adicional (R$)"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblOpcionais.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblOpcionaisMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblOpcionais);

        javax.swing.GroupLayout pnlTabelaLayout = new javax.swing.GroupLayout(pnlTabela);
        pnlTabela.setLayout(pnlTabelaLayout);
        pnlTabelaLayout.setHorizontalGroup(
            pnlTabelaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTabelaLayout.createSequentialGroup()
                .addComponent(jLabel6)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(pnlTabelaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlTabelaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(pnlTabelaLayout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPesquisarOpcional, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPesquisarOpcional, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(95, Short.MAX_VALUE))))
        );
        pnlTabelaLayout.setVerticalGroup(
            pnlTabelaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTabelaLayout.createSequentialGroup()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlTabelaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtPesquisarOpcional, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPesquisarOpcional))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlBotoes, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(pnlCamposOpcional, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(pnlTabela, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 35, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(383, 383, 383)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlTabela, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlCamposOpcional, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, 34, Short.MAX_VALUE)
                .addComponent(pnlBotoes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnNovoOpcionalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNovoOpcionalActionPerformed
        this.opcionalDTO = new OpcionalDTO(); 
        limparCamposFormulario();
        habilitarDesabilitarCampos(true);
        configuraComponentesParaNovoOuEditar();
        gravar_alterar = 1; 
        btnExcluirOpcional.setEnabled(false);
        txtNomeOpcional.requestFocusInWindow();
    }//GEN-LAST:event_btnNovoOpcionalActionPerformed

    private void btnSalvarOpcionalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarOpcionalActionPerformed
        if (gravar_alterar == 1) {
            gravarNovoOpcional();
        } else if (gravar_alterar == 2) {
            alterarOpcionalExistente();
        } else {
            JOptionPane.showMessageDialog(this, "Ação indefinida. Clique em 'Novo' ou selecione um opcional e clique em 'Editar'.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnSalvarOpcionalActionPerformed

    private void btnEditarOpcionalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarOpcionalActionPerformed
        if (tblOpcionais.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um opcional na tabela para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        gravar_alterar = 2;
        habilitarDesabilitarCampos(true);
        configuraComponentesParaNovoOuEditar();
        txtNomeOpcional.requestFocusInWindow();
    }//GEN-LAST:event_btnEditarOpcionalActionPerformed

    private void btnExcluirOpcionalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirOpcionalActionPerformed
        if (tblOpcionais.getSelectedRow() < 0 && this.opcionalDTO.getId_opcional() == 0) {
             JOptionPane.showMessageDialog(this, "Selecione um opcional na tabela para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        excluirOpcionalSelecionado();
    }//GEN-LAST:event_btnExcluirOpcionalActionPerformed

    private void btnCancelarOpcionalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarOpcionalActionPerformed
        configuraEstadoInicialComponentes();
    }//GEN-LAST:event_btnCancelarOpcionalActionPerformed

    private void txtPrecoAdicionalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPrecoAdicionalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPrecoAdicionalActionPerformed

    private void btnSairOpcionalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSairOpcionalActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnSairOpcionalActionPerformed

    private void tblOpcionaisMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblOpcionaisMouseClicked
        int linhaSelecionada = tblOpcionais.getSelectedRow();
        if (linhaSelecionada >= 0) {
            int idOpcionalSelecionado = (Integer) modeloTabelaOpcionais.getValueAt(linhaSelecionada, 0);
            carregarOpcionalSelecionadoParaCampos(idOpcionalSelecionado);
        }
    }//GEN-LAST:event_tblOpcionaisMouseClicked

    private void btnPesquisarOpcionalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesquisarOpcionalActionPerformed
        carregarOpcionaisNaTabela(txtPesquisarOpcional.getText().trim());
    }//GEN-LAST:event_btnPesquisarOpcionalActionPerformed


    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelarOpcional;
    private javax.swing.JButton btnEditarOpcional;
    private javax.swing.JButton btnExcluirOpcional;
    private javax.swing.JButton btnNovoOpcional;
    private javax.swing.JButton btnPesquisarOpcional;
    private javax.swing.JButton btnSairOpcional;
    private javax.swing.JButton btnSalvarOpcional;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel pnlBotoes;
    private javax.swing.JPanel pnlCamposOpcional;
    private javax.swing.JPanel pnlTabela;
    private javax.swing.JTable tblOpcionais;
    private javax.swing.JTextArea txtDescricaoOpcional;
    private javax.swing.JTextField txtNomeOpcional;
    private javax.swing.JTextField txtPesquisarOpcional;
    private javax.swing.JTextField txtPrecoAdicional;
    // End of variables declaration//GEN-END:variables
}
