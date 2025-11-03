package com.trazia.trazia_project;

import org.springframework.boot.SpringApplication;

public class TestTraziaProjectApplication {

	public static void main(String[] args) {
		SpringApplication.from(TraziaProjectApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
