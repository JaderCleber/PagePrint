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
import sample.model.Catalogo;
import sample.model.Imagem;
import sample.model.ImagemJson;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.channels.FileChannel;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class Controller {
    @FXML
    private TableView tbImagens, tbPesquisa;
    @FXML
    private TextArea taDescricao, taDetalhes;
    @FXML
    private TableColumn tcId, tcImagem, tcDescricao, tcIdCatalogo, tcNome, tcDetalhes, tcData;
    @FXML
    private TextField inNomeCatalogo, inPesquisaNome;
    @FXML
    private DatePicker inDataInicial, inDataFinal;
    @FXML
    private Label lbCodigo;
    @FXML
    private TabPane tab;

    private ArrayList<Imagem> tabelaImagens = new ArrayList<>();
    private ArrayList<Catalogo> tabelaBusca = new ArrayList<>();
    private Collection<Imagem> imagemList = new ArrayList<Imagem>();
    private int id = 0;
    private String clicado = "";
    private FileChooser fileChooser = new FileChooser();
    private File ultimoArquivo = null;
    private int idCatalogo = 0;
    private SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
    private Connection con;

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
        inDataInicial.setEditable(false);
        inDataFinal.setEditable(false);
        inDataInicial.setValue(LocalDate.parse(f.format(new Date(System.currentTimeMillis()-(86400000*7)))));
        inDataFinal.setValue(LocalDate.parse(f.format(new Date(System.currentTimeMillis()))));
        openCon();
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
        } catch (JRException e) {
            e.printStackTrace();
            System.out.println(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void actAdicionar(ActionEvent actionEvent) {
        fileChooser.setTitle("Selecione a imagem");
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
            if (con == null) {
                criarDialogo(Alert.AlertType.ERROR, "Atenção", "Comunicado do Sistema", "Não foi possível abrir conecxão com o banco de dados");
                return;
            }

//            boolean limpeza = con.executar("DELETE FROM ITEM WHERE ICATALOGO = " + idCatalogo);
//            if (!limpeza)
//                throw new EmptyStackException();
            String sql = "";
            Statement stmt = null;
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

                    con.setAutoCommit(false);
                    stmt = con.createStatement();
                    stmt.executeUpdate(sql);
                    con.commit();
//                    con.executar(sql);
                    break;
                case "deletar":
                    sql = "DELETE FROM ITEM WHERE ID = "+ imagem.getId();
                    sql += " AND ICATALOGO = " + idCatalogo;

                    con.setAutoCommit(false);
                    stmt = con.createStatement();
                    stmt.executeUpdate(sql);
                    con.commit();
//                    con.executar(sql);
                    break;
                default:
                    sql = "UPDATE ITEM SET TDESCRICAO = \"" + imagem.getDescricao() + "\"";
                    sql += " WHERE IORDEM = " + imagem.getId();
                    sql += " AND ICATALOGO = " + idCatalogo;

                    con.setAutoCommit(false);
                    stmt = con.createStatement();
                    stmt.executeUpdate(sql);
                    con.commit();
//                    con.executar(sql);
                    break;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void criarCatalogo() {
        try {
            if (con == null) {
                criarDialogo(Alert.AlertType.ERROR, "Atenção", "Comunicado do Sistema", "Não foi possível conectar ao banco de dados");
                return;
            }

//            boolean limpeza = con.executar("DELETE FROM ITEM WHERE ICATALOGO = " + idCatalogo);
//            if (!limpeza)
//                throw new EmptyStackException();
            SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
            String sql = "";
            String dataCadastro = format.format(new Date());
            sql = "INSERT INTO CATALOGO (ID, TNOME, TCADASTRO, TDETALHES) VALUES (NULL, '"
                    + (inNomeCatalogo.getText().toString().equals("")?"":inNomeCatalogo.getText())+"', '"
                    + dataCadastro + "', '')";
            idCatalogo = novoCatalogo(sql, dataCadastro);
            lbCodigo.setText("Catálogo Aberto: " + idCatalogo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String toJSON() {
        ArrayList<ImagemJson> arrayJsons = new ArrayList<>();
        try {
            Statement stmt = con.createStatement();
            ResultSet catalogo = stmt.executeQuery(" SELECT TIMAGEM FROM ITEM WHERE ICATALOGO = " + idCatalogo);

            if( catalogo.next() ) {
                ArrayList<String> imagens64 = new ArrayList<String>();
                do {
                    imagens64.add(catalogo.getString("TIMAGEM"));
                }while (catalogo.next());
                catalogo.close();

                for (int i = 0; i < tabelaImagens.size(); i += 2) {
                    Imagem d = tabelaImagens.get(i);
                    ImagemJson imagemJson = new ImagemJson();
                    imagemJson.setImagem(imagens64.get(i));
                    imagemJson.setDescricao(d.getDescricao());
                    arrayJsons.add(imagemJson);
                    if (tabelaImagens.get(i + 1) != null) {
                        d = tabelaImagens.get(i + 1);
                        imagemJson.setImagem1(imagens64.get(i+1));
                        imagemJson.setDescricao1(d.getDescricao());
                        arrayJsons.set(arrayJsons.size() - 1, imagemJson);
                    }
                }
            } else
                criarDialogo(Alert.AlertType.INFORMATION, "Atenção", "Comunicado do Banco", "Nenhum resultado foi encontrado");
        } catch (Exception e) {

        }
        Gson gson = new Gson();
            return gson.toJson(arrayJsons);
    }

    public void actSalvar(ActionEvent actionEvent) {
        if(inNomeCatalogo.getText().toString().equals("")){
            inNomeCatalogo.requestFocus();
            return;
        }
        if(tabelaImagens.size() > 0) {
            String sql = "UPDATE CATALOGO SET TNOME = \"";
            sql += inNomeCatalogo.getText().toString() + "\", TDETALHES = \"";
            sql += taDetalhes.getText().toString();
            sql += "\" WHERE ID = ";
            sql += idCatalogo;

            try {
                con.setAutoCommit(false);
                Statement stmt = con.createStatement();
                stmt.executeUpdate(sql);
                con.commit();
//            con.executar(sql);
                limparPrincipal();
                limparBusca();
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void limparBusca() {
        tbPesquisa.getItems().clear();
        tabelaBusca = new ArrayList<>();
        inPesquisaNome.setText("");
    }

    public void actEditar(ActionEvent actionEvent) {
        limparPrincipal();
        int index = tbPesquisa.getSelectionModel().getSelectedIndex();
        if(index == -1) {
            criarDialogo(Alert.AlertType.WARNING, "Atenção", "Comunicado do Sistema", "Selecione um item na lista para Editar");
            return;
        }
        idCatalogo = Integer.parseInt(tabelaBusca.get(index).getId());
        inNomeCatalogo.setText(tabelaBusca.get(index).getNome());
        taDetalhes.setText(tabelaBusca.get(index).getDetalhes());
        lbCodigo.setText("Em Edição: " + idCatalogo);
        try{
            Statement stmt = con.createStatement();
            String sql = " SELECT ID, TCAMINHO, TDESCRICAO, TIMAGEM FROM ITEM WHERE ICATALOGO = " + idCatalogo;

            ResultSet itens = stmt.executeQuery(sql);
            tabelaImagens = new ArrayList<>();
            if (itens.next()) {
                do {
                    Imagem i = new Imagem();
                    i.setId(itens.getString("ID"));
                    i.setCaminho(itens.getString("TCAMINHO"));
                    i.setDescricao(itens.getString("TDESCRICAO"));
//                    File aux = new File("aux");

//                    FileInputStream arquivo = new FileInputStream(aux);
//                    byte[] imageData = Base64.decode(itens.getString("TIMAGEM"));
//                    arquivo.read(imageData);
//                    arquivo.close();
//                    FileOutputStream fos = new FileOutputStream(aux);
                    BASE64Decoder decoder = new BASE64Decoder();
                    byte[] arquivo = decoder.decodeBuffer(itens.getString("TIMAGEM"));
                    BufferedImage bufImg = ImageIO.read(new ByteArrayInputStream(arquivo));
                    File aux = new File("newLabel.jpg");
                    ImageIO.write(bufImg, "jpg", aux);
//                    fos.write(arquivo);
//                    fos.close();
                    i.setImagem(aux);
                    aux.delete();
                    tabelaImagens.add(i);
                } while (itens.next());
                tab.getSelectionModel().select(0);
                carregarTabela();
            } else
                criarDialogo(Alert.AlertType.INFORMATION,"Atenção", "Comunicado do Banco", "Nenhum item foi encontrado para o Catálogo");
        }catch (Exception e){

        }
    }

    public void actReimprimir(ActionEvent actionEvent) {
        limparPrincipal();
        int index = tbPesquisa.getSelectionModel().getSelectedIndex();
        if(index == -1) {
            criarDialogo(Alert.AlertType.WARNING, "Atenção", "Comunicado do Sistema", "Selecione um item na lista para Editar");
            return;
        }
        idCatalogo = Integer.parseInt(tabelaBusca.get(index).getId());
        try{
            Statement stmt = con.createStatement();
            String sql = " SELECT ID, TCAMINHO, TDESCRICAO FROM ITEM WHERE ICATALOGO = " + idCatalogo;

            ResultSet itens = stmt.executeQuery(sql);
            tabelaImagens = new ArrayList<>();
            if (itens.next()) {
                do {
                    Imagem i = new Imagem();
                    i.setId(itens.getString("ID"));
                    i.setCaminho(itens.getString("TCAMINHO"));
                    i.setDescricao(itens.getString("TDESCRICAO"));
                    tabelaImagens.add(i);
                } while (itens.next());
                actImprimir(null);
//                carregarTabela();
            } else
                criarDialogo(Alert.AlertType.INFORMATION,"Atenção", "Comunicado do Banco", "Nenhum item foi encontrado para o Catálogo");
        }catch (Exception e){

        }

    }

    public void actExcluir(ActionEvent actionEvent) {
        limparPrincipal();
        int index = tbPesquisa.getSelectionModel().getSelectedIndex();
        String selecionado = tabelaBusca.get(index).getId();
        try{
            if(selecionado.equals("")) {
                criarDialogo(Alert.AlertType.WARNING, "Atenção", "Comunicado do Sistema", "Selecione uma linha da lista para efetuar a operação");
                return;
            }
            String sql = " DELETE FROM ITEM WHERE ICATALOGO = " + selecionado + ";";
            sql += " DELETE FROM CATALOGO WHERE ID = " + selecionado + ";";

            try {
                con.setAutoCommit(false);
                Statement stmt = con.createStatement();
                stmt.executeUpdate(sql);
                con.commit();
//            con.executar(sql);
                limparPrincipal();
                limparBusca();
            }catch (SQLException e) {
                e.printStackTrace();
            }
//            if(!con.executar(sql)) {
//                criarDialogo(Alert.AlertType.ERROR, "Atenção", "Comunicado do Sistema", "Não foi possível excluir o Catálogo");
//                return;
//            }
            tabelaBusca.remove(index);
            carregarPesquisa();
        }catch (Exception e){

        }
    }

    public void actBuscar(ActionEvent actionEvent) {
        limparPrincipal();
        try {
            Statement stmt = con.createStatement();
            String sql = " SELECT * FROM CATALOGO WHERE TNOME LIKE '%"
                    + inPesquisaNome.getText().toString() + "%' and strftime('%Y-%m-%d', TCADASTRO) " +
                    " BETWEEN strftime('%Y-%m-%d', '" + inDataInicial.getValue()+ "') "
                    + " AND strftime('%Y-%m-%d', '"
                    + inDataFinal.getValue() + "')";
            ResultSet catalogos = stmt.executeQuery(sql);
            tabelaBusca = new ArrayList<>();
            tbPesquisa.getItems().clear();
            if(catalogos.next()) {
                do {
                    Catalogo c = new Catalogo();
                    String[] cadastro = catalogos.getString("TCADASTRO").split(" ");
                    String[] data = cadastro[0].split("-");
                    c.setCadastro(data[2]+ "/" + data[1]+ "/" + data[0] + " " + cadastro[1]);
                    c.setDetalhes(catalogos.getString("TDETALHES"));
                    c.setNome(catalogos.getString("TNOME"));
                    c.setId(catalogos.getString("ID"));
                    tabelaBusca.add(c);
                }while(catalogos.next());
                carregarPesquisa();
                stmt.close();
            } else
                criarDialogo(Alert.AlertType.INFORMATION, "Atenção", "Comunicado do Banco", "Nenhum resultado foi encontrado");
        }catch (Exception e){

        }
    }

    private void carregarPesquisa() {
        tbPesquisa.getItems().clear();
        tcIdCatalogo.setCellValueFactory(new PropertyValueFactory<Catalogo, String>("id"));
        tcNome.setCellValueFactory(new PropertyValueFactory<Catalogo, String>("nome"));
        tcData.setCellValueFactory(new PropertyValueFactory<Catalogo, String>("cadastro"));
        tcDetalhes.setCellValueFactory(new PropertyValueFactory<Catalogo, String>("detalhes"));
        tbPesquisa.setItems(FXCollections.observableArrayList(tabelaBusca));
    }

    private void criarDialogo(Alert.AlertType tipo, String titulo, String cabecalho, String mensagem){
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(cabecalho);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void limparPrincipal(){
        tbImagens.getItems().clear();
        tabelaImagens = new ArrayList<>();
        lbCodigo.setText("");
        idCatalogo = 0;
        inNomeCatalogo.setText("");
        taDetalhes.setText("");
    }

    private void openCon(){
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:pageprint.db");
        } catch (Exception e) {
            criarDialogo(Alert.AlertType.ERROR, "Atenção", "Comunicado do Sistema", "Nâo foi possível conectar ao banco de dados");
            //return;
        }
    }

    public int novoCatalogo(String sql, String dataCadastro) {
        Statement stmt = null;
        try {
            con.setAutoCommit(false);
            stmt = con.createStatement();
            stmt.executeUpdate(sql);
            con.commit();

            ResultSet idGerado = stmt.executeQuery(" SELECT ID FROM CATALOGO WHERE TCADASTRO = '" +dataCadastro + "'");
            int resultado = 0;
            while(idGerado.next()){
                resultado = idGerado.getInt("ID");
            }

            stmt.close();
            return resultado;
        } catch (Exception e) {
            return 0;
        }
    }
}
