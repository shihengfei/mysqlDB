package com.qinmei.databasebackup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * 要执行监听器需要加注解 @ServletComponentScan
 */
@SpringBootApplication
@ServletComponentScan
public class DatabasebackupApplication {

	public static void main(String[] args) {
		SpringApplication.run(DatabasebackupApplication.class, args);
	}
}
