/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.pcar.dao;

import br.com.pcar.dto.OpcionalDTO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; // Para obter o ID gerado
import java.util.ArrayList;
import java.util.List;

public class OpcionalDAO {

    public OpcionalDAO() {
    }

    public String inserirOpcional(OpcionalDTO opcionalDTO) {
        PreparedStatement ps = null;
        String sql = "INSERT INTO opcionais (nome_opcional, descricao_opcional, preco_adicional) VALUES (?, ?, ?)";
        try {
            ConexaoDAO.ConectDB();
            // Usar RETURN_GENERATED_KEYS para obter o ID do opcional inserido, se necessário
            ps = ConexaoDAO.con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, opcionalDTO.getNome_opcional());
            ps.setString(2, opcionalDTO.getDescricao_opcional());
            ps.setDouble(3, opcionalDTO.getPreco_adicional());

            if (ps.executeUpdate() > 0) {
                // Opcional: Obter o ID gerado se precisar dele imediatamente
                // ResultSet generatedKeys = ps.getGeneratedKeys();
                // if (generatedKeys.next()) {
                //     opcionalDTO.setId_opcional(generatedKeys.getInt(1));
                // }
                if (!ConexaoDAO.con.getAutoCommit()) ConexaoDAO.con.commit();
                return "Opcional cadastrado com sucesso!";
            } else {
                return "Erro ao cadastrar opcional.";
            }
        } catch (SQLException e) {
            System.err.println("Erro ao inserir opcional DAO: " + e.getMessage());
            if (e.getSQLState().equals("23505")) { // Código de violação de constraint UNIQUE (varia por BD)
                 return "Erro: Opcional com este nome já existe.";
            }
            return "Erro ao cadastrar opcional: " + e.getMessage();
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) { System.err.println("Erro ao fechar PS: " + e.getMessage()); }
            ConexaoDAO.CloseDB();
        }
    }

    public String alterarOpcional(OpcionalDTO opcionalDTO) {
        PreparedStatement ps = null;
        String sql = "UPDATE opcionais SET nome_opcional = ?, descricao_opcional = ?, preco_adicional = ? WHERE id_opcional = ?";
        try {
            ConexaoDAO.ConectDB();
            ps = ConexaoDAO.con.prepareStatement(sql);
            ps.setString(1, opcionalDTO.getNome_opcional());
            ps.setString(2, opcionalDTO.getDescricao_opcional());
            ps.setDouble(3, opcionalDTO.getPreco_adicional());
            ps.setInt(4, opcionalDTO.getId_opcional());

            if (ps.executeUpdate() > 0) {
                if (!ConexaoDAO.con.getAutoCommit()) ConexaoDAO.con.commit();
                return "Opcional alterado com sucesso!";
            } else {
                return "Opcional não encontrado ou dados inalterados.";
            }
        } catch (SQLException e) {
            System.err.println("Erro ao alterar opcional DAO: " + e.getMessage());
            if (e.getSQLState().equals("23505")) {
                 return "Erro: Já existe um opcional com este nome.";
            }
            return "Erro ao alterar opcional: " + e.getMessage();
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) { System.err.println("Erro ao fechar PS: " + e.getMessage()); }
            ConexaoDAO.CloseDB();
        }
    }

    public String excluirOpcional(int idOpcional) {
        PreparedStatement ps = null;
        String sql = "DELETE FROM opcionais WHERE id_opcional = ?";
        try {
            ConexaoDAO.ConectDB();
            ps = ConexaoDAO.con.prepareStatement(sql);
            ps.setInt(1, idOpcional);

            if (ps.executeUpdate() > 0) {
                if (!ConexaoDAO.con.getAutoCommit()) ConexaoDAO.con.commit();
                return "Opcional excluído com sucesso!";
            } else {
                return "Opcional não encontrado.";
            }
        } catch (SQLException e) {
            System.err.println("Erro ao excluir opcional DAO: " + e.getMessage());
            // Verificar erro de FK se 'opcionais' for referenciado em 'veiculo_opcionais'
             if (e.getSQLState().startsWith("23")) { // Códigos de erro de integridade
                return "Erro: Opcional não pode ser excluído pois está associado a veículos.";
            }
            return "Erro ao excluir opcional: " + e.getMessage();
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) { System.err.println("Erro ao fechar PS: " + e.getMessage()); }
            ConexaoDAO.CloseDB();
        }
    }

    /**
     * Consulta opcionais.
     * @param opcao 1 para listar todos, 2 para buscar por ID, 3 para buscar por nome.
     * @param dtoFiltro DTO com o ID ou nome para filtro (ignorado se opcao=1).
     * @return ResultSet com os opcionais.
     */
    public ResultSet consultarOpcionais(int opcao, OpcionalDTO dtoFiltro) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "";
        String colunas = "id_opcional, nome_opcional, descricao_opcional, preco_adicional";
        try {
            ConexaoDAO.ConectDB();
            switch (opcao) {
                case 1: // Listar todos
                    sql = "SELECT " + colunas + " FROM opcionais ORDER BY nome_opcional";
                    ps = ConexaoDAO.con.prepareStatement(sql);
                    break;
                case 2: // Buscar por ID
                    if (dtoFiltro == null || dtoFiltro.getId_opcional() == 0) return null;
                    sql = "SELECT " + colunas + " FROM opcionais WHERE id_opcional = ?";
                    ps = ConexaoDAO.con.prepareStatement(sql);
                    ps.setInt(1, dtoFiltro.getId_opcional());
                    break;
                case 3: // Buscar por Nome
                    if (dtoFiltro == null || dtoFiltro.getNome_opcional() == null || dtoFiltro.getNome_opcional().trim().isEmpty()) return null;
                    sql = "SELECT " + colunas + " FROM opcionais WHERE nome_opcional ILIKE ? ORDER BY nome_opcional";
                    ps = ConexaoDAO.con.prepareStatement(sql);
                    ps.setString(1, "%" + dtoFiltro.getNome_opcional() + "%");
                    break;
                default:
                    System.err.println("Opção de consulta inválida para opcionais.");
                    ConexaoDAO.CloseDB(); // Fecha se a opção for inválida antes de retornar
                    return null;
            }
            rs = ps.executeQuery();
            // Conexão será fechada pela CTR/VIEW
            return rs;
        } catch (SQLException e) {
            System.err.println("Erro ao consultar opcionais DAO: " + e.getMessage());
            try { if (ps != null) ps.close(); } catch (SQLException ex) {}
            ConexaoDAO.CloseDB();
            return null;
        }
    }
}
