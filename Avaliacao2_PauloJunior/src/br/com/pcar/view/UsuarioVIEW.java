package br.com.pcar.view;

import br.com.pcar.ctr.UsuarioCTR;
import br.com.pcar.dto.UsuarioDTO;
import java.awt.Dimension;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JPasswordField; 
import java.awt.event.*;

public class UsuarioVIEW extends javax.swing.JInternalFrame {

    private UsuarioCTR usuarioCTR = new UsuarioCTR();
    private UsuarioDTO usuarioDTO = new UsuarioDTO();
    private DefaultTableModel modeloTabela; // Modelo para a JTable

    // Variável para controlar o estado da operação (novo, alterar)
    private String PcarSTATUS_OPERACAO = "";
    private int idUsuarioSelecionado = -1; 

    /**
     * Creates new form UsuarioVIEW
     */
    public UsuarioVIEW(UsuarioDTO usuarioLogadoDTO) { 
        initComponents();
        this.modeloTabela = (DefaultTableModel) tblUsuarios.getModel();
        carregarTiposUsuario();
        limpaCampos();
        liberaCampos(false);
        liberaBotoes(true, false, false, false, true); 
        preencheTabela(""); 
        setPosicao();
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
    private void carregarTiposUsuario() {
        tipoUsuarioComboBox.removeAllItems();
        tipoUsuarioComboBox.addItem("Administrador");
        tipoUsuarioComboBox.addItem("Funcionário");
        tipoUsuarioComboBox.addItem("Cliente");
        // Se for gerenciar clientes como usuários do sistema aqui, adicione "Cliente"
        // tipoUsuarioComboBox.addItem("Cliente"); 
        tipoUsuarioComboBox.setSelectedIndex(-1);
    }

    private void limpaCampos() {
        txtNome.setText("");
        txtCpf.setText("");
        txtLogin.setText("");
        pwdSenha.setText("");
        tipoUsuarioComboBox.setSelectedIndex(-1);
        txtPesquisarNome.setText("");
        idUsuarioSelecionado = -1;
        PcarSTATUS_OPERACAO = "";
        checkAlterarSenha.setSelected(false);
        pwdSenha.setEnabled(false);
        checkAlterarSenha.setEnabled(true);
    }

    private void liberaCampos(boolean liberar) {
        txtNome.setEnabled(liberar);
        txtCpf.setEnabled(liberar);
        txtLogin.setEnabled(liberar);
        pwdSenha.setEnabled(liberar);
        tipoUsuarioComboBox.setEnabled(liberar);
    }

    private void liberaBotoes(boolean novo, boolean salvar, boolean cancelar, boolean excluir, boolean pesquisar) {
        btnNovo.setEnabled(novo);
        btnSalvar.setEnabled(salvar);
        btnCancelar.setEnabled(cancelar);
        btnExcluir.setEnabled(excluir);
        btnPesquisar.setEnabled(pesquisar); // Botão de pesquisa da tabela
    }

    private void preencheCampos(UsuarioDTO dto) {
        idUsuarioSelecionado = dto.getId();
        txtNome.setText(dto.getNome_usuario());
        txtCpf.setText(dto.getCpf_usuario());
        txtLogin.setText(dto.getLogin_usuario());
        // A senha não é preenchida de volta por segurança. Deve ser redefinida se necessário.
        pwdSenha.setText(""); // Limpar o campo senha, ou adicionar um placeholder
        tipoUsuarioComboBox.setSelectedItem(dto.getTipo_usuario());
    }

    private void preencheTabela(String nomeFiltro) {
        modeloTabela.setRowCount(0); // Limpa a tabela
        ResultSet rs;
        UsuarioDTO filtroDTO = new UsuarioDTO();

        int opcaoConsulta;
        if (nomeFiltro.isEmpty()) {
            opcaoConsulta = 3; // Listar todos
        } else {
            opcaoConsulta = 1; // Pesquisar por nome
            filtroDTO.setNome_usuario(nomeFiltro);
        }

        rs = usuarioCTR.consultarUsuario(filtroDTO, opcaoConsulta);

        if (rs != null) {
            try {
                while (rs.next()) {
                    modeloTabela.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nome_usuario"),
                        rs.getString("cpf_usuario"),
                        rs.getString("login_usuario"),
                        rs.getString("tipo_usuario")
                    });
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Erro ao preencher tabela de usuários: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            } finally {
                usuarioCTR.closeDbConnection(); // Fecha a conexão após usar o ResultSet
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
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        btnNovo = new javax.swing.JButton();
        btnSalvar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        btnExcluir = new javax.swing.JButton();
        btnSair = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblUsuarios = new javax.swing.JTable();
        btnPesquisar = new javax.swing.JButton();
        txtNome = new javax.swing.JTextField();
        txtCpf = new javax.swing.JTextField();
        txtLogin = new javax.swing.JTextField();
        tipoUsuarioComboBox = new javax.swing.JComboBox<>();
        checkAlterarSenha = new javax.swing.JCheckBox();
        txtPesquisarNome = new javax.swing.JTextField();
        pwdSenha = new javax.swing.JPasswordField();

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setText("Usuário");

        jLabel2.setText("Nome:");

        jLabel3.setText("CPF:");

        jLabel4.setText("Login:");

        jLabel5.setText("Senha:");

        jLabel6.setText("Tipo:");

        btnNovo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/novo_1.png"))); // NOI18N
        btnNovo.setText("Novo");
        btnNovo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNovoActionPerformed(evt);
            }
        });

        btnSalvar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/salvar_1.png"))); // NOI18N
        btnSalvar.setText("Salvar");
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarActionPerformed(evt);
            }
        });

        btnCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/cancelar_1.png"))); // NOI18N
        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        btnExcluir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/apagar.png"))); // NOI18N
        btnExcluir.setText("Excluir");
        btnExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirActionPerformed(evt);
            }
        });

        btnSair.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/sair_1.png"))); // NOI18N
        btnSair.setText("Sair");
        btnSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel7.setText("Consulta");

        jLabel8.setText("Nome:");

        tblUsuarios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nome", "CPF", "Login", "Tipo"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblUsuarios.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblUsuariosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblUsuarios);

        btnPesquisar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/busca.png"))); // NOI18N
        btnPesquisar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesquisarActionPerformed(evt);
            }
        });

        tipoUsuarioComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ADMINISTRADOR", "FUNCIONARIO", "CLIENTE" }));
        tipoUsuarioComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tipoUsuarioComboBoxActionPerformed(evt);
            }
        });

        checkAlterarSenha.setText("Alterar Senha");
        checkAlterarSenha.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                checkAlterarSenhaMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 405, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel6))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(pwdSenha)
                                        .addGap(18, 18, 18)
                                        .addComponent(checkAlterarSenha))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(tipoUsuarioComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, 409, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtCpf, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(107, 107, 107))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel8)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtPesquisarNome)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 9, Short.MAX_VALUE)))
                                .addContainerGap())))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(127, 127, 127)
                        .addComponent(btnNovo)
                        .addGap(18, 18, 18)
                        .addComponent(btnSalvar)
                        .addGap(18, 18, 18)
                        .addComponent(btnCancelar)
                        .addGap(18, 18, 18)
                        .addComponent(btnExcluir)
                        .addGap(18, 18, 18)
                        .addComponent(btnSair)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(323, 323, 323))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(60, 60, 60)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtCpf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txtLogin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(pwdSenha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(checkAlterarSenha))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tipoUsuarioComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 72, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnNovo)
                            .addComponent(btnSalvar)
                            .addComponent(btnCancelar)
                            .addComponent(btnExcluir)
                            .addComponent(btnSair))
                        .addGap(74, 74, 74))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel8)
                                    .addComponent(txtPesquisarNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(20, 20, 20)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnPesquisar))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnNovoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNovoActionPerformed
        limpaCampos();
        liberaCampos(true);
        liberaBotoes(false, true, true, false, false);
        PcarSTATUS_OPERACAO = "novo";
        txtNome.requestFocus();
    }//GEN-LAST:event_btnNovoActionPerformed

    private void btnPesquisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesquisarActionPerformed
        preencheTabela(txtPesquisarNome.getText());
    }//GEN-LAST:event_btnPesquisarActionPerformed

    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarActionPerformed
        if (txtNome.getText().trim().isEmpty()
                || txtCpf.getText().trim().isEmpty()
                || txtLogin.getText().trim().isEmpty()
                || (PcarSTATUS_OPERACAO.equals("novo") && pwdSenha.getPassword().length == 0)
                || // Senha obrigatória apenas para novo
                tipoUsuarioComboBox.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos obrigatórios!", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }
        usuarioDTO = new UsuarioDTO(); // Novo DTO para salvar
        usuarioDTO.setNome_usuario(txtNome.getText());
        usuarioDTO.setCpf_usuario(txtCpf.getText()); // Adicionar formatação/validação de CPF se necessário
        usuarioDTO.setLogin_usuario(txtLogin.getText());

        if (pwdSenha.getPassword().length > 0) {
            usuarioDTO.setSenha_usuario(new String(pwdSenha.getPassword()));
        } else if (PcarSTATUS_OPERACAO.equals("alterar")) {
            // Se estiver alterando e a senha estiver vazia, não altera a senha
            usuarioDTO.setSenha_usuario(null);
        }

        usuarioDTO.setTipo_usuario(tipoUsuarioComboBox.getSelectedItem().toString());

        String PcarSTATUS_MENSAGEM;
        if (PcarSTATUS_OPERACAO.equals("novo")) {
            PcarSTATUS_MENSAGEM = usuarioCTR.inserirUsuario(usuarioDTO);
        } else { // alterar
            usuarioDTO.setId(idUsuarioSelecionado);
            PcarSTATUS_MENSAGEM = usuarioCTR.alterarUsuario(usuarioDTO);
        }

        JOptionPane.showMessageDialog(this, PcarSTATUS_MENSAGEM, "PcarSTATUS da Operação", JOptionPane.INFORMATION_MESSAGE);

        if (PcarSTATUS_MENSAGEM.toLowerCase().contains("sucesso")) {
            limpaCampos();
            liberaCampos(false);
            liberaBotoes(true, false, false, false, true);
            preencheTabela("");
        }
    }//GEN-LAST:event_btnSalvarActionPerformed

    private void tblUsuariosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblUsuariosMouseClicked
        int linhaSelecionada = tblUsuarios.getSelectedRow();
    if (linhaSelecionada >= 0) {
        UsuarioDTO dtoSelecionado = new UsuarioDTO();
        try {
            dtoSelecionado.setId((Integer) modeloTabela.getValueAt(linhaSelecionada, 0));
            dtoSelecionado.setNome_usuario((String) modeloTabela.getValueAt(linhaSelecionada, 1));
            dtoSelecionado.setCpf_usuario((String) modeloTabela.getValueAt(linhaSelecionada, 2));    // Lendo coluna CPF (índice 2)
            dtoSelecionado.setLogin_usuario((String) modeloTabela.getValueAt(linhaSelecionada, 3)); 
            dtoSelecionado.setTipo_usuario((String) modeloTabela.getValueAt(linhaSelecionada, 4));  

            preencheCampos(dtoSelecionado); // Agora o DTO está completo
            liberaCampos(true); 
            // Sua lógica original de botões:
            liberaBotoes(false, true, true, true, false); // Novo F, Salvar T, Cancelar T, Excluir T, Pesquisar F
                                                                                                                
            PcarSTATUS_OPERACAO = "alterar";

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao ler dados da tabela: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            System.err.println("Erro em tblUsuariosMouseClicked: " + e);
        }
    }
    }//GEN-LAST:event_tblUsuariosMouseClicked

    private void btnExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirActionPerformed
        if (idUsuarioSelecionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário na tabela para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(this, "Deseja realmente excluir o usuário " + txtNome.getText() + "?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (confirmacao == JOptionPane.YES_OPTION) {
            usuarioDTO = new UsuarioDTO();
            usuarioDTO.setId(idUsuarioSelecionado);
            String PcarSTATUS_MENSAGEM = usuarioCTR.excluirUsuario(usuarioDTO);
            JOptionPane.showMessageDialog(this, PcarSTATUS_MENSAGEM, "PcarSTATUS da Exclusão", JOptionPane.INFORMATION_MESSAGE);

            if (PcarSTATUS_MENSAGEM.toLowerCase().contains("sucesso")) {
                limpaCampos();
                liberaCampos(false);
                liberaBotoes(true, false, false, false, true);
                preencheTabela("");
            }

    }//GEN-LAST:event_btnExcluirActionPerformed
    }

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        limpaCampos();
        liberaCampos(false);
        liberaBotoes(true, false, false, false, true);
        tblUsuarios.clearSelection();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSairActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnSairActionPerformed

    private void checkAlterarSenhaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_checkAlterarSenhaMouseClicked
        if (checkAlterarSenha.isSelected()) {
            pwdSenha.setEnabled(true);
        } else {
            pwdSenha.setEnabled(false);
            pwdSenha.setText(null);
        }
    }//GEN-LAST:event_checkAlterarSenhaMouseClicked

    private void tipoUsuarioComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tipoUsuarioComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tipoUsuarioComboBoxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnExcluir;
    private javax.swing.JButton btnNovo;
    private javax.swing.JButton btnPesquisar;
    private javax.swing.JButton btnSair;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JCheckBox checkAlterarSenha;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPasswordField pwdSenha;
    private javax.swing.JTable tblUsuarios;
    private javax.swing.JComboBox<String> tipoUsuarioComboBox;
    private javax.swing.JTextField txtCpf;
    private javax.swing.JTextField txtLogin;
    private javax.swing.JTextField txtNome;
    private javax.swing.JTextField txtPesquisarNome;
    // End of variables declaration//GEN-END:variables
}
