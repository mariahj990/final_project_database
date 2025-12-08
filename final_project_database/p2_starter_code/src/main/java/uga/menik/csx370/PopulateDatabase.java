/**
Copyright (c) 2024 Sami Menik, PhD. All rights reserved.

This is a project developed by Dr. Menik to give the students an opportunity to apply database concepts learned in the class in a real world project. Permission is granted to host a running version of this software and to use images or videos of this work solely for the purpose of demonstrating the work to potential employers. Any form of reproduction, distribution, or transmission of the software's source code, in part or whole, without the prior written consent of the copyright owner, is strictly prohibited.
*/
package uga.menik.csx370;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class PopulateDatabase implements CommandLineRunner {

    private final DataSource dataSource;

    // runs after Spring Boot starts
    // script to populate database 
    public PopulateDatabase(DataSource datasource) {
        this.dataSource = datasource;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Running csv loader script...");

        String alrSetUP = "SELECT ran from csv_data_loading_status where ran = 1 limit 1;";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(alrSetUP)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    boolean alrLoad = rs.getBoolean("ran");
                    if(alrLoad == true) {
                        System.out.println("csv's are already loaded, skipping import...");
                        return;
                    }
                }
            }

	}

	/*
	boolean booksExist = false;

	String bookCountAlready = "SELECT count(*) from book;";
	try(Connection conn = dataSource.getConnection();
	    PreparedStatement stmt = conn.prepareStatement(tablesPopulated)) {
	    try(ResultSet = rs.stmt.executeQuery()) {
		booksExist = rs.next() && rs.getInt("count") > 0;
	    }
	}
	*/
	    
	  
	    
	    
	    
	    
        // call python script to load csvs
        //File correctdir = new File("final_project_database/p2_starter_code/src/main/resources");
        ProcessBuilder pb = new ProcessBuilder("python", "import_book_data.py");
        pb.directory(new File("src/main/resources"));
        pb.inheritIO(); // prints output to console
        Process process = pb.start();
        int exitCode = process.waitFor();
        System.out.println("Python script finished with exit code: " + exitCode);
        
        //update table
        String setUP = "insert ignore into csv_data_loading_status values (1, 1);";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(setUP)) {
            stmt.executeUpdate();
            System.out.println("csv's loaded in!");
        }
    }
}
