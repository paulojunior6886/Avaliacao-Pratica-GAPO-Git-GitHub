/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.pcar.dto;

public class OpcionalDTO {
    private int id_opcional;
    private String nome_opcional;
    private String descricao_opcional;
    private double preco_adicional;

    // Construtor padrão
    public OpcionalDTO() {
    }

    // Construtor com campos (opcional, mas útil)
    public OpcionalDTO(int id_opcional, String nome_opcional, String descricao_opcional, double preco_adicional) {
        this.id_opcional = id_opcional;
        this.nome_opcional = nome_opcional;
        this.descricao_opcional = descricao_opcional;
        this.preco_adicional = preco_adicional;
    }

    // Getters e Setters
    public int getId_opcional() {
        return id_opcional;
    }

    public void setId_opcional(int id_opcional) {
        this.id_opcional = id_opcional;
    }

    public String getNome_opcional() {
        return nome_opcional;
    }

    public void setNome_opcional(String nome_opcional) {
        this.nome_opcional = nome_opcional;
    }

    public String getDescricao_opcional() {
        return descricao_opcional;
    }

    public void setDescricao_opcional(String descricao_opcional) {
        this.descricao_opcional = descricao_opcional;
    }

    public double getPreco_adicional() {
        return preco_adicional;
    }

    public void setPreco_adicional(double preco_adicional) {
        this.preco_adicional = preco_adicional;
    }

    // Opcional: Sobrescrever toString() para facilitar a exibição em ComboBoxes/JLists
    @Override
    public String toString() {
        return nome_opcional; // Ou nome_opcional + " (R$ " + preco_adicional + ")"
    }
}
