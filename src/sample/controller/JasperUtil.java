package sample.controller;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

/**
 * Created by JaderCleber on 22/10/2016.
 */
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