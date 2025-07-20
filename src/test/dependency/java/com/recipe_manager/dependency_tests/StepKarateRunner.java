package com.recipe_manager.dependency_tests;

import com.intuit.karate.junit5.Karate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;

@Tag("dependency")
class StepKarateRunner {
  @Karate.Test
  @DisplayName("Get Steps Endpoint")
  Karate testGetSteps() {
    return Karate.run("feature/step/get-steps.feature").relativeTo(getClass());
  }

  @Karate.Test
  @DisplayName("Add Step Comment Endpoint")
  Karate testAddStepComment() {
    return Karate.run("feature/step/add-step-comment.feature").relativeTo(getClass());
  }

  @Karate.Test
  @DisplayName("Edit Step Comment Endpoint")
  Karate testEditStepComment() {
    return Karate.run("feature/step/edit-step-comment.feature").relativeTo(getClass());
  }

  @Karate.Test
  @DisplayName("Delete Step Comment Endpoint")
  Karate testDeleteStepComment() {
    return Karate.run("feature/step/delete-step-comment.feature").relativeTo(getClass());
  }

  @Karate.Test
  @DisplayName("Add Step Media Endpoint")
  Karate testAddStepMedia() {
    return Karate.run("feature/step/add-step-media.feature").relativeTo(getClass());
  }

  @Karate.Test
  @DisplayName("Update Step Media Endpoint")
  Karate testUpdateStepMedia() {
    return Karate.run("feature/step/update-step-media.feature").relativeTo(getClass());
  }

  @Karate.Test
  @DisplayName("Delete Step Media Endpoint")
  Karate testDeleteStepMedia() {
    return Karate.run("feature/step/delete-step-media.feature").relativeTo(getClass());
  }
}
