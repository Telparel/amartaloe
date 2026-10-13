package com.amarthaloe.mod.faction;

import com.amarthaloe.mod.profile.PlayerProfileData;
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
        return getTitle(faction, rep, PlayerProfileData.Gender.MALE);
    }

    public static Component getTitle(Faction faction, int rep, PlayerProfileData.Gender gender) {
        int tier = getTierIndex(rep);
        boolean female = gender == PlayerProfileData.Gender.FEMALE;

        String titleStr = switch (faction) {
            case ISONOMIA -> switch (tier) {
                case 5 -> female ? "Гарант Равновесия" : "Гарант Равновесия";
                case 4 -> female ? "Доверенная Исполнительница" : "Доверенный Исполнитель";
                case 3 -> female ? "Партнёрша Контракта" : "Партнёр Контракта";
                case 2 -> female ? "Сторона Сделки" : "Сторона Сделки";
                case 1 -> female ? "Вносительница Залога" : "Вноситель Залога";
                case 0 -> female ? "Свободное Лицо" : "Свободное Лицо";
                case -1 -> female ? "Ненадёжная Должница" : "Ненадёжный Должник";
                case -2 -> female ? "Нарушительница Условий" : "Нарушитель Условий";
                case -3 -> female ? "Неустойщица" : "Неустойщик";
                case -4 -> female ? "Злостная Неплательщица" : "Злостный Неплательщик";
                default -> female ? "Клятвопреступница" : "Клятвопреступник";
            };
            case GLADIUS -> switch (tier) {
                case 5 -> female ? "Авангард Войны" : "Авангард Войны";
                case 4 -> female ? "Посестра Клинка" : "Побратим Клинка";
                case 3 -> female ? "Ветеран Сечи" : "Ветеран Сечи";
                case 2 -> female ? "Рядовой Воин" : "Рядовой Строй";
                case 1 -> female ? "Новобранец" : "Новобранец";
                case 0 -> female ? "Безоружная" : "Безоружный";
                case -1 -> female ? "Трусиха" : "Трус";
                case -2 -> female ? "Дезертирка" : "Дезертир";
                case -3 -> female ? "Вражеская Лазутчица" : "Вражеский Лазутчик";
                case -4 -> female ? "Предательница Шеренги" : "Предатель Шеренги";
                default -> female ? "Смертная Мишень" : "Смертная Мишень";
            };
            case ADEL -> switch (tier) {
                case 5 -> female ? "Услада Взора" : "Услада Взора";
                case 4 -> female ? "Избранное Блюдо" : "Избранное Блюдо";
                case 3 -> female ? "Подносчица Даров" : "Подносчик Даров";
                case 2 -> female ? "Угодница Каприза" : "Угодник Каприза";
                case 1 -> female ? "Покорная Служанка" : "Покорный Слуга";
                case 0 -> female ? "Чуждая Аппетиту" : "Чужой Аппетиту";
                case -1 -> female ? "Скупая Нищая" : "Скупой Нищий";
                case -2 -> female ? "Отказчица" : "Отказчик";
                case -3 -> female ? "Гнилой Кусок" : "Гнилой Кусок";
                case -4 -> female ? "Осквернительница Трапезы" : "Осквернитель Трапезы";
                default -> female ? "Враг Утехи" : "Враг Утехи";
            };
            case GNOSTERA -> switch (tier) {
                case 5 -> female ? "Идеальный Сосуд" : "Идеальный Сосуд";
                case 4 -> female ? "Гармоничный Симбиот" : "Гармоничный Симбиот";
                case 3 -> female ? "Удачный Образец" : "Удачный Образец";
                case 2 -> female ? "Ассистентка Опытов" : "Ассистент Опытов";
                case 1 -> female ? "Одобренный Материал" : "Одобренный Материал";
                case 0 -> female ? "Неприкосновенный Орган" : "Неприкосновенный Орган";
                case -1 -> female ? "Бракованный Образец" : "Бракованный Образец";
                case -2 -> female ? "Субъект Препарирования" : "Субъект Препарирования";
                case -3 -> female ? "Отторгнутая Плоть" : "Отторгнутая Плоть";
                case -4 -> female ? "Гнойный Нарост" : "Гнойный Нарост";
                default -> female ? "Мусор Вивисекции" : "Мусор Вивисекции";
            };
            case MARIS -> switch (tier) {
                case 5 -> female ? "Глас Безмолвия" : "Глас Безмолвия";
                case 4 -> female ? "Хранительница Покоя" : "Хранитель Покоя";
                case 3 -> female ? "Убаюканная" : "Убаюканный";
                case 2 -> female ? "Шепчущая Воде" : "Шепчущий Воде";
                case 1 -> female ? "Смиренная Гостья" : "Смиренный Гость";
                case 0 -> female ? "Чуждая Глубине" : "Чуждый Глубине";
                case -1 -> female ? "Нарушительница Безмолвия" : "Нарушитель Безмолвия";
                case -2 -> female ? "Шумливая Смутьянка" : "Шумливый Смутьян";
                case -3 -> female ? "Всплеск Тревоги" : "Всплеск Тревоги";
                case -4 -> female ? "Вопиющая Святотатица" : "Вопиющий Святотатец";
                default -> female ? "Вечный Гул" : "Вечный Гул";
            };
            case FULGORT -> switch (tier) {
                case 5 -> female ? "Соратница Триумфатора" : "Соратник Триумфатора";
                case 4 -> female ? "Герольд Величия" : "Герольд Величия";
                case 3 -> female ? "Увенчанная Лаврами" : "Увенчанный Лаврами";
                case 2 -> female ? "Льстица Чертога" : "Льстец Чертога";
                case 1 -> female ? "Свидетельница Триумфа" : "Свидетель Триумфа";
                case 0 -> female ? "Серая Чернь" : "Серая Чернь";
                case -1 -> female ? "Дерзкая Критикесса" : "Дерзкий Критик";
                case -2 -> female ? "Упрямая Невежда" : "Упрямый Невежда";
                case -3 -> female ? "Жалкое Ничтожество" : "Жалкое Ничтожество";
                case -4 -> female ? "Попранная В Пыли" : "Попранный В Пыли";
                default -> female ? "Прах Завоевания" : "Прах Завоевания";
            };
            case CALIGO -> switch (tier) {
                case 5 -> female ? "Хранительница Вечной Скорби" : "Хранитель Вечной Скорби";
                case 4 -> female ? "Спутница Забвения" : "Спутник Забвения";
                case 3 -> female ? "Застывшая В Инее" : "Застывший В Инее";
                case 2 -> female ? "Разделившая Тоску" : "Разделивший Тоску";
                case 1 -> female ? "Путница Пустошей" : "Путник Пустошей";
                case 0 -> female ? "Тень На Снегу" : "Тень На Снегу";
                case -1 -> female ? "Беспечная Чужачка" : "Беспечный Чужак";
                case -2 -> female ? "Осквернительница Покоя" : "Осквернитель Покоя";
                case -3 -> female ? "Пробудительница Гнева" : "Пробудитель Гнева";
                case -4 -> female ? "Ледяное Проклятие" : "Ледяное Проклятие";
                default -> female ? "Жертва Нахта" : "Жертва Нахта";
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
