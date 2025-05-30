package br.com.pcar.view;

import javax.swing.JOptionPane;
import java.awt.Dimension;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import br.com.pcar.dto.PcarDTO; //
import br.com.pcar.ctr.VendaCTR; //
// ClienteCTR e VeiculoCTR são usados pela VendaCTR para popular os ComboBoxes.
// Não precisam ser instanciados diretamente aqui se a VendaCTR já os utiliza.
// No entanto, sua VendaCTR atual usa ClienteCTR e VeiculoCTR, então manteremos as instâncias por ora.
import br.com.pcar.ctr.ClienteCTR;
import br.com.pcar.ctr.VeiculoCTR; 
import java.text.SimpleDateFormat; // Para formatação de data
import java.util.Date; // Para a data atual, se necessário

public class VendaVIEW extends javax.swing.JInternalFrame {

    PcarDTO vendaDTO = new PcarDTO(); //
    VendaCTR vendaCTR = new VendaCTR(); //
    // ClienteCTR clienteCTR_paraCombo = new ClienteCTR(); // Usado por vendaCTR.consultarTodosClientesParaVenda()
    // VeiculoCTR veiculoCTR_paraCombo = new VeiculoCTR(); // Usado por vendaCTR.consultarTodosVeiculosParaVenda()


    int gravar_alterar; // 1 para gravar (novo), 2 para alterar
    DefaultTableModel modelo_tabela_vendas;

    // Classe interna para itens do ComboBox que armazenam ID e Texto
    private static class ComboBoxItem {
        private int id;
        private String texto;

        public ComboBoxItem(int id, String texto) {
            this.id = id;
            this.texto = texto;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return texto; // O que será exibido no ComboBox
        }
    }


    public VendaVIEW() {
        initComponents();
        modelo_tabela_vendas = (DefaultTableModel) tabelaVendas.getModel();
        
        setPosicao(); 
        configuraEstadoInicialComponentes(); // Configura botões e campos
        
        carregarClientesComboBox();
        carregarVeiculosComboBox();
        
        listarTodasVendas(); // Carrega vendas ao iniciar
    }
    
   public void setPosicao() {
    // Usa invokeLater para garantir que a lógica execute na Event Dispatch Thread (EDT)
    // e após outros eventos de UI pendentes, dando tempo para o JDesktopPane estar pronto.
    javax.swing.SwingUtilities.invokeLater(() -> {
        if (getDesktopPane() != null) {
            Dimension desktopSize = getDesktopPane().getSize();
            Dimension frameSize = getSize(); // Pega o tamanho atual do JInternalFrame

            // Se o frameSize ainda não foi definido (ex: pack() não foi chamado ou
            // o frame não foi totalmente processado), ele pode ser (0,0).
            // Nesse caso, usar o preferredSize pode ser uma alternativa, mas o ideal
            // é que pack() seja chamado na PrincipalVIEW antes de setVisible(true).
            if (frameSize.width == 0 || frameSize.height == 0) {
                // System.out.println("Aviso (setPosicao): frameSize é (0,0), usando getPreferredSize(). Chame pack() na PrincipalVIEW.");
                frameSize = getPreferredSize();
            }

            if (desktopSize.width > 0 && desktopSize.height > 0 && 
                frameSize.width > 0 && frameSize.height > 0) {
                
                int x = (desktopSize.width - frameSize.width) / 2;
                int y = (desktopSize.height - frameSize.height) / 2;

                // Garante que as coordenadas não sejam negativas (caso o frame seja maior que o desktop)
                setLocation(Math.max(0, x), Math.max(0, y));
            } else {
                // Se ainda assim as dimensões não forem válidas, pode haver um problema mais fundamental
                // ou a tela está sendo aberta antes do desktop pane estar pronto.
                // System.err.println("Não foi possível centralizar VendaVIEW: Dimensões inválidas. Desktop: " + desktopSize + ", Frame: " + frameSize);
                 // Como fallback, você pode definir uma posição padrão pequena, ex: setLocation(10,10);
            }
        } else {
            // System.err.println("Não foi possível centralizar VendaVIEW: getDesktopPane() é null.");
        }
    });
}

    private void habilitarDesabilitarCampos(boolean habilitar) {
        cliente_venda.setEnabled(habilitar);
        veiculo_venda.setEnabled(habilitar);
        status_venda.setEnabled(habilitar);
        data_venda.setEnabled(habilitar);
        valor_venda.setEnabled(habilitar);
    }

    private void limparCamposFormulario() {
        if (cliente_venda.getItemCount() > 0) cliente_venda.setSelectedIndex(0); else cliente_venda.setSelectedIndex(-1);
        if (veiculo_venda.getItemCount() > 0) veiculo_venda.setSelectedIndex(0); else veiculo_venda.setSelectedIndex(-1);
        if (status_venda.getItemCount() > 0) status_venda.setSelectedIndex(0); else status_venda.setSelectedIndex(-1);
        
        // Define a data atual como padrão para novas vendas
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        data_venda.setText(sdf.format(new Date()));
        
        valor_venda.setText("");
        
        this.vendaDTO = new PcarDTO(); // Limpa o DTO da classe
        if (data_venda.isEnabled()) { // Só foca se estiver habilitado
             data_venda.requestFocusInWindow();
        }
    }

    // Botões da sua UI: btnNovaVenda, btnSalvarVenda, btnEditarVenda, btnExcluirVenda, btnCancelarVenda, btnBuscarVenda
    private void configuraEstadoInicialComponentes() {
        limparCamposFormulario();
        habilitarDesabilitarCampos(false);
        
        btnNovaVenda.setEnabled(true);
        btnSalvarVenda.setEnabled(false);
        btnEditarVenda.setEnabled(false); 
        btnExcluirVenda.setEnabled(false); 
        btnCancelarVenda.setEnabled(false); // "Cancelar" só faz sentido durante uma operação
        btnBuscarVenda.setEnabled(true);
        
        tabelaVendas.setEnabled(true);
        tabelaVendas.clearSelection();
        gravar_alterar = 0; 
    }

    private void configuraComponentesParaNovoOuEditar() {
        habilitarDesabilitarCampos(true);
        
        btnNovaVenda.setEnabled(false);
        btnSalvarVenda.setEnabled(true);
        btnEditarVenda.setEnabled(false); 
        btnExcluirVenda.setEnabled(gravar_alterar == 2); 
        btnCancelarVenda.setEnabled(true); 
        btnBuscarVenda.setEnabled(false); 
        
        tabelaVendas.setEnabled(false); 
    }

    private void carregarClientesComboBox() {
        ResultSet rs = null;
        try {
            cliente_venda.removeAllItems();
            cliente_venda.addItem(new ComboBoxItem(0, "Selecione um Cliente...")); // Item placeholder

            // Usa o método da VendaCTR que chama ClienteCTR
            rs = vendaCTR.consultarTodosClientesParaVenda(); //

            if (rs != null) {
                while (rs.next()) {
                    cliente_venda.addItem(new ComboBoxItem(rs.getInt("id"), rs.getString("nome_cliente")));
                }
            } else {
                 System.err.println("ResultSet nulo ao carregar clientes para ComboBox.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar clientes no ComboBox: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Erro ao carregar lista de clientes:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (rs != null) {
                vendaCTR.CloseDB(); // Fecha conexão após usar o ResultSet vindo de VendaCTR -> ClienteCTR
            }
        }
    }

    private void carregarVeiculosComboBox() {
        ResultSet rs = null;
        try {
            veiculo_venda.removeAllItems();
            veiculo_venda.addItem(new ComboBoxItem(0, "Selecione um Veículo...")); // Item placeholder

            // Usa o método da VendaCTR que chama VeiculoCTR
            rs = vendaCTR.consultarTodosVeiculosParaVenda(); //

            if (rs != null) {
                while (rs.next()) {
                    // Ex: "Uno (ABC-1234) - R$ 25000.00"
                    String displayText = rs.getString("modelo_veiculo") + 
                                         " (" + rs.getString("placa_veiculo") + ")" +
                                         " - R$ " + String.format("%.2f", rs.getDouble("preco_veiculo"));
                    veiculo_venda.addItem(new ComboBoxItem(rs.getInt("id"), displayText));
                }
            } else {
                System.err.println("ResultSet nulo ao carregar veículos para ComboBox.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar veículos no ComboBox: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Erro ao carregar lista de veículos:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } finally {
             if (rs != null) {
                vendaCTR.CloseDB(); // Fecha conexão após usar o ResultSet vindo de VendaCTR -> VeiculoCTR
            }
        }
    }

    private void gravarNovaVenda() {
        try {
            if (cliente_venda.getSelectedIndex() <= 0 || veiculo_venda.getSelectedIndex() <= 0 ||
                data_venda.getText().trim().isEmpty() || valor_venda.getText().trim().isEmpty() ||
                status_venda.getSelectedIndex() == -1 ) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos obrigatórios (Cliente, Veículo, Data, Valor, Status)!", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return;
            }

            PcarDTO dtoParaGravar = new PcarDTO(); //
            
            ComboBoxItem clienteSelecionado = (ComboBoxItem) cliente_venda.getSelectedItem();
            ComboBoxItem veiculoSelecionado = (ComboBoxItem) veiculo_venda.getSelectedItem();

            dtoParaGravar.setId_cliente(clienteSelecionado.getId()); // PEGA O ID
            dtoParaGravar.setId_veiculo(veiculoSelecionado.getId()); // PEGA O ID
            dtoParaGravar.setData_venda(data_venda.getText()); // Validar formato YYYY-MM-DD
            dtoParaGravar.setValor_venda(Double.parseDouble(valor_venda.getText().replace(",", "."))); //
            dtoParaGravar.setStatus_venda(status_venda.getSelectedItem().toString()); //
            
            String PcarSTATUS = vendaCTR.inserirVenda(dtoParaGravar); //
            JOptionPane.showMessageDialog(this, PcarSTATUS, "Cadastro de Venda", JOptionPane.INFORMATION_MESSAGE);
            
            if (PcarSTATUS.toLowerCase().contains("sucesso")) {
                listarTodasVendas();
                configuraEstadoInicialComponentes();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Erro de Formato: Verifique o campo Valor. Use ponto para decimais.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            System.err.println("Erro ao Gravar Venda: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Erro ao Gravar Venda:\n" + e.getMessage(), "Erro Crítico", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void alterarVendaExistente() {
        try {
            if (this.vendaDTO.getId_venda() == 0) { //
                JOptionPane.showMessageDialog(this, "Nenhuma venda selecionada ou ID inválido para alteração!", "Erro de Alteração", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (cliente_venda.getSelectedIndex() <= 0 || veiculo_venda.getSelectedIndex() <= 0 ||
                data_venda.getText().trim().isEmpty() || valor_venda.getText().trim().isEmpty() ||
                status_venda.getSelectedIndex() == -1 ) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos obrigatórios!", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ComboBoxItem clienteSelecionado = (ComboBoxItem) cliente_venda.getSelectedItem();
            ComboBoxItem veiculoSelecionado = (ComboBoxItem) veiculo_venda.getSelectedItem();

            // Usa o vendaDTO da classe, que já tem o ID da venda.
            this.vendaDTO.setId_cliente(clienteSelecionado.getId()); //
            this.vendaDTO.setId_veiculo(veiculoSelecionado.getId()); //
            this.vendaDTO.setData_venda(data_venda.getText()); //
            this.vendaDTO.setValor_venda(Double.parseDouble(valor_venda.getText().replace(",", "."))); //
            this.vendaDTO.setStatus_venda(status_venda.getSelectedItem().toString()); //

            String PcarSTATUS = vendaCTR.alterarVenda(this.vendaDTO); //
            JOptionPane.showMessageDialog(this, PcarSTATUS, "Alteração de Venda", JOptionPane.INFORMATION_MESSAGE);

            if (PcarSTATUS.toLowerCase().contains("sucesso")) {
                listarTodasVendas();
                configuraEstadoInicialComponentes();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Erro de Formato: Verifique o campo Valor. Use ponto para decimais.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            System.err.println("Erro ao Alterar Venda: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Erro ao Alterar Venda:\n" + e.getMessage(), "Erro Crítico", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirVendaSelecionada() {
        if (this.vendaDTO.getId_venda() == 0) { //
            JOptionPane.showMessageDialog(this, "Nenhuma venda selecionada para exclusão.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String nomeClienteParaExibicao = "";
        if(cliente_venda.getSelectedIndex() > 0 && cliente_venda.getSelectedItem() instanceof ComboBoxItem){
             nomeClienteParaExibicao = ((ComboBoxItem)cliente_venda.getSelectedItem()).toString();
        } else if (tabelaVendas.getSelectedRow() >=0){
            nomeClienteParaExibicao = tabelaVendas.getValueAt(tabelaVendas.getSelectedRow(), 1).toString();
        }


        if (JOptionPane.showConfirmDialog(this, "Deseja realmente excluir a Venda para o cliente " + nomeClienteParaExibicao + "?", "Confirmação de Exclusão",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
            
            String PcarSTATUS = vendaCTR.excluirVenda(this.vendaDTO); //
            JOptionPane.showMessageDialog(this, PcarSTATUS, "Exclusão de Venda", JOptionPane.INFORMATION_MESSAGE);

            if (PcarSTATUS.toLowerCase().contains("sucesso")) {
                listarTodasVendas();
                configuraEstadoInicialComponentes();
            }
        }
    }

    private void listarTodasVendas() {
        carregarVendasNaTabela("");
    }
    
    private void carregarVendasNaTabela(String nomeClienteFiltro) {
        modelo_tabela_vendas.setRowCount(0);
        PcarDTO dtoConsulta = new PcarDTO(); //
        ResultSet rs = null;
        int opcao;

        if (nomeClienteFiltro.trim().isEmpty()) {
            opcao = 3; // Listar todas
        } else {
            opcao = 1; // Pesquisar por nome do cliente
            dtoConsulta.setNome_cliente(nomeClienteFiltro); //
        }
        
        rs = vendaCTR.consultarVenda(dtoConsulta, opcao); //

        try {
            if (rs != null) {
                while (rs.next()) {
                    modelo_tabela_vendas.addRow(new Object[]{
                        rs.getInt("id"), 
                        rs.getString("nome_cliente"), 
                        rs.getString("modelo_veiculo") + " (" + rs.getString("placa_veiculo") + ")", 
                        rs.getString("status_venda"),
                        rs.getString("data_venda"), 
                        rs.getDouble("valor_venda") 
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao preencher tabela de vendas: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Erro ao carregar vendas:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } finally {
             if (rs != null) {
                vendaCTR.CloseDB(); //
            }
        }
    }

    private void carregarVendaSelecionadaParaCampos(int id_venda_param) {
        PcarDTO dtoConsulta = new PcarDTO(); //
        dtoConsulta.setId_venda(id_venda_param); //
        ResultSet rs = null;
        
        try {
            rs = vendaCTR.consultarVenda(dtoConsulta, 2); // Opção 2 para buscar por ID da venda
            if (rs != null && rs.next()) {
                limparCamposFormulario(); 

                // Atualiza o vendaDTO da classe
                this.vendaDTO.setId_venda(rs.getInt("id")); //
                // Para preencher os ComboBoxes, precisamos encontrar os IDs correspondentes
                // ou armazenar nome/modelo e depois encontrar o ComboBoxItem.
                // Por simplicidade, vamos setar os IDs e a VIEW se encarrega de selecionar o ComboBoxItem correto.
                
                // A consulta de venda já retorna nome_cliente e modelo_veiculo
                String nomeClienteDB = rs.getString("nome_cliente");
                String modeloVeiculoDB = rs.getString("modelo_veiculo"); // Poderia ser ID_veiculo se a consulta retornasse
                                                                         // e o ComboBoxItem do veículo armazenasse o ID.
                
                // Seleciona o cliente no ComboBox
                for (int i = 0; i < cliente_venda.getItemCount(); i++) {
                    Object item = cliente_venda.getItemAt(i);
                    if (item instanceof ComboBoxItem && ((ComboBoxItem) item).toString().equals(nomeClienteDB)) {
                        cliente_venda.setSelectedIndex(i);
                        this.vendaDTO.setId_cliente(((ComboBoxItem)item).getId()); // Guarda o ID do cliente
                        break;
                    } else if (item instanceof String && item.equals(nomeClienteDB)) { // Fallback se não for ComboBoxItem
                        cliente_venda.setSelectedIndex(i);
                        // ID do cliente não seria recuperado neste fallback, precisaria de nova consulta ou ajuste
                        break;
                    }
                }
                
                // Seleciona o veículo no ComboBox (comparando parte do texto, pois o ComboBoxItem de veículo tem mais info)
                for (int i = 0; i < veiculo_venda.getItemCount(); i++) {
                     Object item = veiculo_venda.getItemAt(i);
                    if (item instanceof ComboBoxItem && ((ComboBoxItem) item).toString().contains(modeloVeiculoDB)) {
                        veiculo_venda.setSelectedIndex(i);
                         this.vendaDTO.setId_veiculo(((ComboBoxItem)item).getId()); // Guarda o ID do veículo
                        break;
                    } else if (item instanceof String && item.toString().contains(modeloVeiculoDB)){
                        veiculo_venda.setSelectedIndex(i);
                        break;
                    }
                }
                
                status_venda.setSelectedItem(rs.getString("status_venda"));
                data_venda.setText(rs.getString("data_venda"));
                valor_venda.setText(String.valueOf(rs.getDouble("valor_venda")));
                
                habilitarDesabilitarCampos(false); // Mantém campos bloqueados após selecionar
                btnNovaVenda.setEnabled(true);
                btnSalvarVenda.setEnabled(false);
                btnEditarVenda.setEnabled(true);
                btnExcluirVenda.setEnabled(true);
                btnCancelarVenda.setEnabled(true); // Para cancelar a visualização/edição
                btnBuscarVenda.setEnabled(true);
                gravar_alterar = 0; 
            } else {
                 JOptionPane.showMessageDialog(this, "Venda não encontrada.", "Aviso", JOptionPane.WARNING_MESSAGE);
                 configuraEstadoInicialComponentes();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar dados da venda nos campos: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados da venda:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            configuraEstadoInicialComponentes();
        } finally {
            if (rs != null) {
                vendaCTR.CloseDB(); //
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
        cliente_venda = new javax.swing.JComboBox<>();
        veiculo_venda = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        status_venda = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        data_venda = new javax.swing.JTextField();
        valor_venda = new javax.swing.JTextField();
        btnSalvarVenda = new javax.swing.JButton();
        btnCancelarVenda = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        txtPesquisaVenda = new javax.swing.JTextField();
        btnBuscarVenda = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabelaVendas = new javax.swing.JTable();
        btnSairVendas = new javax.swing.JButton();
        btnNovaVenda = new javax.swing.JButton();
        btnExcluirVenda = new javax.swing.JButton();
        btnEditarVenda = new javax.swing.JButton();

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        jLabel1.setText("Vendas");

        jLabel2.setText("Lista de Clientes:");

        cliente_venda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cliente_vendaActionPerformed(evt);
            }
        });

        veiculo_venda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                veiculo_vendaActionPerformed(evt);
            }
        });

        jLabel3.setText("Lista de Veículos:");

        jLabel4.setText("Status Venda:");

        status_venda.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pendente", "Finalizada", "Cancelada" }));
        status_venda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                status_vendaActionPerformed(evt);
            }
        });

        jLabel5.setText("Data da Venda:");

        jLabel6.setText("Valor da Venda:");

        data_venda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                data_vendaActionPerformed(evt);
            }
        });

        valor_venda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                valor_vendaActionPerformed(evt);
            }
        });

        btnSalvarVenda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/salvar_1.png"))); // NOI18N
        btnSalvarVenda.setText("Salvar");
        btnSalvarVenda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarVendaActionPerformed(evt);
            }
        });

        btnCancelarVenda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/cancelar_1.png"))); // NOI18N
        btnCancelarVenda.setText("Cancelar");
        btnCancelarVenda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarVendaActionPerformed(evt);
            }
        });

        jLabel7.setText("Pesquisar Venda:");

        txtPesquisaVenda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPesquisaVendaActionPerformed(evt);
            }
        });

        btnBuscarVenda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/busca.png"))); // NOI18N
        btnBuscarVenda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarVendaActionPerformed(evt);
            }
        });

        tabelaVendas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Cliente", "Veículo", "Status", "Data ", "Valor"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabelaVendas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelaVendasMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tabelaVendas);

        btnSairVendas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/sair_1.png"))); // NOI18N
        btnSairVendas.setText("Sair");
        btnSairVendas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairVendasActionPerformed(evt);
            }
        });

        btnNovaVenda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/novo_1.png"))); // NOI18N
        btnNovaVenda.setText("Novo");
        btnNovaVenda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNovaVendaActionPerformed(evt);
            }
        });

        btnExcluirVenda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/apagar.png"))); // NOI18N
        btnExcluirVenda.setText("Excluir");
        btnExcluirVenda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirVendaActionPerformed(evt);
            }
        });

        btnEditarVenda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/editar.png"))); // NOI18N
        btnEditarVenda.setText("Editar");
        btnEditarVenda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarVendaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnSalvarVenda)
                                .addGap(18, 18, 18)
                                .addComponent(btnCancelarVenda)
                                .addGap(18, 18, 18)
                                .addComponent(btnNovaVenda)
                                .addGap(18, 18, 18)
                                .addComponent(btnExcluirVenda)
                                .addGap(18, 18, 18)
                                .addComponent(btnEditarVenda)
                                .addGap(18, 18, 18)
                                .addComponent(btnSairVendas))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cliente_venda, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(veiculo_venda, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(status_venda, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(data_venda, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(valor_venda, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtPesquisaVenda, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnBuscarVenda, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(355, 355, 355)
                        .addComponent(jLabel1)))
                .addContainerGap(54, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(cliente_venda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(veiculo_venda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(status_venda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)
                        .addComponent(data_venda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(valor_venda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSalvarVenda)
                    .addComponent(btnCancelarVenda)
                    .addComponent(btnNovaVenda)
                    .addComponent(btnExcluirVenda)
                    .addComponent(btnEditarVenda)
                    .addComponent(btnSairVendas))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(txtPesquisaVenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(btnBuscarVenda)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(39, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSalvarVendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarVendaActionPerformed
        if (gravar_alterar == 1) { 
            gravarNovaVenda();
        } else if (gravar_alterar == 2) { 
            alterarVendaExistente();
        } else {
            JOptionPane.showMessageDialog(this, "Ação indefinida. Clique em 'Nova Venda' ou selecione uma venda para 'Editar'.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnSalvarVendaActionPerformed

    private void btnCancelarVendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarVendaActionPerformed
        configuraEstadoInicialComponentes();
    }//GEN-LAST:event_btnCancelarVendaActionPerformed

    private void btnBuscarVendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarVendaActionPerformed
        carregarVendasNaTabela(txtPesquisaVenda.getText().trim());
    }//GEN-LAST:event_btnBuscarVendaActionPerformed

    private void btnSairVendasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSairVendasActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnSairVendasActionPerformed

    private void btnExcluirVendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirVendaActionPerformed
        if (tabelaVendas.getSelectedRow() < 0 && this.vendaDTO.getId_venda() <= 0) { //
             JOptionPane.showMessageDialog(this, "Selecione uma venda na tabela para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // O this.vendaDTO já foi populado com o ID no clique da tabela.
        excluirVendaSelecionada();
    }//GEN-LAST:event_btnExcluirVendaActionPerformed

    private void btnNovaVendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNovaVendaActionPerformed
        this.vendaDTO = new PcarDTO(); // Limpa DTO para nova venda
        limparCamposFormulario(); // Limpa e define data atual
        habilitarDesabilitarCampos(true); 
        configuraComponentesParaNovoOuEditar(); 
        gravar_alterar = 1; 
        btnExcluirVenda.setEnabled(false); 
        data_venda.requestFocusInWindow();
    }//GEN-LAST:event_btnNovaVendaActionPerformed

    private void btnEditarVendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarVendaActionPerformed
         int linhaSelecionada = tabelaVendas.getSelectedRow();
        if (linhaSelecionada < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma venda na tabela para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Os dados da venda já foram carregados no this.vendaDTO e nos campos pelo clique na tabela.
        gravar_alterar = 2; 
        habilitarDesabilitarCampos(true); 
        configuraComponentesParaNovoOuEditar(); 
        data_venda.requestFocusInWindow();
    }//GEN-LAST:event_btnEditarVendaActionPerformed

    private void tabelaVendasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelaVendasMouseClicked
        int linhaSelecionada = tabelaVendas.getSelectedRow();
        if (linhaSelecionada >= 0) {
            int idVendaSelecionada = (int) modelo_tabela_vendas.getValueAt(linhaSelecionada, 0);
            carregarVendaSelecionadaParaCampos(idVendaSelecionada); 
        
        }
    }//GEN-LAST:event_tabelaVendasMouseClicked

    private void cliente_vendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cliente_vendaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cliente_vendaActionPerformed

    private void veiculo_vendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_veiculo_vendaActionPerformed
         // Lógica adicional, como auto-preencher o valor da venda com o preço do veículo
        Object itemSelecionado = veiculo_venda.getSelectedItem();
        if(itemSelecionado instanceof ComboBoxItem && ((ComboBoxItem)itemSelecionado).getId() != 0){
            // Para pegar o preço, precisaríamos buscar o veículo pelo ID ou tê-lo no ComboBoxItem
            // Por simplicidade, vamos assumir que o usuário digita o valor por enquanto.
            // Se quiser auto-preencher, precisaria de um método em VeiculoCTR para buscar por ID
            // e retornar o preço.
            // Exemplo:
            // ComboBoxItem veiculoItem = (ComboBoxItem) itemSelecionado;
            // PcarDTO veiculoDetalhe = veiculoCTR.buscarVeiculoPorId(veiculoItem.getId()); // Método hipotético
            // if(veiculoDetalhe != null) {
            //     valor_venda.setText(String.valueOf(veiculoDetalhe.getPreco_veiculo()));
            // }
        } else if (veiculo_venda.getSelectedIndex() == 0) { // Se "Selecione..." for escolhido
            valor_venda.setText(""); // Limpa o valor
        }
    }//GEN-LAST:event_veiculo_vendaActionPerformed

    private void status_vendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_status_vendaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_status_vendaActionPerformed

    private void data_vendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_data_vendaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_data_vendaActionPerformed

    private void valor_vendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_valor_vendaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_valor_vendaActionPerformed

    private void txtPesquisaVendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPesquisaVendaActionPerformed
        btnBuscarVendaActionPerformed(evt);
    }//GEN-LAST:event_txtPesquisaVendaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscarVenda;
    private javax.swing.JButton btnCancelarVenda;
    private javax.swing.JButton btnEditarVenda;
    private javax.swing.JButton btnExcluirVenda;
    private javax.swing.JButton btnNovaVenda;
    private javax.swing.JButton btnSairVendas;
    private javax.swing.JButton btnSalvarVenda;
    private javax.swing.JComboBox<ComboBoxItem> cliente_venda;
    private javax.swing.JTextField data_venda;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox<String> status_venda;
    private javax.swing.JTable tabelaVendas;
    private javax.swing.JTextField txtPesquisaVenda;
    private javax.swing.JTextField valor_venda;
    private javax.swing.JComboBox<ComboBoxItem> veiculo_venda;
    // End of variables declaration//GEN-END:variables
}
