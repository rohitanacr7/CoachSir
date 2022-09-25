package com.rohitanabhavane.coachsir.Model;

public class TrainingTimeModel {
    String TrainingTimeID, TrainingTimeFrom, TrainingTimeAMPM1, TrainingTimeTo, TrainingTimeAMPM2;

    public TrainingTimeModel() {

    }

    public TrainingTimeModel(String trainingTimeID, String trainingTimeFrom, String trainingTimeAMPM1, String trainingTimeTo, String trainingTimeAMPM2) {
        TrainingTimeID = trainingTimeID;
        TrainingTimeFrom = trainingTimeFrom;
        TrainingTimeAMPM1 = trainingTimeAMPM1;
        TrainingTimeTo = trainingTimeTo;
        TrainingTimeAMPM2 = trainingTimeAMPM2;
    }

    public String getTrainingTimeID() {
        return TrainingTimeID;
    }

    public void setTrainingTimeID(String trainingTimeID) {
        TrainingTimeID = trainingTimeID;
    }

    public String getTrainingTimeFrom() {
        return TrainingTimeFrom;
    }

    public void setTrainingTimeFrom(String trainingTimeFrom) {
        TrainingTimeFrom = trainingTimeFrom;
    }

    public String getTrainingTimeAMPM1() {
        return TrainingTimeAMPM1;
    }

    public void setTrainingTimeAMPM1(String trainingTimeAMPM1) {
        TrainingTimeAMPM1 = trainingTimeAMPM1;
    }

    public String getTrainingTimeTo() {
        return TrainingTimeTo;
    }

    public void setTrainingTimeTo(String trainingTimeTo) {
        TrainingTimeTo = trainingTimeTo;
    }

    public String getTrainingTimeAMPM2() {
        return TrainingTimeAMPM2;
    }

    public void setTrainingTimeAMPM2(String trainingTimeAMPM2) {
        TrainingTimeAMPM2 = trainingTimeAMPM2;
    }
}
