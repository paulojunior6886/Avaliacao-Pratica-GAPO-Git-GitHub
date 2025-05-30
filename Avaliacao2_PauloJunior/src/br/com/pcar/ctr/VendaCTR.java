package br.com.pcar.ctr;

import java.sql.ResultSet;
import br.com.pcar.dto.PcarDTO; //
import br.com.pcar.dao.VendaDAO; //
import br.com.pcar.dao.ConexaoDAO;
import br.com.pcar.dao.ClienteDAO; // Para o método consultarCliente

public class VendaCTR {

    VendaDAO vendaDAO = new VendaDAO(); //
    ClienteCTR clienteCTR = new ClienteCTR(); // Para buscar clientes para ComboBox
    VeiculoCTR veiculoCTR = new VeiculoCTR(); // Para buscar veículos para ComboBox

    public VendaCTR() {
    }

    /**
     * Insere uma nova venda.
     * @param vendaDTO Dados da venda.
     * @return Mensagem de PcarSTATUS.
     */
    public String inserirVenda(PcarDTO vendaDTO) { //
        try {
            // Validações de regra de negócio podem ser adicionadas aqui antes de chamar o DAO
            // Ex: Verificar se o cliente existe, se o veículo está disponível, etc.
            if (vendaDTO.getId_cliente() <= 0 || vendaDTO.getId_veiculo() <= 0) { //
                return "Erro: Cliente ou Veículo não selecionado corretamente.";
            }
            if (vendaDTO.getData_venda() == null || vendaDTO.getData_venda().trim().isEmpty()) { //
                return "Erro: Data da venda é obrigatória.";
            }
             // Adicionar mais validações conforme necessário

            if (vendaDAO.inserirVenda(vendaDTO)) { //
                return "Venda registrada com sucesso!";
            } else {
                return "Erro ao registrar venda no banco de dados!";
            }
        } catch (Exception e) {
            System.err.println("Erro na VendaCTR ao inserirVenda: " + e.getMessage());
            return "Erro ao registrar venda!";
        }
    }

    /**
     * Consulta vendas.
     * @param vendaDTO Critérios de consulta.
     * @param opcao Tipo de consulta.
     * @return ResultSet com as vendas.
     */
    public ResultSet consultarVenda(PcarDTO vendaDTO, int opcao) { //
        // A VendaVIEW será responsável por fechar a conexão após usar o ResultSet
        // chamando o método CloseDB() desta CTR.
        return vendaDAO.consultarVenda(vendaDTO, opcao); //
    }

    /**
     * Consulta todos os clientes para popular ComboBoxes na VendaVIEW.
     * @return ResultSet com ID e nome dos clientes.
     */
    public ResultSet consultarTodosClientesParaVenda() {
        // Reutiliza a lógica já existente em ClienteCTR
        // ClienteCTR.consultarCliente(dto, 3) lista todos.
        // Precisamos de um DTO para ClienteCTR, pode ser um PcarDTO vazio.
        PcarDTO clienteDtoFiltro = new PcarDTO(); //
        return clienteCTR.consultarCliente(clienteDtoFiltro, 3); // Opção 3 para listar todos
    }
    
    /**
     * Consulta todos os veículos para popular ComboBoxes na VendaVIEW.
     * Idealmente, consultaria apenas veículos disponíveis para venda.
     * @return ResultSet com ID, modelo e placa dos veículos.
     */
    public ResultSet consultarTodosVeiculosParaVenda() {
        // Reutiliza a lógica já existente em VeiculoCTR
        // VeiculoCTR.consultarVeiculo(dto, 3) lista todos.
        PcarDTO veiculoDtoFiltro = new PcarDTO(); //
        // Poderia adaptar VeiculoDAO/CTR para ter uma opção de "veículos disponíveis"
        return veiculoCTR.consultarVeiculo(veiculoDtoFiltro, 3); // Opção 3 para listar todos
    }


    /**
     * Altera uma venda existente.
     * @param vendaDTO Dados da venda para alterar.
     * @return Mensagem de PcarSTATUS.
     */
    public String alterarVenda(PcarDTO vendaDTO) { //
        try {
            if (vendaDTO.getId_venda() <= 0) { //
                return "Erro: ID da venda inválido para alteração.";
            }
            // Adicionar outras validações necessárias
            
            if (vendaDAO.alterarVenda(vendaDTO)) { //
                return "Venda alterada com sucesso!";
            } else {
                return "Erro ao alterar venda no banco de dados!";
            }
        } catch (Exception e) {
            System.err.println("Erro na VendaCTR ao alterarVenda: " + e.getMessage());
            return "Erro ao alterar venda!";
        }
    }

    /**
     * Exclui uma venda.
     * @param vendaDTO DTO com o ID da venda.
     * @return Mensagem de PcarSTATUS.
     */
    public String excluirVenda(PcarDTO vendaDTO) { //
        try {
            if (vendaDTO.getId_venda() <= 0) { //
                 return "Erro: ID da venda inválido para exclusão.";
            }
            if (vendaDAO.excluirVenda(vendaDTO)) { //
                return "Venda excluída com sucesso!";
            } else {
                return "Erro ao excluir venda no banco de dados!";
            }
        } catch (Exception e) {
            System.err.println("Erro na VendaCTR ao excluirVenda: " + e.getMessage());
            return "Erro ao excluir venda!";
        }
    }
    
    // Dentro da classe VendaCTR.java

/**
 * Consulta todas as vendas de um cliente específico.
 * @param idCliente O ID do cliente (da tabela 'clientes').
 * @return ResultSet com as vendas do cliente.
 */
public ResultSet consultarVendasPorCliente(int idCliente) {
    if (idCliente <= 0) {
        System.err.println("VendaCTR: ID do cliente inválido para consulta de vendas.");
        return null;
    }
    // A VIEW será responsável por fechar a conexão após usar o ResultSet
    // chamando o método CloseDB() desta CTR.
    return vendaDAO.consultarVendasPorCliente(idCliente);
}

/**
 * Tenta cancelar uma venda (muda o status para "Cancelada").
 * Só pode cancelar se o status atual for "Pendente".
 * @param idVenda O ID da venda a ser cancelada.
 * @return String indicando o resultado da operação.
 */
public String cancelarVendaPeloCliente(int idVenda) {
    if (idVenda <= 0) {
        return "Erro: ID da venda inválido.";
    }
    try {
        if (vendaDAO.cancelarVenda(idVenda)) {
            return "Venda cancelada com sucesso!";
        } else {
            return "Não foi possível cancelar a venda (pode não estar mais pendente ou não foi encontrada).";
        }
    } catch (Exception e) {
        System.err.println("Erro na VendaCTR ao cancelarVendaPeloCliente: " + e.getMessage());
        return "Erro ao tentar cancelar a venda.";
    }
}

    /**
     * Fecha a conexão com o banco de dados.
     * Deve ser chamado pela VIEW após operações de consulta que retornam ResultSet.
     */
    public void CloseDB() {
        ConexaoDAO.CloseDB();
    }
}