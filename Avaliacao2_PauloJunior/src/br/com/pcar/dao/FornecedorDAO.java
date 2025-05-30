/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.pcar.dao;

import br.com.pcar.dto.FornecedorDTO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

public class FornecedorDAO {

    public FornecedorDAO() {
    }

    /**
     * Insere um novo fornecedor no banco de dados.
     * @param fornecedorDTO O objeto FornecedorDTO contendo os dados.
     * @return String indicando o resultado da operação.
     */
    public String inserirFornecedor(FornecedorDTO fornecedorDTO) {
        PreparedStatement ps = null;
        String sql = "INSERT INTO fornecedores (nome_fornecedor, cnpj_fornecedor, endereco_fornecedor, " +
                     "telefone_fornecedor, email_fornecedor, contato_principal) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            ConexaoDAO.ConectDB();
            ps = ConexaoDAO.con.prepareStatement(sql);
            ps.setString(1, fornecedorDTO.getNome_fornecedor());
            ps.setString(2, fornecedorDTO.getCnpj_fornecedor());
            ps.setString(3, fornecedorDTO.getEndereco_fornecedor());
            ps.setString(4, fornecedorDTO.getTelefone_fornecedor());
            ps.setString(5, fornecedorDTO.getEmail_fornecedor());
            ps.setString(6, fornecedorDTO.getContato_principal());

            if (ps.executeUpdate() > 0) {
                if (!ConexaoDAO.con.getAutoCommit()) ConexaoDAO.con.commit();
                return "Fornecedor cadastrado com sucesso!";
            } else {
                return "Erro ao cadastrar fornecedor: Nenhuma linha afetada.";
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("Erro de integridade ao inserir fornecedor (CNPJ duplicado?): " + e.getMessage());
             try { if(!ConexaoDAO.con.getAutoCommit()) ConexaoDAO.con.rollback(); } catch (SQLException ex) { System.err.println("Erro no rollback: " + ex.getMessage()); }
            if (e.getMessage().toLowerCase().contains("cnpj_fornecedor")) { // Ajustar conforme o nome da constraint do seu BD
                return "Erro: CNPJ já cadastrado.";
            }
            return "Erro de integridade ao cadastrar fornecedor.";
        } catch (SQLException e) {
            System.err.println("Erro ao inserir fornecedor DAO: " + e.getMessage());
             try { if(!ConexaoDAO.con.getAutoCommit()) ConexaoDAO.con.rollback(); } catch (SQLException ex) { System.err.println("Erro no rollback: " + ex.getMessage()); }
            return "Erro ao cadastrar fornecedor no banco de dados.";
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) { System.err.println("Erro ao fechar PreparedStatement: " + e.getMessage()); }
            ConexaoDAO.CloseDB();
        }
    }

    /**
     * Altera os dados de um fornecedor existente.
     * @param fornecedorDTO O objeto FornecedorDTO com os dados atualizados.
     * @return String indicando o resultado da operação.
     */
    public String alterarFornecedor(FornecedorDTO fornecedorDTO) {
        PreparedStatement ps = null;
        String sql = "UPDATE fornecedores SET nome_fornecedor = ?, cnpj_fornecedor = ?, endereco_fornecedor = ?, " +
                     "telefone_fornecedor = ?, email_fornecedor = ?, contato_principal = ? WHERE id = ?";
        try {
            ConexaoDAO.ConectDB();
            ps = ConexaoDAO.con.prepareStatement(sql);
            ps.setString(1, fornecedorDTO.getNome_fornecedor());
            ps.setString(2, fornecedorDTO.getCnpj_fornecedor());
            ps.setString(3, fornecedorDTO.getEndereco_fornecedor());
            ps.setString(4, fornecedorDTO.getTelefone_fornecedor());
            ps.setString(5, fornecedorDTO.getEmail_fornecedor());
            ps.setString(6, fornecedorDTO.getContato_principal());
            ps.setInt(7, fornecedorDTO.getId_fornecedor());

            if (ps.executeUpdate() > 0) {
                if (!ConexaoDAO.con.getAutoCommit()) ConexaoDAO.con.commit();
                return "Fornecedor alterado com sucesso!";
            } else {
                return "Fornecedor não encontrado ou dados inalterados.";
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("Erro de integridade ao alterar fornecedor (CNPJ duplicado?): " + e.getMessage());
            try { if(!ConexaoDAO.con.getAutoCommit()) ConexaoDAO.con.rollback(); } catch (SQLException ex) { System.err.println("Erro no rollback: " + ex.getMessage()); }
             if (e.getMessage().toLowerCase().contains("cnpj_fornecedor")) {
                return "Erro: CNPJ já cadastrado para outro fornecedor.";
            }
            return "Erro de integridade ao alterar fornecedor.";
        } catch (SQLException e) {
            System.err.println("Erro ao alterar fornecedor DAO: " + e.getMessage());
            try { if(!ConexaoDAO.con.getAutoCommit()) ConexaoDAO.con.rollback(); } catch (SQLException ex) { System.err.println("Erro no rollback: " + ex.getMessage()); }
            return "Erro ao alterar fornecedor no banco de dados.";
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) { System.err.println("Erro ao fechar PreparedStatement: " + e.getMessage()); }
            ConexaoDAO.CloseDB();
        }
    }

    /**
     * Exclui um fornecedor do banco de dados.
     * @param fornecedorDTO O objeto FornecedorDTO contendo o ID do fornecedor.
     * @return String indicando o resultado da operação.
     */
    public String excluirFornecedor(FornecedorDTO fornecedorDTO) {
        PreparedStatement ps = null;
        String sql = "DELETE FROM fornecedores WHERE id = ?";
        try {
            ConexaoDAO.ConectDB();
            ps = ConexaoDAO.con.prepareStatement(sql);
            ps.setInt(1, fornecedorDTO.getId_fornecedor());

            if (ps.executeUpdate() > 0) {
                 if (!ConexaoDAO.con.getAutoCommit()) ConexaoDAO.con.commit();
                return "Fornecedor excluído com sucesso!";
            } else {
                return "Fornecedor não encontrado.";
            }
        } catch (SQLException e) {
            System.err.println("Erro ao excluir fornecedor DAO: " + e.getMessage());
            // Verificar se é erro de FK antes de um rollback genérico
            // Ex: if (e.getSQLState().equals("23503")) { return "Erro: Fornecedor não pode ser excluído pois está associado a outros registros."; }
            try { if(!ConexaoDAO.con.getAutoCommit()) ConexaoDAO.con.rollback(); } catch (SQLException ex) { System.err.println("Erro no rollback: " + ex.getMessage()); }
            return "Erro ao excluir fornecedor (verifique dependências).";
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) { System.err.println("Erro ao fechar PreparedStatement: " + e.getMessage()); }
            ConexaoDAO.CloseDB();
        }
    }

    /**
     * Consulta fornecedores.
     * @param fornecedorDTO DTO com critérios de busca (nome, cnpj).
     * @param opcao Tipo de consulta (1: por nome, 2: por CNPJ, 3: por ID, 4: todos).
     * @return Um ResultSet com os resultados.
     */
    public ResultSet consultarFornecedor(FornecedorDTO fornecedorDTO, int opcao) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "";
        String colunas = "id, nome_fornecedor, cnpj_fornecedor, endereco_fornecedor, telefone_fornecedor, email_fornecedor, contato_principal";
        try {
            ConexaoDAO.ConectDB();
            switch (opcao) {
                case 1: // Consultar por nome
                    sql = "SELECT " + colunas + " FROM fornecedores WHERE nome_fornecedor ILIKE ? ORDER BY nome_fornecedor";
                    ps = ConexaoDAO.con.prepareStatement(sql);
                    ps.setString(1, "%" + fornecedorDTO.getNome_fornecedor() + "%");
                    break;
                case 2: // Consultar por CNPJ
                    sql = "SELECT " + colunas + " FROM fornecedores WHERE cnpj_fornecedor = ?";
                    ps = ConexaoDAO.con.prepareStatement(sql);
                    ps.setString(1, fornecedorDTO.getCnpj_fornecedor());
                    break;
                case 3: // Consultar por ID
                    sql = "SELECT " + colunas + " FROM fornecedores WHERE id = ?";
                    ps = ConexaoDAO.con.prepareStatement(sql);
                    ps.setInt(1, fornecedorDTO.getId_fornecedor());
                    break;
                case 4: // Listar todos
                    sql = "SELECT " + colunas + " FROM fornecedores ORDER BY nome_fornecedor";
                    ps = ConexaoDAO.con.prepareStatement(sql);
                    break;
                default:
                    System.err.println("Opção de consulta inválida para fornecedor.");
                    return null;
            }
            rs = ps.executeQuery();
            // A conexão e o PreparedStatement NÃO são fechados aqui para permitir o uso do ResultSet.
            // A camada CTR/VIEW será responsável por chamar CloseDB().
            return rs;
        } catch (SQLException e) {
            System.err.println("Erro ao consultar fornecedor DAO: " + e.getMessage());
            try { if (ps != null) ps.close(); } catch (SQLException ex) { System.err.println("Erro ao fechar PS na consulta: " + ex.getMessage());}
            ConexaoDAO.CloseDB(); // Fecha a conexão em caso de erro na consulta.
            return null;
        }
    }
}