package controllers;

import ai.Config;

public interface TrainingCompletionListener {
    void onTrainingComplete(Config config);
}
