/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.pcar.view;

import br.com.pcar.ctr.FornecedorCTR;
import br.com.pcar.dto.FornecedorDTO;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.Dimension;
// Para máscara de CNPJ (opcional, mas recomendado)
// import javax.swing.text.MaskFormatter;
// import java.text.ParseException;

public class FornecedorVIEW extends javax.swing.JInternalFrame {

    private FornecedorCTR fornecedorCTR = new FornecedorCTR();
    private FornecedorDTO fornecedorDTO = new FornecedorDTO(); // DTO da classe para operações
    private DefaultTableModel modeloTabelaFornecedores;

    private int gravar_alterar; // 1 para gravar (novo), 2 para alterar
    // private int idFornecedorSelecionado = -1; // O ID será mantido no this.fornecedorDTO

    public FornecedorVIEW() {
        initComponents();
        modeloTabelaFornecedores = (DefaultTableModel) tblFornecedores.getModel();
        
        setPosicao();
        configuraEstadoInicialComponentes();
        listarTodosFornecedoresNaTabela();
        
        // Opcional: Aplicar máscara ao campo CNPJ
        // aplicarMascaraCnpj();
    }

    /*
    // Opcional: Método para aplicar máscara de CNPJ
    private void aplicarMascaraCnpj() {
        try {
            MaskFormatter mascaraCnpj = new MaskFormatter("##.###.###/####-##");
            mascaraCnpj.setPlaceholderCharacter('_');
            // Se o JFormattedTextField for usado no design:
            // cnpj_fornecedor_fmt.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(mascaraCnpj));
            // Se estiver usando JTextField, a aplicação da máscara é mais complexa ou requer componentes de terceiros.
            // Por simplicidade, manteremos JTextField e o usuário deve inserir formatado ou faremos a formatação/validação na lógica.
        } catch (ParseException e) {
            System.err.println("Erro ao aplicar máscara de CNPJ: " + e.getMessage());
        }
    }
    */

    private void configuraEstadoInicialComponentes() {
        limparCamposFormulario();
        habilitarDesabilitarCampos(false);
        
        btnNovoFornecedor.setEnabled(true);
        btnSalvarFornecedor.setEnabled(false);
        btnEditarFornecedor.setEnabled(false); 
        btnExcluirFornecedor.setEnabled(false); 
        btnCancelarFornecedor.setEnabled(false);
        
        cmbTipoPesquisa.setSelectedIndex(0); // Default para pesquisar por nome
        txtPesquisarFornecedor.setText("");
        btnPesquisarFornecedor.setEnabled(true);
        
        tblFornecedores.setEnabled(true);
        tblFornecedores.clearSelection();
        gravar_alterar = 0; 
    }

    private void configuraComponentesParaNovoOuEditar() {
        habilitarDesabilitarCampos(true);
        
        btnNovoFornecedor.setEnabled(false);
        btnSalvarFornecedor.setEnabled(true);
        btnEditarFornecedor.setEnabled(false); 
        btnExcluirFornecedor.setEnabled(gravar_alterar == 2); 
        btnCancelarFornecedor.setEnabled(true); 
        
        btnPesquisarFornecedor.setEnabled(false);
        txtPesquisarFornecedor.setEnabled(false);
        cmbTipoPesquisa.setEnabled(false);
        tblFornecedores.setEnabled(false); 
    }
    
    public void setPosicao() {
         if (this.getDesktopPane() != null) {
            javax.swing.SwingUtilities.invokeLater(() -> {
                if (this.getDesktopPane() != null) { 
                    Dimension d = this.getDesktopPane().getSize();
                    this.setSize(Math.min(d.width - 20, this.getPreferredSize().width), 
                                 Math.min(d.height - 20, this.getPreferredSize().height));
                    int x = (d.width - this.getWidth()) / 2;
                    int y = (d.height - this.getHeight()) / 2;
                    if (x < 0) x = 0;
                    if (y < 0) y = 0;
                    this.setLocation(x, y);
                }
            });
        }
    }

    private void habilitarDesabilitarCampos(boolean habilitar) {
        txtNomeFornecedor.setEnabled(habilitar);
        txtCnpjFornecedor.setEnabled(habilitar);
        txtEnderecoFornecedor.setEnabled(habilitar);
        txtTelefoneFornecedor.setEnabled(habilitar);
        txtEmailFornecedor.setEnabled(habilitar);
        txtTelefoneFornecedor.setEnabled(habilitar);
    }

    private void limparCamposFormulario() {
        txtNomeFornecedor.setText("");
        txtCnpjFornecedor.setText("");
        txtEnderecoFornecedor.setText("");
        txtTelefoneFornecedor.setText("");
        txtEmailFornecedor.setText("");
        txtTelefoneFornecedor.setText("");
        
        this.fornecedorDTO = new FornecedorDTO(); // Limpa o DTO da classe
        
        if (txtNomeFornecedor.isEnabled()) {
             txtNomeFornecedor.requestFocusInWindow();
        }
    }

    private void carregarFornecedoresNaTabela(String termoPesquisa, String tipoPesquisa) {
        modeloTabelaFornecedores.setRowCount(0);
        FornecedorDTO dtoConsulta = new FornecedorDTO();
        ResultSet rs = null;
        int opcao = 4; // Padrão: Listar todos

        if (!termoPesquisa.trim().isEmpty()) {
            if ("Nome".equals(tipoPesquisa)) {
                opcao = 1; // Pesquisar por nome
                dtoConsulta.setNome_fornecedor(termoPesquisa);
            } else if ("CNPJ".equals(tipoPesquisa)) {
                opcao = 2; // Pesquisar por CNPJ
                dtoConsulta.setCnpj_fornecedor(termoPesquisa);
            }
        }
        
        rs = fornecedorCTR.consultarFornecedor(dtoConsulta, opcao);

        try {
            if (rs != null) {
                while (rs.next()) {
                    modeloTabelaFornecedores.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nome_fornecedor"),
                        rs.getString("cnpj_fornecedor"),
                        rs.getString("telefone_fornecedor"),
                        rs.getString("email_fornecedor"),
                        rs.getString("contato_principal")
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao preencher tabela de fornecedores: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados dos fornecedores:\n" + e.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (rs != null) {
                fornecedorCTR.closeDbConnection();
            }
        }
    }
    
    private void listarTodosFornecedoresNaTabela() {
        carregarFornecedoresNaTabela("", ""); // Termo e tipo vazios para listar todos
    }

    private void carregarFornecedorSelecionadoParaCampos(int idFornecedorParam) {
        FornecedorDTO dtoConsulta = new FornecedorDTO();
        dtoConsulta.setId_fornecedor(idFornecedorParam);
        ResultSet rs = null;
        
        try {
            rs = fornecedorCTR.consultarFornecedor(dtoConsulta, 3); // Opção 3 para buscar por ID
            if (rs != null && rs.next()) {
                limparCamposFormulario(); 

                // Atualiza o fornecedorDTO da classe
                this.fornecedorDTO.setId_fornecedor(rs.getInt("id"));
                this.fornecedorDTO.setNome_fornecedor(rs.getString("nome_fornecedor"));
                this.fornecedorDTO.setCnpj_fornecedor(rs.getString("cnpj_fornecedor"));
                this.fornecedorDTO.setEndereco_fornecedor(rs.getString("endereco_fornecedor"));
                this.fornecedorDTO.setTelefone_fornecedor(rs.getString("telefone_fornecedor"));
                this.fornecedorDTO.setEmail_fornecedor(rs.getString("email_fornecedor"));
                this.fornecedorDTO.setContato_principal(rs.getString("contato_principal"));

                // Preenche campos da UI
                txtNomeFornecedor.setText(this.fornecedorDTO.getNome_fornecedor());
                txtCnpjFornecedor.setText(this.fornecedorDTO.getCnpj_fornecedor());
                txtEnderecoFornecedor.setText(this.fornecedorDTO.getEndereco_fornecedor());
                txtTelefoneFornecedor.setText(this.fornecedorDTO.getTelefone_fornecedor());
                txtEmailFornecedor.setText(this.fornecedorDTO.getEmail_fornecedor());
                txtTelefoneFornecedor.setText(this.fornecedorDTO.getContato_principal());
                
                habilitarDesabilitarCampos(false); // Mantém campos bloqueados
                btnNovoFornecedor.setEnabled(true);
                btnSalvarFornecedor.setEnabled(false);
                btnEditarFornecedor.setEnabled(true);
                btnExcluirFornecedor.setEnabled(true);
                btnCancelarFornecedor.setEnabled(true);
                gravar_alterar = 0; 
            } else {
                 JOptionPane.showMessageDialog(this, "Fornecedor não encontrado.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar dados do fornecedor: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados do fornecedor:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (rs != null) {
                fornecedorCTR.closeDbConnection();
            }
        }
    }

    private void gravarNovoFornecedor() {
        try {
            if (txtNomeFornecedor.getText().trim().isEmpty() || txtCnpjFornecedor.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nome e CNPJ do fornecedor são obrigatórios!", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }

            FornecedorDTO dtoParaGravar = new FornecedorDTO();
            dtoParaGravar.setNome_fornecedor(txtNomeFornecedor.getText());
            dtoParaGravar.setCnpj_fornecedor(txtCnpjFornecedor.getText()); // Adicionar validação/formatação de CNPJ aqui se necessário
            dtoParaGravar.setEndereco_fornecedor(txtEnderecoFornecedor.getText());
            dtoParaGravar.setTelefone_fornecedor(txtTelefoneFornecedor.getText());
            dtoParaGravar.setEmail_fornecedor(txtEmailFornecedor.getText());
            dtoParaGravar.setContato_principal(txtTelefoneFornecedor.getText());
            
            String PcarSTATUS = fornecedorCTR.inserirFornecedor(dtoParaGravar);
            JOptionPane.showMessageDialog(this, PcarSTATUS, "Cadastro de Fornecedor", JOptionPane.INFORMATION_MESSAGE);
            
            if (PcarSTATUS.toLowerCase().contains("sucesso")) {
                listarTodosFornecedoresNaTabela();
                configuraEstadoInicialComponentes();
            }
        } catch (Exception e) {
            System.err.println("Erro ao Gravar Fornecedor: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Erro ao Gravar Fornecedor:\n" + e.getMessage(), "Erro Crítico", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void alterarFornecedorExistente() {
        try {
            if (this.fornecedorDTO.getId_fornecedor() == 0) { 
                JOptionPane.showMessageDialog(this, "Nenhum fornecedor selecionado ou ID inválido para alteração!", "Erro de Alteração", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (txtNomeFornecedor.getText().trim().isEmpty() || txtCnpjFornecedor.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nome e CNPJ do fornecedor são obrigatórios!", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }

            this.fornecedorDTO.setNome_fornecedor(txtNomeFornecedor.getText());
            this.fornecedorDTO.setCnpj_fornecedor(txtCnpjFornecedor.getText());
            this.fornecedorDTO.setEndereco_fornecedor(txtEnderecoFornecedor.getText());
            this.fornecedorDTO.setTelefone_fornecedor(txtTelefoneFornecedor.getText());
            this.fornecedorDTO.setEmail_fornecedor(txtEmailFornecedor.getText());
            this.fornecedorDTO.setContato_principal(txtTelefoneFornecedor.getText());

            String PcarSTATUS = fornecedorCTR.alterarFornecedor(this.fornecedorDTO);
            JOptionPane.showMessageDialog(this, PcarSTATUS, "Alteração de Fornecedor", JOptionPane.INFORMATION_MESSAGE);

            if (PcarSTATUS.toLowerCase().contains("sucesso")) {
                listarTodosFornecedoresNaTabela();
                configuraEstadoInicialComponentes();
            }
        } catch (Exception e) {
            System.err.println("Erro ao Alterar Fornecedor: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Erro ao Alterar Fornecedor:\n" + e.getMessage(), "Erro Crítico", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirFornecedorSelecionado() {
        if (this.fornecedorDTO.getId_fornecedor() == 0) {
            JOptionPane.showMessageDialog(this, "Nenhum fornecedor selecionado para exclusão.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (JOptionPane.showConfirmDialog(this, "Deseja realmente excluir o Fornecedor " + this.fornecedorDTO.getNome_fornecedor() + "?", "Confirmação de Exclusão",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
            
            String PcarSTATUS = fornecedorCTR.excluirFornecedor(this.fornecedorDTO);
            JOptionPane.showMessageDialog(this, PcarSTATUS, "Exclusão de Fornecedor", JOptionPane.INFORMATION_MESSAGE);

            if (PcarSTATUS.toLowerCase().contains("sucesso")) {
                listarTodosFornecedoresNaTabela();
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

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtNomeFornecedor = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtCnpjFornecedor = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtEnderecoFornecedor = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        btnNovoFornecedor = new javax.swing.JButton();
        btnSalvarFornecedor = new javax.swing.JButton();
        btnCancelarFornecedor = new javax.swing.JButton();
        btnExcluirFornecedor = new javax.swing.JButton();
        btnEditarFornecedor = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        txtPesquisarFornecedor = new javax.swing.JTextField();
        btnPesquisarFornecedor = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblFornecedores = new javax.swing.JTable();
        txtEmailFornecedor = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtTelefoneFornecedor = new javax.swing.JTextField();
        cmbTipoPesquisa = new javax.swing.JComboBox<>();
        btnSairFornecedor1 = new javax.swing.JButton();
        txtContatoPrincipal = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setText("Fornecedor");

        jLabel2.setText("Nome/Razão Social:");

        jLabel3.setText("CNPJ:");

        jLabel4.setText("Endereço:");

        jLabel5.setText("Email:");

        btnNovoFornecedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/novo_1.png"))); // NOI18N
        btnNovoFornecedor.setText("Novo");
        btnNovoFornecedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNovoFornecedorActionPerformed(evt);
            }
        });

        btnSalvarFornecedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/salvar_1.png"))); // NOI18N
        btnSalvarFornecedor.setText("Salvar");
        btnSalvarFornecedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarFornecedorActionPerformed(evt);
            }
        });

        btnCancelarFornecedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/cancelar_1.png"))); // NOI18N
        btnCancelarFornecedor.setText("Cancelar");
        btnCancelarFornecedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarFornecedorActionPerformed(evt);
            }
        });

        btnExcluirFornecedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/apagar.png"))); // NOI18N
        btnExcluirFornecedor.setText("Excluir");
        btnExcluirFornecedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirFornecedorActionPerformed(evt);
            }
        });

        btnEditarFornecedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/editar.png"))); // NOI18N
        btnEditarFornecedor.setText("Editar");
        btnEditarFornecedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarFornecedorActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel6.setText("Consulta");

        btnPesquisarFornecedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/busca.png"))); // NOI18N
        btnPesquisarFornecedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesquisarFornecedorActionPerformed(evt);
            }
        });

        tblFornecedores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nome"
            }
        ));
        tblFornecedores.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblFornecedoresMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblFornecedores);

        jLabel8.setText("Contato:");

        txtTelefoneFornecedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTelefoneFornecedorActionPerformed(evt);
            }
        });

        cmbTipoPesquisa.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nome", "CNPJ" }));

        btnSairFornecedor1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/sair_1.png"))); // NOI18N
        btnSairFornecedor1.setText("Sair");
        btnSairFornecedor1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairFornecedor1ActionPerformed(evt);
            }
        });

        txtContatoPrincipal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtContatoPrincipalActionPerformed(evt);
            }
        });

        jLabel9.setText("Contato Principal:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(13, 13, 13)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel1)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel2)
                                            .addComponent(jLabel4)
                                            .addComponent(jLabel3)
                                            .addComponent(jLabel9))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                    .addComponent(txtEnderecoFornecedor, javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(txtCnpjFornecedor, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(layout.createSequentialGroup()
                                                        .addGap(12, 12, 12)
                                                        .addComponent(jLabel5)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(txtEmailFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addGroup(layout.createSequentialGroup()
                                                        .addComponent(jLabel8)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(txtTelefoneFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                            .addComponent(txtContatoPrincipal, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(txtNomeFornecedor)))))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(btnNovoFornecedor)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSalvarFornecedor)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnCancelarFornecedor)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnExcluirFornecedor)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnEditarFornecedor)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(cmbTipoPesquisa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtPesquisarFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(148, 148, 148)
                                        .addComponent(jLabel6)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnPesquisarFornecedor))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSairFornecedor1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtPesquisarFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cmbTipoPesquisa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(btnPesquisarFornecedor, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtNomeFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtCnpjFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)
                            .addComponent(txtEmailFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txtEnderecoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)
                            .addComponent(txtTelefoneFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(txtContatoPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(31, 31, 31)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnNovoFornecedor)
                            .addComponent(btnSalvarFornecedor)
                            .addComponent(btnCancelarFornecedor)
                            .addComponent(btnExcluirFornecedor)
                            .addComponent(btnEditarFornecedor))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                .addComponent(btnSairFornecedor1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnNovoFornecedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNovoFornecedorActionPerformed
       this.fornecedorDTO = new FornecedorDTO(); // Limpa DTO para novo
        limparCamposFormulario();
        habilitarDesabilitarCampos(true);
        configuraComponentesParaNovoOuEditar();
        gravar_alterar = 1; // Modo Novo
        btnExcluirFornecedor.setEnabled(false);
        txtNomeFornecedor.requestFocusInWindow();
    }//GEN-LAST:event_btnNovoFornecedorActionPerformed

    private void btnSalvarFornecedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarFornecedorActionPerformed
         if (gravar_alterar == 1) {
            gravarNovoFornecedor();
        } else if (gravar_alterar == 2) {
            alterarFornecedorExistente();
        } else {
            JOptionPane.showMessageDialog(this, "Ação indefinida. Clique em 'Novo' ou selecione um fornecedor e clique em 'Editar'.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnSalvarFornecedorActionPerformed

    private void btnPesquisarFornecedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesquisarFornecedorActionPerformed
        String termo = txtPesquisarFornecedor.getText();
        String tipo = cmbTipoPesquisa.getSelectedItem().toString();
        carregarFornecedoresNaTabela(termo, tipo);
    }//GEN-LAST:event_btnPesquisarFornecedorActionPerformed

    private void tblFornecedoresMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblFornecedoresMouseClicked
          int linhaSelecionada = tblFornecedores.getSelectedRow();
        if (linhaSelecionada >= 0) {
            int idFornecedorSelecionado = (int) modeloTabelaFornecedores.getValueAt(linhaSelecionada, 0);
            carregarFornecedorSelecionadoParaCampos(idFornecedorSelecionado);
        }
    }//GEN-LAST:event_tblFornecedoresMouseClicked

    private void btnExcluirFornecedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirFornecedorActionPerformed
        if (tblFornecedores.getSelectedRow() < 0 && this.fornecedorDTO.getId_fornecedor() == 0) {
             JOptionPane.showMessageDialog(this, "Selecione um fornecedor na tabela para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        excluirFornecedorSelecionado();
    }//GEN-LAST:event_btnExcluirFornecedorActionPerformed

    private void btnCancelarFornecedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarFornecedorActionPerformed
        configuraEstadoInicialComponentes();
    }//GEN-LAST:event_btnCancelarFornecedorActionPerformed

    private void btnEditarFornecedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarFornecedorActionPerformed
        if (tblFornecedores.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um fornecedor na tabela para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Os dados já foram carregados no this.fornecedorDTO pelo clique na tabela
        gravar_alterar = 2;
        habilitarDesabilitarCampos(true);
        configuraComponentesParaNovoOuEditar();
        txtNomeFornecedor.requestFocusInWindow();
    }//GEN-LAST:event_btnEditarFornecedorActionPerformed

    private void txtTelefoneFornecedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTelefoneFornecedorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTelefoneFornecedorActionPerformed

    private void btnSairFornecedor1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSairFornecedor1ActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnSairFornecedor1ActionPerformed

    private void txtContatoPrincipalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtContatoPrincipalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtContatoPrincipalActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelarFornecedor;
    private javax.swing.JButton btnEditarFornecedor;
    private javax.swing.JButton btnExcluirFornecedor;
    private javax.swing.JButton btnNovoFornecedor;
    private javax.swing.JButton btnPesquisarFornecedor;
    private javax.swing.JButton btnSairFornecedor1;
    private javax.swing.JButton btnSalvarFornecedor;
    private javax.swing.JComboBox<String> cmbTipoPesquisa;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblFornecedores;
    private javax.swing.JTextField txtCnpjFornecedor;
    private javax.swing.JTextField txtContatoPrincipal;
    private javax.swing.JTextField txtEmailFornecedor;
    private javax.swing.JTextField txtEnderecoFornecedor;
    private javax.swing.JTextField txtNomeFornecedor;
    private javax.swing.JTextField txtPesquisarFornecedor;
    private javax.swing.JTextField txtTelefoneFornecedor;
    // End of variables declaration//GEN-END:variables
}
