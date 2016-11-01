/**
 * Copyright 2016 Jader Cleber
 *
 * This file is part of PagePrint.
 *
 * PagePrint is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PagePrint is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */
package sample;

import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import sample.controller.Base64;
import sample.model.Imagem;
import sample.model.ImagemJson;
import sample.persistence.Conexao;

import java.io.*;
import java.nio.channels.FileChannel;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

public class Controller {
    @FXML
    private TableView tbImagens;
    @FXML
    private TextArea taDescricao, taDetalhes;
    @FXML
    private TableColumn tcId, tcImagem, tcDescricao;
    @FXML
    private TextField inNomeCatalogo, inPesquisaNome;
    @FXML
    private DatePicker inDataInicial, inDataFinal;

    private ArrayList<Imagem> tabelaImagens = new ArrayList<>();
    private Collection<Imagem> imagemList = new ArrayList<Imagem>();
    private int id = 0;
    private String clicado = "";
    private FileChooser fileChooser = new FileChooser();
    private File ultimoArquivo = null;
    private int idCatalogo = 0;

    @FXML
    void initialize() {
        tbImagens.setOnMouseClicked(event -> {
            if (tabelaImagens.size() > 0) {
                clicado = String.valueOf(tbImagens.getSelectionModel().getSelectedIndex());
                taDescricao.setText(tabelaImagens.get(Integer.parseInt(clicado)).getDescricao());
                taDescricao.requestFocus();
            }
        });
        taDescricao.setWrapText(true);
    }

    public void actRemover(ActionEvent actionEvent) {
        if (!clicado.equals("")) {
            atualizarBanco(tabelaImagens.get(Integer.parseInt(clicado)), "deletar");
            tabelaImagens.remove(Integer.parseInt(clicado));
            taDescricao.setText("");
        }
        carregarTabela();
    }

    public void actSalvarDescricao(ActionEvent actionEvent) {
        if (!clicado.equals("")) {
            tabelaImagens.get(Integer.parseInt(clicado)).setDescricao(taDescricao.getText().toString());
            atualizarBanco(tabelaImagens.get(Integer.parseInt(clicado)), "atualizar");
            taDescricao.setText("");
        }
        carregarTabela();
    }

    public void actImprimir(ActionEvent actionEvent) {
        try {
            OutputStream bytes = new FileOutputStream("json.json");
            OutputStreamWriter chars = new OutputStreamWriter(bytes, "UTF-8");
            BufferedWriter strings = new BufferedWriter(chars);
            String jsonString = toJSON();
            strings.write(jsonString);
            strings.close();
            bytes.close();
            JsonDataSource ds = new JsonDataSource(new File("json.json"));

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new File("Printer.jasper"));
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, ds);
            JasperViewer viewer = new JasperViewer(jasperPrint, false);
            viewer.setVisible(true);
            viewer.toFront();
//            tbImagens.getItems().clear();
//            tabelaImagens = new ArrayList<>();
        } catch (JRException e) {
            e.printStackTrace();
            System.out.println(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void actAdicionar(ActionEvent actionEvent) {
        fileChooser.setTitle("Selecione a imagem da assinatura");
        if (ultimoArquivo != null)
            fileChooser.setInitialDirectory(ultimoArquivo);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPG", "*.jpg"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPEG", "*.jpeg"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));
        List<File> files = fileChooser.showOpenMultipleDialog(null);
        if (files.size() > 0) {
            try {
                for (File fileImg : files) {
                    ultimoArquivo = new File(fileImg.getParent());
                    FileInputStream stream = new FileInputStream(fileImg.getAbsolutePath());
                    String caminho = fileImg.getAbsolutePath();
                    FileOutputStream output = new FileOutputStream("imagens/" + id + ".jpg");
                    FileChannel o = stream.getChannel();
                    FileChannel d = output.getChannel();
                    o.transferTo(0, o.size(), d);
                    o.close();
                    d.close();
//            fileImg = new File("imagens/"+id+".jpg");
//            BufferedImage imageLoaded = null;
//            try {
//                imageLoaded = ImageIO.read(fileImg);
//            } catch (IOException e) {
//
//            }
//            BufferedImage imageServer = new BufferedImage(300, 160, BufferedImage.TYPE_INT_RGB);
//            Graphics2D g = imageServer.createGraphics();
//            g.drawImage(imageLoaded, 0, 0, 300, 160, null);
//            ImageIO.write(imageServer, "JPG", fileImg);

                    Imagem imagem = new Imagem();
                    imagem.setImagem(fileImg);
                    imagem.setId(String.valueOf(id++));
                    imagem.setCaminho(caminho);
                    imagem.setDescricao("");
                    imagemList.add(imagem);
                    tabelaImagens.add(imagem);
                    atualizarBanco(imagem, "inserir");
                }
                carregarTabela();
                tbImagens.getSelectionModel().selectLast();
                clicado = String.valueOf(tbImagens.getSelectionModel().getSelectedIndex());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void carregarTabela() {
        tbImagens.getItems().clear();
        tcId.setCellValueFactory(new PropertyValueFactory<Imagem, String>("id"));
        tcImagem.setCellValueFactory(new PropertyValueFactory<Imagem, String>("caminho"));
        tcDescricao.setCellValueFactory(new PropertyValueFactory<Imagem, String>("descricao"));
        tbImagens.setItems(FXCollections.observableArrayList(tabelaImagens));
    }

    private void atualizarBanco(Imagem imagem, String acao) {
        try {
            Conexao con = new Conexao();
            if (con.getCon() == null)
                throw new EmptyStackException();

//            boolean limpeza = con.executar("DELETE FROM ITEM WHERE ICATALOGO = " + idCatalogo);
//            if (!limpeza)
//                throw new EmptyStackException();
            String sql = "";
            switch (acao){
                case "inserir":
                    FileInputStream imageInFile = new FileInputStream(imagem.getImagem());
                    byte[] imageData = new byte[(int) imagem.getImagem().length()];
                    imageInFile.read(imageData);
                    sql = "INSERT INTO ITEM (TIMAGEM, TCAMINHO, TDESCRICAO,IORDEM, ICATALOGO) " +
                            " VALUES(\"";
                    sql += Base64.encodeBytes(imageData);
                    sql += "\",\"";
                    sql += imagem.getCaminho();
                    sql += "\",\"";
                    sql += imagem.getDescricao().replace("\"", "'");
                    sql += "\",";
                    sql += imagem.getId();
                    sql += ",";
                    if(idCatalogo == 0)
                        criarCatalogo();
                    sql += idCatalogo;
                    sql += ");";
                    con.executar(sql);
                    break;
                case "deletar":
                    sql = "DELETE FROM ITEM WHERE IORDEM = "+ imagem.getId();
                    sql += " AND ICATALOGO = " + idCatalogo;
                    con.executar(sql);
                    break;
                default:
                    sql = "UPDATE ITEM SET TDESCRICAO = \"" + imagem.getDescricao() + "\"";
                    sql += " WHERE IORDEM = " + imagem.getId();
                    sql += " AND ICATALOGO = " + idCatalogo;
                    con.executar(sql);
                    break;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void criarCatalogo() {
        try {
            Conexao con = new Conexao();
            if (con.getCon() == null)
                throw new EmptyStackException();

//            boolean limpeza = con.executar("DELETE FROM ITEM WHERE ICATALOGO = " + idCatalogo);
//            if (!limpeza)
//                throw new EmptyStackException();
            SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
            String sql = "";
            String dataCadastro = format.format(new Date());
            sql = "INSERT INTO CATALOGO (ID, TNOME, TCADASTRO, TDETALHES) VALUES (NULL, '"
                    + (inNomeCatalogo.getText().toString().equals("")?"":inNomeCatalogo.getText())+"', '"
                    + dataCadastro + "', '')";
            idCatalogo = con.novoCatalogo(sql, dataCadastro);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String toJSON() {
        ArrayList<ImagemJson> arrayJsons = new ArrayList<>();
        try {
            Conexao con = new Conexao();
            Statement stmt = con.getCon().createStatement();
            ResultSet catalogo = con.consultar(" SELECT TIMAGEM FROM ITEM WHERE ICATALOGO = " + idCatalogo, stmt);
            if(catalogo == null)
                throw new EmptyStackException();
            ArrayList<String> imagens64 = new ArrayList<String>();
            catalogo.first();
            while ( catalogo.next() ) {
                imagens64.add(catalogo.getString("TIMAGEM"));
            }
            catalogo.close();
            for (int i = 0; i < tabelaImagens.size(); i += 2) {
                Imagem d = tabelaImagens.get(i);
//                FileInputStream imageInFile = new FileInputStream(d.getImagem());
//                byte[] imageData = new byte[(int) d.getImagem().length()];
//                imageInFile.read(imageData);
                ImagemJson imagemJson = new ImagemJson();
//                imagemJson.setImagem(Base64.encodeBytes(imageData));
                imagemJson.setImagem(imagens64.get(i));
                imagemJson.setDescricao(d.getDescricao());
                arrayJsons.add(imagemJson);
                if (tabelaImagens.get(i + 1) != null) {
                    d = tabelaImagens.get(i + 1);
//                    imageInFile = new FileInputStream(d.getImagem());
//                    imageData = new byte[(int) d.getImagem().length()];
//                    imageInFile.read(imageData);
//                    imagemJson.setImagem1(Base64.encodeBytes(imageData));
                    imagemJson.setImagem(imagens64.get(i));
                    imagemJson.setDescricao1(d.getDescricao());
                    arrayJsons.set(arrayJsons.size() - 1, imagemJson);
                }
            }
        } catch (Exception e) {

        }
        Gson gson = new Gson();
        return gson.toJson(arrayJsons);
    }

    public void actSalvar(ActionEvent actionEvent) {
        if(tabelaImagens.size() > 0) {
            Conexao con = new Conexao();
            String sql = "UPDATE INTO CATALOGO (TNOME, TDETALHES) VALUES (\"";
            sql += inNomeCatalogo.getText().toString() + "\",\"";
            sql += taDetalhes.getText().toString();
            sql += "\")";
            con.executar(sql);
        }
    }

    public void actEditar(ActionEvent actionEvent) {

    }

    public void actReimprimir(ActionEvent actionEvent) {

    }

    public void actExcluir(ActionEvent actionEvent) {

    }
}
