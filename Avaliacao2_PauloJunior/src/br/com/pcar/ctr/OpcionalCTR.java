/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.pcar.ctr;

import br.com.pcar.dao.ConexaoDAO;
import br.com.pcar.dao.OpcionalDAO;
import br.com.pcar.dto.OpcionalDTO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList; // Para retornar lista de DTOs se necessário
import java.util.List;    // Para retornar lista de DTOs se necessário


public class OpcionalCTR {
    
    OpcionalDAO opcionalDAO = new OpcionalDAO();

    public OpcionalCTR() {
    }

    public String inserirOpcional(OpcionalDTO opcionalDTO) {
        if (opcionalDTO.getNome_opcional() == null || opcionalDTO.getNome_opcional().trim().isEmpty()) {
            return "Nome do opcional é obrigatório.";
        }
        return opcionalDAO.inserirOpcional(opcionalDTO);
    }

    public String alterarOpcional(OpcionalDTO opcionalDTO) {
        if (opcionalDTO.getId_opcional() <= 0) {
            return "ID do opcional inválido para alteração.";
        }
        if (opcionalDTO.getNome_opcional() == null || opcionalDTO.getNome_opcional().trim().isEmpty()) {
            return "Nome do opcional é obrigatório.";
        }
        return opcionalDAO.alterarOpcional(opcionalDTO);
    }

    public String excluirOpcional(int idOpcional) {
        if (idOpcional <= 0) {
            return "ID do opcional inválido para exclusão.";
        }
        return opcionalDAO.excluirOpcional(idOpcional);
    }

    public ResultSet consultarOpcionais(int opcao, OpcionalDTO dtoFiltro) {
        return opcionalDAO.consultarOpcionais(opcao, dtoFiltro);
    }
    
    // Método para converter ResultSet em Lista de OpcionalDTO (útil para JList/ComboBoxes)
    public List<OpcionalDTO> listarTodosOpcionaisDTO() {
        List<OpcionalDTO> listaOpcionais = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = opcionalDAO.consultarOpcionais(1, null); // Opção 1 para listar todos
            if (rs != null) {
                while (rs.next()) {
                    OpcionalDTO opc = new OpcionalDTO();
                    opc.setId_opcional(rs.getInt("id_opcional"));
                    opc.setNome_opcional(rs.getString("nome_opcional"));
                    opc.setDescricao_opcional(rs.getString("descricao_opcional"));
                    opc.setPreco_adicional(rs.getDouble("preco_adicional"));
                    listaOpcionais.add(opc);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao converter ResultSet de opcionais para Lista: " + e.getMessage());
        } finally {
            if (rs != null) {
                closeDbConnection(); // Fecha a conexão após usar o RS
            }
        }
        return listaOpcionais;
    }

    public void closeDbConnection() {
        ConexaoDAO.CloseDB();
    }
}
