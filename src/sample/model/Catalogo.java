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

package sample.model;

import java.io.File;

public class Catalogo {
    private String id = "";
    private String nome = "";
    private String cadastro = "";
    private String detalhes = "";

    public String getDetalhes() {
        return detalhes;
    }

    public void setDetalhes(String detalhes) {
        this.detalhes = detalhes;
    }

    public void setCadastro(String cadastro) {
        this.cadastro = cadastro;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCadastro() {
        return cadastro;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }
}
