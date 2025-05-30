package br.com.pcar.dao;

import java.sql.*;
import br.com.pcar.dto.PcarDTO; //
import java.util.ArrayList;
import java.util.List;

public class VeiculoDAO {

    public VeiculoDAO() {
    }

    // Os PreparedStatement e ResultSet serão declarados localmente nos métodos.
    // Dentro da classe VeiculoDAO.java
// Certifique-se de ter: import java.sql.Statement; (para Statement.RETURN_GENERATED_KEYS)

public boolean inserirVeiculo(PcarDTO veiculoDTO) { // Mantém o retorno boolean, mas atualiza o DTO
    PreparedStatement ps = null;
    String sql = "INSERT INTO veiculos (modelo_veiculo, ano_fabricacao_veiculo, ano_modelo_veiculo, " +
                 "preco_veiculo, cor_veiculo, placa_veiculo, tipo_veiculo, marca_veiculo, caminho_imagem) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    boolean sucesso = false;
    try {
        ConexaoDAO.ConectDB();
        // Solicitar chaves geradas (o ID do veículo)
        ps = ConexaoDAO.con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS); 

        ps.setString(1, veiculoDTO.getModelo_veiculo());
        ps.setInt(2, veiculoDTO.getAno_fabricacao_veiculo());
        ps.setInt(3, veiculoDTO.getAno_modelo_veiculo());
        ps.setDouble(4, veiculoDTO.getPreco_veiculo());
        ps.setString(5, veiculoDTO.getCor_veiculo());
        ps.setString(6, veiculoDTO.getPlaca_veiculo().toUpperCase()); // Garante placa em maiúsculas
        ps.setString(7, veiculoDTO.getTipo_veiculo());
        ps.setString(8, veiculoDTO.getMarca_veiculo());
        ps.setString(9, veiculoDTO.getCaminho_imagem());

        int statusExecucao = ps.executeUpdate();
        if (statusExecucao > 0) {
            ResultSet rsKeys = ps.getGeneratedKeys(); // Obter as chaves geradas
            if (rsKeys.next()) {
                int idGerado = rsKeys.getInt(1);
                veiculoDTO.setId_veiculo(idGerado); // ATUALIZA O ID NO OBJETO DTO!
                System.out.println("DEBUG VeiculoDAO: ID do novo veículo inserido: " + idGerado); // Debug
            }
            rsKeys.close(); 

            if(!ConexaoDAO.con.getAutoCommit()) {
                ConexaoDAO.con.commit();
            }
            sucesso = true;
        }
    } catch (SQLException e) {
        System.err.println("Erro ao inserir veículo DAO: " + e.getMessage());
        // Considerar rollback em caso de erro se não for autocommit
        try { 
            if(ConexaoDAO.con != null && !ConexaoDAO.con.getAutoCommit()) ConexaoDAO.con.rollback();
        } catch (SQLException ex) { 
            System.err.println("Erro no rollback da inserção de veículo: " + ex.getMessage()); 
        }
        sucesso = false;
    } finally {
        try {
            if (ps != null) ps.close();
        } catch (SQLException e) { System.err.println("Erro ao fechar PreparedStatement em inserirVeiculo: " + e.getMessage());}
        ConexaoDAO.CloseDB();
    }
    return sucesso;
}

    public ResultSet consultarVeiculo(PcarDTO veiculoDTO, int opcao) { //
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "";
        // Incluindo caminho_imagem na consulta
        String colunas = "id, modelo_veiculo, ano_fabricacao_veiculo, ano_modelo_veiculo, preco_veiculo, "
                + "cor_veiculo, placa_veiculo, tipo_veiculo, marca_veiculo, caminho_imagem ";
        try {
            ConexaoDAO.ConectDB();
            switch (opcao) {
                case 1: // Busca por modelo
                    sql = "SELECT " + colunas + "FROM veiculos WHERE modelo_veiculo ILIKE ? ORDER BY modelo_veiculo";
                    ps = ConexaoDAO.con.prepareStatement(sql);
                    ps.setString(1, "%" + veiculoDTO.getModelo_veiculo() + "%"); //
                    break;
                case 2: // Busca por ID
                    sql = "SELECT " + colunas + "FROM veiculos WHERE id = ?";
                    ps = ConexaoDAO.con.prepareStatement(sql);
                    ps.setInt(1, veiculoDTO.getId_veiculo()); //
                    break;
                case 3: // Listar todos os veículos
                    sql = "SELECT " + colunas + "FROM veiculos ORDER BY modelo_veiculo";
                    ps = ConexaoDAO.con.prepareStatement(sql);
                    break;
                default:
                    return null; // Opção inválida
            }
            rs = ps.executeQuery();
            // A conexão e o PreparedStatement serão fechados pela VeiculoCTR após o uso do ResultSet
            return rs;
        } catch (SQLException e) {
            System.err.println("Erro ao consultar veículo DAO: " + e.getMessage());
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
            }
            ConexaoDAO.CloseDB(); // Fecha em caso de erro
            return null;
        }
    }

    public boolean alterarVeiculo(PcarDTO veiculoDTO) { //
        PreparedStatement ps = null;
        // A placa geralmente não é alterada. Se precisar, adicione-a ao SET.
        // Incluindo caminho_imagem na alteração.
        String sql = "UPDATE veiculos SET modelo_veiculo = ?, ano_fabricacao_veiculo = ?, ano_modelo_veiculo = ?, "
                + "preco_veiculo = ?, cor_veiculo = ?, tipo_veiculo = ?, marca_veiculo = ?, caminho_imagem = ?, placa_veiculo = ? "
                + "WHERE id = ?";
        try {
            ConexaoDAO.ConectDB();
            ps = ConexaoDAO.con.prepareStatement(sql);

            ps.setString(1, veiculoDTO.getModelo_veiculo()); //
            ps.setInt(2, veiculoDTO.getAno_fabricacao_veiculo()); //
            ps.setInt(3, veiculoDTO.getAno_modelo_veiculo()); //
            ps.setDouble(4, veiculoDTO.getPreco_veiculo()); //
            ps.setString(5, veiculoDTO.getCor_veiculo()); //
            ps.setString(6, veiculoDTO.getTipo_veiculo()); //
            ps.setString(7, veiculoDTO.getMarca_veiculo()); //
            ps.setString(8, veiculoDTO.getCaminho_imagem()); //
            ps.setString(9, veiculoDTO.getPlaca_veiculo()); // Placa adicionada ao update
            ps.setInt(10, veiculoDTO.getId_veiculo()); //

            int PcarSTATUS = ps.executeUpdate();
            if (PcarSTATUS > 0) {
                if (!ConexaoDAO.con.getAutoCommit()) {
                    ConexaoDAO.con.commit();
                }
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao alterar veículo DAO: " + e.getMessage());
            return false;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                System.err.println("Erro ao fechar PreparedStatement: " + e.getMessage());
            }
            ConexaoDAO.CloseDB();
        }
    }

    // Dentro da classe VeiculoDAO.java

public String excluirVeiculo(PcarDTO veiculoDTO) { // Alterado para retornar String com a mensagem
    PreparedStatement psCheck = null;
    ResultSet rsCheck = null;
    PreparedStatement psDelete = null;
    String sqlCheck = "SELECT COUNT(*) AS total_vendas FROM vendas WHERE id_veiculo = ?";
    String sqlDelete = "DELETE FROM veiculos WHERE id = ?";

    try {
        ConexaoDAO.ConectDB();
        // 1. Verificar se o veículo está em alguma venda
        psCheck = ConexaoDAO.con.prepareStatement(sqlCheck);
        psCheck.setInt(1, veiculoDTO.getId_veiculo());
        rsCheck = psCheck.executeQuery();
        if (rsCheck.next() && rsCheck.getInt("total_vendas") > 0) {
            return "Erro: Veículo não pode ser excluído pois possui " + rsCheck.getInt("total_vendas") + " venda(s) registrada(s).";
        }

        // 2. Se não houver vendas, proceder com a exclusão dos opcionais associados (ON DELETE CASCADE na FK já faz isso)
        // OU deletar manualmente de veiculo_opcionais PRIMEIRO se não houver CASCADE:
        // PreparedStatement psDeleteOpcionais = ConexaoDAO.con.prepareStatement("DELETE FROM veiculo_opcionais WHERE id_veiculo_fk = ?");
        // psDeleteOpcionais.setInt(1, veiculoDTO.getId_veiculo());
        // psDeleteOpcionais.executeUpdate();
        // psDeleteOpcionais.close();

        // 3. Proceder com a exclusão do veículo
        psDelete = ConexaoDAO.con.prepareStatement(sqlDelete);
        psDelete.setInt(1, veiculoDTO.getId_veiculo());

        int statusExecucao = psDelete.executeUpdate();
        if (statusExecucao > 0) {
            if(!ConexaoDAO.con.getAutoCommit()) ConexaoDAO.con.commit();
            return "Veículo excluído com sucesso!";
        } else {
            return "Veículo não encontrado para exclusão."; // Ou erro se não deletou
        }
    } catch (SQLException e) {
        // O erro de FK não deveria mais acontecer aqui se a checagem acima for feita.
        // Mas, por segurança, tratamos.
        System.err.println("Erro ao excluir veículo DAO: " + e.getMessage());
         try { 
            if(ConexaoDAO.con != null && !ConexaoDAO.con.getAutoCommit()) ConexaoDAO.con.rollback();
        } catch (SQLException ex) { 
            System.err.println("Erro no rollback da exclusão de veículo: " + ex.getMessage()); 
        }
        return "Erro ao excluir veículo: " + e.getMessage();
    } finally {
        try {
            if (rsCheck != null) rsCheck.close();
            if (psCheck != null) psCheck.close();
            if (psDelete != null) psDelete.close();
        } catch (SQLException e) { System.err.println("Erro ao fechar recursos em excluirVeiculo: " + e.getMessage());}
        ConexaoDAO.CloseDB();
    }
}

    // Dentro da classe VeiculoDAO.java
    /**
     * Busca os IDs dos opcionais associados a um veículo específico.
     *
     * @param idVeiculo O ID do veículo.
     * @return Uma lista de Integers contendo os IDs dos opcionais.
     */
    public List<Integer> buscarIdsOpcionaisPorVeiculo(int idVeiculo) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "SELECT id_opcional_fk FROM veiculo_opcionais WHERE id_veiculo_fk = ?";
        List<Integer> idsOpcionais = new ArrayList<>();
        try {
            ConexaoDAO.ConectDB();
            ps = ConexaoDAO.con.prepareStatement(sql);
            ps.setInt(1, idVeiculo);
            rs = ps.executeQuery();
            while (rs.next()) {
                idsOpcionais.add(rs.getInt("id_opcional_fk"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar IDs de opcionais por veículo DAO: " + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                System.err.println("Erro ao fechar recursos: " + e.getMessage());
            }
            ConexaoDAO.CloseDB(); // Fechar conexão principal
        }
        return idsOpcionais;
    }

    /**
     * Atualiza os opcionais associados a um veículo. Primeiro remove todas as
     * associações existentes e depois insere as novas.
     *
     * @param idVeiculo O ID do veículo.
     * @param idsOpcionais A lista de IDs dos novos opcionais a serem
     * associados.
     * @return true se a operação for bem-sucedida, false caso contrário.
     */
    public boolean atualizarOpcionaisDoVeiculo(int idVeiculo, List<Integer> idsOpcionais) {
        PreparedStatement psDelete = null;
        PreparedStatement psInsert = null;
        String sqlDelete = "DELETE FROM veiculo_opcionais WHERE id_veiculo_fk = ?";
        String sqlInsert = "INSERT INTO veiculo_opcionais (id_veiculo_fk, id_opcional_fk) VALUES (?, ?)";
        boolean sucesso = false;

        try {
            ConexaoDAO.ConectDB();
            // Importante: Controlar transação manualmente se não for autocommit
            boolean autoCommitOriginal = ConexaoDAO.con.getAutoCommit();
            if (autoCommitOriginal) {
                ConexaoDAO.con.setAutoCommit(false);
            }

            // 1. Deletar opcionais antigos
            psDelete = ConexaoDAO.con.prepareStatement(sqlDelete);
            psDelete.setInt(1, idVeiculo);
            psDelete.executeUpdate(); // Executa mesmo que não haja opcionais antigos

            // 2. Inserir novos opcionais
            if (idsOpcionais != null && !idsOpcionais.isEmpty()) {
                psInsert = ConexaoDAO.con.prepareStatement(sqlInsert);
                for (Integer idOpcional : idsOpcionais) {
                    psInsert.setInt(1, idVeiculo);
                    psInsert.setInt(2, idOpcional);
                    psInsert.addBatch(); // Adiciona para execução em lote
                }
                psInsert.executeBatch(); // Executa todas as inserções
            }

            if (!autoCommitOriginal) {
                ConexaoDAO.con.commit();
            }
            sucesso = true;

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar opcionais do veículo DAO: " + e.getMessage());
            try {
                if (!ConexaoDAO.con.getAutoCommit()) {
                    ConexaoDAO.con.rollback();
                }
            } catch (SQLException ex) {
                System.err.println("Erro no rollback da atualização de opcionais: " + ex.getMessage());
            }
            sucesso = false;
        } finally {
            try {
                if (psDelete != null) {
                    psDelete.close();
                }
                if (psInsert != null) {
                    psInsert.close();
                }
                if (!ConexaoDAO.con.getAutoCommit()) { // Restaura autocommit se foi alterado
                    ConexaoDAO.con.setAutoCommit(true);
                }
            } catch (SQLException e) {
                System.err.println("Erro ao fechar PreparedStatement ou restaurar autocommit: " + e.getMessage());
            }
            ConexaoDAO.CloseDB();
        }
        return sucesso;
    }
}
