/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package bueno.spring.restpoc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import bueno.spring.restpoc.model.User;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserControllerMainTests {

	@Autowired
	private MockMvc mockMvc;

	// -------------------Retrieve All Users--------------------------------------------------------

	@Test
	public void teste01RetrieveAllUsers() throws Exception {
		this.mockMvc.perform(get("/user/"))//
				.andDo(print())//
				.andExpect(status().isOk())//
				.andExpect(jsonPath("$[0].name").value("Sam"))//
		;
	}

	// -------------------Retrieve Single User--------------------------------------------------------

	@Test
	public void teste02RetrieveSingleUser() throws Exception {
		this.mockMvc.perform(get("/user/1"))//
				.andDo(print()).andExpect(status().isOk())//
				.andExpect(jsonPath("$.id").value(1))//
				.andExpect(jsonPath("$.name").value("Sam"))//
				.andExpect(jsonPath("$.age").value(30))//
				.andExpect(jsonPath("$.salary").value(70000.0))//
		;
	}

	// -------------------Create a User--------------------------------------------------------

	@Test
	public void teste03CreateUser() throws Exception {

		User usr = new User(1000L, "Mark", 79, 0D);

		this.mockMvc
				.perform(post("/user/")//
						.content(asJsonString(usr))//
						.contentType(MediaType.APPLICATION_JSON)//
						.accept(MediaType.APPLICATION_JSON))//
				.andDo(print()).andExpect(status().isCreated())//
				.andExpect(redirectedUrl("http://localhost/user/5"))//
		;
	}

	// ------------------- Update a User --------------------------------------------------------
	@Test
	public void teste04UpdateUser() throws Exception {
		User usr = new User(1L, "Sam Smith", 25, 15000D);

		MvcResult result = this.mockMvc
				.perform(put("/user/1")//
						.content(asJsonString(usr))//
						.contentType(MediaType.APPLICATION_JSON)//
						.accept(MediaType.APPLICATION_JSON))//
				.andDo(print()).andExpect(status().isOk())//
				.andReturn()//
		;

		Assert.assertEquals(usr, asJsonStringToUser(result));
	}

	// ------------------- Delete a User --------------------------------------------------------

	@Test
	public void teste05DeleteUser() throws Exception {
		this.mockMvc.perform(delete("/user/4"))//
				.andDo(print())//
				.andExpect(status().isNoContent())//
		;
	}

	// ------------------- Delete All Users --------------------------------------------------------

	@Test
	public void teste06DeleteAllUser() throws Exception {
		this.mockMvc.perform(delete("/user/"))//
				.andDo(print())//
				.andExpect(status().isNoContent())//
		;
	}

	// ------------------- Utility --------------------------------------------------------

	public static String asJsonString(final Object obj) {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static User asJsonStringToUser(final MvcResult result) {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(result.getResponse().getContentAsString(), User.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
