package com.example.microservices.product;

import com.example.microservices.product.dto.ProductRequest;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductServiceApplicationTests {


	@LocalServerPort
	private Integer port;

	@BeforeEach
	void setUp() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}


	@Test
	void shouldCreateProduct() {
		ProductRequest productRequest = getProductRequest();

		RestAssured.given()
				.contentType("application/json")
				.body(productRequest)
				.when()
				.post("/api/v1/products")
				.then()
				.statusCode(201)
				.body("id", Matchers.notNullValue())
				.body("name", Matchers.equalTo(productRequest.name()))
				.body("description", Matchers.equalTo(productRequest.description()))
				.body("price", Matchers.is(productRequest.price().intValue()));
	}

	@Test
	void shouldGetProducts(){
		ProductRequest productRequest = getProductRequest();

		RestAssured.given()
				.contentType("application/json")
				.body(productRequest)
				.when()
				.post("/api/v1/products")
				.then()
				.statusCode(201);

		RestAssured.given()
				.when()
				.get("/api/v1/products")
				.then()
				.statusCode(200)
				.body("$.size()", Matchers.greaterThan(0))
				.body("[0].name", Matchers.equalTo(productRequest.name()))
				.body("[0].description", Matchers.equalTo(productRequest.description()))
				.body("[0].price", Matchers.is(productRequest.price().intValue()));
	}

	private ProductRequest getProductRequest() {
		return new ProductRequest(
				"Iphone 16",
				"Iphone 16 is the last smartphone from apple",
				BigDecimal.valueOf(1400)
		);
	}

}
