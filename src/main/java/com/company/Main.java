package com.company;

import com.company.service.DemoService;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        calcElapsedTime("Demo_xml", () -> {
            if (args.length < 4) {
                System.out.println("Недостаточно параметров для запуска программы");
                System.exit(1);
            }
            int countFields = 0;
            try {
                countFields = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                System.out.println(String.format("Параметр %s должно быть целым числом", args[3]));
                System.exit(1);
            }
            if (countFields < 1) {
                System.out.println(String.format("Параметр %s должно быть больше ноля", args[3]));
                System.exit(1);
            }

            DemoService demoService = new DemoService();//"jdbc:postgresql://localhost:5432/demo_db", "demo", "123456", 10000000);
            demoService.setJdbcUrl(args[0]);
            demoService.setJdbcUserName(args[1]);
            demoService.setJdbcPassword(args[2]);
            demoService.setCountFields(countFields);

            try {
                calcElapsedTime("insertFields", () -> demoService.insertFields());
                calcElapsedTime("createXMLDocument", () -> {
                    try {
                        demoService.createXMLDocument("1.xml");
                    } catch (JsonGenerationException e) {
                        System.out.println("Ошибка при генерации xml документа");
                        System.out.println(e.getMessage());
                        System.exit(5);
                    } catch (JsonMappingException e) {
                        System.out.println("Ошибка при маппинге xml документа");
                        System.out.println(e.getMessage());
                        System.exit(5);
                    } catch (IOException e) {
                        System.out.println("Ошибка ввода вывода");
                        System.out.println(e.getMessage());
                        System.exit(3);
                    }
                });
            } catch (CannotGetJdbcConnectionException e) {
                System.out.println("Неудалось установить соединение с БД");
                System.out.println(e.getMessage());
                System.exit(2);
            } catch (BadSqlGrammarException e) {
                System.out.println("Ошибка в запросе к БД");
                System.out.println(e.getMessage());
                System.exit(2);
            } catch (DataAccessException e) {
                System.out.println("Недостаточно прав для работы с БД");
                System.out.println(e.getMessage());
                System.exit(2);
            }

            calcElapsedTime("modifyXMLDocument", () -> {
                try {
                    demoService.modifyXMLDocument("1.xml", "2.xml");
                } catch (TransformerException e){
                    System.out.println("Ошибка трансформации xml документа");
                    System.out.println(e.getMessage());
                    System.exit(4);
                }
            });
            calcElapsedTime("calcXMLDocument", () -> {
                try {
                    demoService.calcXMLDocument("2.xml");
                } catch (JsonParseException e) {
                    System.out.println("Ошибка при парсинге xml документа");
                    System.out.println(e.getMessage());
                    System.exit(5);
                } catch (JsonMappingException e) {
                    System.out.println("Ошибка при маппинге xml документа");
                    System.out.println(e.getMessage());
                    System.exit(5);
                } catch (IOException e) {
                    System.out.println("Ошибка ввода вывода");
                    System.out.println(e.getMessage());
                    System.exit(3);
                }
            });
        });
    }


    @FunctionalInterface
    public interface MyFunction {
        void execute();
    }


    public static void calcElapsedTime(String task, MyFunction function) {
        System.out.println("Start " + task);
        long startTime = System.nanoTime();
        function.execute();
        System.out.println(task + " elapsed: " +
                TimeUnit.SECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS) + " seconds");
    }

}
