package br.com.pcar.ctr;

import java.sql.ResultSet;
import br.com.pcar.dto.PcarDTO;
import br.com.pcar.dao.ClienteDAO;
import br.com.pcar.dao.ConexaoDAO;

public class ClienteCTR {

    ClienteDAO clienteDAO = new ClienteDAO();

    public ClienteCTR() {
    }

    public String inserirCliente(PcarDTO clienteDTO) {
        try {
            if (clienteDAO.inserirCliente(clienteDTO)) {
                return "Cliente cadastrado com sucesso!";
            } else {
                return "Erro ao cadastrar cliente!";
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "Erro ao cadastrar cliente!";
        }
    }

    public ResultSet consultarCliente(PcarDTO clienteDTO, int opcao) {
        return clienteDAO.consultarCliente(clienteDTO, opcao);
    }

    public String alterarCliente(PcarDTO clienteDTO) {
        try {
            if (clienteDAO.alterarCliente(clienteDTO)) {
                return "Cliente alterado com sucesso!";
            } else {
                return "Erro ao alterar cliente!";
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "Erro ao alterar cliente!";
        }
    }

    public String excluirCliente(PcarDTO clienteDTO) {
        try {
            if (clienteDAO.excluirCliente(clienteDTO)) {
                return "Cliente excluído com sucesso!";
            } else {
                return "Erro ao excluir cliente!";
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "Erro ao excluir cliente!";
        }
    }

    // Dentro da classe ClienteCTR.java
    /**
     * Busca o ID de um cliente pelo seu CPF.
     *
     * @param cpf O CPF do cliente.
     * @return O ID do cliente se encontrado, ou -1 caso contrário.
     */
    public int buscarIdClientePorCPF(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            return -1; // CPF inválido
        }
        // Se você já tem uma instância de ClienteDAO na classe, use-a.
        // ClienteDAO clienteDAO = new ClienteDAO(); // Ou use a instância da classe se já existir
        return clienteDAO.getIdClientePorCPF(cpf); // Assumindo que clienteDAO é um membro da classe
    }

    public void CloseDB() {
        ConexaoDAO.CloseDB();
    }
}
