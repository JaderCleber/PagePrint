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

package sample.controller;

import sample.model.Imagem;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Created by JaderCleber on 22/10/2016.
 */
public class ExportacaoJasper {
    public void exportarFotos() throws FileNotFoundException {
        FileInputStream layoutRelatorio = new FileInputStream("Blank_A4.jasper");
        /**
         *
         */
//        HashMap<String, InputStream> imagens = new HashMap<>();

        Collection<Imagem> imagemList = new ArrayList<Imagem>();
        Imagem imagem = new Imagem();
//        imagem.setImagem(new FileInputStream("1.jpg"));
        imagemList.add(imagem);
        //
//        imagens.put("imagem", imagem.getImagem());

        imagem = new Imagem();
//        imagem.setImagem(new FileInputStream("2.jpg"));
        imagemList.add(imagem);
        imagem = new Imagem();
        imagemList.add(imagem);
        //
//        imagens.put("imagem", imagem.getImagem());
//        try {
//            OutputStream bytes = new FileOutputStream("json.json");
//            OutputStreamWriter chars = new OutputStreamWriter(bytes, "UTF-8");
//            BufferedWriter strings = new BufferedWriter(chars);
//            Gson gson = new Gson();
//            String jsonString = gson.toJson(imagens);
//            strings.write(jsonString);
//            strings.close();
//            bytes.close();
//            JsonDataSource ds = new JsonDataSource(new File("json.json"));
//
//            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new File("Printer.jasper"));
//            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, ds);
//            JasperViewer viewer = new JasperViewer(jasperPrint, false);
//            viewer.setVisible(true);
//            viewer.toFront();
//        } catch (JRException e) {
//            e.printStackTrace();
//            System.out.println(e);
//        } catch (Exception e){
//            e.printStackTrace();
//        }

        //Nenhum paramentro necessario
        Map parameters = null ;
        // Executa exportacao para pdf
        JasperUtil.exportPdfOnBrowser(imagemList, parameters, layoutRelatorio ,"teste.pdf");
    }
}
