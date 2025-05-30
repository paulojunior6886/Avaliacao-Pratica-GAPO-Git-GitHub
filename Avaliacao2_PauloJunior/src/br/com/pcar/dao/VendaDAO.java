package br.com.pcar.dao;

import java.sql.*;
import br.com.pcar.dto.PcarDTO; //

public class VendaDAO {
    public VendaDAO() { }

    /**
     * Insere uma nova venda no banco de dados.
     * Espera que id_cliente e id_veiculo estejam preenchidos no DTO.
     * @param vendaDTO O objeto PcarDTO contendo os dados da venda.
     * @return true se a inserção for bem-sucedida, false caso contrário.
     */
    public boolean inserirVenda(PcarDTO vendaDTO) { //
        PreparedStatement ps = null;
        String sql = "INSERT INTO vendas (id_cliente, id_veiculo, data_venda, valor_venda, status_venda) VALUES (?, ?, ?, ?, ?)";
        try {
            ConexaoDAO.ConectDB();
            ps = ConexaoDAO.con.prepareStatement(sql);
            
            ps.setInt(1, vendaDTO.getId_cliente()); //
            ps.setInt(2, vendaDTO.getId_veiculo()); //
            // Para datas, é melhor converter para java.sql.Date
            // Assumindo que vendaDTO.getData_venda() está no formato YYYY-MM-DD
            try {
                ps.setDate(3, Date.valueOf(vendaDTO.getData_venda())); //
            } catch (IllegalArgumentException e) {
                System.err.println("Formato de data inválido para venda: " + vendaDTO.getData_venda() + ". Use YYYY-MM-DD."); //
                // Considerar lançar uma exceção mais específica ou retornar false de forma mais clara
                return false;
            }
            ps.setDouble(4, vendaDTO.getValor_venda()); //
            ps.setString(5, vendaDTO.getStatus_venda()); //

            int PcarSTATUS = ps.executeUpdate();
            if (PcarSTATUS > 0) {
                if(!ConexaoDAO.con.getAutoCommit()) ConexaoDAO.con.commit();
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao inserir venda DAO: " + e.getMessage());
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) { System.err.println("Erro ao fechar PreparedStatement: " + e.getMessage());}
            ConexaoDAO.CloseDB();
        }
    }

    /**
     * Consulta vendas com base na opção e critério fornecidos.
     * @param vendaDTO DTO com critérios (nome_cliente para opção 1, id_venda para opção 2).
     * @param opcao 1: por nome do cliente, 2: por ID da venda, 3: todas as vendas.
     * @return ResultSet com os dados das vendas.
     */
    public ResultSet consultarVenda(PcarDTO vendaDTO, int opcao) { //
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "";
        // Campos selecionados para a consulta
        String campos = "v.id, c.nome_cliente, ve.modelo_veiculo, ve.placa_veiculo, v.status_venda, v.data_venda, v.valor_venda ";
        
        try {
            ConexaoDAO.ConectDB();
            switch (opcao) {
                case 1: // Buscar por nome do cliente
                    sql = "SELECT " + campos +
                          "FROM vendas v " +
                          "JOIN clientes c ON v.id_cliente = c.id " +
                          "JOIN veiculos ve ON v.id_veiculo = ve.id " +
                          "WHERE c.nome_cliente ILIKE ? " + // ILIKE para case-insensitive no PostgreSQL
                          "ORDER BY v.data_venda";
                    ps = ConexaoDAO.con.prepareStatement(sql);
                    ps.setString(1, "%" + vendaDTO.getNome_cliente() + "%"); //
                    break;
                case 2: // Buscar por ID da venda
                    sql = "SELECT " + campos +
                          "FROM vendas v " +
                          "JOIN clientes c ON v.id_cliente = c.id " +
                          "JOIN veiculos ve ON v.id_veiculo = ve.id " +
                          "WHERE v.id = ?";
                    ps = ConexaoDAO.con.prepareStatement(sql);
                    ps.setInt(1, vendaDTO.getId_venda()); //
                    break;
                case 3: // Buscar todas as vendas
                    sql = "SELECT " + campos +
                          "FROM vendas v " +
                          "JOIN clientes c ON v.id_cliente = c.id " +
                          "JOIN veiculos ve ON v.id_veiculo = ve.id " +
                          "ORDER BY v.data_venda DESC"; // Mais recente primeiro
                    ps = ConexaoDAO.con.prepareStatement(sql);
                    break;
                default:
                    return null; // Opção inválida
            }
            rs = ps.executeQuery();
            // A conexão e o PreparedStatement serão fechados pela VendaCTR após o uso do ResultSet
            return rs;
        } catch (SQLException e) {
            System.err.println("Erro ao consultar venda DAO: " + e.getMessage());
            try { if (ps != null) ps.close(); } catch (SQLException ex) {}
            ConexaoDAO.CloseDB();
            return null;
        }
    }
    
    /**
     * Altera uma venda existente.
     * Espera que id_cliente, id_veiculo e id_venda (para o WHERE) estejam preenchidos no DTO.
     * @param vendaDTO O objeto PcarDTO com os dados da venda a ser alterada.
     * @return true se a alteração for bem-sucedida, false caso contrário.
     */
    public boolean alterarVenda(PcarDTO vendaDTO) { //
        PreparedStatement ps = null;
        String sql = "UPDATE vendas SET id_cliente = ?, id_veiculo = ?, data_venda = ?, valor_venda = ?, status_venda = ? " +
                     "WHERE id = ?";
        try {
            ConexaoDAO.ConectDB();
            ps = ConexaoDAO.con.prepareStatement(sql);

            ps.setInt(1, vendaDTO.getId_cliente()); //
            ps.setInt(2, vendaDTO.getId_veiculo()); //
            try {
                ps.setDate(3, Date.valueOf(vendaDTO.getData_venda())); //
            } catch (IllegalArgumentException e) {
                System.err.println("Formato de data inválido para alteração de venda: " + vendaDTO.getData_venda() + ". Use YYYY-MM-DD."); //
                return false;
            }
            ps.setDouble(4, vendaDTO.getValor_venda()); //
            ps.setString(5, vendaDTO.getStatus_venda()); //
            ps.setInt(6, vendaDTO.getId_venda()); // ID da venda para o WHERE

            int PcarSTATUS = ps.executeUpdate();
            if (PcarSTATUS > 0) {
                if(!ConexaoDAO.con.getAutoCommit()) ConexaoDAO.con.commit();
                return true;
            } else {
                return false; // Nenhuma linha afetada
            }
        } catch (SQLException e) {
            System.err.println("Erro ao alterar venda DAO: " + e.getMessage());
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) { System.err.println("Erro ao fechar PreparedStatement: " + e.getMessage());}
            ConexaoDAO.CloseDB();
        }
    }

    /**
     * Exclui uma venda do banco de dados.
     * @param vendaDTO O objeto PcarDTO contendo o ID da venda a ser excluída.
     * @return true se a exclusão for bem-sucedida, false caso contrário.
     */
   public boolean excluirVenda(PcarDTO vendaDTO) { //
       PreparedStatement ps = null;
       String sql = "DELETE FROM vendas WHERE id = ?";
        try {
            ConexaoDAO.ConectDB();
            ps = ConexaoDAO.con.prepareStatement(sql);
            ps.setInt(1, vendaDTO.getId_venda()); //

            int PcarSTATUS = ps.executeUpdate();
            if (PcarSTATUS > 0) {
                 if(!ConexaoDAO.con.getAutoCommit()) ConexaoDAO.con.commit();
                return true;
            } else {
                return false; // Nenhuma linha afetada
            }
        } catch (SQLException e) {
            System.err.println("Erro ao excluir venda DAO: " + e.getMessage());
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) { System.err.println("Erro ao fechar PreparedStatement: " + e.getMessage());}
            ConexaoDAO.CloseDB();
        }
    }
   
   // Dentro da classe VendaDAO.java

/**
 * Consulta todas as vendas de um cliente específico.
 * @param idCliente O ID do cliente (da tabela 'clientes').
 * @return ResultSet com os dados das vendas do cliente.
 */
public ResultSet consultarVendasPorCliente(int idCliente) {
    PreparedStatement ps = null;
    ResultSet rs = null;
    String sql = "SELECT v.id, ve.marca_veiculo, ve.modelo_veiculo, ve.placa_veiculo, " +
                 "v.data_venda, v.valor_venda, v.status_venda " +
                 "FROM vendas v " +
                 "JOIN veiculos ve ON v.id_veiculo = ve.id " +
                 "WHERE v.id_cliente = ? " +
                 "ORDER BY v.data_venda DESC"; // Mais recentes primeiro
    try {
        ConexaoDAO.ConectDB();
        ps = ConexaoDAO.con.prepareStatement(sql);
        ps.setInt(1, idCliente);
        rs = ps.executeQuery();
        // A conexão e o PreparedStatement serão fechados pela VendaCTR/VIEW
        return rs;
    } catch (SQLException e) {
        System.err.println("Erro ao consultar vendas por cliente DAO: " + e.getMessage());
        try { if (ps != null) ps.close(); } catch (SQLException ex) {}
        ConexaoDAO.CloseDB(); // Fecha em caso de erro
        return null;
    }
}

/**
 * Atualiza o status de uma venda para 'Cancelada'.
 * @param idVenda O ID da venda a ser cancelada.
 * @return true se a atualização for bem-sucedida, false caso contrário.
 */
public boolean cancelarVenda(int idVenda) {
    PreparedStatement ps = null;
    String sql = "UPDATE vendas SET status_venda = ? WHERE id = ? AND status_venda = ?";
    try {
        ConexaoDAO.ConectDB();
        ps = ConexaoDAO.con.prepareStatement(sql);
        ps.setString(1, "Cancelada"); // Novo status
        ps.setInt(2, idVenda);
        ps.setString(3, "Pendente");  // Só pode cancelar se estiver Pendente

        int PcarSTATUS = ps.executeUpdate();
        if (PcarSTATUS > 0) {
            if(!ConexaoDAO.con.getAutoCommit()) ConexaoDAO.con.commit();
            return true;
        } else {
            // Nenhuma linha afetada (ou a venda não estava Pendente ou não foi encontrada)
            return false;
        }
    } catch (SQLException e) {
        System.err.println("Erro ao cancelar venda DAO: " + e.getMessage());
        return false;
    } finally {
        try {
            if (ps != null) ps.close();
        } catch (SQLException e) { System.err.println("Erro ao fechar PreparedStatement: " + e.getMessage());}
        ConexaoDAO.CloseDB();
    }
}
}