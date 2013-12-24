package angularspringapp.contoller;

import angularspringapp.dao.MSSQLUserDAO;
import angularspringapp.entity.Payment;
import angularspringapp.entity.User;
import org.apache.commons.fileupload.FileUploadException;
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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(res.getOutputStream());
            ServletFileUpload upload = new ServletFileUpload();
            FileItemIterator iterator = upload.getItemIterator(req);
            iterator.next();
            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = dBuilder.parse(iterator.next().openStream());
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("service");
            writer.println("----------------------------");
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

                    writer.println("Service name : " + payment.getServiceName());
                    writer.println("Amount : " + payment.getAmount());
                }
            }
            userDAO.savePayments(payments);
            writer.flush();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    @RequestMapping(value = "uploadPaymentFile.xlsx", method = RequestMethod.POST)
    public void uploadXLSX(HttpServletRequest req, HttpServletResponse res) {

        try {
            ServletFileUpload upload = new ServletFileUpload();
            FileItemIterator iterator = upload.getItemIterator(req);
            iterator.next();
            ZipInputStream zis = new ZipInputStream(iterator.next().openStream());
            ZipEntry nextEntry = zis.getNextEntry();
        } catch (FileUploadException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
