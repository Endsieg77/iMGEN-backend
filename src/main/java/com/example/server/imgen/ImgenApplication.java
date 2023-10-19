package com.example.server.imgen;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
// import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
// @CrossOrigin(origins = "*")
@MapperScan("com.example.server.imgen.mapper")
// @SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class ImgenApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImgenApplication.class, args);
	}

}
