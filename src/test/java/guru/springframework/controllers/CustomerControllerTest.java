package guru.springframework.controllers;

import guru.springframework.domain.Customer;
import guru.springframework.services.CustomerService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by jt on 11/16/15.
 */
public class CustomerControllerTest {

    @Mock //Mockito Mock object
    private CustomerService customerService;

    @InjectMocks //setups up controller, and injects mock objects into it.
    private CustomerController customerController;
    private MockMvc mockMvc;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this); //initilizes controller and mocks

        mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();
    }

    @Test
    public void testList() throws Exception{

        List<Customer> customer = new ArrayList<>();
        customer.add(new Customer());
        customer.add(new Customer());


        //specific Mockito interaction, tell stub to return list of products
        when(customerService.listAll()).thenReturn((List) customer); //need to strip generics to keep Mockito happy.

        mockMvc.perform(get("/customer/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("customer/list"))
                .andExpect(model().attribute("customers", hasSize(2)));

    }

    @Test
    public void testShow() throws Exception{
        Integer id = 1;

        //Tell Mockito stub to return new product for ID 1
        when(customerService.getById(id)).thenReturn(new Customer());

        mockMvc.perform(get("/customer/show/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("customer/show"))
                .andExpect(model().attribute("customer", instanceOf(Customer.class)));
    }

    @Test
    public void testEdit() throws Exception{
        Integer id = 1;

        //Tell Mockito stub to return new product for ID 1
        when(customerService.getById(id)).thenReturn(new Customer());

        mockMvc.perform(get("/customer/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("customer/customerform"))
                .andExpect(model().attribute("customer", instanceOf(Customer.class)));
    }

    @Test
    public void testNewCustomer() throws Exception {
        Integer id = 1;

        //should not call service
        verifyZeroInteractions(customerService);

        mockMvc.perform(get("/customer/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("customer/customerform"))
                .andExpect(model().attribute("customer", instanceOf(Customer.class)));
    }

    @Test
    public void testSaveOrUpdate() throws Exception {
        Integer id = 1;
        String firstName = "Cliff";
        String lastName = "Douglas";
        String email = "cliff.douglas@gcs4sqa.com";
        String phoneNumber = "07725 123456";
        String addressLine1 = "1 Wibsey Road";
        String addressLine2 = "Little Coates";
        String city = "Grimsby";
        String state = "N E Lincs";
        String zipCode = "DN379AB";

        Customer returnCustomer = new Customer();
        returnCustomer.setId(id);
        returnCustomer.setFirstName(firstName);
        returnCustomer.setLastName(lastName);
        returnCustomer.setEmail(email);
        returnCustomer.setPhoneNumber(phoneNumber);
        returnCustomer.setAddressLine1(addressLine1);
        returnCustomer.setAddressLine2(addressLine2);
        returnCustomer.setCity(city);
        returnCustomer.setState(state);
        returnCustomer.setZipCode(zipCode);

        when(customerService.saveOrUpdate(Matchers.<Customer>any())).thenReturn(returnCustomer);

        mockMvc.perform(post("/customer")
                        .param("id", "1")
                        .param("firstName", firstName)
                        .param("lastName", lastName)
                        .param("email", email)
                        .param("phoneNumber", phoneNumber)
                        .param("addressLine1", addressLine1)
                        .param("addressLine2", addressLine2)
                        .param("city", city)
                        .param("state", state)
                        .param("zipCode", zipCode))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:customer/show/1"))
                .andExpect(model().attribute("customer", instanceOf(Customer.class)))
                .andExpect(model().attribute("customer", hasProperty("id", is(id))))
                .andExpect(model().attribute("customer", hasProperty("firstName", is(firstName))))
                .andExpect(model().attribute("customer", hasProperty("lastName", is(lastName))))
                .andExpect(model().attribute("customer", hasProperty("email", is(email))))
                .andExpect(model().attribute("customer", hasProperty("phoneNumber", is(phoneNumber))))
                .andExpect(model().attribute("customer", hasProperty("addressLine1", is(addressLine1))))
                .andExpect(model().attribute("customer", hasProperty("addressLine2", is(addressLine2))))
                .andExpect(model().attribute("customer", hasProperty("city", is(city))))
                .andExpect(model().attribute("customer", hasProperty("state", is(state))))
                .andExpect(model().attribute("customer", hasProperty("zipCode", is(zipCode))));



        //verify properties of bound object
        ArgumentCaptor<Customer> boundCustomer = ArgumentCaptor.forClass(Customer.class);
        verify(customerService).saveOrUpdate(boundCustomer.capture());

        assertEquals(id, boundCustomer.getValue().getId());
        assertEquals(firstName, boundCustomer.getValue().getFirstName());
        assertEquals(lastName, boundCustomer.getValue().getLastName());
        assertEquals(email, boundCustomer.getValue().getEmail());
        assertEquals(phoneNumber, boundCustomer.getValue().getPhoneNumber());
        assertEquals(addressLine1, boundCustomer.getValue().getAddressLine1());
        assertEquals(addressLine2, boundCustomer.getValue().getAddressLine2());
        assertEquals(city, boundCustomer.getValue().getCity());
        assertEquals(state, boundCustomer.getValue().getState());
        assertEquals(zipCode, boundCustomer.getValue().getZipCode());
    }

    @Test
    public void testDelete() throws Exception{
        Integer id = 1;

        mockMvc.perform(get("/customer/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/customer/list"));

        verify(customerService, times(1)).delete(id);
    }
}