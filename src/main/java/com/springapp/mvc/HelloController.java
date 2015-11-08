package com.springapp.mvc;

import be.quodlibet.boxable.BaseTable;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.List;


@Controller
@RequestMapping("/")
public class HelloController {
    @RequestMapping(method = RequestMethod.GET)
    public String printWelcome(ModelMap model, HttpServletResponse response) throws IOException {
        samplePdf();
        model.addAttribute("message", "Hello world!");
        return "hello";
    }

    private void samplePdf() throws IOException {
        PDDocument doc = new PDDocument();
        PDPage page = PdfStatisticCreator.addNewPage(doc);

        PdfStatisticCreator creator = new PdfStatisticCreator();

        BaseTable table = PdfStatisticCreator.createTable(doc, page, 10f);
        PdfStatisticCreator.createHeader(table, "New Header", 15f, PDType1Font.HELVETICA_BOLD);
        creator.createParamsHeader(table, 15f, PDType1Font.HELVETICA_OBLIQUE);
        creator.createContent(table, getList(), 10f, PDType1Font.HELVETICA, 6);

        table.draw();
        PdfStatisticCreator.savePdf(doc, "pdf_sample");
        doc.close();
    }

    private List<MyClass> getList() {
        List<MyClass> list = new ArrayList<MyClass>();
        list.add(new MyClass("sd- Two 8-Three ggFour Five- Six -Seven-f -asd asd Eight", "abely", 1));
        list.add(new MyClass("OneAAAAhdfjSDFJKFJSDHJFHDKLFJDKsdhfjsdhfksdjhfjksdhfjkhdjFDSGSDGkfsdhfjhdjfkdfsdAAAAAAAAAAAAAAAAAAAAAAA skoval", "skoval", 2));
        list.add(new MyClass("abelyfhjhdjfdhfddfhdsjfhdjhfdsfdfjdksfjdjfkdfjkdjfdjfdsfkdjfjdkfdf fdshfjsdhfjdfdfsdf", "AAA", 3));
        list.add(new MyClass("helloJFKSDVSDNHVHDJHFJSHIEJJKDSKFHDFJHDJFHDJHGFJHGJFHGRGRJHGJRHGJRGHRJ", "world", 4));
        for (int i = 0; i < 5; i++) {
            list.addAll(list);
        }
        return list;
    }
//    OneAAAAhdfjSDFJKFJSDHJFHDKLFJDKsdhfjsdhfksdjhfjksdhfjkhdjFDSGSDGkfsdhfjhdjfkdfsdAAAAAAAAAAAAAAAAAAAAAAA
}