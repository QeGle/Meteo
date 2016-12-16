import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Класс для расшифровки строки с метеоданными, получаемыми из файла.
 */

class Convert {
    private final String dataNotFound = "данные не найдены.";
    private int countRepeat = 1;
    private int COUNT = 0;
    private final String anotherFormat = "\nошибка, неправильный формат данных: ";
    private final StringBuilder messageBuilder = new StringBuilder();
    private final StringBuilder itogBuilder = new StringBuilder();


    /**
     * Массив предназначен для {@link #analyzePrecipitation(String)}
     * Содержит продолжительность выпадения осадков.
     */
    private static final String[] precipitation = new String[]{
            "менее 1 ч.",
            "от 1 до 3 ч.",
            "от 3 до 6 ч.",
            "от 6 до 12 ч.",
            "более 12 ч."
    };


    /**
     * Массив предназначен для {@link #analyzeIceDepth(String)}
     * Содержит высоту снега на льду.
     */
    private final String[] snowHeightMass = new String[]{
            "На льду снега нет.",
            "менее 5 см.",
            "5-10 см.",
            "11-15 см.",
            "16-20 см.",
            "21-25 см.",
            "26-35 см.",
            "36-50 см.",
            "51-70 см.",
            "больше 70 см."
    };

    /**
     * Массив предназначен для {@link #analyzeIceType(String)}
     * Содержит тип ледяного явления
     */
    private final String[] iceType = new String[]{
            "Сало",
            "Снежура",
            "Заберги(первичные, наносные); припай шириной менее 100 м - для озер и водохранилищ",
            "Припай шириной более 100 м - для озер и водохранилищ",
            "Забреги нависшие",
            "Ледоход; для озер, водохранилищ - дрейф льда",
            "Ледоход;лед из притока, озера, водохранилища",
            "Ледоход поверх ледяного покрова",
            "Шугоход",
            "Внутриводный лед(донный, глубинный)",
            "Пятры",
            "Осевший лед(на береговой мели после понижения уровня)",
            "Навалы льда на берегах(ледяные валы)",
            "Ледяная перемычка в створе поста",
            "Ледяная перемычка выше поста",
            "Ледяная перемычка ниже поста",
            "Неверный идентификатор",
            "Неверный идентификатор",
            "Неверный идентификатор",
            "Затор льда выше поста",
            "Затор льда ниже поста",
            "Затор льда искусственно разрушается",
            "Неверный идентификатор",
            "Зажор льда выше поста",
            "Зажор льда ниже поста",
            "Зажор льда искусственно разрушается",
            "Вода на льду",
            "Вода течет поверх льда(после промерзания реки, при наличии воды подо льдом)",
            "Закраины",
            "Лед потмнел",
            "Снежица",
            "Лед подняло(вспучило)",
            "Подвижка льда",
            "Разводья",
            "Лед тает на месте",
            "Забереги остаточные",
            "Наслуд",
            "Битый лед (для озер, водохранилищ, устьевых участков рек)",
            "Блинчатый лед",
            "Ледяные поля - для озер, водохранилищ, устьевых участков рек",
            "Ледяная каша - для озер, водохранилищ, устьевых участков рек",
            "Стамуха",
            "Лед относит (отнесло) от берега - для озер, водохранилищ",
            "Лед прижимает (прижало) к берегу - для озер, водохранилищ",
            "Неверный идентификатор",
            "Неверный идентификатор",
            "Неверный идентификатор",
            "Неверный идентификатор",
            "Неверный идентификатор",
            "Неверный идентификатор",
            "Неверный идентификатор",
            "Неверный идентификатор",
            "Ледостав неполный",
            "Ледяной покров с полыньями (промоинами, пропаринами)",
            "Ледостав, ровный ледяной покров",
            "Ледостав, ледяной покров с торосами",
            "Ледяной покров с грядами тросов - для водохранилищ",
            "Шуговая дорожка",
            "Неверный идентификатор",
            "Неверный идентификатор",
            "Неверный идентификатор",
            "Неверный идентификатор",
            "Начало навиигации",
            "Конец навигации",
            "Неверный идентификатор",
            "Неверный идентификатор",
            "Забор воды выше поста",
            "Забор воды ниже поста",
            "Забор воды выше поста прекратился",
            "Забор воды ниже поста прекратился",
            "Сброс воды выше поста",
            "Сброс воды ниже поста",
            "Сброс воды выше поста прекратился",
            "Сброс воды ниже поста прекратился",
            "Плотина (перемычка, запруда, дамба) выше поста",
            "Плотина (перемычка, запруда, дамба) ниже поста",
            "Разрушена плотина (перемычка, запруда, дамба) выше поста",
            "Разрушена плотина (перемычка, запруда, дамба) ниже поста",
            "Подпор от засорения русла",
            "Подпор от мостовых переправ",
            "Попуски воды из озера, водохранилища"
    };


    public static void main(String[] args) {
        Convert a = new Convert();
        System.out.print(a.sort("test.txt"));
    }


    /**
     * Производит чтение символов из файла и возвращает расшифрованные данные
     *
     * @param fileName Путь к файлу.
     * @return Данные в расшифрованном виде, если чтение из файла прошло удачно или строку: Ошибка при чтении файла
     * Если информация в файле отсутствует или не соответствует требованиям - строку: Информация отсутствует
     * Использует {@link #analyzeString(String)} для анализа строк из файла
     */
    String sort(String fileName) {
        StringBuilder str = new StringBuilder();
        int ch;
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            do {
                do {
                    ch = reader.read();

                    //Не завершается без этого if
                    if (ch != -1)
                        str.append((char) ch);
                } while ((!str.toString().contains("HHZZ")) && (ch != -1));

                StringBuilder builder = new StringBuilder();

                //Не завершается без этого if
                if (ch != -1) {
                    do {
                        ch = reader.read();

                        //61 равно '='
                        if (ch != 61)
                            builder.append((char) ch);
                        count++;

                    } while (ch != 61);

                    itogBuilder.append(analyzeString(builder.toString()));
                    countRepeat = 1;
                    messageBuilder.delete(0,messageBuilder.length()-1);
                    setMessage("");
                    COUNT = 0;
                    str.delete(0, str.length());

                }
            } while (ch != -1);

        } catch (IOException e) {
            itogBuilder.delete(0, messageBuilder.length() - 1);
            itogBuilder.append("Ошибка при чтении файла");
        }
        if (count == 0)
            return "Информация отсутствует";
        return itogBuilder.toString();
    }

    /**
     * Анализирует строку с информацией, полученной от {@link #sort(String)}
     * разбивает ее на меньшие по размеру строки, относящиеся к разным параметрам.
     * Вызываются методы для анализа каждой такой строки.
     *
     * @param str Строка, сичтанная из файла в {@link #sort(String)}
     */
    private StringBuilder analyzeString(String str) {


        StringBuilder station = new StringBuilder();

        StringBuilder stationMessage = new StringBuilder();

        int count = 0;
        StringBuilder analyzeLine = new StringBuilder();

        str = delWhitespace(str);


        for (int i = 0; i < str.length(); i++) {
            if ((str.charAt(i) != '\r') && (str.charAt(i) != '\t') && (str.charAt(i) != '\n'))
                analyzeLine.append(str.charAt(i));
        }

        do {
            if (analyzeLine.charAt(count) != ' ')
                station.append(analyzeLine.charAt(count));
            count++;
        } while (station.length() != 5);

        analyzeLine.delete(0, count);
        analyzeLine = delWhitespace(analyzeLine);


        if (station.length() == 5) {
            //Номер станции
            String stationNumber;
            String podStr = station.substring(0, 2);
            if (podStr.contains("//"))
                stationNumber = "параметр отсутствует";
            else if (isNumber(podStr))
                stationNumber = podStr;
            else
                stationNumber = "ошибка: " + podStr;

            stationMessage.append("Номер станции: " + stationNumber + ".");

            //Номер поста
            podStr = station.substring(2);
            String postNumber;
            if (podStr.contains("//"))
                postNumber = "параметр отсутствует";
            else if (isNumber(podStr))
                postNumber = podStr;
            else
                postNumber = "ошибка: " + podStr;


            stationMessage.append("\nНомер гидрологического поста: " + postNumber + ".");
        } else
            stationMessage.append(station + "ошибка, неверная длина: " + station);

        //Запись в общую строку
        setMessage(stationMessage);


        if (analyzeLine.length() > 6) {
            StringBuilder dataTime = new StringBuilder();
            StringBuilder dataTimeMess = new StringBuilder();
            String data;
            String time;
            count = 0;

            do {
                if (analyzeLine.charAt(count) != ' ')
                    dataTime.append(analyzeLine.charAt(count));
                count++;
            } while (dataTime.length() != 5);

            analyzeLine.delete(0, count);
            analyzeLine = delWhitespace(analyzeLine);


            if (dataTime.length() < 6) {
                String podStr = dataTime.substring(0, 2);
                //Дата
                if (podStr.contains("//"))
                    data = "параметр отсутствует";
                else if (isNumber(podStr))
                    data = podStr;
                else
                    data = "ошибка: " + podStr;

                dataTimeMess.append("Дата наблюдения: " + data + ".");


                //Время
                podStr = dataTime.substring(2, 4);
                if (podStr.contains("//"))
                    time = "параметр отсутствует";
                else if (isNumber(podStr))
                    time = podStr;
                else
                    time = "ошибка: " + podStr;
                if (isNumber(time))
                    dataTimeMess.append("\nСрок наблюдения: " + Integer.parseInt(time) + " ч.");
                else
                    dataTimeMess.append("\nСрок наблюдения: " + time);
            } else
                dataTimeMess.append("\nСрок наблюдения: ошибка, неверная длина: " + dataTime);

            //Запись в общую строку
            setMessage(dataTimeMess);
            String analiseLineStd = delWhitespaceInStr(analyzeLine).toString();
            if (analiseLineStd.length() % 5 == 0) {
                for (int i = 0; i < analiseLineStd.length() - 1; i += 5) {
                    StringBuilder fiveSymbols = new StringBuilder();
                    for (int j = 0; j < 5; j++) {
                        fiveSymbols.append(analiseLineStd.charAt(i + j));
                    }
                    int ch = fiveSymbols.charAt(0) - 48;
                    switch (ch) {
                        case 1:
                            if (ch == countRepeat) {
                                setMessage(analyzeWaterLvl(fiveSymbols.substring(1)));
                                countRepeat = ch;
                            } else
                                COUNT++;
                            break;
                        case 2:
                            if (ch > countRepeat) {
                                setMessage(analyzeChangeWaterLvl8h(fiveSymbols.substring(1)));
                                countRepeat = ch;
                            } else
                                COUNT++;
                            break;
                        case 3:
                            if (ch > countRepeat) {
                                setMessage(analyzeWaterLvl20h(fiveSymbols.substring(1)));
                                countRepeat = ch;
                            } else
                                COUNT++;
                            break;
                        case 4:
                            if (ch > countRepeat) {
                                setMessage(analyzeTempWaterAir(fiveSymbols.substring(1)));
                                countRepeat = ch;
                            } else
                                COUNT++;
                            break;
                        case 5:
                            if (ch > countRepeat) {
                                setMessage(analyzeIceType(fiveSymbols.substring(1)));
                                countRepeat = ch;
                            } else
                                COUNT++;
                            break;
                        case 6:
                            if (ch > countRepeat) {
                                setMessage(analyzeIceType(fiveSymbols.substring(1)));
                                countRepeat = ch;
                            } else
                                COUNT++;
                            break;
                        case 7:
                            if (ch > countRepeat) {
                                setMessage(analyzeIceDepth(fiveSymbols.substring(1)));
                                countRepeat = ch;
                            } else
                                COUNT++;
                            break;
                        case 8:
                            if (ch > countRepeat) {
                                setMessage(analyzeWaterConsumption(fiveSymbols.substring(1)));
                                countRepeat = ch;
                            } else
                                COUNT++;
                            break;
                        case 0:
                            if (countRepeat != 11) {
                                setMessage(analyzePrecipitation(fiveSymbols.substring(1)));
                                countRepeat = 11;
                            } else
                                COUNT++;
                            break;
                        default:
                            COUNT++;
                            break;
                    }
                }
            } else
                setMessage("Ошибка во вводе данных: \n" + analyzeLine);
        } else if (delWhitespaceInStr(analyzeLine).toString().equals("NIL"))
            setMessage("Информация не найдена");
        else
            setMessage("Ошибка во вводе данных: \n" + analyzeLine);

        if (COUNT == 0) {
            return messageBuilder;
        }
        else
            return new StringBuilder("\nОшибка в анализе строки, лишние данные: \n" + analyzeLine);
    }

    /**
     * Вызывается {@link #analyzeString(String)}
     * Расшифровывает полученную строку с информацией об уровне воды
     * <br>Например, если задать str=0336 то на выходе получится строка:
     * <br>Уровень воды над нулем поста: 336 см.
     *
     * @param str Строка из 4 цифровых символов. Обозначают уровень воды
     * @return если строка соответствует требованиям {@link #isCorrect(String)}, {@link #isNumber(String)}, и не содержит "//":
     * <br>Уровень воды над нулем поста: + waterLvl + см.,
     * <br>если содержит "//":
     * <br>Уровень воды над нулем поста: Данные не найдены,
     * <br>если не соответствует {@link #isCorrect(String)}, {@link #isNumber(String)}:
     * <br>Уровень воды над нулем поста: ошибка, неправильный формат данных
     */
    private String analyzeWaterLvl(String str) {

        StringBuilder text = new StringBuilder("Уровень воды над нулем поста: ");

        if (isCorrect(str)) {
            if (!str.contains("//")) {
                if (isNumber(str)) {
                    int waterLvl = 0;
                    if (str.charAt(0) == '5')
                        text.append("-");
                    int count = str.length() / 2;
                    for (int i = 1; i < str.length(); i++) {
                        //Избавляемся от нулей перед числом
                        waterLvl += (str.charAt(i) - 48) * Math.pow(10, count);
                        count--;
                    }
                    return text.toString() + waterLvl + " см.";
                }
                return text + anotherFormat + str;
            }
            return text + dataNotFound;
        }
        return text + anotherFormat + str;
    }

    /**
     * Вызывается {@link #analyzeString(String)}
     * Расшифровывает полученную строку с информацией об изменении уровня воды за 8 часов
     * Например, если задать str=0051 то на выходе получится строка:
     * Уровень воды за 8 часов повысился на: 5 см.
     *
     * @param str Строка из 4 цифровых символов. Первые три обозначают число на которое изменился уровень воды.
     *            Четвертая обозначает в какую сторону было изменение
     * @return если строка удовлетворяет требованиям {@link #isCorrect(String)}, {@link #isNumber(String)}, и не содержит "//":
     * <br>Уровень воды за 8 часов +
     * в зависимости от 4-й цифры: не изменился./повысился на:/понизился нa:
     * + первые три цифры получаемой строки
     * <br>если содержит "//":
     * <br>"Уровень воды за 8 часов " Данные не найдены,
     * <br>если не соответствует {@link #isCorrect(String)}, {@link #isNumber(String)}:
     * <br>"Уровень воды за 8 часов " ошибка, неправильный формат данных
     */
    private String analyzeChangeWaterLvl8h(String str) {
        String text = "Уровень воды за 8 часов ";

        if (isCorrect(str)) {
            if (!str.contains("//")) {
                if (isNumber(str)) {

                    int count = str.length() / 2;
                    String change = "";
                    int changeWaterLvl8h = 0;

                    switch (str.charAt(3)) {
                        case '0':
                            return text + "не изменился.";
                        case '1':
                            change = "повысился на: ";
                            break;
                        case '2':
                            change = "понизился нa: ";
                            break;
                    }

                    for (int i = 0; i < str.length(); i++) {
                        changeWaterLvl8h += (str.charAt(i) - 48) * Math.pow(10, count);
                        count--;
                    }
                    return text + change + changeWaterLvl8h + " см.";
                }
                return text + anotherFormat + str;
            }
            return text + dataNotFound;
        }
        return text + anotherFormat + str;
    }

    /**
     * Расшифровывает полученную строку с информацией об уровне воды за 20-часовой срок предшествующих суток
     * <br>Например, если задать str=0150 то на выходе получится строка:
     * <br>Уровень воды над нулем поста за 20-часовой срок наблюдений предшествующих суток: 150 см.
     *
     * @param str Строка из 4 цифровых символов.
     *            Обозначают уровень воды за 20-часовой срок предшествующих суток
     * @return если строка соответствует требованиям {@link #isCorrect(String)}, {@link #isNumber(String)}, и не содержит "//":
     * <br>Уровень воды над нулем поста за 20-часовой срок наблюдений предшествующих суток: + полученная строка
     * <br>если содержит "//":
     * <br>Уровень воды над нулем поста за 20-часовой срок наблюдений предшествующих суток: Данные не найдены,
     * <br>если не соответствует {@link #isCorrect(String)}, {@link #isNumber(String)}:
     * <br>Уровень воды над нулем поста за 20-часовой срок наблюдений предшествующих суток: ошибка, неправильный формат данных
     */
    private String analyzeWaterLvl20h(String str) {
        int count = str.length() / 2;
        int waterLvl20hAgo = 0;
        StringBuilder text = new StringBuilder("Уровень воды над нулем поста " +
                "\nза 20-часовой срок наблюдений" +
                "\nпредшествующих суток: ");

        if (isCorrect(str)) {
            if (!str.contains("//")) {
                if (isNumber(str)) {
                    if ((str.charAt(0) == '5'))
                        text.append("-");

                    for (int i = 1; i < str.length(); i++) {
                        waterLvl20hAgo += (str.charAt(i) - 48) * Math.pow(10, count);
                        count--;
                    }

                    return text.toString() + waterLvl20hAgo + " см.";
                }
                return text + anotherFormat + str;
            }
            return text + dataNotFound;
        }
        return text + anotherFormat + str;
    }


    /**
     * Вызывается {@link #analyzeString(String)}
     * Расшифровывает полученную строку с информацией о температуре воздуха и воды
     * <br>Например, если задать str=1714 то на выходе получится строка:
     * <br>Температура воды: 17 C.
     * <br>Температура воздуха: 14 C.
     *
     * @param str Строка из 4 цифровых символов. Первые два обозначают температуре воздуха,
     *            следующие два - температуру воды.
     * @return если строка соответствует требованиям {@link #isCorrect(String)}, {@link #isNumber(String)}, и не содержит "//":
     * <br>Температура воды: + первые два цифровых символа
     * Температура воздуха: + вторые два цифровых символа
     * <br>если одно из этих значений не выполняет требования{@link #isCorrect(String)}, {@link #isNumber(String)}:
     * <br>Температура воды: + ошибка, неправильный формат данных
     * или
     * Температура воздуха: + ошибка, неправильный формат данных
     * <br>если содержит "//":
     * <br>Уровень воды над нулем поста за 20-часовой срок наблюдений предшествующих суток: Данные не найдены.
     */
    private String analyzeTempWaterAir(String str) {
        int tempWater = 0;
        int tempAir = 0;
        StringBuilder text = new StringBuilder("Температура воды: ");
        String subStr = str.substring(0, 2);

        if (isCorrect(subStr)) {
            if (!subStr.contains("//")) {
                if (isNumber(subStr)) {
                    int count = subStr.length() / 2;
                    for (int i = 0; i < str.length() / 2; i++) {
                        tempWater += (str.charAt(i) - 48) * Math.pow(10, count);
                        count--;
                    }
                    text.append(tempWater + " C.");
                } else
                    text.append(anotherFormat + subStr);
            } else
                text.append(dataNotFound);
        } else
            text.append(anotherFormat + subStr);

        text.append("\nТемпература воздуха: ");

        subStr = str.substring(2, 4);


        if (isCorrect(subStr)) {
            if (!subStr.contains("//")) {
                if (isNumber(subStr)) {
                    int count = subStr.length() / 2;
                    for (int i = str.length() / 2; i < str.length(); i++) {
                        tempAir += (str.charAt(i) - 48) * Math.pow(10, count);
                        count--;
                    }
                    text.append(tempAir + " C.");
                } else
                    text.append(anotherFormat + subStr);
            } else
                text.append(dataNotFound);
        } else
            text.append(anotherFormat + subStr);

        return text.toString();
    }


    /**
     * Вызывается {@link #analyzeString(String)}
     * Расшифровывает полученную строку с информацией о ледяных явлениях и их интенсивности
     * <br>Например, если задать str=2202 то на выходе получится строка:
     * <br>Наблюдаемое ледяное явление: Осевший лед(на береговой мели после понижения уровня)
     * <br>Интенсивность наблюдаемого явления: 20%
     *
     * @param str Строка из 4 цифровых символов. Первые две - тип ледового явления,
     *            вторые две либо интенсивность ледового явления, либо тип ледового явления
     * @return если строка соответствует требованиям {@link #isCorrect(String)}, {@link #isNumber(String)}, и не содержит "//":
     * <br>Наблюдаемое ледяное явление: + тип ледового явления
     * Интенсивность наблюдаемого явления: + интенсивность
     * <br>если одно из этих значений не выполняет требования {@link #isCorrect(String)}, {@link #isNumber(String)}:
     * <br>значение типа ледового явления или интенсивности(в зависимости от того, где не выполняются) заменяется строкой:
     * <br>ошибка, неправильный формат данных
     * <br>если содержит "//":
     * <br>значение типа ледового явления или интенсивности(в зависимости от того, где не выполняются) заменяется строкой:
     * Данные не найдены
     */
    private String analyzeIceType(String str) {
        int type = 0;
        int intens = 0;

        StringBuilder text = new StringBuilder("Наблюдаемые ледяные явления: ");
        String subStr = str.substring(0, 2);

        if (isCorrect(subStr)) {
            if (!subStr.contains("//")) {
                if (isNumber(subStr)) {
                    int count = subStr.length() / 2;
                    for (int i = 0; i < str.length() / 2; i++) {
                        type += (str.charAt(i) - 48) * Math.pow(10, count);
                        count--;
                    }
                } else
                    text.append(anotherFormat + subStr);
            } else
                text.append(dataNotFound);
        } else
            text.append(anotherFormat + subStr);


        subStr = str.substring(2, 4);
        if (isCorrect(subStr)) {
            if (!subStr.contains("//")) {
                if (isNumber(subStr)) {
                    int count = subStr.length() / 2;
                    for (int i = str.length() / 2; i < str.length(); i++) {
                        intens += (str.charAt(i) - 48) * Math.pow(10, count);
                        count--;
                    }
                } else
                    text.append(anotherFormat + subStr);
            } else
                text.append(dataNotFound);
        } else
            text.append(anotherFormat + subStr);

        if (type > 10) {
            text.append(iceType[type - 11]);
            if (iceType[type - 11].equals("Неверный идентификатор"))
                return text.toString();

            if (intens > 10) {
                text.append("\nи " + iceType[intens - 11]);
            } else {
                text.append("\nИнтенсивность: " + intens * 10 + "%.");
            }
        } else
            return "Неверные данные по наблюдаемым ледяным явлениям";

        return text.toString();
    }


    /**
     * Вызывается {@link #analyzeString(String)}
     * Расшифровывает полученную строку с информацией о толщине льда и высоте снежного покрова
     * <br>Например, если задать str=0450 то на выходе получится строка:
     * <br>Толщина льда: 45 см.
     * <br>На льду снега нет.
     *
     * @param str Строка из 4 цифровых символов. Первые три обозначают толщину льда.
     *            Четвертый обозначает высоту снежного покрова
     * @return если строка соответствует требованиям {@link #isCorrect(String)}, {@link #isNumber(String)}, и не содержит "//":
     * <br>Толщина льда: + первые три цифровых символа
     * Высота снега на льду: + строка в зависимости от значения 4-го цифрового символа
     * если не выполняют:
     * <br>если одно из этих значений не выполняет требования {@link #isCorrect(String)}, {@link #isNumber(String)}:
     * <br>значение толщины льда или высоты снега на льду(в зависимости от того, где не выполняются) заменяется строкой:
     * <br>ошибка, неправильный формат данных
     * <br>если содержит "//":
     * <br>значение толщины льда или высоты снега на льду(в зависимости от того, где не выполняются) заменяется строкой:
     * Данные не найдены
     */
    private String analyzeIceDepth(String str) {
        int iceDepth = 0;
        StringBuilder text = new StringBuilder("Толщина льда: ");
        int snowHeight;
        String subStr = str.substring(0, 3);
        if (isCorrect(subStr)) {
            if (!subStr.contains("//")) {
                if (isNumber(subStr)) {
                    int count = str.length() / 2;
                    for (int i = 0; i < str.length() - 1; i++) {
                        iceDepth += (str.charAt(i) - 48) * Math.pow(10, count);
                        count--;
                    }
                    text.append(iceDepth + " см.");
                } else
                    text.append(anotherFormat + subStr);
            } else
                text.append(dataNotFound);
        } else
            text.append(anotherFormat + subStr);

        text.append("\nВысота снега на льду: ");

        if (isCorrect(subStr)) {
            if (!subStr.contains("/")) {
                if (isNumber(subStr)) {
                    snowHeight = str.charAt(3) - 48;
                    text.append(snowHeightMass[snowHeight]);
                } else
                    text.append(anotherFormat + subStr);
            } else
                text.append(dataNotFound);
        } else
            text.append(anotherFormat + subStr);

        return text.toString();
    }


    /**
     * Вызывается {@link #analyzeString(String)}
     * Расшифровывает полученную строку с информацией о ежедневном расходе воды
     * <br>Например, если задать str=2217 то на выходе получится строка:
     * <br>Ежедненый расход воды равен: 21.7 м3/с.
     *
     * @param str Строка из 4 цифровых символов. Первый обозначает количиство цифр в целой части расхода воды.
     *            Остальные три - обозначают расход воды.
     * @return если строка соответствует требованиям {@link #isCorrect(String)}, {@link #isNumber(String)}, и не содержит "//":
     * <br>Ежедненый расход воды равен: + значение расхода воды
     * <br>если одно из этих значений не выполняет требования {@link #isCorrect(String)}, {@link #isNumber(String)}:
     * <br>Ежедненый расход воды равен: ошибка, неправильный формат данных
     * <br>если содержит "//":
     * <br>Ежедненый расход воды равен: Данные не найдены
     */
    private String analyzeWaterConsumption(String str) {
        StringBuilder text = new StringBuilder("Ежедненый расход воды равен: ");

        if (isCorrect(str)) {
            if (!str.contains("//")) {
                if (isNumber(str)) {
                    int waterConsumption = 0;
                    double consumption;
                    int count = str.charAt(0) - 48;
                    int countWaterCons = str.length() / 2;
                    for (int i = 1; i < str.length(); i++) {
                        waterConsumption += (str.charAt(i) - 48) * Math.pow(10, countWaterCons);
                        countWaterCons--;
                    }
                    consumption = waterConsumption * Math.pow(10, (count)) / 1000;

                    text.append(consumption + " м3/с.");
                } else
                    text.append(anotherFormat + str);
            } else
                text.append(dataNotFound);
        } else
            text.append(anotherFormat + str);
        return text.toString();


    }


    /**
     * Вызывается {@link #analyzeString(String)}
     * Расшифровывает полученную строку с информацией о количестве и продолжительности осадков за сутки
     * <br>Например, если задать str=0010 то на выходе получится строка:
     * <br>Количество выпавших осадков: 1.0 мм.
     * <br>Продолжительность выпадения осадков: менее 1 ч.
     *
     * @param str Строка из 4 цифровых символов. Первые три обозначают количество осадков
     *            Четвертый обозначает продолжительность осадков
     * @return если строка соответствует требованиям {@link #isCorrect(String)}, {@link #isNumber(String)}, и не содержит "//":
     * Количество выпавших осадков: + первые три цифры
     * Продолжительность выпадения осадков: + в зависимости от значения 4 цифрового символа,
     * менее 1 ч./от 1 до 3 ч./от 3 до 6 ч./от 6 до 12 ч./более 12 ч.
     * <br>если одно из этих значений не выполняет требования {@link #isCorrect(String)}, {@link #isNumber(String)}:
     * <br>значение количество осадков или продолжительность осадков(в зависимости от того, где не выполняются) заменяется строкой:
     * ошибка, неправильный формат данных
     * <br>если содержит "//":
     * <br>значение количество осадков или продолжительность осадков(в зависимости от того, где не выполняются) заменяется строкой:
     * Данные не найдены
     */
    private String analyzePrecipitation(String str) {
        StringBuilder text = new StringBuilder("Количество выпавших осадков: ");
        float countPrecipitation = 0;
        String subStr = str.substring(0, 3);
        if (isCorrect(subStr)) {
            if (!subStr.contains("//")) {
                if (isNumber(subStr)) {
                    int count = subStr.length() / 2;
                    for (int i = 0; i < 3; i++) {
                        countPrecipitation += (str.charAt(i) - 48) * Math.pow(10, count);
                        count--;
                    }

                    if (countPrecipitation > 990)
                        countPrecipitation = (countPrecipitation % 10) / 10;

                    text.append(countPrecipitation + " мм.");
                } else
                    text.append(anotherFormat + subStr);
            } else
                text.append(dataNotFound);
        } else
            text.append(anotherFormat + subStr);

        text.append("\nПродолжительность выпадения осадков: ");

        subStr = str.substring(3);
        if (isCorrect(subStr)) {
            if (!subStr.contains("/")) {
                if (isNumber(subStr)) {
                    if ((str.charAt(3) - 48) < precipitation.length)
                        text.append(precipitation[str.charAt(3) - 48]);
                    else
                        text.append(anotherFormat + str + ", символ " + str.charAt(3));
                } else
                    text.append(anotherFormat + str + ", символ " + str.charAt(3));
            } else
                text.append(dataNotFound);
        } else
            text.append(anotherFormat + str + ", символ " + str.charAt(3));

        return text.toString();
    }

    /**
     * Добавляет к строке message деятельности класса заданное сообщение
     * <br>Например, если {@link #messageBuilder}  = 111 задать str=0051 то на выходе получится строка:
     * 1110051
     *
     * @param str Любая строка
     */
    private void setMessage(StringBuilder str) {
        messageBuilder.append(str + "\n");
    }

    /**
     * Перегружает метод {@link #setMessage(StringBuilder)} для использования строки String в качестве параметра
     *
     * @param str Любая строка
     */
    private void setMessage(String str) {
        messageBuilder.append(str + "\n");
    }

    /**
     * Проверяет, имеются ли в строке недопустимые символы
     * <br>Например, при string = 11111 метод вернет значение true,
     * а при string = 1A111 метод вернет false
     *
     * @param string любая строка
     * @return true, если в строке нет недопустимых символов
     * и false, если таковые имеются
     */
    private static boolean isCorrect(String string) {
        for (int i = 0; i < string.length(); i++) {
            if (!((string.charAt(i) > 46) && (string.charAt(i) < 58)))
                return false;

        }
        return true;
    }

    /**
     * Проверяет, состоит ли строка полностью из цифровых символов
     * <br>Например, при string = 11111 метод вернет значение true,
     * а при string = 1A111 метод вернет false
     *
     * @param string любая строка
     * @return true, если в строке имеются только цифровые символы
     * и false, если имеются другие символы
     */

    private static boolean isNumber(String string) {
        try {
            Long.parseLong(string);
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    /**
     * Сохраняет файл с датой и с содержимым переменной {@link #messageBuilder} в указанное место
     *
     * @param fileOutput Путь к указанному месту
     */
    void saveFile(String fileOutput) {
        try (FileWriter writer = new FileWriter(fileOutput + ".txt", false)) {
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            String data = format.format(new Date());

            for (int i = 0; i < data.length(); i++)
                writer.write(data.charAt(i));
            writer.write('\n');
            for (int i = 0; i < messageBuilder.length(); i++)
                writer.write(messageBuilder.charAt(i));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Стирает пробелы в начале строки
     * <br>Например, если str = " 123", то метод вернет "123"
     *
     * @param str любая строка
     * @return отформатированная строка
     */
    private StringBuilder delWhitespace(StringBuilder str) {
        do {
            if (Character.isWhitespace(str.charAt(0)))
                str.deleteCharAt(0);

        } while (Character.isWhitespace(str.charAt(0)));
        return str;
    }

    /**
     * Перегружает метод {@link #delWhitespace(StringBuilder)} для использования строки String в качестве параметра
     *
     * @param str любая строка
     * @return отформатированная строка
     */
    private String delWhitespace(String str) {
        do {
            if (Character.isWhitespace(str.charAt(0)))
                str = str.substring(1);

        } while (Character.isWhitespace(str.charAt(0)));
        return str;
    }

    /**
     * Стирает все полбелы, табуляцию, перенос строки в строке.
     * Например, если str="\n123  \r3", то метод вернет "1233"
     *
     * @param str любая строка
     * @return отформатированная строка
     */
    private StringBuilder delWhitespaceInStr(StringBuilder str) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i)))
                builder.append(str.charAt(i));
        }
        return builder;
    }

}
