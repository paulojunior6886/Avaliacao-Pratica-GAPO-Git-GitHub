package br.com.pcar.ctr; // Pacote correto


import java.sql.Connection;
import br.com.pcar.dao.ConexaoDAO; // DAO do seu projeto PCar
import java.io.InputStream;
import java.util.HashMap; // Usar HashMap para parâmetros se não tiver específicos
import java.util.Map;
import javax.swing.JFrame;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.swing.JRViewer;
// Adicionar import para JasperExportManager se for exportar para PDF diretamente
import net.sf.jasperreports.engine.JasperExportManager; 
import java.io.File; // Para salvar PDF
import javax.swing.JOptionPane;

public class RelatorioCTR {

    public RelatorioCTR() {
    }

    public JFrame abrirRelatorioVendas(String nomeArquivoRelatorio, String tituloJanela, Map<String, Object> parametros) {
        try {
            // Ajuste o caminho para a pasta 'rels' dentro do seu pacote 'br.com.pcar'
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("br/com/pcar/rels/" + nomeArquivoRelatorio);

            if (inputStream == null) {
                JOptionPane.showMessageDialog(null, "Arquivo de relatório não encontrado: " + nomeArquivoRelatorio, "Erro Relatório", JOptionPane.ERROR_MESSAGE);
                System.err.println("Caminho procurado: br/com/pcar/rels/" + nomeArquivoRelatorio);
                return null;
            }

            // Usa o método de conexão da sua ConexaoDAO
            Connection conexao = ConexaoDAO.ConnectDBRels(); // Ou ConnectDBRels() se você tiver um específico

            if (conexao == null) {
                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao banco de dados para gerar o relatório.", "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            
            // Se 'parametros' for null, inicializa para evitar NullPointerException
            if (parametros == null) {
                parametros = new HashMap<>();
            }

            JasperPrint print = JasperFillManager.fillReport(inputStream, parametros, conexao);
            
            // Fechando a conexão do banco de dados que foi aberta para gerar o relatório
            ConexaoDAO.CloseDB(); // Garante que a conexão é fechada

            JRViewer viewer = new JRViewer(print);
            JFrame frameRelatorio = new JFrame(tituloJanela);
            frameRelatorio.add(viewer);
            frameRelatorio.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frameRelatorio.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Fecha apenas a janela do relatório

            return frameRelatorio;
        } catch (Exception e) {
            System.err.println("Erro ao abrir relatório: " + e.getMessage());
            e.printStackTrace(); // Importante para ver o erro completo no console
            JOptionPane.showMessageDialog(null, "Erro ao gerar o relatório:\n" + e.getMessage(), "Erro Relatório", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    // Método para gerar e salvar o relatório como PDF diretamente
    public boolean gerarPdfRelatorioVendas(String nomeArquivoRelatorio, String caminhoDestinoPdf, Map<String, Object> parametros) {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("br/com/pcar/rels/" + nomeArquivoRelatorio);
            if (inputStream == null) {
                System.err.println("Arquivo de relatório não encontrado: br/com/pcar/rels/" + nomeArquivoRelatorio);
                return false;
            }

            Connection conexao = ConexaoDAO.ConnectDBRels();
            if (conexao == null) return false;

            if (parametros == null) {
                parametros = new HashMap<>();
            }

            JasperPrint print = JasperFillManager.fillReport(inputStream, parametros, conexao);
            JasperExportManager.exportReportToPdfFile(print, caminhoDestinoPdf);
            
            ConexaoDAO.CloseDB();
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao gerar PDF do relatório: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}