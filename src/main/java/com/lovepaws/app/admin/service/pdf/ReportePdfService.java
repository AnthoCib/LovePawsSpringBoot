package com.lovepaws.app.admin.service.pdf;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lovepaws.app.admin.dto.MascotaPorEstadoDTO;
import com.lovepaws.app.admin.dto.UsuarioPorRolDTO;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

@Service
public class ReportePdfService {

    public byte[] generarReporte(
            Long totalUsuarios,
            Long totalMascotas,
            List<MascotaPorEstadoDTO> mascotas,
            List<UsuarioPorRolDTO> usuarios
    ) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 40, 40, 40, 40);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            /* ===============================
               FUENTES
               =============================== */
            Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD, new Color(33, 37, 41));
            Font subtitleFont = new Font(Font.HELVETICA, 14, Font.BOLD);
            Font textFont = new Font(Font.HELVETICA, 11);
            Font boldFont = new Font(Font.HELVETICA, 11, Font.BOLD);

            /* ===============================
               HEADER (LOGO + TÍTULO)
               =============================== */
            PdfPTable header = new PdfPTable(2);
            header.setWidthPercentage(100);
            header.setWidths(new float[]{1, 4});
            header.setSpacingAfter(20);

            try {
                Image logo = Image.getInstance("src/main/resources/static/img/logo.png");
                logo.scaleToFit(60, 60);
                PdfPCell logoCell = new PdfPCell(logo); 
                logoCell.setBorder(Rectangle.NO_BORDER);
                header.addCell(logoCell);
            } catch (Exception e) {
                header.addCell("");
            }

            PdfPCell titleCell = new PdfPCell();
            titleCell.setBorder(Rectangle.NO_BORDER);
            titleCell.addElement(new Paragraph("Reporte del Sistema LovePaws", titleFont));
            titleCell.addElement(new Paragraph("Resumen general del sistema", textFont));
            header.addCell(titleCell);

            document.add(header);

            /* ===============================
               MÉTRICAS (CARDS)
               =============================== */
            PdfPTable metrics = new PdfPTable(2);
            metrics.setWidthPercentage(100);
            metrics.setSpacingAfter(25);
            metrics.setWidths(new float[]{1, 1});

            metrics.addCell(metricCell("Usuarios", totalUsuarios.toString()));
            metrics.addCell(metricCell("Mascotas", totalMascotas.toString()));

            document.add(metrics);

            /* ===============================
               TABLA MASCOTAS POR ESTADO
               =============================== */
            document.add(new Paragraph("Mascotas por estado", subtitleFont));
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            table.setWidths(new float[]{3, 1});

            table.addCell(headerCell("Estado"));
            table.addCell(headerCell("Total"));

            boolean alternate = false;
            for (MascotaPorEstadoDTO m : mascotas) {
                table.addCell(bodyCell(m.getEstado(), alternate));
                table.addCell(bodyCell(String.valueOf(m.getTotal()), alternate));
                alternate = !alternate;
            }

            document.add(table);

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF", e);
        }
    }

    /* ===============================
       HELPERS
       =============================== */
    private PdfPCell metricCell(String title, String value) {
        Font titleFont = new Font(Font.HELVETICA, 12, Font.BOLD);
        Font valueFont = new Font(Font.HELVETICA, 22, Font.BOLD, new Color(13, 110, 253));

        PdfPCell cell = new PdfPCell();
        cell.setPadding(15);
        cell.setBorderColor(Color.LIGHT_GRAY);
        cell.addElement(new Paragraph(title, titleFont));
        cell.addElement(new Paragraph(value, valueFont));
        return cell;
    }

    private PdfPCell headerCell(String text) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, new Font(Font.HELVETICA, 11, Font.BOLD, Color.WHITE)));
        cell.setBackgroundColor(new Color(33, 37, 41));
        cell.setPadding(8);
        return cell;
    }

    private PdfPCell bodyCell(String text, boolean alternate) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, new Font(Font.HELVETICA, 11)));
        cell.setPadding(8);
        if (alternate) {
            cell.setBackgroundColor(new Color(248, 249, 250));
        }
        return cell;
    }
}
