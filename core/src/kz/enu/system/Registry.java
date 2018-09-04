package kz.enu.system;


import com.badlogic.gdx.graphics.Color;

/**
 * Created by SLUX on 10.06.2017.
 */

public class Registry {
    public static String[] skinsList = {"taha", "space", "empire", "slime", "primal", "aqua", "minimal", "social"};
    public static String[][] SKINS_NAME = {{"CLASSIC", "SPACE", "EMPIRE", "SLIME", "PRIMAL", "AQUA", "MINIMAL", "SOCIAL"},
                                            {"КЛАССИКА", "КОСМОС", "ИМПЕРИЯ", "ТОКСИЧНЫЙ", "ДОИСТОРИЧЕСКИЙ", "АКВА", "МИНИМАЛЬНЫЙ", "СОЦИУМ"},
                                            {"КЛАССИКА", "ҒАРЫШ", "ИМПЕРИЯ", "ТОКСИКАЛЫҚ", "ЕЖЕЛГІ", "АКВА", "МИНИМАЛДЫ", "СОЦИУМ"}};
    public static final int SINGLE_PLAYER = 0;
    public static final int MULTIPLAYER = 1;
    public static final int INTERNET = 2;
    public static final int TUTORIAL = 3;
    public static final int CREATE = 0;
    public static final int CONNECT = 1;
    //Mutable
    //Board
    public static String BACKGROUND = "bg_";
    public static String SLOT = "slot_";
    public static String STONE_BANK = "stonebank_";
    public static String STONE = "stone_";
    public static String TUZDYK = "tuzdyk_";
    public static String TURN_UP = "turn_up_";
    public static String TURN_DOWN = "turn_down_";

    //Menu
    public static String MAIN_MENU = "mainmenu_";
    public static String WRAPPER = "wrapper_";

    //Funcional button
    public static String UNDO = "undo_";
    public static String HOME = "home_";
    public static String SOUND = "sound_";
    public static String SOUND_OFF = "soundoff_";

    //Sound
    public static String EMPTY_MOVE = "emptymove_";
    public static String TUZDYK_SOUND = "tuzdyk_";

    //Music
    public static String MUSIC = "music_";


    //Immutable
    //Board
    public static String GLOW = "outglow.png";

    //Menu
    public static String EXAMPLE_OUTGLOW = "example_outglow.png";
    public static String BLACK_BG = "black_bg.png";

    //Sound
    public static String WIN = "win.mp3";
    public static String BUTTON = "button.mp3";
    public static String JACKPOT = "jackpot.mp3";
    public static String EFFECTIVE_MOVE = "effective.mp3";
    public static String ERROR = "error.mp3";


    public static final String[][] DICTIONARY = {
            //0               1              2           3            4           5           6            7              8        9         10           11       12              13              14         15               16        17      18          19          20       21                        22           23                          24             25
            {"SINGLE PLAYER", "MULTIPLAYER", "SETTINGS", "NEW GAME", "CONTINUE", "GAMEOVER", "Language:", "Difficulty:", "Level", "YOU WIN", "YOU LOSE", "DRAW", "PLAYER 1 WIN", "PLAYER 2 WIN", "LOADING", "THEME SETTINGS", "Music:", "BACK", "TRAINING", "INTERNET", "LOCAL", "WAITING FOR CONNECTION", "YOUR ID:", "Insert the opponent's ID", "CREATE GAME", "CONNECT TO GAME"},
            {"ОДИНОЧНАЯ ИГРА", "ДВА ИГРОКА", "НАСТРОЙКИ", "НОВАЯ ИГРА", "ПРОДОЛЖИТЬ", "КОНЕЦ", "Язык:", "Сложность:", "Уровень", "ВЫ ПОБЕДИЛИ", "ВЫ ПРОИГРАЛИ", "НИЧЬЯ", "ИГРОК 1 ПОБЕДИЛ", "ИГРОК 2 ПОБЕДИЛ", "ЗАГРУЗКА", "ВЫБОР ТЕМЫ", "Музыка:", "НАЗАД", "ОБУЧЕНИЕ", "ИНТЕРНЕТ", "ЛОКАЛЬНАЯ", "ЖДЕМ СОЕДИНЕНИЯ", "ВАШ ID:", "Введите ID противника", "СОЗДАТЬ ИГРУ", "ПРИСОДЕНИТЬСЯ"},
            {"БІР ОЙЫНШЫ", "ЕКІ ОЙЫНШЫ", "БАПТАУ", "ЖАҢА ОЙЫН", "ЖАЛҒАСТЫРУ", "ОЙЫН АЯҚТАЛДЫ", "Тіл:", "Қиындық:", "Денгей", "СІЗ ҰТТЫҢЫЗ", "СІЗ ҰТЫЛДЫҢЫЗ", "ТЕҢ ОЙЫН", "1-ІНШІ ОЙЫНШЫ ЖЕҢДІ", "2-ІНШІ ОЙЫНШЫ ЖЕҢДІ", "ЖҮКТЕУ", "ТАҚЫРЫП ТАҢДАУ", "Музыка:", "ҚАЙТУ", "ҮЙРЕНУ", "ИНТЕРНЕТ", "ЛОКАЛДЫ", "ҚОСЫЛЫМДЫ КҮТУДЕМІЗ", "СІЗДІҢ ID:", "Қарсыластын ID-ін енгізіңіз", "ОЙЫНДЫ ҚҰРУ", "ҚОСЫЛУ"}};
    public static final String[] LANGUAGES = {"English", "Русский", "Қазақша"};
    public static final float[] LANGUAGE_WIDTH = {198f, 220f, 235f};
    public static final float[] LEVEL_OFFSET = {40f, 0f, 20f};
    public static final String RIGTH_ARROW = "►";
    public static final String LEFT_ARROW = "◄";

    // Color initialization
    public static final Color[] COLORS = {new Color(1f, 0.8f, 0.53f, 1f), new Color(0f, 1f, 0.99f, 1f), new Color(1f, 1f, 1f, 1f), new Color(0.1f, 1f, 0.1f, 1f), new Color(1f, 0.8f, 0f, 1f), new Color(0f, 0.153f, 0.173f, 1f), new Color(0.623f, 0.623f, 0.623f, 1f), new Color(1f, 1f, 1f, 1f)};
    public static final Color[] INDEX_COLORS = {new Color(1f, 0.8f, 0.53f, 1f), new Color(0f, 1f, 0.99f, 1f), new Color(1f, 0f, 0f, 1f), new Color(0.1f, 1f, 0.1f, 1f), new Color(1f, 0.8f, 0f, 1f), new Color(0f, 0.153f, 0.173f, 1f), new Color(0.2f, 0.2f, 0.2f, 1f), new Color(0.35f, 0.6f, 1f, 1f)};
    public static final Color[] NUMBER_COLORS = {new Color(1f, 1f, 1f, 1f), new Color(1f, 1f, 1f, 1f), new Color(1f, 1f, 1f, 1f), new Color(1f, 1f, 1f, 1f), new Color(1f, 1f, 1f, 1f), new Color(0.6f, 0.2f, 0f, 1f), new Color(0.623f, 0.623f, 0.623f, 1f), new Color(1f, 1f, 1f, 1f)};
    public static final Color[] LOADING_COLORS = {new Color(1f, 0.8f, 0.53f, 1f), new Color(0f, 1f, 0.99f, 1f), new Color(1f, 0f, 0f, 1f), new Color(0.1f, 1f, 0.1f, 1f), new Color(1f, 0.8f, 0f, 1f), new Color(0f, 0.9f, 1f, 1f), new Color(0.623f, 0.623f, 0.623f, 1f), new Color(1f, 1f, 1f, 1f)};
}
