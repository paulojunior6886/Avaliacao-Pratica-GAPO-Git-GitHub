package br.com.pcar.dto;

public class PcarDTO {

    private int id, id_cliente, id_venda, id_veiculo;
    private String login_usuario, senha_usuario, tipo_usuario;
    private String nome_cliente, cpf_cliente, endereco_cliente, telefone_cliente, email_cliente;
    private String modelo_veiculo, cor_veiculo, placa_veiculo, tipo_veiculo, marca_veiculo;
    private int ano_fabricacao_veiculo, ano_modelo_veiculo;
    private double preco_veiculo;
    private String data_venda, status_venda;
    private double valor_venda;
    private String caminho_imagem;
    // Dentro da classe PcarDTO.java
    private java.util.List<OpcionalDTO> opcionaisDoVeiculo;
    // Adicionar getter e setter para opcionaisDoVeiculo

    public java.util.List<OpcionalDTO> getOpcionaisDoVeiculo() {
        return opcionaisDoVeiculo;
    }

    public void setOpcionaisDoVeiculo(java.util.List<OpcionalDTO> opcionaisDoVeiculo) {
        this.opcionaisDoVeiculo = opcionaisDoVeiculo;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(int id_cliente) {
        this.id_cliente = id_cliente;
    }

    public int getId_venda() {
        return id_venda;
    }

    public void setId_venda(int id_venda) {
        this.id_venda = id_venda;
    }

    public int getId_veiculo() {
        return id_veiculo;
    }

    public void setId_veiculo(int id_veiculo) {
        this.id_veiculo = id_veiculo;
    }

    public String getLogin_usuario() {
        return login_usuario;
    }

    public void setLogin_usuario(String login_usuario) {
        this.login_usuario = login_usuario;
    }

    public String getSenha_usuario() {
        return senha_usuario;
    }

    public void setSenha_usuario(String senha_usuario) {
        this.senha_usuario = senha_usuario;
    }

    public String getTipo_usuario() {
        return tipo_usuario;
    }

    public void setTipo_usuario(String tipo_usuario) {
        this.tipo_usuario = tipo_usuario;
    }

    public String getNome_cliente() {
        return nome_cliente;
    }

    public void setNome_cliente(String nome_cliente) {
        this.nome_cliente = nome_cliente;
    }

    public String getCpf_cliente() {
        return cpf_cliente;
    }

    public void setCpf_cliente(String cpf_cliente) {
        this.cpf_cliente = cpf_cliente;
    }

    public String getEndereco_cliente() {
        return endereco_cliente;
    }

    public void setEndereco_cliente(String endereco_cliente) {
        this.endereco_cliente = endereco_cliente;
    }

    public String getTelefone_cliente() {
        return telefone_cliente;
    }

    public void setTelefone_cliente(String telefone_cliente) {
        this.telefone_cliente = telefone_cliente;
    }

    public String getEmail_cliente() {
        return email_cliente;
    }

    public void setEmail_cliente(String email_cliente) {
        this.email_cliente = email_cliente;
    }

    public String getModelo_veiculo() {
        return modelo_veiculo;
    }

    public void setModelo_veiculo(String modelo_veiculo) {
        this.modelo_veiculo = modelo_veiculo;
    }

    public String getCor_veiculo() {
        return cor_veiculo;
    }

    public void setCor_veiculo(String cor_veiculo) {
        this.cor_veiculo = cor_veiculo;
    }

    public String getPlaca_veiculo() {
        return placa_veiculo;
    }

    public void setPlaca_veiculo(String placa_veiculo) {
        this.placa_veiculo = placa_veiculo;
    }

    public String getTipo_veiculo() {
        return tipo_veiculo;
    }

    public void setTipo_veiculo(String tipo_veiculo) {
        this.tipo_veiculo = tipo_veiculo;
    }

    public String getMarca_veiculo() {
        return marca_veiculo;
    }

    public void setMarca_veiculo(String marca_veiculo) {
        this.marca_veiculo = marca_veiculo;
    }

    public int getAno_fabricacao_veiculo() {
        return ano_fabricacao_veiculo;
    }

    public void setAno_fabricacao_veiculo(int ano_fabricacao_veiculo) {
        this.ano_fabricacao_veiculo = ano_fabricacao_veiculo;
    }

    public int getAno_modelo_veiculo() {
        return ano_modelo_veiculo;
    }

    public void setAno_modelo_veiculo(int ano_modelo_veiculo) {
        this.ano_modelo_veiculo = ano_modelo_veiculo;
    }

    public double getPreco_veiculo() {
        return preco_veiculo;
    }

    public void setPreco_veiculo(double preco_veiculo) {
        this.preco_veiculo = preco_veiculo;
    }

    public String getData_venda() {
        return data_venda;
    }

    public void setData_venda(String data_venda) {
        this.data_venda = data_venda;
    }

    public String getStatus_venda() {
        return status_venda;
    }

    public void setStatus_venda(String status_venda) {
        this.status_venda = status_venda;
    }

    public double getValor_venda() {
        return valor_venda;
    }

    public void setValor_venda(double valor_venda) {
        this.valor_venda = valor_venda;
    }

    public String getCaminho_imagem() {
        return caminho_imagem;
    }

    public void setCaminho_imagem(String caminho_imagem) {
        this.caminho_imagem = caminho_imagem;
    }

}
