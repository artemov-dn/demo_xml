package com.company.service;

import com.company.dao.DemoDAO;
import com.company.model.Entries;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;

public class DemoService {

    private DriverManagerDataSource dataSource = new DriverManagerDataSource();

    private JdbcTemplate jdbcTemplate = new JdbcTemplate();

    private DemoDAO demoDAO = new DemoDAO();

    private String jdbcUrl;

    private String jdbcUserName;

    private String jdbcPassword;

    private int countFields;

    public DemoService() {
        dataSource.setDriverClassName("org.postgresql.Driver");
        jdbcTemplate.setDataSource(dataSource);
        demoDAO.setJdbcTemplate(jdbcTemplate);
    }


    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
        dataSource.setUrl(jdbcUrl);
    }

    public void setJdbcUserName(String jdbcUserName) {
        this.jdbcUserName = jdbcUserName;
        dataSource.setUsername(jdbcUserName);
    }

    public void setJdbcPassword(String jdbcPassword) {
        this.jdbcPassword = jdbcPassword;
        dataSource.setPassword(jdbcPassword);
    }

    public void setCountFields(int countFields) {
        this.countFields = countFields;
    }

    public void insertFields() throws DataAccessException {
        demoDAO.insertNFields(countFields);
    }

    public void createXMLDocument(String filename) throws DataAccessException, IOException {
        Entries entries = demoDAO.getAllFields();
        serializeToXML(filename, entries);
    }

    public void modifyXMLDocument(String fromFile, String toFile) throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Source xslt = new StreamSource(new File("modify.xslt"));
        Transformer transformer = factory.newTransformer(xslt);
        Source xml = new StreamSource(new File(fromFile));
        transformer.transform(xml, new StreamResult(new File(toFile)));
    }

    public void calcXMLDocument(String filename) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.setDefaultUseWrapper(false);
        Entries list = xmlMapper.readValue(new File(filename), Entries.class);
        if (list != null && list.getEntryList() != null && list.getEntryList().size() > 0) {
            long sum = list.getEntryList().parallelStream()
                    .map(dto -> (long) dto.getField())
                    .reduce((s1, s2) -> s1 + s2)
                    .orElse(0L);
            System.out.println(String.format("All fileds sum = %d", sum));
        }
    }


    private static void serializeToXML(String filename, Object obj) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.setDefaultUseWrapper(false);
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        xmlMapper.writeValue(new File(filename), obj);
    }

}
