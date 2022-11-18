package com.nttdata.customerservice.service.impl;

import com.nttdata.customerservice.model.Account;
import com.nttdata.customerservice.model.Customer;
import com.nttdata.customerservice.repository.CustomerRepository;
import com.nttdata.customerservice.request.CustomerRequest;
import com.nttdata.customerservice.service.CustomerService;
import com.nttdata.customerservice.util.AccountRoutine;
import com.nttdata.customerservice.util.AccountType;
import com.nttdata.customerservice.util.AppConstant;
import com.nttdata.customerservice.util.CustomerType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



/**
 * Implementation for CustomerService interface.
 */
@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerServiceImpl.class);

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Flux<Customer> getAll() {
        LOGGER.info("getAll");
        return customerRepository.findAll();
    }

    @Override
    public Mono<Customer> save(Customer customer) {
        LOGGER.info("save: {}", customer);
        return customerRepository.save(customer)
                .flatMap(customerDB -> customerRepository.findById(customerDB.getId()));
    }

    @Override
    public Mono<ResponseEntity<Object>> getById(String id) {
        LOGGER.info("getById: id={}", id);
        return customerRepository.findById(id)
                .map(customerDB -> ResponseEntity.status(HttpStatus.OK).body((Object) customerDB))
                .switchIfEmpty(Mono.defer(() -> {
                    LOGGER.warn(AppConstant.CUSTOMER_DOES_NOT_EXIST_LOGGER, id);
                    return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                            String.format(AppConstant.CUSTOMER_DOES_NOT_EXIST, id))
                    );
                }));
    }

    @Override
    public Mono<Boolean> existsById(String id) {
        LOGGER.info("existsById: id={}", id);
        return customerRepository.existsById(id);
    }

    @Override
    public Mono<ResponseEntity<String>> deleteById(String id) {
        LOGGER.info("deleteById: id={}", id);
        return customerRepository.findById(id)
                .flatMap(customer -> customerRepository.deleteById(id)
                        .thenReturn(ResponseEntity.status(HttpStatus.OK).body(AppConstant.SUCCESSFULLY_REMOVED))
                ).switchIfEmpty(
                        Mono.defer(() -> {
                            LOGGER.warn(AppConstant.CUSTOMER_DOES_NOT_EXIST_LOGGER, id);
                            return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                                    String.format(AppConstant.CUSTOMER_DOES_NOT_EXIST, id))
                            );
                        })
                );
    }

    @Override
    public Mono<ResponseEntity<Object>> update(String id, CustomerRequest request) {
        LOGGER.info("deleteById: id={}", id);

        if (!validateNumberAccounts(request)) {
            LOGGER.warn(AppConstant.NUMBER_OR_TYPE_OF_ACCOUNTS_NOT_ALLOWED, id);
            return Mono.just(ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    AppConstant.NUMBER_OR_TYPE_OF_ACCOUNTS_NOT_ALLOWED)
            );
        }

        return customerRepository.findById(id)
                .flatMap(customer -> {
                    request.getCustomer().setId(id);
                    return customerRepository.save(request
                                    .getCustomer()).map(customerDB -> ResponseEntity
                            .status(HttpStatus.OK).body((Object) customerDB));
                }).switchIfEmpty(
                        Mono.defer(() -> {
                            LOGGER.warn(AppConstant.CUSTOMER_DOES_NOT_EXIST_LOGGER, id);
                            return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                                    String.format(AppConstant.CUSTOMER_DOES_NOT_EXIST, id))
                            );
                        })
                );
    }

    /**
     * Validates the number of accounts for people and companies.
     *
     * @param request a customer request.
     * @return true if the validation with the number of accounts is correct,
     * false otherwise
     */
    @Override
    public boolean validateNumberAccounts(CustomerRequest request) {
        Customer customer = request.getCustomer();
        LOGGER.info("verifyNumberAccounts: customerId={}", customer);
        List<Account> accounts = customer.getAccounts();

        if (customer.getCustomerType().equals(CustomerType.PERSON)) {
            int checkingCount = AccountRoutine.getCountByAccountType(
                    accounts, AccountType.CHECKING);

            int fixedCount = AccountRoutine.getCountByAccountType(
                    accounts, AccountType.FIXED);

            return checkingCount <= 1 && fixedCount <= 1;
        }

        int savingCount = AccountRoutine.getCountByAccountType(
                accounts, AccountType.SAVING);

        int fixedCount = AccountRoutine.getCountByAccountType(
                accounts, AccountType.FIXED);

        return savingCount == 0 && fixedCount == 0;
    }
}
