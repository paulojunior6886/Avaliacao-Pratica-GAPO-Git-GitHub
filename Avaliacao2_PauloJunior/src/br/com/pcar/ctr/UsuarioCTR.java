package br.com.pcar.ctr;

import br.com.pcar.dao.UsuarioDAO;
import br.com.pcar.dto.UsuarioDTO;
import java.sql.ResultSet;
import br.com.pcar.dao.ConexaoDAO;

/**
 *
 * @author paulo
 */
public class UsuarioCTR {
    
    UsuarioDAO usuarioDAO = new UsuarioDAO();

    /**
     * Autentica um usuário.
     * @param login O login do usuário.
     * @param senha A senha do usuário.
     * @return UsuarioDTO com os dados do usuário autenticado, ou null se falhar.
     */
    public UsuarioDTO autenticar(String login, String senha) { //
        return usuarioDAO.autenticarUsuario(login, senha);
    }

    /**
     * Insere um novo usuário.
     * @param usuarioDTO Os dados do usuário a serem inseridos.
     * @return Mensagem de PcarSTATUS.
     */
    public String inserirUsuario(UsuarioDTO usuarioDTO) {
        try {
            if (usuarioDAO.inserirUsuario(usuarioDTO).startsWith("Usuário cadastrado")) {
                return "Usuário cadastrado com sucesso!";
            } else {
                return usuarioDAO.inserirUsuario(usuarioDTO); // Retorna a mensagem de erro específica do DAO
            }
        } catch (Exception e) {
            System.out.println("Erro ao inserir usuário CTR: " + e.getMessage());
            return "Erro ao cadastrar usuário!";
        }
    }

    /**
     * Altera um usuário existente.
     * @param usuarioDTO Os dados do usuário a serem alterados.
     * @return Mensagem de PcarSTATUS.
     */
    public String alterarUsuario(UsuarioDTO usuarioDTO) {
        try {
            if (usuarioDAO.alterarUsuario(usuarioDTO).startsWith("Usuário alterado")) {
                return "Usuário alterado com sucesso!";
            } else {
                return usuarioDAO.alterarUsuario(usuarioDTO); // Retorna a mensagem de erro específica do DAO
            }
        } catch (Exception e) {
            System.out.println("Erro ao alterar usuário CTR: " + e.getMessage());
            return "Erro ao alterar usuário!";
        }
    }

    /**
     * Exclui um usuário.
     * @param usuarioDTO O usuário a ser excluído (necessário ID).
     * @return Mensagem de PcarSTATUS.
     */
    public String excluirUsuario(UsuarioDTO usuarioDTO) {
        try {
            if (usuarioDAO.excluirUsuario(usuarioDTO).startsWith("Usuário excluído")) {
                return "Usuário excluído com sucesso!";
            } else {
                return usuarioDAO.excluirUsuario(usuarioDTO); // Retorna a mensagem de erro específica do DAO
            }
        } catch (Exception e) {
            System.out.println("Erro ao excluir usuário CTR: " + e.getMessage());
            return "Erro ao excluir usuário!";
        }
    }

    /**
     * Consulta usuários.
     * @param usuarioDTO Critérios de busca.
     * @param opcao Opção de consulta.
     * @return ResultSet com os resultados.
     */
    public ResultSet consultarUsuario(UsuarioDTO usuarioDTO, int opcao) {
        // A CTR simplesmente repassa a chamada para o DAO neste caso.
        // A VIEW que chamar este método será responsável por iterar sobre o ResultSet
        // e fechar a conexão através de um método na CTR ou diretamente no ConexaoDAO.
        return usuarioDAO.consultarUsuario(usuarioDTO, opcao);
    }
    
    /**
    * Método para fechar a conexão com o banco de dados.
    * Deve ser chamado após operações de consulta que retornam ResultSet,
    * depois que o ResultSet não for mais necessário.
    */
    public void closeDbConnection() {
        ConexaoDAO.CloseDB();
    }
}