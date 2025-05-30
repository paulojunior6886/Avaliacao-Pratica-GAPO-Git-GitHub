package br.com.pcar.ctr;

import java.sql.ResultSet;
import br.com.pcar.dto.PcarDTO;
import br.com.pcar.dao.VeiculoDAO;
import br.com.pcar.dao.ConexaoDAO;
import java.util.ArrayList;
import java.util.List;

public class VeiculoCTR {

    VeiculoDAO veiculoDAO = new VeiculoDAO();

    public VeiculoCTR() {
    }

    // Dentro da classe VeiculoCTR.java

public String inserirVeiculo(PcarDTO veiculoDTO) { //
    try {
        // Validações de regra de negócio (se houver) podem vir aqui
        // Ex: if(veiculoDTO.getPreco_veiculo() <= 0) return "Preço inválido";

        // O veiculoDAO.inserirVeiculo agora atualiza o veiculoDTO com o ID gerado
        if (veiculoDAO.inserirVeiculo(veiculoDTO)) { //
            // Se o DAO teve sucesso e atualizou o DTO, o ID estará em veiculoDTO.getId_veiculo()
            return "Veículo cadastrado com sucesso! ID: " + veiculoDTO.getId_veiculo(); //
        } else {
            // Se chegou aqui, o ID no DTO pode não ter sido atualizado se o DAO retornou false.
            return "Erro ao cadastrar veículo no banco de dados!";
        }
    } catch (Exception e) {
        System.err.println("Erro na VeiculoCTR ao inserirVeiculo: " + e.getMessage());
        return "Erro crítico ao tentar cadastrar veículo!";
    }
}

    public ResultSet consultarVeiculo(PcarDTO veiculoDTO, int opcao) {
        return veiculoDAO.consultarVeiculo(veiculoDTO, opcao);
    }

    public String alterarVeiculo(PcarDTO veiculoDTO) {
        try {
            if (veiculoDAO.alterarVeiculo(veiculoDTO)) {
                return "Veículo alterado com sucesso!";
            } else {
                return "Erro ao alterar veículo!";
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "Erro ao alterar veículo!";
        }
    }

   // Dentro da classe VeiculoCTR.java
public String excluirVeiculo(PcarDTO veiculoDTO) { //
    if (veiculoDTO == null || veiculoDTO.getId_veiculo() <= 0) { //
        return "Erro: Seleção de veículo inválida para exclusão.";
    }
    try {
        // O DAO agora retorna a mensagem de status diretamente
        return veiculoDAO.excluirVeiculo(veiculoDTO); //
    } catch (Exception e) {
        System.err.println("Erro na VeiculoCTR ao excluirVeiculo: " + e.getMessage());
        return "Erro crítico ao tentar excluir veículo!";
    }
}

    // Dentro da classe VeiculoCTR.java
// import java.util.List; (se não estiver importado)
// import br.com.pcar.dto.OpcionalDTO; (se o PcarDTO usar List<OpcionalDTO>)
    /**
     * Busca os IDs dos opcionais associados a um veículo.
     *
     * @param idVeiculo O ID do veículo.
     * @return Uma lista de IDs dos opcionais.
     */
    public List<Integer> buscarIdsOpcionaisPorVeiculo(int idVeiculo) {
        if (idVeiculo <= 0) {
            return new ArrayList<>(); // Retorna lista vazia se ID for inválido
        }
        return veiculoDAO.buscarIdsOpcionaisPorVeiculo(idVeiculo);
    }

    /**
     * Atualiza os opcionais de um veículo.
     *
     * @param idVeiculo O ID do veículo.
     * @param idsOpcionaisSelecionados Lista dos IDs dos opcionais a serem
     * associados.
     * @return Mensagem de status.
     */
    public String atualizarOpcionaisDoVeiculo(int idVeiculo, List<Integer> idsOpcionaisSelecionados) {
        if (idVeiculo <= 0) {
            return "Erro: ID do veículo inválido para atualizar opcionais.";
        }
        // A lista de idsOpcionaisSelecionados pode ser nula ou vazia se nenhum opcional for selecionado.
        // O DAO já lida com isso (deleta todos e não insere nenhum novo se a lista for vazia).

        if (veiculoDAO.atualizarOpcionaisDoVeiculo(idVeiculo, idsOpcionaisSelecionados)) {
            return "Opcionais do veículo atualizados com sucesso!";
        } else {
            return "Erro ao atualizar os opcionais do veículo.";
        }
    }

    public void CloseDB() {
        ConexaoDAO.CloseDB();
    }
}


/*package br.com.pcar.ctr;

import java.sql.ResultSet;
import br.com.pcar.dto.PcarDTO;
import br.com.pcar.dao.VeiculoDAO;
import br.com.pcar.dao.ConexaoDAO;

public class VeiculoCTR {
    VeiculoDAO veiculoDAO = new VeiculoDAO();

    public VeiculoCTR() { }

    // MÉTODO PARA INSERIR VEÍCULO
    public String inserirVeiculo(PcarDTO veiculoDTO) {
        try {
            if (veiculoDAO.inserirVeiculo(veiculoDTO)) {
                return "Veículo cadastrado com sucesso!";
            } else {
                return "Erro ao cadastrar veículo!";
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "Erro ao cadastrar veículo!";
        }
    }

    // MÉTODO PARA CONSULTAR VEÍCULO
    public ResultSet consultarVeiculo(PcarDTO veiculoDTO, int opcao) {
        ResultSet rs = null;
        rs = veiculoDAO.consultarVeiculo(veiculoDTO, opcao);
        return rs;
    }

    // MÉTODO PARA ALTERAR VEÍCULO
    public String alterarVeiculo(PcarDTO veiculoDTO) {
        try {
            if (veiculoDAO.alterarVeiculo(veiculoDTO)) {
                return "Veículo alterado com sucesso!";
            } else {
                return "Erro ao alterar veículo!";
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "Erro ao alterar veículo!";
        }
    }

    // MÉTODO PARA EXCLUIR VEÍCULO
    public String excluirVeiculo(PcarDTO veiculoDTO) {
        try {
            if (veiculoDAO.excluirVeiculo(veiculoDTO)) {
                return "Veículo excluído com sucesso!";
            } else {
                return "Erro ao excluir veículo!";
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "Erro ao excluir veículo!";
        }
    }

    public void CloseDB() {
        ConexaoDAO.CloseDB();
    }
}*/
