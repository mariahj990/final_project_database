/**
Copyright (c) 2024 Sami Menik, PhD. All rights reserved.

This is a project developed by Dr. Menik to give the students an opportunity to apply database concepts learned in the class in a real world project. Permission is granted to host a running version of this software and to use images or videos of this work solely for the purpose of demonstrating the work to potential employers. Any form of reproduction, distribution, or transmission of the software's source code, in part or whole, without the prior written consent of the copyright owner, is strictly prohibited.
*/
package uga.menik.csx370;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class PopulateDatabase implements CommandLineRunner {

    // runs after Spring Boot starts
    // script to populate database 

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Running csv loader script...");

        // call python script to load csvs
        ProcessBuilder pb = new ProcessBuilder("python", "p2_starter_code/src/main/resources/import_book_data.py");
        pb.inheritIO(); // prints output to console
        Process process = pb.start();
        int exitCode = process.waitFor();
        System.out.println("Python script finished with exit code: " + exitCode);
    }
}
