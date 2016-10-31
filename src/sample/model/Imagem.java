package sample.model;

import java.io.File;
import java.io.InputStream;

/**
 * Created by JaderCleber on 22/10/2016.
 */
public class Imagem {
    private File imagem;
    private String id = "";
    private String caminho = "";
    private String descricao = "";

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getId() {
        return id;
    }

    public String getCaminho() {
        return caminho;
    }

    public File getImagem() {
        return imagem;
    }

    public void setImagem(File imagem) {
        this.imagem = imagem;
    }
}
