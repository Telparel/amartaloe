package com.amarthaloe.mod.client;

public class ModClientConfig {
    public enum HudPosition {
        TOP_LEFT("Слева сверху"),
        BOTTOM_LEFT("Слева снизу"),
        TOP_RIGHT("Справа сверху"),
        BOTTOM_RIGHT("Справа снизу");

        private final String display;
        HudPosition(String display) { this.display = display; }
        public String getDisplay() { return display; }
    }

    public static HudPosition partyHudPosition = HudPosition.TOP_LEFT;

    public static void cyclePartyHudPosition() {
        HudPosition[] values = HudPosition.values();
        partyHudPosition = values[(partyHudPosition.ordinal() + 1) % values.length];
    }
}
