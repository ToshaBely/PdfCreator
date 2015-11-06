package com.springapp.mvc;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.Paragraph;
import be.quodlibet.boxable.Row;
import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PdfStatisticCreator {
    public static PDPage addNewPage(PDDocument doc) {
        PDPage page = new PDPage();
        doc.addPage(page);
        return page;
    }

    public static BaseTable createTable(PDDocument doc, PDPage page, float margin) throws IOException {
        float yStartNewPage = page.findMediaBox().getHeight() - 2 * margin;
        float tableWidth = page.findMediaBox().getWidth() - 2 * margin;
        boolean drawContent = true;
        float yStart = yStartNewPage;
        float bottomMargin = 70;
        return new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true, drawContent);
    }

    public static void createHeader(BaseTable table, String text, float height, PDFont font) {
        Row headerRow = table.createRow(height);
        Cell cell = headerRow.createCell(100, text);
        cell.setFont(font);

        table.setHeader(headerRow);
    }

    public void createParamsHeader(BaseTable table, float height, PDFont font) {
        Row row = table.createRow(height);
        Cell cell = row.createCell(100 / 5 * 2, "Name");
        cell.setFont(font);
        cell = row.createCell(100 / 5 * 2, "Login");
        cell.setFont(font);
        cell = row.createCell(100 / 5, "ID");
        cell.setFont(font);
    }

    public void createContent(BaseTable table, List<MyClass> list, float height, PDFont font, int size) throws IOException {
        Row row;
        Cell cell;
        for (MyClass myClass : list) {
            row = table.createRow(height);
            cell = row.createCell(100 / 5, "");
            cell.setText(getCellString(myClass.getName(), font, size, cell.getWidth()));
            cell.setFont(font);
            cell.setFontSize(size);
            cell = row.createCell(100 / 5 * 2, "");
            cell.setText(getCellString(myClass.getLogin(), font, size, cell.getWidth()));
            cell.setFont(font);
            cell.setFontSize(size);
            cell = row.createCell(100 / 5 * 2, Integer.toString(myClass.getId()));
            cell.setFont(font);
            cell.setFontSize(size);
        }
    }

    private String getCellString(String text, PDFont font, float fontSize, float cellWidth) throws IOException {
        Paragraph paragraph = new Paragraph(text, font, (int)fontSize, (int)cellWidth);
        List<String> list = paragraph.getLines();
        List<String> resultList = new ArrayList<String>();

        float textWidth;
        String nextLine = "";
        for (String line : list) {
            line = nextLine + " " + line;
            textWidth = font.getStringWidth(StringUtils.repeat("D", line.length())) / 1000 * (fontSize);
            if (textWidth > cellWidth) {
                List<String> splitList = splitCellString(line, cellWidth, fontSize, font);
                resultList.add(splitList.get(0));
                nextLine = splitList.get(1);
            } else {
                resultList.add(line);
            }
        }
        textWidth = font.getStringWidth(StringUtils.repeat("D", nextLine.length())) / 1000 * (fontSize);
        while (textWidth > cellWidth) {
            List<String> splitList = splitCellString(nextLine, cellWidth, fontSize, font);
            resultList.add(splitList.get(0));
            nextLine = splitList.get(1);
            textWidth = font.getStringWidth(StringUtils.repeat("D", nextLine.length())) / 1000 * (fontSize);
        }
        if (StringUtils.isNotBlank(nextLine)) {
            resultList.add(nextLine);
        }
        return getMultiStringFromList(resultList);
//        if (textWidth > cellWidth) {
//            List <String> list = new ArrayList<String>();
//            float kf = textWidth / cellWidth;
//            int maxLength = (int) Math.floor((float) text.length() / kf);
//            int position = 0; //maxLength;
//            while (position + maxLength < text.length()) {
//                int spacePlace = text.lastIndexOf(" ", position + maxLength);
//                if (spacePlace < position) {
//                    list.add(text.substring(position, position + maxLength));
//                    position += maxLength;
//                } else {
//                    spacePlace++;
//                    String line = text.substring(position, spacePlace);
//                    position += line.length();
//                    if (StringUtils.isNotBlank(line)) {
//                        list.add(line);
//                    }
//                }
//            }
////            position -= maxLength;
//            if (position != text.length()) {
//                list.add(text.substring(position));
//            }
//            return getMultiStringFromList(list);
//        } else {
//            return text;
//        }
    }

    private List<String> splitCellString(String string, float cellWidth, float fontSize, PDFont font) throws IOException {
        float textWidth = font.getStringWidth(StringUtils.repeat("D", string.length())) / 1000 * (fontSize);

        List <String> list = new ArrayList<String>();
        float kf = textWidth / cellWidth;
        int maxLength = (int) Math.floor((float) string.length() / kf);
        int spacePlace = string.lastIndexOf(" ", maxLength);
        String firstString;
        if (spacePlace == -1) {
            firstString = string.substring(0, maxLength);
            textWidth = font.getStringWidth(firstString) / 1000 * fontSize;
            float size = cellWidth / textWidth - 1;
            if (size > 0.1) {
                int lengthSize = (int) Math.floor((float) firstString.length() / size);
                firstString += string.substring(maxLength, maxLength + lengthSize);
                maxLength += lengthSize;
            }
        } else {
            spacePlace++;
            if (StringUtils.isBlank(string.substring(0, spacePlace))) {
                return splitCellString(string.substring(spacePlace), cellWidth, fontSize, font);
            }
            firstString = string.substring(0, spacePlace);
            textWidth = font.getStringWidth(firstString) / 1000 * fontSize;
            float size = cellWidth / textWidth - 1;
            if (size > 0.1) {
                int lengthSize = (int) Math.floor((float) firstString.length() / size);
                firstString += string.substring(maxLength, maxLength + lengthSize);
            }
            maxLength = spacePlace;
        }
        list.add(firstString);
        list.add(string.substring(maxLength));
        return list;
    }

    private static String getMultiStringFromList(List<String> list) {
        if (list == null || list.size() == 0) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder("");
        for (String string : list) {
            stringBuilder.append(string);
            stringBuilder.append("\n\r");
        }
        return stringBuilder.toString();
    }

    public static void savePdf (PDDocument doc, String dest) throws IOException {
        File file = new File(dest + ".pdf");
        try {
            doc.save(file);
        } catch (COSVisitorException e) {
            e.printStackTrace();
        }
    }
}
