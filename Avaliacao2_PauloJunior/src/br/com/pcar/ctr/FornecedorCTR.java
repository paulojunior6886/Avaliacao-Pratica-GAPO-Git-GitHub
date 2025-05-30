/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.pcar.ctr;

import br.com.pcar.dao.ConexaoDAO;
import br.com.pcar.dao.FornecedorDAO;
import br.com.pcar.dto.FornecedorDTO;
import java.sql.ResultSet;

public class FornecedorCTR {

    FornecedorDAO fornecedorDAO = new FornecedorDAO();

    public FornecedorCTR() {
    }

    public String inserirFornecedor(FornecedorDTO fornecedorDTO) {
        // Validações de regra de negócio podem ser adicionadas aqui
        if (fornecedorDTO.getNome_fornecedor() == null || fornecedorDTO.getNome_fornecedor().trim().isEmpty() ||
            fornecedorDTO.getCnpj_fornecedor() == null || fornecedorDTO.getCnpj_fornecedor().trim().isEmpty()) {
            return "Nome e CNPJ do fornecedor são obrigatórios.";
        }
        // Adicionar mais validações (formato CNPJ, etc.) se necessário

        return fornecedorDAO.inserirFornecedor(fornecedorDTO);
    }

    public String alterarFornecedor(FornecedorDTO fornecedorDTO) {
        if (fornecedorDTO.getId_fornecedor() <= 0) {
            return "ID do fornecedor inválido para alteração.";
        }
        if (fornecedorDTO.getNome_fornecedor() == null || fornecedorDTO.getNome_fornecedor().trim().isEmpty() ||
            fornecedorDTO.getCnpj_fornecedor() == null || fornecedorDTO.getCnpj_fornecedor().trim().isEmpty()) {
            return "Nome e CNPJ do fornecedor são obrigatórios.";
        }
        // Adicionar mais validações se necessário

        return fornecedorDAO.alterarFornecedor(fornecedorDTO);
    }

    public String excluirFornecedor(FornecedorDTO fornecedorDTO) {
         if (fornecedorDTO.getId_fornecedor() <= 0) {
            return "ID do fornecedor inválido para exclusão.";
        }
        return fornecedorDAO.excluirFornecedor(fornecedorDTO);
    }

    public ResultSet consultarFornecedor(FornecedorDTO fornecedorDTO, int opcao) {
        // A VIEW será responsável por fechar a conexão após usar o ResultSet
        // chamando o método CloseDB() desta CTR.
        return fornecedorDAO.consultarFornecedor(fornecedorDTO, opcao);
    }

    /**
    * Método para fechar a conexão com o banco de dados.
    * Deve ser chamado pela VIEW após operações de consulta que retornam ResultSet,
    * depois que o ResultSet não for mais necessário.
    */
    public void closeDbConnection() {
        ConexaoDAO.CloseDB();
    }
}
