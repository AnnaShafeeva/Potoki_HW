import basket.Basket;

import java.io.File;
import java.util.Scanner;

public class Main {
    final static String[][] PRODUCTS = {{"Молоко", "100"}, {"Сыр", "250"}, {"Картофель", "80"}, {"Чай", "80"}};

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Basket basket = new Basket(PRODUCTS);
        File basketTxt = new File("basket.txt");
        if (basketTxt.exists()) {
            Basket loadedBasket = Basket.loadFromFile(basketTxt);
            for (int i = 0; i < PRODUCTS.length; i++) {
                for (int j = 0; j < loadedBasket.getProductsList().length; j++) {
                    if (loadedBasket.getProductsList()[j][0].equals(PRODUCTS[i][0])) {
                        basket.addToCart(i, Integer.parseInt(loadedBasket.getProductsList()[j][1]));
                    }
                }
            }
            basket.printCart();
        } else {
            System.out.println("Список не найден. Будет создан новый файл записи");
        }
        System.out.println();
        while (true) {
            printList();
            System.out.println("Введите номер строки продукта и его количество через пробел." +
                    "Чтобы завершить покупку, введите \"end\":");
            String choice = scanner.nextLine();

            if (choice.equals("end")) {
                break;
            }

            String[] parts = choice.split(" "); // создаем массив из номера товара и количества
            if (parts.length != 2) {
                System.out.println("Некорректный ввод! Нужно ввести два числа!");
                continue;
            }
            int productNumber;
            int productCount;
            try {
                productNumber = Integer.parseInt(parts[0]) - 1;
                productCount = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                System.out.println("Некорректный ввод!");
                continue;
            }
            if (productNumber < 0 || productNumber > PRODUCTS.length) {
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
            basket.saveTxt(basketTxt);
        }
        basket.printCart();
    }

    static void printList() {
        System.out.println("Список продуктов\n");
        for (String[] product : PRODUCTS) {
            System.out.println(product[0] + ", " + product[1] + " руб/шт");
        }
    }
}