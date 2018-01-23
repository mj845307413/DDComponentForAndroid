package com.example.buildsrc

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction;

/**
 * Created by majun on 18/1/23.
 */

public class ShowAddress extends DefaultTask {

    @TaskAction
    void output() {
        System.out.println("我的家乡在${project.address.province}省${project.address.city}市")
    }
}
