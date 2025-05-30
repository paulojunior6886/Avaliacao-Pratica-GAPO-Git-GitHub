package br.com.pcar.dao;

import java.sql.*;
import br.com.pcar.dto.UsuarioDTO;

/**
 *
 * @author paulo
 */
public class UsuarioDAO {

    private PreparedStatement ps = null; // Mover para ser reutilizável nos métodos
    private ResultSet rs = null;     // Mover para ser reutilizável nos métodos

    /**
     * Método para autenticar um usuário no sistema.
     * @param login O login do usuário.
     * @param senha A senha do usuário.
     * @return Um objeto UsuarioDTO se a autenticação for bem-sucedida, caso contrário null.
     */
    public UsuarioDTO autenticarUsuario(String login, String senha) { //
        UsuarioDTO usuario = null;
        try {
            ConexaoDAO.ConectDB();
            ps = ConexaoDAO.con.prepareStatement(
                "SELECT id, nome_usuario, cpf_usuario, login_usuario, tipo_usuario FROM usuarios WHERE login_usuario = ? AND senha_usuario = ?" // Não retornar a senha
            );
            ps.setString(1, login);
            ps.setString(2, senha); // Idealmente, a senha no banco deve ser hashed. Esta comparação é para senhas em texto plano.
            rs = ps.executeQuery();

            if (rs.next()) {
                usuario = new UsuarioDTO();
                usuario.setId(rs.getInt("id"));
                usuario.setNome_usuario(rs.getString("nome_usuario"));
                usuario.setCpf_usuario(rs.getString("cpf_usuario"));
                usuario.setLogin_usuario(rs.getString("login_usuario"));
                usuario.setTipo_usuario(rs.getString("tipo_usuario"));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao autenticar usuário DAO: " + e.getMessage());
        } finally {
            ConexaoDAO.CloseDB(); // Garante que ps e rs sejam fechados se ConexaoDAO.CloseDB() os fechar.
                                 // Caso contrário, fechar ps e rs aqui.
        }
        return usuario;
    }

    /**
     * Método para inserir um novo usuário no banco de dados.
     * @param usuarioDTO O objeto UsuarioDTO contendo os dados do usuário a ser inserido.
     * @return Uma mensagem indicando o sucesso ou falha da operação.
     */
    public String inserirUsuario(UsuarioDTO usuarioDTO) {
        try {
            ConexaoDAO.ConectDB();
            ps = ConexaoDAO.con.prepareStatement(
                "INSERT INTO usuarios (nome_usuario, cpf_usuario, login_usuario, senha_usuario, tipo_usuario) VALUES (?, ?, ?, ?, ?)"
            );
            ps.setString(1, usuarioDTO.getNome_usuario());
            ps.setString(2, usuarioDTO.getCpf_usuario());
            ps.setString(3, usuarioDTO.getLogin_usuario());
            ps.setString(4, usuarioDTO.getSenha_usuario()); // Idealmente, a senha deve ser hashed antes de inserir.
            ps.setString(5, usuarioDTO.getTipo_usuario());
            
            int PcarSTATUS = ps.executeUpdate();
            
            if (PcarSTATUS > 0) {
                ConexaoDAO.con.commit(); // Não é necessário se autoCommit=true
                return "Usuário cadastrado com sucesso!";
            } else {
                return "Erro ao cadastrar usuário: Nenhuma linha afetada.";
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            // Trata violações de constraint, como CPF ou Login duplicado
             try { ConexaoDAO.con.rollback(); } catch (SQLException ex) { System.out.println("Erro no rollback: " + ex.getMessage()); }
            if (e.getMessage().toLowerCase().contains("cpf_usuario_unique")) { // Ajuste o nome da constraint conforme seu BD
                return "Erro ao cadastrar usuário: CPF já cadastrado.";
            } else if (e.getMessage().toLowerCase().contains("login_usuario_unique")) { // Ajuste o nome da constraint
                return "Erro ao cadastrar usuário: Login já cadastrado.";
            }
            return "Erro ao cadastrar usuário (constraint): " + e.getMessage();
        } catch (SQLException e) {
             try { ConexaoDAO.con.rollback(); } catch (SQLException ex) { System.out.println("Erro no rollback: " + ex.getMessage()); }
            System.out.println("Erro ao inserir usuário DAO: " + e.getMessage());
            return "Erro ao cadastrar usuário!";
        } finally {
            ConexaoDAO.CloseDB();
        }
    }

    /**
     * Método para alterar os dados de um usuário existente.
     * @param usuarioDTO O objeto UsuarioDTO contendo os dados atualizados.
     * @return Uma mensagem indicando o sucesso ou falha da operação.
     */
    public String alterarUsuario(UsuarioDTO usuarioDTO) {
        try {
            ConexaoDAO.ConectDB();
            // Não permitir alteração de login ou CPF para evitar problemas de integridade,
            // ou tratar com muito cuidado. Senha pode ser alterada em um método separado.
            String sql = "UPDATE usuarios SET nome_usuario = ?, tipo_usuario = ?";
            if (usuarioDTO.getSenha_usuario() != null && !usuarioDTO.getSenha_usuario().isEmpty()) {
                sql += ", senha_usuario = ?";
            }
            sql += " WHERE id = ?";

            ps = ConexaoDAO.con.prepareStatement(sql);
            ps.setString(1, usuarioDTO.getNome_usuario());
            ps.setString(2, usuarioDTO.getTipo_usuario());
            
            int parameterIndex = 3;
            if (usuarioDTO.getSenha_usuario() != null && !usuarioDTO.getSenha_usuario().isEmpty()) {
                ps.setString(parameterIndex++, usuarioDTO.getSenha_usuario()); // Idealmente, hashear nova senha
            }
            ps.setInt(parameterIndex, usuarioDTO.getId());

            if (ps.executeUpdate() > 0) {
                ConexaoDAO.con.commit();
                return "Usuário alterado com sucesso!";
            } else {
                return "Erro ao alterar usuário: Usuário não encontrado ou dados inalterados.";
            }
        } catch (SQLException e) {
            try { ConexaoDAO.con.rollback(); } catch (SQLException ex) { System.out.println("Erro no rollback: " + ex.getMessage()); }
            System.out.println("Erro ao alterar usuário DAO: " + e.getMessage());
            return "Erro ao alterar usuário!";
        } finally {
            ConexaoDAO.CloseDB();
        }
    }

    /**
     * Método para excluir um usuário do banco de dados.
     * @param usuarioDTO O objeto UsuarioDTO contendo o ID do usuário a ser excluído.
     * @return Uma mensagem indicando o sucesso ou falha da operação.
     */
    public String excluirUsuario(UsuarioDTO usuarioDTO) {
        try {
            ConexaoDAO.ConectDB();
            ps = ConexaoDAO.con.prepareStatement("DELETE FROM usuarios WHERE id = ?");
            ps.setInt(1, usuarioDTO.getId());

            if (ps.executeUpdate() > 0) {
                ConexaoDAO.con.commit();
                return "Usuário excluído com sucesso!";
            } else {
                return "Erro ao excluir usuário: Usuário não encontrado.";
            }
        } catch (SQLException e) {
             try { ConexaoDAO.con.rollback(); } catch (SQLException ex) { System.out.println("Erro no rollback: " + ex.getMessage()); }
            System.out.println("Erro ao excluir usuário DAO: " + e.getMessage());
            return "Erro ao excluir usuário!";
        } finally {
            ConexaoDAO.CloseDB();
        }
    }

    /**
     * Método para consultar usuários.
     * @param usuarioDTO DTO para os critérios de busca (nome, cpf).
     * @param opcao Tipo de consulta (1: por nome, 2: por CPF, 3: todos).
     * @return Um ResultSet com os resultados da consulta.
     */
    public ResultSet consultarUsuario(UsuarioDTO usuarioDTO, int opcao) {
        rs = null; 
        try {
            ConexaoDAO.ConectDB();
            String comando = "";
            switch (opcao) {
                case 1: // Consultar por nome
                    comando = "SELECT id, nome_usuario, cpf_usuario, login_usuario, tipo_usuario FROM usuarios WHERE nome_usuario ILIKE ?";
                    ps = ConexaoDAO.con.prepareStatement(comando);
                    ps.setString(1, "%" + usuarioDTO.getNome_usuario() + "%");
                    break;
                case 2: // Consultar por CPF
                    comando = "SELECT id, nome_usuario, cpf_usuario, login_usuario, tipo_usuario FROM usuarios WHERE cpf_usuario = ?";
                    ps = ConexaoDAO.con.prepareStatement(comando);
                    ps.setString(1, usuarioDTO.getCpf_usuario());
                    break;
                case 3: // Listar todos
                    comando = "SELECT id, nome_usuario, cpf_usuario, login_usuario, tipo_usuario FROM usuarios ORDER BY nome_usuario";
                    ps = ConexaoDAO.con.prepareStatement(comando);
                    break;
                default:
                    // Lançar uma exceção ou retornar null/vazio se a opção for inválida
                    return null; 
            }
            rs = ps.executeQuery();
            // O ResultSet será fechado junto com a conexão em CloseDB, ou explicitamente pela VIEW/CTR.
            // Não feche ps ou rs aqui se você pretende usá-lo fora deste método.
        } catch (SQLException e) {
            System.out.println("Erro ao consultar usuário DAO: " + e.getMessage());
            // Não feche a conexão aqui se o rs ainda for necessário, mas lide com o fechamento em caso de erro.
        }
        // Não feche a ConexaoDAO.CloseDB() aqui se o ResultSet precisar ser usado externamente.
        // A classe que chama este método será responsável por fechar a conexão após usar o ResultSet.
        return rs;
    }
}