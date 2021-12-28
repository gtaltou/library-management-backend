package com.taltou.library.customer;


import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/rest/customer/api")
@Api(value = "Customer Rest Controller: contains all operations for managing customers")
public class CustomerRestController {

    public static final Logger LOGGER = LoggerFactory.getLogger(CustomerRestController.class);

    @Autowired
    private CustomerServiceImpl customerService;

    @Autowired
    private JavaMailSender javaMailSender;

    /**
     * Ajoute un nouveau client dans la base de donnée H2. Si le client existe déjà, on retourne un code indiquant que la création n'a pas abouti.
     * @param customerDTORequest
     * @return
     */
    @PostMapping("/addCustomer")
    @ApiOperation(value = "Add a new Customer in the Library", response = CustomerDTO.class)
    @ApiResponses(value = { @ApiResponse(code = 409, message = "Conflict: the customer already exist"),
            @ApiResponse(code = 201, message = "Created: the customer is successfully inserted"),
            @ApiResponse(code = 304, message = "Not Modified: the customer is unsuccessfully inserted") })
    public ResponseEntity<CustomerDTO> createNewCustomer(@RequestBody CustomerDTO customerDTORequest) {
        //, UriComponentsBuilder uriComponentBuilder
        Customer existingCustomer = customerService.findCustomerByEmail(customerDTORequest.getEmail());
        if (existingCustomer != null) {
            return new ResponseEntity<CustomerDTO>(HttpStatus.CONFLICT);
        }
        Customer customerRequest = mapCustomerDTOToCustomer(customerDTORequest);
        customerRequest.setCreationDate(LocalDate.now());
        Customer customerResponse = customerService.saveCustomer(customerRequest);
        if (customerResponse != null) {
            CustomerDTO customerDTO = mapCustomerToCustomerDTO(customerResponse);
            return new ResponseEntity<CustomerDTO>(customerDTO, HttpStatus.CREATED);
        }
        return new ResponseEntity<CustomerDTO>(HttpStatus.NOT_MODIFIED);

    }

    /**
     * Met à jour les données d'un client dans la base de donnée H2. Si le client n'est pas retrouvé, on retourne un code indiquant que la mise à jour n'a pas abouti.
     * @param customerDTORequest
     * @return
     */
    @PutMapping("/updateCustomer")
    @ApiOperation(value = "Update/Modify an existing customer in the Library", response = CustomerDTO.class)
    @ApiResponses(value = { @ApiResponse(code = 404, message = "Not Found : the customer does not exist"),
            @ApiResponse(code = 200, message = "Ok: the customer is successfully updated"),
            @ApiResponse(code = 304, message = "Not Modified: the customer is unsuccessfully updated") })
    public ResponseEntity<CustomerDTO> updateCustomer(@RequestBody CustomerDTO customerDTORequest) {
        //, UriComponentsBuilder uriComponentBuilder
        if (!customerService.checkIfIdexists(customerDTORequest.getId())) {
            return new ResponseEntity<CustomerDTO>(HttpStatus.NOT_FOUND);
        }
        Customer customerRequest = mapCustomerDTOToCustomer(customerDTORequest);
        Customer customerResponse = customerService.updateCustomer(customerRequest);
        if (customerResponse != null) {
            CustomerDTO customerDTO = mapCustomerToCustomerDTO(customerResponse);
            return new ResponseEntity<CustomerDTO>(customerDTO, HttpStatus.OK);
        }
        return new ResponseEntity<CustomerDTO>(HttpStatus.NOT_MODIFIED);
    }

    /**
     * Supprime un client dans la base de donnée H2. Si le client n'est pas retrouvé, on retourne le Statut HTTP NO_CONTENT.
     * @param customerId
     * @return
     */
    @DeleteMapping("/deleteCustomer/{customerId}")
    @ApiOperation(value = "Delete a customer in the Library, if the customer does not exist, nothing is done", response = String.class)
    @ApiResponse(code = 204, message = "No Content: customer sucessfully deleted")
    public ResponseEntity<String> deleteCustomer(@PathVariable Integer customerId) {
        customerService.deleteCustomer(customerId);
        return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/paginatedSearch")
    @ApiOperation(value="List customers of the Library in a paginated way", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok: successfully listed"),
            @ApiResponse(code = 204, message = "No Content: no result founded"),
    })
    public ResponseEntity<List<CustomerDTO>> searchCustomers(@RequestParam("beginPage") int beginPage,
                                                             @RequestParam("endPage") int endPage) {
        //, UriComponentsBuilder uriComponentBuilder
        Page<Customer> customers = customerService.getPaginatedCustomersList(beginPage, endPage);
        if (customers != null) {
            List<CustomerDTO> customerDTOs = customers.stream().map(customer -> {
                return mapCustomerToCustomerDTO(customer);
            }).collect(Collectors.toList());
            return new ResponseEntity<List<CustomerDTO>>(customerDTOs, HttpStatus.OK);
        }
        return new ResponseEntity<List<CustomerDTO>>(HttpStatus.NO_CONTENT);
    }

    /**
     * Retourne le client ayant l'adresse email passé en paramètre.
     * @param email
     * @return
     */
    @GetMapping("/searchByEmail")
    @ApiOperation(value="Search a customer in the Library by its email", response = CustomerDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok: successfull research"),
            @ApiResponse(code = 204, message = "No Content: no result founded"),
    })
    public ResponseEntity<CustomerDTO> searchCustomerByEmail(@RequestParam("email") String email) {
        //, UriComponentsBuilder uriComponentBuilder
        Customer customer = customerService.findCustomerByEmail(email);
        if (customer != null) {
            CustomerDTO customerDTO = mapCustomerToCustomerDTO(customer);
            return new ResponseEntity<CustomerDTO>(customerDTO, HttpStatus.OK);
        }
        return new ResponseEntity<CustomerDTO>(HttpStatus.NO_CONTENT);
    }

    /**
     * Retourne la liste des clients ayant le nom passé en paramètre.
     * @param lastName
     * @return
     */
    @GetMapping("/searchByLastName")
    @ApiOperation(value="Search a customer in the Library by its Last name", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok: successfull research"),
            @ApiResponse(code = 204, message = "No Content: no result founded"),
    })
    public ResponseEntity<List<CustomerDTO>> searchBookByLastName(@RequestParam("lastName") String lastName) {
        //,	UriComponentsBuilder uriComponentBuilder
        List<Customer> customers = customerService.findCustomerByLastName(lastName);
        if (customers != null && !CollectionUtils.isEmpty(customers)) {
            List<CustomerDTO> customerDTOs = customers.stream().map(customer -> {
                return mapCustomerToCustomerDTO(customer);
            }).collect(Collectors.toList());
            return new ResponseEntity<List<CustomerDTO>>(customerDTOs, HttpStatus.OK);
        }
        return new ResponseEntity<List<CustomerDTO>>(HttpStatus.NO_CONTENT);
    }

    /**
     * Envoi un mail à un client. L'objet MailDTO contient l'identifiant et l'email du client concerné, l'objet du mail et le contenu du message.
     * @param loanMailDto
     * @param uriComponentBuilder
     * @return
     */
    @PutMapping("/sendEmailToCustomer")
    @ApiOperation(value="Send an email to customer of the Library", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok: Email successfully sent"),
            @ApiResponse(code = 404, message = "Not Found: no customer found, or wrong email"),
            @ApiResponse(code = 403, message = "Forbidden: Email cannot be sent")
    })
    public ResponseEntity<Boolean> sendMailToCustomer(@RequestBody MailDTO loanMailDto, UriComponentsBuilder uriComponentBuilder) {

        Customer customer = customerService.findCustomerById(loanMailDto.getCustomerId());
        if (customer == null) {
            String errorMessage = "The selected Customer for sending email is not found in the database";
            LOGGER.info(errorMessage);
            return new ResponseEntity<Boolean>(false, HttpStatus.NOT_FOUND);
        } else if (customer != null && StringUtils.isEmpty(customer.getEmail())) {
            String errorMessage = "No existing email for the selected Customer for sending email to";
            LOGGER.info(errorMessage);
            return new ResponseEntity<Boolean>(false, HttpStatus.NOT_FOUND);
        }

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(loanMailDto.MAIL_FROM);
        mail.setTo(customer.getEmail());
        mail.setSentDate(new Date());
        mail.setSubject(loanMailDto.getEmailSubject());
        mail.setText(loanMailDto.getEmailContent());

        try {
            javaMailSender.send(mail);
        } catch (MailException e) {
            return new ResponseEntity<Boolean>(false, HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }

    /**
     * Transforme un entity Customer en un POJO CustomerDTO
     *
     * @param customer
     * @return
     */
    private CustomerDTO mapCustomerToCustomerDTO(Customer customer) {
        ModelMapper mapper = new ModelMapper();
        CustomerDTO customerDTO = mapper.map(customer, CustomerDTO.class);
        return customerDTO;
    }

    /**
     * Transforme un POJO CustomerDTO en en entity Customer
     *
     * @param customerDTO
     * @return
     */
    private Customer mapCustomerDTOToCustomer(CustomerDTO customerDTO) {
        ModelMapper mapper = new ModelMapper();
        Customer customer = mapper.map(customerDTO, Customer.class);
        return customer;
    }

}
