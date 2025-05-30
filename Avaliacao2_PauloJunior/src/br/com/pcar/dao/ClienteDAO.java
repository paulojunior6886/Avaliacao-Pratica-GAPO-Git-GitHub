package br.com.pcar.dao;

import java.sql.*;
import br.com.pcar.dto.PcarDTO;

public class ClienteDAO {
    public ClienteDAO() { }

    // ResultSet e PreparedStatement devem ser declarados no escopo do método
    // ou fechados cuidadosamente se forem membros da classe e reutilizados.
    // Para maior clareza e segurança no gerenciamento de recursos, é melhor
    // declará-los dentro de cada método.

    public boolean inserirCliente(PcarDTO clienteDTO) {
        PreparedStatement ps = null;
        String sql = "INSERT INTO clientes (nome_cliente, cpf_cliente, endereco_cliente, telefone_cliente, email_cliente) VALUES (?, ?, ?, ?, ?)";
        try {
            ConexaoDAO.ConectDB();
            ps = ConexaoDAO.con.prepareStatement(sql);
            ps.setString(1, clienteDTO.getNome_cliente());
            ps.setString(2, clienteDTO.getCpf_cliente());
            ps.setString(3, clienteDTO.getEndereco_cliente());
            ps.setString(4, clienteDTO.getTelefone_cliente());
            ps.setString(5, clienteDTO.getEmail_cliente());

            int PcarSTATUS = ps.executeUpdate();
            if (PcarSTATUS > 0) {
                 if(ConexaoDAO.con.getAutoCommit() == false) ConexaoDAO.con.commit(); // Commit se não for autocommit
                return true;
            } else {
                return false;
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Erro de integridade (ex: CPF duplicado): " + e.getMessage());
            // Você pode querer propagar uma mensagem mais específica para a CTR/VIEW
            return false;
        } catch (SQLException e) {
            System.out.println("Erro ao inserir cliente DAO: " + e.getMessage());
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) { System.out.println("Erro ao fechar PreparedStatement: " + e.getMessage());}
            ConexaoDAO.CloseDB();
        }
    }

    public ResultSet consultarCliente(PcarDTO clienteDTO, int opcao) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "";
        try {
            ConexaoDAO.ConectDB();
            switch (opcao) {
                case 1: // Busca pelo nome
                    sql = "SELECT * FROM clientes WHERE nome_cliente ILIKE ? ORDER BY nome_cliente"; // Usar ILIKE para case-insensitive no PostgreSQL
                    ps = ConexaoDAO.con.prepareStatement(sql);
                    ps.setString(1, "%" + clienteDTO.getNome_cliente() + "%");
                    break;
                case 2: // Busca pelo ID do cliente
                    sql = "SELECT * FROM clientes WHERE id = ?";
                    ps = ConexaoDAO.con.prepareStatement(sql);
                    ps.setInt(1, clienteDTO.getId_cliente());
                    break;
                case 3: // Listar todos os clientes
                    sql = "SELECT * FROM clientes ORDER BY nome_cliente";
                    ps = ConexaoDAO.con.prepareStatement(sql);
                    break;
                default:
                    return null; // Opção inválida
            }
            rs = ps.executeQuery();
            // NÃO feche o PreparedStatement ou a Conexão aqui se o ResultSet for usado externamente.
            // A responsabilidade de fechar será da camada que consome o ResultSet.
            // A ClienteCTR tem o método CloseDB() para isso.
            return rs;
        } catch (SQLException e) {
            System.out.println("Erro ao consultar cliente DAO: " + e.getMessage());
            // Se houver erro, é importante fechar o ps e a conexão se foram abertos
            try { if (ps != null) ps.close(); } catch (SQLException ex) {}
            ConexaoDAO.CloseDB(); // Fechar em caso de erro para não vazar conexão
            return null;
        }
        // O 'finally' para fechar 'ps' e 'ConexaoDAO.CloseDB()' foi removido daqui
        // para permitir que o ResultSet seja usado pela VIEW. A VIEW/CTR deve chamar CloseDB().
    }

    public boolean alterarCliente(PcarDTO clienteDTO) {
        PreparedStatement ps = null;
        String sql = "UPDATE clientes SET nome_cliente = ?, cpf_cliente = ?, endereco_cliente = ?, telefone_cliente = ?, email_cliente = ? WHERE id = ?";
        try {
            ConexaoDAO.ConectDB();
            ps = ConexaoDAO.con.prepareStatement(sql);
            ps.setString(1, clienteDTO.getNome_cliente());
            ps.setString(2, clienteDTO.getCpf_cliente());
            ps.setString(3, clienteDTO.getEndereco_cliente());
            ps.setString(4, clienteDTO.getTelefone_cliente());
            ps.setString(5, clienteDTO.getEmail_cliente());
            ps.setInt(6, clienteDTO.getId_cliente());

            int PcarSTATUS = ps.executeUpdate();
            if (PcarSTATUS > 0) {
                if(ConexaoDAO.con.getAutoCommit() == false) ConexaoDAO.con.commit();
                return true;
            } else {
                return false; // Nenhuma linha afetada, cliente não encontrado com o ID?
            }
        } catch (SQLException e) {
            System.out.println("Erro ao alterar cliente DAO: " + e.getMessage());
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) { System.out.println("Erro ao fechar PreparedStatement: " + e.getMessage());}
            ConexaoDAO.CloseDB();
        }
    }

    public boolean excluirCliente(PcarDTO clienteDTO) {
        PreparedStatement ps = null;
        String sql = "DELETE FROM clientes WHERE id = ?";
        try {
            ConexaoDAO.ConectDB();
            ps = ConexaoDAO.con.prepareStatement(sql);
            ps.setInt(1, clienteDTO.getId_cliente());

            int PcarSTATUS = ps.executeUpdate();
            if (PcarSTATUS > 0) {
                 if(ConexaoDAO.con.getAutoCommit() == false) ConexaoDAO.con.commit();
                return true;
            } else {
                return false; // Nenhuma linha afetada
            }
        } catch (SQLException e) {
            System.out.println("Erro ao excluir cliente DAO: " + e.getMessage());
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) { System.out.println("Erro ao fechar PreparedStatement: " + e.getMessage());}
            ConexaoDAO.CloseDB();
        }
    }
    
    // Dentro da classe ClienteDAO.java

/**
 * Consulta um cliente pelo CPF e retorna seu ID.
 * @param cpf O CPF do cliente a ser buscado.
 * @return O ID do cliente se encontrado, ou -1 caso contrário.
 */
public int getIdClientePorCPF(String cpf) {
    PreparedStatement ps = null;
    ResultSet rs = null;
    String sql = "SELECT id FROM clientes WHERE cpf_cliente = ?";
    int idCliente = -1; // Valor padrão para não encontrado

    try {
        ConexaoDAO.ConectDB();
        ps = ConexaoDAO.con.prepareStatement(sql);
        ps.setString(1, cpf);
        rs = ps.executeQuery();

        if (rs.next()) {
            idCliente = rs.getInt("id");
        }
    } catch (SQLException e) {
        System.err.println("Erro ao consultar cliente por CPF no DAO: " + e.getMessage());
        // Não retorna exceção, apenas o ID -1 indicando falha ou não encontrado
    } finally {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        } catch (SQLException e) {
            System.err.println("Erro ao fechar ResultSet/PreparedStatement em getIdClientePorCPF: " + e.getMessage());
        }
        ConexaoDAO.CloseDB();
    }
    return idCliente;
}
}