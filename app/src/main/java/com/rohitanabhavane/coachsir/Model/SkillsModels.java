package com.rohitanabhavane.coachsir.Model;

public class SkillsModels {
    String SkillID, Skill;

    public SkillsModels() {

    }

    public SkillsModels(String skillID, String skill) {
        SkillID = skillID;
        Skill = skill;
    }

    public String getSkillID() {
        return SkillID;
    }

    public void setSkillID(String skillID) {
        SkillID = skillID;
    }

    public String getSkill() {
        return Skill;
    }

    public void setSkill(String skill) {
        Skill = skill;
    }
}
