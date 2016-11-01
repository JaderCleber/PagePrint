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

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

public class JasperUtil {
    public static void exportPdfOnBrowser(Collection lista, Map parameters, InputStream localJasper, String nomeRelatorio) {
        // Bytes que serao exportados
        // Recupera JasperPrint
        try {
//            ServletOutputStream servletOutputStream = response.getOutputStream();
            JasperPrint jasperPrint = JasperFillManager.fillReport(localJasper, parameters, new JRBeanCollectionDataSource(lista));
            jasperPrint.setName(nomeRelatorio);
            JasperViewer viewer = new JasperViewer(jasperPrint, false);
            viewer.setVisible(true);
            viewer.toFront();
            // create a PDF file
//            byte bytes[] = JasperExportManager.exportReportToPdf(jasperPrint);
//            response.setContentType("application/pdf");
//            response.setContentLength(bytes.length);
//            ServletOutputStream ouputStream = response.getOutputStream();
//            ouputStream.write(bytes, 0, bytes.length);
//            ouputStream.flush();
//            ouputStream.close();
        }  catch (Exception e) {
            e.printStackTrace();
        }
    }
}