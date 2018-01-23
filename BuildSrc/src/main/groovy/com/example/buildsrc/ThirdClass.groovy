package com.example.buildsrc

import org.gradle.api.Plugin
import org.gradle.api.Project;

/**
 * Created by majun on 18/1/21.
 */

public class ThirdClass implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.android.registerTransform(new MyTransform(project))
        System.out.println("===============================试一下=============================");
    }
}
