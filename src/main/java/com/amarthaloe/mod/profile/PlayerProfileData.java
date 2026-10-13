package com.amarthaloe.mod.profile;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class PlayerProfileData implements INBTSerializable<CompoundTag> {
    public enum Gender {
        MALE("Мужской"),
        FEMALE("Женский");

        private final String display;
        Gender(String display) { this.display = display; }
        public String getDisplay() { return display; }
    }

    private String rpName = "";
    private Gender gender = Gender.MALE;
    private String selectedTitle = "";
    private String heightWeight = "175 см / 70 кг";
    private String build = "Среднее";
    private String hair = "Тёмные";
    private String eyes = "Карие";
    private String distinctiveFeatures = "Шрам через бровь";
    private String backstory = "Один из выживших на руинах старого мира...";
    private boolean hasSeenTutorial = false;

    public PlayerProfileData() {}

    public String getRpName() { return rpName; }
    public void setRpName(String rpName) { this.rpName = rpName; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public String getSelectedTitle() { return selectedTitle; }
    public void setSelectedTitle(String selectedTitle) { this.selectedTitle = selectedTitle; }

    public String getHeightWeight() { return heightWeight; }
    public void setHeightWeight(String heightWeight) { this.heightWeight = heightWeight; }

    public String getBuild() { return build; }
    public void setBuild(String build) { this.build = build; }

    public String getHair() { return hair; }
    public void setHair(String hair) { this.hair = hair; }

    public String getEyes() { return eyes; }
    public void setEyes(String eyes) { this.eyes = eyes; }

    public String getDistinctiveFeatures() { return distinctiveFeatures; }
    public void setDistinctiveFeatures(String distinctiveFeatures) { this.distinctiveFeatures = distinctiveFeatures; }

    public String getBackstory() { return backstory; }
    public void setBackstory(String backstory) { this.backstory = backstory; }

    public boolean hasSeenTutorial() { return hasSeenTutorial; }
    public void setHasSeenTutorial(boolean hasSeenTutorial) { this.hasSeenTutorial = hasSeenTutorial; }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putString("rpName", rpName);
        tag.putString("gender", gender.name());
        tag.putString("selectedTitle", selectedTitle);
        tag.putString("heightWeight", heightWeight);
        tag.putString("build", build);
        tag.putString("hair", hair);
        tag.putString("eyes", eyes);
        tag.putString("distinctiveFeatures", distinctiveFeatures);
        tag.putString("backstory", backstory);
        tag.putBoolean("hasSeenTutorial", hasSeenTutorial);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        if (tag.contains("rpName")) rpName = tag.getString("rpName");
        if (tag.contains("gender")) {
            try { gender = Gender.valueOf(tag.getString("gender")); } catch (Exception ignored) {}
        }
        if (tag.contains("selectedTitle")) selectedTitle = tag.getString("selectedTitle");
        if (tag.contains("heightWeight")) heightWeight = tag.getString("heightWeight");
        if (tag.contains("build")) build = tag.getString("build");
        if (tag.contains("hair")) hair = tag.getString("hair");
        if (tag.contains("eyes")) eyes = tag.getString("eyes");
        if (tag.contains("distinctiveFeatures")) distinctiveFeatures = tag.getString("distinctiveFeatures");
        if (tag.contains("backstory")) backstory = tag.getString("backstory");
        if (tag.contains("hasSeenTutorial")) hasSeenTutorial = tag.getBoolean("hasSeenTutorial");
    }
}
