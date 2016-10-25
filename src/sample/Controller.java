package sample;

import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.apache.lucene.util.IOUtils;
import sample.controller.Base64;
import sample.controller.JasperUtil;
import sample.model.Imagem;
import sample.model.ImagemJson;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.List;

public class Controller {
    @FXML
    private TableView lvImagens;
    @FXML
    private TextArea taDescricao;
    @FXML
    private TableColumn tcId, tcImagem, tcDescricao;

    private ArrayList<Imagem> tabelaImagens = new ArrayList<>();
    private Collection<Imagem> imagemList = new ArrayList<Imagem>();
    private int id = 0;
    private String clicado = "";
    private FileChooser fileChooser = new FileChooser();
    private File ultimoArquivo = null;

    @FXML
    void initialize() {
        lvImagens.setOnMouseClicked(event -> {
            if(tabelaImagens.size()>0) {
                clicado = String.valueOf(lvImagens.getSelectionModel().getSelectedIndex());
                taDescricao.setText(tabelaImagens.get(Integer.parseInt(clicado)).getDescricao());
                taDescricao.requestFocus();
            }
        });
        taDescricao.setWrapText(true);
    }

    public void actRemover(ActionEvent actionEvent) {
        if(!clicado.equals("")){
            tabelaImagens.remove(Integer.parseInt(clicado));
            taDescricao.setText("");
        }
        carregarTabela();
    }

    public void actSalvar(ActionEvent actionEvent) {
        if(!clicado.equals("")){
            tabelaImagens.get(Integer.parseInt(clicado)).setDescricao(taDescricao.getText().toString());
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
//            lvImagens.getItems().clear();
//            tabelaImagens = new ArrayList<>();
        } catch (JRException e) {
            e.printStackTrace();
            System.out.println(e);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void actAdicionar(ActionEvent actionEvent) {
        fileChooser.setTitle("Selecione a imagem da assinatura");
        if(ultimoArquivo != null)
            fileChooser.setInitialDirectory(ultimoArquivo);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPG", "*.jpg"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPEG", "*.jpeg"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));
        List<File> files = fileChooser.showOpenMultipleDialog(null);
        if(files.size()>0) {
            try {
                for (File fileImg : files) {
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
                    ultimoArquivo = new File(fileImg.getParent());
                }
                carregarTabela();
                lvImagens.getSelectionModel().selectLast();
                clicado = String.valueOf(lvImagens.getSelectionModel().getSelectedIndex());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void carregarTabela() {
        lvImagens.getItems().clear();
        tcId.setCellValueFactory(new PropertyValueFactory<Imagem, String>("id"));
        tcImagem.setCellValueFactory(new PropertyValueFactory<Imagem, String>("caminho"));
        tcDescricao.setCellValueFactory(new PropertyValueFactory<Imagem, String>("descricao"));
        lvImagens.setItems(FXCollections.observableArrayList(tabelaImagens));
    }

    public String toJSON() {
        ArrayList<ImagemJson> arrayJsons = new ArrayList<>();
        try {
            for (int i=0; i<tabelaImagens.size(); i+=2) {
                Imagem d = tabelaImagens.get(i);
                FileInputStream imageInFile = new FileInputStream(d.getImagem());
                byte[] imageData = new byte[(int) d.getImagem().length()];
                imageInFile.read(imageData);
                ImagemJson imagemJson = new ImagemJson();
                imagemJson.setImagem(Base64.encodeBytes(imageData));
                imagemJson.setDescricao(d.getDescricao());
                arrayJsons.add(imagemJson);
                if(tabelaImagens.get(i+1) != null) {
                    d = tabelaImagens.get(i+1);
                    imageInFile = new FileInputStream(d.getImagem());
                    imageData = new byte[(int) d.getImagem().length()];
                    imageInFile.read(imageData);
                    imagemJson.setImagem1(Base64.encodeBytes(imageData));
                    imagemJson.setDescricao1(d.getDescricao());
                    arrayJsons.set(arrayJsons.size()-1, imagemJson);
                }
            }
        }catch (Exception e){

        }
        Gson gson = new Gson();
        return gson.toJson(arrayJsons);
    }
}
