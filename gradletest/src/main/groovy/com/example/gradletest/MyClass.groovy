package com.example.gradletest;

import org.gradle.api.DefaultTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * @author majun
 */
public class MyClass implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        System.out.print("hello,world\n");
        System.out.print("hello,majun\n");
        project.getTasks().create("hello", DefaultTask.class);
    }
}
