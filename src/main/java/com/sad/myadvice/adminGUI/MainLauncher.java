package com.sad.myadvice.adminGUI;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.sad.myadvice.MyadviceApplication;
import com.sad.myadvice.adminServices.CourseService;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainLauncher extends Application {

    private static ConfigurableApplicationContext context;

    @Override
    public void init() {
        // Start Spring Boot
        context = new SpringApplicationBuilder(MyadviceApplication.class).run();
    }

    @Override
    public void start(Stage stage) {
 /*
        //Timetable Screen
        // Get service from Spring
        TimetableService timetableService =
                context.getBean(TimetableService.class);
        CourseService CourseService =
                context.getBean(CourseService.class);

        // Launch screen
        EditableTimetableScreen screen =
                new EditableTimetableScreen(timetableService, CourseService);
 */

/* 
        // Research Areas Screen
        ResearchAreasService researchAreasService =
                context.getBean(ResearchAreasService.class);

        EditableResearchAreasScreen screen =
                new EditableResearchAreasScreen(researchAreasService);
*/

       // Prerequisites Screen
        CourseService courseService =
            context.getBean(CourseService.class);

        EditablePrerequisitesScreen screen =
                new EditablePrerequisitesScreen(courseService);

        screen.show(stage);
    }

    @Override
    public void stop() {
        context.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}