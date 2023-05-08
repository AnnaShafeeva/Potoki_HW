import basket.Basket;

import java.io.File;
import java.util.Scanner;

public class Main {
    final static String[][] PRODUCTS = {{"Молоко", "100"}, {"Сыр", "250"}, {"Картофель", "80"}, {"Чай", "80"}};

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Basket basket = new Basket(PRODUCTS);
        File basketBin = new File("basket.bin");
        if (basketBin.exists()) {
            basket = Basket.loadFromBinFile(basketBin);
            basket.printCart();
        } else System.out.println("Файл не найден. Будет создан новый файл записи");
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
            basket.saveBin(basketBin);
        }
        basket.printCart();
    }

    static void printList() {
        System.out.println("Список продуктов\n");
        for (int i = 0; i < PRODUCTS.length; i++) {
            System.out.println(PRODUCTS[i][0] + ", " + PRODUCTS[i][1] + " руб/шт");
        }
    }
}