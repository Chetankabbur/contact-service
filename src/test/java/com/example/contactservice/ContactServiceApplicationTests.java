package com.example.contactservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
/**
 * ContactServiceApplicationTests
 */
@SpringBootTest
class ContactServiceApplicationTests {
	/**
	 * contestLoads
	 */
	@Test
	void contextLoads() {
	}

}
