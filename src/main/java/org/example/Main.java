package org.example;

import org.example.basket.Basket;
import org.example.log.ClientLog;
import java.io.File;
import java.util.Scanner;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Main {
    final static String[][] PRODUCTS = {{"Молоко", "100"}, {"Сыр", "250"}, {"Картофель", "80"}, {"Чай", "80"}};

    static boolean isBasketLoadEnabled = false;
    static String basketLoadFromFileName = "basket.json";
    static String basketLoadFormat = "json";

    static boolean isBasketSaveEnabled = false;
    static String basketSaveToFileName = "basket.json";
    static String basketSaveFormat = "json";

    static boolean isLogEnabled = false;
    static String logFileName = "log.csv";

    public static void main(String[] args) {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File("shop.xml"));
            Node root = doc.getDocumentElement();
            readXML(root);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println("isBasketLoadEnabled " + isBasketLoadEnabled);
        Scanner scanner = new Scanner(System.in);
        Basket basket = new Basket(PRODUCTS);
        String basketLoadFileName = basketLoadFromFileName.split("\\.")[0] + "." + basketLoadFormat;
        String basketSaveFileName = basketSaveToFileName.split("\\.")[0] + "." + basketSaveFormat;
        File basketSaveFile = new File(basketSaveFileName);
        File basketLoadFile = new File(basketLoadFileName);
        File operationsLog = new File(logFileName);
        ClientLog log = new ClientLog();
        if (isBasketLoadEnabled == true) {
            if (basketLoadFile.exists()) {
                String[][] loadedBasket = PRODUCTS;
                if (basketLoadFormat.equals("json")) {
                    loadedBasket = Basket.loadFromJSON(basketLoadFile);
                } else if (basketLoadFormat.equals("txt")) {
                    loadedBasket = Basket.loadFromFile(basketLoadFile);
                }
                for (int i = 0; i < PRODUCTS.length; i++) {
                    for (int j = 0; j < loadedBasket.length; j++) {
                        if (loadedBasket[j][0].equals(PRODUCTS[i][0])) {
                            basket.addToCart(i, Integer.parseInt(loadedBasket[j][1]));
                        }
                    }
                }
                basket.printCart();
            } else {
                System.out.println("Список не найден. Будет создан новый файл записи");
            }
        } else System.out.println("Выбрана опция создания нового файла корзины (load disabled)");
        System.out.println();
        while (true) {
            printList();
            System.out.println("Введите номер строки продукта и его количество через пробел." +
                    "Чтобы завершить покупку, введите \"end\":");
            String choice = scanner.nextLine();

            if (choice.equals("end")) {
                break;
            }

            String parts[] = choice.split(" "); // создаем массив из номера товара и количества
            if (parts.length != 2) {
                System.out.println("Некорректный ввод! Нужно ввести два числа!");
                continue;
            }
            int productNumber;
            int productCount;
            try {
                productNumber = Integer.parseInt(parts[0]) - 1; //порядковый номер продукта в массиве,
                productCount = Integer.parseInt(parts[1]); // количество единиц данного продукта
                log.log(productNumber, productCount);
            } catch (NumberFormatException e) {
                System.out.println("Некорректный ввод!");
                continue;
            }
            if (productNumber < 0 || productNumber >= PRODUCTS.length) {
                System.out.println("Выберите порядковый номер строки списка продуктов");
                continue;
            } else if (productCount < 0) {
                System.out.println("Некорректный ввод! Вы ввели отрицательное число");
                continue;
            } else if (productCount == 0) {
                System.out.println("Вы ничего не положили в корзину");
                continue;
            }
            System.out.println("Продукт добавлен в корзину: " + PRODUCTS[productNumber][0] + ",в количестве " + productCount + " шт");
            basket.addToCart(productNumber, productCount);
            if (isBasketSaveEnabled && basketSaveFormat.equals("json")) {
                basket.saveJSON(basketSaveFile);
            } else if (isBasketSaveEnabled && basketSaveFormat.equals("txt")) {
                basket.saveTxt(basketSaveFile);
            }
        }
        basket.printCart();
        if (isBasketSaveEnabled == false) System.out.println("Опция сохранения файла корзины отключена");
        if (isLogEnabled) {
            log.exportAsCSV(operationsLog);
        } else System.out.println("Опция сохранения Log-файла отключена");
    }

    static void printList() {
        System.out.println("Список продуктов\n");
        for (int i = 0; i < PRODUCTS.length; i++) {
            System.out.println(PRODUCTS[i][0] + ", " + PRODUCTS[i][1] + " руб/шт");
        }
    }


    static void readXML(Node node) {
        NodeList paramGroups = node.getChildNodes();
        for (int i = 0; i < paramGroups.getLength(); i++) {
            Node loadSaveLog = paramGroups.item(i);
            if (Node.ELEMENT_NODE == loadSaveLog.getNodeType()) {
                // Запрашиваем список у load, save и log
                NodeList parametersValues = loadSaveLog.getChildNodes();
                for (int j = 0; j < parametersValues.getLength(); j++) {
                    Node paramValue = parametersValues.item(j);
                    // Убираем текстовые ноды
                    if (paramValue.getNodeType() != Node.TEXT_NODE) {
                        // рассматриваем и присваиваем поля внутри каждого элемента
                        if (loadSaveLog.getNodeName().equals("load")) {
                            if (paramValue.getNodeName().equals("enabled")) {
                                if (paramValue.getFirstChild().getTextContent().equals("true")) {
                                    isBasketLoadEnabled = true;
                                } else isBasketLoadEnabled = false;
                            } else if (paramValue.getNodeName().equals("fileName")) {
                                basketLoadFromFileName = paramValue.getFirstChild().getTextContent();
                            } else if (paramValue.getNodeName().equals("format")) {
                                basketLoadFormat = paramValue.getFirstChild().getTextContent();
                            }
                        } else if (loadSaveLog.getNodeName().equals("save")) {
                            if (paramValue.getNodeName().equals("enabled")) {
                                if (paramValue.getFirstChild().getTextContent().equals("true")) {
                                    isBasketSaveEnabled = true;
                                } else isBasketSaveEnabled = false;
                            } else if (paramValue.getNodeName().equals("fileName")) {
                                basketSaveToFileName = paramValue.getFirstChild().getTextContent();
                            } else if (paramValue.getNodeName().equals("format")) {
                                basketSaveFormat = paramValue.getFirstChild().getTextContent();
                            }
                        } else if (loadSaveLog.getNodeName().equals("log")) {
                            if (paramValue.getNodeName().equals("enabled")) {
                                if (paramValue.getFirstChild().getTextContent().equals("true")) {
                                    isLogEnabled = true;
                                } else isLogEnabled = false;
                            } else if (paramValue.getNodeName().equals("fileName")) {
                                logFileName = paramValue.getFirstChild().getTextContent();
                            }
                        }
                    }
                }
            }
        }
    }
}