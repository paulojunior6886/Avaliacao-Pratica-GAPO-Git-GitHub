/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.pcar.view;

import javax.swing.JOptionPane;
import java.awt.Dimension;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import br.com.pcar.dto.PcarDTO;
import br.com.pcar.ctr.ClienteCTR;

/**
 *
 * @author paulo
 */
public class ClienteVIEW extends javax.swing.JInternalFrame {

    PcarDTO clienteDTO = new PcarDTO(); 
    ClienteCTR clienteCTR = new ClienteCTR(); //

    int gravar_alterar; 
    DefaultTableModel modelo_tabela_cliente;

    public ClienteVIEW() {
        initComponents();
        modelo_tabela_cliente = (DefaultTableModel) tabelaClientes.getModel();

        
        setPosicao(); // Centraliza o JInternalFrame
        limpaCampos();
        liberaCampos(false);
        configuraBotoesEstadoInicial();
        listarClientes(); // Carrega todos os clientes ao abrir a tela
    }

    
    private void carregarTabelaClientes(String nome_filtro) {
        modelo_tabela_cliente.setRowCount(0); // Limpa a tabela
        PcarDTO dtoConsulta = new PcarDTO(); //
        ResultSet rs; //
        int opcao;

        if (nome_filtro.trim().isEmpty()) {
            opcao = 3; // Listar todos
        } else {
            opcao = 1; // Pesquisar por nome
            dtoConsulta.setNome_cliente(nome_filtro); //
        }

        rs = clienteCTR.consultarCliente(dtoConsulta, opcao); //

        try {
            if (rs != null) {
                while (rs.next()) {
                    modelo_tabela_cliente.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nome_cliente"),
                        rs.getString("cpf_cliente"),
                        rs.getString("endereco_cliente"),
                        rs.getString("telefone_cliente"),
                        rs.getString("email_cliente")
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao preencher tabela de clientes: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados dos clientes:\n" + e.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (rs != null) { 
                clienteCTR.CloseDB(); //
            }
        }
    }

    // Método wrapper para listar todos os clientes 
    private void listarClientes() {
        carregarTabelaClientes("");
    }

    /**
     * Centraliza o JInternalFrame no DesktopPane.
     */
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

    private void liberaCampos(boolean liberar) {
        nome_cliente.setEnabled(liberar);
        cpf_cliente.setEnabled(liberar);
        endereco_cliente.setEnabled(liberar);
        telefone_cliente.setEnabled(liberar);
        email_cliente.setEnabled(liberar);
    }

    private void limpaCampos() {
        nome_cliente.setText("");
        cpf_cliente.setText("");
        endereco_cliente.setText("");
        telefone_cliente.setText("");
        email_cliente.setText("");
        if (nome_cliente.isEnabled()) { // Só foca se estiver habilitado
            nome_cliente.requestFocus();
        }
        // Não reseta o clienteDTO da classe aqui, pois ele pode conter o ID do cliente selecionado
    }

    private void configuraBotoesEstadoInicial() {
        btnNovoCliente.setEnabled(true);
        btnSalvarCliente.setEnabled(false);
        btnEditarCliente.setEnabled(false); 
        btnExcluirCliente.setEnabled(false); 
        btnLimparCliente.setEnabled(false); 
        btnBuscarCliente.setEnabled(true);

        tabelaClientes.setEnabled(true); 
        liberaCampos(false); 
        limpaCampos(); 
        tabelaClientes.clearSelection();
        gravar_alterar = 0; 
    }

    private void configuraBotoesParaNovoOuEditar() {
        btnNovoCliente.setEnabled(false);
        btnSalvarCliente.setEnabled(true);
        btnEditarCliente.setEnabled(false); 
        btnExcluirCliente.setEnabled(gravar_alterar == 2); 
        btnLimparCliente.setEnabled(true); 
        btnBuscarCliente.setEnabled(false); 

        tabelaClientes.setEnabled(false); 
        liberaCampos(true); 
    }

    /**
     * Preenche os campos do formulário com os dados de um cliente específico
     * (buscado por ID). Atualiza o clienteDTO da classe com os dados
     * carregados.
     *
     * @param id_cliente_para_carregar O ID do cliente cujos dados serão
     * carregados.
     */
    private void carregarDadosClienteNosCampos(int id_cliente_para_carregar) {
        PcarDTO dtoConsulta = new PcarDTO(); 
        dtoConsulta.setId_cliente(id_cliente_para_carregar); 

        ResultSet rs = clienteCTR.consultarCliente(dtoConsulta, 2); 

        try {
            if (rs != null && rs.next()) {
                this.clienteDTO.setId_cliente(rs.getInt("id")); 
                this.clienteDTO.setNome_cliente(rs.getString("nome_cliente")); 
                this.clienteDTO.setCpf_cliente(rs.getString("cpf_cliente")); 
                this.clienteDTO.setEndereco_cliente(rs.getString("endereco_cliente")); 
                this.clienteDTO.setTelefone_cliente(rs.getString("telefone_cliente")); 
                this.clienteDTO.setEmail_cliente(rs.getString("email_cliente")); 

                // Preenche os campos da interface
                nome_cliente.setText(this.clienteDTO.getNome_cliente());
                cpf_cliente.setText(this.clienteDTO.getCpf_cliente());
                endereco_cliente.setText(this.clienteDTO.getEndereco_cliente());
                telefone_cliente.setText(this.clienteDTO.getTelefone_cliente());
                email_cliente.setText(this.clienteDTO.getEmail_cliente());

                gravar_alterar = 2; // Prepara para uma possível alteração
                configuraBotoesParaNovoOuEditar(); 
                btnEditarCliente.setEnabled(true); 

            } else {
                JOptionPane.showMessageDialog(this, "Cliente não encontrado.", "Aviso", JOptionPane.WARNING_MESSAGE);
                configuraBotoesEstadoInicial();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar dados do cliente nos campos: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados do cliente:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            configuraBotoesEstadoInicial();
        } finally {
            if (rs != null) {
                clienteCTR.CloseDB(); //
            }
        }
    }

    private void gravarNovoCliente() {
        try {
            PcarDTO novoCliente = new PcarDTO(); 
            novoCliente.setNome_cliente(nome_cliente.getText()); 
            novoCliente.setCpf_cliente(cpf_cliente.getText()); 
            novoCliente.setEndereco_cliente(endereco_cliente.getText()); 
            novoCliente.setTelefone_cliente(telefone_cliente.getText()); 
            novoCliente.setEmail_cliente(email_cliente.getText()); 

            String PcarSTATUS = clienteCTR.inserirCliente(novoCliente); 
            JOptionPane.showMessageDialog(this, PcarSTATUS, "Cadastro de Cliente", JOptionPane.INFORMATION_MESSAGE);

            if (PcarSTATUS.toLowerCase().contains("sucesso")) {
                listarClientes();
                configuraBotoesEstadoInicial();
            }
        } catch (Exception e) {
            System.err.println("Erro ao Gravar Cliente: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Erro ao Gravar Cliente:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void alterarClienteExistente() {
        try {           
            this.clienteDTO.setNome_cliente(nome_cliente.getText()); 
            this.clienteDTO.setCpf_cliente(cpf_cliente.getText()); 
            this.clienteDTO.setEndereco_cliente(endereco_cliente.getText()); 
            this.clienteDTO.setTelefone_cliente(telefone_cliente.getText()); 
            this.clienteDTO.setEmail_cliente(email_cliente.getText()); 

            String PcarSTATUS = clienteCTR.alterarCliente(this.clienteDTO); 
            JOptionPane.showMessageDialog(this, PcarSTATUS, "Alteração de Cliente", JOptionPane.INFORMATION_MESSAGE);

            if (PcarSTATUS.toLowerCase().contains("sucesso")) {
                listarClientes();
                configuraBotoesEstadoInicial();
            }
        } catch (Exception e) {
            System.err.println("Erro ao Alterar Cliente: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Erro ao Alterar Cliente:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirClienteSelecionado() {
        if (this.clienteDTO.getId_cliente() <= 0) { // Verifica se um cliente (com ID) está carregado no DTO da classe
            JOptionPane.showMessageDialog(this, "Nenhum cliente selecionado para exclusão.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (JOptionPane.showConfirmDialog(this, "Deseja realmente excluir o Cliente " + this.clienteDTO.getNome_cliente() + "?", "Confirmação de Exclusão", //
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {

            String PcarSTATUS = clienteCTR.excluirCliente(this.clienteDTO); 
            JOptionPane.showMessageDialog(this, PcarSTATUS, "Exclusão de Cliente", JOptionPane.INFORMATION_MESSAGE);

            if (PcarSTATUS.toLowerCase().contains("sucesso")) {
                listarClientes();
                configuraBotoesEstadoInicial();
            }
        }
    }

    /* private void preencheCampos(int cpf_cliente) {
        try {
            clienteDTO.setCpf_cliente(String.valueOf(cpf_cliente));
            rs = clienteCTR.consultarCliente(clienteDTO, 2);
            if (rs.next()) {
                limpaCampos();
                nome_cliente.setText(rs.getString("nome_cliente"));
                endereco_cliente.setText(rs.getString("endereco_cliente"));
                telefone_cliente.setText(rs.getString("telefone_cliente"));
                email_cliente.setText(rs.getString("email_cliente"));

                gravar_alterar = 2;
                liberaCampos(true);
            }
        } catch (Exception erTab) {
            System.out.println("Erro SQL: " + erTab);
        } finally {
            clienteCTR.CloseDB();
        }
    }*/
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
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        nome_cliente = new javax.swing.JTextField();
        cpf_cliente = new javax.swing.JTextField();
        endereco_cliente = new javax.swing.JTextField();
        telefone_cliente = new javax.swing.JTextField();
        email_cliente = new javax.swing.JTextField();
        btnEditarCliente = new javax.swing.JButton();
        btnExcluirCliente = new javax.swing.JButton();
        btnLimparCliente = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        txtPesquisarCliente = new javax.swing.JTextField();
        btnBuscarCliente = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabelaClientes = new javax.swing.JTable();
        btnNovoCliente = new javax.swing.JButton();
        btnSairCliente = new javax.swing.JButton();
        btnSalvarCliente = new javax.swing.JButton();

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        jLabel1.setText("Cliente");

        jLabel2.setText("Nome:");

        jLabel3.setText("CPF:");

        jLabel4.setText("Endereço:");

        jLabel5.setText("Telefone:");

        jLabel6.setText("E-mail:");

        btnEditarCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/editar.png"))); // NOI18N
        btnEditarCliente.setText("Editar");
        btnEditarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarClienteActionPerformed(evt);
            }
        });

        btnExcluirCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/apagar.png"))); // NOI18N
        btnExcluirCliente.setText("Excluir");
        btnExcluirCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirClienteActionPerformed(evt);
            }
        });

        btnLimparCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/cancelar_1.png"))); // NOI18N
        btnLimparCliente.setText("Limpar");
        btnLimparCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimparClienteActionPerformed(evt);
            }
        });

        jLabel7.setText("Pesquisar:");

        btnBuscarCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/busca.png"))); // NOI18N
        btnBuscarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarClienteActionPerformed(evt);
            }
        });

        tabelaClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nome", "CPF", "Endereço", "Telefone", "Email"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabelaClientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelaClientesMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tabelaClientes);

        btnNovoCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/novo_1.png"))); // NOI18N
        btnNovoCliente.setText("Novo");
        btnNovoCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNovoClienteActionPerformed(evt);
            }
        });

        btnSairCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/sair_1.png"))); // NOI18N
        btnSairCliente.setText("Sair");
        btnSairCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairClienteActionPerformed(evt);
            }
        });

        btnSalvarCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/salvar_1.png"))); // NOI18N
        btnSalvarCliente.setText("Salvar");
        btnSalvarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarClienteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(endereco_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(nome_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cpf_cliente)
                            .addComponent(telefone_cliente)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 707, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel6)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(email_cliente))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(btnNovoCliente)
                                                .addGap(18, 18, 18)
                                                .addComponent(btnSalvarCliente)))
                                        .addGap(18, 18, 18)
                                        .addComponent(btnEditarCliente)
                                        .addGap(18, 18, 18)
                                        .addComponent(btnExcluirCliente)
                                        .addGap(18, 18, 18)
                                        .addComponent(btnLimparCliente)
                                        .addGap(18, 18, 18)
                                        .addComponent(btnSairCliente)
                                        .addGap(61, 61, 61))
                                    .addComponent(txtPesquisarCliente))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBuscarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(374, 374, 374))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(nome_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(cpf_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(endereco_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(telefone_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(email_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNovoCliente)
                    .addComponent(btnSalvarCliente)
                    .addComponent(btnEditarCliente)
                    .addComponent(btnExcluirCliente)
                    .addComponent(btnLimparCliente)
                    .addComponent(btnSairCliente))
                .addGap(18, 27, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7)
                        .addComponent(txtPesquisarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnBuscarCliente))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
        
    private void btnEditarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarClienteActionPerformed
         if (tabelaClientes.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        gravar_alterar = 2; 
        liberaCampos(true); 
        configuraBotoesParaNovoOuEditar(); 
        nome_cliente.requestFocus();
    }//GEN-LAST:event_btnEditarClienteActionPerformed

    private void btnExcluirClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirClienteActionPerformed
        if (tabelaClientes.getSelectedRow() < 0 && this.clienteDTO.getId_cliente() <= 0) { 
             JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // O this.clienteDTO já foi populado com o ID no clique da tabela.
        excluirClienteSelecionado();
        // A atualização da tabela e o reset dos botões são feitos dentro de excluir se sucesso
    }//GEN-LAST:event_btnExcluirClienteActionPerformed

    private void btnBuscarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarClienteActionPerformed
        carregarTabelaClientes(txtPesquisarCliente.getText());
    }//GEN-LAST:event_btnBuscarClienteActionPerformed

    private void tabelaClientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelaClientesMouseClicked
         int linhaSelecionada = tabelaClientes.getSelectedRow();
        if (linhaSelecionada >= 0) {
            int id_cliente_selecionado = (int) modelo_tabela_cliente.getValueAt(linhaSelecionada, 0);
            carregarDadosClienteNosCampos(id_cliente_selecionado); // Carrega dados e prepara para edição
            btnEditarCliente.setEnabled(true);
            btnExcluirCliente.setEnabled(true);
            btnLimparCliente.setEnabled(true); 
        }
    }//GEN-LAST:event_tabelaClientesMouseClicked

    private void btnLimparClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimparClienteActionPerformed
        configuraBotoesEstadoInicial();
    }//GEN-LAST:event_btnLimparClienteActionPerformed

    private void btnNovoClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNovoClienteActionPerformed
        limpaCampos(); 
        liberaCampos(true); 
        configuraBotoesParaNovoOuEditar(); 
        gravar_alterar = 1; 
        btnExcluirCliente.setEnabled(false); 
        nome_cliente.requestFocus();
    }//GEN-LAST:event_btnNovoClienteActionPerformed

    private void btnSairClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSairClienteActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnSairClienteActionPerformed

    private void btnSalvarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarClienteActionPerformed
       if (nome_cliente.getText().trim().isEmpty() || cpf_cliente.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome e CPF são obrigatórios!", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (gravar_alterar == 1) { // Novo
            gravarNovoCliente();
        } else if (gravar_alterar == 2) { // Alterar
            alterarClienteExistente();
        } else {
            JOptionPane.showMessageDialog(this, "Ação indefinida. Clique em 'Novo' ou selecione um cliente e clique em 'Editar'.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
        // A atualização da tabela e o reset dos botões são feitos dentro de gravar/alterar se sucesso
    }//GEN-LAST:event_btnSalvarClienteActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscarCliente;
    private javax.swing.JButton btnEditarCliente;
    private javax.swing.JButton btnExcluirCliente;
    private javax.swing.JButton btnLimparCliente;
    private javax.swing.JButton btnNovoCliente;
    private javax.swing.JButton btnSairCliente;
    private javax.swing.JButton btnSalvarCliente;
    private javax.swing.JTextField cpf_cliente;
    private javax.swing.JTextField email_cliente;
    private javax.swing.JTextField endereco_cliente;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField nome_cliente;
    private javax.swing.JTable tabelaClientes;
    private javax.swing.JTextField telefone_cliente;
    private javax.swing.JTextField txtPesquisarCliente;
    // End of variables declaration//GEN-END:variables
}
