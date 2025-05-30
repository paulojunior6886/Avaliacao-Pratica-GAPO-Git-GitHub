package br.com.pcar.view;

import javax.swing.JOptionPane;
import java.awt.Dimension;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import br.com.pcar.dto.PcarDTO;
import br.com.pcar.ctr.VeiculoCTR;
import br.com.pcar.dto.OpcionalDTO;
import br.com.pcar.ctr.OpcionalCTR;
import javax.swing.JFileChooser;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import java.sql.Statement; // Para Statement.RETURN_GENERATED_KEYS no DAO  

public class VeiculoVIEW extends javax.swing.JInternalFrame {

    PcarDTO veiculoDTO = new PcarDTO();
    VeiculoCTR veiculoCTR = new VeiculoCTR();
    OpcionalCTR opcionalCTR = new OpcionalCTR();

    int gravar_alterar; // 1 para novo, 2 para alterar
    DefaultTableModel modelo_tabela_veiculo;
    DefaultListModel<OpcionalDTO> listModelOpcionais;

    public VeiculoVIEW() {
        initComponents();
        modelo_tabela_veiculo = (DefaultTableModel) tabelaVeiculos.getModel();
        listModelOpcionais = new DefaultListModel<>();
        lstOpcionaisDisponiveis.setModel(listModelOpcionais);
        lstOpcionaisDisponiveis.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);


        setPosicao();
        corrigirTipoVeiculo(); // Garante itens no ComboBox de tipo
        
        // Estado Inicial
        limpaCampos();  
        liberaCampos(false);
        liberaBotoes(true, false, false, false, true); // (Novo, Salvar, Editar, Excluir, Limpar)
        btnBuscarVeiculo.setEnabled(true);
        tabelaVeiculos.setEnabled(true);
        tabelaVeiculos.clearSelection();
        gravar_alterar = 0;

        carregarOpcionaisParaLista();
        listarVeiculos();
    }

    private void corrigirTipoVeiculo() {
        if (tipo_veiculo.getItemCount() == 0) {
            tipo_veiculo.addItem("Carro");
            tipo_veiculo.addItem("Moto");
        }
        tipo_veiculo.setSelectedIndex(-1);
        marca_veiculo.removeAllItems();
        marca_veiculo.setSelectedIndex(-1);
    }

    private void carregarOpcionaisParaLista() {
        listModelOpcionais.clear();
        List<OpcionalDTO> todosOpcionais = opcionalCTR.listarTodosOpcionaisDTO();
        for (OpcionalDTO opcional : todosOpcionais) {
            listModelOpcionais.addElement(opcional);
        }
    }
    
    private void selecionarOpcionaisNaLista(List<Integer> idsOpcionaisAssociados) {
        lstOpcionaisDisponiveis.clearSelection();
        if (idsOpcionaisAssociados == null || idsOpcionaisAssociados.isEmpty()) {
            return;
        }
        List<Integer> indicesParaSelecionar = new ArrayList<>();
        for (int i = 0; i < listModelOpcionais.getSize(); i++) {
            OpcionalDTO opcionalNaLista = listModelOpcionais.getElementAt(i);
            if (idsOpcionaisAssociados.contains(opcionalNaLista.getId_opcional())) {
                indicesParaSelecionar.add(i);
            }
        }
        lstOpcionaisDisponiveis.setSelectedIndices(indicesParaSelecionar.stream().mapToInt(Integer::intValue).toArray());
    }

    private void listarVeiculos() {
        modelo_tabela_veiculo.setRowCount(0);
        ResultSet rs = null;
        try {
            rs = veiculoCTR.consultarVeiculo(new PcarDTO(), 3);
            while (rs.next()) {
                modelo_tabela_veiculo.addRow(new Object[]{
                    rs.getInt("id"), rs.getString("modelo_veiculo"),
                    rs.getInt("ano_fabricacao_veiculo"), rs.getInt("ano_modelo_veiculo"),
                    rs.getDouble("preco_veiculo"), rs.getString("cor_veiculo"),
                    rs.getString("placa_veiculo"), rs.getString("tipo_veiculo"),
                    rs.getString("marca_veiculo"), rs.getString("caminho_imagem")
                });
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar veículos: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Erro ao listar veículos.", "Erro", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (rs != null) veiculoCTR.CloseDB();
        }
    }

   /* public void setPosicao() {
        if (this.getDesktopPane() != null) {
            javax.swing.SwingUtilities.invokeLater(() -> {
                if (this.getDesktopPane() != null) {
                    Dimension d = this.getDesktopPane().getSize();
                    this.setSize(Math.min(d.width - 20, this.getPreferredSize().width), 
                                 Math.min(d.height - 20, this.getPreferredSize().height));
                    int x = (d.width - this.getWidth()) / 2;
                    int y = (d.height - this.getHeight()) / 2;
                    if (x < 0) x = 0; if (y < 0) y = 0;
                    this.setLocation(x, y);
                }
            });
        }
    }*/
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

    private void liberaCampos(boolean habilitar) {
        modelo_veiculo.setEnabled(habilitar);
        ano_fabricacao_veiculo.setEnabled(habilitar);
        ano_modelo_veiculo.setEnabled(habilitar);
        preco_veiculo.setEnabled(habilitar);
        cor_veiculo.setEnabled(habilitar);
        placa_veiculo.setEnabled(habilitar);
        tipo_veiculo.setEnabled(habilitar);
        marca_veiculo.setEnabled(habilitar);
        txtCaminhoImagem.setEnabled(habilitar);
        btnSelecionarImagem.setEnabled(habilitar);
        lstOpcionaisDisponiveis.setEnabled(habilitar);
    }

    private void limpaCampos() {
        modelo_veiculo.setText("");
        ano_fabricacao_veiculo.setText("");
        ano_modelo_veiculo.setText("");
        preco_veiculo.setText("");
        cor_veiculo.setText("");
        placa_veiculo.setText("");
        tipo_veiculo.setSelectedIndex(-1);
        marca_veiculo.removeAllItems();
        marca_veiculo.setSelectedIndex(-1);
        txtCaminhoImagem.setText("");
        lstOpcionaisDisponiveis.clearSelection();
        
        this.veiculoDTO = new PcarDTO(); // Importante resetar o DTO da classe
        if (modelo_veiculo.isEnabled()) modelo_veiculo.requestFocusInWindow();
    }

    // Seu método original de liberar botões
    private void liberaBotoes(boolean novo, boolean salvar, boolean editar, boolean excluir, boolean limpar) {
        btnNovoVeiculo.setEnabled(novo);
        btnSalvarVeiculo.setEnabled(salvar);
        btnEditarVeiculo.setEnabled(editar);
        btnExcluirVeiculo.setEnabled(excluir);
        btnLimparVeiculo.setEnabled(limpar); 
         
    }

    private void gravar() {
        try {
            if (modelo_veiculo.getText().trim().isEmpty() || ano_fabricacao_veiculo.getText().trim().isEmpty() ||
                ano_modelo_veiculo.getText().trim().isEmpty() || preco_veiculo.getText().trim().isEmpty() ||
                placa_veiculo.getText().trim().isEmpty() || tipo_veiculo.getSelectedIndex() == -1 ||
                (marca_veiculo.getItemCount() > 0 && marca_veiculo.getSelectedIndex() == -1) ) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos obrigatórios (Modelo, Anos, Preço, Placa, Tipo, Marca)!", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Popula o DTO da classe (veiculoDTO) com os dados dos campos
            this.veiculoDTO.setModelo_veiculo(modelo_veiculo.getText());
            this.veiculoDTO.setAno_fabricacao_veiculo(Integer.parseInt(ano_fabricacao_veiculo.getText()));
            this.veiculoDTO.setAno_modelo_veiculo(Integer.parseInt(ano_modelo_veiculo.getText()));
            this.veiculoDTO.setPreco_veiculo(Double.parseDouble(preco_veiculo.getText().replace(",", ".")));
            this.veiculoDTO.setCor_veiculo(cor_veiculo.getText());
            this.veiculoDTO.setPlaca_veiculo(placa_veiculo.getText().toUpperCase());
            this.veiculoDTO.setTipo_veiculo(tipo_veiculo.getSelectedItem().toString());
            this.veiculoDTO.setMarca_veiculo(marca_veiculo.getSelectedItem().toString());
            this.veiculoDTO.setCaminho_imagem(txtCaminhoImagem.getText());

             
        this.veiculoDTO.setModelo_veiculo(modelo_veiculo.getText());
        this.veiculoDTO.setCaminho_imagem(txtCaminhoImagem.getText());

        String PcarSTATUS_VEICULO = veiculoCTR.inserirVeiculo(this.veiculoDTO);  

        if (PcarSTATUS_VEICULO.toLowerCase().contains("sucesso")) {
            
            if (this.veiculoDTO.getId_veiculo() > 0) {
                List<OpcionalDTO> selecionados = lstOpcionaisDisponiveis.getSelectedValuesList();
                List<Integer> idsOpcionaisSelecionados = new ArrayList<>();
                for (OpcionalDTO opc : selecionados) {
                    idsOpcionaisSelecionados.add(opc.getId_opcional());
                }
                String statusOpc = veiculoCTR.atualizarOpcionaisDoVeiculo(this.veiculoDTO.getId_veiculo(), idsOpcionaisSelecionados);
                PcarSTATUS_VEICULO += "\n" + statusOpc; 
            } else {              
                PcarSTATUS_VEICULO += "\nALERTA: Não foi possível obter o ID do novo veículo para associar opcionais. Verifique o DAO.";
            }

            JOptionPane.showMessageDialog(this, PcarSTATUS_VEICULO, "Cadastro de Veículo", JOptionPane.INFORMATION_MESSAGE);
            listarVeiculos();         
            limpaCampos();
            liberaCampos(false);
            liberaBotoes(true, false, false, false, true); // (Novo, Salvar, Editar, Excluir, Limpar)
            btnBuscarVeiculo.setEnabled(true); 
        } else {
            JOptionPane.showMessageDialog(this, PcarSTATUS_VEICULO, "Falha no Cadastro", JOptionPane.ERROR_MESSAGE);
        }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Erro de Formato: Verifique os campos numéricos (Anos, Preço). Use ponto para decimais.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            System.err.println("Erro ao Gravar Veículo: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Erro ao Gravar Veículo:\n" + e.getMessage(), "Erro Crítico", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void alterar() {
        try {
            if (this.veiculoDTO.getId_veiculo() == 0) {
                JOptionPane.showMessageDialog(this, "Erro: ID do veículo não encontrado para alteração!", "Erro de Alteração", JOptionPane.ERROR_MESSAGE);
                return;
            }
             if (modelo_veiculo.getText().trim().isEmpty() || ano_fabricacao_veiculo.getText().trim().isEmpty()
                    || ano_modelo_veiculo.getText().trim().isEmpty() || preco_veiculo.getText().trim().isEmpty()
                    || placa_veiculo.getText().trim().isEmpty()
                    || tipo_veiculo.getSelectedIndex() == -1 || (marca_veiculo.getItemCount() > 0 && marca_veiculo.getSelectedIndex() == -1) ) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos obrigatórios!", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return;
            }

            this.veiculoDTO.setModelo_veiculo(modelo_veiculo.getText());
            this.veiculoDTO.setAno_fabricacao_veiculo(Integer.parseInt(ano_fabricacao_veiculo.getText()));
            this.veiculoDTO.setAno_modelo_veiculo(Integer.parseInt(ano_modelo_veiculo.getText()));
            this.veiculoDTO.setPreco_veiculo(Double.parseDouble(preco_veiculo.getText().replace(",", ".")));
            this.veiculoDTO.setCor_veiculo(cor_veiculo.getText());
            this.veiculoDTO.setPlaca_veiculo(placa_veiculo.getText().toUpperCase());
            this.veiculoDTO.setTipo_veiculo(tipo_veiculo.getSelectedItem().toString());
            this.veiculoDTO.setMarca_veiculo(marca_veiculo.getSelectedItem().toString());
            this.veiculoDTO.setCaminho_imagem(txtCaminhoImagem.getText());

            String PcarSTATUS_VEICULO = veiculoCTR.alterarVeiculo(this.veiculoDTO);

            if (PcarSTATUS_VEICULO.toLowerCase().contains("sucesso")) {
                List<OpcionalDTO> selecionados = lstOpcionaisDisponiveis.getSelectedValuesList();
                List<Integer> idsOpcionaisSelecionados = new ArrayList<>();
                for (OpcionalDTO opc : selecionados) {
                    idsOpcionaisSelecionados.add(opc.getId_opcional());
                }
                String statusOpc = veiculoCTR.atualizarOpcionaisDoVeiculo(this.veiculoDTO.getId_veiculo(), idsOpcionaisSelecionados);
                PcarSTATUS_VEICULO += "\n" + statusOpc;
                
                listarVeiculos();
                limpaCampos();
                liberaCampos(false);
                liberaBotoes(true, false, false, false, true);
                btnBuscarVeiculo.setEnabled(true);
            }
            JOptionPane.showMessageDialog(this, PcarSTATUS_VEICULO, "Alteração de Veículo", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Erro de Formato nos campos numéricos.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            System.err.println("Erro ao Alterar Veículo: " + e.getMessage());
             JOptionPane.showMessageDialog(this, "Erro ao Alterar Veículo:\n" + e.getMessage(), "Erro Crítico", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void excluir() {
    if (this.veiculoDTO.getId_veiculo() == 0) { 
        JOptionPane.showMessageDialog(this, "Nenhum veículo selecionado ou ID inválido para exclusão.\nPor favor, selecione um veículo na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    // Pega o nome do modelo do DTO para a mensagem de confirmação
    String modeloParaMensagem = (this.veiculoDTO.getModelo_veiculo() != null && !this.veiculoDTO.getModelo_veiculo().isEmpty()) 
                                ? this.veiculoDTO.getModelo_veiculo() 
                                : "este veículo";

    if (JOptionPane.showConfirmDialog(this, "Deseja realmente excluir o Veículo " + modeloParaMensagem + "?\nEsta ação não pode ser desfeita.", "Confirmação de Exclusão",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
        
        // Chama o método da CTR. O this.veiculoDTO já deve ter o ID correto.
        String PcarSTATUS_EXCLUSAO = veiculoCTR.excluirVeiculo(this.veiculoDTO); //
        
        // Verifica a mensagem retornada pela CTR
        if (PcarSTATUS_EXCLUSAO.toLowerCase().contains("sucesso")) {
            JOptionPane.showMessageDialog(this, PcarSTATUS_EXCLUSAO, "Exclusão de Veículo", JOptionPane.INFORMATION_MESSAGE);
            listarVeiculos();        
            limpaCampos(); 
            liberaCampos(false); // Bloqueia os campos
            liberaBotoes(true, false, false, false, true); // (Novo, Salvar, Editar, Excluir, Limpar) - Ajuste conforme sua UI
            btnBuscarVeiculo.setEnabled(true); // Garante que o botão de busca seja reabilitado
            tabelaVeiculos.clearSelection();
            gravar_alterar = 0; 
        } else {
            JOptionPane.showMessageDialog(this, PcarSTATUS_EXCLUSAO, "Falha na Exclusão", JOptionPane.ERROR_MESSAGE);
        }
    }
}

    private void preencheTabelaPorModelo(String modelo) { 
        modelo_tabela_veiculo.setRowCount(0);
        PcarDTO dtoConsulta = new PcarDTO();
        dtoConsulta.setModelo_veiculo(modelo);
        ResultSet rs = null;
        try {
            rs = veiculoCTR.consultarVeiculo(dtoConsulta, 1); // Opção 1 para buscar por modelo
            while (rs != null && rs.next()) { // Adicionado rs != null
                 modelo_tabela_veiculo.addRow(new Object[]{
                    rs.getInt("id"), rs.getString("modelo_veiculo"),
                    rs.getInt("ano_fabricacao_veiculo"), rs.getInt("ano_modelo_veiculo"),
                    rs.getDouble("preco_veiculo"), rs.getString("cor_veiculo"),
                    rs.getString("placa_veiculo"), rs.getString("tipo_veiculo"),
                    rs.getString("marca_veiculo"), rs.getString("caminho_imagem")
                });
            }
        } catch (Exception e) {
            System.err.println("Erro ao preencher tabela (pesquisa): " + e.getMessage());
             JOptionPane.showMessageDialog(this, "Erro ao pesquisar veículos.", "Erro", JOptionPane.ERROR_MESSAGE);
        } finally {
            if(rs != null) veiculoCTR.CloseDB();
        }
    }

    // Seu método preencheCampos (renomeei para clareza e adaptei)
    private void carregarDadosVeiculoSelecionadoNosCampos(int id_veiculo_param) { 
        PcarDTO dtoConsulta = new PcarDTO();
        dtoConsulta.setId_veiculo(id_veiculo_param);
        ResultSet rs = null;
        try {
            rs = veiculoCTR.consultarVeiculo(dtoConsulta, 2); // Busca por ID
            if (rs != null && rs.next()) { // Adicionado rs != null
                limpaCampos(); 
                
                this.veiculoDTO.setId_veiculo(rs.getInt("id"));
                this.veiculoDTO.setModelo_veiculo(rs.getString("modelo_veiculo"));
                this.veiculoDTO.setAno_fabricacao_veiculo(rs.getInt("ano_fabricacao_veiculo"));
                this.veiculoDTO.setAno_modelo_veiculo(rs.getInt("ano_modelo_veiculo"));
                this.veiculoDTO.setPreco_veiculo(rs.getDouble("preco_veiculo"));
                this.veiculoDTO.setCor_veiculo(rs.getString("cor_veiculo"));
                this.veiculoDTO.setPlaca_veiculo(rs.getString("placa_veiculo"));
                this.veiculoDTO.setTipo_veiculo(rs.getString("tipo_veiculo"));
                this.veiculoDTO.setMarca_veiculo(rs.getString("marca_veiculo"));
                this.veiculoDTO.setCaminho_imagem(rs.getString("caminho_imagem"));

                modelo_veiculo.setText(this.veiculoDTO.getModelo_veiculo());
                ano_fabricacao_veiculo.setText(String.valueOf(this.veiculoDTO.getAno_fabricacao_veiculo()));
                ano_modelo_veiculo.setText(String.valueOf(this.veiculoDTO.getAno_modelo_veiculo()));
                preco_veiculo.setText(String.valueOf(this.veiculoDTO.getPreco_veiculo()));
                cor_veiculo.setText(this.veiculoDTO.getCor_veiculo());
                placa_veiculo.setText(this.veiculoDTO.getPlaca_veiculo());
                tipo_veiculo.setSelectedItem(this.veiculoDTO.getTipo_veiculo());
                marca_veiculo.setSelectedItem(this.veiculoDTO.getMarca_veiculo());
                txtCaminhoImagem.setText(this.veiculoDTO.getCaminho_imagem());

                List<Integer> idsOpcionais = veiculoCTR.buscarIdsOpcionaisPorVeiculo(this.veiculoDTO.getId_veiculo());
                selecionarOpcionaisNaLista(idsOpcionais);
                
                liberaCampos(false); 
                liberaBotoes(true, false, true, true, true); 
                gravar_alterar = 0; 
            } else {
                JOptionPane.showMessageDialog(this, "Veículo não encontrado para carregar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) { 
            System.err.println("Erro ao preencher campos do veículo: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados do veículo.", "Erro", JOptionPane.ERROR_MESSAGE);
        } finally {
             if(rs != null) veiculoCTR.CloseDB();
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
        modelo_veiculo = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        ano_fabricacao_veiculo = new javax.swing.JTextField();
        ano_modelo_veiculo = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        preco_veiculo = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        cor_veiculo = new javax.swing.JTextField();
        placa_veiculo = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        tipo_veiculo = new javax.swing.JComboBox<>();
        marca_veiculo = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        btnSalvarVeiculo = new javax.swing.JButton();
        btnEditarVeiculo = new javax.swing.JButton();
        btnExcluirVeiculo = new javax.swing.JButton();
        btnLimparVeiculo = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        txtPesquisarVeiculo = new javax.swing.JTextField();
        btnBuscarVeiculo = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabelaVeiculos = new javax.swing.JTable();
        btnSairVeiculo = new javax.swing.JButton();
        btnNovoVeiculo = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        txtCaminhoImagem = new javax.swing.JTextField();
        btnSelecionarImagem = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstOpcionaisDisponiveis = new javax.swing.JList<>();

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        jLabel1.setText("Veículo");

        jLabel2.setText("Modelo:");

        jLabel3.setText("Ano Fabricação:");

        jLabel4.setText("Ano do Modelo:");

        jLabel5.setText("Preço:");

        jLabel6.setText("Cor:");

        jLabel7.setText("Placa:");

        jLabel8.setText("Tipo:");

        tipo_veiculo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Carro", "Moto" }));
        tipo_veiculo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tipo_veiculoActionPerformed(evt);
            }
        });

        marca_veiculo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Fiat", "Volkswagen", "Chevrolet", "Toyota", "Hyundai", "Jeep", "Honda", "Renault", "Ford", "Nissan", "Peugeot", "Citroën", "Mitsubishi", "BMW", "Mercedes-Benz", "Audi", "Volvo", "Land Rover", "Kia", "Suzuki", " ", "Honda", "Yamaha", "Suzuki", "Kawasaki", "BMW", "Dafra", "Triumph", "Harley-Davidson", "Ducati", "KTM", "Royal Enfield", "Haojue", "Shineray", "Zontes" }));

        jLabel9.setText("Marca:");

        btnSalvarVeiculo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/salvar_1.png"))); // NOI18N
        btnSalvarVeiculo.setText("Salvar");
        btnSalvarVeiculo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarVeiculoActionPerformed(evt);
            }
        });

        btnEditarVeiculo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/editar.png"))); // NOI18N
        btnEditarVeiculo.setText("Editar");
        btnEditarVeiculo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarVeiculoActionPerformed(evt);
            }
        });

        btnExcluirVeiculo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/apagar.png"))); // NOI18N
        btnExcluirVeiculo.setText("Excluir");
        btnExcluirVeiculo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirVeiculoActionPerformed(evt);
            }
        });

        btnLimparVeiculo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/cancelar_1.png"))); // NOI18N
        btnLimparVeiculo.setText("Limpar");
        btnLimparVeiculo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimparVeiculoActionPerformed(evt);
            }
        });

        jLabel10.setText("Pesquisar Veículo:");

        btnBuscarVeiculo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/busca.png"))); // NOI18N
        btnBuscarVeiculo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarVeiculoActionPerformed(evt);
            }
        });

        tabelaVeiculos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Modelo", "Ano Fabricação", "Ano Modelo", "Preço", "Cor", "Placa", "Tipo", "Marca"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabelaVeiculos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelaVeiculosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tabelaVeiculos);

        btnSairVeiculo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/sair_1.png"))); // NOI18N
        btnSairVeiculo.setText("Sair");
        btnSairVeiculo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairVeiculoActionPerformed(evt);
            }
        });

        btnNovoVeiculo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/novo_1.png"))); // NOI18N
        btnNovoVeiculo.setText("Novo");
        btnNovoVeiculo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNovoVeiculoActionPerformed(evt);
            }
        });

        jLabel11.setText("Imagem:");

        btnSelecionarImagem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/pcar/view/imagens/novo_1.png"))); // NOI18N
        btnSelecionarImagem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelecionarImagemActionPerformed(evt);
            }
        });

        jLabel12.setText("Opcionais Disponíveis:");

        jScrollPane2.setViewportView(lstOpcionaisDisponiveis);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(42, 42, 42)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 901, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtPesquisarVeiculo, javax.swing.GroupLayout.PREFERRED_SIZE, 388, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnBuscarVeiculo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                            .addComponent(jLabel2)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(modelo_veiculo))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                    .addComponent(jLabel5)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(preco_veiculo))
                                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                    .addComponent(jLabel3)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(ano_fabricacao_veiculo, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addGap(18, 18, 18)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addGroup(layout.createSequentialGroup()
                                                    .addComponent(jLabel6)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(cor_veiculo, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGroup(layout.createSequentialGroup()
                                                    .addComponent(jLabel4)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(ano_modelo_veiculo)))))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel9)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(marca_veiculo, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabel11)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel8)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(tipo_veiculo, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(32, 32, 32)
                                                .addComponent(jLabel7)
                                                .addGap(8, 8, 8)))
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(txtCaminhoImagem, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnSelecionarImagem, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(placa_veiculo)
                                                .addGap(3, 3, 3)))))
                                .addGap(52, 52, 52)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel12)
                                    .addComponent(jScrollPane2)))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(443, 443, 443)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(168, 168, 168)
                        .addComponent(btnNovoVeiculo)
                        .addGap(18, 18, 18)
                        .addComponent(btnSalvarVeiculo)
                        .addGap(18, 18, 18)
                        .addComponent(btnEditarVeiculo)
                        .addGap(18, 18, 18)
                        .addComponent(btnExcluirVeiculo)
                        .addGap(18, 18, 18)
                        .addComponent(btnLimparVeiculo)
                        .addGap(18, 18, 18)
                        .addComponent(btnSairVeiculo)))
                .addContainerGap(105, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(modelo_veiculo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel12))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(preco_veiculo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6)
                            .addComponent(cor_veiculo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(ano_fabricacao_veiculo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ano_modelo_veiculo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(placa_veiculo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8)
                            .addComponent(tipo_veiculo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSelecionarImagem, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel11)
                        .addComponent(txtCaminhoImagem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(marca_veiculo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSalvarVeiculo)
                    .addComponent(btnEditarVeiculo)
                    .addComponent(btnExcluirVeiculo)
                    .addComponent(btnLimparVeiculo)
                    .addComponent(btnNovoVeiculo)
                    .addComponent(btnSairVeiculo))
                .addGap(32, 32, 32)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel10)
                        .addComponent(txtPesquisarVeiculo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnBuscarVeiculo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(56, 56, 56))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tipo_veiculoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tipo_veiculoActionPerformed
         // Sua lógica original para popular marca_veiculo
        marca_veiculo.removeAllItems();
        if (tipo_veiculo.getSelectedItem() != null) { 
            String tipoSelecionado = tipo_veiculo.getSelectedItem().toString();
            if (tipoSelecionado.equals("Carro")) {
                String[] marcasCarro = {"Fiat", "Volkswagen", "Chevrolet", "Toyota", "Hyundai", "Jeep", "Honda", "Renault", "Ford", "Nissan", "Peugeot", "Citroën", "Mitsubishi", "BMW", "Mercedes-Benz", "Audi", "Volvo", "Land Rover", "Kia", "Suzuki"};
                for (String marca : marcasCarro) {
                    marca_veiculo.addItem(marca);
                }
            } else if (tipoSelecionado.equals("Moto")) { 
                String[] marcasMoto = {"Honda", "Yamaha", "Suzuki", "Kawasaki", "BMW", "Dafra", "Triumph", "Harley-Davidson", "Ducati", "KTM", "Royal Enfield", "Haojue", "Shineray", "Zontes"};
                for (String marca : marcasMoto) {
                    marca_veiculo.addItem(marca);
                }
            }
            marca_veiculo.setSelectedIndex(-1);
        }
    }//GEN-LAST:event_tipo_veiculoActionPerformed

    private void btnSalvarVeiculoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarVeiculoActionPerformed
       if (gravar_alterar == 1) { 
            gravar();
        } else if (gravar_alterar == 2) { 
            if (this.veiculoDTO.getId_veiculo() > 0) {
                alterar();
            } else {
                JOptionPane.showMessageDialog(this, "Nenhum veículo selecionado para alteração ou ID inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
                 // Volta para o estado inicial se não há ID válido para alterar
                limpaCampos();
                liberaCampos(false);
                liberaBotoes(true, false, false, false, true);
                btnBuscarVeiculo.setEnabled(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Ação indefinida. Clique em 'Novo' ou selecione e clique em 'Editar'.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnSalvarVeiculoActionPerformed

    private void btnEditarVeiculoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarVeiculoActionPerformed
       int linhaSelecionada = tabelaVeiculos.getSelectedRow();
        if (linhaSelecionada >= 0) {
            // Os dados já foram carregados no this.veiculoDTO pelo clique na tabela (via carregarDadosVeiculoSelecionadoNosCampos)
            gravar_alterar = 2; 
            liberaCampos(true); 
            liberaBotoes(false, true, false, true, true); // Novo=F, Salvar=T, Editar=F, Excluir=T, Limpar=T
            btnBuscarVeiculo.setEnabled(false); // Desabilitar busca durante edição
            modelo_veiculo.requestFocusInWindow();
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um veículo na tabela para editar!");
        }
    }//GEN-LAST:event_btnEditarVeiculoActionPerformed

    private void btnExcluirVeiculoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirVeiculoActionPerformed
         int linhaSelecionada = tabelaVeiculos.getSelectedRow();
        // Garante que o ID no DTO da classe está correto se um item estiver selecionado
        if (linhaSelecionada >= 0) {
             this.veiculoDTO.setId_veiculo((int) tabelaVeiculos.getValueAt(linhaSelecionada, 0));
        }
       
       if (this.veiculoDTO.getId_veiculo() == 0){ 
           JOptionPane.showMessageDialog(this, "Selecione um veículo na tabela para excluir!");
           return;
       }
       excluir();
    }//GEN-LAST:event_btnExcluirVeiculoActionPerformed

    private void btnBuscarVeiculoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarVeiculoActionPerformed
        preencheTabelaPorModelo(txtPesquisarVeiculo.getText().trim());
    }//GEN-LAST:event_btnBuscarVeiculoActionPerformed

    private void btnLimparVeiculoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimparVeiculoActionPerformed
         // Seu btnLimparVeiculo age como um Cancelar/Resetar para o estado inicial
        limpaCampos();
        liberaCampos(false);
        liberaBotoes(true, false, false, false, true); // (Novo, Salvar, Editar, Excluir, Limpar)
        btnBuscarVeiculo.setEnabled(true);
        tabelaVeiculos.setEnabled(true);
        tabelaVeiculos.clearSelection();
        gravar_alterar = 0;
        // Se limpar o campo de pesquisa também e recarregar todos:
        // txtPesquisarVeiculo.setText("");
        // listarVeiculos(); 
    }//GEN-LAST:event_btnLimparVeiculoActionPerformed

    private void btnSairVeiculoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSairVeiculoActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnSairVeiculoActionPerformed

    private void btnNovoVeiculoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNovoVeiculoActionPerformed
         this.veiculoDTO = new PcarDTO(); // Reseta o DTO para um novo veículo
        limpaCampos(); 
        liberaCampos(true); 
        liberaBotoes(false, true, false, false, true); // (Novo=F, Salvar=T, Editar=F, Excluir=F, Limpar=T)
        btnBuscarVeiculo.setEnabled(false); // Desabilitar busca enquanto cadastra novo
        gravar_alterar = 1;
        modelo_veiculo.requestFocusInWindow();
    }//GEN-LAST:event_btnNovoVeiculoActionPerformed

    private void tabelaVeiculosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelaVeiculosMouseClicked
        int linhaSelecionada = tabelaVeiculos.getSelectedRow();
        if (linhaSelecionada >= 0) {
            int idVeiculoSelecionado = (Integer) modelo_tabela_veiculo.getValueAt(linhaSelecionada, 0);
            carregarDadosVeiculoSelecionadoNosCampos(idVeiculoSelecionado); 
            // O método carregarDadosVeiculoSelecionadoNosCampos já configura os botões para
            // (Novo=T, Salvar=F, Editar=T, Excluir=T, Limpar=T)
        }
    }//GEN-LAST:event_tabelaVeiculosMouseClicked

    private void btnSelecionarImagemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelecionarImagemActionPerformed
       JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecione a imagem do veículo");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        javax.swing.filechooser.FileNameExtensionFilter filter = new javax.swing.filechooser.FileNameExtensionFilter("Imagens (JPG, PNG, GIF)", "jpg", "jpeg", "png", "gif");
        fileChooser.setFileFilter(filter);

        int PcarSTATUS = fileChooser.showOpenDialog(this); 
        if (PcarSTATUS == JFileChooser.APPROVE_OPTION) {
            File arquivoSelecionado = fileChooser.getSelectedFile();
            txtCaminhoImagem.setText(arquivoSelecionado.getAbsolutePath());
        }
    }//GEN-LAST:event_btnSelecionarImagemActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField ano_fabricacao_veiculo;
    private javax.swing.JTextField ano_modelo_veiculo;
    private javax.swing.JButton btnBuscarVeiculo;
    private javax.swing.JButton btnEditarVeiculo;
    private javax.swing.JButton btnExcluirVeiculo;
    private javax.swing.JButton btnLimparVeiculo;
    private javax.swing.JButton btnNovoVeiculo;
    private javax.swing.JButton btnSairVeiculo;
    private javax.swing.JButton btnSalvarVeiculo;
    private javax.swing.JButton btnSelecionarImagem;
    private javax.swing.JTextField cor_veiculo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList<br.com.pcar.dto.OpcionalDTO> lstOpcionaisDisponiveis;
    private javax.swing.JComboBox<String> marca_veiculo;
    private javax.swing.JTextField modelo_veiculo;
    private javax.swing.JTextField placa_veiculo;
    private javax.swing.JTextField preco_veiculo;
    private javax.swing.JTable tabelaVeiculos;
    private javax.swing.JComboBox<String> tipo_veiculo;
    private javax.swing.JTextField txtCaminhoImagem;
    private javax.swing.JTextField txtPesquisarVeiculo;
    // End of variables declaration//GEN-END:variables
}
