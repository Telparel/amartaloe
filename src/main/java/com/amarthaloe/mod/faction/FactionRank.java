package com.amarthaloe.mod.faction;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class FactionRank {
    public static final int[] TIERS = {-1000, -500, -100, -50, -10, 0, 10, 50, 100, 500, 1000};

    public static int getTierIndex(int rep) {
        if (rep >= 1000) return 5;
        if (rep >= 500)  return 4;
        if (rep >= 100)  return 3;
        if (rep >= 50)   return 2;
        if (rep >= 10)   return 1;
        if (rep > -10)   return 0;
        if (rep > -50)   return -1;
        if (rep > -100)  return -2;
        if (rep > -500)  return -3;
        if (rep > -1000) return -4;
        return -5;
    }

    public static Component getTitle(Faction faction, int rep) {
        int tier = getTierIndex(rep);
        String titleStr = switch (faction) {
            case ISONOMIA -> switch (tier) { // Либра: Контракты, честный обмен, закон
                case 5 -> "Гарант Равновесия";
                case 4 -> "Доверенный Исполнитель";
                case 3 -> "Партнёр Контракта";
                case 2 -> "Сторона Сделки";
                case 1 -> "Вноситель Залога";
                case 0 -> "Свободное Лицо";
                case -1 -> "Ненадёжный Должник";
                case -2 -> "Нарушитель Условий";
                case -3 -> "Неустойщик";
                case -4 -> "Злостный Неплательщик";
                default -> "Клятвопреступник";
            };
            case GLADIUS -> switch (tier) { // Гладиус: Война, армия, пыл битвы
                case 5 -> "Авангард Войны";
                case 4 -> "Побратим Клинка";
                case 3 -> "Ветеран Сечи";
                case 2 -> "Рядовой Строй";
                case 1 -> "Новобранец";
                case 0 -> "Безоружный";
                case -1 -> "Трус";
                case -2 -> "Дезертир";
                case -3 -> "Вражеский Лазутчик";
                case -4 -> "Предатель Шеренги";
                default -> "Смертная Мишень";
            };
            case ADEL -> switch (tier) { // Адэль: Обжорство, капризность, жажда обладать
                case 5 -> "Услада Взора";
                case 4 -> "Избранное Блюдо";
                case 3 -> "Подносчик Даров";
                case 2 -> "Угодник Каприза";
                case 1 -> "Покорный Слуга";
                case 0 -> "Чужой Аппетиту";
                case -1 -> "Скупой Нищий";
                case -2 -> "Отказчик";
                case -3 -> "Гнилой Кусок";
                case -4 -> "Осквернитель Трапезы";
                default -> "Враг Утехи";
            };
            case GNOSTERA -> switch (tier) { // Гностер: Слияние, опыты, сосуд для тьмы
                case 5 -> "Идеальный Сосуд";
                case 4 -> "Гармоничный Симбиот";
                case 3 -> "Удачный Образец";
                case 2 -> "Ассистент Опытов";
                case 1 -> "Одобренный Материал";
                case 0 -> "Неприкосновенный Орган";
                case -1 -> "Бракованный Образец";
                case -2 -> "Субъект Препарирования";
                case -3 -> "Отторгнутая Плоть";
                case -4 -> "Гнойный Нарост";
                default -> "Мусор Вивисекции";
            };
            case MARIS -> switch (tier) { // Марис: Тишина, усыпление, безмолвие
                case 5 -> "Глас Безмолвия";
                case 4 -> "Хранитель Покоя";
                case 3 -> "Убаюканный";
                case 2 -> "Шепчущий Воде";
                case 1 -> "Смиренный Гость";
                case 0 -> "Чуждый Глубине";
                case -1 -> "Нарушитель Безмолвия";
                case -2 -> "Шумливый Смутьян";
                case -3 -> "Всплеск Тревоги";
                case -4 -> "Вопиющий Святотатец";
                default -> "Вечный Гул";
            };
            case FULGORT -> switch (tier) { // Фульгорт: Завоевание, тщеславие, слава
                case 5 -> "Соратник Триумфатора";
                case 4 -> "Герольд Величия";
                case 3 -> "Увенчанный Лаврами";
                case 2 -> "Льстец Чертога";
                case 1 -> "Свидетель Триумфа";
                case 0 -> "Серая Чернь";
                case -1 -> "Дерзкий Критик";
                case -2 -> "Упрямый Невежда";
                case -3 -> "Жалкое Ничтожество";
                case -4 -> "Попранный В Пыли";
                default -> "Прах Завоевания";
            };
            case CALIGO -> switch (tier) { // Калиго: Печаль, холод, страж перед Нахтом
                case 5 -> "Хранитель Вечной Скорби";
                case 4 -> "Спутник Забвения";
                case 3 -> "Застывший В Инее";
                case 2 -> "Разделивший Тоску";
                case 1 -> "Путник Пустошей";
                case 0 -> "Тень На Снегу";
                case -1 -> "Беспечный Чужак";
                case -2 -> "Осквернитель Покоя";
                case -3 -> "Пробудитель Гнева";
                case -4 -> "Ледяное Проклятие";
                default -> "Жертва Нахта";
            };
        };

        ChatFormatting color = switch (tier) {
            case 5 -> ChatFormatting.GOLD;
            case 4 -> ChatFormatting.YELLOW;
            case 3 -> ChatFormatting.GREEN;
            case 2 -> ChatFormatting.DARK_GREEN;
            case 1 -> ChatFormatting.AQUA;
            case 0 -> ChatFormatting.GRAY;
            case -1 -> ChatFormatting.DARK_GRAY;
            case -2 -> ChatFormatting.RED;
            case -3 -> ChatFormatting.DARK_RED;
            case -4 -> ChatFormatting.DARK_RED;
            default -> ChatFormatting.DARK_PURPLE;
        };

        boolean bold = (tier == 5 || tier <= -4);
            if (bold) {
                return Component.literal(titleStr).withStyle(color, ChatFormatting.BOLD);
            }
            return Component.literal(titleStr).withStyle(color);
    }

    public static int getNextTierTarget(int rep) {
        if (rep >= 1000) return 1000;
        if (rep >= 500)  return 1000;
        if (rep >= 100)  return 500;
        if (rep >= 50)   return 100;
        if (rep >= 10)   return 50;
        if (rep >= 0)    return 10;
        if (rep >= -10)  return 0;
        if (rep >= -50)  return -10;
        if (rep >= -100) return -50;
        if (rep >= -500) return -100;
        return -500;
    }

    public static int getPrevTierTarget(int rep) {
        if (rep >= 1000) return 500;
        if (rep >= 500)  return 100;
        if (rep >= 100)  return 50;
        if (rep >= 50)   return 10;
        if (rep >= 10)   return 0;
        if (rep >= 0)    return -10;
        if (rep >= -10)  return -50;
        if (rep >= -50)  return -100;
        if (rep >= -100) return -500;
        return -1000;
    }
}
