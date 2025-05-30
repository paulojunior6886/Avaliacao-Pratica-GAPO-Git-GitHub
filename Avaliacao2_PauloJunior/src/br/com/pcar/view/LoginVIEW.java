/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.pcar.view;

import javax.swing.JOptionPane;
import br.com.pcar.dto.UsuarioDTO;
import br.com.pcar.ctr.UsuarioCTR;
//import br.com.pcar.dao.ConexaoDAO;

/**
 *
 * @author paulo
 */
public class LoginVIEW extends javax.swing.JFrame {

    UsuarioCTR usuarioCTR = new UsuarioCTR(); 
    //UsuarioDTO usuarioDTO = new UsuarioDTO(); 

    public LoginVIEW() {
        initComponents();
        this.setLocationRelativeTo(null);

    }

    private void logar() {
        String login = login_txt.getText();
        String senha = String.valueOf(senha_txt.getPassword());
        String tipoSelecionadoNoComboBox = tipoUsuarioComboBox.getSelectedItem().toString();

        UsuarioDTO usuarioAutenticadoPeloBanco = usuarioCTR.autenticar(login, senha);

       /* if (usuarioAutenticadoPeloBanco != null && usuarioAutenticadoPeloBanco.getTipo_usuario() != null) {
            // Usuário e senha corretos, agora verificar se o tipo bate com o selecionado
            if (usuarioAutenticadoPeloBanco.getTipo_usuario().equalsIgnoreCase(tipoSelecionadoNoComboBox)) {
                // Tipo do banco confere com o tipo selecionado no ComboBox
                this.dispose(); // Fecha a tela de login

                if (tipoSelecionadoNoComboBox.equalsIgnoreCase("CLIENTE")) {
                    // new CatalogoClienteVIEW(usuarioAutenticadoPeloBanco).setVisible(true); // Descomente quando CatalogoClienteVIEW existir
                    JOptionPane.showMessageDialog(this, "Login como CLIENTE bem-sucedido!\n(CatalogoClienteVIEW ainda não implementada)"); // Placeholder
                } else { // ADMINISTRADOR ou FUNCIONARIO
                    new PrincipalVIEW(usuarioAutenticadoPeloBanco).setVisible(true);
                }
            } else {
                // Login e senha corretos, mas o tipo selecionado não corresponde ao do banco
                JOptionPane.showMessageDialog(this,
                        "Você se autenticou como " + usuarioAutenticadoPeloBanco.getTipo_usuario() +
                        ", mas selecionou o perfil " + tipoSelecionadoNoComboBox + ".\nPor favor, selecione o perfil correto.",
                        "Erro de Perfil", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Login ou senha incorretos!", "Erro de Autenticação", JOptionPane.ERROR_MESSAGE);
        }*/
        if (usuarioAutenticadoPeloBanco != null && usuarioAutenticadoPeloBanco.getTipo_usuario() != null) {
            if (usuarioAutenticadoPeloBanco.getTipo_usuario().equalsIgnoreCase(tipoSelecionadoNoComboBox)) {
                this.dispose(); 

                if (tipoSelecionadoNoComboBox.equalsIgnoreCase("ADMINISTRADOR") || 
                    tipoSelecionadoNoComboBox.equalsIgnoreCase("FUNCIONÁRIO")) { 
                    new PrincipalVIEW(usuarioAutenticadoPeloBanco).setVisible(true);
                } else if (tipoSelecionadoNoComboBox.equalsIgnoreCase("CLIENTE")) {                 
                    new CatalogoClienteVIEW(usuarioAutenticadoPeloBanco).setVisible(true); 
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Você se autenticou como " + usuarioAutenticadoPeloBanco.getTipo_usuario() +
                        ", mas selecionou o perfil " + tipoSelecionadoNoComboBox + ".\nPor favor, selecione o perfil correto.",
                        "Erro de Perfil", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Login ou senha incorretos!", "Erro de Autenticação", JOptionPane.ERROR_MESSAGE);
        }
    
    }

   private boolean verificaPreenchimento() {
        if (login_txt.getText().trim().isEmpty()) { // .isEmpty() é mais comum que .equals("")
            JOptionPane.showMessageDialog(this, "Preencha o campo Login!", "Campo Obrigatório", JOptionPane.WARNING_MESSAGE);
            login_txt.requestFocus();
            return false;
        }
        if (senha_txt.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "Preencha o campo Senha!", "Campo Obrigatório", JOptionPane.WARNING_MESSAGE);
            senha_txt.requestFocus();
            return false;
        }
        if (tipoUsuarioComboBox.getSelectedIndex() == -1) { // Garante que algo foi selecionado
            JOptionPane.showMessageDialog(this, "Selecione o Tipo de Usuário!", "Campo Obrigatório", JOptionPane.WARNING_MESSAGE);
            tipoUsuarioComboBox.requestFocus();
            return false;
        }
        // Adicional: se eu tivesse adicionado o item placeholder como "Selecione..." no índice 0:
        /*
        if (tipoUsuarioComboBox.getSelectedIndex() == 0 && tipoUsuarioComboBox.getSelectedItem().toString().equals("Selecione...")) {
            JOptionPane.showMessageDialog(this, "Selecione o Tipo de Usuário!", "Campo Obrigatório", JOptionPane.WARNING_MESSAGE);
            tipoUsuarioComboBox.requestFocus();
            return false;
        }
        */
        return true;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        senha_txt = new javax.swing.JPasswordField();
        login_txt = new javax.swing.JTextField();
        btnLogar = new javax.swing.JButton();
        bntCancelar = new javax.swing.JButton();
        tipoUsuarioComboBox = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel2.setText("Login:");

        jLabel3.setText("Senha:");

        btnLogar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/logar.png"))); // NOI18N
        btnLogar.setText("Logar");
        btnLogar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogarActionPerformed(evt);
            }
        });

        bntCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/cancelar_1.png"))); // NOI18N
        bntCancelar.setText("Cancelar");
        bntCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bntCancelarActionPerformed(evt);
            }
        });

        tipoUsuarioComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Administrador", "Funcionário", "Cliente" }));

        jLabel4.setText("Tipo:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnLogar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 166, Short.MAX_VALUE)
                        .addComponent(bntCancelar))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tipoUsuarioComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(senha_txt)
                                .addComponent(login_txt, javax.swing.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)))))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(login_txt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(senha_txt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tipoUsuarioComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLogar)
                    .addComponent(bntCancelar))
                .addGap(33, 33, 33))
        );

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setText("Login");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(178, 178, 178))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLogarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogarActionPerformed
        if (verificaPreenchimento()) {
            logar();
        }
    }//GEN-LAST:event_btnLogarActionPerformed

    private void bntCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntCancelarActionPerformed
        System.exit(0);
    }//GEN-LAST:event_bntCancelarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(LoginVIEW.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LoginVIEW.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LoginVIEW.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoginVIEW.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoginVIEW().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bntCancelar;
    private javax.swing.JButton btnLogar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField login_txt;
    private javax.swing.JPasswordField senha_txt;
    private javax.swing.JComboBox<String> tipoUsuarioComboBox;
    // End of variables declaration//GEN-END:variables
}
