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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        Cell cell = row.createCell(100 / 5, "Name");
        cell.setFont(font);
        cell = row.createCell(100 / 5 * 2, "Login");
        cell.setFont(font);
        cell = row.createCell(100 / 5 * 2, "ID");
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
            textWidth = font.getStringWidth(line.toUpperCase()) / 1000 * (fontSize);
            if (textWidth > cellWidth) {
                List<String> splitList = splitCellString(line, cellWidth, fontSize, font);
                resultList.add(splitList.get(0));
                nextLine = splitList.get(1);
            } else {
                resultList.add(line);
            }
        }
        textWidth = font.getStringWidth(nextLine.toUpperCase()) / 1000 * (fontSize);
        while (textWidth > cellWidth) {
            List<String> splitList = splitCellString(nextLine, cellWidth, fontSize, font);
            resultList.add(splitList.get(0));
            nextLine = splitList.get(1);
            textWidth = font.getStringWidth(nextLine.toUpperCase()) / 1000 * (fontSize);
        }
        if (StringUtils.isNotBlank(nextLine)) {
            resultList.add(nextLine);
        }
        return getMultiStringFromList(resultList);
    }

    private List<String> splitCellString(String string, float cellWidth, float fontSize, PDFont font) throws IOException {
        List <String> list = new ArrayList<String>();
        String firstString;

        float upperTextWidth = font.getStringWidth(string.toUpperCase()) / 1000 * fontSize;
        float kf = cellWidth / upperTextWidth;
        int firstLength = (int) Math.floor(string.length() * kf);

        Pattern pattern = Pattern.compile("(.*\\W)+(.*)");
        Matcher matcher = pattern.matcher(string.substring(0, firstLength));
        if (!matcher.matches()) {
            firstString = string.substring(0, firstLength);
        } else {
            firstString = matcher.group(matcher.groupCount() - 1);
            firstLength = firstString.length();
            if (StringUtils.isBlank(firstString)) {
                return splitCellString(string.substring(firstLength), cellWidth, fontSize, font);
            }
        }

        float textWidth = font.getStringWidth(firstString) / 1000 * fontSize;
        upperTextWidth = font.getStringWidth(firstString.toUpperCase()) / 1000 * fontSize;
        float freeSpace = cellWidth / textWidth - 1;
        if (freeSpace > 0.1) {
            freeSpace -= 0.05;
            kf = Math.max(firstString.length() - 5, 0) / upperTextWidth;
            int addLength = (int) (cellWidth * freeSpace * kf);
            if (firstLength + addLength > string.length()) {
                addLength = string.length() - firstLength;
            }
            firstString += string.substring(firstLength, firstLength + addLength);
            firstLength += addLength;
        }
        list.add(firstString);
        list.add(string.substring(firstLength));
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
        return stringBuilder.substring(0, stringBuilder.length() - 2);
    }

    public static void savePdf (PDDocument doc, String dest) throws IOException {
        dest = "/home/anton/" + dest;
        File file = new File(dest + ".pdf");
        try {
            doc.save(file);
        } catch (COSVisitorException e) {
            e.printStackTrace();
        }
    }
}
