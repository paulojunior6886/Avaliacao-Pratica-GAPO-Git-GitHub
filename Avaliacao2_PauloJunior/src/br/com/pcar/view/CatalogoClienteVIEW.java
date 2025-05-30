/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package br.com.pcar.view;

import br.com.pcar.ctr.VeiculoCTR;
import br.com.pcar.dto.PcarDTO;
import br.com.pcar.dto.UsuarioDTO; 
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.io.File;

public class CatalogoClienteVIEW extends javax.swing.JFrame {

    private UsuarioDTO clienteLogadoDTO;
    private VeiculoCTR veiculoCTR = new VeiculoCTR();
    private DefaultTableModel modeloTabelaVeiculos;
    private PcarDTO veiculoSelecionadoParaCompra; 

    public CatalogoClienteVIEW(UsuarioDTO clienteLogado) {
        this.clienteLogadoDTO = clienteLogado;
        initComponents(); 
        
        if (clienteLogadoDTO != null && clienteLogadoDTO.getNome_usuario() != null) {
            this.setTitle("PCar - Catálogo (Bem-vindo(a), " + clienteLogadoDTO.getNome_usuario() + "!)");
        } else {
            this.setTitle("PCar - Catálogo de Veículos");
        }
        setLocationRelativeTo(null); 
        
        modeloTabelaVeiculos = (DefaultTableModel) tblVeiculosCatalogo.getModel();
        carregarVeiculosDisponiveisNaTabela();
        
        limparPainelDetalhes();
        btnComprar.setEnabled(false);
    }

    private void carregarVeiculosDisponiveisNaTabela() {
        modeloTabelaVeiculos.setRowCount(0); 
        PcarDTO dtoConsulta = new PcarDTO();
        ResultSet rs = null; 
        
        
        rs = veiculoCTR.consultarVeiculo(dtoConsulta, 3);

        try {
            if (rs != null) {
                while (rs.next()) {
                    modeloTabelaVeiculos.addRow(new Object[]{
                        rs.getInt("id"), 
                        rs.getString("marca_veiculo"),
                        rs.getString("modelo_veiculo"),
                        rs.getInt("ano_modelo_veiculo"), 
                        rs.getDouble("preco_veiculo")
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar veículos no catálogo: " + e.getMessage());
             JOptionPane.showMessageDialog(this, "Erro ao carregar veículos para o catálogo:\n" + e.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (rs != null) { 
                 veiculoCTR.CloseDB();
            }
        }
    }
    
    private void limparPainelDetalhes() {
        lblValorMarca.setText("-");
        lblValorModelo.setText("-");
        lblValorAno.setText("-");
        lblValorCor.setText("-");
        // lblValorPlaca.setText("-"); // Se adicionar placa aos detalhes
        lblValorPreco.setText("R$ -");
        lblImagemVeiculoDetalhe.setIcon(null);
        lblImagemVeiculoDetalhe.setText("Selecione um veículo na tabela.");
        veiculoSelecionadoParaCompra = null;
    }

    private void exibirDetalhesVeiculoSelecionado() {
        int linhaSelecionada = tblVeiculosCatalogo.getSelectedRow();
        if (linhaSelecionada < 0) {
            limparPainelDetalhes();
            btnComprar.setEnabled(false);
            return;
        }

        int idVeiculo = (Integer) modeloTabelaVeiculos.getValueAt(linhaSelecionada, 0); // ID da coluna 0
        
        PcarDTO dtoConsulta = new PcarDTO();
        dtoConsulta.setId_veiculo(idVeiculo);
        ResultSet rs = null;
        
        try {
            
            rs = veiculoCTR.consultarVeiculo(dtoConsulta, 2); 
            if (rs != null && rs.next()) {
                veiculoSelecionadoParaCompra = new PcarDTO(); 
                veiculoSelecionadoParaCompra.setId_veiculo(rs.getInt("id"));
                veiculoSelecionadoParaCompra.setMarca_veiculo(rs.getString("marca_veiculo"));
                veiculoSelecionadoParaCompra.setModelo_veiculo(rs.getString("modelo_veiculo"));
                veiculoSelecionadoParaCompra.setAno_fabricacao_veiculo(rs.getInt("ano_fabricacao_veiculo")); 
                veiculoSelecionadoParaCompra.setAno_modelo_veiculo(rs.getInt("ano_modelo_veiculo"));
                veiculoSelecionadoParaCompra.setPreco_veiculo(rs.getDouble("preco_veiculo"));
                veiculoSelecionadoParaCompra.setCor_veiculo(rs.getString("cor_veiculo"));
                veiculoSelecionadoParaCompra.setPlaca_veiculo(rs.getString("placa_veiculo"));
                veiculoSelecionadoParaCompra.setTipo_veiculo(rs.getString("tipo_veiculo")); 
                veiculoSelecionadoParaCompra.setCaminho_imagem(rs.getString("caminho_imagem"));

                lblValorMarca.setText(veiculoSelecionadoParaCompra.getMarca_veiculo());
                lblValorModelo.setText(veiculoSelecionadoParaCompra.getModelo_veiculo());
                lblValorAno.setText(String.valueOf(veiculoSelecionadoParaCompra.getAno_modelo_veiculo()));
                lblValorCor.setText(veiculoSelecionadoParaCompra.getCor_veiculo());
                // lblValorPlaca.setText(veiculoSelecionadoParaCompra.getPlaca_veiculo()); // Se adicionar
                lblValorPreco.setText("R$ " + String.format("%,.2f", veiculoSelecionadoParaCompra.getPreco_veiculo())); 
                
                btnComprar.setEnabled(true);

                String caminhoImagem = veiculoSelecionadoParaCompra.getCaminho_imagem();
                if (caminhoImagem != null && !caminhoImagem.trim().isEmpty()) {
                    File arquivoImagem = new File(caminhoImagem);
                    if (arquivoImagem.exists() && !arquivoImagem.isDirectory()) {
                        ImageIcon icon = new ImageIcon(caminhoImagem);
                        Image img = icon.getImage();
                        
                        int lblWidth = lblImagemVeiculoDetalhe.getWidth();
                        int lblHeight = lblImagemVeiculoDetalhe.getHeight();
                                            
                        if (lblWidth <= 0) lblWidth = 250; 
                        if (lblHeight <= 0) lblHeight = 180;

                        Image imgRedimensionada = img.getScaledInstance(lblWidth, lblHeight, Image.SCALE_SMOOTH);
                        lblImagemVeiculoDetalhe.setIcon(new ImageIcon(imgRedimensionada));
                        lblImagemVeiculoDetalhe.setText(""); 
                    } else {
                        lblImagemVeiculoDetalhe.setIcon(null);
                        lblImagemVeiculoDetalhe.setText("Imagem não encontrada");
                    }
                } else {
                    lblImagemVeiculoDetalhe.setIcon(null);
                    lblImagemVeiculoDetalhe.setText("Imagem não disponível");
                }
            } else {
                limparPainelDetalhes();
                btnComprar.setEnabled(false);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao exibir detalhes do veículo: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Erro ao carregar detalhes do veículo:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            limparPainelDetalhes();
            btnComprar.setEnabled(false);
        } finally {
            if (rs != null) {
                veiculoCTR.CloseDB();
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
        jPanel1 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblVeiculosCatalogo = new javax.swing.JTable();
        pnlDetalhesVeiculo = new javax.swing.JPanel();
        lblImagemVeiculoDetalhe = new javax.swing.JLabel();
        l1 = new javax.swing.JLabel();
        l2 = new javax.swing.JLabel();
        l3 = new javax.swing.JLabel();
        l4 = new javax.swing.JLabel();
        l5 = new javax.swing.JLabel();
        lblValorMarca = new javax.swing.JLabel();
        lblValorModelo = new javax.swing.JLabel();
        lblValorAno = new javax.swing.JLabel();
        lblValorCor = new javax.swing.JLabel();
        lblValorPreco = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        btnComprar = new javax.swing.JButton();
        btnMinhasCompras = new javax.swing.JButton();
        btnSairCatalogo = new javax.swing.JButton();

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("PCar - Catálogo de Veículos");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        tblVeiculosCatalogo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Marca", "Modelo", "Ano", "Preço (R$)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tblVeiculosCatalogo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblVeiculosCatalogoMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tblVeiculosCatalogo);

        jScrollPane4.setViewportView(jScrollPane3);

        lblImagemVeiculoDetalhe.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, java.awt.Color.black, null, null));
        lblImagemVeiculoDetalhe.setPreferredSize(new java.awt.Dimension(250, 180));

        l1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        l1.setText("Marca:");

        l2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        l2.setText("Modelo:");

        l3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        l3.setText("Ano:");

        l4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        l4.setText("Cor:");

        l5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        l5.setText("Preço:");

        javax.swing.GroupLayout pnlDetalhesVeiculoLayout = new javax.swing.GroupLayout(pnlDetalhesVeiculo);
        pnlDetalhesVeiculo.setLayout(pnlDetalhesVeiculoLayout);
        pnlDetalhesVeiculoLayout.setHorizontalGroup(
            pnlDetalhesVeiculoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDetalhesVeiculoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblImagemVeiculoDetalhe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlDetalhesVeiculoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDetalhesVeiculoLayout.createSequentialGroup()
                        .addComponent(l1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblValorMarca, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnlDetalhesVeiculoLayout.createSequentialGroup()
                        .addComponent(l2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblValorModelo, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE))
                    .addGroup(pnlDetalhesVeiculoLayout.createSequentialGroup()
                        .addComponent(l3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblValorAno, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnlDetalhesVeiculoLayout.createSequentialGroup()
                        .addComponent(l5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblValorPreco, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnlDetalhesVeiculoLayout.createSequentialGroup()
                        .addComponent(l4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblValorCor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlDetalhesVeiculoLayout.setVerticalGroup(
            pnlDetalhesVeiculoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDetalhesVeiculoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDetalhesVeiculoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDetalhesVeiculoLayout.createSequentialGroup()
                        .addGroup(pnlDetalhesVeiculoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(l1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblValorMarca, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlDetalhesVeiculoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(l2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblValorModelo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlDetalhesVeiculoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(l3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblValorAno, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlDetalhesVeiculoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(l4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblValorCor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlDetalhesVeiculoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(l5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblValorPreco, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(lblImagemVeiculoDetalhe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnComprar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/compra.png"))); // NOI18N
        btnComprar.setText("Comprar Veículo");
        btnComprar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnComprarActionPerformed(evt);
            }
        });

        btnMinhasCompras.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/comprar.png"))); // NOI18N
        btnMinhasCompras.setText("Minhas Compras");
        btnMinhasCompras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMinhasComprasActionPerformed(evt);
            }
        });

        btnSairCatalogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/sair_1.png"))); // NOI18N
        btnSairCatalogo.setText("Sair");
        btnSairCatalogo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairCatalogoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(btnComprar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMinhasCompras)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSairCatalogo)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnComprar)
                    .addComponent(btnMinhasCompras)
                    .addComponent(btnSairCatalogo))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(302, 302, 302)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane4))
                        .addGap(18, 18, 18)
                        .addComponent(pnlDetalhesVeiculo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(13, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(jLabel1)
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pnlDetalhesVeiculo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(74, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblVeiculosCatalogoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVeiculosCatalogoMouseClicked
        exibirDetalhesVeiculoSelecionado();
    }//GEN-LAST:event_tblVeiculosCatalogoMouseClicked

    private void btnComprarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnComprarActionPerformed
         if (veiculoSelecionadoParaCompra != null && veiculoSelecionadoParaCompra.getId_veiculo() > 0) {
            // Abre a tela CompraVIEW como um diálogo modal
            CompraVIEW compraView = new CompraVIEW(this, true, clienteLogadoDTO, veiculoSelecionadoParaCompra);
            compraView.setVisible(true); // Exibe o diálogo modal
            
            carregarVeiculosDisponiveisNaTabela();
            limparPainelDetalhes();
            btnComprar.setEnabled(false);

        } else {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um veículo na tabela antes de prosseguir com a compra.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnComprarActionPerformed

    private void btnSairCatalogoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSairCatalogoActionPerformed
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Deseja realmente sair e fazer logout?", 
                "Confirmação de Saída", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose(); 
            new LoginVIEW().setVisible(true); 
        }
    }//GEN-LAST:event_btnSairCatalogoActionPerformed

    private void btnMinhasComprasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMinhasComprasActionPerformed
         // Abre a tela MinhasComprasVIEW como um diálogo modal
        MinhasComprasVIEW minhasComprasView = new MinhasComprasVIEW(this, true, clienteLogadoDTO);
        minhasComprasView.setVisible(true);
    }//GEN-LAST:event_btnMinhasComprasActionPerformed


    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnComprar;
    private javax.swing.JButton btnMinhasCompras;
    private javax.swing.JButton btnSairCatalogo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel l1;
    private javax.swing.JLabel l2;
    private javax.swing.JLabel l3;
    private javax.swing.JLabel l4;
    private javax.swing.JLabel l5;
    private javax.swing.JLabel lblImagemVeiculoDetalhe;
    private javax.swing.JLabel lblValorAno;
    private javax.swing.JLabel lblValorCor;
    private javax.swing.JLabel lblValorMarca;
    private javax.swing.JLabel lblValorModelo;
    private javax.swing.JLabel lblValorPreco;
    private javax.swing.JPanel pnlDetalhesVeiculo;
    private javax.swing.JTable tblVeiculosCatalogo;
    // End of variables declaration//GEN-END:variables
}
