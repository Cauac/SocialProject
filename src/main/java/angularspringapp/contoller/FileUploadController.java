package angularspringapp.contoller;

import angularspringapp.dao.MSSQLUserDAO;
import angularspringapp.entity.Payment;
import angularspringapp.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Controller
@RequestMapping("/")
public class FileUploadController implements Serializable {

    @Autowired
    MSSQLUserDAO userDAO;

    @RequestMapping(value = "uploadPaymentFile.xml", method = RequestMethod.POST)
    public void uploadXML(HttpServletRequest req, HttpServletResponse res) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userDAO.findByName(name);
        List<Payment> payments = new ArrayList();

        try {
            ServletFileUpload upload = new ServletFileUpload();
            FileItemIterator iterator = upload.getItemIterator(req);
            iterator.next();
            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = dBuilder.parse(iterator.next().openStream());
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("service");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    Payment payment = new Payment();
                    payment.setServiceName(eElement.getAttribute("name"));
                    payment.setYear(Integer.parseInt(eElement.getElementsByTagName("year").item(0).getTextContent()));
                    payment.setMonth(Integer.parseInt(eElement.getElementsByTagName("month").item(0).getTextContent()));
                    payment.setAmount(Integer.parseInt(eElement.getElementsByTagName("amount").item(0).getTextContent()));
                    payment.setUser(currentUser);

                    payments.add(payment);
                }
            }
            userDAO.savePayments(payments);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @RequestMapping(value = "uploadPaymentFile.xlsx", method = RequestMethod.POST)
    public void uploadXLSX(HttpServletRequest req) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userDAO.findByName(name);

        try {
            ServletFileUpload upload = new ServletFileUpload();
            FileItemIterator iterator = upload.getItemIterator(req);
            iterator.next();

            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            File file = File.createTempFile("entry", "xslx");
            writeFile(iterator.next().openStream(), new FileOutputStream(file));
            ZipFile zipFile = new ZipFile(file);
            Enumeration files = zipFile.entries();

            Document sharedStringDoc = null;
            Document sheetDoc = null;

            while (files.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) files.nextElement();
                if ("xl/sharedStrings.xml".equals(entry.getName())) {
                    sharedStringDoc = dBuilder.parse(zipFile.getInputStream(entry));
                }
                if ("xl/worksheets/sheet1.xml".equals(entry.getName())) {
                    sheetDoc = dBuilder.parse(zipFile.getInputStream(entry));
                }
            }

            String[] sharedStrings = readSharedStrings(sharedStringDoc);
            Node sheetData = sheetDoc.getDocumentElement().getElementsByTagName("sheetData").item(0);
            Collection<Payment> payments = readPaymentsFromSheerData(currentUser, sheetData, sharedStrings);
            userDAO.savePayments(payments);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private Collection<Payment> readPaymentsFromSheerData(User user, Node sheetData, String[] sharedStrings) {
        Collection<Payment> payments = new ArrayList<>();
        NodeList rows = sheetData.getChildNodes();

        for (int i = 1; i < rows.getLength(); i++) {
            Node row = rows.item(i);
            NodeList fields = row.getChildNodes();
            Payment payment = new Payment();
            Element serviceNameField = (Element) fields.item(0);
            Element yearField = (Element) fields.item(1);
            Element monthField = (Element) fields.item(2);
            Element amountField = (Element) fields.item(3);
            payment.setServiceName(getXLSXFieldValue(serviceNameField, sharedStrings));
            payment.setYear(Integer.parseInt(yearField.getFirstChild().getTextContent()));
            payment.setMonth(Integer.parseInt(monthField.getFirstChild().getTextContent()));
            payment.setAmount(Integer.parseInt(amountField.getFirstChild().getTextContent()));
            payment.setUser(user);
            payments.add(payment);
        }

        return payments;
    }

    private String[] readSharedStrings(Document doc) {
        doc.getDocumentElement().normalize();

        NodeList nList = doc.getElementsByTagName("sst");
        Node item = nList.item(0);
        NodeList childNodes = item.getChildNodes();

        String[] result = new String[childNodes.getLength()];
        for (int i = 0; i < childNodes.getLength(); i++) {
            Element eElement = (Element) childNodes.item(i);
            result[i] = eElement.getElementsByTagName("t").item(0).getTextContent();
        }
        return result;
    }

    private String getXLSXFieldValue(Element eElement, String[] sharedStrings) {
        if (eElement.hasAttribute("t")) {
            int index = Integer.parseInt(eElement.getFirstChild().getTextContent());
            return sharedStrings[index];
        }

        return eElement.getFirstChild().getTextContent();
    }

    private void writeFile(InputStream inputStream, FileOutputStream outputStream) throws IOException {
        int read = 0;
        byte[] bytes = new byte[1024];

        while ((read = inputStream.read(bytes)) != -1) {
            outputStream.write(bytes, 0, read);
        }
        inputStream.close();
        outputStream.close();
    }

}

